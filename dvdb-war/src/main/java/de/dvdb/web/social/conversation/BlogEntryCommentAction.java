package de.dvdb.web.social.conversation;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;

import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.message.BlogEntryComment;
import de.dvdb.domain.model.social.BlogEntry;
import de.dvdb.web.Actor;
import de.dvdb.web.search.BasicSearchForm;
import de.dvdb.web.search.Page;

@Name("blogEntryCommentAction")
public class BlogEntryCommentAction implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In
	Actor actor;

	@In
	FacesMessages facesMessages;

	@In
	FacesContext facesContext;

	@In
	BlogEntry selectedBlogEntry;

	@In(create = true)
	Renderer renderer;

	@In(required = false)
	Mediabase mediabase;

	@In(create = true, value = "dvdb")
	protected EntityManager dvdb;

	@In(required = false)
	BlogEntryComment blogEntryComment;

	@In(create = true)
	BasicSearchForm basicSearchForm;

	@Out(scope = ScopeType.EVENT, value = "blogEntryCommentsPage", required = false)
	protected Page page;

	private Long blogEntryCommentId;

	public void search() {

		StringBuilder sb = new StringBuilder(
				"from BlogEntryComment tm where tm.blogEntry = :blogEntry order by tm.messageDate desc ");

		appendRestrictions(sb);

		// do count
		Query selectQuery = dvdb.createQuery(sb.toString()).setParameter(
				"blogEntry", selectedBlogEntry);

		sb = new StringBuilder(
				"select count(tm) from BlogEntryComment tm where tm.blogEntry = :blogEntry ");

		appendRestrictions(sb);

		Query countQuery = dvdb.createQuery(sb.toString()).setParameter(
				"blogEntry", selectedBlogEntry);

		// create page
		page = new Page(selectQuery, countQuery, basicSearchForm
				.getPageNumber(), basicSearchForm.getPageSize(),
				basicSearchForm.getResultLimit());
	}

	private void appendRestrictions(StringBuilder sb) {

	}

	@In
	Identity identity;

	@Restrict(value = "#{identity.loggedIn}")
	public void deleteEntry() {
		BlogEntryComment tm = dvdb.find(BlogEntryComment.class,
				getBlogEntryCommentId());

		if (tm.getRecipient().equals(actor.getUser())
				|| identity.hasRole("superuser")
				|| actor.getUser().equals(tm.getSender())) {

			dvdb.remove(tm);
			facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_INFO,
					"mb.guestbook.action.deleteGuestbookEntry.success");

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

		// try {
		if (blogEntryComment.getId() == null) {
			dvdb.persist(blogEntryComment);
			refreshCommentsCount(blogEntryComment);

			blogEntryComment.getRecipient().setDateOfLastMessage(
					blogEntryComment.getMessageDate());
		}

		basicSearchForm.setPageNumber(1);

		// Contexts.getConversationContext().set("textMessage", textMessage);
		// renderer.render("/WEB-INF/mail/conversation/contact_de.xhtml");
		// facesMessages.add("Danke fuer Deine Nachricht.");
		// this.textMessage = null;
		//
		// } catch (Exception e) {
		// facesMessages.add("Deine Meldung konnte nicht versendet werden:"
		// + e.getMessage());
		// e.printStackTrace();
		// }
	}

	public Long getBlogEntryCommentId() {
		return blogEntryCommentId;
	}

	public void setBlogEntryCommentId(Long blogEntryCommentId) {
		this.blogEntryCommentId = blogEntryCommentId;
	}

}
