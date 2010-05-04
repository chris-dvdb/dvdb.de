package de.dvdb.web.social.conversation;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.Log;

import de.dvdb.application.ApplicationSettings;
import de.dvdb.domain.model.social.Newsletter;
import de.dvdb.domain.model.user.User;
import de.dvdb.web.Actor;
import de.dvdb.web.search.BasicSearchForm;
import de.dvdb.web.search.Page;

@Name("newsletterAction")
@Scope(ScopeType.CONVERSATION)
public class NewsletterAction implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In
	Actor actor;

	@In
	EntityManager dvdb;

	@Out(scope = ScopeType.PAGE, value = "newsletterPage", required = false)
	protected Page page;

	@In(create = true)
	BasicSearchForm basicSearchForm;

	@In
	ApplicationSettings applicationSettings;

	@In
	FacesMessages facesMessages;

	@In(required = false)
	@Out(required = false)
	Newsletter selectedNewsletter;

	Long newsletterId;

	public void search() {

		StringBuilder sb = new StringBuilder();

		sb.append("  ");

		// select
		Query selectQuery = dvdb
				.createQuery("from Newsletter n order by n.dateOfLastEdit desc");

		// count
		Query countQuery = dvdb
				.createQuery("select count(n) from Newsletter n");

		this.page = new Page(selectQuery, countQuery, basicSearchForm
				.getPageNumber(), basicSearchForm.getPageSize(),
				basicSearchForm.getResultLimit());
	}

	@End
	public void save() {
		dvdb.persist(selectedNewsletter);

		facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_INFO,
				"mbadmin.editmic.action.add.success");

	}

	public void update() {
		selectedNewsletter.setDateOfLastEdit(new Date());
		dvdb.merge(selectedNewsletter);
	}

	@End
	public void remove() {
		dvdb
				.createQuery(
						"update User u set u.lastNewsletter = null where u.lastNewsletter = :lastNewsletter")
				.setParameter("lastNewsletter", selectedNewsletter)
				.executeUpdate();
		dvdb.remove(dvdb.find(Newsletter.class, selectedNewsletter.getId()));
	}

	@Factory("selectedNewsletter")
	@Begin(nested = true)
	public void loadNewsletter() {
		selectedNewsletter = dvdb.find(Newsletter.class, getNewsletterId());
	}

	@Begin(nested = true)
	public void createNewsletter() {
		selectedNewsletter = (Newsletter) Component.getInstance("newsletter",
				true);
	}

	public void invalid() {
		FacesMessages.instance().addFromResourceBundle(
				"mb.blog.post.action.error");
	}

	public void sendNewsletter() {

		List<User> users = dvdb
				.createQuery(
						"from User u where u.newsletter = true and (u.lastNewsletter != :newsletter or u.lastNewsletter is null)")
				.setParameter("newsletter", selectedNewsletter).setMaxResults(
						selectedNewsletter.getBatchSize()).getResultList();

		log.info("Sending newsletter to " + users.size() + " users");

		for (User user : users) {
			if (applicationSettings.isProduction())
				selectedNewsletter.setToEmail(user.getEmail());
			else
				selectedNewsletter.setToEmail("chris@dvdb.de");

			log.info("Sending newsletter to " + selectedNewsletter.getToEmail()
					+ " - production mode " + user.getEmail());

			// NewsletterAction na = (NewsletterAction) Component
			// .getInstance("newsletterAction");
			scheduleSend(selectedNewsletter);
			user.setLastNewsletter(selectedNewsletter);

		}
	}

	// @Asynchronous
	// public void scheduleSend(@Duration long delay, Newsletter newsletter) {
	// @Asynchronous
	public void scheduleSend(Newsletter newsletter) {
		try {
			Contexts.getEventContext().set("newsletter", selectedNewsletter);
			Renderer.instance().render(
					"/WEB-INF/mail/newsletter/template_de.xhtml");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Long getNewsletterId() {
		return newsletterId;
	}

	public void setNewsletterId(Long newsletterId) {
		this.newsletterId = newsletterId;
	}
}
