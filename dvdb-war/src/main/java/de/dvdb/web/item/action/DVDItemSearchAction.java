package de.dvdb.web.item.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.mediabase.MediabaseItem;
import de.dvdb.domain.model.pricing.Price;
import de.dvdb.web.Actor;
import de.dvdb.web.search.BasicSearchForm;
import de.dvdb.web.search.Page;
import de.dvdb.web.search.SearchContext;

@Name("dvdItemSearchAction")
@Scope(ScopeType.PAGE)
public class DVDItemSearchAction {

	@In
	Actor actor;

	@In(create = true)
	BasicSearchForm basicSearchForm;

	@In(create = true)
	DVDSearchForm dvdSearchForm;

	@In(create = true)
	SearchContext searchContext;

	@Out(scope = ScopeType.PAGE, value = "page", required = false)
	protected Page page;

	@In
	protected EntityManager dvdb;

	@Logger
	protected Log log;

	private Map<String, Object> resMap = new HashMap<String, Object>();

	public void search() {

		StringBuilder sb = new StringBuilder(
				"from PalaceDVDItem item where 1 = 1 ");

		appendRestrictions(sb);

		// ORDERING
		if (basicSearchForm.getOrder() == null)
			basicSearchForm.setOrder("numberOfOwnersDesc");

		if (basicSearchForm.getOrder().equals("titleAsc"))
			sb.append(" order by titleLex asc");
		else if (basicSearchForm.getOrder().equals("titleDesc"))
			sb.append(" order by titleLex desc");
		else if (basicSearchForm.getOrder().equals("releaseDateAsc"))
			sb.append(" order by releaseDate asc");
		else if (basicSearchForm.getOrder().equals("releaseDateDesc"))
			sb.append(" order by releaseDate desc");
		else if (basicSearchForm.getOrder().equals("userRatingContentAsc"))
			sb.append(" order by userRatingContent asc");
		else if (basicSearchForm.getOrder().equals("userRatingContentDesc"))
			sb.append(" order by userRatingContent desc");
		else if (basicSearchForm.getOrder().equals("userRatingMasteringAsc"))
			sb.append(" order by userRatingMastering asc");
		else if (basicSearchForm.getOrder().equals("userRatingMasteringDesc"))
			sb.append(" order by userRatingMastering desc");
		else if (basicSearchForm.getOrder().equals("numberOfOwnersAsc"))
			sb.append(" order by numberOfOwners asc");
		else if (basicSearchForm.getOrder().equals("numberOfOwnersDesc"))
			sb.append(" order by numberOfOwners desc");
		else if (basicSearchForm.getOrder().equals("numberOfWishesAsc"))
			sb.append(" order by numberOfWishes asc");
		else if (basicSearchForm.getOrder().equals("numberOfWishesDesc"))
			sb.append(" order by numberOfWishes desc");
		else if (basicSearchForm.getOrder().equals("lengthAsc"))
			sb.append(" order by length asc");
		else if (basicSearchForm.getOrder().equals("lengthDesc"))
			sb.append(" order by length desc");
		else
			sb.append(" order by numberOfOwners desc");

		Query selectQuery = dvdb.createQuery(sb.toString());

		for (String key : resMap.keySet()) {
			log.info("Setting parameter " + key + " to value "
					+ resMap.get(key));
			selectQuery.setParameter(key, resMap.get(key));
		}

		sb = new StringBuilder("SELECT COUNT(item) "
				+ "FROM PalaceDVDItem item where 1 = 1");
		appendRestrictions(sb);
		Query countQuery = dvdb.createQuery(sb.toString());

		for (String key : resMap.keySet()) {
			log.info("Setting parameter " + key + " to value "
					+ resMap.get(key));
			countQuery.setParameter(key, resMap.get(key));
		}

		// create page
		this.page = new Page(selectQuery, countQuery,
				basicSearchForm.getPageNumber(), basicSearchForm.getPageSize(),
				basicSearchForm.getResultLimit());

		if (page.getTotalResults() > 0) {
			if (actor.getUser() != null
					&& actor.getUser().getMediabase() != null) {
				addMediabaseItemsToPage();
			}

			// do we need to add all prices
			if (dvdSearchForm.getShowPrices())
				addPricesToPage();
		}

	}

