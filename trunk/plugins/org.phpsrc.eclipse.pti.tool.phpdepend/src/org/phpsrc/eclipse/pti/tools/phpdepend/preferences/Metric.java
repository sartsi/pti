package org.phpsrc.eclipse.pti.tools.phpdepend.preferences;

public class Metric {
	public static final String COMPARE_LESS = "<";
	public static final String COMPARE_LESS_OR_EQUAL = "<=";
	public static final String COMPARE_EQUAL = "=";
	public static final String COMPARE_GREATER = ">";
	public static final String COMPARE_GREATER_OR_EQUAL = ">=";

	public boolean enabled;
	public String name;
	public String id;
	public String warningCompare;
	public int warningLevel;
	public String errorCompare;
	public int errorLevel;

	@Override
	public String toString() {
		return name;
	}
}
