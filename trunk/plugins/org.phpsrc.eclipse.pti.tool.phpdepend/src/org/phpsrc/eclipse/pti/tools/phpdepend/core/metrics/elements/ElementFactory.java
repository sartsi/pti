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

package org.phpsrc.eclipse.pti.tools.phpdepend.core.metrics.elements;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.xerces.parsers.DOMParser;
import org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences.Metric;
import org.phpsrc.eclipse.pti.ui.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ElementFactory {
	public static MetricSummary fromXML(String filePath, Metric[] metrics) throws FileNotFoundException, SAXException,
			IOException {

		DOMParser parser = new DOMParser();
		parser.parse(new InputSource(new FileInputStream(filePath)));

		Document doc = parser.getDocument();

		HashMap<String, Metric> map = new HashMap<String, Metric>(metrics.length);
		for (int i = 0; i < metrics.length; i++) {
			map.put(metrics[i].id, metrics[i]);
		}

		return (MetricSummary) parseXMLNode(doc.getFirstChild(), null, map);
	}

	protected static IElement parseXMLNode(Node node, IElement parent, HashMap<String, Metric> metrics) {
		String name = node.getNodeName();
		NamedNodeMap attr = node.getAttributes();

		int length = attr != null ? attr.getLength() : 0;
		ArrayList<MetricResult> results = new ArrayList<MetricResult>(length);
		for (int i = 0; i < length; i++) {
			Node a = attr.item(i);
			String n = a.getNodeName();
			if (!"".equals(n) && !"name".equals(n) && !"generated".equals(n) && !"pdepend".equals(n)) {
				try {
					Metric m = metrics.get(n);
					if (m != null)
						results.add(new MetricResult(m, Float.parseFloat(a.getNodeValue())));
					else
						results.add(new MetricResult(n, Float.parseFloat(a.getNodeValue())));
				} catch (Exception e) {
					Logger.logException(e);
				}
			}
		}

		IElement element = null;
		if ("metrics".equals(name)) {
			Date generated = new Date();
			try {
				generated = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(attr.getNamedItem("generated")
						.getTextContent());
			} catch (ParseException e) {
			}
			element = new MetricSummary(parent, "Summary", results.toArray(new MetricResult[0]), generated);
		} else if ("package".equals(name)) {
			element = new Package(parent, attr.getNamedItem("name").getTextContent(), results
					.toArray(new MetricResult[0]));
		} else if ("class".equals(name)) {
			element = new Class(parent, attr.getNamedItem("name").getTextContent(), results
					.toArray(new MetricResult[0]));
		} else if ("method".equals(name)) {
			element = new Method(parent, attr.getNamedItem("name").getTextContent(), results
					.toArray(new MetricResult[0]));
		} else if ("function".equals(name)) {
			element = new Function(parent, attr.getNamedItem("name").getTextContent(), results
					.toArray(new MetricResult[0]));
		} else if ("file".equals(name)) {
			element = new File(parent, attr.getNamedItem("name").getTextContent(), results.toArray(new MetricResult[0]));
		} else if ("files".equals(name)) {
			element = new Files(parent, "files", results.toArray(new MetricResult[0]));
		}

		if (element != null)
			parseXMLNodeList(node.getChildNodes(), element, metrics);

		return element;
	}

	protected static void parseXMLNodeList(NodeList list, IElement parent, HashMap<String, Metric> metrics) {
		int length = list.getLength();
		for (int i = 0; i < length; i++) {
			parseXMLNode(list.item(i), parent, metrics);
		}
	}
}