	private void addMediabaseItemsToPage() {

		Map<Long, MediabaseItem> mediabaseItems = null;

		if (actor.getUser() != null && actor.getUser().getMediabase() != null
				&& page.getResults().size() > 0) {
			Query q = dvdb
					.createQuery(
							"from MediabaseItem mi "
									+ " inner join fetch mi.item "
									+ " inner join fetch mi.mediabase "
									+ " where mi.item in (:items) and mi.mediabase = :mediabase")
					.setParameter("items", page.getResults())
					.setParameter("mediabase", actor.getUser().getMediabase());
			List<MediabaseItem> list = q.getResultList();

			mediabaseItems = new HashMap<Long, MediabaseItem>();

			for (MediabaseItem mi : list) {
				mediabaseItems.put(mi.getItem().getId(), mi);
			}

			for (Object item : page.getResults()) {
				Item it = (Item) item;
				MediabaseItem mi = mediabaseItems.get(it.getId());
				it.setMediabaseItem(mi);
			}
		}
	}

	private void addPricesToPage() {

		Map<Long, List<Price>> prices = null;

		Query q = dvdb.createQuery(
				"from Price p " + " inner join fetch p.shop "
						+ " where p.item in (:items)").setParameter("items",
				page.getResults());
		List<Price> list = q.getResultList();

		prices = new HashMap<Long, List<Price>>();

		for (Price p : list) {
			if (prices.get(p.getItem().getId()) == null)
				prices.put(p.getItem().getId(), new ArrayList<Price>());
			List<Price> price = prices.get(p.getItem().getId());
			price.add(p);
		}

		for (Object item : page.getResults()) {
			Item it = (Item) item;
			List<Price> ps = prices.get(it.getId());
			if (ps == null)
				continue;
			Collections.sort(ps);
			it.setTop3Prices(ps);
		}
	}

	public String quickSearch() {
		String keyword = dvdSearchForm.getKeyword1();
		dvdSearchForm.reset();
		dvdSearchForm.setKeyword1(keyword);
		basicSearchForm.reset();
		searchContext.setContextAlias("discover.dvd.results");
		search();
		return "/discover/dvd/results.xhtml";
	}

	private void appendRestrictions(StringBuilder sb) {

		// RESTRICTIONS
		if (dvdSearchForm.getBlockbuster()) {
			sb.append(" and (numberOfOwners >= 20 or numberOfWishes >= 5)");
		}

		if (dvdSearchForm.getOscar()) {
			sb.append(" and yearOfOscars is not null ");
		}

		if (dvdSearchForm.getUpcoming()) {
			sb.append(" and releaseDate >= now() ");
		}

		if (dvdSearchForm.getReleaseDateAfter() != null) {
			sb.append(" and releaseDate >= :releaseDateAfter ");
			resMap.put("releaseDateAfter", dvdSearchForm.getReleaseDateAfter());
		}

		if (dvdSearchForm.getReleaseDateBefore() != null) {
			sb.append(" and releaseDate <= :releaseDateBefore ");
			resMap.put("releaseDateBefore", dvdSearchForm.getReleaseDateAfter());
		}

		if (dvdSearchForm.getRatingContent() != null) {
			sb.append(" and userRatingContent >= "
					+ dvdSearchForm.getRatingContent() + " ");
		}

		if (dvdSearchForm.getRatingMastering() != null) {
			sb.append(" and userRatingMastering >= "
					+ dvdSearchForm.getRatingMastering() + " ");
		}

		if (dvdSearchForm.getGenre() != null
				&& !dvdSearchForm.getGenre().equals("")) {
			sb.append(" and tagString like :genre ");
			resMap.put("genre", "%" + dvdSearchForm.getGenre() + "%");
		}

		if (dvdSearchForm.getIndiziert()) {
			sb.append(" and indiziert = true ");
		}

		sb.append(" and ( 1 = 2 ");

		if (dvdSearchForm.getMediatypeBR())
			sb.append(" or mediaType = 4 ");

		if (dvdSearchForm.getMediatypeHD())
			sb.append(" or mediaType = 3 ");

		if (dvdSearchForm.getMediatypeDVD())
			sb.append(" or mediaType = 1 ");

		sb.append(" ) ");

		addDomainFilters(dvdSearchForm.getKeyword1(),
				dvdSearchForm.getDomain1(), sb, "1");
		addDomainFilters(dvdSearchForm.getKeyword2(),
				dvdSearchForm.getDomain2(), sb, "2");
		addDomainFilters(dvdSearchForm.getKeyword3(),
				dvdSearchForm.getDomain3(), sb, "3");
	}

