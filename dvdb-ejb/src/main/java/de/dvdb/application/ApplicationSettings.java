package de.dvdb.application;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.PasswordHash;

import de.dvdb.domain.model.item.type.DVDItem;
import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.mediabase.Skin;
import de.dvdb.domain.model.social.BlogEntry;
import de.dvdb.domain.model.user.User;
import de.dvdb.domain.model.user.UserRepository;
import de.dvdb.infrastructure.persistence.EntityMetadata;

@Name("applicationSettings")
@Scope(ScopeType.APPLICATION)
@Startup
@AutoCreate
public class ApplicationSettings implements Serializable {

	private static final long serialVersionUID = 1755776412011374856L;

	public static String TASK_REFRESHUSERS = "REFRESHUSERS";
	public static String TASK_REFRESHPRICES = "REFRESHPRICES";
	public static String TASK_MAINTAINITEMS = "MAINTAINITEMS";

	public static final String PREFERENCE_SYSTEM_ISPRODUCTION = "/system/isproduction";
	public static final String PREFERENCE_SYSTEM_MOVIEBASEIMAGEDIR = "/system/moviebaseImageDir";
	public static final String PREFERENCE_SYSTEM_SCRAPEASINS = "/system/scrapeAsins";
	public static final String PREFERENCE_SYSTEM_COVERIMAGEROOT = "/system/coverImageRoot";
	public static final String PREFERENCE_SYSTEM_REFRESHPRICES = "/system/refreshPrices";
	public static final String PREFERENCE_SYSTEM_MAINTAINITEMS = "/system/maintainItems";
	public static final String PREFERENCE_SYSTEM_RETRIEVECOVERS = "/system/retrieveCovers";
	public static final String PREFERENCE_SYSTEM_DOMAIN = "/system/domain";
	public static final String PREFERENCE_SYSTEM_PORT = "/system/port";
	public static final String PREFERENCE_SYSTEM_APPLICATIONCONTEXT = "/system/applicationContext";
	public static final String PREFERENCE_DEV_DEVELOPEREMAIL = "/dev/developerEmail";
	public static final String PREFERENCE_SEO_PRIMARYKEYWORD = "/seo/primaryKeyword";
	public static final String PREFERENCE_SEO_SECONDARYKEYWORD = "/seo/secondaryKeyword";

	public static final String PREFERENCE_MAIL_OUTGOING_FROMNAME = "/mail/outgoing/fromname";
	public static final String PREFERENCE_MAIL_OUTGOING_FROMADDRESS = "/mail/outgoing/fromaddress";

	public static String PREFERENCE_AMAZON_ACCESSKEY = "/amazon/accessKey";
	public static String PREFERENCE_AMAZON_SECRET = "/amazon/secretKey";
	public static String PREFERENCE_AMAZON_TAGID = "/amazon/tagId";
	public static String PREFERENCE_AMAZON_AMAZONMERCHANTID = "/amazon/merchantId";
	public static String PREFERENCE_AMAZON_PRODUCTURL = "/amazon/productUrl";
	public static String PREFERENCE_FACEBOOK_APIKEY = "/facebook/apiKey";
	public static String PREFERENCE_FACEBOOK_SECRET = "/facebook/secretKey";
	public static String PREFERENCE_FORUM_SECRET = "/forum/secret";
	public static String PREFERENCE_GOOGLE_MAPSKEY = "/google/mapsKey";
	public static String PREFERENCE_RECAPTCHA_PUBLICKEY = "/recaptcha/publicKey";
	public static String PREFERENCE_RECAPTCHA_PRIVATEKEY = "/recaptcha/privateKey";

	@Logger
	Log log;

	@In
	EntityManager dvdb;

	@In
	IdentityManager identityManager;

	public String getRecaptchaPublicKey() {
		return (String) features.get(PREFERENCE_RECAPTCHA_PUBLICKEY);
	}

	public String getRecaptchaPrivateKey() {
		return (String) features.get(PREFERENCE_RECAPTCHA_PRIVATEKEY);
	}

	public String getGoogleMapsKey() {
		return (String) features.get(PREFERENCE_GOOGLE_MAPSKEY);
	}

	public String getForumSecret() {
		return (String) features.get(PREFERENCE_FORUM_SECRET);
	}

	public String getFacebookApiKey() {
		return (String) features.get(PREFERENCE_FACEBOOK_APIKEY);
	}

	public String getFacebookSecret() {
		return (String) features.get(PREFERENCE_FACEBOOK_SECRET);
	}

