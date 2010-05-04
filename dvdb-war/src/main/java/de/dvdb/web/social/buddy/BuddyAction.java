package de.dvdb.web.social.buddy;

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
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.AuthorizationException;

import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.message.BuddyAddMessage;
import de.dvdb.domain.model.social.Buddy;
import de.dvdb.domain.model.user.User;
import de.dvdb.web.Actor;
import de.dvdb.web.search.BasicSearchForm;
import de.dvdb.web.search.NewspaperPage;

@Name("buddyAction")
@Scope(ScopeType.CONVERSATION)
public class BuddyAction implements Serializable {

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

	@Out(scope = ScopeType.EVENT, value = "buddiesPage", required = false)
	NewspaperPage page;

	@In(required = false)
	@Out(required = false)
	Buddy selectedBuddy;

	Long buddyId;

	@RequestParameter
	Long userId;

	String searchString;

	@Begin(nested = true)
	public void createBuddy() {
		selectedBuddy = (Buddy) Component.getInstance("buddy", true);
		selectedBuddy.setOwner(actor.getUser());

		if (userId != null) {
			selectedBuddy.setUser(dvdb.find(User.class, userId));
			selectedBuddy.setName(selectedBuddy.getUser().getUsername());
		}

	}

	@Factory("selectedBuddy")
	@Begin(nested = true)
	public void loadBuddy() {
		selectedBuddy = dvdb.find(Buddy.class, getBuddyId());

		if (!selectedBuddy.getOwner().equals(actor.getUser())) {
			selectedBuddy = null;
			throw new AuthorizationException("Are you trying to manipulate?");
		}

	}

	@End
	public void remove() {
		Long borrowedDvds = (Long) dvdb
				.createQuery(
						"select count(mi) from MediabaseItemCollectible mi where mi.borrowedToBuddy = :buddy")
				.setParameter("buddy", selectedBuddy).getSingleResult();

		if (borrowedDvds > 0) {
			facesMessages
					.addFromResourceBundle("social.buddy.action.failure.borrowed");
			return;
		}

		dvdb.remove(dvdb.find(Buddy.class, selectedBuddy.getId()));
	}

	@SuppressWarnings("unchecked")
	@End
	public void save() {

		if (selectedBuddy.getUser() == null
				&& getSearchString() == null
				&& (selectedBuddy.getName() == null || selectedBuddy.getName()
						.equals(""))) {
			facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_WARN,
					"social.selectedBuddy.action.failure.nameMissing");
			return;
		}

		if (selectedBuddy.getUser() == null && !getSearchString().equals("")) {
			List<User> users = (List<User>) dvdb.createQuery(
					"from User u where u.username = :username").setParameter(
					"username", getSearchString()).getResultList();
			if (users.size() != 1) {
				facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_WARN,
						"social.selectedBuddy.action.search.failure");
				return;
			} else {
				selectedBuddy.setUser(users.get(0));
				selectedBuddy.setName(selectedBuddy.getUser().getUsername());
			}
		}

		boolean isAlreadyABuddy = dvdb
				.createQuery(
						"select count(buddy) from Buddy buddy where buddy.owner = :owner and buddy.user = :user")
				.setParameter("owner", actor.getUser()).setParameter("user",
						selectedBuddy.getUser()).getResultList().get(0).equals(
						new Long(1));
		if (isAlreadyABuddy) {
			facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_WARN,
					"social.buddy.action.persist.failure.alreadyDefined");
			return;
		}

		setSearchString(null);
		dvdb.persist(selectedBuddy);

		if (selectedBuddy.getUser() != null) {
			BuddyAddMessage bam = new BuddyAddMessage();
			bam.setSender(selectedBuddy.getOwner());
			bam.setRecipient(selectedBuddy.getUser());
			bam.setSubject("Du wurdest als Filmfreund aufgenommen");
			bam.setMessageDate(new Date());
			bam
					.setBody(String
							.format(
									"%s hat Dich als Filmfreund aufgenommen.\r\n\r\nDies ist eine systemgenerierte Nachricht. ",
									selectedBuddy.getOwner().getUsername()));
			bam.getRecipient().setDateOfLastMessage(bam.getMessageDate());
			dvdb.persist(bam);
		}

	}

	@SuppressWarnings("unchecked")
	@End
	public void update() {

		if (selectedBuddy.getUser() == null) {
			if (!("".equals(getSearchString()))) {
				List<User> users = (List<User>) dvdb.createQuery(
						"from User u where u.username = :username")
						.setParameter("username", getSearchString())
						.getResultList();
				if (users.size() != 1) {
					facesMessages.addFromResourceBundle(
							FacesMessage.SEVERITY_WARN,
							"social.buddy.action.search.failure");
					return;
				} else {
					selectedBuddy.setUser(users.get(0));
					if (selectedBuddy.getName() == null
							|| selectedBuddy.getName().equals(""))
						selectedBuddy.setName(selectedBuddy.getUser()
								.getUsername());
				}
			} else {
				return;
			}
		}

		dvdb.merge(selectedBuddy);
	}

	@SuppressWarnings("unchecked")
	public List<User> suggest(Object fragment) {

		return dvdb
				.createQuery(
						"from User u where u.username like :fragment order by u.username")
				.setMaxResults(10).setParameter("fragment", fragment + "%")
				.getResultList();

	}

	@Begin(join = true)
	public void search() {

		StringBuilder sb = new StringBuilder(
				"from Buddy buddy where buddy.owner = :owner order by buddy.name asc ");

		Query selectQuery = dvdb.createQuery(sb.toString()).setParameter(
				"owner", actor.getUser());

		sb = new StringBuilder(
				"select count(buddy) from Buddy buddy where buddy.owner = :owner ");

		Query countQuery = dvdb.createQuery(sb.toString()).setParameter(
				"owner", actor.getUser());

		page = new NewspaperPage(selectQuery, countQuery, basicSearchForm
				.getPageNumber(), basicSearchForm.getPageSize(),
				basicSearchForm.getResultLimit(), 5);

	}

	@In(required = false)
	Mediabase mediabase;

	@Begin(join = true)
	public void mediabaseSearch() {

		StringBuilder sb = new StringBuilder(
				"from Buddy buddy where buddy.owner = :owner order by buddy.name asc ");

		Query selectQuery = dvdb.createQuery(sb.toString()).setParameter(
				"owner", mediabase.getUser());

		sb = new StringBuilder(
				"select count(buddy) from Buddy buddy where buddy.owner = :owner ");

		Query countQuery = dvdb.createQuery(sb.toString()).setParameter(
				"owner", mediabase.getUser());

		page = new NewspaperPage(selectQuery, countQuery, basicSearchForm
				.getPageNumber(), basicSearchForm.getPageSize(),
				basicSearchForm.getResultLimit(), 5);
	}

	public Long getBuddyId() {
		return buddyId;
	}

	public void setBuddyId(Long buddyId) {
		this.buddyId = buddyId;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
