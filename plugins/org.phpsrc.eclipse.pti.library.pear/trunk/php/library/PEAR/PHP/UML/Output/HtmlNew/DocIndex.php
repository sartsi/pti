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
 * Implementation of the HTML renderer for the Index page
 *
 * @category   PHP
 * @package    PHP_UML
 * @subpackage Output
 * @subpackage HtmlNew
 * @author     Baptiste Autin <ohlesbeauxjours@yahoo.fr> 
 * @license    http://www.gnu.org/licenses/lgpl.html LGPL License 3
 */
class PHP_UML_Output_HtmlNew_DocIndex extends PHP_UML_Output_HtmlNew_DocMenu
{
    /**
     * Temporary structure to store all the elements (in order to sort them)
     * @var array First index: the element, Second index: filepath to detail page, Third index: true only for operations/attributes of a Package (procedural code)
     */
    private $elements = array();

    /**
     * Generates and saves the HTML code for the Index page
     * 
     * @param PHP_UML_Metamodel_Package $p
     */
    public function createHtml($p)
    {
        $tpl = self::getTemplate(self::INDEX_FILENAME.'.'.self::FILE_EXT);

        $nav = $this->getNavigationBlock();
        
        $str  = '<ul id="MainList" class="indexAll">';
        $str .= $this->getContent($p);
        $str .= '</ul>';

        $tmp = str_replace('#NAVIG', $nav, $tpl);
        $str = str_replace('#INDEX', $str, $tmp);

        $this->save(self::INDEX_FILENAME, $str);
    }
    
    /**
     * Return the HTML code for an Index page
     * 
     * @param PHP_UML_Metamodel_Package $p Starting package
     * 
     * @return string HTML code
     */
    private function getContent(PHP_UML_Metamodel_Package $p)
    {
        $this->readPackage($p);
        $str = '';
        usort($this->elements, array('self', 'compareElt'));
        foreach ($this->elements as $x) {
            list($elt, $path, $pkgElt) = $x;
            switch(get_class($elt)) {
            case self::META_CLASS:
            case self::META_INTERFACE:
                $str .= $this->getClassifierItem($elt, $path, 0);
                break;
            case self::META_OPERATION:
                if ($pkgElt)
                    $str .= $this->getPackageOperationItem($elt, $path);
                else
                    $str .= $this->getOperationItem($elt, $path);
                break;
            case self::META_PROPERTY:
                if ($pkgElt)
                    $str .= $this->getPackagePropertyItem($elt, $path);
                else
                    $str .= $this->getPropertyItem($elt, $path);
                break;
            }
        }
        return $str;
    }

    static private function compareElt($a, $b)
    {
        return strcasecmp($a[0]->name, $b[0]->name);
    }

    /**
     * Set the elements[] property with the API data of a package
     * 
     * @param PHP_UML_Metamodel_Package $p     Package
     * @param string                    $path  Relative path to the current package
     * @param string                    $level Recursion level
     * 
     * @return void
     */
    private function readPackage(PHP_UML_Metamodel_Package $p, $path='', $level=0)
    {
        foreach ($p->nestedPackage as $np) {
            $npDir = $path.$np->name;
            $this->readPackage($np, $npDir.'/', $level+1);
        }
        foreach ($p->ownedType as $c) {
            $this->elements[] = array($c, $path, false);
            foreach ($c->ownedOperation as $o) {
                $this->elements[] = array($o, $path, false);
            }
            if (isset($c->ownedAttribute)) {
                foreach ($c->ownedAttribute as $a) {
                    $this->elements[] = array($a, $path, false);
                }
            }
        }
        foreach ($p->ownedOperation as $o) {
            $this->elements[] = array($o, $path, true);
        }
        foreach ($p->ownedAttribute as $a) {
            $this->elements[] = array($a, $path, true);
        }
    }

    /**
     * Return the HTML code for the navigation bar
     * 
     * @return string
     */
    private function getNavigationBlock()
    {
        $str  = '<ul class="navig">';
        $str .= $this->getCommonLinks();
        $str .= '</ul>';
        return $str;
    }
    
    protected function getPropertyItem(PHP_UML_Metamodel_Property $p, $path)
    {
        return '<li>'.
            '<a href="'.$path.self::getObjPrefix($p->class).$p->class->name.'.'.self::FILE_EXT.'#'.$p->name.
            '" class="'.$this->getPropertyStyle($p->visibility).'" target="main">'.$p->name.'</a>'.
            '</li>';
    }

    protected function getOperationItem(PHP_UML_Metamodel_Operation $o, $path)
    {
        return '<li>'.
          '<a href="'.$path.self::getObjPrefix($o->class).$o->class->name.'.'.self::FILE_EXT.'#f'.$o->id.
          '" class="'.$this->getFunctionStyle($o->visibility).'" target="main">'.$o->name.$this->getParameterList($o).'</a>'.
          '</li>';
    }
}
?>
