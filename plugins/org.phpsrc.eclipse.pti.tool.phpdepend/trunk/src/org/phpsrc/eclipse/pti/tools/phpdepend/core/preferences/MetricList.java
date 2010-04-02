package org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences;

public class MetricList {
	public static Metric[] getAll() {
		return new Metric[] {
				// ClassLevel (Class)
				new Metric("dit", "Depth of Inheritance Tree", Metric.PROPERTY_CLASS),
				new Metric("impl", "Numer of impemented interfaces", Metric.PROPERTY_CLASS),
				new Metric("cis", "Class interface size", Metric.PROPERTY_CLASS),
				new Metric("csz", "Class size", Metric.PROPERTY_CLASS),
				new Metric("vars", "Number of defined class properties", Metric.PROPERTY_CLASS),
				new Metric("varsi", "Number of own and inherited class properties", Metric.PROPERTY_CLASS),
				new Metric("varsnp", "Number of public class properties", Metric.PROPERTY_CLASS),
				new Metric("wmc", "Weighted Method per Class", Metric.PROPERTY_CLASS),
				new Metric("wmci", "Weighted Method per Class + inherited WMC", Metric.PROPERTY_CLASS),
				new Metric("wmcnp", "Wighted Method per Class for all public class methods", Metric.PROPERTY_CLASS),
				// CodeRank (Class, Package)
				new Metric("cr", "Code rank", Metric.PROPERTY_CLASS | Metric.PROPERTY_PACKAGE),
				new Metric("rcr", "Reverse code rank", Metric.PROPERTY_CLASS | Metric.PROPERTY_PACKAGE),
				// Coupling (Project)
				new Metric("calls", "Number of called methods and functions", Metric.PROPERTY_PROJECT),
				new Metric("fanout", "Number of referenced types", Metric.PROPERTY_PROJECT),
				// Cyclomatic Complexity (Project, Method, Function)
				new Metric("ccn", "Cyclomatic Complexity", Metric.PROPERTY_PROJECT | Metric.PROPERTY_METHOD
						| Metric.PROPERTY_FUNCTION),
				new Metric("ccn2", "Extended Cyclomatic Complexity", Metric.PROPERTY_PROJECT | Metric.PROPERTY_METHOD
						| Metric.PROPERTY_FUNCTION),
				// Dependency (Package)
				new Metric("ca", "Afferent coupling", Metric.PROPERTY_PACKAGE),
				new Metric("ce", "Efferent coupling", Metric.PROPERTY_PACKAGE),
				new Metric("a", "Abstraction", Metric.PROPERTY_PACKAGE),
				new Metric("i", "Instability", Metric.PROPERTY_PACKAGE),
				new Metric("d", "Distance", Metric.PROPERTY_PACKAGE),
				// Hierachy (Project)
				new Metric("clsa", "Number of abstract classes", Metric.PROPERTY_PROJECT),
				new Metric("clsc", "Number of concrete classes", Metric.PROPERTY_PROJECT),
				new Metric("roots", "Number of root classes within the analyzed system", Metric.PROPERTY_PROJECT),
				new Metric("leafs", "Number of leaf classes", Metric.PROPERTY_PROJECT),
				new Metric("maxDIT", "Max Depth of Inheritance Tree value", Metric.PROPERTY_PROJECT),
				// Inheritance (Project)
				new Metric("andc", "Average Number of Derived Classes", Metric.PROPERTY_PROJECT),
				new Metric("ahh", "Average Hierachy Height", Metric.PROPERTY_PROJECT),
				// NodeCount (Project, Package, Class, Interface)
				new Metric("nop", "Number Of Packages", Metric.PROPERTY_PROJECT | Metric.PROPERTY_PACKAGE
						| Metric.PROPERTY_CLASS | Metric.PROPERTY_INTERFACE),
				new Metric("noc", "Number Of Classes", Metric.PROPERTY_PROJECT | Metric.PROPERTY_PACKAGE
						| Metric.PROPERTY_CLASS | Metric.PROPERTY_INTERFACE),
				new Metric("noi", "Number Of Interfaces", Metric.PROPERTY_PROJECT | Metric.PROPERTY_PACKAGE
						| Metric.PROPERTY_CLASS | Metric.PROPERTY_INTERFACE),
				new Metric("nom", "Number Of Methods", Metric.PROPERTY_PROJECT | Metric.PROPERTY_PACKAGE
						| Metric.PROPERTY_CLASS | Metric.PROPERTY_INTERFACE),
				new Metric("nof", "Number Of Functions", Metric.PROPERTY_PROJECT | Metric.PROPERTY_PACKAGE
						| Metric.PROPERTY_CLASS | Metric.PROPERTY_INTERFACE),
				// NodeLOC (Project, Package, Class, Interface, Method,
				// Function)
				new Metric("loc", "Lines of code", Metric.PROPERTY_ALL),
				new Metric("cloc", "Comment lines of code", Metric.PROPERTY_ALL),
				new Metric("ncloc", "Non-comment lines of code", Metric.PROPERTY_ALL),
				// Other
				new Metric("dit", "Depth of inheritence tree"), new Metric("eloc", "Executable lines of code"),
				new Metric("nom", "Number of methods"), new Metric("npath", "NPath Complexity"),
				new Metric("noam", "Number of added methods"), new Metric("nocc", "Number of child classes"),
				new Metric("noom", "Number of overwritten methods"), };
	}
}
