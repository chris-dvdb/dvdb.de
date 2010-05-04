package de.dvdb.web.social.blog;

import java.util.Date;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import de.dvdb.domain.model.item.Item;
import de.dvdb.web.Actor;
import de.dvdb.web.search.BasicSearchForm;
import de.dvdb.web.search.SearchContext;

@Name("preparedBlogSearchActions")
public class PreparedBlogSearchActions {

	@In(create = true)
	BasicSearchForm basicSearchForm;

	@In(create = true)
	BlogSearchForm blogSearchForm;

	@In(create = true)
	BlogAction blogAction;

	@In(create = true)
	SearchContext searchContext;

	@In(required = false)
	Item itemDetails;

	@In
	Actor actor;

	public void showAllActorsPosts() {
		resetAllForms();
		blogSearchForm.setPublished(null);
		blogSearchForm.setByUser(actor.getUser().getUsername());
		searchContext.setContextAlias("blog.showallactorspost");
		blogAction.search();
	}

	public void showLivePosts() {
		resetAllForms();
		blogSearchForm.setPublished(true);
		blogSearchForm.setByUser(null);
		blogSearchForm.setEndDate(new Date());
		searchContext.setContextAlias("blog.showliveposts");
		blogAction.search();
	}

	public void showPublishedActorsPosts() {
		resetAllForms();
		blogSearchForm.setPublished(true);
		blogSearchForm.setByUser(actor.getUser().getUsername());
		searchContext.setContextAlias("blog.showpublishedactorspost");
		blogAction.search();
	}

	public void showUnpublishedActorsPosts() {
		resetAllForms();
		blogSearchForm.setPublished(false);
		blogSearchForm.setByUser(actor.getUser().getUsername());
		searchContext.setContextAlias("blog.showunpublishedactorspost");
		blogAction.search();
	}

	private String username;

	public void showUsersPosts() {
		resetAllForms();
		blogSearchForm.setPublished(true);
		blogSearchForm.setByUser(username);
		searchContext.setContextAlias("blog.show.byuser");
		blogAction.search();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	private void resetAllForms() {
		blogSearchForm.reset();
		basicSearchForm.reset();
	}
}
