package de.dvdb.web.mediabase.action;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.mediabase.MediabaseItem;
import de.dvdb.web.Actor;

@Name("exportMediabaseAction")
public class ExportMediabaseAction implements Serializable {

	private static final long serialVersionUID = -5244432023472224176L;

	@Logger
	Log log;

	@In
	Actor actor;

	@In
	EntityManager dvdb;

	@Out
	List<MediabaseItem> exportedMediabaseItemCollectibles;

	@Out
	List<MediabaseItem> exportedMediabaseItemWishes;

	@SuppressWarnings("unchecked")
	@Restrict(value = "#{identity.loggedIn}")
	public void export() {

		exportedMediabaseItemWishes = (List<MediabaseItem>) dvdb
				.createQuery(
						"from MediabaseItemWish mi where mi.mediabase = :mediabase order by mi.item.titleLex")
				.setParameter("mediabase", actor.getUser().getMediabase())
				.getResultList();

		exportedMediabaseItemCollectibles = (List<MediabaseItem>) dvdb
				.createQuery(
						"from MediabaseItemCollectible mi left join fetch mi.item where mi.mediabase = :mediabase order by mi.item.titleLex")
				.setParameter("mediabase", actor.getUser().getMediabase())
				.getResultList();

	}
}
