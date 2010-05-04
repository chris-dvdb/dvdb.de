package de.dvdb.infrastructure.persistence;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.PasswordHash;
import org.jboss.seam.ui.graphicImage.Image;

import de.dvdb.domain.model.forum.ForumService;
import de.dvdb.domain.model.mediabase.AccessLevelEnum;
import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.user.User;
import de.dvdb.domain.model.user.UserRepository;

@Stateless
@Local(UserRepository.class)
@Name("userRepository")
@AutoCreate
public class UserRepositoryJPA implements UserRepository, Serializable {

	private static final long serialVersionUID = 965755125849259771L;

	protected Log log = LogFactory.getLog(UserRepositoryJPA.class.getName());

	// ------ Persistence Context Definitions --------

	@PersistenceContext(unitName = "dvdb")
	EntityManager entityManager;

	@In
	ForumService forumService;

	@In
	IdentityManager identityManager;

	@In
	PasswordHash passwordHash;

	// -------- Business Methods Impl --------------

	@SuppressWarnings("unchecked")
	public User getUser(String username) {
		Query q = entityManager
				.createQuery("from User u where u.username = :username");
		q.setParameter("username", username);
		List<User> results = q.getResultList();

		if (results.size() == 0)
			return null;
		return (User) results.get(0);
	}

	public User getUser(Long id) {
		return entityManager.find(User.class, id);
	}

	public void deleteUser(User u) {
		User user = entityManager.find(User.class, u.getId());
		user.setUsername("GELOESCHT" + user.getId());
		user.setEmail("deleted@dvdb.de");
		user.setDateOfBirth(new Date());
		user.setSignature("");
		user.setFirstname("");
		user.setLastname("");
		user.setImageData(null);
		user.setNumberCollectibles(0);
		user.setNewsletter(false);
		entityManager.merge(user);

		Mediabase mediabase = user.getMediabase();

		if (mediabase == null)
			return; // this should never be true happen...

		mediabase.setAccessLevel(AccessLevelEnum.MYSELF);
		mediabase.setAddress(null);
		entityManager.merge(mediabase);
	}

	public void updateUser(User user) {
		entityManager.merge(user);
	}

	public void scaleDown(User user) throws IOException {

		if (user.getImageData() == null)
			return;

		Image img = new Image();
		img.setInput(user.getImageData());
		Image small = img.scaleToWidth(40);
		user.setImageDataSmall(small.getImage());
		updateUser(user);

	}

	public void changePassword(final User u, final String newPassword) {

		u.setPassword(newPassword);
		
		new RunAsOperation() {
			public void execute() {
				identityManager.changePassword(u.getUsername(), newPassword);
			}
		}.addRole("admin").run();

		forumService.updatePassword(u);
	}

}
