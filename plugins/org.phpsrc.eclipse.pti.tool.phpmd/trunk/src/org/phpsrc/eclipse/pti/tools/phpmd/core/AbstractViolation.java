package org.phpsrc.eclipse.pti.tools.phpmd.core;

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

	@Override
	public int getBeginline() {
		return beginline;
	}

	@Override
	public String getDescription() {
		return new String(description);
	}

	@Override
	public int getEndline() {
		return endline;
	}

	@Override
	public URL getExternalInfoURL() {
		return externalInfoURL;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public String getRule() {
		return new String(rule);
	}

	@Override
	public String getRuleSet() {
		return new String(ruleSet);
	}

	@Override
	public void setBeginline(final int line) {
		beginline = line;
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public void setEndline(final int line) {
		endline = line;
	}

	@Override
	public void setExternalInfoURL(final String url) throws MalformedURLException {
		setExternalInfoURL(new URL(url));
	}

	@Override
	public void setExternalInfoURL(final URL url) {
		externalInfoURL = url;
	}

	@Override
	public void setPriority(final int priority) {
		this.priority = priority;
	}

	@Override
	public void setRule(final String rule) {
		this.rule = rule;
	}

	@Override
	public void setRuleSet(String ruleSet) {
		this.ruleSet = rule;
	}
}
