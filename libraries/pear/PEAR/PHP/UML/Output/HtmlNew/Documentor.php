<?php
/**
 * PHP_UML
 *
 * PHP version 5
 *
 * @category PHP
 * @package  PHP_UML
 * @author   Baptiste Autin <ohlesbeauxjours@yahoo.fr> 
 * @license  http://www.gnu.org/licenses/lgpl.html LGPL License 3
 * @version  SVN: $Revision: 139 $
 * @link     http://pear.php.net/package/PHP_UML
 * @since    $Date: 2009-12-13 21:48:54 +0100 (dim., 13 déc. 2009) $
 */

/**
 * Top-level class for the HTML "renderers"
 *
 * @category   PHP
 * @package    PHP_UML
 * @subpackage Output
 * @subpackage HtmlNew
 * @author     Baptiste Autin <ohlesbeauxjours@yahoo.fr> 
 * @license    http://www.gnu.org/licenses/lgpl.html LGPL License 3
 */
abstract class PHP_UML_Output_HtmlNew_Documentor
{
    const PACKAGE_FILENAME  = 'package-summary';
    const CLASS_PREFIX      = 'class-';
    const INTERFACE_PREFIX  = 'interface-';
    const DATATYPE_PREFIX   = 'datatype-';
    const TEMPLATES_DIRNAME = PHP_UML_Output_HtmlNew_Exporter::TEMPLATES_DIRNAME;
    const FILE_EXT          = PHP_UML_Output_HtmlNew_Exporter::FILE_EXT;
    const RESOURCES_DIRNAME = PHP_UML_Output_HtmlNew_Exporter::RESOURCES_DIRNAME;
    const HELP_FILENAME     = PHP_UML_Output_HtmlNew_Exporter::HELP_FILENAME;
    const INDEX_FILENAME    = 'index-all';
    const MENU_FILENAME     = 'menu';
    const JS_MAIN_NAME      = 'MainList';

    const META_INTERFACE = PHP_UML_Metamodel_Superstructure::META_INTERFACE;
    const META_CLASS     = PHP_UML_Metamodel_Superstructure::META_CLASS;
    const META_DATATYPE  = PHP_UML_Metamodel_Superstructure::META_DATATYPE;
    const META_OPERATION = PHP_UML_Metamodel_Superstructure::META_OPERATION;
    const META_PROPERTY  = PHP_UML_Metamodel_Superstructure::META_PROPERTY;

    const T_NAMESPACE = '\\';

    /**
     * HTML main template (we pre-store it to avoid reading repeatedly) 
     * @var string
     */
    protected $mainTpl;

    /**
     * Classes that won't appear directly in the main API lists
     * for readability (even though each one has its own detail page)
     * @var array
     */
    protected $hiddenClasses = array();

    /**
     * Hidden interfaces
     * @var array
     */
    protected $hiddenInterfaces = array();

    /**
     * Hidden classifiers
     * @var array
     */
    protected $hiddenClassifiers = array();

    /**
     * These docblocks will not be displayed in the docblocks list 
     * @var array
     */
    static private $ignoredTag = array('var');

    /**
     * Reference to the Context object, which stores various useful data
     * @var PHP_UML_Output_HtmlNew_Context
     */
    protected $ctx;

    /**
     * Generates and saves the HTML code for a type of element
     * 
     * @param PHP_UML_Metamodel_Package $p Starting package (model)
     */
    abstract function createHtml($p);

    public function __construct(PHP_UML_Output_HtmlNew_Context $ctx)
    {
        $this->mainTpl = self::getTemplate('main.htm');

        $this->hiddenClasses     = array_merge($this->hiddenClasses, PHP_UML_Metamodel_Enumeration::$classes);
        $this->hiddenInterfaces  = array_merge($this->hiddenInterfaces, PHP_UML_Metamodel_Enumeration::$interfaces);
        $this->hiddenClassifiers = array_merge($this->hiddenClassifiers, $this->hiddenClasses);
        $this->hiddenClassifiers = array_merge($this->hiddenClassifiers, $this->hiddenInterfaces);
        $this->hiddenClassifiers = array_merge($this->hiddenClassifiers, PHP_UML_Metamodel_Enumeration::$datatypes);

        $this->ctx = $ctx;
    }

