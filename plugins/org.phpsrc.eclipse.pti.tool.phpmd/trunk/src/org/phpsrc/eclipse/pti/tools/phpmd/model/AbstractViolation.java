package org.phpsrc.eclipse.pti.tools.phpmd.model;

import java.net.MalformedURLException;
import java.net.URL;


public abstract class AbstractViolation implements IViolation {
	private int beginline;
	private int endline;
	private int priority;
	private String description;
	private URL externalInfoURL;
	private String rule;
	private String ruleSet;

	public int getBeginline() {
		return beginline;
	}

	public String getDescription() {
		return new String(description);
	}

	public int getEndline() {
		return endline;
	}

	public URL getExternalInfoURL() {
		return externalInfoURL;
	}

	public int getPriority() {
		return priority;
	}

	public String getRule() {
		return new String(rule);
	}

	public String getRuleSet() {
		return new String(ruleSet);
	}

	public void setBeginline(final int line) {
		beginline = line;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setEndline(final int line) {
		endline = line;
	}

	public void setExternalInfoURL(final String url) throws MalformedURLException {
		setExternalInfoURL(new URL(url));
	}

	public void setExternalInfoURL(final URL url) {
		externalInfoURL = url;
	}

	public void setPriority(final int priority) {
		this.priority = priority;
	}

	public void setRule(final String rule) {
		this.rule = rule;
	}

	public void setRuleSet(String ruleSet) {
		this.ruleSet = rule;
	}
}
