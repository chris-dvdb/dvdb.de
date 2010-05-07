package de.dvdb.web.social.blog;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;

import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.message.BlogEntryComment;
import de.dvdb.domain.model.social.BlogEntry;
import de.dvdb.domain.model.tag.BlogEntryTag;
import de.dvdb.domain.model.tag.Tag2;
import de.dvdb.domain.model.tag.TagForm;
import de.dvdb.domain.model.tag.TagManager;
import de.dvdb.domain.shared.DateTimeQueryHelper;
import de.dvdb.web.Actor;
import de.dvdb.web.item.ItemSearchComponent;
import de.dvdb.web.search.BasicSearchForm;
import de.dvdb.web.search.Page;
import de.dvdb.web.search.SearchContext;

@Name("blogAction")
@Scope(ScopeType.CONVERSATION)
public class BlogAction {

	@In
	EntityManager dvdb;

	@Logger
	Log log;

	@In(required = false)
	@Out(required = false)
	BlogEntry selectedBlogEntry;

	@In(create = true)
	BasicSearchForm basicSearchForm;

	@In(create = true)
	BlogSearchForm blogSearchForm;

	@In(required = false)
	@Out(required = false)
	BlogEntryComment selectedBlogEntryComment;

	@In(create = true)
	@Out
	TagForm tagForm;

	@In(create = true)
	SearchContext searchContext;

	@Out(scope = ScopeType.PAGE, value = "page", required = false)
	protected Page page;

	@In
	DateTimeQueryHelper dateTimeQueryHelper;

	@In
	Actor actor;

	@In
	Identity identity;

	Long blogEntryId;

	boolean livePreview = true;

	public BlogEntry loadBlogEntry(Long id) {

		if (id != null)
			return dvdb.find(BlogEntry.class, id);

		return null;

	}

	public void removeBlogEntry(BlogEntry blogEntry) {
		blogEntry = dvdb.find(BlogEntry.class, blogEntry.getId());

		tagManager.removeTagsForTagable(blogEntry);

		dvdb
				.createQuery(
						"delete from BlogEntryComment bec where bec.blogEntry = :blogEntry")
				.setParameter("blogEntry", blogEntry).executeUpdate();

		dvdb.remove(blogEntry);
	}

	public BlogEntry post(BlogEntry blogEntry) {

		blogEntry = dvdb.merge(blogEntry);

		return blogEntry;

	}

	@Factory("selectedBlogEntry")
	public void loadBlogEntry() {

		if (getBlogEntryId() != null)
			selectedBlogEntry = loadBlogEntry(getBlogEntryId());

		if (!selectedBlogEntry.getActive()
				&& (actor.isAnonymous() || !actor.getUser().equals(
						selectedBlogEntry.getMediabase().getUser()))) {
			throw new AuthorizationException("Are you trying to manipulate?");
		}

		initBlogEntryComment();
		tagForm.setTags(tagManager.getTagsForTagable(selectedBlogEntry));

	}

	@RequestParameter
	String tag;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String addTagToSearch() {
		if (!blogSearchForm.getTags().contains(tag))
			blogSearchForm.getTags().add(tag);
		return "/blog/results.xhtml";
	}

	public String removeTagFromSearch() {
		blogSearchForm.getTags().remove(tag);
		return "/blog/results.xhtml";
	}

	public String removeUserFromSearch() {
		blogSearchForm.setByUser(null);
		searchContext.setContextAlias("blog.results");
		return "/blog/results.xhtml";
	}

	public void recent() {

		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		sb.append(" from BlogEntry be where ");
		sb.append(" be.date < :now ");
		paramMap.put("now", new Date());
		sb.append(" and be.active = true ");

		// select
		Query selectQuery = dvdb.createQuery(sb.toString()
				+ " order by be.date desc");

		for (String key : paramMap.keySet())
			selectQuery.setParameter(key, paramMap.get(key));

		// count
		Query countQuery = dvdb
				.createQuery("select count(be) " + sb.toString());

		for (String key : paramMap.keySet())
			countQuery.setParameter(key, paramMap.get(key));

		this.page = new Page(selectQuery, countQuery, basicSearchForm
				.getPageNumber(), 5, 5);
	}

	@In(required = false)
	Item itemDetails;

	public void searchForItem(Item item) {

		if (item == null)
			return;

		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		sb.append(" from BlogEntry be where ");
		sb.append(" be.date < :now ");
		paramMap.put("now", new Date());
		sb.append(" and be.active = true ");
		sb.append(" and be.item.id = :itemId ");
		paramMap.put("itemId", Long.valueOf(item.getId()));

		// select
		Query selectQuery = dvdb.createQuery(sb.toString()
				+ " order by be.date desc");

		for (String key : paramMap.keySet())
			selectQuery.setParameter(key, paramMap.get(key));

		// count
		Query countQuery = dvdb
				.createQuery("select count(be) " + sb.toString());

		for (String key : paramMap.keySet())
			countQuery.setParameter(key, paramMap.get(key));

		this.page = new Page(selectQuery, countQuery, basicSearchForm
				.getPageNumber(), 5, 5);
	}

