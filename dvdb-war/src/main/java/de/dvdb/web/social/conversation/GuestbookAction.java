package de.dvdb.web.social.conversation;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.message.GuestbookEntry;
import de.dvdb.domain.model.message.TextMessage;
import de.dvdb.web.Actor;
import de.dvdb.web.search.BasicSearchForm;
import de.dvdb.web.search.Page;

@Name("guestbookAction")
public class GuestbookAction implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In
	Actor actor;

	@In
	FacesMessages facesMessages;

	@In
	FacesContext facesContext;

	@In(create = true)
	Renderer renderer;

	@In(required = false)
	Mediabase mediabase;

	@In(create = true, value = "dvdb")
	protected EntityManager dvdb;

	@In(required = false, value = "guestbookEntry")
	GuestbookEntry guestbookEntry;

	@In(create = true)
	BasicSearchForm basicSearchForm;

	@Out(scope = ScopeType.EVENT, value = "guestbookPage", required = false)
	protected Page page;

	private Long guestbookEntryId;

	public void search() {

		StringBuilder sb = new StringBuilder(
				"from GuestbookEntry tm where tm.recipient = :recipient order by tm.messageDate desc ");

		appendRestrictions(sb);

		// do count
		Query selectQuery = dvdb.createQuery(sb.toString()).setParameter(
				"recipient", mediabase.getUser());

		sb = new StringBuilder(
				"select count(tm) from GuestbookEntry tm where tm.recipient = :recipient ");

		appendRestrictions(sb);

		Query countQuery = dvdb.createQuery(sb.toString()).setParameter(
				"recipient", mediabase.getUser());

		// create page
		page = new Page(selectQuery, countQuery, basicSearchForm
				.getPageNumber(), basicSearchForm.getPageSize(),
				basicSearchForm.getResultLimit());

	}

	private void appendRestrictions(StringBuilder sb) {

	}

	@Restrict(value = "#{identity.loggedIn}")
	public void deleteEntry() {
		TextMessage tm = dvdb.find(GuestbookEntry.class, getGuestbookEntryId());
		if (tm.getRecipient().equals(actor.getUser())) {
			dvdb.remove(tm);
			facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_INFO,
					"mb.guestbook.action.deleteGuestbookEntry.success");
		}
	}

	public void addMessage() {

		// try {
		if (guestbookEntry.getId() == null) {
			guestbookEntry.getRecipient().setDateOfLastMessage(
					guestbookEntry.getMessageDate());

			dvdb.persist(guestbookEntry);
		}
		basicSearchForm.setPageNumber(1);

	}

	public Long getGuestbookEntryId() {
		return guestbookEntryId;
	}

	public void setGuestbookEntryId(Long guestbookEntryId) {
		this.guestbookEntryId = guestbookEntryId;
	}

	@Begin(join = true)
	public Boolean getKeepAlive() {
		return true;
	}

}
