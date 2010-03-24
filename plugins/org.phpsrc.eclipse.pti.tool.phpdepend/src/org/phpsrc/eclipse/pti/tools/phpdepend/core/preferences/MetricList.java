package org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences;

public class MetricList {
	public static Metric[] getAll() {
		return new Metric[] { new Metric("cis", "Class interface size"), new Metric("cloc", "Comment lines of code"),
				new Metric("cr", "Code rank"), new Metric("csz", "Class size"),
				new Metric("dit", "Depth of inheritence tree"), new Metric("eloc", "Executable lines of code"),
				new Metric("impl", "Numer of impemented interfaces"), new Metric("loc", "Lines of code"),
				new Metric("ncloc", "Non-comment lines of code"), new Metric("nom", "Number of methods"),
				new Metric("rcr", "Reverse code rank"), new Metric("vars", "Number of defined class properties"),
				new Metric("varsi", "Number of own and inherited class properties"),
				new Metric("varsnp", "Number of public class properties"),
				new Metric("wmc", "Weighted Method per Class"),
				new Metric("wmci", "Weighted Method per Class + inherited WMC"),
				new Metric("wmcnp", "Wighted Method per Class for all public class methods"),
				new Metric("ccn", "Cyclomatic Complexity"), new Metric("ccn2", "Extended Cyclomatic Complexity"),
				new Metric("npath", "NPath Complexity"), };
	}
}