	public void search() {

		selectedBlogEntry = null;

		log.info("Searching for blogs with search form " + blogSearchForm);

		StringBuilder sb = new StringBuilder();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		sb.append(" from BlogEntry be where 1 = 1 ");

		if (blogSearchForm.getPublished() != null
				&& blogSearchForm.getPublished()) {
			sb.append(" and be.active = true");
		}

		if (blogSearchForm.getPublished() != null
				&& !blogSearchForm.getPublished()) {
			sb.append(" and be.active = false");
		}

		if (blogSearchForm.getStartDate() != null) {
			sb.append(" and be.date >= :startdate");
			paramMap.put("startdate", blogSearchForm.getStartDate());
		}

		if (blogSearchForm.getEndDate() != null) {
			sb.append(" and be.date <= :enddate");
			paramMap.put("enddate", blogSearchForm.getEndDate());
		}

		if (blogSearchForm.getByUser() != null) {
			sb.append(" and be.mediabase.user.username = :username");
			paramMap.put("username", blogSearchForm.getByUser());
		}

		if (blogSearchForm.getTags().size() > 0) {
			sb
					.append(" and be.id in (select bet.blogEntry.id from BlogEntryTag bet where bet.value in (:tags) group by bet.blogEntry.id having count(distinct bet.value) = :numbertags) ");
			paramMap.put("tags", blogSearchForm.getTags());
			paramMap.put("numbertags",
					new Long(blogSearchForm.getTags().size()));
		}

		if (searchContext.getContextAlias() == null) {
			searchContext.setContextAlias("blog.results");
		}

		// select
		Query selectQuery = dvdb.createQuery(sb.toString()
				+ " order by be.date desc");

		for (String key : paramMap.keySet())
			selectQuery.setParameter(key, paramMap.get(key));

		// select id
		Query selectIdQuery = dvdb.createQuery("select be.id " + sb.toString());
		for (String key : paramMap.keySet())
			selectIdQuery.setParameter(key, paramMap.get(key));

		availableTags = tagManager
				.getTagsForListOfTagables((List<Object>) selectIdQuery
						.getResultList());

		Collections.sort(availableTags);

		// count
		Query countQuery = dvdb
				.createQuery("select count(be) " + sb.toString());

		for (String key : paramMap.keySet())
			countQuery.setParameter(key, paramMap.get(key));

		this.page = new Page(selectQuery, countQuery, basicSearchForm
				.getPageNumber(), basicSearchForm.getPageSize(),
				basicSearchForm.getResultLimit());
	}

	@Out(required = false)
	List<String> availableTags;

	@In
	@Out
	ItemSearchComponent itemSearchComponent;

	@Restrict(value = "#{identity.loggedIn}")
	public void createBlogEntry() {

		selectedBlogEntry = new BlogEntry();
		selectedBlogEntry.setDate(new Date());
		if (actor.getUser() != null) {
			selectedBlogEntry.setMediabase(actor.getUser().getMediabase());
		}

		selectedBlogEntry.setItem(itemDetails);

		selectedBlogEntry.setActive(false);
		selectedBlogEntryComment = null;

	}

	@Restrict(value = "#{identity.loggedIn and (actor.user eq selectedBlogEntry.mediabase.user or identity.hasRole('superuser'))}")
	public void remove() {

		removeBlogEntry(selectedBlogEntry);
		this.selectedBlogEntry = null;
		selectedBlogEntryComment = null;
	}

	public void invalid() {
		FacesMessages.instance().addFromResourceBundle(
				"mb.blog.post.action.error");
	}

	@Restrict(value = "#{identity.loggedIn and (actor.user eq selectedBlogEntry.mediabase.user or identity.hasRole('superuser'))}")
	public void post() {

		selectedBlogEntry = post(selectedBlogEntry);

		initBlogEntryComment();

		for (Tag2 tag : tagForm.getTags()) {
			((BlogEntryTag) tag).setBlogEntry(selectedBlogEntry);
		}
		tagManager.replaceTagsForTagable(selectedBlogEntry, tagForm.getTags());

	}

	@Begin(join = true)
	public Boolean getKeepAlive() {
		return true;
	}

	@Out(scope = ScopeType.EVENT, value = "blogEntryCommentsPage", required = false)
	protected Page blogEntryCommentsPage;

	private Long blogEntryCommentId;

	public void removeItem() {
		selectedBlogEntry.setItem(null);
	}

	public void setItem(Item item) {
		selectedBlogEntry.setItem(item);
	}

