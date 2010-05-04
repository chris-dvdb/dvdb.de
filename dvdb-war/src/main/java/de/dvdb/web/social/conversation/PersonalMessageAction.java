package de.dvdb.web.social.conversation;

import java.io.Serializable;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.AuthorizationException;

import de.dvdb.domain.model.message.TextMessage;
import de.dvdb.domain.model.user.User;
import de.dvdb.web.Actor;
import de.dvdb.web.search.BasicSearchForm;
import de.dvdb.web.search.Page;
import de.dvdb.web.user.UserSearchComponent;

@Name("personalMessageAction")
@Scope(ScopeType.CONVERSATION)
public class PersonalMessageAction implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In
	Actor actor;

	@In
	FacesMessages facesMessages;

	@In
	EntityManager dvdb;

	@In(create = true)
	BasicSearchForm basicSearchForm;

	@Out(scope = ScopeType.EVENT, value = "inboxPage", required = false)
	Page page;

	@Out(scope = ScopeType.EVENT, value = "outboxPage", required = false)
	Page pageOutbox;

	@In(required = false)
	@Out(required = false)
	TextMessage selectedMessage;

	@RequestParameter
	Long recipientId;

	Long messageId;

	@Begin(nested = true)
	public void createMessage() {
		selectedMessage = new TextMessage();
		selectedMessage.setSender(actor.getUser());

		if (recipientId != null) {
			selectedMessage.setRecipient(dvdb.find(User.class, recipientId));
		}
	}

	public void replyMessage() {
		String subject = "Re: "
				+ selectedMessage.getSubject().replaceAll("Re: ", "");
		subject = subject.substring(0, Math.min(subject.length(), 254));

		User rec = selectedMessage.getSender();

		selectedMessage = new TextMessage();
		selectedMessage.setSubject(subject);
		selectedMessage.setSender(actor.getUser());
		selectedMessage.setRecipient(rec);
	}

	@Begin(join = true)
	public Boolean getKeepAlive() {
		return true;
	}

	@Factory("selectedMessage")
	@Begin(nested = true)
	public void loadMessage() {
		if (getMessageId() != null) {
			selectedMessage = dvdb.find(TextMessage.class, getMessageId());
			if (!selectedMessage.getRecipient().equals(actor.getUser())
					&& !selectedMessage.getSender().equals(actor.getUser())) {
				selectedMessage = null;
				throw new AuthorizationException(
						"Are you trying to manipulate?");
			}
			selectedMessage.setMessageRead(true);
		}
	}

	@End
	public void remove() {
		dvdb.remove(dvdb.find(TextMessage.class, selectedMessage.getId()));
	}

	@In
	UserSearchComponent userSearchComponent;

	public void send() {

		if (selectedMessage.getRecipient() == null)

			try {
				selectedMessage.setRecipient(userSearchComponent.getUser());
			} catch (NoResultException e) {
			}

		if (selectedMessage.getRecipient() == null) {
			facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_INFO,
					"social.pm.write.send.failure.noUser");
			return;
		}

		selectedMessage.setMessageDate(new Date());

		selectedMessage.getRecipient().setDateOfLastMessage(
				selectedMessage.getMessageDate());

		dvdb.persist(selectedMessage);

		facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_INFO,
				"social.pm.write.send.success");

		Conversation.instance().end();
	}

	public void check() {
		if (actor.isAnonymous())
			return;
		User user = dvdb.find(User.class, actor.getUser().getId());
		actor.getUser().setDateOfLastMessage(user.getDateOfLastMessage());
	}

	@Begin(join = true)
	public void searchInbox() {

		StringBuilder sb = new StringBuilder(
				"from TextMessage tm where tm.recipient = :recipient order by tm.messageDate desc ");

		Query selectQuery = dvdb.createQuery(sb.toString()).setParameter(
				"recipient", actor.getUser());

		sb = new StringBuilder(
				"select count(tm) from TextMessage tm where tm.recipient = :recipient ");

		Query countQuery = dvdb.createQuery(sb.toString()).setParameter(
				"recipient", actor.getUser());

		page = new Page(selectQuery, countQuery, basicSearchForm
				.getPageNumber(), basicSearchForm.getPageSize(),
				basicSearchForm.getResultLimit());

		if (basicSearchForm.getPageNumber() == null
				|| basicSearchForm.equals(1)) {
			actor.getUser().setLastInboxVisit(new Date());
			dvdb.merge(actor.getUser());
		}

	}

	@Begin(join = true)
	public void searchOutbox() {

		StringBuilder sb = new StringBuilder(
				"from TextMessage tm where tm.sender = :sender order by tm.messageDate desc ");

		Query selectQuery = dvdb.createQuery(sb.toString()).setParameter(
				"sender", actor.getUser());

		sb = new StringBuilder(
				"select count(tm) from TextMessage tm where tm.sender = :sender ");

		Query countQuery = dvdb.createQuery(sb.toString()).setParameter(
				"sender", actor.getUser());

		pageOutbox = new Page(selectQuery, countQuery, basicSearchForm
				.getPageNumber(), basicSearchForm.getPageSize(),
				basicSearchForm.getResultLimit());

	}

	public Long getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(Long recipientId) {
		this.recipientId = recipientId;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

}
