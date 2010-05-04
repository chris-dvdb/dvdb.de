package de.dvdb.web.mediabase.action;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.log.Log;

import de.dvdb.web.Actor;
import de.dvdb.web.search.BasicSearchForm;
import de.dvdb.web.search.Page;

@Name("visitorsAction")
public class VisitorsAction implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In
	protected EntityManager dvdb;

	@Out(scope = ScopeType.EVENT, value = "visitorsPage", required = false)
	protected Page page;

	@In(create = true)
	BasicSearchForm basicSearchForm;

	@In
	Actor actor;

	public void search() {

		Query selectQuery = dvdb
				.createQuery(
						"select distinct(mv.visitor) from MediabaseVisit mv where mv.mediabase = :mediabase")
				.setParameter("mediabase", actor.getUser().getMediabase());

		Query countQuery = dvdb
				.createQuery(
						"select count(mv.visitor) from MediabaseVisit mv where mv.mediabase = :mediabase")
				.setParameter("mediabase", actor.getUser().getMediabase());

		basicSearchForm.setResultLimit(100);

		// create page
		page = new Page(selectQuery, countQuery, basicSearchForm
				.getPageNumber(), basicSearchForm.getPageSize(), 100);
	}
}
