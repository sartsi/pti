package org.phpsrc.eclipse.pti.tools.phpdepend.core.preferences;

public class Metric {
	public static final int TYPE_FILE = 1;
	public static final int TYPE_FILE_WITH_HIERACHY = 2;
	public static final int TYPE_PACKAGE = 3;

	public boolean enabled;
	public String name;
	public String id;
	public Float warningMin;
	public Float warningMax;
	public Float errorMax;
	public Float errorMin;
	public int type;

	@Override
	public String toString() {
		return name;
	}
}
