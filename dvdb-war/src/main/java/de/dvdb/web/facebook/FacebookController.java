package de.dvdb.web.facebook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.log.Log;

import com.google.code.facebookapi.Attachment;
import com.google.code.facebookapi.AttachmentMediaImage;
import com.google.code.facebookapi.BundleActionLink;
import com.google.code.facebookapi.FacebookXmlRestClient;

import de.dvdb.PartnerSecrets;
import de.dvdb.application.ApplicationSettings;
import de.dvdb.domain.model.item.palace.PalaceDVDItem;
import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.mediabase.MediabaseItem;
import de.dvdb.domain.model.social.FacebookSession;
import de.dvdb.domain.model.user.User;
import de.dvdb.web.Actor;

@Name("facebookController")
@AutoCreate
@Scope(ScopeType.CONVERSATION)
public class FacebookController implements Serializable, PartnerSecrets {

	private static final long serialVersionUID = -6227299519500898191L;

	@Logger
	Log log;

	@RequestParameter("fb_sig_profile_id")
	String fb_sig_profile_id;

	@RequestParameter("fb_sig_canvas_user")
	String fb_sig_canvas_user;

	@RequestParameter("fb_sig_user")
	String fb_sig_user;

	@Out(required = false)
	User user;

	@In
	EntityManager dvdb;

	@In
	Actor actor;

	@Out(required = false)
	List<MediabaseItem> items;

	public String handle() {

		log.info("Handling facebook request");

		Long displayUserId = getFacebookUserId();

		user = getUserByFacebookUserId(displayUserId);

		if (user != null) {
			items = getLatestItems(user);
			return null;
		}

		if (user == null
				&& (fb_sig_user != null || getFacebookUserId() == null))
			return "/facebook/connect.xhtml";

		else
			return "/facebook/auth.xhtml";

	}

	@RequestParameter
	String uid;
	@RequestParameter
	String username;
	@Out(required = false)
	Boolean connectResult;
	@Out(required = false)
	String connectError;

	public void connect() {

		User u = dvdb.find(User.class, actor.getUser().getId());

		if (!username.trim().equalsIgnoreCase(u.getUsername())) {
			connectResult = false;
			connectError = "Dein bei Facebook eingegebener dvdb.de Username stimmt nicht mit diesem Account ueberein. Bitte nochmal probieren.";
			return;
		}

		if (uid == null) {
			connectResult = false;
			connectError = "Facebook ID wurde nicht uebermittelt. ";
			return;
		}

		connectResult = true;
		u.setFacebookUserId(Long.parseLong(uid));
		actor.getUser().setFacebookUserId(u.getFacebookUserId());
		dvdb.merge(u);

	}

	@SuppressWarnings("unchecked")
	private User getUserByFacebookUserId(Long userId) {

		List<User> users = dvdb.createQuery(
				"from User u where u.facebookUserId = :uid").setParameter(
				"uid", userId).getResultList();
		if (users.size() == 0)
			return null;

		return users.get(0);
	}

	@RequestParameter
	String fb_sig_session_key;

	public void authorize() {
		log.info("Trying to authorize");
		if (fb_sig_session_key == null || fb_sig_user == null)
			return;

		List<FacebookSession> sessions = dvdb.createQuery(
				"from FacebookSession fb where fb.user = :uid").setParameter(
				"uid", Long.parseLong(fb_sig_user)).getResultList();
		if (sessions.size() == 0) {
			FacebookSession fb = new FacebookSession();
			fb.setSessionKey(fb_sig_session_key);
			fb.setUser(Long.parseLong(fb_sig_user));
			dvdb.persist(fb);
			log.info("Added session key %s for user %s", fb.getSessionKey(), fb
					.getUser());
		} else {
			FacebookSession fb = sessions.get(0);
			fb.setSessionKey(fb_sig_session_key);
			dvdb.merge(fb);
			log.info("Updated session key %s for user %s", fb.getSessionKey(),
					fb.getUser());
		}
	}

	private List<MediabaseItem> getLatestItems(User user) {
		return dvdb
				.createQuery(
						"from MediabaseItemCollectible mi join fetch mi.item where mi.mediabase = :mediabase order by mi.entityMetadata.dateOfCreation desc")
				.setParameter("mediabase", user.getMediabase()).setMaxResults(
						10).getResultList();
	}

	public Long getFacebookUserId() {

		String id;

		log.info("fb_sig_profile_id " + fb_sig_profile_id);
		log.info("fb_sig_canvas_user " + fb_sig_canvas_user);
		log.info("fb_sig_user " + fb_sig_user);

		id = fb_sig_profile_id;

		if (id == null)
			id = fb_sig_canvas_user;

		if (id == null && fb_sig_user != null)
			id = fb_sig_user;

		if (id == null && fb_sig_canvas_user != null)
			id = fb_sig_canvas_user;

		if (id == null)
			return null;

		return Long.parseLong(id);

	}

	public void publish(User user, FacebookSession fbSession, Item item,
			ApplicationSettings appSet) throws Exception {

		if (!(item instanceof PalaceDVDItem))
			return;

		PalaceDVDItem pItem = (PalaceDVDItem) item;

		if (pItem.getRequires18())
			return;

		String message = "hat eine neue DVD: ";

		if (pItem.getMediaType().equals(PalaceDVDItem.MEDIATYPE_BR)) {
			message = "hat eine neue Blu-ray: ";
		}

		message += pItem.getTitle();

		String dvdUrl = "http://www.dvdb.de" + appSet.getDvdDetailsURL(pItem);

		FacebookXmlRestClient client = new FacebookXmlRestClient(
				FACEBOOK_APIKEY, FACEBOOK_SECRET, fbSession.getSessionKey());
		Attachment att = new Attachment();
		att.setCaption(pItem.getCountryAndYear());
		att.setName(pItem.getTitle());
		att.setHref(dvdUrl);

		AttachmentMediaImage image = new AttachmentMediaImage();
		image.addImage(pItem.getUrlImageMedium(), dvdUrl);
		att.setMedia(image);

		BundleActionLink link1 = new BundleActionLink("Zur Moviebase",
				"http://moviebase.dvdb.de/" + user.getUsername());
		List<BundleActionLink> list = new ArrayList<BundleActionLink>();
		list.add(link1);

		client.stream_publish(message, att, list, null, fbSession.getUser());
	}
}
