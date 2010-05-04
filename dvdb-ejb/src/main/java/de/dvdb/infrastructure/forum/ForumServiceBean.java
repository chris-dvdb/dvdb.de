package de.dvdb.infrastructure.forum;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import de.dvdb.PartnerSecrets;
import de.dvdb.domain.model.forum.ForumService;
import de.dvdb.domain.model.forum.UserField;
import de.dvdb.domain.model.forum.UserTextfield;
import de.dvdb.domain.model.user.User;
import de.dvdb.domain.shared.StringUtils;

@Stateless
@Name("forumService")
@AutoCreate
public class ForumServiceBean implements ForumService, PartnerSecrets,
		Serializable {

	private static final long serialVersionUID = 965755125849259771L;

	protected Log log = LogFactory.getLog(ForumServiceBean.class.getName());

	// ------ Persistence Context Definitions --------

	@PersistenceContext(unitName = "forum")
	EntityManager forum;

	// -------- Business Methods Impl --------------

	@SuppressWarnings("unchecked")
	public Long getNumberOfPostings(Integer threadid) {

		List<BigInteger> postings = (List<BigInteger>) forum.createNativeQuery(
				"select replycount+1 from thread where threadid = :threadid")
				.setParameter("threadid", threadid).getResultList();

		if (postings.size() == 0)
			return 0l;
		return postings.get(0).longValue();

	}

	@SuppressWarnings("deprecation")
	public void registerUser(User user, String ip) {

		log.info("Creating forum account for " + user.getUsername());

		de.dvdb.domain.model.forum.User forumUser = new de.dvdb.domain.model.forum.User();
		forumUser.setEmail(user.getEmail());
		forumUser.setBirthdaySearch(user.getDateOfBirth());

		if (user.getDateOfBirth() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			forumUser.setBirthday(sdf.format(user.getDateOfBirth()));
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			forumUser.setBirthday(sdf.format(new Date(1900, 1, 1)));
			forumUser.setBirthdaySearch(new Date(1900, 1, 1));
		}

		String p1 = StringUtils.getMD5(user.getPassword());
		String p2 = StringUtils.getMD5(p1 + "dvd");
		forumUser.setPassword(p2);
		forumUser.setSalt("dvd");
		forumUser.setUsername(user.getUsername());
		forumUser.setUsergroupid((short) 2);

		long millisSince1970 = System.currentTimeMillis() / 1000;
		forumUser.setJoindate((int) millisSince1970);
		forumUser.setIpaddress(ip);
		forumUser.setLastvisit((int) millisSince1970);
		forumUser.setJoindate((int) millisSince1970);
		forumUser.setLastactivity((int) millisSince1970);
		forum.persist(forumUser);

		// forum.flush();

		forumUser = (de.dvdb.domain.model.forum.User) forum.createQuery(
				"from User u where u.username = :username").setParameter(
				"username", user.getUsername()).getSingleResult();
		Integer forumUserId = forumUser.getUserid();
		log.info("Forum user ID " + forumUserId);

		UserField uf = new UserField();
		uf.setUserid(forumUserId);
		uf.setField5("http://moviebase.dvdb.de/"
				+ user.getUsername().toLowerCase());
		forum.persist(uf);

		UserTextfield utf = new UserTextfield();
		utf.setUserid(forumUserId);
		utf.setSignature(user.getSignature());
		forum.persist(utf);
		forum.flush();
	}

	public void updatePassword(User user) {
		String p1 = StringUtils.getMD5(user.getPassword());
		String p2 = StringUtils.getMD5(p1 + "dvd");

		de.dvdb.domain.model.forum.User forumUser = (de.dvdb.domain.model.forum.User) forum
				.createQuery("from User u where u.username = :username")
				.setParameter("username", user.getUsername()).getSingleResult();
		forumUser.setPassword(p2);
		forumUser.setSalt("dvd");
		forumUser.setUsername(user.getUsername());
		forum.merge(forumUser);
	}

	public void setThreadDateToOld(Integer threadid) {
		forum.createNativeQuery(
				"update thread set lastpost = 1 where threadid = :id")
				.setParameter("id", threadid).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public Integer postThread(Integer forumId, String identifier,
			String subject, String message, Boolean simulate) throws Exception {

		if (simulate) {
			log.info("Running in simluation mode. In real mode I'd create a new post with subject "
					+ subject + " in forum " + forumId);
			return null;
		}

		HttpClient client = new HttpClient();
		client.getHostConfiguration().setHost("www.dvdb.de", 80, "http");
		PostMethod newThread = new PostMethod("/forum/newthread.php");

		NameValuePair f = new NameValuePair("f", "" + forumId);
		NameValuePair s = new NameValuePair("s", "");
		NameValuePair d = new NameValuePair("do", "postthread");
		NameValuePair psubject = new NameValuePair("subject", subject);
		NameValuePair iconid = new NameValuePair("iconid", "0");
		NameValuePair mode = new NameValuePair("mode", "0");
		NameValuePair pmessage = new NameValuePair("message", message);
		NameValuePair parseurl = new NameValuePair("parseurl", "1");
		NameValuePair username = new NameValuePair("username", "DVDB-Luise");
		NameValuePair secret = new NameValuePair("secret", FORUM_SECRET);
		// NameValuePair loggedinuser = new NameValuePair("loggedinuser", "0");

		newThread.setRequestBody(new NameValuePair[] { f, s, d, psubject,
				iconid, mode, pmessage, parseurl, username, secret });
		client.executeMethod(newThread);
		newThread.releaseConnection();

		// sleep 5 seconds!
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}

		List<Object> threadid = forum
				.createNativeQuery(
						"select threadid from thread where title like :identifier order by threadid desc")
				.setParameter("identifier", identifier).getResultList();

		if (threadid.size() == 0)
			return null;

		log.info("Created new thread " + threadid);
		return (Integer) threadid.get(0);
	}

	public void postReply(Integer threadId, String subject, String message,
			Boolean simulate) throws Exception {

		if (simulate) {
			log.info("Running in simluation mode. In real mode I'd create a new reply with subject "
					+ subject + " in thread " + threadId);
			return;
		}
		HttpClient client = new HttpClient();
		client.getHostConfiguration().setHost("www.dvdb.de", 80, "http");
		PostMethod newThread = new PostMethod("/forum/newreply.php");

		NameValuePair t = new NameValuePair("t", "" + threadId);
		NameValuePair s = new NameValuePair("s", "");
		NameValuePair d = new NameValuePair("do", "postreply");
		NameValuePair psubject = new NameValuePair("subject", subject);
		NameValuePair iconid = new NameValuePair("iconid", "0");
		NameValuePair mode = new NameValuePair("mode", "0");
		NameValuePair pmessage = new NameValuePair("message", message);
		NameValuePair parseurl = new NameValuePair("parseurl", "1");
		NameValuePair username = new NameValuePair("username", "Luise");
		NameValuePair secret = new NameValuePair("secret", FORUM_SECRET);

		newThread.setRequestBody(new NameValuePair[] { t, s, d, psubject,
				iconid, mode, pmessage, parseurl, username, secret });
		client.executeMethod(newThread);
		newThread.releaseConnection();

	}

}
