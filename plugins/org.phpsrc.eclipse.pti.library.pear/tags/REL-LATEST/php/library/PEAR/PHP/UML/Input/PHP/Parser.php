<?php
/**
 * PHP_UML (PHP_UML_Input_PHP_Parser)
 *
 * PHP version 5
 *
 * @category PHP
 * @package  PHP_UML
 * @author   Baptiste Autin <ohlesbeauxjours@yahoo.fr> 
 * @license  http://www.gnu.org/licenses/lgpl.html LGPL License 3
 * @version  SVN: $Revision: 144 $
 * @link     http://pear.php.net/package/PHP_UML
 * @since    $Date: 2010-01-07 21:47:12 +0100 (jeu., 07 janv. 2010) $
 */

/**
 * The PHP parser.
 * It stores all the program elements of a PHP file in
 * a PHP_UML_Metamodel_Superstructure object.
 * 
 * It relies the PHP instruction token_get_all().
 * Most navigabilities between associated elements are bidirectional
 * (the packages know their owned elements, and the classes know their
 * nesting package)
 * In a first step, relations (extends, implements) use strings. It means that
 * the namespace of a class is memorized through a string.
 * Once the parsing is done, the method finalize() must be called,
 * so that all the named references be replaced by PHP references (&$xxx).
 *
 * @category   PHP
 * @package    PHP_UML
 * @subpackage Input
 * @subpackage PHP
 * @author     Baptiste Autin <ohlesbeauxjours@yahoo.fr> 
 * @license    http://www.gnu.org/licenses/lgpl.html LGPL License 3
 */
class PHP_UML_Input_PHP_Parser
{
    const T_NS_SEPARATOR  = '\\';
    const T_NS_SEPARATOR2 = '::';    // for backward compat
    
    /**
     * Reference to a PHP_UML_Metamodel_Superstructure
     * (where the parser stores all the program elements it finds)
     * @var PHP_UML_Metamodel_Superstructure
     */
    static private $structure;

    /**
     * Current PHP_UML_Metamodel_Artifact
     * @var PHP_UML_Metamodel_Artifact
     */
    static private $file;

    /**
     * If true, all docblocks are interpreted, especially @package and the types of
     * the properties/function parameters (if given).
     * @var bool
     */
    static private $docblocks;

    /**
     * If true, the elements (class, function) are included in the API only if their
     * comments contain explicitly a docblock "@api"
     * @var bool
     */
    static private $onlyApi;
    
    /**
     * If true, the symbol $ is kept along with the variable names
     * @var bool
     */
    static private $keepDollar;

    /**
     * If true, elements marked with @internal are skipped
     * @var bool
     */
    static private $skipInternal;

    /**
     * Current package index (which does not necessary match the last one put
     * over $packages stack). This index refers to the array $structure->packages
     * @var PHP_UML_Metamodel_Package
     */
    static private $currentPackage;

    /**
     * Current docblock comment
     * @var string
     */
    static private $curDocComment;

    /**
     * Current class features (abstract, final)
     * @var array Array of tokens
     */
    static private $classFeatures;

    /**
     * Current element (property or function) features (abstract, public, static...)
     * @var array Array of tokens
     */
    static private $classElementFeatures;

    /**
     * Current namespace, as defined by the PHP "namespace" instruction
     * @var string
     */
    static private $currentQn = '';

    /**
     * PHP namespace aliases ("use <value> as <key>")
     * @var array Associative array alias => namespace
     */
    static private $aliases = array();

    /**
     * Strict object oriented mode
     * @var bool
     */
    static private $pureObj = false;
    
    /**
     * Constructor
     *
     * @param PHP_UML_Metamodel_Superstructure &$struct   An empty instance of metamodel (superstructure)
     * @param bool                             $docblocks Set to true to interpret docblocks (@package, types)
     * @param bool                             $dollar    Set to true to keep the symbol $ in variable names
     * @param bool                             $internal  Set to true to skip elements tagged with @internal
     * @param bool                             $onlyApi   Set to true to include only the elements tagged with @api
     * @param bool                             $pureObj   Set to true to discard procedural code
     */
    public function __construct(PHP_UML_Metamodel_Superstructure &$struct, $docblocks = true, $dollar = true, $internal = true, $onlyApi = false, $pureObj = false)
    {
        self::$structure = $struct;

        self::$docblocks    = $docblocks;
        self::$keepDollar   = $dollar;
        self::$skipInternal = $internal;
        self::$onlyApi      = $onlyApi;
        self::$pureObj      = $pureObj;
 
        if (!defined('T_NAMESPACE'))
            define('T_NAMESPACE', -1);
        if (!defined('T_NS_SEPARATOR'))
            define('T_NS_SEPARATOR', -2);
    }

