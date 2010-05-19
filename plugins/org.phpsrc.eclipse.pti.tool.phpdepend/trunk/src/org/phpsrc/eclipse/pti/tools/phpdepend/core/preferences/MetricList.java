/*******************************************************************************
 * Copyright (c) 2010 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences;

import java.util.ArrayList;
import java.util.Collection;

public class MetricList {
	public static Collection<Metric> getAll() {
		ArrayList<Metric> list = new ArrayList<Metric>();

		// ClassLevel (Class)
		list.add(new Metric("dit", "Depth of Inheritance Tree", Metric.LEVEL_CLASS, null, null,
				new Float(5), null));
		list.add(new Metric("impl", "Numer of impemented interfaces", Metric.LEVEL_CLASS));
		list.add(new Metric("cis", "Class interface size", Metric.LEVEL_CLASS));
		list.add(new Metric("csz", "Class size", Metric.LEVEL_CLASS));
		list.add(new Metric("vars", "Number of defined class properties", Metric.LEVEL_CLASS));
		list.add(new Metric("varsi", "Number of own and inherited class properties",
				Metric.LEVEL_CLASS));
		list.add(new Metric("varsnp", "Number of public class properties", Metric.LEVEL_CLASS));
		list.add(new Metric("wmc", "Weighted Method per Class", Metric.LEVEL_CLASS, new Float(20),
				new Float(49), new Float(50), null));
		list
				.add(new Metric("wmci", "Weighted Method per Class + inherited WMC",
						Metric.LEVEL_CLASS));
		list.add(new Metric("wmcnp", "Wighted Method per Class for all public class methods",
				Metric.LEVEL_CLASS));

		// CodeRank (Class, Package)
		list.add(new Metric("cr", "Code rank", Metric.LEVEL_CLASS));
		list.add(new Metric("rcr", "Reverse code rank", Metric.LEVEL_CLASS));

		// Coupling (Project)
		list
				.add(new Metric("calls", "Number of called methods and functions",
						Metric.LEVEL_PROJECT));
		list.add(new Metric("fanout", "Number of referenced types", Metric.LEVEL_PROJECT));

		// Cyclomatic Complexity (Project, Method, Function)
		list.add(new Metric("ccn", "Cyclomatic Complexity", Metric.LEVEL_METHOD, new Float(7),
				new Float(10), new Float(11), null));
		list.add(new Metric("ccn2", "Extended Cyclomatic Complexity", Metric.LEVEL_METHOD));

		// Dependency (Package)
		list.add(new Metric("ca", "Afferent coupling", Metric.LEVEL_PACKAGE));
		list.add(new Metric("ce", "Efferent coupling", Metric.LEVEL_PACKAGE));
		list.add(new Metric("a", "Abstraction", Metric.LEVEL_PACKAGE));
		list.add(new Metric("i", "Instability", Metric.LEVEL_PACKAGE));
		list.add(new Metric("d", "Distance", Metric.LEVEL_PACKAGE));

		// Hierachy (Project)
		list.add(new Metric("clsa", "Number of abstract classes", Metric.LEVEL_PROJECT));
		list.add(new Metric("clsc", "Number of concrete classes", Metric.LEVEL_PROJECT));
		list.add(new Metric("roots", "Number of root classes within the analyzed system",
				Metric.LEVEL_PROJECT));
		list.add(new Metric("leafs", "Number of leaf classes", Metric.LEVEL_PROJECT));
		list.add(new Metric("maxDIT", "Max Depth of Inheritance Tree value", Metric.LEVEL_PROJECT));

		// Inheritance (Project)
		list.add(new Metric("andc", "Average Number of Derived Classes", Metric.LEVEL_PROJECT));
		list.add(new Metric("ahh", "Average Hierachy Height", Metric.LEVEL_PROJECT));

		// NodeCount (Project, Package, Class, Interface)
		list.add(new Metric("nop", "Number Of Packages", Metric.LEVEL_CLASS));
		list.add(new Metric("noc", "Number Of Classes", Metric.LEVEL_CLASS));
		list.add(new Metric("noi", "Number Of Interfaces", Metric.LEVEL_CLASS));
		list.add(new Metric("nom", "Number Of Methods", Metric.LEVEL_CLASS));
		list.add(new Metric("nof", "Number Of Functions", Metric.LEVEL_CLASS));

		// NodeLOC (Project, Package, Class, Interface, Method, Function)
		list.add(new Metric("loc", "Lines of code", Metric.LEVEL_METHOD));
		list.add(new Metric("cloc", "Comment lines of code", Metric.LEVEL_METHOD));
		list.add(new Metric("ncloc", "Non-comment lines of code", Metric.LEVEL_METHOD));
		list.add(new Metric("lloc", "Logical lines of code", Metric.LEVEL_METHOD));

		// Other
		list.add(new Metric("eloc", "Executable lines of code", Metric.LEVEL_METHOD));
		list.add(new Metric("nom", "Number of methods", Metric.LEVEL_CLASS));
		list.add(new Metric("npath", "NPath Complexity", Metric.LEVEL_METHOD));
		list.add(new Metric("noam", "Number of added methods", Metric.LEVEL_CLASS));
		list.add(new Metric("nocc", "Number of child classes", Metric.LEVEL_CLASS));
		list.add(new Metric("noom", "Number of overwritten methods", Metric.LEVEL_CLASS));

		return list;
	}
}
