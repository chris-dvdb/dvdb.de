package de.dvdb.web.item.action;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import de.dvdb.domain.shared.DateTimeQueryHelper;
import de.dvdb.web.item.pricing.action.ReducedItemSearchAction;
import de.dvdb.web.mediabase.action.MbMediabaseSearchAction;
import de.dvdb.web.mediabase.action.MediabaseItemSearchAction;
import de.dvdb.web.search.BasicSearchForm;
import de.dvdb.web.search.SearchContext;

@Name("preparedItemSearchActions")
public class PreparedItemSearchActions {

	@In(create = true)
	BasicSearchForm basicSearchForm;

	@In(create = true)
	DVDSearchForm dvdSearchForm;

	@In(create = true)
	SearchContext searchContext;

	@In(create = true)
	DateTimeQueryHelper dateTimeQueryHelper;

	@In(create = true)
	DVDItemSearchAction dvdItemSearchAction;

	@In(create = true)
	ReducedItemSearchAction reducedItemSearchAction;
	
	public void showNewReleases() {
		resetAllForms();
		dvdSearchForm.setReleaseDateAfter(dateTimeQueryHelper.getOneMonthAgo());
		searchContext.setContextAlias("discover.dvd.releases");
		dvdItemSearchAction.search();
	}

	public void showBluRayReleases() {
		resetAllForms();
		dvdSearchForm.setReleaseDateAfter(dateTimeQueryHelper.getOneWeekAgo());
		basicSearchForm
				.setOrder(MediabaseItemSearchAction.ORDER_RELEASEDATE_ASC);
		dvdSearchForm.setMediatypeBR(true);
		dvdSearchForm.setMediatypeDVD(false);
		dvdSearchForm.setMediatypeHD(false);
		searchContext.setContextAlias("discover.bluray.neuerscheinungen");
		dvdItemSearchAction.search();
	}

	public void showSoon() {
		resetAllForms();
		dvdSearchForm.setBlockbuster(true);
		dvdSearchForm.setReleaseDateAfter(dateTimeQueryHelper.getYesterday());
		basicSearchForm
				.setOrder(MbMediabaseSearchAction.ORDER_NUMBEROFWISHES_DESC);
		searchContext.setContextAlias("discover.dvd.soon");
		dvdItemSearchAction.search();
	}

	public void showCheaper() {
		resetAllForms();
		dvdSearchForm.setCheaper(true);
		basicSearchForm.setOrder(MbMediabaseSearchAction.ORDER_PRICE_DESC);
		searchContext.setContextAlias("discover.dvd.cheaper");
		dvdItemSearchAction.search();
	}

	public void showAllSoon() {
		resetAllForms();
		dvdSearchForm.setBlockbuster(false);
		dvdSearchForm.setReleaseDateAfter(dateTimeQueryHelper.getYesterday());
		basicSearchForm.setOrder(MbMediabaseSearchAction.ORDER_RELEASEDATE_ASC);
		searchContext.setContextAlias("discover.dvd.allsoon");
		dvdItemSearchAction.search();
	}

	public void showBRSoon() {
		resetAllForms();
		dvdSearchForm.setBlockbuster(false);
		dvdSearchForm.setMediatypeBR(true);
		dvdSearchForm.setMediatypeDVD(false);
		dvdSearchForm.setMediatypeHD(false);
		dvdSearchForm.setReleaseDateAfter(dateTimeQueryHelper.getYesterday());
		basicSearchForm
				.setOrder(MbMediabaseSearchAction.ORDER_NUMBEROFWISHES_DESC);
		searchContext.setContextAlias("discover.dvd.brsoon");
		dvdItemSearchAction.search();
	}

	public void showBlockbuster() {
		resetAllForms();
		dvdSearchForm.setBlockbuster(true);
		searchContext.setContextAlias("discover.dvd.blockbuster");
		dvdItemSearchAction.search();
	}

	public void resetSearch() {
		resetAllForms();
		searchContext.setContextAlias("discover.dvd.results");
	}

	public void showTop() {
		resetAllForms();
		basicSearchForm.setResultLimit(100);
		basicSearchForm.setOrder("numberOfOwnersDesc");
		searchContext.setContextAlias("discover.dvd.top");
		dvdItemSearchAction.search();
	}

	// reduced item search
	
	public void showReducedBlockbuster() {
		resetAllForms();
		basicSearchForm.setOrder("numberOfWishesDesc");
		searchContext.setContextAlias("discover.schnaeppchen.blockbuster");
		reducedItemSearchAction.search();
	}

	public void showReducedReleases() {
		resetAllForms();
		basicSearchForm.setOrder("numberOfWishesDesc");
		dvdSearchForm.setReleaseDateAfter(dateTimeQueryHelper.getOneMonthAgo());
		searchContext.setContextAlias("discover.schnaeppchen.blockbuster");
		reducedItemSearchAction.search();
	}

	public void showCremeReduction() {
		resetAllForms();
		basicSearchForm.setOrder("numberOfWishesDesc");
		dvdSearchForm.setRatingMastering(7);
		searchContext.setContextAlias("discover.schnaeppchen.creme");
		reducedItemSearchAction.search();
	}

	public void showAbsReduction() {
		resetAllForms();
		basicSearchForm.setOrder("priceChangeAbsDesc");
		searchContext.setContextAlias("discover.schnaeppchen.maxAbs");
		reducedItemSearchAction.search();
	}

	public void showRelReduction() {
		resetAllForms();
		basicSearchForm.setOrder("priceChangeRelDesc");
		searchContext.setContextAlias("discover.schnaeppchen.maxRel");
		reducedItemSearchAction.search();
	}

	public void showBluRayReduction() {
		resetAllForms();
		basicSearchForm.setOrder("numberOfWishesDesc");
		dvdSearchForm.setMediatypeBR(true);
		dvdSearchForm.setMediatypeDVD(false);
		dvdSearchForm.setMediatypeHD(false);
		searchContext.setContextAlias("discover.schnaeppchen.bluRay");
		reducedItemSearchAction.search();
	}

	private void resetAllForms() {
		dvdSearchForm.reset();
		basicSearchForm.reset();
	}
}
