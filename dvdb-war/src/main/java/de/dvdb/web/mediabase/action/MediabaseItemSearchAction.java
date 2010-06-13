package de.dvdb.web.mediabase.action;

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
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.mediabase.MediabaseItemCollectible;
import de.dvdb.web.Actor;
import de.dvdb.web.item.action.DVDSearchForm;
import de.dvdb.web.mediabase.MediabaseItemSearchForm;
import de.dvdb.web.search.BasicSearchForm;
import de.dvdb.web.search.Page;

@Name("mediabaseItemSearchAction")
@Scope(ScopeType.PAGE)
public class MediabaseItemSearchAction {

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
	public static final String ORDER_PRICE_ASC = "priceAsc";
	public static final String ORDER_PRICE_DESC = "priceDesc";

	@In(create = true)
	Actor actor;

	@In(create = true)
	BasicSearchForm basicSearchForm;

	@In(create = true)
	DVDSearchForm dvdSearchForm;

	@In(create = true)
	MediabaseItemSearchForm mediabaseItemSearchForm;

	@Out(scope = ScopeType.PAGE, value = "page", required = false)
	protected Page page;

	@In(create = true, value = "dvdb")
	protected EntityManager em;

	@Logger
	protected Log log;

	private Map<String, Object> resMap = new HashMap<String, Object>();

	protected StringBuilder buildBaseQuery() {
		if (mediabaseItemSearchForm.getMediabaseItemClassName() == null)
			mediabaseItemSearchForm
					.setMediabaseItemClassName(MediabaseItemCollectible.class
							.getName());

		StringBuilder sb = new StringBuilder(
				"from "
						+ mediabaseItemSearchForm.getMediabaseItemClassName()
						+ " mi inner join fetch mi.item item where mi.mediabase = :mediabase ");
		return sb;
	}

	protected StringBuilder buildBaseCountQuery() {
		StringBuilder sb = new StringBuilder("SELECT COUNT(mi) " + "FROM "
				+ mediabaseItemSearchForm.getMediabaseItemClassName()
				+ " mi inner join mi.item item where mi.mediabase = :mediabase");
		return sb;
	}

	protected void setPage(Page page) {
		this.page = page;
	}

