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
 * @version  SVN: $Revision: 138 $
 * @link     http://pear.php.net/package/PHP_UML
 * @link     http://www.baptisteautin.com/projects/PHP_UML/
 * @since    $Date: 2009-12-13 04:23:11 +0100 (dim., 13 déc. 2009) $
 */

require_once 'PEAR/Exception.php';

spl_autoload_register(array('PHP_UML', 'autoload'));


/**
 * The main class to use, through its methods:
 * - setInput(), parse(), parseFile() and/or parseDirectory()
 * - generateXMI()
 * - saveXMI() (deprecated)
 * - export()
 * 
 * For example:
 * <code>
 * $t = new PHP_UML();
 * $t->setInput('PHP_UML/');
 * $t->export('xmi', '/home/wwww/');
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
     * If true, only classes and namespaces are retrieved. If false, procedural
     * functions and constants are also included
     */
    public $pureObject = false;
    
    /**
     * If true, the empty namespaces (inc. no classes nor interfaces) are ignored
     * @var boolean
     */
    public $removeEmptyNamespaces = true;

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
     * Base XMI Exporter object
     * @var PHP_UML_Output_Xmi_Exporter
     */
    private $xmiExporter;
    
    
    /**
     * Constructor.
     * We create an Xmi_Exporter object to store the XMI that may be generated
     *
     */
    public function __construct()
    {
        $this->model       = new PHP_UML_Metamodel_Superstructure;
        $this->xmiExporter = new PHP_UML_Output_Xmi_Exporter;
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
     * 
     * @deprecated Use ->xmiExporter->readXMIFile($filepath) instead
     */
    public function readXMIFile($filepath)
    {
        $this->xmiExporter->readXMIFile($filepath);
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
            if (is_file($path)) {
                $this->files[] = $path;
            }
            elseif (is_dir($path))
                $this->directories[] = $path;
            else
                throw new PHP_UML_Exception($path.': unknown file or folder');
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
     * @param string $p Pattern
     * 
     * @see PHP_UML_FilePatternFilterIterator#accept()
     */
    private static function cleanPattern($p)
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
        $this->visited = array();    // we initialize the stack of visited files
        $this->model->initModel($modelName);

        $fileScanner = new PHP_UML_FileScannerImpl();

        $fileScanner->files          = $this->files;
        $fileScanner->directories    = $this->directories;
        $fileScanner->matchPatterns  = $this->matchPatterns;
        $fileScanner->ignorePatterns = $this->ignorePatterns;

        $fileScanner->parser = new PHP_UML_Input_PHP_Parser($this->model, $this->docblocks, $this->dollar, !$this->showInternal, $this->onlyApi, $this->pureObject);
        $fileScanner->scan();

        $this->model->finalizeAll($this->removeEmptyNamespaces);
    }


    /**
     * Runs the XMI generator on the PHP model stored in $this->model.
     * After the PHP parsing, parseDirectory() (or parseFile()) will have filled
     * $this->model for you. Note that it is now unnecessary to call this method if
     * you intent to call the method export('xmi') later (the generation will be
     * done implicitely, in version 2)
     * If you want to use the XMI generator without doing any prior PHP parsing,
     * simply set $this->model to a proper PHP_UML_Metamodel_Superstructure object
     *  
     * @param float  $version  XMI Version For XMI 1.x, any value below 2.
     *                                     For XMI 2.x, any value above or equal to 2.
     * @param string $encoding XML Encoding (iso-8859-1 or utf-8)
     */
    public function generateXMI($version = 2.1, $encoding = 'iso-8859-1')
    {
        $this->xmiExporter->xmlEncoding = $encoding;
        $this->xmiExporter->xmiVersion  = $version;
        $this->bindXmiExporterData(); 
        $this->xmiExporter->generateXMI();
    }
    
    /**
     * Saves the previously generated XMI to a file
     * You must run that method to get your XMI, or you can access the XMI property  
     * 
     * @param string $outputFile Filename
     * 
     * @deprecated Use $this->xmiExporter->saveXMI() instead
     */
    public function saveXMI($outputFile)
    {
        $this->xmiExporter->saveXMI($outputFile);
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
     * @param string $format    Desired format ("xmi", "html", "php"...)
     * @param string $outputDir Output directory
     */
    public function export($format='xmi', $outputDir='.')
    {
        if (empty($outputDir))
            throw new PHP_UML_Exception('No output folder given.');

        $format = strtolower($format);
        switch($format) {
        case 'xmi':
            $this->xmiExporter->format = $format;
            $this->bindXmiExporterData();
            return $this->xmiExporter->generate($outputDir);
            break;
        case 'htmlnew':
            if (empty($this->model->packages)) {
                throw new PHP_UML_Exception('No model given.');
            }
            $format = 'HtmlNew';
            $e      = PHP_UML_Output_ExporterAPI::getExporterObject($format);

            $e->structure = $this->model;
            $e->format    = $format;
            return $e->generate($outputDir);
            break;
        case 'php':
        case 'html':
            if (empty($this->xmiExporter->xmi)) {
                $this->generateXMI();    // since these formats rely on a XSLT of XMI
            }
            $e         = new PHP_UML_Output_ExporterXSL;
            $e->format = $format;
            $e->xmi    = $this->xmiExporter->xmi;
            return $e->generate($outputDir);
            break;
        default:
            throw new PHP_UML_Exception('Unknown export format "'.$format.'"');
        }
    }

    /**
     * Some data required by xmiExporter bas to be copied from the one contained
     * in PHP_UML before any method is called on xmiExporter
     * This method copies the necessary information
     *
     */
    private function bindXmiExporterData()
    {
        $this->xmiExporter->structure         = $this->model;
        $this->xmiExporter->addDeploymentView = $this->deploymentView;
        $this->xmiExporter->addLogicalView    = $this->logicalView;
        $this->xmiExporter->addComponentView  = $this->componentView;
        $this->xmiExporter->addStereotypes    = $this->docblocks;   
    }

    /**
     * Public accessor to the XMI code
     *
     * @deprecated
     * @return string The XMI code
     */
    public function getXMI()
    {
        return $this->xmiExporter->getXMI();
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