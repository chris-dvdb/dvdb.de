package de.dvdb.application.tasks;

import de.dvdb.domain.model.item.palace.PalaceDVDItem;
import de.dvdb.domain.model.user.User;

public interface ForumTasks {

	public Long getNumberOfPostings(Integer threadid);
	
	public void make18(User user);

	public Integer createPalaceDVDPosting(PalaceDVDItem dvdItem)
			throws Exception;
}