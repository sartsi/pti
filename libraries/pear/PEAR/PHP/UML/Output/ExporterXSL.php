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
 * @version  SVN: $Revision: 138 $
 * @link     http://pear.php.net/package/PHP_UML
 * @since    $Date: 2009-12-13 04:23:11 +0100 (dim., 13 déc. 2009) $
 */
 
/**
 * An exportation class, that relies on XSL transformations
 * It expects to receive the XMI data, and applies XSL transformations on it
 * The XSL files must be stored in a subfolder named according to the desired format 
 * 
 * @category   PHP
 * @package    PHP_UML
 * @subpackage Output
 * @author     Baptiste Autin <ohlesbeauxjours@yahoo.fr>
 * @license    http://www.gnu.org/licenses/lgpl.html LGPL License 3
 * @link       http://pear.php.net/package/PHP_UML
 */
class PHP_UML_Output_ExporterXSL extends PHP_UML_Output_Xmi_Exporter
{
    const APP_NAME         = 'PHP_UML';
    const RESOURCES_FOLDER = 'resources';
    const STARTING_TPL     = 'main.xsl';
   
    /**
     * Generates output data by applying a transformation on a given XMI file
     *
     * @param string $outputDir Output directory
     * @param string $xslFile   XSL file (template file)
     * @param string $xmlFile   XMI file  
     */
    static public function generateFromFile($outputDir, $xslFile, $xmlFile)
    {
        if (is_file($xmlFile) && is_readable($xmlFile)) {
            $xmlDom = new DomDocument;
            $xmlDom->load($xmlFile);
            self::transform($outputDir, $xslFile, $xmlDom);
        } else {
            throw new PHP_UML_Exception(
                'Could not read file '.$xmlFile
            );
        }
    }
    
    /**
     * Generates ouput data by applying a transformation on some provided XMI
     *
     * @param string $outputDir Output directory
     * @param string $xslFile   XSL file (template file)
     * @param string $xmlData   The XMI data
     */
    static public function generateFromXml($outputDir, $xslFile, $xmlData)
    {
        if(empty($xmlData))
            throw new PHP_UML_Exception(
                'No XMI data was available for transformation.'
            );
        $xmlDom = new DomDocument;
        $xmlDom->loadXML($xmlData);
        self::transform($outputDir, $xslFile, $xmlDom);
    }
    
    /**
     * Generates output data by applying a transformation on the XMI stored in the
     * property $xmi
     *
     * @param string $outputDir Output folder
     */
    public function generate($outputDir)
    {
        $xslFile = dirname(__FILE__).DIRECTORY_SEPARATOR.$this->format.
             DIRECTORY_SEPARATOR.self::STARTING_TPL;

        if (file_exists($xslFile))
            self::generateFromXml($outputDir, $xslFile, $this->xmi);
        else {
            throw new PHP_UML_Exception(
                'Could not find the XSL template file. It must be named '.
                self::STARTING_TPL.', under Format/YourTemplate/'
            );
        }
    }
    
    /**
     * Generates output data by applying a transformation on a Dom Document
     *
     * @param string      $outputDir Output folder
     * @param string      $xslFile   XSL file (template file)
     * @param DomDocument $xmlDom    XMI data (Dom)
     *
     * @return string (possible) messages raised during XSLT
     */
    static private function transform($outputDir, $xslFile, DomDocument $xmlDom)
    {
        clearstatcache();

        $userCurrentDir = getcwd();    // we memorize the current working dir
        chdir(dirname(__FILE__));      // ... swap to the /Output dir  
        if ($xslFile=='')
            $xslFile = 'html/'.self::STARTING_TPL;
        
        if (!(file_exists($xslFile) && is_readable($xslFile)))
            throw new PHP_UML_Exception(
                'Could not read file '.$xslFile
            );    
        $xslDom = new DomDocument;
        $xslDom->load($xslFile);       // ... load the XSLT starting file
        
        chdir($userCurrentDir);        // ... and change back to the user dir
 
        // so that we can check the output dir (counted from the user dir)
        if ($outputDir=='')
            throw new PHP_UML_Exception(
                'No output directory was given.'
            );
        if (!file_exists($outputDir))
            throw new PHP_UML_Exception(
                'Directory '.$outputDir.' does not exist.'
            );
        chdir($outputDir);

        self::copyResources(dirname($xslFile));

        $xslProc = new XSLTProcessor;
        $xslProc->registerPHPFunctions();
        $xslProc->importStylesheet($xslDom);

        $xslProc->setParameter('', 'phpSaveToFile', __CLASS__.'::saveToFile');
        $xslProc->setParameter('', 'phpCreateFolder', __CLASS__.'::createFolder');
        $xslProc->setParameter('', 'appName', self::APP_NAME);
        $xslProc->setParameter('', 'genDate', date('r'));

        return $xslProc->transformToXML($xmlDom);
    }

