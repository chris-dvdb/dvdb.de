package de.dvdb.web.mediabase.action;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.log.Log;

import de.dvdb.web.Actor;
import de.dvdb.web.item.action.DVDSearchForm;
import de.dvdb.web.search.BasicSearchForm;
import de.dvdb.web.search.Page;

@Name("userDvdItemSearchAction")
public class UserDVDItemSearchAction {

	@In(create = true)
	Actor actor;

	@In(create = true)
	BasicSearchForm basicSearchForm;

	@In(create = true)
	DVDSearchForm dvdSearchForm;

	@Out(scope = ScopeType.EVENT, value = "userDVDItemsPage", required = false)
	protected Page page;

	@In(create = true, value = "dvdb")
	protected EntityManager em;

	@Logger
	protected Log log;

	public void search() {

		StringBuilder sb = new StringBuilder(
				"from UserDVDItem item where item.user = :user ");

		appendRestrictions(sb);

		// ORDERING
		if (basicSearchForm.getOrder() == null)
			basicSearchForm.setOrder("titleAsc");

		if (basicSearchForm.getOrder().equals("titleAsc"))
			sb.append(" order by title asc");
		else if (basicSearchForm.getOrder().equals("titleDesc"))
			sb.append(" order by title desc");
		else if (basicSearchForm.getOrder().equals("releaseDateAsc"))
			sb.append(" order by releaseDate asc");
		else if (basicSearchForm.getOrder().equals("releaseDateDesc"))
			sb.append(" order by releaseDate desc");

		Query selectQuery = em.createQuery(sb.toString()).setParameter("user",
				actor.getUser());

		// do count
		sb = new StringBuilder("SELECT COUNT(item) "
				+ "from UserDVDItem item where item.user = :user");
		appendRestrictions(sb);
		Query countQuery = em.createQuery(sb.toString()).setParameter("user",
				actor.getUser());

		// create page
		this.page = new Page(selectQuery, countQuery, basicSearchForm
				.getPageNumber(), basicSearchForm.getPageSize(),
				basicSearchForm.getResultLimit());
	}

	public List<String> getSupportedSortOrders() {
		List<String> l = new ArrayList<String>();
		l.add("titleAsc");
		l.add("titleDesc");
		l.add("releaseDateAsc");
		l.add("releaseDateDesc");
		return l;
	}

	private void appendRestrictions(StringBuilder sb) {
	}

}
