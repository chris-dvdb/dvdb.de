package de.dvdb.web.item.pricing.action;

import java.util.ArrayList;
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
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.shared.DateTimeQueryHelper;
import de.dvdb.web.Actor;
import de.dvdb.web.item.action.DVDSearchForm;
import de.dvdb.web.search.BasicSearchForm;
import de.dvdb.web.search.NewspaperPage;
import de.dvdb.web.search.SearchContext;

@Name("reducedItemSearchAction")
public class ReducedItemSearchAction {

	public static final String ORDER_TITLE_ASC = "titleAsc";
	public static final String ORDER_TITLE_DESC = "titleDesc";
	public static final String ORDER_RELEASEDATE_ASC = "releaseDateAsc";
	public static final String ORDER_RELEASEDATE_DESC = "releaseDateDesc";
	public static final String ORDER_USERRATINGCONTENT_ASC = "userRatingContentAsc";
	public static final String ORDER_USERRATINGCONTENT_DESC = "userRatingContentDesc";
	public static final String ORDER_USERRATINGMASTERING_ASC = "userRatingMasteringAsc";
	public static final String ORDER_USERRATINGMASTERING_DESC = "userRatingMasteringDesc";
	public static final String ORDER_NUMBEROFOWNERS_ASC = "numberOfOwnersAsc";
	public static final String ORDER_NUMBEROFOWNERS_DESC = "numberOfOwnersDesc";
	public static final String ORDER_NUMBEROFWISHES_ASC = "numberOfWishesAsc";
	public static final String ORDER_NUMBEROFWISHES_DESC = "numberOfWishesDesc";

	public static final String ORDER_PRICECHANGEDATE_DESC = "priceChangeDateDesc";
	public static final String ORDER_PRICECHANGEREL_DESC = "priceChangeRelDesc";
	public static final String ORDER_PRICECHANGEABS_DESC = "priceChangeAbsDesc";

	public static final String ORDER_RATINGCONTENT_ASC = "ratingContentAsc";
	public static final String ORDER_RATINGCONTENT_DESC = "ratingContentDesc";
	public static final String ORDER_RATINGMASTERING_ASC = "ratingMasteringAsc";
	public static final String ORDER_RATINGMASTERING_DESC = "ratingMasteringDesc";
	public static final String ORDER_POSITION_ASC = "positionAsc";
	public static final String ORDER_POSITION_DESC = "positionDesc";
	public static final String ORDER_DATEOFPURCHASE_ASC = "dateOfPurchaseAsc";
	public static final String ORDER_DATEOFPURCHASE_DESC = "dateOfPurchaseDesc";
	public static final String ORDER_TOPNPOSITION_ASC = "topNPositionAsc";
	public static final String ORDER_DATEOFCREATION_ASC = "dateOfCreationAsc";
	public static final String ORDER_DATEOFCREATION_DESC = "dateOfCreationDesc";
	public static final String ORDER_INTENSITY_ASC = "intensityAsc";
	public static final String ORDER_INTENSITY_DESC = "intensityDesc";

	@In(create = true)
	Actor actor;

	@In(create = true)
	BasicSearchForm basicSearchForm;

	@In(create = true)
	DVDSearchForm dvdSearchForm;

	@In
	FacesMessages facesMessages;

	@Out(scope = ScopeType.EVENT, value = "reducedPricesPage", required = false)
	protected NewspaperPage reducedPricesPage;

	@In(create = true, value = "dvdb")
	protected EntityManager em;

	@Logger
	protected Log log;

	private Map<String, Object> resMap = new HashMap<String, Object>();

	protected StringBuilder buildBaseQuery() {

		StringBuilder sb = new StringBuilder(
				"from Price p left join fetch p.item where 1 = 1 ");
		return sb;
	}

	protected StringBuilder buildBaseCountQuery() {
		StringBuilder sb = new StringBuilder(
				"SELECT COUNT(p) from Price p where 1 = 1 ");
		return sb;
	}

	@In(create = true)
	@Out(required = false)
	SearchContext searchContext;

	protected void initSearch() {
//		searchContext.setContextAlias("social.browse.results");
		basicSearchForm.setPageSize(50);
	}