    /**
     * Parse a PHP file
     * 
     * @param string $fileBase Base directory
     * @param string $filePath Pathfile (relative to $fileBase)
     */
    static public function parse($fileBase, $filePath)
    {
        $filename = $fileBase.$filePath;

        if (!(file_exists($filename) && is_readable($filename))) {
            throw new PHP_UML_Exception('Could not read '.$filename.'.');
        }

        self::$file = new PHP_UML_Metamodel_Artifact;
        self::$file->id   = self::getUID();
        self::$file->name = basename($filePath);        
        $dirname = dirname($filePath);

        if ($dirname!='.' && $dirname!=DIRECTORY_SEPARATOR) {
            $fp = self::addDeploymentPackage(dirname($filePath));
        } else {
            $fp = self::$structure->deploymentPackages;    // global
        }

        $fp->ownedType[]     = self::$file;
        self::$file->package = $fp;

        self::$aliases = array();

        self::$currentPackage = self::$structure->packages;

        $tokens = token_get_all(file_get_contents($filename));

        if (self::$docblocks) {    // || self::$structureFromDocblocks
            // First, let's have a look at the file docblock :
            $dc = self::tNextDocComment($tokens);
            reset($tokens);
            if ($dc!='' && self::findPackageInDocblock($dc, $set)) {
                self::$currentPackage = self::addPackage($set[1]);
            }
        }
        self::tBody($tokens);
    }


    /**
     * Template matching T_CLASS
     *
     * @param array &$tokens Tokens
     */
    static private function tClass(&$tokens)
    {
        while (list($l, $v) = each($tokens)) {
            switch($v[0]) {
            case T_STRING:
                list($classPkg, $className) = self::getCurrentPackage($v[1]);

                $c       = new PHP_UML_Metamodel_Class;
                $c->name = $className;
                $c->file = self::$file;
                $c->id   = self::getUID();
                self::setClassifierFeatures($c);
                break;
            case T_IMPLEMENTS:
                $c->implements = self::tStringCommaList($tokens);
                break;
            case T_EXTENDS:
                $c->superClass = self::tStringCommaList($tokens);
                break;
            case '{':
                $c = self::tClassifierBody($tokens, $c);
                if (!is_null($c))
                    self::setNestingPackageOfType($c, $classPkg);
                return;
            }
        }
    }
    
    /**
     * Template matching T_INTERFACE
     *
     * @param array &$tokens Tokens
     */
    static private function tInterface(&$tokens)
    {
        while (list($c, $v) = each($tokens)) {
            switch ($v[0]) {
            case T_STRING:
                list($classPkg, $className) = self::getCurrentPackage($v[1]);

                $i       = new PHP_UML_Metamodel_Interface;
                $i->name = $className;
                $i->file = self::$file;
                $i->id   = self::getUID();
                break;
            case T_EXTENDS:
                $i->superClass = self::tStringCommaList($tokens);
                break;
            case '{':
                $i = self::tClassifierBody($tokens, $i);
                if(!is_null($i))
                    self::setNestingPackageOfType($i, $classPkg);
                return;
            }
        }
    }
    
    /**
     * Specific template for T_CLASS/T_INTERFACE
     * Normally preceded by a tClass or tInterface
     * 
     * @param array                        &$tokens Tokens
     * @param PHP_UML_Metamodel_Classifier $class   A class or interface
     * 
     * @return PHP_UML_Metamodel_Classifier The updated class or interface
     */
    static private function tClassifierBody(&$tokens, $class)
    {
        $operations = array();
        $attributes = array();
        
        //$docs = self::getDocblocksInDocComment(self::$curDocComment);
        //$desc = self::getDescriptionInDocComment(self::$curDocComment);
        self::addDocumentation($class);
        $skipClassifier = self::toBeSkipped(self::$curDocComment);    // should the class be ignored?

        self::$curDocComment = '';
        self::$classElementFeatures = array();

        $curly = 1;
        while (list($c, $v) = each($tokens)) {
            $skipCurrentElement = self::toBeSkipped(self::$curDocComment);
            switch ($v[0]) {
            case T_FUNCTION:
                if ($skipCurrentElement) {
                    self::tFunction($tokens, $class);
                } else {
                    $operations[] = self::tFunction($tokens, $class);
                }
                self::$classElementFeatures = array();
                self::$curDocComment = '';
                break;
            case T_STRING:
            case T_VARIABLE:
                if (!$skipCurrentElement) {
                    $attributes[] = self::tAttribute($tokens, $v[1], $class);;
                }
                self::$classElementFeatures = array();
                self::$curDocComment = '';
                break;

            case T_STATIC:
            case T_ABSTRACT:
            case T_PUBLIC:
            case T_PRIVATE:
            case T_PROTECTED:
            case T_CONST:
            case T_FINAL:
                self::$classElementFeatures[] = $v[0];
                break;

            case T_DOC_COMMENT:
                self::$curDocComment = self::removeDocCommentMarks($v[1]);
                break;

            case '{':
                $curly++;
                break;
            case '}':
                $curly--;
                if ($curly==0) {
                    $class->ownedOperation = $operations;
                    $class->ownedAttribute = $attributes;
                    return $skipClassifier ? null : $class;
                }
            }
        }
    }