    protected function getDescription(PHP_UML_Metamodel_Stereotype $s, $annotatedElement='')
    {
        $tag = PHP_UML_Metamodel_Superstructure::getStereotypeTag($s, 'description');
        if (!is_null($tag))
            return nl2br(htmlspecialchars($tag->value));
        else
            return '';
    }

    /**
     * Returns the operation's parameters, as a comma-sep list, between brackets
     * 
     * @param PHP_UML_Metamodel_Operation $operation The operation
     * @param bool                        $withType  If true, adds an hyperlink
     * 
     * @return string
     */
    protected function getParameterList(PHP_UML_Metamodel_Operation $operation, $withType = false)
    {
        $str = '(';
        $n   = count($operation->ownedParameter);
        for ($i=0; $i<$n; $i++) {
            $parameter = $operation->ownedParameter[$i];
            if (substr($parameter->direction, 0, 2)=='in') {
                if ($withType && isset($parameter->type)) {
                    if (is_object($parameter->type))
                        $str .= $this->getLinkTo($parameter->type).' ';
                    else
                        $str .= $this->displayUnresolved($parameter->type);
                }
                if ($parameter->direction=='inout') {
                    $str .= '&#38;';
                }
                $str .= $parameter->name;
                $str .= $this->getDefaultValue($parameter);
                if ($i<($n-1))
                    $str .= ', ';
            }
        }
        $str .= ')';
        return $str;
    }

    /**
     * Returns the HTML code for a default value
     * 
     * @param PHP_UML_Metamodel_TypedElement $obj A property or a parameter
     * 
     * @return string
     */
    protected function getDefaultValue(PHP_UML_Metamodel_TypedElement $obj)
    {
        if ($obj->default!='')
            return '<span class="defVal"> = '.htmlentities($obj->default, ENT_QUOTES, 'UTF-8').'</span>';
        else
            return '';
    }

    /**
     * Returns the content of a template file
     * 
     * @param string $str Template filename
     *     
     * @return string The content of the template
     */
    static public function getTemplate($str)
    {
        $baseSrc = dirname(__FILE__).DIRECTORY_SEPARATOR.self::TEMPLATES_DIRNAME;
        return file_get_contents($baseSrc.DIRECTORY_SEPARATOR.$str);
    }
    
    static protected function getObjPrefix(PHP_UML_Metamodel_NamedElement $x)
    {
        switch (get_class($x)) {
        case self::META_INTERFACE:
            return self::INTERFACE_PREFIX;
        case self::META_DATATYPE:
            return self::DATATYPE_PREFIX;
        case self::META_OPERATION:
            return self::PACKAGE_FILENAME;
        case self::META_PROPERTY:
            return self::PACKAGE_FILENAME;
        }
        return self::CLASS_PREFIX;
    }

    static protected function getObjStyle(PHP_UML_Metamodel_NamedElement $x)
    {
        switch (get_class($x)) {
        case self::META_INTERFACE:
            return 'interface';
        case self::META_DATATYPE:
            return 'datatype';
        case self::META_OPERATION:
            return 'method';
        case self::META_PROPERTY:
            return 'property';
        }
        return 'class';
    }

    /**
     * Returns a HTML hyperlink towards a given element
     * (since datatypes don't own to a "package",  we suppose they are located in
     * the top package)
     * 
     * @param PHP_UML_Metamodel_Classifier $t        The element
     * @param string                       $cssStyle CSS style to use
     * 
     * @return string
     */
    protected function getLinkTo(PHP_UML_Metamodel_Classifier $t, $cssStyle='link')
    {
        $loc = '';
        $ns  = '';
        if (isset($t->package)) {
            $loc = $this->getAbsPath($t->package);
            $ns  = $this->getAbsPath($t->package, self::T_NAMESPACE);
        }
        return '<a href="'.$this->ctx->rpt.$loc.self::getObjPrefix($t).$t->name.'.'.
            self::FILE_EXT.'" class="'.$cssStyle.'">'.$ns.$t->name.'</a>';
    }

