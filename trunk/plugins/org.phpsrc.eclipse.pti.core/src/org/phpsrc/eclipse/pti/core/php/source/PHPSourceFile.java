/*******************************************************************************
 * Copyright (c) 2009, Sven Kiera
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of the Organisation nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package org.phpsrc.eclipse.pti.core.php.source;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class PHPSourceFile implements ISourceFile {

	private final IFile file;
	private ArrayList<Integer> lineStarts;
	private ArrayList<Integer> lineEnds;

	public PHPSourceFile(IFile file) throws CoreException, IOException {
		this.file = file;
		determineLinePositions(file);
	}

	private void determineLinePositions(IFile file) throws CoreException, IOException {
		lineStarts = new ArrayList<Integer>();
		lineEnds = new ArrayList<Integer>();

		InputStreamReader isr;
		isr = new InputStreamReader(file.getContents());

		int last = -1;
		int i = 0;
		int c;
		while ((c = isr.read()) != -1) {
			if ((char) c == '\n') {
				lineStarts.add(new Integer(last + 1));
				lineEnds.add(new Integer(i));
				last = i;
			}
			i++;
		}

		lineStarts.add(new Integer(last + 1));
		lineEnds.add(new Integer(i));
	}

	public int lineStart(int lineNumber) throws IndexOutOfBoundsException {
		return lineStarts.get(lineNumber - 1);
	}

	public int lineEnd(int lineNumber) throws IndexOutOfBoundsException {
		return lineEnds.get(lineNumber - 1);
	}

	public IFile getFile() {
		return file;
	}

}
