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
 * @version  SVN: $Revision: 136 $
 * @link     http://pear.php.net/package/PHP_UML
 * @since    $Date: 2009-12-10 01:35:58 +0100 (jeu., 10 déc. 2009) $
 */

/**
 * This is the exportation class relying on the API (= on the full hierarchy of
 * metaclasses stored in the model). Note that another way to export a model would
 * be to use ExporterXSL, which is based on an XSL transformation of XMI. 
 * A class implementing ExporterAPI must reside in a subfolder containing a class
 * named PHP_UML_<name of the output format>_Exporter. This class must also have a
 * public method "generate", which is used to start the serialization process.
 *
 * @category   PHP
 * @package    PHP_UML
 * @subpackage Output
 * @author     Baptiste Autin <ohlesbeauxjours@yahoo.fr> 
 * @license    http://www.gnu.org/licenses/lgpl.html LGPL License 3
 */
abstract class PHP_UML_Output_ExporterAPI extends PHP_UML_Output_Exporter
{
    /**
     * Factory method to retrieve an implementation of an exporter
     * 
     * @param string $format Name of the exportation format
     * 
     * @return PHP_UML_Output_ExporterAPI
     */
    static function getExporterObject($format)
    {
        $className = 'PHP_UML_Output_'.ucfirst($format).'_Exporter';
        return new $className();
    }

    /**
     * Starts the generation of the output format, and all actions related
     * 
     * @param string $outDir Directory where the resulting output must be stored
     * 
     * @return string Warnings/Errors
     */
    abstract public function generate($outDir);
}
?>
