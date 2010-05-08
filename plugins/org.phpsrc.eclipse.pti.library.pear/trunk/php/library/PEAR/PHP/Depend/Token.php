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
 * @category  PHP
 * @package   PHP_Depend
 * @author    Manuel Pichler <mapi@pdepend.org>
 * @copyright 2008-2010 Manuel Pichler. All rights reserved.
 * @license   http://www.opensource.org/licenses/bsd-license.php  BSD License
 * @version   SVN: $Id$
 * @link      http://www.pdepend.org/
 */

/**
 * This struct represents a code token.
 *
 * @category  PHP
 * @package   PHP_Depend
 * @author    Manuel Pichler <mapi@pdepend.org>
 * @copyright 2008-2010 Manuel Pichler. All rights reserved.
 * @license   http://www.opensource.org/licenses/bsd-license.php  BSD License
 * @version   Release: 0.9.12
 * @link      http://www.pdepend.org/
 */
class PHP_Depend_Token
{
    /**
     * The token type identifier.
     *
     * @var integer $type
     */
    public $type = null;

    /**
     * The token image/textual representation.
     *
     * @var string $image
     */
    public $image = null;

    /**
     * The start line number for this token.
     *
     * @var integer $startLine
     */
    public $startLine = null;

    /**
     * The end line number for this token.
     *
     * @var integer $endLine
     */
    public $endLine = null;

    /**
     * The start column number for this token.
     *
     * @var integer $startColumn
     */
    public $startColumn = null;

    /**
     * The end column number for this token.
     *
     * @var integer $endColumn
     */
    public $endColumn = null;

    /**
     * Constructs a new source token.
     *
     * @param integer $type        The token type identifier.
     * @param string  $image       The token image/textual representation.
     * @param integer $startLine   The start line number for this token.
     * @param integer $endLine     The end line number for this token.
     * @param integer $startColumn The start column number for this token.
     * @param integer $endColumn   The end column number for this token.
     */
    public function __construct(
        $type,
        $image,
        $startLine,
        $endLine,
        $startColumn, 
        $endColumn
    ) {
        $this->type        = $type;
        $this->image       = $image;
        $this->startLine   = $startLine;
        $this->endLine     = $endLine;
        $this->startColumn = $startColumn;
        $this->endColumn   = $endColumn;
    }
}