	public void search() {
		initSearch();

		if (dvdSearchForm.getMinNumberOfWishes() != null)
			resMap.put("mnw", dvdSearchForm.getMinNumberOfWishes());
		if (dvdSearchForm.getMinPriceChange() != null)
			resMap.put("mpc", dvdSearchForm.getMinPriceChange());

		StringBuilder sb = buildBaseQuery();
		appendRestrictions(sb);

		// ORDERING
		if (basicSearchForm.getOrder() == null)
			basicSearchForm.setOrder(ORDER_PRICECHANGEDATE_DESC);

		if (basicSearchForm.getOrder().equals(ORDER_TITLE_ASC))
			sb.append(" order by p.item.titleLex asc");
		else if (basicSearchForm.getOrder().equals(ORDER_TITLE_DESC))
			sb.append(" order by p.item.titleLex desc");
		else if (basicSearchForm.getOrder().equals(ORDER_RELEASEDATE_DESC))
			sb.append(" order by p.item.releaseDate desc");
		else if (basicSearchForm.getOrder()
				.equals(ORDER_USERRATINGCONTENT_DESC))
			sb.append(" order by p.item.userRatingContent desc");
		else if (basicSearchForm.getOrder().equals(
				ORDER_USERRATINGMASTERING_DESC))
			sb.append(" order by p.item.userRatingMastering desc");
		else if (basicSearchForm.getOrder().equals(ORDER_NUMBEROFOWNERS_DESC))
			sb.append(" order by p.item.numberOfOwners desc");
		else if (basicSearchForm.getOrder().equals(ORDER_NUMBEROFWISHES_DESC))
			sb.append(" order by p.item.numberOfWishes desc");
		else if (basicSearchForm.getOrder().equals(ORDER_PRICECHANGEDATE_DESC))
			sb.append(" order by p.changeDate desc ");
		else if (basicSearchForm.getOrder().equals(ORDER_PRICECHANGEREL_DESC))
			sb.append(" order by p.previousPrice/p.price desc ");
		else if (basicSearchForm.getOrder().equals(ORDER_PRICECHANGEABS_DESC))
			sb.append(" order by p.previousPrice-p.price desc ");

		// do count
		Query selectQuery = em.createQuery(sb.toString()).setParameter("twa",
				dateTimeQueryHelper.getOneWeekAgo());

		for (String key : resMap.keySet()) {
			log.info("Setting parameter " + key + " to value "
					+ resMap.get(key));
			selectQuery.setParameter(key, resMap.get(key));
		}

		sb = buildBaseCountQuery();
		appendRestrictions(sb);

		Query countQuery = em.createQuery(sb.toString()).setParameter("twa",
				dateTimeQueryHelper.getOneWeekAgo());

		for (String key : resMap.keySet()) {
			log.info("Setting parameter " + key + " to value "
					+ resMap.get(key));
			countQuery.setParameter(key, resMap.get(key));
		}

		// create page

		reducedPricesPage = new NewspaperPage(selectQuery, countQuery,
				basicSearchForm.getPageNumber(), basicSearchForm.getPageSize(),
				basicSearchForm.getResultLimit(), 5);

	}

	@In
	DateTimeQueryHelper dateTimeQueryHelper;

