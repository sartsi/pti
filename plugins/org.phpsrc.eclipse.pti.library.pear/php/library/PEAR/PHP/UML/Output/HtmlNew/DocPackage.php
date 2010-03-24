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
 * Implementation of the HTML renderer for a Package
 * 
 * @category   PHP
 * @package    PHP_UML
 * @subpackage Output
 * @subpackage HtmlNew
 * @author     Baptiste Autin <ohlesbeauxjours@yahoo.fr> 
 * @license    http://www.gnu.org/licenses/lgpl.html LGPL License 3
 */
class PHP_UML_Output_HtmlNew_DocPackage extends PHP_UML_Output_HtmlNew_Documentor
{
    /**
     * Generates and saves the HTML code for a package
     * 
     * @param PHP_UML_Metamodel_Package $p Starting package
     */
    public function createHtml($p)
    {
        if ($this->ctx->dir=='') {
            $fullName = $p->name;
        } else {
            $fullName = rtrim($this->getAbsPath($p, self::T_NAMESPACE), self::T_NAMESPACE);
        }

        $nav = $this->getNavigationBlock();

        $tit = $this->getTitleBlock($fullName);

        $str  = $this->getDescriptionBlock($p);
        $str .= $this->getPropertyBlock($p);
        $str .= $this->getFunctionBlock($p);

        $str = $this->replaceInTpl($str, $nav, $tit, $fullName);

        $this->save(self::PACKAGE_FILENAME, $str);
    }

    private function getDescriptionBlock($p)
    {
        $str = '';

        if (!is_null($p->description)) {
            $str .= '<div class="descriptionPkg"><p><ul class="single">'.$this->getTagsAsList($p->description).'</ul></p></div>';
        }

        if (count($p->nestedPackage)>0) {
            $str .= '<h2>Packages</h2>';
            $str .= '<ul class="summary">';
            foreach ($p->nestedPackage as $np) {
                $str .= '<li>';
                $str .= '<a href="'.$np->name.'/'.self::PACKAGE_FILENAME.'.'.self::FILE_EXT.'" class="package" target="main">'.$np->name.'</a>';
                $str .= '</li>';
            }
            $str .= '</ul>';
        }

        $display = false;
        $tmp     = '<h2>Classes</h2>';
        $tmp    .= '<ul class="summary">';
        foreach ($this->ctx->classes as $o) {
            if ($this->ctx->dir!='' || !in_array($o->name, $this->hiddenClasses)) {
                $display = true;
                $tmp    .= '<li>'.
                    '<a href="'.self::getObjPrefix($o).$o->name.'.'.self::FILE_EXT.'" class="'.self::getObjStyle($o).'" target="main">'.$o->name.'</a>'.
                    '</li>';
            }
        }
        $tmp .= '</ul>';
        if ($display)
            $str .= $tmp;

        $display = false;
        $tmp     = '<h2>Interfaces</h2>';
        $tmp    .= '<ul class="summary">';
        foreach ($this->ctx->interfaces as $o) {
            if ($this->ctx->dir!='' || !in_array($o->name, $this->hiddenInterfaces)) {
                $display = true;
                $tmp    .= '<li>'.
                    '<a href="'.self::getObjPrefix($o).$o->name.'.'.self::FILE_EXT.'" class="'.self::getObjStyle($o).'" target="main">'.$o->name.'</a>'.
                    '</li>';
            }
        }
        $tmp .= '</ul>';
        if ($display)
            $str .= $tmp;

        return $str;
    }

    /**
     * Return the HTML code for the navigation bar
     * 
     * @return string
     */
    private function getNavigationBlock()
    {
        $str = $this->getCommonLinks();
        if (!empty($this->ctx->rpt))
            $str .= $this->getNavigParentPackage('../');
        return $str;
    }

    private function getTitleBlock($name)
    {
        return '<h1 class="'.$this->getStyleName().'">'.$name.'</h1>';
    }

    private function getStyleName()
    {
        return $this->ctx->dir=='' ? 'model' : 'package';
    }

    protected function getTypeName()
    {
        return $this->getStyleName();
    }

    protected function getPropertyStyle($visibility)
    {
        return 'property';
    }

    protected function getFunctionStyle($visibility)
    {
        return 'method';
    }
}
?>
