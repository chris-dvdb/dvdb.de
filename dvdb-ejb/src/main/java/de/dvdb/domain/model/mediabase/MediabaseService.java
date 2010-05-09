package de.dvdb.domain.model.mediabase;

import javax.ejb.Local;

import de.dvdb.domain.model.user.User;

@Local
public interface MediabaseService {

	public static final String EVENT_MEDIABASEREFRESHREQUIRED = "mediabaseRefreshRequired";

	public Mediabase getMediabase(String username);

	public void refreshMediabase(User user);
}