    /**
     * Template for the attributes (properties)
     * 
     * @param array                        &$tokens Tokens
     * @param string                       $name    Property name
     * @param PHP_UML_Metamodel_Classifier $class   Parent class
     * 
     * @return PHP_UML_Metamodel_Property 
     */
    static private function tAttribute(&$tokens, $name, PHP_UML_Metamodel_Classifier $class=null)
    {
        $r     = self::tScalar($tokens);
        $token = $r[0];
        $value = $r[1];

        $a       = new PHP_UML_Metamodel_Property;
        $a->name = self::cleanVariable($name);
        $a->id   = self::getUID();

        if (!is_null($class))
            $a->class = $class;
        else {    // isolated function
            list($classPkg, $className) = self::getCurrentPackage($name);
            self::setNestingPackageOfAttribute($a, $classPkg);
            $a->file = self::$file;
        }
        $a->default = $value;
        self::setElementFeatures($a);

        $docs = self::getDocblocksInDocComment(self::$curDocComment);
        //$desc = self::getDescriptionInDocComment(self::$curDocComment);
        self::addDocumentation($a);
        $dbParam = array();
        foreach ($docs as $k) {
            switch($k[1]) {
            case 'var':
                $dbParam[$name] = $k;
                break 2;
            }
        }
        self::setTypeElement($a, '', $token, $value, $name, $dbParam);      
        return $a;      
    }
    
    /**
     * Specific template for matching a list of T_STRING, separated by a comma
     *
     * @param array &$tokens Tokens
     * 
     * @return array Array of elements found
     */
    static private function tStringCommaList(&$tokens)
    {
        $values = array();
        $value  = '';
        while (list($c, $v) = each($tokens)) {
            switch ($v[0]) {
            case ',':
                $values[] = self::resolveQName($value);
                $value    = '';
                break;
            case T_STRING:
            case T_NS_SEPARATOR:
                $value .= $v[1];
                break;
            case ';':
            case '{':
            case T_IMPLEMENTS:
                $values[] = self::resolveQName($value);
                prev($tokens);
                return $values;
            }
        }
    }

    /**
     * Specific template for T_FUNCTION
     *
     * @param array                        &$tokens Tokens
     * @param PHP_UML_Metamodel_Classifier $class   Owning class/interface
     * 
     * @return PHP_UML_Metamodel_Operation The operation created
     */
    static private function tFunction(&$tokens, PHP_UML_Metamodel_Classifier $class=null)
    {
        $o = new PHP_UML_Metamodel_Operation;
        while (list($c, $v) = each($tokens)) {
            switch ($v[0]) {
            case T_STRING:
                $o->name = $v[1];
                $o->id   = self::getUID();
                if (!is_null($class))
                    $o->class = $class;
                else {    // isolated function
                    list($classPkg, $className) = self::getCurrentPackage($v[1]);
                    self::setNestingPackageOfOperation($o, $classPkg);
                    $o->file = self::$file;
                }
                self::setElementFeatures($o);
                break;
            case '(':
                $o->ownedParameter = self::tParametersList($tokens, $o);
                break;
            case '{':
                self::tBody($tokens);
                prev($tokens);
                break;
            case ';':
            case '}':
                return $o;
            }
        }
    }

    /**
     * Specific template for T_CONST outside of a class
     *
     * @param array &$tokens Tokens
     *
     */
    static private function tConst(&$tokens)
    {
        while (list($c, $v) = each($tokens)) {
            switch ($v[0]) {
            case T_STRING:
                self::tAttribute($tokens, $v[1]);
                return;
            case ';':
            case '}':
                return;
            }
        }
    }
    
    /**
     * Specific template for matching the parameters of a function
     *
     * @param array                       &$tokens Tokens
     * @param PHP_UML_Metamodel_Operation $o       Operation
     * 
     * @return PHP_UML_Metamodel_Parameter The parameter created
     */
    static private function tParametersList(&$tokens, PHP_UML_Metamodel_Operation $o) 
    {
        $bracket    = 1;
        $type       = '';
        $parameters = array();
        $dbParam    = array();
        $direction  = 'in';

        $docs = self::getDocblocksInDocComment(self::$curDocComment);
        //$desc = self::getDescriptionInDocComment(self::$curDocComment);
        self::addDocumentation($o);
        
        foreach ($docs as $k) {
            switch ($k[1]) {
            case 'param':
                $dbParam[$k[3]] = $k;
                break;
            case 'return':
                $type = self::resolveQName($k[2]);
                break 2;
            }
        }
        $pr            = new PHP_UML_Metamodel_Parameter;
        $pr->name      = 'return';
        $pr->id        = self::getUID();
        $pr->operation = &$o;
        $pr->direction = 'return';
        $pr->type      = ($type!='' ? $type : 'void');
        $parameters[]  = $pr;
        
        $type = '';
        while (list($c, $v) = each($tokens)) {
            switch ($v[0]) {
            case T_STRING:
            case T_ARRAY:
            case T_NS_SEPARATOR:
                $type .= $v[1];
                break;
            case T_VARIABLE:
                $r     = self::tScalar($tokens);
                $token = $r[0];
                $value = $r[1];

                $p            = new PHP_UML_Metamodel_Parameter;
                $p->name      = self::cleanVariable($v[1]);
                $p->id        = self::getUID();
                $p->operation = &$o;
                $p->default   = $value;
                $p->direction = $direction;
                self::setTypeElement($p, $type, $token, $value, $v[1], $dbParam);                              
                $parameters[] = $p;

                prev($tokens);
                break;
            case '&':
                $direction = 'inout';
                break;
            case ',':
                $type      = '';
                $direction = 'in';
                break;
            case '(':
                $bracket++;
                break;
            case ')':
                $bracket--;
                if ($bracket==0) {
                    return $parameters;
                }
            }
        }
    }

