/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.core.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.phpsrc.eclipse.pti.core.Messages;
import org.phpsrc.eclipse.pti.tools.phpdepend.PHPDependPlugin;
import org.phpsrc.eclipse.pti.ui.viewsupport.BasicElementLabels;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Central registry for JUnit test runs.
 */
public final class PHPDependModel {

	private final ListenerList fMetricRunSessionListeners = new ListenerList();
	/**
	 * Active test run sessions, youngest first.
	 */
	private final LinkedList fMetricRunSessions = new LinkedList();

	/**
	 * Starts the model (called by the {@link JUnitPlugin} on startup).
	 */
	public void start() {
	}

	/**
	 * Stops the model (called by the {@link JUnitPlugin} on shutdown).
	 */
	public void stop() {
	}

	public void addMetricRunSessionListener(IMetricRunSessionListener listener) {
		fMetricRunSessionListeners.add(listener);
	}

	public void removeMetricRunSessionListener(IMetricRunSessionListener listener) {
		fMetricRunSessionListeners.remove(listener);
	}

	/**
	 * Adds the given {@link MetricRunSession} and notifies all registered
	 * {@link ITestRunSessionListener}s.
	 * <p>
	 * <b>To be called in the UI thread only!</b>
	 * </p>
	 * 
	 * @param testRunSession
	 *            the session to add
	 */
	public void addMetricRunSession(MetricRunSession testRunSession) {
		Assert.isNotNull(testRunSession);
		Assert.isLegal(!fMetricRunSessions.contains(testRunSession));
		fMetricRunSessions.addFirst(testRunSession);
		notifyMetricRunSessionAdded(testRunSession);
	}

	/**
	 * @return a list of active {@link MetricRunSession}s. The list is a copy of
	 *         the internal data structure and modifications do not affect the
	 *         global list of active sessions. The list is sorted by age,
	 *         youngest first.
	 */
	public List<MetricRunSession> getMetricRunSessions() {
		return new ArrayList<MetricRunSession>(fMetricRunSessions);
	}

	/**
	 * Removes the given {@link MetricRunSession} and notifies all registered
	 * {@link ITestRunSessionListener}s.
	 * <p>
	 * <b>To be called in the UI thread only!</b>
	 * </p>
	 * 
	 * @param testRunSession
	 *            the session to remove
	 */
	public void removeMetricRunSession(MetricRunSession testRunSession) {
		boolean existed = fMetricRunSessions.remove(testRunSession);
		if (existed) {
			notifyMetricRunSessionRemoved(testRunSession);
		}
		testRunSession.removeSwapFile();
	}

	private void notifyMetricRunSessionRemoved(MetricRunSession testRunSession) {
		Object[] listeners = fMetricRunSessionListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((IMetricRunSessionListener) listeners[i]).sessionRemoved(testRunSession);
		}
	}

	private void notifyMetricRunSessionAdded(MetricRunSession testRunSession) {
		Object[] listeners = fMetricRunSessionListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((IMetricRunSessionListener) listeners[i]).sessionAdded(testRunSession);
		}
	}

	public static void exportMetricRunSession(MetricRunSession metricRunSession, OutputStream out)
			throws TransformerFactoryConfigurationError, TransformerException {

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		InputSource inputSource = new InputSource();
		SAXSource source = new SAXSource(new MetricRunSessionSerializer(metricRunSession), inputSource);
		StreamResult result = new StreamResult(out);
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
		/*
		 * Bug in Xalan: Only indents if proprietary property
		 * org.apache.xalan.templates.OutputProperties.S_KEY_INDENT_AMOUNT is
		 * set.
		 * 
		 * Bug in Xalan as shipped with J2SE 5.0: Does not read the
		 * indent-amount property at all >:-(.
		 */

		try {
			transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (IllegalArgumentException e) {
			// no indentation today...
		}
		transformer.transform(source, result);
	}

	public static void exportMetricRunSession(MetricRunSession metricRunSession, File file) throws CoreException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			exportMetricRunSession(metricRunSession, out);

		} catch (IOException e) {
			throwExportError(file, e);
		} catch (TransformerConfigurationException e) {
			throwExportError(file, e);
		} catch (TransformerException e) {
			throwExportError(file, e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e2) {
					PHPDependPlugin.log(e2);
				}
			}
		}
	}

	public static MetricRunSession importMetricRunSession(File file) throws CoreException {
		return importMetricRunSession(file, null);
	}

	public static MetricRunSession importMetricRunSession(File file, IResource dependentResource) throws CoreException {
		return importMetricRunSession(file, null, null, dependentResource);
	}

	public static MetricRunSession importMetricRunSession(File file, File jdependChartFile, File summaryPyramidFile,
			IResource dependentResource) throws CoreException {
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			// parserFactory.setValidating(true); // TODO: add DTD and debug
			// flag
			SAXParser parser = parserFactory.newSAXParser();
			MetricRunHandler handler = new MetricRunHandler(dependentResource);
			parser.parse(file, handler);
			MetricRunSession session = handler.getMetricRunSession();
			session.setJDependChartFile(jdependChartFile);
			session.setSummaryPyramidFile(summaryPyramidFile);

			PHPDependPlugin.getModel().addMetricRunSession(session);
			return session;
		} catch (ParserConfigurationException e) {
			throwImportError(file, e);
		} catch (SAXException e) {
			throwImportError(file, e);
		} catch (IOException e) {
			throwImportError(file, e);
		}
		return null; // does not happen
	}

	public static void importIntoMetricRunSession(File swapFile, MetricRunSession metricRunSession)
			throws CoreException {
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			// parserFactory.setValidating(true); // TODO: add DTD and debug
			// flag
			SAXParser parser = parserFactory.newSAXParser();
			MetricRunHandler handler = new MetricRunHandler(metricRunSession);
			parser.parse(swapFile, handler);
		} catch (ParserConfigurationException e) {
			throwImportError(swapFile, e);
		} catch (SAXException e) {
			throwImportError(swapFile, e);
		} catch (IOException e) {
			throwImportError(swapFile, e);
		}
	}

	private static void throwExportError(File file, Exception e) throws CoreException {
		throw new CoreException(new org.eclipse.core.runtime.Status(IStatus.ERROR, PHPDependPlugin.PLUGIN_ID, Messages
				.format(ModelMessages.JUnitModel_could_not_write, BasicElementLabels.getPathLabel(file)), e));
	}

	private static void throwImportError(File file, Exception e) throws CoreException {
		throw new CoreException(new org.eclipse.core.runtime.Status(IStatus.ERROR, PHPDependPlugin.PLUGIN_ID, Messages
				.format(ModelMessages.JUnitModel_could_not_read, BasicElementLabels.getPathLabel(file)), e));
	}
}