	private void addDomainFilters(String keyword, String domain,
			StringBuilder sb, String crit) {
		if (keyword != null && !keyword.equals("")) {
			if (domain.equals(null))
				domain = DVDSearchForm.DOMAIN_TITLE;

			if (domain.equals(DVDSearchForm.DOMAIN_TITLE)) {
				String[] tokens = keyword.split("\\s");

				int n = 0;
				for (String token : tokens) {
					n++;
					String tokname = domain + n + crit;
					sb.append(" and ( title like :" + tokname
							+ " or originalTitle like :" + tokname + ") ");
					resMap.put(tokname, "%" + token + "%");
				}
			}

			else if (domain.equals(DVDSearchForm.DOMAIN_COUNTRY)) {
				String[] tokens = keyword.split("\\s");

				int n = 0;
				for (String token : tokens) {
					n++;
					String tokname = domain + n + crit;
					sb.append(" and countryAndYear like :" + tokname + " ");
					resMap.put(tokname, "%" + token + "%");
				}
			}

			else if (domain.equals(DVDSearchForm.DOMAIN_TITLE_BEGIN)) {
				sb.append(" and title like :" + domain + crit + "  ");
				resMap.put(domain + crit, keyword + "%");
			} else if (domain.equals(DVDSearchForm.DOMAIN_EAN)) {
				sb.append(" and ean like :" + domain + crit + " ");
				resMap.put(domain + crit, "%" + keyword + "%");
			} else if (domain.equals(DVDSearchForm.DOMAIN_DVDBID)) {
				sb.append(" and id = :" + domain + crit + " ");
				resMap.put(domain + crit, new Long(keyword));
			} else if (domain.equals(DVDSearchForm.DOMAIN_ACTOR)) {
				sb.append(" and actors like :" + domain + crit + " ");
				resMap.put(domain + crit, "%" + keyword + "%");
			} else if (domain.equals(DVDSearchForm.DOMAIN_DIRECTOR)) {
				sb.append(" and directors like :" + domain + crit + " ");
				resMap.put(domain + crit, "%" + keyword + "%");
			}
		}
	}

	public List<String> getSupportedSortOrders() {
		List<String> l = new ArrayList<String>();
		l.add("numberOfOwnersDesc");
		l.add("titleAsc");
		l.add("titleDesc");
		l.add("releaseDateAsc");
		l.add("releaseDateDesc");
		l.add("lengthAsc");
		l.add("lengthDesc");
		l.add("userRatingContentAsc");
		l.add("userRatingContentDesc");
		l.add("userRatingMasteringAsc");
		l.add("userRatingMasteringDesc");
		l.add("numberOfOwnersAsc");
		l.add("numberOfOwnersDesc");
		l.add("numberOfWishesAsc");
		l.add("numberOfWishesDesc");
		return l;
	}
}