    /**
     * Template for matching a scalar/static data 
     * (eg: $a = -14.5)
     * Stopping characters: , ; )
     * 
     * @param array &$tokens Tokens
     * 
     * @return array An array((string) type, (string) default value)
     */
    static private function tScalar(&$tokens)
    {
        $bracket = 0;
        $type    = 0;
        $value   = '';
        while (list($c, $v) = each($tokens)) {
            switch ($v[0]) {
            case T_STRING:
            case T_VARIABLE:
            case T_LNUMBER:
            case T_DNUMBER:
            case T_CONSTANT_ENCAPSED_STRING:
                $value .= $v[1];
                $type   = $v[0];
                break;
            case T_ARRAY:
                $vtmp   = self::tScalar($tokens);
                $value .= $v[1].$vtmp[1];
                prev($tokens);
                $type = $v[0];
                break;
            case '-':
                $value .= $v[0];
                break;
            case T_DOUBLE_ARROW:
                $value .= $v[1];
                break;
            case '(':
                $value .= $v[0];
                $bracket++;
                break;
            case ')':
                if ($bracket>0) {
                    $value .= $v[0];
                    $bracket--;
                    break;
                } else {
                    break 2;
                }
            case ',':
            case ';':
                if ($bracket>0) {
                    $value .= $v[0];
                    break;
                } else {
                    break 2;
                }
            }
        }
        return array($type, $value);
    }

    /**
     * Template for matching T_NAMESPACE
     *
     * @param array &$tokens Tokens
     */
    static private function tNamespace(&$tokens)
    {
        $curly = 0;
        $value = '';
        while (list($c, $v) = each($tokens)) {
            switch ($v[0]) {
            case T_STRING:
            case T_NS_SEPARATOR:
                $value .= $v[1];
                break;
            case ';':
                self::$currentPackage = self::addPackage($value);
                self::$currentQn = $value;
                if (self::$curDocComment!='') {
                    //$desc = self::getDescriptionInDocComment(self::$curDocComment);
                    //$docs = self::getDocblocksInDocComment(self::$curDocComment);
                    self::addDocumentation(self::$currentPackage);
                }
                return;
            case '{':
                $curly++;
                self::$currentPackage = self::addPackage($value);
                self::$currentQn = $value;
                if (self::$curDocComment!='') {
                    //$desc = self::getDescriptionInDocComment(self::$curDocComment);
                    //$docs = self::getDocblocksInDocComment(self::$curDocComment);
                    self::addDocumentation(self::$currentPackage);
                }
                self::$curDocComment = '';
                self::tBody($tokens);
                prev($tokens);
                break;
            case '}':
                self::$aliases = array();
                self::$currentQn = '';
                $curly--;
                if ($curly==0)
                    return;
            }
        }
    }

    /**
     * Specific template for matching the first T_STRING
     *
     * @param array &$tokens Tokens
     * 
     * @return string The value
     */
    static private function tString(&$tokens)
    {
        while (list($c, $v) = each($tokens)) {
            switch ($v[0]) {
            case T_STRING:
                return $v[1];
            }
        }
    }

    /**
     * Specific template for matching a qualified name, after a namespace instr
     *
     * @param array &$tokens Tokens
     * 
     * @return string The value
     */
    static private function tQualifiedName(&$tokens)
    {
        $value = '';
        while (list($c, $v) = each($tokens)) {
            switch ($v[0]) {
            case T_STRING:
            case T_NS_SEPARATOR:
                $value .= $v[1];
                break;
            case T_AS:
            case ';':
            case ',':
            case ')':
                prev($tokens);
                return $value;
            }
        }
    }

