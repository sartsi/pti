<?php
/**
 * This file is part of PHP_Depend.
 *
 * PHP Version 5
 *
 * Copyright (c) 2008-2010, Manuel Pichler <mapi@pdepend.org>.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 *   * Neither the name of Manuel Pichler nor the names of his
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @category   QualityAssurance
 * @package    PHP_Depend
 * @subpackage Code
 * @author     Manuel Pichler <mapi@pdepend.org>
 * @copyright  2008-2010 Manuel Pichler. All rights reserved.
 * @license    http://www.opensource.org/licenses/bsd-license.php  BSD License
 * @version    SVN: $Id: Parameter.php 675 2009-03-05 07:40:28Z mapi $
 * @link       http://pdepend.org/
 */

require_once 'PHP/Depend/Code/ASTArrayType.php';
require_once 'PHP/Depend/Code/ASTVariableDeclarator.php';
require_once 'PHP/Depend/Code/ASTClassOrInterfaceReference.php';

require_once 'PHP/Depend/Code/NodeI.php';

/**
 * An instance of this class represents a function or method parameter within
 * the analyzed source code.
 *
 * <code>
 * <?php
 * class PHP_Depend_BuilderI
 * {
 *     public function buildNode($name, $line, PHP_Depend_Code_File $file) {
 *     }
 * }
 *
 * function parse(PHP_Depend_BuilderI $builder, $file) {
 * }
 * </code>
 *
 * @category   QualityAssurance
 * @package    PHP_Depend
 * @subpackage Code
 * @author     Manuel Pichler <mapi@pdepend.org>
 * @copyright  2008-2010 Manuel Pichler. All rights reserved.
 * @license    http://www.opensource.org/licenses/bsd-license.php  BSD License
 * @version    Release: 0.9.11
 * @link       http://pdepend.org/
 */
