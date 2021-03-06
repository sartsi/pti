<?php
/**
 * Directory container.
 *
 * @package  bovigo_vfs
 * @version  $Id: vfsStreamDirectory.php 211 2010-10-06 16:33:05Z google@frankkleine.de $
 */
/**
 * @ignore
 */
require_once dirname(__FILE__) . '/vfsStreamAbstractContent.php';
require_once dirname(__FILE__) . '/vfsStreamContainer.php';
require_once dirname(__FILE__) . '/vfsStreamContainerIterator.php';
require_once dirname(__FILE__) . '/vfsStreamException.php';
/**
 * Directory container.
 *
 * @package  bovigo_vfs
 */
class vfsStreamDirectory extends vfsStreamAbstractContent implements vfsStreamContainer
{
    /**
     * list of directory children
     *
     * @var  array<string,vfsStreamContent>
     */
    protected $children = array();

    /**
     * constructor
     *
     * @param   string  $name
     * @param   int     $permissions  optional
     * @throws  vfsStreamException
     */
    public function __construct($name, $permissions = null)
    {
        if (strstr($name, '/') !== false) {
            throw new vfsStreamException('Directory name can not contain /.');
        }

        $this->type = vfsStreamContent::TYPE_DIR;
        parent::__construct($name, $permissions);
    }

    /**
     * returns default permissions for concrete implementation
     *
     * @return  int
     * @since   0.8.0
     */
    protected function getDefaultPermissions()
    {
        return 0777;
    }

    /**
     * returns size of directory
     *
     * The size of a directory is always 0 bytes. To calculate the summarized
     * size of all children in the directory use sizeSummarized().
     *
     * @return  int
     */
    public function size()
    {
        return 0;
    }

    /**
     * returns summarized size of directory and its children
     *
     * @return  int
     */
    public function sizeSummarized()
    {
        $size = 0;
        foreach ($this->children as $child) {
            if ($child->getType() === vfsStreamContent::TYPE_DIR) {
                $size += $child->sizeSummarized();
            } else {
                $size += $child->size();
            }
        }
        
        return $size;
    }

    /**
     * renames the content
     *
     * @param   string  $newName
     * @throws  vfsStreamException
     */
    public function rename($newName)
    {
        if (strstr($newName, '/') !== false) {
            throw new vfsStreamException('Directory name can not contain /.');
        }
        
        parent::rename($newName);
    }

    /**
     * adds child to the directory
     *
     * @param  vfsStreamContent  $child
     */
    public function addChild(vfsStreamContent $child)
    {
        $this->children[$child->getName()] = $child;
    }

    /**
     * removes child from the directory
     *
     * @param   string  $name
     * @return  bool
     */
    public function removeChild($name)
    {
        foreach ($this->children as $key => $child) {
            if ($child->appliesTo($name) === true) {
                unset($this->children[$key]);
                return true;
            }
        }
        
        return false;
    }

    /**
     * checks whether the container contains a child with the given name
     *
     * @param   string  $name
     * @return  bool
     */
    public function hasChild($name)
    {
        return ($this->getChild($name) !== null);
    }

    /**
     * returns the child with the given name
     *
     * @param   string  $name
     * @return  vfsStreamContent
     */
    public function getChild($name)
    {
        $childName = $this->getRealChildName($name);
        foreach ($this->children as $child) {
            if ($child->getName() === $childName) {
                return $child;
            }
            
            if ($child->appliesTo($childName) === true && $child->hasChild($childName) === true) {
                return $child->getChild($childName);
            }
        }
        
        return null;
    }

    /**
     * helper method to detect the real child name
     *
     * @param   string  $name
     * @return  string
     */
    protected function getRealChildName($name)
    {
        if ($this->appliesTo($name) === true) {
            return self::getChildName($name, $this->name);
        }
        
        return $name;
    }

    /**
     * helper method to calculate the child name
     *
     * @param   string  $name
     * @param   string  $ownName
     * @return  string
     */
    protected static function getChildName($name, $ownName)
    {
        return substr($name, strlen($ownName) + 1);
    }

    /**
     * returns a list of children for this directory
     *
     * @return  array<vfsStreamContent>
     */
    public function getChildren()
    {
        return array_values($this->children);
    }

    /**
     * returns iterator for the children
     *
     * @return  vfsStreamContainerIterator
     */
    public function getIterator()
    {
        return new vfsStreamContainerIterator($this->children);
    }
}
?>