    /**
     * Template for matching T_USE
     *
     * @param array &$tokens Tokens
     */
    static private function tUse(&$tokens)
    {
        $qname = self::tQualifiedName($tokens);
        $alias = '';
        while (list($c, $v) = each($tokens)) {
            switch ($v[0]) {
            case T_AS:
                $alias = self::tString($tokens);
                break;
            case ',':
                self::tUse($tokens);    // no additional break please
            case ';':
                self::addUse($qname, $alias);
                return;
            }
        }
    }

    /**
     * Base template
     * It loops over PHP code, either in the global space, either inside
     * functions, or class functions
     * 
     * @param array &$tokens Tokens
     */
    static private function tBody(&$tokens)
    {
        $curly = 1;
        self::$classFeatures = array();
        while (list($c, $v) = each($tokens)) {
            switch ($v[0]) {
            case T_CLASS:
                self::tClass($tokens);
                self::$classFeatures = array();
                self::$curDocComment = '';
                break;
            case T_INTERFACE:
                self::tInterface($tokens);
                self::$classFeatures = array();
                self::$curDocComment = '';
                break;
            case T_NAMESPACE:
                list($c, $v) = current($tokens);
                if ($c!=T_NS_SEPARATOR) {
                    prev($tokens);
                    self::tNamespace($tokens);
                }
                self::$curDocComment = '';
                break;
            case T_FUNCTION:
                if (!self::$pureObj) {
                    self::$classElementFeatures = array();
                    self::tFunction($tokens);
                }
                self::$curDocComment = '';
                break;
            case T_CONST:
                if (!self::$pureObj) {
                    self::$classElementFeatures = array();
                    self::tConst($tokens);
                }
                self::$curDocComment = '';
                break;
            case T_STRING:
                if ((!self::$pureObj) && $v[1]=='define') { 
                    self::tDefine($tokens);
                }
                self::$curDocComment = '';
                break;
            case T_DOC_COMMENT:
                self::$curDocComment = self::removeDocCommentMarks($v[1]);
                self::tDocComment($tokens);
                break;
            case T_USE:
                self::tUse($tokens);
                break;
            case T_ABSTRACT:
            case T_FINAL:
                self::$classFeatures[] = $v[0];
                break;
            case T_CURLY_OPEN:
            case T_DOLLAR_OPEN_CURLY_BRACES:
            case '{':
                $curly++;
                break;
            case '}':
                $curly--;
                if ($curly==0) {
                    return;
                }
            }
        }
    }

    /**
     * Template for matching doc-comment (the docblocks)
     * We do some forward-tracking to see the instruction that comes next.
     * We must "remember" the comment only for certain instructions (class, const,
     * function, etc). If we don't get one of these instructions, we forget it.
     *
     * @param array &$tokens Tokens
     */
    static private function tDocComment(&$tokens)
    {
        while (list($c, $v) = each($tokens)) {
            switch ($v[0]) {
            case T_WHITESPACE:
            case '@':
                break;
            default:
                if (!($v[0]==T_STRING && $v[1]=='define')) { 
                    self::$curDocComment = '';
                }
            case T_DOC_COMMENT:
            case T_ABSTRACT:
            case T_FINAL:
            case T_CLASS:
            case T_INTERFACE:
            case T_FUNCTION:
            case T_CONST:
            case T_NAMESPACE:
                prev($tokens);
                return;
            }
        }
    }
    
    /**
     * Specific template for T_DOC_COMMENT.
     * Returns at the first T_DOC_COMMENT found.
     * Used to get the file-level doc-comment
     *
     * @param array &$tokens Tokens
     *
     * @return string The doc-comment, as a string
     */
    static private function tNextDocComment(&$tokens)
    {
        while (list($c, $v) = each($tokens)) {
            switch ($v[0]) { 
            case T_DOC_COMMENT:
                return self::removeDocCommentMarks($v[1]);
            case T_OPEN_TAG:
            case T_WHITESPACE:
            case T_COMMENT:
                break;
            }
        }
        return '';
    }

    /**
     * Specific template for dealing with the "define" instruction
     * 
     * @param array &$tokens Tokens
     * 
     * @return string The value
     */
    static private function tDefine(&$tokens)
    {
        $name = '';
        while (list($c, $v) = each($tokens)) {
            switch ($v[0]) { 
            case T_WHITESPACE:
            case T_COMMENT:
            case '(':
                break;
            case T_CONSTANT_ENCAPSED_STRING:
                $name = str_replace('"', '', str_replace("'", '', $v[1]));
                break;
            default:
                return;
            case ',':
                if ($name!='') {
                    self::tAttribute($tokens, $name);
                    return;
                }
                else
                    return;
            }
        }
        return;
    }

