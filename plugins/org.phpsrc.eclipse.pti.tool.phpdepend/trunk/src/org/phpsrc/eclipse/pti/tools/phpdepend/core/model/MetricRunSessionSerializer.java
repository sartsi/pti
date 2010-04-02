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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

public class MetricRunSessionSerializer implements XMLReader {

	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String CDATA = "CDATA"; //$NON-NLS-1$
	private static final Attributes NO_ATTS = new AttributesImpl();

	private final MetricRunSession fMetricRunSession;
	private ContentHandler fHandler;
	private ErrorHandler fErrorHandler;

	private final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss"; //$NON-NLS-1$

	/**
	 * @param testRunSession
	 *            the test run session to serialize
	 */
	public MetricRunSessionSerializer(MetricRunSession metricRunSession) {
		Assert.isNotNull(metricRunSession);
		fMetricRunSession = metricRunSession;
	}

	public void parse(InputSource input) throws IOException, SAXException {
		if (fHandler == null)
			throw new SAXException("ContentHandler missing"); //$NON-NLS-1$

		fHandler.startDocument();
		handleMetricRun();
		fHandler.endDocument();
	}

	private void handleMetricRun() throws SAXException {
		AttributesImpl atts = new AttributesImpl();

		MetricSummary summary = fMetricRunSession.getSummaryRoot();

		Date generated = summary.getGenerated();
		if (generated != null) {
			SimpleDateFormat formater = new SimpleDateFormat(dateFormat);
			addCDATA(atts, IXMLTags.ATTR_GENERATED, formater.format(generated));
		}

		if (summary.getVersion() != null)
			addCDATA(atts, IXMLTags.ATTR_PDEPEND, summary.getVersion());

		addMetricResults(atts, summary.getResults());

		startElement(IXMLTags.NODE_METRICS, atts);

		for (IMetricElement element : summary.getChildren()) {
			handleMetricElement(element);
		}

		endElement(IXMLTags.NODE_METRICS);
	}

	private void handleMetricElement(IMetricElement metricElement)
			throws SAXException {
		String elementName = null;
		String nameAttr = null;

		if (metricElement instanceof MetricPackage) {
			elementName = IXMLTags.NODE_PACKAGE;
			nameAttr = metricElement.getName();
		} else if (metricElement instanceof MetricClass) {
			elementName = IXMLTags.NODE_CLASS;
			nameAttr = metricElement.getName();
		} else if (metricElement instanceof MetricMethod) {
			elementName = IXMLTags.NODE_METHOD;
			nameAttr = metricElement.getName();
		} else if (metricElement instanceof MetricFunction) {
			elementName = IXMLTags.NODE_FUNCTION;
			nameAttr = metricElement.getName();
		} else if (metricElement instanceof MetricFiles) {
			elementName = IXMLTags.NODE_FILES;
		} else if (metricElement instanceof MetricFile) {
			elementName = IXMLTags.NODE_FILE;
			IResource resource = metricElement.getResource();
			if (resource != null) {
				nameAttr = resource.getLocation().toOSString();
			}
		}

		if (elementName == null) {
			throw new IllegalStateException(String.valueOf(metricElement));
		}

		AttributesImpl atts = new AttributesImpl();

		if (nameAttr != null)
			addCDATA(atts, IXMLTags.ATTR_NAME, nameAttr);
		addMetricResults(atts, metricElement.getResults());

		startElement(elementName, atts);

		for (IMetricElement element : metricElement.getChildren()) {
			handleMetricElement(element);
		}

		endElement(elementName);
	}

	private void startElement(String name, Attributes atts) throws SAXException {
		fHandler.startElement(EMPTY, name, name, atts);
	}

	private void endElement(String name) throws SAXException {
		fHandler.endElement(EMPTY, name, name);
	}

	private static void addCDATA(AttributesImpl atts, String name, int value) {
		addCDATA(atts, name, Integer.toString(value));
	}

	private static void addCDATA(AttributesImpl atts, String name, float value) {
		addCDATA(atts, name, Float.toString(value));
	}

	private static void addCDATA(AttributesImpl atts, String name, String value) {
		atts.addAttribute(EMPTY, EMPTY, name, CDATA, value);
	}

	private static void addMetricResults(AttributesImpl atts,
			MetricResult[] results) {
		for (MetricResult result : results) {
			addCDATA(atts, result.id, result.value);
		}
	}

	public void setContentHandler(ContentHandler handler) {
		this.fHandler = handler;
	}

	public ContentHandler getContentHandler() {
		return fHandler;
	}

	public void setErrorHandler(ErrorHandler handler) {
		fErrorHandler = handler;
	}

	public ErrorHandler getErrorHandler() {
		return fErrorHandler;
	}

	// ignored:

	public void parse(String systemId) throws IOException, SAXException {
	}

	public void setDTDHandler(DTDHandler handler) {
	}

	public DTDHandler getDTDHandler() {
		return null;
	}

	public void setEntityResolver(EntityResolver resolver) {
	}

	public EntityResolver getEntityResolver() {
		return null;
	}

	public void setProperty(java.lang.String name, java.lang.Object value) {
	}

	public Object getProperty(java.lang.String name) {
		return null;
	}

	public void setFeature(java.lang.String name, boolean value) {
	}

	public boolean getFeature(java.lang.String name) {
		return false;
	}
}