	public void searchComments() {

		StringBuilder sb = new StringBuilder(
				"from BlogEntryComment tm where tm.blogEntry.id = :blogEntryId order by tm.messageDate desc ");

		appendRestrictions(sb);

		// do count
		Query selectQuery = dvdb.createQuery(sb.toString()).setParameter(
				"blogEntryId", blogEntryId);

		sb = new StringBuilder(
				"select count(tm) from BlogEntryComment tm where tm.blogEntry.id = :blogEntryId ");

		appendRestrictions(sb);

		Query countQuery = dvdb.createQuery(sb.toString()).setParameter(
				"blogEntryId", blogEntryId);

		// create page
		blogEntryCommentsPage = new Page(selectQuery, countQuery,
				basicSearchForm.getPageNumber(), basicSearchForm.getPageSize(),
				basicSearchForm.getResultLimit());
	}

	private void appendRestrictions(StringBuilder sb) {

	}

	// --------------------------------------------------------
	// --- tag stuff ------------------------------------------
	// --------------------------------------------------------

	@Restrict(value = "#{identity.loggedIn}")
	public void deleteEntry() {
		BlogEntryComment tm = dvdb.find(BlogEntryComment.class,
				getBlogEntryCommentId());

		if (tm.getRecipient().equals(actor.getUser())
				|| identity.hasRole("superuser")
				|| actor.getUser().equals(tm.getSender())) {

			dvdb.remove(tm);
			refreshCommentsCount(tm);
		}
	}

	private void refreshCommentsCount(BlogEntryComment erc) {
		BlogEntry be = dvdb.find(BlogEntry.class, erc.getBlogEntry().getId());
		be
				.setCountComments(((Long) dvdb
						.createQuery(
								"select count(bec) from BlogEntryComment bec where bec.blogEntry = :be")
						.setParameter("be", be).getSingleResult()).intValue());
	}

	public void addMessage() {
		if (selectedBlogEntryComment.getId() == null) {
			dvdb.persist(selectedBlogEntryComment);
			refreshCommentsCount(selectedBlogEntryComment);

			selectedBlogEntryComment.getRecipient().setDateOfLastMessage(
					selectedBlogEntryComment.getMessageDate());
		}

		initBlogEntryComment();

		basicSearchForm.setPageNumber(1);
	}

	private void initBlogEntryComment() {
		selectedBlogEntryComment = new BlogEntryComment();
		selectedBlogEntryComment.setBlogEntry(selectedBlogEntry);
		selectedBlogEntryComment.setSender(actor.getUser());
		selectedBlogEntryComment.setMessageDate(new Date());
		selectedBlogEntryComment.setRecipient(selectedBlogEntry.getMediabase()
				.getUser());

		if (!actor.isAnonymous()) {
			selectedBlogEntryComment.setSubject("Kommentar von "
					+ actor.getUser().getUsername() + " zu "
					+ selectedBlogEntry.getTitle());
		} else {
			selectedBlogEntryComment.setSubject("Anonymer Kommentar zu "
					+ selectedBlogEntry.getTitle());
		}
	}

	public Long getBlogEntryCommentId() {
		return blogEntryCommentId;
	}

	public void setBlogEntryCommentId(Long blogEntryCommentId) {
		this.blogEntryCommentId = blogEntryCommentId;
	}

	// --------------------------------------------------------
	// --- tag stuff ------------------------------------------
	// --------------------------------------------------------

	@In
	TagManager tagManager;

	public void removeBlogEntryTag() {
		log.info("Removing tag " + tag);

		int n = -1;
		for (int i = 0; i < tagForm.getTags().size(); i++) {
			if (tagForm.getTags().get(i).getValue().equals(this.tag)) {
				n = i;
				break;
			}
		}

		if (n > -1)
			tagForm.getTags().remove(n);
	}

	public void addNewBlogEntryTag() {

		log.info("Adding tag " + tagForm.getNewTag());

		if (tagForm.getNewTag() == null || tagForm.getNewTag().length() == 0)
			return;

		String[] tags = tagForm.getNewTag().split(",");
		for (String string : tags) {

			if (tagForm.containsTagValue(string))
				continue;

			BlogEntryTag bet = new BlogEntryTag();
			bet.setValue(string.trim().toLowerCase());
			bet.setBlogEntry(selectedBlogEntry);
			bet.setAlias(false);
			tagForm.getTags().add(bet);
		}
		tagForm.setNewTag("");
	}

	// --------------------------------------------------------
	// --- getter/setter --------------------------------------
	// --------------------------------------------------------

	public Long getBlogEntryId() {
		return blogEntryId;
	}

	public void setBlogEntryId(Long blogEntryId) {
		this.blogEntryId = blogEntryId;
	}

	public boolean isLivePreview() {
		return livePreview;
	}

	public void setLivePreview(boolean livePreview) {
		this.livePreview = livePreview;
	}
}
