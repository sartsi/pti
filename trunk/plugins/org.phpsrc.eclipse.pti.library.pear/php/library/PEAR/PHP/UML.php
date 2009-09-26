<?php
/**
 * PHP Parser and UML/XMI generator. Reverse-engineering tool.
 *
 * A package to scan PHP files and directories, and get an UML/XMI representation
 * of the parsed classes/packages.
 * The XMI code can then be imported into a UML designer tool, like Rational Rose
 * or ArgoUML.
 *
 * PHP version 5
 *
 * @category PHP
 * @package  PHP_UML
 * @author   Baptiste Autin <ohlesbeauxjours@yahoo.fr>
 * @license  http://www.gnu.org/licenses/lgpl.html LGPL License 3
 * @version  SVN: $Revision: 129 $
 * @link     http://pear.php.net/package/PHP_UML
 * @link     http://www.baptisteautin.com/projects/PHP_UML/
 * @since    $Date: 2009-08-27 17:50:59 +0200 (jeu., 27 août 2009) $
 */

require_once 'PEAR/Exception.php';

spl_autoload_register(array('PHP_UML', 'autoload'));


/**
 * The main class to use, through its methods:
 * - setInput(), parse(), parseFile() and/or parseDirectory()
 * - generateXMI()
 * - saveXMI()
 * 
 * For example:
 * <code>
 * $t = new PHP_UML();
 * $t->parseDirectory('PHP_UML/');
 * $t->generateXMI(2);
 * $t->saveXMI('PHP_UML2.xmi');
 * </code>
 * 
 * If you want to produce XMI without using the PHP parser, please refer to
 * the file /examples/test_with_api.php; it will show how you can build a
 * model by yourself, with the PHP_UML_Metamodel package.
 * 
 * @category PHP
 * @package  PHP_UML
 * @author   Baptiste Autin <ohlesbeauxjours@yahoo.fr>
 * @license  http://www.gnu.org/licenses/lgpl.html LGPL License 3
 * @link     http://pear.php.net/package/PHP_UML
 * @link     http://www.baptisteautin.com/projects/PHP_UML/
 * @see      PHP_UML_Metamodel_Superstructure
 * 
 */
class PHP_UML
{
    /**
     * Character used to separate the patterns passed to setIgnorePattern() and
     * setMatchPattern().
     * @var string
     */
    const PATTERN_SEPARATOR = ',';

    /**
     * If true, a UML logical view is created.
     * @var boolean
     */
    public $logicalView = true;

    /**
     * If true, a UML deployment view is created.
     * Each file produces an artifact.
     * @var boolean
     */
    public $deploymentView = true;

    /**
     * If true, a component view is created.
     * file system. Each file produces an component
     * @var boolean
     */
    public $componentView = false;

    /**
     * If true, the docblocks content is parsed.
     * All possible information is retrieved : general comments, @package, @param...
     * @var boolean
     */
    public $docblocks = true;

    /**
     * If true, the elements (class, function) are included in the API only if their
     * comments contain explicitly a docblock "@api"
     * @var boolean
     */
    public $onlyApi = false;

    /**
     * If true, the elements marked with @internal are included in the API.
     * @var boolean
     */
    public $showInternal = false;

    /**
     * If true, the PHP variable prefix $ is kept
     * @var boolean
     */
    public $dollar = true;

    /**
     * A reference to a PHP_UML_Metamodel_Superstructure object.
     * @var PHP_UML::Metamodel::PHP_UML_Metamodel_Superstructure
     */
    public $model;

    
    /**
     * The concatened XMI string
     * @var string
     */
    private $xmi = '';
    
    /**
     * A reference to a PHP_UML_XMI_BuilderImplX object
     * @var PHP_UML_XMI_Builder
     */
    private $builder;

    /**
     * List of directories to scan
     * @var array
     */
    private $directories = array();
    
    /**
     * List of files to scan
     * @var array
     */
    private $files = array();

    /**
     * Allowed filenames (possible wildcards are ? and *)
     * 
     * @var array
     */
    private $matchPatterns = array('*.php');