	public String getAmazonAccessKey() {
		return (String) features.get(PREFERENCE_AMAZON_ACCESSKEY);
	}

	public String getAmazonSecret() {
		return (String) features.get(PREFERENCE_AMAZON_SECRET);
	}

	public String getAmazonTagId() {
		return (String) features.get(PREFERENCE_AMAZON_TAGID);
	}

	public String getAmazonMerchantId() {
		return (String) features.get(PREFERENCE_AMAZON_AMAZONMERCHANTID);
	}

	public String getAmazonProduktUrl() {
		return (String) features.get(PREFERENCE_AMAZON_PRODUCTURL);
	}

	public String getMailOutgoingFromAddress() {
		return (String) features.get(PREFERENCE_MAIL_OUTGOING_FROMADDRESS);
	}

	public String getMailOutgoingFromName() {
		return (String) features.get(PREFERENCE_MAIL_OUTGOING_FROMNAME);
	}

	public String getSystemPort() {
		return (String) features.get(PREFERENCE_SYSTEM_PORT);
	}

	public String getSystemDomain() {
		return (String) features.get(PREFERENCE_SYSTEM_DOMAIN);
	}

	public String getSystemApplicationContext() {
		return (String) features.get(PREFERENCE_SYSTEM_APPLICATIONCONTEXT);
	}

	public String getSeoPrimaryKeyword() {
		return (String) features.get(PREFERENCE_SEO_PRIMARYKEYWORD);
	}

	public String getSeoSecondaryKeyword() {
		return (String) features.get(PREFERENCE_SEO_SECONDARYKEYWORD);
	}

	public String getDevEmail() {
		return (String) features.get(PREFERENCE_DEV_DEVELOPEREMAIL);
	}

	public String getImageRoot() {
		return (String) features.get(PREFERENCE_SYSTEM_COVERIMAGEROOT);
	}

	public boolean getCheckImage() {
		return isProduction();
	}

	public boolean isProduction() {
		return (Boolean) features.get(PREFERENCE_SYSTEM_ISPRODUCTION);
	}

	public String getMoviebaseImageDir() {
		return (String) features.get(PREFERENCE_SYSTEM_MOVIEBASEIMAGEDIR);
	}

	public boolean getScrapeAsin() {
		return (Boolean) features.get(PREFERENCE_SYSTEM_SCRAPEASINS);
	}

	public boolean getRetrieveCovers() {
		return (Boolean) features.get(PREFERENCE_SYSTEM_RETRIEVECOVERS);
	}

	public boolean getMaintainItemsActive() {
		return (Boolean) features.get(PREFERENCE_SYSTEM_MAINTAINITEMS);
	}

	public boolean getRefreshPricesActive() {
		return (Boolean) features.get(PREFERENCE_SYSTEM_REFRESHPRICES);
	}

	private Map<String, Object> features = new HashMap<String, Object>();

	@In PasswordHash passwordHash;
	
	@Transactional(TransactionPropagationType.REQUIRED)
	public void convertUsers() {
		List<User> users = dvdb.createQuery(
				"from User u where passwordHash is null").setMaxResults(10000)
				.getResultList();
		for (User user : users) {
			try {
				String hash = passwordHash.generateSaltedHash(user.getPassword(),
						user.getUsername());
				user.setPasswordHash(hash);
				
			} catch (RuntimeException e) {
				log.error("Cannot convert user " + user + ". Username/password " + user.getUsername() + "/" + user.getPassword());
			}

		}
	}
	
	@SuppressWarnings("unchecked")
	@Create
	@Transactional(TransactionPropagationType.REQUIRED)
	public void onCreate() {

		log.info("Initializing Application Settings");

		List<SystemPreference> prefs = dvdb
				.createQuery("from SystemPreference").getResultList();

		for (SystemPreference systemPreference : prefs) {
			String value = systemPreference.getValue();
			Boolean bool = null;
			if (value.equalsIgnoreCase("true"))
				bool = true;
			else if (value.equalsIgnoreCase("false"))
				bool = false;
			if (bool != null) {
				features.put(systemPreference.getFeature().trim(), bool);
				System.out.println("Feature " + systemPreference.getFeature()
						+ ": " + bool);
			} else {
				features.put(systemPreference.getFeature().trim(), value);
				System.out.println("Feature " + systemPreference.getFeature()
						+ ": " + value);
			}
		}
	}

