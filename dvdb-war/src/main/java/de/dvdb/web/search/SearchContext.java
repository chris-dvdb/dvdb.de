package de.dvdb.web.search;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("searchContext")
@Scope(ScopeType.CONVERSATION)
public class SearchContext implements Serializable {

	private static final long serialVersionUID = -2515101762301351002L;

	String contextAlias;

	public String getContextAlias() {
		return contextAlias;
	}

	public void setContextAlias(String contextAlias) {
		this.contextAlias = contextAlias;
	}

	public String getHeadlineKey() {
		return contextAlias + ".headline";
	}

	public String getBrowserTitleKey() {
		return contextAlias + ".browserTitle";
	}

	public String getIntroKey() {
		return contextAlias + ".intro";
	}

}
