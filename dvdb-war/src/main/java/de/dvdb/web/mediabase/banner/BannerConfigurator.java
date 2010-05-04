package de.dvdb.web.mediabase.banner;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.mediabase.MediabaseBanner;
import de.dvdb.web.Actor;

@Name("bannerConfigurator")
@Scope(ScopeType.CONVERSATION)
public class BannerConfigurator implements Serializable {

	private static final long serialVersionUID = -2183381433283606816L;

	@Logger
	Log log;

	@In
	Actor actor;

	@In
	EntityManager dvdb;

	MediabaseBanner mediabaseBanner;

	@Factory("mediabaseBanner")
	@Begin
	public MediabaseBanner initMediabaseBanner() {
		try {
			mediabaseBanner = (MediabaseBanner) dvdb.createQuery(
					"from MediabaseBanner mb where mb.mediabase = :mediabase")
					.setParameter("mediabase", actor.getUser().getMediabase())
					.getSingleResult();
		} catch (NoResultException e) {
			mediabaseBanner = new MediabaseBanner();
			mediabaseBanner.setMediabase(actor.getUser().getMediabase());
			mediabaseBanner.setDisplayLatestDVD(true);
			mediabaseBanner.setBannerId(1l);
		}
		return mediabaseBanner;
	}

	@End
	public void persist() {
		if (mediabaseBanner.getId() == null)
			dvdb.persist(mediabaseBanner);
		else
			dvdb.merge(mediabaseBanner);
	}
}