    /**
     * Ignored directories (possible wildcards are ? and *)
     *
     * @var array();
     */
    private $ignorePatterns = array();

    /**
     * Constructor
     *
     */
    public function __construct()
    {
    }

    /**
     * Parses a PHP file, and builds a PHP_UML_Metamodel_Superstructure object
     * corresponding to what has been found in the file.
     * To get the XMI serialization of that object, run generateXMI()
     *
     * @param mixed  $files File(s) to parse. Can be a single file,
     *                      or an array of files.
     * @param string $model Model name
     */
    public function parseFile($files, $model = 'default')
    {
        $this->setInput($files);
        $this->parse($model);
    }

    /**
     * Read the content of an existing XMI file.
     * If the file is UML/XMI 1, a conversion to version 2 is automatically applied.
     *
     * @param string $filepath Filename
     */
    public function readXMIFile($filepath)
    {
        if (file_exists($filepath)) {
            $this->xmi = file_get_contents($filepath);
        } else {
            throw new PHP_UML_Exception('Could not open '.$filepath);
        }

        $xmlDom = new DomDocument;
        $xmlDom->loadXML($this->xmi);
        $version = $xmlDom->getElementsByTagName('XMI')->item(0)->getAttribute('xmi.version');
        if ($version=='')
            $version = $xmlDom->getElementsByTagName('XMI')->item(0)->getAttribute('xmi:version');
        if ($version<2) {
            $this->xmi = PHP_UML_Output_Exporter::transform('xmi1to2.xsl', $this->xmi);
        }
    }

    /**
     * Set the input elements (files and/or directories) to parse
     *
     * @param mixed $pathes Array, or string of comma-separated-values
     */
    public function setInput($pathes)
    {
        if (!is_array($pathes)) {
            $pathes = explode(self::PATTERN_SEPARATOR, $pathes);
            $pathes = array_map('trim', $pathes);
        }
        if (count($pathes)==1 && strtolower(substr($pathes[0], -4))=='.xmi') {
            return $this->readXMIFile($pathes[0]);
        }
        foreach ($pathes as $path) {
            if (is_file($path))
                $this->files[] = $path;
            elseif (is_dir($path))
                $this->directories[] = $path;
        }    
    }


    /**
     * Setter for the filename patterns.
     * Usage: $phpuml->setFilePatterns(array('*.php', '*.php5'));
     * Or:    $phpuml->setFilePatterns('*.php, *.php5');
     *
     * @param mixed $patterns List of patterns (string or array)
     */
    public function setMatchPatterns($patterns)
    {
        if (is_array($patterns)) {
            $this->matchPatterns = $patterns;
        } else {
            $this->matchPatterns = explode(self::PATTERN_SEPARATOR, $patterns);
            $this->matchPatterns = array_map('trim', $this->matchPatterns);
        }
    }

    /**
     * Set a list of files / directories to ignore during parsing
     * Usage: $phpuml->setIgnorePatterns(array('examples', '.svn'));
     * Or:    $phpuml->setIgnorePatterns('examples .svn');
     * 
     * @param mixed $patterns List of patterns (string or array)
     */
    public function setIgnorePatterns($patterns)
    {
        if (is_array($patterns)) {
            $this->ignorePatterns = $patterns;
        } else {
            $this->ignorePatterns = explode(self::PATTERN_SEPARATOR, $patterns);
        }
        $this->ignorePatterns = array_map(array('self', 'cleanPattern'), $this->ignorePatterns);
    }

    /**
     * Converts a path pattern to the format expected by FileScanner
     * (separator can only be / ; must not start by any separator)
     * 
     * @see PHP_UML_FilePatternFilterIterator#accept()
     *
     * @param string $p Pattern
     */
    public static function cleanPattern($p)
    {
        $p = str_replace('/', DIRECTORY_SEPARATOR, trim($p));
        if ($p[0]==DIRECTORY_SEPARATOR)
            $p = substr($p, 1);
        return $p;
    }

