<?php
/**
 * PHP Parser and UML/XMI generator. Reverse-engineering tool.
 *
 * A package to scan PHP files and directories, and get an UML/XMI representation
 * of the parsed classes/packages.
 *
 * PHP version 5
 *
 * @category PHP
 * @package  PHP_UML
 * @author   Baptiste Autin <ohlesbeauxjours@yahoo.fr>
 * @license  http://www.gnu.org/licenses/lgpl.html LGPL License 3
 * @version  SVN: $Revision: 105 $
 * @link     http://pear.php.net/package/PHP_UML
 * @link     http://www.baptisteautin.com/projects/PHP_UML/
 * @since    $Date: 2009-06-04 19:48:27 +0200 (jeu., 04 juin 2009) $
 */


/**
 * The PHP_UML implementation of a FileScanner class.
 * 
 * @category PHP
 * @package  PHP_UML
 * @author   Baptiste Autin <ohlesbeauxjours@yahoo.fr>
 * @license  http://www.gnu.org/licenses/lgpl.html LGPL License 3
 * 
 */
class PHP_UML_FileScannerImpl extends PHP_UML_FileScanner
{

    /**
     * Reference to a PHP_UML_PHP_Parser object
     *
     * @var PHP_UML_PHP_Parser
     */
    public $parser;

    /**
     * Constructor
     *
     */
    public function __construct()
    {
    }

    /**
     * Implementation of tickFile() : we parse every file met.
     * 
     * @param string $basedir  Directory path
     * @param string $filename File name
     *
     * @see PHP_UML/UML/FileScanner#tickFile()
     */
    public function tickFile($basedir, $filename)
    {
        $this->parser->parse($basedir, $filename);
    }

    public function raiseUnknownFolderException($basedir)
    {
        throw new PHP_UML_Exception($basedir.': unknown folder.');
    }

}
?>
