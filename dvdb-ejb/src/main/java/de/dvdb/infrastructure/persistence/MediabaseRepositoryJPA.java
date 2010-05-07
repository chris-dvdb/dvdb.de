package de.dvdb.infrastructure.persistence;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;

import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.mediabase.MediabaseItem;
import de.dvdb.domain.model.mediabase.MediabaseService;
import de.dvdb.domain.model.user.User;

@Stateless
@Name("mediabaseService")
public class MediabaseRepositoryJPA implements MediabaseService, Serializable {

	private static final long serialVersionUID = 965755125849259771L;

	protected Log log = LogFactory.getLog(MediabaseRepositoryJPA.class
			.getName());

	// ------ Persistence Context Definitions --------

	@PersistenceContext(unitName = "dvdb")
	EntityManager entityManager;

	@PersistenceContext(unitName = "forum")
	EntityManager forum;

	// -------- Business Methods Impl --------------

	@SuppressWarnings("unchecked")
	public Mediabase getMediabase(String username) {
		Query q = entityManager
				.createQuery("select distinct(mediabase) from Mediabase mediabase "
						+ "where mediabase.user.username = :username");
		q.setParameter("username", username);
		List<Mediabase> l = q.getResultList();
		if (l.size() != 1)
			return null;
		return (Mediabase) l.get(0);
	}

	@Observer(value = EVENT_MEDIABASEREFRESHREQUIRED)
	public void refreshMediabase(User user) {
		log.info("Refreshing Mediabase statisics");
		Mediabase mediabase = user.getMediabase();
		mediabase = entityManager.find(Mediabase.class, user.getMediabase()
				.getId());
		Long l = (Long) entityManager
				.createQuery(
						"select count(mic) from MediabaseItemCollectible mic where mic.mediabase = :mediabase")
				.setParameter("mediabase", mediabase).getSingleResult();
		mediabase.setNumberCollectibles(l.intValue());
		mediabase.getUser().setNumberCollectibles(
				mediabase.getNumberCollectibles());

		log.info("Number of coll. " + l.intValue());
		l = (Long) entityManager
				.createQuery(
						"select count(mic) from MediabaseItemCollectible mic where mic.mediabase = :mediabase and mic.borrowedToBuddy is not null")
				.setParameter("mediabase", mediabase).getSingleResult();
		mediabase.setNumberBorrowed(l.intValue());
		l = (Long) entityManager
				.createQuery(
						"select count(mic) from MediabaseItemCollectible mic where mic.mediabase = :mediabase and mic.status = :status")
				.setParameter("mediabase", mediabase).setParameter("status",
						MediabaseItem.STATUS_ORDERED).getSingleResult();
		mediabase.setNumberOrdered(l.intValue());
		l = (Long) entityManager
				.createQuery(

				"select count(mic) from MediabaseItemWish mic where mic.mediabase = :mediabase")
				.setParameter("mediabase", mediabase).getSingleResult();
		mediabase.setNumberWishes(l.intValue());

		List<Item> i = (List<Item>) entityManager
				.createQuery(
						"select mbi.item from MediabaseItemCollectible mbi where mbi.mediabase = :mb order by mbi.entityMetadata.dateOfCreation desc")
				.setParameter("mb", mediabase).setMaxResults(1).getResultList();
		if (i.size() == 1)
			mediabase.setLastItem(i.get(0));

		entityManager.merge(mediabase);

		user.setMediabase(mediabase);

		forum.createNativeQuery(
				"update userfield,user set field7 = :dvdcount where user.userid = userfield.userid and user.username = :username")
				.setParameter("dvdcount", mediabase.getNumberCollectibles())
				.setParameter("username", user.getUsername()).executeUpdate();
	}
}