    /**
     * Set the packages to include in the XMI code
     * By default, ALL packages found will be included.
     *
     * @param mixed $packages List of packages (string or array)
     * TODO
     
    public function setPackages($packages)
    {
        if (is_array($patterns)) {
            $this->packages = $patterns;
        }
        else {
            $this->packages = explode(self::PATTERN_SEPARATOR, $patterns);
            $this->packages = array_map('trim', $this->packages);
        }  
    }
    */


    /**
     * Parses a PHP folder, and builds a PHP_UML_Metamodel_Superstructure object
     * corresponding to what has been parsed.
     * To get the XMI serialization of that object, run generateXMI()
     *
     * @param mixed  $directories Directory path(es). Can be a single path,
     *                            or an array of pathes.
     * @param string $model       Model name
     */
    public function parseDirectory($directories, $model = 'default')
    {
        $this->setInput($directories);
        $this->parse($model);
    }

    /**
     * Parse the directories and the files (depending on what the $directories
     * and $files properties have been set to with setInput())
     *
     * @param string $modelName A model name (e.g., the name of your application)
     */
    public function parse($modelName = 'default')
    {
        $this->visited = array();

        $this->model                 = new PHP_UML_Metamodel_Superstructure();
        $this->model->packages       = new PHP_UML_Metamodel_Package;
        $this->model->packages->name = $modelName;
        $this->model->packages->id   = PHP_UML_SimpleUID::getUID();
        $this->model->addInternalPhpTypes($this->model->packages);

        $this->model->deploymentPackages       = new PHP_UML_Metamodel_Package;
        $this->model->deploymentPackages->name = 'Deployment View';
        $this->model->deploymentPackages->id   = PHP_UML_SimpleUID::getUID();

        $fileScanner = new PHP_UML_FileScannerImpl();

        $fileScanner->files          = $this->files;
        $fileScanner->directories    = $this->directories;
        $fileScanner->matchPatterns  = $this->matchPatterns;
        $fileScanner->ignorePatterns = $this->ignorePatterns;

        $fileScanner->parser = new PHP_UML_PHP_Parser($this->model, $this->docblocks, $this->dollar, !$this->showInternal, $this->onlyApi);
        $fileScanner->scan();

        $this->model->resolveAll();
    }
 
 
    /**
     * Runs the XMI generator on the PHP model stored in $this->model.
     * 
     * After their PHP parsing, parseDirectory() or parseFile() will have set $this->model for you.
     *
     * If you need to use the XMI generator without any previous PHP parsing,
     * simply set $this->model with a proper PHP_UML_Metamodel_Superstructure object
     *  
     * @param float  $version  XMI Version For XMI 1.x, any value below 2.
     *                                     For XMI 2.x, any value above or equal to 2.
     * @param string $encoding XML Encoding (iso-8859-1 or utf-8)
     */
    public function generateXMI($version = 2.1, $encoding = 'iso-8859-1')
    {
        if (empty($this->model)) {
            throw new PHP_UML_Exception('No model given');
        }

        $this->builder = PHP_UML_XMI_AbstractBuilder::factory($version, $encoding);
        $this->xmi     = $this->builder->getXmlHeader();
        $this->xmi    .= $this->builder->getXmiHeaderOpen();

        $_root      = &$this->model->packages;
        $this->xmi .= $this->builder->getModelOpen($_root);
        $this->xmi .= $this->builder->getNamespaceOpen();
 
        if ($this->logicalView) {
            $this->addLogicalView($_root);
        }

        if ($this->componentView) {
            $this->addComponentView($_root);
        }

        if ($this->deploymentView) {
            $this->addDeploymentView($this->model->deploymentPackages);
        }

        $this->xmi .= $this->builder->getNamespaceClose();
        $this->xmi .= $this->builder->getModelClose();

        if ($this->docblocks) {    // = XML metadata only for the moment
            $this->addStereotypeInstances(PHP_UML_PHP_Parser::PHP_PROFILE_NAME);
        }

        $this->xmi .= $this->builder->getXmiHeaderClose();
        
        if (strtolower($encoding)=='utf-8') {
            $this->xmi = utf8_encode($this->xmi);
        }

    }
    