    /**
     * Adds a package to the metamodel ($packages)
     *
     * @param string                    $name       Name of the package to create
     * @param PHP_UML_Metamodel_Package $nestingPkg Enclosing package. If not given,
     *                                              the package is created at root
     *
     * @return PHP_UML_Metamodel_Package The package created (or the existing one)
     */
    static private function addPackage($name, PHP_UML_Metamodel_Package $nestingPkg = null)
    {
        if ($name=='')
            return self::$structure->packages;
 
        if (is_null($nestingPkg)) {
            // root package (global namespace)
            $nestingPkg = self::$structure->packages;
        }
   
        list($pos, $name, $following) = self::$structure->getPackagePathParts($name);
 
        // let's check if it does not already exist:
        $p = self::$structure->searchIntoSubpackage($nestingPkg, $name);
        
        // ok, pkg does not exist, let's add it:
        if ($p===false) {
            $p                 = new PHP_UML_Metamodel_Package;
            $p->id             = self::getUID();
            $p->name           = $name;
            $p->nestingPackage = $nestingPkg;
            $p->nestedPackage  = array();

            $nestingPkg->nestedPackage[] = $p;
        }

        if (!($pos===false)) {
            $p = self::addPackage($following, $p);
        }
        return $p;
    }

    /**
     * Adds a deployment package to the metamodel
     *
     * @param string                    $name       Name of the package to create
     * @param PHP_UML_Metamodel_Package $nestingPkg Enclosing package
     *
     * @return PHP_UML_Metamodel_Package The package created (or the existing one)
     */
    static private function addDeploymentPackage($name, PHP_UML_Metamodel_Package $nestingPkg = null)
    {
        if ($name=='')
            return self::$structure->deploymentPackages;
 
        if (is_null($nestingPkg)) {
            // root package (global namespace)
            $nestingPkg = self::$structure->deploymentPackages;
        }
   
        list($pos, $name, $following) = self::$structure->getPackagePathParts($name, true, '/');

        // let's check if it does not already exist:
        $p = self::$structure->searchIntoSubpackage($nestingPkg, $name);
        
        // ok, pkg does not exist, let's add it:
        if ($p===false) {
            $p                 = new PHP_UML_Metamodel_Package;
            $p->id             = self::getUID();
            $p->name           = $name;
            $p->nestingPackage = $nestingPkg;
            $p->nestedPackage  = array();

            $nestingPkg->nestedPackage[] = $p;
        }

        if (!($pos===false)) {
            $p = self::addDeploymentPackage($following, $p);
        }
        return $p;
    }
    
    /**
     * Return the index of the current package, depending on the @package in the
     * last parsed docblock, and/or the potential presence of a package path in the
     * class name.
     * Adds new packages to the $packages stack if necessary.
     * Normally used at each new class/interface insertion.
     *
     * @param string $class Name of the classifier
     *
     * @return array Result array[current package, class name]
     */
    static private function getCurrentPackage($class)
    { 
        // Where's the class-level package ?  
        // Is there a namespace along with the class name?
        list($pos, $pkg, $name) = self::$structure->getPackagePathParts($class, false);
        if (!($pos===false)) {
            return array(self::addPackage($pkg), $name);
        }

        if (self::$docblocks) { // || self::$structureFromDocblocks
            // Is there a @package in the class docblock ?
            $r = self::findPackageInDocblock(self::$curDocComment, $set);
            if ($r>0) {
                return array(self::addPackage($set[1]), $class);
            }
        }
        // No ? Then we return the current known package:
        return array(self::$currentPackage, $class);
    }


    /**
     * Adds an alias to the list of namespace aliases
     *
     * @param string $namespace An imported namespace
     * @param string $alias     Alias ("use ... as ...")
     */
    static private function addUse($namespace, $alias='')
    {
        if ($alias=='') {
            list($pos, $first, $alias) = self::$structure->getPackagePathParts($namespace, false);
        }
        if (substr($namespace, 0, 1)==self::T_NS_SEPARATOR)
            self::$aliases[$alias] = substr($namespace, 1);
        else
            self::$aliases[$alias] = $namespace;
    }
    
    /**
     * Resolve a class path into a qualified name
     * - first by searching/replacing aliases (previously set by "use ... as...")
     * - then by prefixing with the current namespace, if the path is not absolute
     * (and if it contains a separator)
     * 
     * @param string $path A qualified name (a class name like "A\B\C")
     * 
     * @return string The full qualified name
     */
    static private function resolveQName($path)
    {
        // if the path does not start by \, we try to replace the alias, if it
        // contains one
        if (count(self::$aliases)>0 && substr($path, 0, strlen(self::T_NS_SEPARATOR))!=self::T_NS_SEPARATOR) {
            foreach (self::$aliases as $a=>$qn) {
                if (substr($path.self::T_NS_SEPARATOR, 0, strlen($a)+1)==$a.self::T_NS_SEPARATOR) {
                    $x = self::T_NS_SEPARATOR.$qn;
                    $r = substr($path, strlen($a)+1);
                    if ($r!='')
                        $x .= self::T_NS_SEPARATOR.$r;
                    return $x;
                }
            }
        }
        // if there is a \ in the path, but it is not absolute, then it's relative 
        if (!(strpos($path, self::T_NS_SEPARATOR)===false) && $path[0]!=self::T_NS_SEPARATOR) {
                return self::$currentQn.self::T_NS_SEPARATOR.$path;
        }
        return $path;
    }
    
