/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brock Janiczak (brockj@tpg.com.au)
 *         - https://bugs.eclipse.org/bugs/show_bug.cgi?id=102236: [JUnit] display execution time next to each test
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.core.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;

import org.eclipse.core.resources.IResource;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.model.IMetricElement.Status;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.Metric;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.PHPDependPreferences;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.PHPDependPreferencesFactory;
import org.phpsrc.eclipse.pti.ui.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class MetricRunHandler extends DefaultHandler {

	/*
	 * TODO: validate (currently assumes correct XML)
	 */

	private int fId;

	private IResource fDependentResource;
	private HashMap<String, Metric> fMetrics;
	private MetricRunSession fMetricRunSession;
	private IMetricElement fMetricElement;
	private Stack/* <Boolean> */fNotClosed = new Stack();

	private StringBuffer fFailureBuffer;
	private String fFailureType;
	private boolean fInExpected;
	private boolean fInActual;
	private StringBuffer fExpectedBuffer;
	private StringBuffer fActualBuffer;

	private Locator fLocator;

	private Status fStatus;

	public MetricRunHandler() {
		fMetrics = new HashMap<String, Metric>();
	}

	public MetricRunHandler(IResource dependentResource) {
		fDependentResource = dependentResource;

		PHPDependPreferences prefs = PHPDependPreferencesFactory.factory(dependentResource);
		if (prefs != null && prefs.metrics != null && prefs.metrics.length > 0) {

			fMetrics = new HashMap<String, Metric>(prefs.metrics.length);
			for (int i = 0; i < prefs.metrics.length; i++) {
				fMetrics.put(prefs.metrics[i].id, prefs.metrics[i]);
			}
		} else {
			fMetrics = new HashMap<String, Metric>();
		}
	}

	public MetricRunHandler(MetricRunSession testRunSession) {
		this();
		fMetricRunSession = testRunSession;
	}

	public void setDocumentLocator(Locator locator) {
		fLocator = locator;
	}

	public void startDocument() throws SAXException {
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals(IXMLTags.NODE_METRICS)) {
			if (fMetricRunSession == null) {
				fMetricRunSession = new MetricRunSession(fDependentResource);
			} else {
				fMetricRunSession.reset();
			}

			Date generated = new Date();
			try {
				generated = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(attributes
						.getValue(IXMLTags.ATTR_GENERATED));
			} catch (ParseException e) {
			}
			fMetricElement = new MetricSummary(fMetricRunSession, "Summary", getMetricResults(attributes), generated);
		} else if (qName.equals(IXMLTags.NODE_PACKAGE)) {
			fMetricElement = new MetricPackage(fMetricElement, attributes.getValue(IXMLTags.ATTR_NAME),
					getMetricResults(attributes));
		} else if (qName.equals(IXMLTags.NODE_CLASS)) {
			fMetricElement = new MetricClass(fMetricElement, attributes.getValue(IXMLTags.ATTR_NAME),
					getMetricResults(attributes));
		} else if (qName.equals(IXMLTags.NODE_METHOD)) {
			fMetricElement = new MetricMethod(fMetricElement, attributes.getValue(IXMLTags.ATTR_NAME),
					getMetricResults(attributes));
		} else if (qName.equals(IXMLTags.NODE_FUNCTION)) {
			fMetricElement = new MetricFunction(fMetricElement, attributes.getValue(IXMLTags.ATTR_NAME),
					getMetricResults(attributes));
		} else if (qName.equals(IXMLTags.NODE_FILE)) {
			fMetricElement = new MetricFile(fMetricElement, attributes.getValue(IXMLTags.ATTR_NAME),
					getMetricResults(attributes));
		} else if (qName.equals(IXMLTags.NODE_FILES)) {
			fMetricElement = new MetricFiles(fMetricElement, "files", getMetricResults(attributes));
		} else {
			throw new SAXParseException("unknown node '" + qName + "'", fLocator); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		boolean resetElement = false;

		if (qName.equals(IXMLTags.NODE_METRICS)) {
			resetElement = fMetricElement instanceof MetricSummary;
		} else if (qName.equals(IXMLTags.NODE_PACKAGE)) {
			resetElement = fMetricElement instanceof MetricPackage;
		} else if (qName.equals(IXMLTags.NODE_CLASS)) {
			resetElement = fMetricElement instanceof MetricClass;
		} else if (qName.equals(IXMLTags.NODE_METHOD)) {
			resetElement = fMetricElement instanceof MetricMethod;
		} else if (qName.equals(IXMLTags.NODE_FUNCTION)) {
			resetElement = fMetricElement instanceof MetricFunction;
		} else if (qName.equals(IXMLTags.NODE_FILE)) {
			resetElement = fMetricElement instanceof MetricFile;
		} else if (qName.equals(IXMLTags.NODE_FILES)) {
			resetElement = fMetricElement instanceof MetricFiles;
		}

		if (resetElement)
			fMetricElement = fMetricElement.getParent();
	}

	private MetricResult[] getMetricResults(Attributes attributes) {
		int length = attributes != null ? attributes.getLength() : 0;
		ArrayList<MetricResult> results = new ArrayList<MetricResult>(length);
		for (int i = 0; i < length; i++) {
			String name = attributes.getQName(i);
			if (!"".equals(name) && !IXMLTags.ATTR_NAME.equals(name) && !IXMLTags.ATTR_GENERATED.equals(name)
					&& !IXMLTags.ATTR_PDEPEND.equals(name)) {
				try {
					Metric metric = fMetrics.get(name);
					if (metric != null)
						results.add(new MetricResult(metric, Float.parseFloat(attributes.getValue(i))));
					else
						results.add(new MetricResult(name, Float.parseFloat(attributes.getValue(i))));
				} catch (Exception e) {
					Logger.logException(e);
				}
			}
		}

		return results.toArray(new MetricResult[0]);
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		if (fInExpected) {
			fExpectedBuffer.append(ch, start, length);

		} else if (fInActual) {
			fActualBuffer.append(ch, start, length);

		} else if (fFailureBuffer != null) {
			fFailureBuffer.append(ch, start, length);
		}
	}

	private String toString(StringBuffer buffer) {
		return buffer != null ? buffer.toString() : null;
	}

	private void handleUnknownNode(String qName) throws SAXException {
		// TODO: just log if debug option is enabled?
		String msg = "unknown node '" + qName + "'"; //$NON-NLS-1$//$NON-NLS-2$
		if (fLocator != null) {
			msg += " at line " + fLocator.getLineNumber() + ", column " + fLocator.getColumnNumber(); //$NON-NLS-1$//$NON-NLS-2$
		}
		throw new SAXException(msg);
	}

	public void error(SAXParseException e) throws SAXException {
		throw e;
	}

	public void warning(SAXParseException e) throws SAXException {
		throw e;
	}

	private String getNextId() {
		return Integer.toString(fId++);
	}

	/**
	 * @return the parsed test run session, or <code>null</code>
	 */
	public MetricRunSession getMetricRunSession() {
		return fMetricRunSession;
	}
}