class PHP_Depend_Code_Parameter
       extends ReflectionParameter
    implements PHP_Depend_Code_NodeI
{
    /**
     * The unique identifier for this function.
     *
     * @var string $_uuid
     */
    private $_uuid = null;

    /**
     * The parent function or method instance.
     *
     * @var PHP_Depend_Code_AbstractCallable $_declaringFunction
     */
    private $_declaringFunction = null;

    /**
     * The parameter position.
     *
     * @var integer $_position
     */
    private $_position = 0;

    /**
     * Is this parameter optional or mandatory?
     *
     * @var boolean
     */
    private $_optional = false;

    /**
     * The wrapped formal parameter instance.
     *
     * @var PHP_Depend_Code_ASTFormalParameter $_formalParameter
     */
    private $_formalParameter = null;

    /**
     * The wrapped variable declarator instance.
     *
     * @var PHP_Depend_Code_ASTVariableDeclarator $_ASTVariableDeclarator
     */
    private $_ASTVariableDeclarator = null;

    /**
     * Constructs a new parameter instance for the given AST node.
     *
     * @param PHP_Depend_Code_ASTFormalParameter $formalParameter The wrapped AST
     *        parameter node.
     */
    public function __construct(PHP_Depend_Code_ASTFormalParameter $formalParameter)
    {
        $this->_formalParameter    = $formalParameter;
        $this->_ASTVariableDeclarator = $formalParameter->getFirstChildOfType(
            PHP_Depend_Code_ASTVariableDeclarator::CLAZZ
        );

        $this->_uuid = spl_object_hash($this);
    }

    /**
     * Returns the item name.
     *
     * @return string
     */
    public function getName()
    {
        return $this->_ASTVariableDeclarator->getImage();
    }

    /**
     * Returns a uuid for this code node.
     *
     * @return string
     */
    public function getUUID()
    {
        return $this->_uuid;
    }

    /**
     * Returns the line number where the item declaration can be found.
     *
     * @return integer
     */
    public function getStartLine()
    {
        return $this->_formalParameter->getStartLine();
    }

    /**
     * Returns the line number where the item declaration ends.
     *
     * @return integer The last source line for this item.
     */
    public function getEndLine()
    {
        return $this->_formalParameter->getEndLine();
    }

    /**
     * Returns the parent function or method instance or <b>null</b>
     *
     * @return PHP_Depend_Code_AbstractCallable
     * @since 0.9.5
     */
    public function getDeclaringFunction()
    {
        return $this->_declaringFunction;
    }

    /**
     * Sets the parent function or method object.
     *
     * @param PHP_Depend_Code_AbstractCallable $function The parent callable.
     *
     * @return void
     * @since 0.9.5
     */
    public function setDeclaringFunction(PHP_Depend_Code_AbstractCallable $function)
    {
        $this->_declaringFunction = $function;
    }

    /**
     * This method will return the class where the parent method was declared.
     * The returned value will be <b>null</b> if the parent is a function.
     *
     * @return PHP_Depend_Code_AbstractClassOrInterface
     * @since 0.9.5
     */
    public function getDeclaringClass()
    {
        // TODO: Review this for refactoring, maybe create a empty getParent()?
        if ($this->_declaringFunction instanceof PHP_Depend_Code_Method) {
            return $this->_declaringFunction->getParent();
        }
        return null;
    }

    /**
     * Returns the parameter position in the method/function signature.
     *
     * @return integer
     */
    public function getPosition()
    {
        return $this->_position;
    }

    /**
     * Sets the parameter position in the method/function signature.
     *
     * @param integer $position The parameter position.
     *
     * @return void
     */
    public function setPosition($position)
    {
        $this->_position = $position;
    }

    /**
     * Returns the class type of this parameter. This method will return
     * <b>null</b> for all scalar type, only classes or interfaces are used.
     *
     * @return PHP_Depend_Code_AbstractClassOrInterface
     * @since 0.9.5
     */
    public function getClass()
    {
        $classReference = $this->_formalParameter->getFirstChildOfType(
            PHP_Depend_Code_ASTClassOrInterfaceReference::CLAZZ
        );
        if ($classReference === null) {
            return null;
        }
        return $classReference->getType();
    }

    /**
     * Returns a value holder for the class or interface type declared for this
     * parameter or <b>null</b> when no parameter type hint was declared.
     *
     * @return PHP_Depend_Code_ASTClassOrInterfaceReference
     * @since 0.9.5
     */
    public function getClassReference()
    {
        return $this->_formalParameter->getFirstChildOfType(
            PHP_Depend_Code_ASTClassOrInterfaceReference::CLAZZ
        );
    }

    /**
     * This method will return <b>true</b> when the parameter is passed by
     * reference.
     *
     * @return boolean
     * @since 0.9.5
     */
    public function isPassedByReference()
    {
        return $this->_formalParameter->isPassedByReference();
    }

    /**
     * This method will return <b>true</b> when the parameter was declared with
     * the array type hint, otherwise the it will return <b>false</b>.
     *
     * @return boolean
     * @since 0.9.5
     */
    public function isArray()
    {
        $arrayType = $this->_formalParameter->getFirstChildOfType(
            PHP_Depend_Code_ASTArrayType::CLAZZ
        );
        return ($arrayType !== null);
    }

    /**
     * This method will return <b>true</b> when current parameter is a simple
     * scalar or it is an <b>array</b> or type explicit declared with a default
     * value <b>null</b>.
     *
     * @return boolean
     * @since 0.9.5
     */
    public function allowsNull()
    {
        return (
            (
                $this->isArray() === false
                && $this->getClass() === null
            ) || (
                $this->isDefaultValueAvailable() === true
                && $this->getDefaultValue() === null
            )
        );
    }

    /**
     * This method will return <b>true</b> when this parameter is optional and
     * can be left blank on invokation.
     *
     * @return boolean
     * @since 0.9.5
     */
    public function isOptional()
    {
        return $this->_optional;
    }

    /**
     * This method can be used to mark a parameter optional. Note that a
     * parameter is only optional when it has a default value an no following
     * parameter has no default value.
     *
     * @param boolean $optional Boolean flag that marks this parameter a
     *                          optional or not.
     *
     * @return void
     * @since 0.9.5
     */
    public function setOptional($optional)
    {
        $this->_optional = (boolean) $optional;
    }

    /**
     * This method will return <b>true</b> when the parameter declaration
     * contains a default value.
     *
     * @return boolean
     * @since 0.9.5
     */
    public function isDefaultValueAvailable()
    {
        $value = $this->_ASTVariableDeclarator->getValue();
        if ($value === null) {
            return false;
        }
        return $value->isValueAvailable();
    }

    /**
     * This method will return the declared default value for this parameter.
     * Please note that this method will return <b>null</b> when no default
     * value was declared, therefore you should combine calls to this method and
     * {@link PHP_Depend_Code_Parameter::isDefaultValueAvailable()} to detect a
     * NULL-value.
     *
     * @return mixed
     * @since 0.9.5
     */
    public function getDefaultValue()
    {
        $value = $this->_ASTVariableDeclarator->getValue();
        if ($value === null) {
            return null;
        }
        return $value->getValue();
    }

    /**
     * Visitor method for node tree traversal.
     *
     * @param PHP_Depend_VisitorI $visitor The context visitor
     *                                              implementation.
     *
     * @return void
     */
    public function accept(PHP_Depend_VisitorI $visitor)
    {
        $visitor->visitParameter($this);
    }

    /**
     * This method returns a string representation of this parameter.
     *
     * @return string
     */
    public function __toString()
    {
        $required  = $this->isOptional() ? 'optional' : 'required';
        $reference = $this->isPassedByReference() ? '&' : '';

        $typeHint = '';
        if ($this->isArray() === true) {
            $typeHint = ' array';
        } else if ($this->getClass() !== null) {
            $typeHint = ' ' . $this->getClass()->getName();
        }

        $default = '';
        if ($this->isDefaultValueAvailable()) {
            $default = ' = ';

            $value = $this->getDefaultValue();
            if ($value === null) {
                $default  .= 'NULL';
                $typeHint .= ($typeHint !== '' ? ' or NULL' : '');
            } else if ($value === false) {
                $default .= 'false';
            } else if ($value === true) {
                $default .= 'true';
            } else if (is_array($value) === true) {
                $default .= 'Array';
            } else if (is_string($value) === true) {
                $default .= "'" . $value . "'";
            } else {
                $default .= $value;
            }
        }

        return sprintf(
            'Parameter #%d [ <%s>%s %s%s%s ]',
            $this->_position,
            $required,
            $typeHint,
            $reference,
            $this->getName(),
            $default
        );
    }

    /**
     * This method can be used to export a single function or method parameter.
     *
     * @param string|array   $function  Name of the parent function.
     * @param string|integer $parameter Name or offset of the export parameter.
     * @param boolean        $return    Should this method return the export.
     *
     * @return string|null
     */
    public static function export($function, $parameter, $return = false)
    {
        if (is_callable($function) === false) {
            throw new ReflectionException(__METHOD__ . '() is not supported.');
        }
        return parent::export($function, $parameter, $return);
    }

    // Deprecated methods
    // @codeCoverageIgnoreStart

    /**
     * Returns the type of this property. This method will return <b>null</b>
     * for all scalar type, only class properties will have a type.
     *
     * @return PHP_Depend_Code_AbstractClassOrInterface
     * @deprecated since 0.9.5
     */
    public function getType()
    {
        fwrite(STDERR, __METHOD__ . '() is deprecated since 0.9.5.' . PHP_EOL);
        return $this->getClass();
    }

    /**
     * Sets the type of this property.
     *
     * @param PHP_Depend_Code_AbstractClassOrInterface $type The property type.
     *
     * @return void
     * @deprecated since 0.9.5
     */
    public function setType(PHP_Depend_Code_AbstractClassOrInterface $type)
    {
        fwrite(STDERR, __METHOD__ . '() is deprecated since 0.9.5.' . PHP_EOL);
        $this->setClass($type);
    }

    /**
     * Returns the parent function or method instance or <b>null</b>
     *
     * @return PHP_Depend_Code_AbstractCallable|null
     * @deprecated since 0.9.5
     */
    public function getParent()
    {
        fwrite(STDERR, __METHOD__ . '() is deprecated since 0.9.5.' . PHP_EOL);
        return $this->getDeclaringFunction();
    }

    /**
     * Sets the parent function or method object.
     *
     * @param PHP_Depend_Code_AbstractCallable $parent The parent callable.
     *
     * @return void
     * @deprecated since 0.9.5
     */
    public function setParent(PHP_Depend_Code_AbstractCallable $parent = null)
    {
        fwrite(STDERR, __METHOD__ . '() is deprecated since 0.9.5.' . PHP_EOL);
        $this->setDeclaringFunction($parent);
    }

    /**
     * The line number where the item declaration starts.
     *
     * @var integer $_startLine
     * @deprecated since 0.9.6
     */
    private $_startLine = 0;

    /**
     * The line number where the item declaration ends.
     *
     * @var integer $_endLine
     * @deprecated since 0.9.6
     */
    private $_endLine = 0;

    /**
     * Sets the start line for this item.
     *
     * @param integer $startLine The start line for this item.
     *
     * @return void
     * @deprecated since 0.9.6
     */
    public function setStartLine($startLine)
    {
        fwrite(STDERR, __METHOD__ . '() is deprecated since 0.9.6.' . PHP_EOL);
        $this->_startLine = $startLine;
    }

    /**
     * Sets the end line for this item.
     *
     * @param integer $endLine The end line for this item
     *
     * @return void
     * @deprecated since 0.9.6
     */
    public function setEndLine($endLine)
    {
        fwrite(STDERR, __METHOD__ . '() is deprecated since 0.9.6.' . PHP_EOL);
        $this->_endLine = $endLine;
    }

    // @codeCoverageIgnoreEnd
}