    /**
     * Docblock helpers
     */

    /**
     * Find a @package declaration in a (filtered) doc-comment
     *
     * @param string $text Text to search in
     * @param array  &$set Results (preg)
     * 
     * @return int Result (preg)
     */
    static private function findPackageInDocblock($text, &$set)
    {
        $p = preg_match('/^[ \t]*\*[ \t]+@package[ \t]+(\S*)/mi', $text, $set);
        $s = preg_match_all('/^[ \t]*\*[ \t]+@subpackage[ \t]+(\S*)/mi', $text, $sub, PREG_SET_ORDER);
        if ($p>0 && $s>0) {
            foreach ($sub as $subItem) {
                $set[1] .= self::T_NS_SEPARATOR.$subItem[1];
            }
        }
        return $p; 
    }

    /**
     * Return the docblocks of a doc-comment
     * 
     * @param string $text Doc comment
     * 
     * @return array Preg results
     */
    static private function getDocblocksInDocComment($text)
    {
        $r = preg_match_all(
            '/^[ \t]*\*[ \t]*@([\w]+)[ \t]*([^\s]*)[ \t]*([^\s]*)[ \t]*(.*)/m',
            $text, $set, PREG_SET_ORDER
        );
        return $set;
    }

    /**
     * Return the description part in a doc-comment
     * It is found before the first docblock.
     * 
     * @param string $text Doc comment
     * 
     * @return string The description
     */
    static private function getDescriptionInDocComment($text)
    {
        // we first detect when the docblocs start:
        if(preg_match('/^[ \t]*\*[ \t]*@/m', $text, $set, PREG_OFFSET_CAPTURE))
            $text = substr($text, 0, $set[0][1]);

        // and we take and filter everything before:
        $r   = preg_match_all('/^[ \t]*\*(.*)/m', $text, $set, PREG_SET_ORDER);
        $str = '';
        foreach ($set as $item) {
            $line = trim($item[1]);
            $last = substr($line, -1, 1);
            if ($last=='.' || $last==':' || $line=='')
                $str .= $line.PHP_EOL;
            else
                $str .= $line.' ';
        }
        return trim($str);
    }
    
    /**
     * Stores the description/comments (in self::$curDocComment) about an element 
     * It is directly added as an object Stereotype to the $structure->stereotypes array
     * Note that the link between a stereotype and its element is bidirectional
     * (element->description and stereotype->element)
     *
     * @param PHP_UML_Metamodel_NamedElement &$element The element
     */
    static private function addDocumentation(PHP_UML_Metamodel_NamedElement &$element)
    {
        $desc = self::getDescriptionInDocComment(self::$curDocComment);
        $docs = self::getDocblocksInDocComment(self::$curDocComment);

        $s = self::$structure;
        $s->addDocTags($element, $desc, $docs);
    }

    /**
     * Removes the leading/trailing comment marks
     *
     * @param string $text Text to filter
     * 
     * @return string Filtered text
     */
    static private function removeDocCommentMarks($text)
    {
        return substr(substr($text, 0, -2), 2);
    }
    
    /**
     * Returns a unique ID
     * 
     * @return string
     */
    static private function getUID()
    {
        return PHP_UML_SimpleUID::getUID();
    }

    /**
     * Returns TRUE if the current element must be ignored (because the docblock
     * contains @internal, or because onlyAPI is set)
     * 
     * @param string $text Docblocks to look into
     * 
     * @return bool
     */
    static private function toBeSkipped($text)
    {
        return (self::$skipInternal && !(stristr($text, '@internal')===false))
            || (self::$onlyApi && stristr($text, '@api')===false);
    }

    /**
     * Filter a variable names
     * (removes $ according to _keepDollar property)
     *
     * @param string $str Text to filter
     * 
     * @return string
     */
    static private function cleanVariable($str)
    {
        if (self::$keepDollar) {
            return $str;
        } else {
            return str_replace('$', '', $str);
        }
    }
 
    /**
     * Sets the nesting package of a type
     *
     * @param PHP_UML_Metamodel_Classifier &$c         A classifier
     * @param PHP_UML_Metamodel_Package    $nestingPkg The enclosing package
     */
    static private function setNestingPackageOfType(PHP_UML_Metamodel_Classifier &$c, PHP_UML_Metamodel_Package $nestingPkg)
    {
        $c->package = $nestingPkg;
        if (self::$structure->searchTypeIntoPackage($c->package, $c->name)===false) {
            $nestingPkg->ownedType[] = &$c;
            self::$file->manifested[] = &$c;
        } else {
            PHP_UML_Warning::add(
                'Class '.$c->name.' already defined, in '.self::$file->name
            );
        }
    }

