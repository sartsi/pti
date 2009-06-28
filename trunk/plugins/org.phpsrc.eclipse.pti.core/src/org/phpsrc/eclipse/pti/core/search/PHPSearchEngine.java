package org.phpsrc.eclipse.pti.core.search;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.search.DLTKSearchParticipant;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.internal.ui.search.DLTKSearchScopeFactory;
import org.eclipse.dltk.ui.search.PatternQuerySpecification;
import org.eclipse.php.internal.core.PHPLanguageToolkit;

public class PHPSearchEngine {
	public static SearchMatch[] findClass(String className) {
		DLTKSearchScopeFactory factory = DLTKSearchScopeFactory.getInstance();
		String scopeDescription = factory.getWorkspaceScopeDescription(false);
		IDLTKSearchScope scope = factory.createWorkspaceScope(false, PHPLanguageToolkit.getDefault());

		PatternQuerySpecification querySpec = new PatternQuerySpecification(className, IDLTKSearchConstants.TYPE,
				false, IDLTKSearchConstants.DECLARATIONS, scope, scopeDescription);

		SearchPattern pattern = SearchPattern.createPattern(querySpec.getPattern(), querySpec.getSearchFor(), querySpec
				.getLimitTo(), SearchPattern.R_EXACT_MATCH, scope.getLanguageToolkit());

		SearchEngine engine = new SearchEngine();
		try {
			PHPClassSearchRequestor requestor = new PHPClassSearchRequestor();
			engine.search(pattern, new SearchParticipant[] { new DLTKSearchParticipant() }, scope, requestor,
					new NullProgressMonitor());

			return requestor.getMatches();

		} catch (CoreException e) {
			e.printStackTrace();
		}

		return new SearchMatch[0];
	}
}
