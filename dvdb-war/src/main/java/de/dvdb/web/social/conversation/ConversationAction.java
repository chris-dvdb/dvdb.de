package de.dvdb.web.social.conversation;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.message.TextMessage;
import de.dvdb.domain.model.user.User;
import de.dvdb.domain.shared.DvdbGlobals;
import de.dvdb.web.Actor;
import de.dvdb.web.mediabase.MediabaseFactory;

@Name("conversationAction")
public class ConversationAction implements Serializable {

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

	@In(create = true, value = "dvdb")
	protected EntityManager dvdb;

	@In(value = "userTextMessage", required = false)
	TextMessage userTextMessage;

	@In(value = "textMessage", required = false)
	TextMessage textMessage;

	@In(required = false)
	User recipient;

	@In
	DvdbGlobals dvdbGlobals;

	@In(required = false)
	MediabaseFactory mediabaseFactory;

	public String sendToChris() {
		textMessage.setRecipient(dvdbGlobals.getChris());
		sendIt(textMessage);
		return null;
	}

	public String sendToUser() {
		if (recipient == null)
			recipient = mediabaseFactory.getMediabase().getUser();
		userTextMessage.setRecipient(recipient);
		sendIt(userTextMessage);
		return null;
	}

	private void sendIt(TextMessage textMessage) {
		try {
			dvdb.persist(textMessage);
			Contexts.getConversationContext().set("textMessage", textMessage);
			renderer.render("/WEB-INF/mail/conversation/contact_de.xhtml");
			facesMessages.add("Danke fuer Deine Nachricht.");
			this.textMessage = null;
			this.userTextMessage = null;

		} catch (Exception e) {
			facesMessages.add("Deine Meldung konnte nicht versendet werden:"
					+ e.getMessage());
			e.printStackTrace();
		}
	}
}
