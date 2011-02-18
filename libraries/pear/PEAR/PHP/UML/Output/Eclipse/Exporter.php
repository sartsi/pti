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
 * @version  SVN: $Revision: 140 $
 * @link     http://pear.php.net/package/PHP_UML
 * @since    $Date: 2009-12-14 00:58:24 +0100 (lun., 14 déc. 2009) $
 */

/**
 * This class generates a specific XMI file that can be imported into Eclipse
 *
 * @category   PHP
 * @package    PHP_UML
 * @subpackage Output
 * @subpackage Xmi
 * @author     Baptiste Autin <ohlesbeauxjours@yahoo.fr> 
 * @license    http://www.gnu.org/licenses/lgpl.html LGPL License 3
 */
class PHP_UML_Output_Eclipse_Exporter extends PHP_UML_Output_Xmi_Exporter
{
    public $addStereotypes = false;

    /**
     * Getter for the XMI factory
     *
     */
    protected function getXmiFactory() {

        return PHP_UML_Output_Eclipse_AbstractBuilder::factory($this->xmiVersion, $this->xmlEncoding);	
    }
}
?>
