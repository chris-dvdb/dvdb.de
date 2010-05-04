package de.dvdb.domain.model.forum;

import javax.ejb.Local;

import de.dvdb.domain.model.user.User;

@Local
public interface ForumService {

	public Long getNumberOfPostings(Integer threadid);

	public void registerUser(User user, String ip);

	public void updatePassword(User user);

	public Integer postThread(Integer forumId, String identifier,
			String subject, String message, Boolean simulate) throws Exception;

	public void postReply(Integer threadId, String subject, String message,
			Boolean simulate) throws Exception;

	public void setThreadDateToOld(Integer threadid);

}
