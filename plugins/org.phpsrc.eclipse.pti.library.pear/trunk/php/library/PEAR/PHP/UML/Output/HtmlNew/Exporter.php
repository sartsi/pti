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
 * This class generates a HTML website from a UML model (a PHP_UML_Metamodel)
 *
 * @category   PHP
 * @package    PHP_UML
 * @subpackage Output
 * @subpackage HtmlNew
 * @author     Baptiste Autin <ohlesbeauxjours@yahoo.fr> 
 * @license    http://www.gnu.org/licenses/lgpl.html LGPL License 3
 */
class PHP_UML_Output_HtmlNew_Exporter extends PHP_UML_Output_ExporterAPI
{
    const FILE_EXT          = 'htm';
    const RESOURCES_DIRNAME = '$resources';
    const TEMPLATES_DIRNAME = 'templates';
    const INDEX_FILENAME    = 'index.htm';
    const HELP_FILENAME     = 'help.htm';

    private $docClass;
    private $docInterface;
    private $docDatatype;
    private $docPackage;
    private $docMenu;
    private $docIndex;

    /**
     * Object storing the contextual data. This object is then passed to the business methods
     * @var PHP_UML_Output_HtmlNew_Context
     */
    private $ctx;

    public function generate($outDir)
    {
        $this->outDir = $outDir;
        if (substr($this->outDir, -1)!=DIRECTORY_SEPARATOR)
            $this->outDir .= DIRECTORY_SEPARATOR;
        if (!file_exists($this->outDir))
            throw new PHP_UML_Exception('Export directory ('.$this->outDir.') does not exist.');       

        $this->ctx = new PHP_UML_Output_HtmlNew_Context();

        $this->docClass     = new PHP_UML_Output_HtmlNew_DocClass($this->ctx);
        $this->docInterface = new PHP_UML_Output_HtmlNew_DocInterface($this->ctx);
        $this->docDatatype  = new PHP_UML_Output_HtmlNew_DocDatatype($this->ctx);
        $this->docPackage   = new PHP_UML_Output_HtmlNew_DocPackage($this->ctx);
        $this->docMenu      = new PHP_UML_Output_HtmlNew_DocMenu($this->ctx);
        $this->docIndex     = new PHP_UML_Output_HtmlNew_DocIndex($this->ctx);

        chdir($this->outDir);
        $this->createResources();
        $this->docMenu->createHtml($this->structure->packages);
        $this->docIndex->createHtml($this->structure->packages);
        // we analyse the inheritance/impl relations beforehand:
        $this->setAllSuperClassifiers($this->structure->packages);
        $this->createDetailFiles($this->structure->packages);
    }

    /**
     * Recurses into the packages, and generates the detailed file (one file per
     * class/interface/package)
     * 
     * @param PHP_UML_Metamodel_Package $pkg Starting package
     * @param string                    $dir Filepath leading to the current package
     * @param string                    $rpt Relative filepath to the top package
     */
    private function createDetailFiles(PHP_UML_Metamodel_Package $pkg, $dir='', $rpt='')
    {
        $this->ctx->classes    = array();
        $this->ctx->interfaces = array();
        $this->ctx->datatypes  = array();

        $this->ctx->dir = $dir;
        $this->ctx->rpt = $rpt;

        foreach ($pkg->ownedType as $type) {
            switch (get_class($type)) {
            case PHP_UML_Metamodel_Superstructure::META_INTERFACE:
                $this->ctx->interfaces[] = $type;
                break;
            case PHP_UML_Metamodel_Superstructure::META_DATATYPE:
                $this->ctx->datatypes[] = $type;
                break;
            default:
                $this->ctx->classes[] = $type;
            }
        }

        $this->docPackage->createHtml($pkg, $this->ctx);
        
        $nbc = count($this->ctx->classes);
        $nbi = count($this->ctx->interfaces);
        $nbd = count($this->ctx->datatypes);

        for ($i=0; $i<$nbc; $i++)
            $this->docClass->createHtml($i);
 
        for ($i=0; $i<$nbi; $i++)
            $this->docInterface->createHtml($i);

        for ($i=0; $i<$nbd; $i++)
            $this->docDatatype->createHtml($i);

        foreach ($pkg->nestedPackage as $np) {
            $npDir = $dir.$np->name;
            if (!file_exists($npDir))
                mkdir($npDir);
            $this->createDetailFiles($np, $npDir.DIRECTORY_SEPARATOR, '../'.$rpt);
        }
    }

