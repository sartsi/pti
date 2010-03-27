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
 * This class is a set of various data/switches that can be used by the
 * implementations of the exporter
 * 
 * @category   PHP
 * @package    PHP_UML
 * @subpackage Output
 * @author     Baptiste Autin <ohlesbeauxjours@yahoo.fr> 
 * @license    http://www.gnu.org/licenses/lgpl.html LGPL License 3
 */
abstract class PHP_UML_Output_Exporter
{
    /**
     * A superstructure (= a PHP_UML metamodel)
     * @var PHP_UML_Metamodel_Superstructure
     */
    public $structure;

    /**
     * Exportation format (eg. xmi)
     * @var string
     */
    public $format;
    
    /**
     * If true, all the stereotypes related to the UML model (currently, these are
     * data retrieved from the PHP docblocks) will be added to the XMI file
     * @var boolean
     */
    public $addStereotypes;
}
?>