    /**
     * Sets the nesting package of an isolated operation
     *
     * @param PHP_UML_Metamodel_Operation &$o         A function
     * @param PHP_UML_Metamodel_Package   $nestingPkg The enclosing package
     */
    static private function setNestingPackageOfOperation(PHP_UML_Metamodel_Operation &$o, PHP_UML_Metamodel_Package $nestingPkg)
    {
        $o->package = $nestingPkg;
        if (self::$structure->searchOperationIntoPackage($o->package, $o->name)===false) {
            $nestingPkg->ownedOperation[] = &$o;
            self::$file->manifested[] = &$o;
        } else {
            PHP_UML_Warning::add(
                'Function '.$o->name.' already defined, in '.self::$file->name
            );
        }
    }

    /**
     * Sets the nesting package of an isolated attribute (the PHP "const")
     *
     * @param PHP_UML_Metamodel_Property &$a         A property
     * @param PHP_UML_Metamodel_Package  $nestingPkg The enclosing package
     */
    static private function setNestingPackageOfAttribute(PHP_UML_Metamodel_Property &$a, PHP_UML_Metamodel_Package $nestingPkg)
    {
        $a->package = $nestingPkg;
        if (self::$structure->searchAttributeIntoPackage($a->package, $a->name)===false) {
            $nestingPkg->ownedAttribute[] = &$a;
            self::$file->manifested[] = &$a;
        } else {
            PHP_UML_Warning::add(
                'Constant '.$a->name.' already defined, in '.self::$file->name
            );
        }
    }

    /**
     * Sets the features (abstract, final) to a classifier
     *
     * @param PHP_UML_Metamodel_Interface &$c Class or interface object
     */
    static private function setClassifierFeatures(PHP_UML_Metamodel_Classifier &$c)
    {
        foreach (self::$classFeatures as $token) {
            switch ($token) {
            case T_ABSTRACT:
                $c->isAbstract = true;
                break;
            case T_FINAL:
                $c->isReadOnly = true;
                break;
            }      
        }
    }

    /**
     * Set the features (static, private...) in a given class property/function
     *
     * @param PHP_UML_Metamodel_NamedElement &$c Element
     */
    static private function setElementFeatures(PHP_UML_Metamodel_NamedElement &$c)
    {
        $c->isInstantiable = true;
        $c->visibility     = 'public';
        foreach (self::$classElementFeatures as $token) {
            switch ($token) {
            case T_STATIC:
                $c->isInstantiable = false;
                break;
            case T_ABSTRACT:
                $c->isAbstract = true;
                break;
            case T_PRIVATE:
                $c->visibility = 'private';
                break;
            case T_PROTECTED:
                $c->visibility = 'protected';
                break;
            case T_CONST:
                $c->isInstantiable = false;
            case T_FINAL:
                $c->isReadOnly = true;
                break;
            }      
        }
    }

    /**
     * Set the type of a given element (class property or function)
     *
     * @param PHP_UML_Metamodel_NamedElement &$p      The element
     * @param string                         $type    Explicit type (type hint)
     * @param string                         $token   The token
     * @param string                         $default The (eventual) default value
     * @param string                         $varName The variable name
     * @param array                          $dbParam The preg-array of docblocks
     */
    static private function setTypeElement(PHP_UML_Metamodel_NamedElement &$p, $type, $token, $default, $varName, array $dbParam)
    {
        if ($type!='') {
            $p->type = self::resolveQName($type);
        } else {
            $type = self::getTypeFromToken($token, $default);
            if ($type!='') {
                $p->type = $type;
            } else {
                $type = self::getTypeFromDocblocks($dbParam, $varName);
                if($type!='')
                    $p->type = $type;
                else
                    $p->type = 'mixed';
            }
        }
    }
    
    /**
     * Return a type (as a string) according to a given token/default value
     *
     * @param string $token        Token
     * @param string $defaultValue Value
     * 
     * @return string The type
     */
    static private function getTypeFromToken($token, $defaultValue)
    {
        switch($token) {
        case T_ARRAY:
            return 'array';
        case T_CONSTANT_ENCAPSED_STRING:
            return 'string';
        case T_DNUMBER:
            return 'float';
        case T_LNUMBER:
            return 'int';
        case T_STRING:
            if ($defaultValue=='true' || $defaultValue=='false')
                return 'bool';
            if ($defaultValue=='void')
                return 'void';
        }            
        return '';
    }
    
    /**
     * Return a type (as a string), from the docblocks of a doc comment
     *
     * @param array  $dbParam  The array of params (obtained through a preg)
     * @param string $variable The name of the parameter you want to know the type
     *
     * @return string The type
     */
    static private function getTypeFromDocblocks(array $dbParam, $variable)
    {
        if (isset($dbParam[$variable]) && self::$docblocks)
            $param = $dbParam[$variable];
        else
            return '';
        if (isset($param[2]))
            return $dbParam[$variable][2];
        else
            return '';
    }
    
}
?>