    /**
     * Creates the needed various resources for the website (such as images folder)
     */
    private function createResources()
    {
        $dir     = $this->ctx->dir;
        $baseSrc = dirname(__FILE__).DIRECTORY_SEPARATOR;

        $index     = PHP_UML_Output_HtmlNew_Documentor::getTemplate(self::INDEX_FILENAME);
        $modelName = $this->structure->packages->name;
        $index     = str_replace('#MODELNAME', $modelName, $index);
        file_put_contents($dir.self::INDEX_FILENAME, $index);

        $src = dirname(__FILE__).DIRECTORY_SEPARATOR.self::TEMPLATES_DIRNAME.DIRECTORY_SEPARATOR.self::HELP_FILENAME;
        copy($src, $dir.self::HELP_FILENAME);

        $src  = $baseSrc.self::RESOURCES_DIRNAME;
        $dest = $dir.self::RESOURCES_DIRNAME;
        if (!file_exists($dest)) {
            mkdir($dest);
        }
        if (file_exists($src)) {
            $iterator = new DirectoryIterator($src);
            foreach ($iterator as $file) {
                if($file->isFile())
                    copy($file->getPathname(), $dest.DIRECTORY_SEPARATOR.$file->getFilename());
            }
        }
    }

    /**
     * Sets the allInherited/-ing arrays with all the classifiers that a given
     * classifier inherits from
     * 
     * @param PHP_UML_Metamodel_Classifier $s The initial reference classifier
     * @param PHP_UML_Metamodel_Classifier $t The current classifier to check
     */
    private function setAllInherited(PHP_UML_Metamodel_Classifier $s, PHP_UML_Metamodel_Classifier $t)
    {
        if (!empty($t->superClass) && is_object($t->superClass[0])) {
            $h = $t->superClass[0];
            $this->setAllInherited($s, $h);
            $this->ctx->allInherited[$s->id][]  = $h;
            $this->ctx->allInheriting[$h->id][] = $s;
        }
    }

    /**
     * Sets the allImplemented/-ing arrays with all the interfaces that a given
     * class implements (including those of the inherited classes)
     * 
     * @param PHP_UML_Metamodel_Class      $s The initial reference class
     * @param PHP_UML_Metamodel_Classifier $t The current classifier to check
     */
    private function setAllImplemented(PHP_UML_Metamodel_Class $s, PHP_UML_Metamodel_Classifier $t)
    {
        if (!empty($t->superClass) && is_object($t->superClass[0])) {
            $this->setAllImplemented($s, $t->superClass[0]);
        }
        if (isset($t->implements) && is_array($t->implements)) {
            foreach ($t->implements as $impl) {
                if (is_object($impl)) {
                    $this->setAllImplemented($s, $impl);
                    $this->ctx->allImplemented[$s->id][]     = $impl;
                    $this->ctx->allImplementing[$impl->id][] = $s; 
                }
            }
        }
    }

    /**
     * Recurses into all the packages to build a list of all the generalizations
     * and realizations between elements.
     * We normally do this before creating the detailed files.
     * 
     * @param PHP_UML_Metamodel_Package $pkg Starting package
     */
    private function setAllSuperClassifiers(PHP_UML_Metamodel_Package $pkg)
    {
        foreach ($pkg->ownedType as $type) {
            switch (get_class($type)) {
            case PHP_UML_Metamodel_Superstructure::META_CLASS:
                $this->setAllImplemented($type, $type);
            case PHP_UML_Metamodel_Superstructure::META_INTERFACE:
                $this->setAllInherited($type, $type);
            }
        }
        foreach ($pkg->nestedPackage as $np) {
            $this->setAllSuperClassifiers($np);
        }
    }
}
?>
