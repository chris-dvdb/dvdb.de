package de.dvdb.domain.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalDuration;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.user.User;

@Name("dvdbGlobals")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class DvdbGlobals implements Serializable {

	private static final long serialVersionUID = -2222102720904149239L;

	@In
	EntityManager dvdb;

	@In
	EntityManager forum;

	@Logger
	Log log;

	private User chris;

	private Object[] stats;

	private List<Object> topBloggers;

	public User getChris() {

		if (chris == null) {
			log.info("Loading user chris");
			chris = (User) dvdb.createQuery(
					"from User u where u.username = 'Chris'").getSingleResult();
		}
		return chris;
	}

	public List<Object> getTopBloggers() {
		return topBloggers;
	}

	public void setTopBloggers(List<Object> topBloggers) {
		this.topBloggers = topBloggers;
	}

	public Object[] getStats() {
		return stats;
	}

	public void setStats(Object[] stats) {
		this.stats = stats;
	}

	@Asynchronous
	@Transactional
	public void populate(@Expiration Date date, @IntervalDuration Long interval) {
		populate();
	}

	@Create
	public void populate() {

		Long user = (Long) dvdb
				.createQuery("select count(user) from User user")
				.getSingleResult();

		Long items = (Long) dvdb.createQuery(
				"select count(item) from Item item").getSingleResult();

		Long baseItems = (Long) dvdb.createQuery(
				"select count(item) from MediabaseItemCollectible item")
				.getSingleResult();

		Long postings = (Long) forum.createQuery("select count(*) from Post")
				.getSingleResult();

		setStats(new Object[] { user, items, baseItems, postings });

		log.info("Populated dvdbGlobals");

		topBloggers();

		log.info("Populated top bloggers");
	}

	private void topBloggers() {

		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		sb.append(" select count(be), be.mediabase.user from BlogEntry be where ");
		sb.append(" be.date < :now ");
		paramMap.put("now", new Date());
		sb.append(" and be.active = true group by be.mediabase.user having count(be) > 2 ");

		// select
		Query selectQuery = dvdb.createQuery(sb.toString()
				+ " order by count(be) desc");

		for (String key : paramMap.keySet())
			selectQuery.setParameter(key, paramMap.get(key));

		topBloggers = selectQuery.getResultList();

	}

}