	public void search() {

		StringBuilder sb = buildBaseQuery();
		appendRestrictions(sb);

		// ORDERING
		if (basicSearchForm.getOrder() == null)
			basicSearchForm.setOrder(getMediabase()
					.getDefaultMediabaseSortOrder());

		if (basicSearchForm.getOrder() == null)
			basicSearchForm.setOrder("dateOfCreationDesc");

		if (basicSearchForm.getOrder().equals(ORDER_TITLE_ASC))
			sb.append(" order by item.titleLex asc");
		else if (basicSearchForm.getOrder().equals(ORDER_TITLE_DESC))
			sb.append(" order by item.titleLex desc");
		else if (basicSearchForm.getOrder().equals(ORDER_RELEASEDATE_ASC))
			sb.append(" order by item.releaseDate asc");
		else if (basicSearchForm.getOrder().equals(ORDER_RELEASEDATE_DESC))
			sb.append(" order by item.releaseDate desc");
		else if (basicSearchForm.getOrder().equals(ORDER_USERRATINGCONTENT_ASC))
			sb.append(" order by item.userRatingContent asc");
		else if (basicSearchForm.getOrder()
				.equals(ORDER_USERRATINGCONTENT_DESC))
			sb.append(" order by item.userRatingContent desc");
		else if (basicSearchForm.getOrder().equals(
				ORDER_USERRATINGMASTERING_ASC))
			sb.append(" order item.by userRatingMastering asc");
		else if (basicSearchForm.getOrder().equals(
				ORDER_USERRATINGMASTERING_DESC))
			sb.append(" order by item.userRatingMastering desc");
		else if (basicSearchForm.getOrder().equals(ORDER_RATINGCONTENT_ASC))
			sb.append(" order by ratingContent asc");
		else if (basicSearchForm.getOrder().equals(ORDER_RATINGCONTENT_DESC))
			sb.append(" order by ratingContent desc");
		else if (basicSearchForm.getOrder().equals(ORDER_RATINGMASTERING_ASC))
			sb.append(" order by ratingMastering asc");
		else if (basicSearchForm.getOrder().equals(ORDER_RATINGMASTERING_DESC))
			sb.append(" order by ratingMastering desc");
		else if (basicSearchForm.getOrder().equals(ORDER_NUMBEROFOWNERS_ASC))
			sb.append(" order by item.numberOfOwners asc");
		else if (basicSearchForm.getOrder().equals(ORDER_NUMBEROFOWNERS_DESC))
			sb.append(" order by item.numberOfOwners desc");
		else if (basicSearchForm.getOrder().equals(ORDER_NUMBEROFWISHES_ASC))
			sb.append(" order by item.numberOfWishes asc");
		else if (basicSearchForm.getOrder().equals(ORDER_NUMBEROFWISHES_DESC))
			sb.append(" order by item.numberOfWishes desc");
		else if (basicSearchForm.getOrder().equals(ORDER_POSITION_ASC))
			sb.append(" order by position asc");
		else if (basicSearchForm.getOrder().equals(ORDER_POSITION_DESC))
			sb.append(" order by position desc");
		else if (basicSearchForm.getOrder().equals(ORDER_INTENSITY_ASC))
			sb.append(" order by intensity asc");
		else if (basicSearchForm.getOrder().equals(ORDER_INTENSITY_DESC))
			sb.append(" order by intensity desc");
		else if (basicSearchForm.getOrder().equals(ORDER_DATEOFPURCHASE_ASC))
			sb.append(" order by dateOfPurchase asc");
		else if (basicSearchForm.getOrder().equals(ORDER_DATEOFPURCHASE_DESC))
			sb.append(" order by dateOfPurchase desc");
		else if (basicSearchForm.getOrder().equals(ORDER_TOPNPOSITION_ASC))
			sb.append(" order by topNPosition asc");
		else if (basicSearchForm.getOrder().equals(ORDER_DATEOFCREATION_ASC))
			sb.append(" order by mi.entityMetadata.dateOfCreation asc");
		else if (basicSearchForm.getOrder().equals(ORDER_DATEOFCREATION_DESC))
			sb.append(" order by mi.entityMetadata.dateOfCreation desc");

		// do count
		Query selectQuery = em.createQuery(sb.toString()).setParameter(
				"mediabase", getMediabase());

		for (String key : resMap.keySet()) {
			log.info("Setting parameter " + key + " to value "
					+ resMap.get(key));
			selectQuery.setParameter(key, resMap.get(key));
		}

		sb = buildBaseCountQuery();
		appendRestrictions(sb);

		Query countQuery = em.createQuery(sb.toString()).setParameter(
				"mediabase", getMediabase());

		for (String key : resMap.keySet()) {
			log.info("Setting parameter " + key + " to value "
					+ resMap.get(key));
			countQuery.setParameter(key, resMap.get(key));
		}

		// create page
		setPage(new Page(selectQuery, countQuery, basicSearchForm
				.getPageNumber(), basicSearchForm.getPageSize(),
				basicSearchForm.getResultLimit()));

	}

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
					sb.append(" and ( item.title like :" + tokname
							+ " or item.originalTitle like :" + tokname + ") ");
					resMap.put(tokname, "%" + token + "%");
				}
			}

			else if (domain.equals(DVDSearchForm.DOMAIN_COUNTRY)) {
				String[] tokens = keyword.split("\\s");

				int n = 0;
				for (String token : tokens) {
					n++;
					String tokname = domain + n + crit;
					sb
							.append(" and item.countryAndYear like :" + tokname
									+ " ");
					resMap.put(tokname, "%" + token + "%");
				}
			}

			else if (domain.equals(DVDSearchForm.DOMAIN_TITLE_BEGIN)) {
				sb.append(" and (item.title like :" + domain + crit
						+ " or item.originalTitle like :" + domain + crit
						+ ") ");
				resMap.put(domain + crit, keyword + "%");
			} else if (domain.equals(DVDSearchForm.DOMAIN_EAN)) {
				sb.append(" and item.ean like :" + domain + crit + " ");
				resMap.put(domain + crit, "%" + keyword + "%");
			} else if (domain.equals(DVDSearchForm.DOMAIN_DVDBID)) {
				sb.append(" and item.id = :" + domain + crit + " ");
				resMap.put(domain + crit, new Long(keyword));
			} else if (domain.equals(DVDSearchForm.DOMAIN_ACTOR)) {
				sb.append(" and item.actors like :" + domain + crit + " ");
				resMap.put(domain + crit, "%" + keyword + "%");
			} else if (domain.equals(DVDSearchForm.DOMAIN_DIRECTOR)) {
				sb.append(" and item.directors like :" + domain + crit + " ");
				resMap.put(domain + crit, "%" + keyword + "%");
			}
		}
	}

	private void appendRestrictions(StringBuilder sb) {

		// RESTRICTIONS
		if (dvdSearchForm.getBlockbuster()) {
			sb
					.append(" and (item.numberOfOwners >= 20 or item.numberOfWishes >= 5)");
		}

		if (dvdSearchForm.getOscar()) {
			sb.append(" and item.yearOfOscars is not null ");
		}

		if (dvdSearchForm.getUpcoming()) {
			sb.append(" and item.releaseDate >= now() ");
		}

		if (dvdSearchForm.getReleaseDateAfter() != null) {
			sb.append(" and item.releaseDate >= :releaseDateAfter ");
			resMap.put("releaseDateAfter", dvdSearchForm.getReleaseDateAfter());
		}

		if (dvdSearchForm.getReleaseDateBefore() != null) {
			sb.append(" and item.releaseDate <= :releaseDateBefore ");
			resMap
					.put("releaseDateBefore", dvdSearchForm
							.getReleaseDateAfter());
		}

		if (dvdSearchForm.getGenre() != null
				&& !dvdSearchForm.getGenre().equals("")) {
			sb.append(" and item.tagString like :genre ");
			resMap.put("genre", "%" + dvdSearchForm.getGenre() + "%");
		}

		if (mediabaseItemSearchForm.getBorrowed()) {
			sb.append(" and mi.borrowedToBuddy is not null ");
		}

		if (mediabaseItemSearchForm.getTopN()) {
			sb.append(" and mi.topNPosition is not null ");
		}

		if (mediabaseItemSearchForm.getPreOrdered()) {
			sb.append(" and mi.status = 2 ");
		}

		if (dvdSearchForm.getRatingContent() != null) {
			sb.append(" and item.userRatingContent >= "
					+ dvdSearchForm.getRatingContent() + " ");
		}

		if (dvdSearchForm.getRatingMastering() != null) {
			sb.append(" and item.userRatingMastering >= "
					+ dvdSearchForm.getRatingMastering() + " ");
		}

		if (dvdSearchForm.getIndiziert()) {
			sb.append(" and item.indiziert = true ");
		}

		sb.append(" and ( 1 = 2 ");

		if (dvdSearchForm.getMediatypeBR())
			sb.append(" or item.mediaType = 4 ");

		if (dvdSearchForm.getMediatypeHD())
			sb.append(" or item.mediaType = 3 ");

		if (dvdSearchForm.getMediatypeDVD())
			sb.append(" or item.mediaType = 1 ");

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
		l.add(ORDER_RELEASEDATE_ASC);
		l.add(ORDER_RELEASEDATE_DESC);
		l.add(ORDER_USERRATINGCONTENT_ASC);
		l.add(ORDER_USERRATINGCONTENT_DESC);
		l.add(ORDER_USERRATINGMASTERING_ASC);
		l.add(ORDER_USERRATINGMASTERING_DESC);
		l.add(ORDER_RATINGCONTENT_ASC);
		l.add(ORDER_RATINGCONTENT_DESC);
		l.add(ORDER_RATINGMASTERING_ASC);
		l.add(ORDER_RATINGMASTERING_DESC);
		l.add(ORDER_NUMBEROFOWNERS_ASC);
		l.add(ORDER_NUMBEROFOWNERS_DESC);
		l.add(ORDER_NUMBEROFWISHES_ASC);
		l.add(ORDER_NUMBEROFWISHES_DESC);
		l.add(ORDER_POSITION_ASC);
		l.add(ORDER_POSITION_DESC);
		l.add(ORDER_INTENSITY_ASC);
		l.add(ORDER_INTENSITY_DESC);
		l.add(ORDER_DATEOFPURCHASE_ASC);
		l.add(ORDER_DATEOFPURCHASE_DESC);
		l.add(ORDER_DATEOFCREATION_ASC);
		l.add(ORDER_DATEOFCREATION_DESC);
		return l;
	}

	public boolean isMediabaseItemCollectible() {
		return MediabaseItemCollectible.class.getName().equals(
				mediabaseItemSearchForm.getMediabaseItemClassName());
	}
}