    /**
     * Returns the complete namespace for an element
     * 
     * @param PHP_UML_Metamodel_Classifier $t The classifier
     * 
     * @return string
     */
    protected function getQualifiedName(PHP_UML_Metamodel_Classifier $t)
    {
        $ns = isset($t->package) ? $this->getAbsPath($t->package, self::T_NAMESPACE) : '';
        return $ns.$t->name;
    }

    /**
     * Returns a HTML code for an unresolved type
     * 
     * @param string $type Type, provided as a string
     * 
     * @return string
     */
    protected function displayUnresolved($type)
    {
        return '<span class="link">'.$type.'</span> ';
    }
    
    /**
     * Returns the path from the top package to a given package
     * 
     * @param PHP_UML_Metamodel_Package $p         The package
     * @param string                    $delimiter Delimiter
     * 
     * @return string
     */
    protected function getAbsPath(PHP_UML_Metamodel_Package $p, $delimiter='/')
    {
        if (!empty($p->nestingPackage)) {
            return $this->getAbsPath($p->nestingPackage, $delimiter).$p->name.$delimiter;
        }
        else
            return '';
    }

    /**
     * Returns the return parameter of a function
     * 
     * @param PHP_UML_Metamodel_Operation $operation The function
     * 
     * @return PHP_UML_Metamodel_Parameter The parameter
     */
    protected function getReturnParam(PHP_UML_Metamodel_Operation $operation)
    {
        foreach ($operation->ownedParameter as $p) {
            if ($p->direction=='return') {
                return $p;
            }
        }
        return null;
    }

    /**
     * Returns an HTML list (LI tags) of all the properties of a given stereotype
     * Docblocks in $ignoredTag are not shown, as well as "return" tag with only a type
     * 
     * @param PHP_UML_Metamodel_Stereotype $s A stereotype
     * 
     * @return string
     */
    protected function getTagsAsList(PHP_UML_Metamodel_Stereotype $s)
    {
        $str = '';
        foreach ($s->ownedAttribute as $tag) {
            if (!(in_array($tag->name, self::$ignoredTag) || ($tag->name=='return' && strpos($tag->value, ' ')===false))) {
                if ($tag->name!='description') {
                    $str .= '<li class="smaller">';
                    $str .= '@'.$tag->name.' ';
                } else {
                    $str .= '<li>';
                }
                if (strlen($tag->value)>0)
                    $str .= nl2br(htmlspecialchars($tag->value));
                $str .= '</li>';
            }
        }   
        return $str;
    }

    /**
     * Returns the array containing all the extended classes of a classifier
     * This array must have been previously set in the Context object
     * 
     * @param PHP_UML_Metamodel_NamedElement $c A classifier or a package
     * 
     * @return array
     */
    protected function getAllInherited(PHP_UML_Metamodel_NamedElement $c)
    {
        return array_key_exists($c->id, $this->ctx->allInherited) ? $this->ctx->allInherited[$c->id] : array();
    }

    /**
     * Returns the array containing all the classes that extends a classifier
     * 
     * @param PHP_UML_Metamodel_NamedElement $c A classifier or a package
     * 
     * @return array
     */
    protected function getAllInheriting(PHP_UML_Metamodel_NamedElement $c)
    {
        return array_key_exists($c->id, $this->ctx->allInheriting) ? $this->ctx->allInheriting[$c->id] : array();
    }

    /**
     * Returns the array containing all the interfaces of a classifier
     * 
     * @param PHP_UML_Metamodel_NamedElement $c A classifier or a package
     * 
     * @return array
     */
    protected function getAllImplemented(PHP_UML_Metamodel_NamedElement $c)
    {
        return array_key_exists($c->id, $this->ctx->allImplemented) ? $this->ctx->allImplemented[$c->id] : array();
    }

