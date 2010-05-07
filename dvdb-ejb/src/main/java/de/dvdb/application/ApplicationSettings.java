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

	private static final int NUMBER_PREFERENCES = 15;

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

	@Logger
	Log log;

	@In
	EntityManager dvdb;

	@In
	IdentityManager identityManager;

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

	@SuppressWarnings("unchecked")
	@Create
	@Transactional(TransactionPropagationType.REQUIRED)
	public void onCreate() {

		log.info("Initializing Application Settings");

		List<SystemPreference> prefs = dvdb
				.createQuery("from SystemPreference").getResultList();

		if (prefs.size() != NUMBER_PREFERENCES) {
			initPrefs();
		}

		prefs = dvdb.createQuery("from SystemPreference").getResultList();

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

	public void initPrefs() {

		dvdb.createQuery("delete from SystemPreference").executeUpdate();
		
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_MAIL_OUTGOING_FROMADDRESS, "development@dvdb.de"));
		dvdb.persist(new SystemPreference(
				ApplicationSettings.PREFERENCE_MAIL_OUTGOING_FROMNAME, "development@dvdb.de"));

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
