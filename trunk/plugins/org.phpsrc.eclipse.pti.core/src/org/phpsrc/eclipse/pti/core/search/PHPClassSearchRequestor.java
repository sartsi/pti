package org.phpsrc.eclipse.pti.core.search;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchRequestor;

public class PHPClassSearchRequestor extends SearchRequestor {
	private ArrayList<SearchMatch> searchMatches;

	public void beginReporting() {
		searchMatches = new ArrayList<SearchMatch>();
		super.beginReporting();
	}

	@Override
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		if (match.isExact()) {
			searchMatches.add(match);
		}
	}

	public SearchMatch[] getMatches() {
		return searchMatches.toArray(new SearchMatch[0]);
	}
}
