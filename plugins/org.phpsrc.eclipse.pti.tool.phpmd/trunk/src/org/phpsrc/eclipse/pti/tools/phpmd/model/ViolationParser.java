/*******************************************************************************
 * Copyright (c) 2009, 2010 Dejan Spasic
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpmd.model;

import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ViolationParser {
	private Collection<IViolation> violations;

	public IViolation[] parse(InputStream violationReportStream) {
		Document document = createDocument(violationReportStream);
		if (null == document)
			return new IViolation[] {};
		return parse(document);
	}

	private Document createDocument(InputStream stream) {
		try {
			DocumentBuilder db = createDocumentBuilder();
			return db.parse(stream);
		} catch (Exception e) {
		}
		return null;
	}

	private DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setErrorHandler(new ErrorHandler() {
			public void error(SAXParseException exception) throws SAXException {
			}

			public void fatalError(SAXParseException exception) throws SAXException {
			}

			public void warning(SAXParseException exception) throws SAXException {
			}
		});
		return db;
	}

	public IViolation[] parse(String violationReportString) {
		Document document = createDocument(violationReportString);
		if (null == document)
			return new IViolation[] {};
		return parse(document);
	}

	private Document createDocument(String violationReportString) {
		try {
			DocumentBuilder db = createDocumentBuilder();
			Reader reader = new CharArrayReader(violationReportString.toCharArray());
			return db.parse(new InputSource(reader));
		} catch (Exception e) {
		}
		return null;
	}

	private IViolation[] parse(Document doc) {
		violations = new HashSet<IViolation>();
		NodeList nodes = doc.getElementsByTagName("file");
		for (int i = 0; i < nodes.getLength(); ++i)
			parse((Element) nodes.item(i));
		return violations.toArray(new IViolation[violations.size()]);
	}

	private void parse(Element file) {
		NodeList node = file.getElementsByTagName("violation");
		String workspaceLocation = getWokspaceLocation().toOSString();

		IPath path = new Path(file.getAttribute("name").substring(workspaceLocation.length()));
		IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(path);

		if (null == res)
			return;

		for (int i = 0; i < node.getLength(); ++i)
			parse((Element) node.item(i), res);
	}

	private void parse(Element violationElement, IResource resource) {
		try {
			IViolation newViolation = new Violation(resource);
			newViolation.setClassName(violationElement.getAttribute("class"));
			newViolation.setPackageName(violationElement.getAttribute("package"));
			newViolation.setMethodName(violationElement.getAttribute("method"));
			newViolation.setFunctionName(violationElement.getAttribute("function"));
			newViolation.setBeginline(Integer.parseInt(violationElement.getAttribute("beginline")));
			newViolation.setEndline(Integer.parseInt(violationElement.getAttribute("endline")));
			newViolation.setRule(violationElement.getAttribute("rule"));
			newViolation.setRuleSet(violationElement.getAttribute("ruleset"));
			newViolation.setPriority(Integer.parseInt(violationElement.getAttribute("priority")));
			newViolation.setExternalInfoURL(violationElement.getAttribute("externalInfoUrl"));
			newViolation.setDescription(violationElement.getTextContent().trim());
			violations.add(newViolation);
		} catch (MalformedURLException e) {
			// ignore the item for now
		}
	}

	private IPath getWokspaceLocation() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation();
	}
}