	public String getDvdDetailsURL(Item item) {
		if (item instanceof DVDItem) {
			DVDItem ditem = (DVDItem) item;

			if (ditem.getMediaType() == DVDItem.MEDIATYPE_BR)
				return "/dvdb/" + item.getURLTitle() + "/bluray/"
						+ item.getId() + "/index.html";
			else if (ditem.getMediaType() == DVDItem.MEDIATYPE_DVD)
				return "/dvdb/" + item.getURLTitle() + "/dvd/" + item.getId()
						+ "/index.html";
			else if (ditem.getMediaType() == DVDItem.MEDIATYPE_HD)
				return "/dvdb/" + item.getURLTitle() + "/hd/" + item.getId()
						+ "/index.html";
			else
				return "/dvdb/" + item.getURLTitle() + "/id/" + item.getId()
						+ "/index.html";

		} else {

			return "/dvdb/" + item.getURLTitle() + "/id/" + item.getId()
					+ "/index.html";
		}
	}

	public String getBlogEntryURL(BlogEntry entry) {
		return "http://www.dvdb.de/dvdb/blog/view.seam?blogEntryId="
				+ entry.getId() + "&username="
				+ entry.getMediabase().getUser().getUsername();
	}

	@Transactional(TransactionPropagationType.REQUIRED)
	public void initPrefs() {

		dvdb.createQuery("delete from SystemPreference").executeUpdate();

		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_MAIL_OUTGOING_FROMADDRESS,
				"development@dvdb.de"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_MAIL_OUTGOING_FROMNAME,
				"development@dvdb.de"));

		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_SYSTEM_DOMAIN, "localhost"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_SYSTEM_PORT, "8080"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_SYSTEM_APPLICATIONCONTEXT,
				"dvdb"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_SYSTEM_ISPRODUCTION, "false"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_SYSTEM_MOVIEBASEIMAGEDIR, ""));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_SYSTEM_SCRAPEASINS, "false"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_SYSTEM_COVERIMAGEROOT, ""));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_SYSTEM_REFRESHPRICES, "false"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_SYSTEM_MAINTAINITEMS, "false"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_SYSTEM_RETRIEVECOVERS, "false"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_DEV_DEVELOPEREMAIL,
				"chris@dvdb.de"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_SEO_PRIMARYKEYWORD,
				"DVD Sammlung"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_SEO_SECONDARYKEYWORD,
				"Deine DVD-Sammlung bei dvdb.de"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_AMAZON_ACCESSKEY, ""));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_AMAZON_AMAZONMERCHANTID,
				"A3JWKAKR8XB7XF"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_AMAZON_PRODUCTURL,
				"http://www.amazon.de/exec/obidos/ASIN/#ASIN#/vooshde-21"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_AMAZON_TAGID, "vooshde-21"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_AMAZON_SECRET, ""));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_FACEBOOK_APIKEY, ""));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_FACEBOOK_SECRET, ""));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_FORUM_SECRET, ""));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_GOOGLE_MAPSKEY, ""));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_RECAPTCHA_PRIVATEKEY, ""));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_RECAPTCHA_PUBLICKEY, ""));

	}

	public void initAdminUser() {

		// create admin user
		new RunAsOperation() {
			public void execute() {

				if (!identityManager.roleExists("admin"))
					identityManager.createRole("admin");

				if (!identityManager.roleExists("user"))
					identityManager.createRole("user");

				if (!identityManager.userExists("admin")) {
					identityManager
							.createUser("admin", "admin123", "Chris", "");
					identityManager.grantRole("admin", "admin");
				}

			}
		}.addRole("admin").run();

		User admin = userRepository.getUser("admin");

		EntityMetadata em = new EntityMetadata();
		em.setDateOfCreation(new Date());
		em.setDateOfLastUpdate(new Date());
		admin.setEntityMetadata(em);

		admin.setAdult(true);
		admin.setEmail("chris@dvdb.de");
		admin.setDateOfLastSignIn(new Date());
		admin.setNumberCollectibles(0);

		Skin skin = new Skin();
		skin.setName("default");
		skin.setTemplate0Columns("/WEB-INF/skins/dvdb3/template2cols.xhtml");
		skin.setTemplate2Columns("/WEB-INF/skins/dvdb3/template2cols.xhtml");
		skin.setTemplate3Columns("/WEB-INF/skins/dvdb3/template3cols.xhtml");
		dvdb.persist(skin);

		Mediabase mb = new Mediabase();
		mb.setUser(admin);
		mb.setSkin(skin);
		admin.setMediabase(mb);

	}

	@In
	UserRepository userRepository;
}
