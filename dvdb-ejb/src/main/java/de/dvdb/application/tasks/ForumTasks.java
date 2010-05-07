package de.dvdb.application.tasks;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.Log;

import de.dvdb.application.ApplicationSettings;
import de.dvdb.domain.model.forum.ForumService;
import de.dvdb.domain.model.item.palace.PalaceDVDItem;
import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.user.User;

@Name("forumTasks")
@AutoCreate
public class ForumTasks implements Serializable {

	private static final long serialVersionUID = 2397643542368662677L;

	@Logger
	Log log;

	@In
	ForumService forumService;

	@In
	FacesMessages facesMessages;

	@In
	ApplicationSettings applicationSettings;

	@In
	EntityManager dvdb;

	@In
	EntityManager forum;

	@In
	Renderer renderer;

	public Long getNumberOfPostings(Integer threadid) {
		return forumService.getNumberOfPostings(threadid);
	}

	public void make18(User user) {
		forum.createNativeQuery(
				"update user set usergroupid = 8 where usergroupid = 2 and username = :username")
				.setParameter("username", user.getUsername()).executeUpdate();

		try {
			Contexts.getConversationContext().set("email", user.getEmail());

			Contexts.getConversationContext().set("username",
					user.getUsername());

			if (!applicationSettings.isProduction()) {
				Contexts.getConversationContext().set("email",
						applicationSettings.getDevEmail());
			}

			renderer.render("/WEB-INF/mail/18/18_de.xhtml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Integer createPalaceDVDPosting(PalaceDVDItem dvdItem)
			throws Exception {

		if (dvdItem.getForumThreadId() != null) {
			log.warn("Forum thread exists already for DVD "
					+ dvdItem.getTitle());
			return null;
		}

		int forumId = 35;

		if (dvdItem.getRequires18()) {
			forumId = 39;
		}

		Integer threadid = forumService.postThread(forumId, "%(#"
				+ dvdItem.getDvdId() + ")%", buildSubjectForDVD(dvdItem),
				buildMessageForDVD(dvdItem), !applicationSettings
						.isProduction());

		if (threadid != null)
			forumService.setThreadDateToOld(threadid);
		return threadid;

	}

	private String buildSubjectForDVD(PalaceDVDItem dvd) {
		int length = Math.min(85, dvd.getTitleWithMediatype().length());
		return String.format("%s (#%s)", dvd.getTitleWithMediatype().substring(
				0, length), dvd.getDvdId());
	}

	private String buildMessageForDVD(Item item) {

		return String
				.format(
						"Diskutiert hier ueber '%s' und findet alle Infos zu [item]%s[/item].\n\rViel Spass!",
						item.getTitleWithMediatype(), item.getId());

	}
}
