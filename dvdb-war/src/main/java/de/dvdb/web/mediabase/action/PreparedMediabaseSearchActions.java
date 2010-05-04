package de.dvdb.web.mediabase.action;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import de.dvdb.domain.model.mediabase.MediabaseItemWish;
import de.dvdb.domain.shared.DateTimeQueryHelper;
import de.dvdb.web.item.action.DVDSearchForm;
import de.dvdb.web.mediabase.MediabaseItemSearchForm;
import de.dvdb.web.search.BasicSearchForm;
import de.dvdb.web.search.SearchContext;

@Name("preparedMediabaseSearchActions")
public class PreparedMediabaseSearchActions {

	@In(create = true)
	BasicSearchForm basicSearchForm;

	@In(create = true)
	DVDSearchForm dvdSearchForm;

	@In(create = true)
	MediabaseItemSearchForm mediabaseItemSearchForm;

	@In(create = true)
	SearchContext searchContext;

	@In(create = true)
	DateTimeQueryHelper dateTimeQueryHelper;

	public void showCollection() {
		resetAllForms();
		searchContext.setContextAlias("manage.collection");
	}

	public void showMediabaseCollection() {
		showCollection();
		searchContext.setContextAlias("mb.collection");
	}

	public void showWishlist() {
		resetAllForms();
		mediabaseItemSearchForm
				.setMediabaseItemClassName(MediabaseItemWish.class.getName());
		searchContext.setContextAlias("manage.wishlist");
	}

	public void showMediabaseWishlist() {
		showWishlist();
		searchContext.setContextAlias("mb.wishlist");
	}

	public void showOrders() {
		resetAllForms();
		mediabaseItemSearchForm.setPreOrdered(true);
		searchContext.setContextAlias("manage.orders");
	}

	public void showMediabaseOrders() {
		showOrders();
		searchContext.setContextAlias("mb.orders");
	}

	public void showBorrowed() {
		resetAllForms();
		mediabaseItemSearchForm.setBorrowed(true);
		searchContext.setContextAlias("manage.borrowed");
	}

	public void showMediabaseBorrowed() {
		showBorrowed();
		searchContext.setContextAlias("mb.borrowed");
	}

	public void showMediabaseTopN() {
		resetAllForms();
		mediabaseItemSearchForm.setTopN(true);
		basicSearchForm
				.setOrder(MediabaseItemSearchAction.ORDER_TOPNPOSITION_ASC);
		searchContext.setContextAlias("mb.top");
	}

	public void showMediabaseNewReleases() {
		resetAllForms();
		dvdSearchForm.setReleaseDateAfter(dateTimeQueryHelper
				.getThreeMonthAgo());
		searchContext.setContextAlias("mb.releases");
	}

	private void resetAllForms() {
		mediabaseItemSearchForm.reset();
		dvdSearchForm.reset();
		basicSearchForm.reset();
	}

	public void resetSearch() {
		resetAllForms();
		searchContext.setContextAlias("mb.search.results");
	}

}