    /**
     * Returns the array containing all the classes that implement a given interface
     * 
     * @param PHP_UML_Metamodel_NamedElement $c A classifier or a package
     * 
     * @return array
     */
    protected function getAllImplementing(PHP_UML_Metamodel_NamedElement $c)
    {
        return array_key_exists($c->id, $this->ctx->allImplementing) ? $this->ctx->allImplementing[$c->id] : array();
    }

    /**
     * Saves a string in a file (in the folder referenced in the context object)
     * 
     * @param string $elementName Filename (without the extension)
     * @param string $str         Content
     */
    protected function save($elementName, $str)
    {
        $fic = $this->ctx->dir.$elementName.'.'.self::FILE_EXT;
        file_put_contents($fic, $str);
    }

    /**
     * Returns the HTML code for the block "Properties" of a package or a class
     * 
     * @param PHP_UML_Metamodel_NamedElement $p A classifier/a package
     * 
     * @return string
     */
    protected function getPropertyBlock(PHP_UML_Metamodel_NamedElement $p)
    {
        if (empty($p->ownedAttribute))
            return '';

        $str  = '<h2>Properties</h2>';
        $str .= '<ul class="summary">';
        foreach ($p->ownedAttribute as $o) {
            $str .= '<li class="Collapsed" id="'.$this->generatePropertyId($o).'">';
            $str .= '<a href="javascript:void(0);" class="'.$this->getPropertyStyle($o->visibility).'" target="main">'.
                $o->name.'</a>';
            if (isset($o->description)) {
                $str .= '<ul class="description"><li>';
                $str .= ucfirst($o->visibility).' ';
                if (!$o->isInstantiable)
                    $str .= 'static ';
                if ($o->isReadOnly)
                    $str .= 'const ';
                if (is_object($o->type))
                    $str .= $this->getLinkTo($o->type).' ';
                else
                    $str .= $this->displayUnresolved($o->type);
                $str .= '<span class="smallTitle">'.$o->name.'</span>'.$this->getDefaultValue($o).'</li>';
                if (!is_null($o->description)) {
                    $str .= $this->getTagsAsList($o->description);
                }
                $str .= $this->getFileInfo($o);
                $str .= '</ul>';
            }
            $str .= '</li>';
        }
        $str .= '</ul>';
        return $str;
    }

    /**
     * Returns an ID for a property
     * 
     * @param PHP_UML_Metamodel_Property $o Element
     * 
     * @return string
     */
    protected function generatePropertyId(PHP_UML_Metamodel_Property $o)
    {
        return str_replace('$', '', $o->name);
    }
    
    /**
     * Returns an ID to identify a function
     * 
     * @param PHP_UML_Metamodel_Operation $o Element
     * 
     * @return string
     */
    protected function generateFunctionId(PHP_UML_Metamodel_Operation $o)
    {
        return 'f'.$o->id;
    }
    
