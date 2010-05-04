package de.dvdb.domain.model.user;

import java.io.IOException;

public interface UserRepository {

	public void changePassword(final User u, final String newPassword);

	public void deleteUser(User u);

	public User getUser(String username);

	public User getUser(Long userId);

	public void updateUser(User user);

	public void scaleDown(User user) throws IOException;
}