	protected Mediabase getMediabase() {
		return actor.getUser().getMediabase();
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
					sb.append(" and ( p.item.title like :" + tokname
							+ " or p.item.originalTitle like :" + tokname
							+ ") ");
					resMap.put(tokname, "%" + token + "%");
				}
			}

			else if (domain.equals(DVDSearchForm.DOMAIN_COUNTRY)) {
				String[] tokens = keyword.split("\\s");

				int n = 0;
				for (String token : tokens) {
					n++;
					String tokname = domain + n + crit;
					sb.append(" and p.item.countryAndYear like :" + tokname
							+ " ");
					resMap.put(tokname, "%" + token + "%");
				}
			}

			else if (domain.equals(DVDSearchForm.DOMAIN_TITLE_BEGIN)) {
				sb.append(" and (p.item.title like :" + domain + crit
						+ " or p.item.originalTitle like :" + domain + crit
						+ ") ");
				resMap.put(domain + crit, keyword + "%");
			} else if (domain.equals(DVDSearchForm.DOMAIN_EAN)) {
				sb.append(" and p.item.ean like :" + domain + crit + " ");
				resMap.put(domain + crit, "%" + keyword + "%");
			} else if (domain.equals(DVDSearchForm.DOMAIN_DVDBID)) {
				sb.append(" and p.item.id = :" + domain + crit + " ");
				resMap.put(domain + crit, new Long(keyword));
			} else if (domain.equals(DVDSearchForm.DOMAIN_ACTOR)) {
				sb.append(" and p.item.actors like :" + domain + crit + " ");
				resMap.put(domain + crit, "%" + keyword + "%");
			} else if (domain.equals(DVDSearchForm.DOMAIN_DIRECTOR)) {
				sb.append(" and p.item.directors like :" + domain + crit + " ");
				resMap.put(domain + crit, "%" + keyword + "%");
			}
		}
	}

	private void appendRestrictions(StringBuilder sb) {

		if(resMap.containsKey("mnw"))
			sb.append(" and numberOfWishes >= :mnw ");
		if(resMap.containsKey("mpc"))
			sb.append(" and (p.price / p.previousPrice) <= :mpc ");
		
		sb.append(" and p.changeDate > :twa ");
		sb.append(" and p.previousPrice > p.price ");
		sb.append(" and (p.price / p.previousPrice) < 0.95 ");

		// RESTRICTIONS
		if (dvdSearchForm.getBlockbuster()) {
			sb
					.append(" and (p.item.numberOfOwners >= 20 or p.item.numberOfWishes >= 5)");
		}

		if (dvdSearchForm.getOscar()) {
			sb.append(" and p.item.yearOfOscars is not null ");
		}

		if (dvdSearchForm.getUpcoming()) {
			sb.append(" and p.item.releaseDate >= now() ");
		}

		if (dvdSearchForm.getReleaseDateAfter() != null) {
			sb.append(" and p.item.releaseDate >= :releaseDateAfter ");
			resMap.put("releaseDateAfter", dvdSearchForm.getReleaseDateAfter());
		}

		if (dvdSearchForm.getReleaseDateBefore() != null) {
			sb.append(" and p.item.releaseDate <= :releaseDateBefore ");
			resMap
					.put("releaseDateBefore", dvdSearchForm
							.getReleaseDateAfter());
		}

		if (dvdSearchForm.getGenre() != null
				&& !dvdSearchForm.getGenre().equals("")) {
			sb.append(" and p.item.tagString like :genre ");
			resMap.put("genre", "%" + dvdSearchForm.getGenre() + "%");
		}

		if (dvdSearchForm.getRatingContent() != null) {
			sb.append(" and p.item.userRatingContent >= "
					+ dvdSearchForm.getRatingContent() + " ");
		}

		if (dvdSearchForm.getRatingMastering() != null) {
			sb.append(" and p.item.userRatingMastering >= "
					+ dvdSearchForm.getRatingMastering() + " ");
		}

		if (dvdSearchForm.getIndiziert()) {
			sb.append(" and p.item.indiziert = true ");
		}

		sb.append(" and ( 1 = 2 ");

		if (dvdSearchForm.getMediatypeBR())
			sb.append(" or p.item.mediaType = 4 ");

		if (dvdSearchForm.getMediatypeHD())
			sb.append(" or p.item.mediaType = 3 ");

		if (dvdSearchForm.getMediatypeDVD())
			sb.append(" or p.item.mediaType = 1 ");

		sb.append(" ) ");

		addDomainFilters(dvdSearchForm.getKeyword1(), dvdSearchForm
				.getDomain1(), sb, "1");
		addDomainFilters(dvdSearchForm.getKeyword2(), dvdSearchForm
				.getDomain2(), sb, "2");
		addDomainFilters(dvdSearchForm.getKeyword3(), dvdSearchForm
				.getDomain3(), sb, "3");

	}

	public List<String> getSupportedSortOrders() {
		List<String> l = new ArrayList<String>();
		l.add(ORDER_TITLE_ASC);
		l.add(ORDER_TITLE_DESC);
		l.add(ORDER_RELEASEDATE_DESC);
		l.add(ORDER_USERRATINGCONTENT_DESC);
		l.add(ORDER_USERRATINGMASTERING_DESC);
		l.add(ORDER_NUMBEROFOWNERS_DESC);
		l.add(ORDER_NUMBEROFWISHES_DESC);
		l.add(ORDER_PRICECHANGEDATE_DESC);
		l.add(ORDER_PRICECHANGEABS_DESC);
		l.add(ORDER_PRICECHANGEREL_DESC);
		return l;
	}
}
