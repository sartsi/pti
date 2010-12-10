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
 * @version  SVN: $Revision: 148 $
 * @link     http://pear.php.net/package/PHP_UML
 * @since    $Date: 2010-04-18 17:46:57 +0200 (dim., 18 avr. 2010) $
 */

/**
 * This class generates the XMI from a UML model (PHP_UML_Metamodel)
 *
 * @category   PHP
 * @package    PHP_UML
 * @subpackage Output
 * @subpackage Xmi
 * @author     Baptiste Autin <ohlesbeauxjours@yahoo.fr> 
 * @license    http://www.gnu.org/licenses/lgpl.html LGPL License 3
 */
class PHP_UML_Output_Xmi_Exporter extends PHP_UML_Output_ExporterAPI
{
    public $xmi = '';
    public $addLogicalView = true;
    public $addDeploymentView = false;
    public $addComponentView = false;
    public $xmiVersion = 2;
    public $xmlEncoding = 'iso-8859-1';
    
    /**
     * Builder object
     * @var PHP_UML_Output_Xmi_Builder
     */
    public $builder;


    public function generate($outDir)
    {
        if (empty($this->xmi))
            $this->generateXMI();

        if ($outDir=='')
            return $this->getXMI();
        else {
            if (!is_dir($outDir))
                $this->saveXMI($outDir);
            else
                $this->saveXMI($outDir.DIRECTORY_SEPARATOR.$this->structure->packages->name.'.xmi');
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
     * Public read accessor to the XMI code
     *
     * @return string The XMI code
     */
    public function getXMI()
    {
        return $this->xmi;
    }

    /**
     * Public write accessor to the XMI code
     * 
     * @param string $str XMI code
     */
    public function setXMI($str)
    {
        $this->xmi = $str;
    }
    
    /**
     * Serialize the metamodel (contained in the property $structure) into XMI code
     *
     */
    public function generateXMI()
    {
        if (empty($this->structure)) {
            throw new PHP_UML_Exception('No model given');
        }

        $this->builder = $this->getXmiFactory();

        $this->xmi  = $this->builder->getXmlHeader();
        $this->xmi .= $this->builder->getXmiHeaderOpen();

        $_root = $this->structure->packages;

        $this->xmi .= $this->builder->getModelOpen($_root);
        $this->xmi .= $this->builder->getNamespaceOpen();
 
        if ($this->addLogicalView) {
            $this->addLogicalView($_root);
        }

        if ($this->addComponentView) {
            $this->addComponentView($_root);
        }

        if ($this->addDeploymentView) {
            $this->addDeploymentView($this->structure->deploymentPackages);
        }

        $this->xmi .= $this->builder->getNamespaceClose();
        $this->xmi .= $this->builder->getModelClose();

        if ($this->addStereotypes) {    // = XML metadata only for the moment
            $this->addStereotypeInstances(PHP_UML_Metamodel_Superstructure::PHP_PROFILE_NAME);
        }

        $this->xmi .= $this->builder->getXmiHeaderClose();

        if (strcasecmp($this->xmlEncoding, 'utf-8')==0) {
            $this->xmi = utf8_encode($this->xmi);
        }
    }
    
    /**
     * Getter for the XMI factory
     *
     */
    protected function getXmiFactory() {

        return PHP_UML_Output_Xmi_AbstractBuilder::factory($this->xmiVersion, $this->xmlEncoding);	
    }
    
    /**
     * Inserts the logical view of the model
     *
     * @param PHP_UML_Metamodel_Package $package Package
     */
    private function addLogicalView(PHP_UML_Metamodel_Package $package)
    {
        $this->xmi .= $this->builder->getStereotypes();

        $this->xmi .= $this->builder->getOwnedElements($package);
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
        foreach ($this->structure->stereotypes as $s) {
            $this->xmi .= $this->builder->getStereotypeInstance($s);
        }
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
            $this->xmi = PHP_UML_Output_ExporterXSL::convertTo2($this->xmi);
        }
    }
}
?>