    const XMLNS_UML1 = 'http://www.omg.org/spec/UML/1.4';

    /**
     * XMI converter
     * 
     * @param string $xmi XMI (in version 1)
     * 
     * @return string XMI (in version 2)
     */
    static public function convertTo2($xmi)
    {
        return self::simpleTransform('xmi1to2.xsl', $xmi);
    }
    
    /**
     * Applies a simple transformation on XML data
     * Used by the UML 1->2 conversion
     *
     * @param string $xsl XSL file
     * @param string $xmi XML data
     *
     * @return string XML
     */
    static private function simpleTransform($xsl, $xmi)
    {   
        //we must set the xmlns:UML to the same value as the XSLT stylesheet
        //(otherwise transfomations don't work)
        $xmi = preg_replace('/xmlns:(UML)\s*=\s*["\'](.*)["\']/i', 'xmlns:$1="'.self::XMLNS_UML1.'"', $xmi, 1);

        $xslDom = new DomDocument;
        $xslDom->load(dirname(__FILE__).DIRECTORY_SEPARATOR.$xsl);

        $xmlDom = new DomDocument;
        $xmlDom->loadXML($xmi);
 
        /* $xmiTag = &$xmlDom->getElementsByTagName('XMI')->item(0);
        if ($xmiTag->getAttribute('xmlns:UML') != '') {
            $xmlDom->getElementsByTagName('XMI')->item(0)->setAttribute('verified', 'http://www.omg.org/spec/UML/1.4');
        } */

        $xslProc = new XSLTProcessor;
        $xslProc->importStylesheet($xslDom);

        $xslProc->setParameter('', 'appName', self::APP_NAME);

        return $xslProc->transformToXML($xmlDom);
    }
    
    /**
     * Copy the "resources" folder
     *
     * @param string $path Path to the folder that contains the XSL templates
     */
    static private function copyResources($path)
    {
        $dir = $path.DIRECTORY_SEPARATOR.self::RESOURCES_FOLDER;

        if (file_exists($dir)) {
            $iterator = new DirectoryIterator($dir);
            foreach ($iterator as $file) {
                if($file->isFile())
                    copy($file->getPathname(), $file->getFilename());
            }
        }
    }


    /**
     * Creates a folder. Callback function for the XSL templates.
     *
     * @param string $path Folder name
     */
    static public function createFolder($path)
    {   
        if (substr(getcwd(), -1)==DIRECTORY_SEPARATOR)
            $k = getcwd().utf8_decode($path);
            else
            $k = getcwd().DIRECTORY_SEPARATOR.utf8_decode($path);
        if (!file_exists($k)) {
            mkdir($k);
        }
        chdir($k);
    }
   
    /**
     * Saves a content to a file. Callback function for the XSL templates.
     *
     * @param string $name    File name
     * @param mixed  $content Content (can be either a string, or a node-set)
     */
    static public function saveToFile($name, $content)
    {
        $file = fopen(utf8_decode($name), 'w');
 
        if (is_string($content)) {
            fwrite($file, $content);
        } else {
            $dom  = new DomDocument();
            $node = $dom->importNode($content[0], true);
            $dom->appendChild($node);
    
            fwrite($file, $dom->saveHTML());
        }
        fclose($file);
    }
}

?>
