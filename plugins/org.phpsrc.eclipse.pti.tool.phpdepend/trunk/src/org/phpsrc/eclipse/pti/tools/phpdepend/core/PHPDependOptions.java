package org.phpsrc.eclipse.pti.tools.phpdepend.core;

import java.util.HashMap;

public class PHPDependOptions extends HashMap<String, Boolean> {
	private static final long serialVersionUID = 6721946024502536688L;

	public final static String OPTION_GENERATE_JDEPEND_CHART = "Generate JDepend Chart";
	public final static String OPTION_GENERATE_SUMMARY_PYRAMID = "Generate Summary Pyramid";

	public PHPDependOptions() {
		super.put(OPTION_GENERATE_JDEPEND_CHART, new Boolean(false));
		super.put(OPTION_GENERATE_SUMMARY_PYRAMID, new Boolean(false));
	}

	public Boolean put(String key, Boolean value) {
		if (containsKey(key)) {
			return super.put(key, value);
		} else {
			return null;
		}
	}
}