    /**
     * Saves the previously generated XMI to a file
     * You must run that method to get your XMI, or you can access the XMI property  
     *
     * @param string $outputFile Filename
     */
    public function saveXMI($outputFile)
    {        
        if ($ptr = fopen($outputFile, 'w+')) {
            fwrite($ptr, $this->getXMI());
            fclose($ptr);
        } else {
            throw new PHP_UML_Exception(
                'File '.$outputFile.' could not be created.'
            );
        }
    }
 
    /**
     * Generates the output data
     * 
     * The default format is XMI.
     * But PHP_UML comes with two additional formats:
     * - HTML (an API documentation, very similar to Javadoc)
     * - PHP code generation (a simple PHP code skeleton)
     * 
     * The templates are stored under /Output/<format_name>/
     * You are free to develop your owns.
     * Except for XMI (which is internally generated), the generation is done
     * through a complex XSLT transformation, applied to XMI data.
     * PHP_UML will look for a "main.xsl" file, in the folder of the desired format.
     *
     * Since the generation rely on XMI data, an XMI generation will be
     * performed in all cases.
     * 
     * @param string $format    Desired format ("xmi", "html", "php")
     * @param string $outputDir Output directory
     */
    public function export($format='xmi', $outputDir='.')
    {
        $format = strtolower($format);
        if ($format=='xmi') {
            if ($outputDir=='')
                return $this->getXMI();
            else {
                if (strtolower(substr($outputDir, -4))=='.xmi')
                    self::saveXMI($outputDir);
                else
                    self::saveXMI($outputDir.DIRECTORY_SEPARATOR.$this->model->packages->name.'.xmi');
            }
        } else {
            PHP_UML_Output_Exporter::generateFromFormat($outputDir, $format, $this->xmi);
        }
    }
 
    /**
     * Public accessor to the XMI code
     *
     * @return string The XMI code
     */
    public function getXMI()
    {
        return $this->xmi;
    }  

    /**
     * Inserts the logical view of the model
     *
     * @param PHP_UML_Metamodel_Package $package Package
     */
    private function addLogicalView(PHP_UML_Metamodel_Package $package)
    {
        $this->xmi .= $this->builder->getStereotypes();

        $this->xmi .= $this->builder->getOwnedTypes($package);
        foreach ($package->nestedPackage as $pkg)
            $this->xmi .= $this->builder->getAllPackages($pkg, false);
    }
    
    /**
     * Inserts a component view of the logical system
     *
     * @param PHP_UML_Metamodel_Package $package Root package to browse into
     */
    private function addComponentView(PHP_UML_Metamodel_Package $package)
    {
        $this->xmi .= $this->builder->getAllComponents($package);
    }

    /**
     * Inserts a deployment view of the scanned file system, through artifacts.
     * A file is viewed as an artifact (artifacts exist from UML 1.4)
     * Filesystem's folders are treated as packages.
     * TODO: use a package-tree, like with logical packages 
     *
     * @param PHP_UML_Metamodel_Package $package The root deployment package
     */
    private function addDeploymentView(PHP_UML_Metamodel_Package $package)
    {
        $this->xmi .= $this->builder->getAllPackages($package);
    }

    /**
     * Adds the instances of stereotypes
     * At the current time, there are only XML metadata, not real UML stereotypes
     *
     */
    private function addStereotypeInstances()
    {
        foreach ($this->model->stereotypes as $s) {
            $this->xmi .= $this->builder->getStereotypeInstance($s);
        }
    }

    /**
     * Autoloader
     *
     * @param string $class Class name
     */
    static function autoload($class)
    {
        if (substr($class, 0, 7)=='PHP_UML') {
            $path = 'UML'.str_replace('_', '/', substr($class, 7).'.php');
            require $path;
        }
    }
}
?>