    /**
     * Returns the HTML code for the block "Function" of a package or a classifier
     * 
     * @param PHP_UML_Metamodel_NamedElement $p A classifier or a package
     * 
     * @return string
     */
    protected function getFunctionBlock(PHP_UML_Metamodel_NamedElement $p)
    {
        if (empty($p->ownedOperation))
            return'';

        $str  = '<h2>Functions</h2>';
        $str .= '<ul class="summary">';
        foreach ($p->ownedOperation as $o) {
            $fullName = $this->getParameterList($o, true);

            $str .= '<li class="Collapsed" id="'.$this->generateFunctionId($o).'">';
            $str .= '<a href="javascript:void(0);" class="'.$this->getFunctionStyle($o->visibility);
            if ($o->isAbstract)
                $str .= ' abstract';
            $str .= '" target="main">'.$o->name.'</a>'.$fullName;
            if (isset($o->description)) {
                $str .= '<ul class="description"><li>';
                $str .= ucfirst($o->visibility).' ';
                if (!$o->isInstantiable)
                    $str .= 'static ';
                if ($o->isAbstract)
                    $str .= 'abstract ';
                $return = $this->getReturnParam($o);
                if (is_object($return->type))
                    $str .= $this->getLinkTo($return->type).' ';
                else
                    $str .= $this->displayUnresolved($return->type);
                $str .= '<span class="smallTitle">'.$o->name.'</span>'.$fullName.'</li>';

                if (!is_null($o->description)) {
                    $str .= $this->getTagsAsList($o->description);
                }
                foreach ($this->getAllImplemented($p) as $ai) {
                    foreach ($ai->ownedOperation as $aiO) {
                        if ($aiO->name == $o->name) {
                            $txt = $this->getDescription($aiO->description, $aiO->id);
                            if ($txt!='')
                                $str .= '<li>'.$txt.'<br/><span class="note">(copied from interface '.$this->getLinkTo($ai).')</span></li>';
                        }
                    }
                }
                foreach ($this->getAllInherited($p) as $ai) {
                    foreach ($ai->ownedOperation as $aiO) {
                        if ($aiO->name == $o->name) {
                            $txt = $this->getDescription($aiO->description, $aiO->id);
                            if ($txt!='')
                                $str .= '<li>'.$txt.'<br/><span class="note">(copied from class '.$this->getLinkTo($ai).')</span></li>';
                        }
                    }
                }
                $str .= $this->getFileInfo($o);
                $str .= '</ul>';
            }
            $str .= '</li>';
        }
        $str .= '</ul>';
        return $str;
    }

    /**
     * Returns the HTML for the link "Package" in the navigation bar
     * 
     * @param string $rel A prefix to add to the hyperlink (eg: ../)
     * 
     * @return string
     */
    protected function getNavigParentPackage($rel='')
    {
        return '<li><a href="'.$rel.self::PACKAGE_FILENAME.'.'.self::FILE_EXT.'" class="top">Package</a></li>';
    }

    /**
     * Returns the HTML code for the common items of the navigation bar
     * 
     * @return string
     */
    protected function getCommonLinks()
    {
        return '<li><a href="javascript:toggler.toggleAll(\''.self::JS_MAIN_NAME.'\', \'btnToggle\')" class="expandAllBtn" id="btnToggle">Expand all</a></li>'.
            '<li><a href="'.$this->ctx->rpt.self::HELP_FILENAME.'" class="helpBtn">Help</a></li>'.
            '<li><a href="'.$this->ctx->rpt.self::INDEX_FILENAME.'.'.self::FILE_EXT.'" class="indexAllBtn">Index</a></li>';
    }
    
    /**
     * Returns the HTML code for the "File" information tag
     * 
     * @param PHP_UML_Metamodel_NamedElement $p An element
     * 
     * @return string
     */
    protected function getFileInfo(PHP_UML_Metamodel_NamedElement $p)
    {
        if (!empty($p->file->package))
            return '<li>File: '.$this->getAbsPath($p->file->package).$p->file->name.'</li>';
        else
            return '';
    }

    protected function getPropertyStyle($visibility)
    {
        return 'property-'.substr($visibility, 0, 3);
    }
    
    protected function getFunctionStyle($visibility)
    {
        return 'method-'.substr($visibility, 0, 3);
    }
    
    /**
     * Replace the template's placeholders with their value
     *  
     * @param string $main Main HTML content (generated by PHP_UML)
     * @param string $nav  Navigation HTML content (navig bar)
     * @param string $tit  Title content
     * @param string $name Element name
     * 
     * @return string
     */
    protected function replaceInTpl($main, $nav, $tit, $name)
    {
        $str = str_replace('#NAVIG', $nav, $this->mainTpl);
        $str = str_replace('#TITLE', $tit, $str);
        $str = str_replace('#DETAIL', $main, $str);
        $str = str_replace('#RELPATHTOP', $this->ctx->rpt, $str);
        $str = str_replace('#NAME', $this->getTypeName().' '.$name, $str);
        $str = str_replace('#CURDATE', date("M j, Y, G:i:s O"), $str);
        return $str;
    }
}
?>
