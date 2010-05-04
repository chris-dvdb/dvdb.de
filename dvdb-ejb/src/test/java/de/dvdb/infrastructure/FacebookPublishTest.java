package de.dvdb.infrastructure;

import junit.framework.TestCase;
import de.dvdb.application.ApplicationSettings;
import de.dvdb.domain.model.item.palace.PalaceDVDItem;
import de.dvdb.domain.model.social.FacebookSession;
import de.dvdb.domain.model.user.User;

public class FacebookPublishTest extends TestCase {

	long userId = 1358961757l;
	String sessionKey = "2.VM_ni7VeSJ6ZVl4HID2Slg__.3600.1265864400-1358961757";

	public void testA() {
	}

	public void _testPublish() throws Exception {

		ApplicationSettings appSet = new ApplicationSettings();

		PalaceDVDItem item = new PalaceDVDItem();
		item.setMediaType(PalaceDVDItem.MEDIATYPE_BR);
		item.setTitle("Das ist der Titel der neuen Blu-ray");
		item.setCountryAndYear("USA, 2009");
		item.setId(123l);
		item.setDvdId(54433l);
		User user = new User();
		user.setUsername("hannes");

		FacebookSession session = new FacebookSession();
		session.setSessionKey(sessionKey);
		session.setUser(userId);

		// FacebookController fbc = new FacebookController();

		// fbc.publish(user, session, (Item) item, appSet);

	}

}
