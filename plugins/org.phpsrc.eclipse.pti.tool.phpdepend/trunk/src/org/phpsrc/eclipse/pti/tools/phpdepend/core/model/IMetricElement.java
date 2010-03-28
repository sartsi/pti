/*******************************************************************************
 * Copyright (c) 2010, Sven Kiera
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

package org.phpsrc.eclipse.pti.tools.phpdepend.core.model;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.graphics.Image;

public interface IMetricElement {
	/**
	 * Running states of a test.
	 */
	public static final class ProgressState {
		/** state that describes that the test element has not started */
		public static final ProgressState NOT_STARTED = new ProgressState("Not Started"); //$NON-NLS-1$
		/** state that describes that the test element has is running */
		public static final ProgressState RUNNING = new ProgressState("Running"); //$NON-NLS-1$
		/**
		 * state that describes that the test element has been stopped before
		 * being completed
		 */
		public static final ProgressState STOPPED = new ProgressState("Stopped"); //$NON-NLS-1$
		/** state that describes that the test element has completed */
		public static final ProgressState COMPLETED = new ProgressState("Completed"); //$NON-NLS-1$

		private String fName;

		private ProgressState(String name) {
			fName = name;
		}

		public String toString() {
			return fName;
		}
	}

	/**
	 * Result states of a test.
	 */
	public static final class Result {
		/** state that describes that the test result is undefined */
		public static final Result UNDEFINED = new Result("Undefined"); //$NON-NLS-1$
		/** state that describes that the test result is 'OK' */
		public static final Result OK = new Result("OK"); //$NON-NLS-1$
		/** state that describes that the test result is 'Error' */
		public static final Result ERROR = new Result("Error"); //$NON-NLS-1$
		/** state that describes that the test result is 'Failure' */
		public static final Result WARNING = new Result("Warning"); //$NON-NLS-1$
		/** state that describes that the test result is 'Ignored' */
		public static final Result IGNORED = new Result("Ignored"); //$NON-NLS-1$

		private String fName;

		private Result(String name) {
			fName = name;
		}

		public String toString() {
			return fName;
		}
	}

	public final static class Status {
		public static final Status RUNNING_ERROR = new Status("RUNNING_ERROR", 5); //$NON-NLS-1$
		public static final Status RUNNING_FAILURE = new Status("RUNNING_FAILURE", 6); //$NON-NLS-1$
		public static final Status RUNNING = new Status("RUNNING", 3); //$NON-NLS-1$

		public static final Status ERROR = new Status("ERROR", /* 1 */IMetricRunListener.STATUS_ERROR); //$NON-NLS-1$
		public static final Status WARNING = new Status("WARNING", /* 2 */IMetricRunListener.STATUS_FAILURE); //$NON-NLS-1$
		public static final Status OK = new Status("OK", /* 0 */IMetricRunListener.STATUS_OK); //$NON-NLS-1$
		public static final Status NOT_RUN = new Status("NOT_RUN", 4); //$NON-NLS-1$

		private static final Status[] OLD_CODE = { OK, ERROR, WARNING };

		private final String fName;
		private final int fOldCode;

		private Status(String name, int oldCode) {
			fName = name;
			fOldCode = oldCode;
		}

		public int getOldCode() {
			return fOldCode;
		}

		public String toString() {
			return fName;
		}

		/* error state predicates */

		public boolean isOK() {
			return this == OK || this == RUNNING || this == NOT_RUN;
		}

		public boolean isWarning() {
			return this == WARNING || this == RUNNING_FAILURE;
		}

		public boolean isError() {
			return this == ERROR || this == RUNNING_ERROR;
		}

		public boolean isErrorOrWarning() {
			return isError() || isWarning();
		}

		/* progress state predicates */

		public boolean isNotRun() {
			return this == NOT_RUN;
		}

		public boolean isRunning() {
			return this == RUNNING || this == RUNNING_FAILURE || this == RUNNING_ERROR;
		}

		public boolean isDone() {
			return this == OK || this == WARNING || this == ERROR;
		}

		public static Status combineStatus(Status one, Status two) {
			Status progress = combineProgress(one, two);
			Status error = combineError(one, two);
			return combineProgressAndErrorStatus(progress, error);
		}

		private static Status combineProgress(Status one, Status two) {
			if (one.isNotRun() && two.isNotRun())
				return NOT_RUN;
			else if (one.isDone() && two.isDone())
				return OK;
			else if (!one.isRunning() && !two.isRunning())
				return OK; // one done, one not-run -> a parent failed and its
			// children are not run
			else
				return RUNNING;
		}

		private static Status combineError(Status one, Status two) {
			if (one.isError() || two.isError())
				return ERROR;
			else if (one.isWarning() || two.isWarning())
				return WARNING;
			else
				return OK;
		}

		private static Status combineProgressAndErrorStatus(Status progress, Status error) {
			if (progress.isDone()) {
				if (error.isError())
					return ERROR;
				if (error.isWarning())
					return WARNING;
				return OK;
			}

			if (progress.isNotRun()) {
				// Assert.isTrue(!error.isErrorOrFailure());
				return NOT_RUN;
			}

			// Assert.isTrue(progress.isRunning());
			if (error.isError())
				return RUNNING_ERROR;
			if (error.isWarning())
				return RUNNING_FAILURE;
			// Assert.isTrue(error.isOK());
			return RUNNING;
		}

		/**
		 * @param oldStatus
		 *            one of {@link IMetricRunListener}'s STATUS_* constants
		 * @return the Status
		 */
		public static Status convert(int oldStatus) {
			return OLD_CODE[oldStatus];
		}

		public Result convertToResult() {
			if (isNotRun())
				return Result.UNDEFINED;
			if (isError())
				return Result.ERROR;
			if (isWarning())
				return Result.WARNING;
			if (isRunning()) {
				return Result.UNDEFINED;
			}
			return Result.OK;
		}

		public ProgressState convertToProgressState() {
			if (isRunning()) {
				return ProgressState.RUNNING;
			}
			if (isDone()) {
				return ProgressState.COMPLETED;
			}
			return ProgressState.NOT_STARTED;
		}

	}

	public String getName();

	public Image getImage();

	public IMetricElement getParent();

	public IMetricElement[] getChildren();

	public boolean hasChildren();

	public IResource getResource();

	public IMarker getFileMarker();

	public MetricResult[] getResults();

	public boolean hasErrors();

	public boolean hasWarnings();

	public Status getStatus();
}
