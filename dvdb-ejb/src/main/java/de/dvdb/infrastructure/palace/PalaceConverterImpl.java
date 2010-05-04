package de.dvdb.infrastructure.palace;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import de.dvdb.application.ApplicationSettings;
import de.dvdb.application.tasks.ForumTasks;
import de.dvdb.domain.model.item.DVDItem;
import de.dvdb.domain.model.item.Dvdbase;
import de.dvdb.domain.model.item.ItemRepository;
import de.dvdb.domain.model.item.palace.PalaceConverter;
import de.dvdb.domain.model.item.palace.PalaceDVDItem;
import de.dvdb.domain.model.tag.GenreTag;
import de.dvdb.domain.model.tag.Tag;
import de.dvdb.domain.shared.StringUtils;
import de.dvdb.infrastructure.http.HttpService;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Local(PalaceConverter.class)
@Name("palaceConverter")
public class PalaceConverterImpl implements PalaceConverter, Serializable {

	private static final long serialVersionUID = 965755125849259771L;

	protected Log log = LogFactory.getLog(PalaceConverterImpl.class.getName());

	// ------ Persistence Context Definitions --------

	@PersistenceContext(unitName = "dvdb")
	EntityManager entityManager;

	@EJB
	ItemRepository itemService;

	@In(create = true)
	ForumTasks forumTasks;

	@In
	ApplicationSettings applicationSettings;

	// -------- Business Methods Impl --------------

	/**
	 * loescht dvds, die nicht mehr in dvdbase enthalten sind. bricht ab, wenn
	 * mehr als 1000 dvds geloescht werden wuerden TODO: vor loeschen eines
	 * ITEMS muessen erst alle exi. abh. geloescht werden
	 */
	@SuppressWarnings("unchecked")
	public void cleanUpDeletedItems() {
		List<PalaceDVDItem> items = entityManager.createQuery(
				"from PalaceDVDItem dvd " + "where dvd.dvdId not in "
						+ "(select dvd.id from Dvdbase dvd)").getResultList();

		if (items.size() > 1000) {
			log.warn("There are more than 1000 DVDs to be deleted from the system. Not possible!");
			return;
		}

		for (PalaceDVDItem item : items) {
			itemService.removeItem(item);
		}
	}

	@SuppressWarnings("unchecked")
	public void importUpdatedDVDs(Integer batchSize) {
		List<Dvdbase> dvds = entityManager
				.createNativeQuery(
						"select dvdbase.* from dvdbase as dvdbase "
								+ "left join dvdb2_item on dvdbase.id = dvdb2_item.source_dvdid "
								+ "where (dvdb2_item.source_dvdid is not null and dvdb2_item.item_type = 2 "
								+ " and (dvdb2_item.itemdateofdata < dvdbase.entryupdate "
								+ " or dvdb2_item.itemdateofdata is null)) or dvdb2_item.source_dvdid is null "
								+ " order by dvdb2_item.source_dvdid",
						Dvdbase.class).setMaxResults(batchSize).getResultList();

		log.info("Importing " + dvds.size() + " new/updated DVDs from Palace");

		for (Dvdbase dvd : dvds) {
			importDvdbase(dvd);
		}
	}

	public void importSingleDVD(Long id) {
		Dvdbase dvdbase = (Dvdbase) entityManager.createQuery(
				"from Dvdbase dvdbase where dvdbase.id = :id").setParameter(
				"id", id).getSingleResult();
		importDvdbase(dvdbase);
	}

	// -----------------------------------------------------------------------
	// --- private methods ---------------------------------------------------
	// -----------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	private void importDvdbase(Dvdbase dvdbase) {

		List<PalaceDVDItem> pitems = entityManager.createQuery(
				"from PalaceDVDItem item where item.dvdId = :dvdid")
				.setParameter("dvdid", dvdbase.getId()).getResultList();

		PalaceDVDItem dvdItem = null;

		if (pitems.size() == 1) {
			dvdItem = (PalaceDVDItem) pitems.get(0);
			updateSinglePalaceDVD(dvdItem, dvdbase);
			log.info("Updated " + dvdItem.getTitle());
		}

		else if (pitems.size() == 0) {
			dvdItem = new PalaceDVDItem();
			dvdItem.setDvdId(dvdbase.getId());
			dvdItem.setNumberOfOwners(0l);
			updateSinglePalaceDVD(dvdItem, dvdbase);
			log.info("Imported " + dvdItem.getTitle());
		}
	}

	private void updateSinglePalaceDVD(PalaceDVDItem dvdItem, Dvdbase dvd) {

		dvdItem.setTitle(dvd.getDvdtitle());
		dvdItem.setTitleLex(dvdItem.calcTitleLex(dvdItem.getTitle()));
		dvdItem.setActors(dvd.getActors());
		dvdItem.setDirectors(dvd.getRegie());
		dvdItem.setLength(dvd.getDvdlengthAsInteger());
		dvdItem.setRating(dvd.getDvdfsk());
		dvdItem.setEan(dvd.getEancode());
		dvdItem.setReleaseDate(dvd.getDvddateAsDate());
		dvdItem.setRegionCode(dvd.getRegionCodeAsChar());
		dvdItem.setDvdType(dvd.getDvdtyp());
		dvdItem.setPictureData(dvd.getBilddata());
		dvdItem.setAudioData(dvd.getTondata());
		dvdItem.setReReleaseDate(dvd.getRereleasedateAsDate());
		dvdItem.setRentalDate(dvd.getDvddaterentalAsDate());

		Date cinemaDate = dvd.getKinostartAsDate();
		if (cinemaDate != null)
			dvdItem.setCinemaDate(cinemaDate);

		dvdItem.setOriginalTitle(dvd.getDvdoriginaltitel());
		dvdItem.setCountryAndYear(dvd.getErscheinungsjahr());
		dvdItem.setAdditionalInfo(dvd.getZusatzinfo());
		dvdItem.setFeatures(StringUtils.ArrayToCSV(dvd.getFeaturesAsArray()));
		dvdItem.setSubtitles(StringUtils.ArrayToCSV(dvd.getSubtitlesAsArray()));
		dvdItem.setOscars(StringUtils.ArrayToCSV(dvd.getOscarsAsArray()));
		dvdItem.setYearOfOscars(dvd.getYearOfOscarsAsDate());
		dvdItem.setStudio(dvd.getStudio());
		dvdItem.setDvdCase(dvd.getDvdcase());
		dvdItem.setLabel(dvd.getLabel());
		dvdItem.setMediaType(dvd.getMediaTypeAsInteger());
		dvdItem.setIndiziert(dvd.getDvdindiziert());

		if (dvdItem.getId() == null) {
			dvdItem.setNumberOfOwners(0l);
			dvdItem.setNumberOfWishes(0l);
			dvdItem.setDvdId(dvd.getId());
			dvdItem.setItemDateOfImport(new Date());
			dvdItem.setItemDateOfUpdate(new Date());
			dvdItem.setItemDateOfData(dvd.getEntryupdate());
			entityManager.persist(dvdItem);
		} else {
			dvdItem.setItemDateOfUpdate(new Date());
			dvdItem.setItemDateOfData(dvd.getEntryupdate());
			entityManager.merge(dvdItem);
		}

		// updateForumIds(dvdItem);
		updateFullDVDItemData(dvdItem);
	}

	@SuppressWarnings("unchecked")
	private void updateForumIds(PalaceDVDItem dvdItem) {

		if (dvdItem.getForumThreadId() != null)
			return;
		List fids = entityManager.createNativeQuery(
				"select threadid from dvdb_dvd_threadid where dvd_id = :dvdid")
				.setParameter("dvdid", dvdItem.getDvdId()).getResultList();
		if (fids.size() == 1) {
			log.info("Found forum id " + fids.get(0) + " for dvd id "
					+ dvdItem.getDvdId());
			dvdItem.setForumThreadId(((Integer) fids.get(0)).longValue());
		} else if (fids.size() == 0) {
			try {
				// create new forum thread
				forumTasks.createPalaceDVDPosting(dvdItem);
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void updateFullDVDItemData(PalaceDVDItem dvdItem) {
		List<Dvdbase> dvds = entityManager.createQuery(
				"from Dvdbase dvdbase where dvdbase.id = :dvdcode")
				.setParameter("dvdcode", dvdItem.getDvdId()).getResultList();
		if (dvds.size() != 1)
			return;
		Dvdbase base = dvds.get(0);
		importDvdStory(base, dvdItem);

		List<Tag> tags = dvdItem.getTags();
		tags.clear();
		dvdItem.setTags(tags);

		addGenreTags(base.getGenreAsArray(), dvdItem, true);
	}

	private void addGenreTags(String[] tagArray, DVDItem item, Boolean isAlias) {

		// create tags required for this item
		for (int i = 0; i < tagArray.length; i++) {
			String string = tagArray[i];
			GenreTag tag = new GenreTag();
			tag.setItem(item);
			tag.setValue(string);
			tag.setAlias(isAlias);
			entityManager.persist(tag);
			item.getTags().add(tag);
			String tagString = item.getTagString();
			if (tagString == null)
				tagString = new String();

			if ((tagString.length() + 3 + string.length()) <= 255)
				tagString += " ; " + string;

			item.setTagString(tagString);
		}
	}

	private void importDvdStory(Dvdbase base, PalaceDVDItem item) {

		item.setPlot(fetchPlot(base.getId()));

	}

	public static Pattern PLOT_PATTERN = Pattern
			.compile(
					"table width=\"100%\" cellpadding=0 cellspacing=5><tr><td class=\"db_white_1\" align=\"justify\" valign=\"top\">(.*)</td><td class=\"db_white_1\" width=20>",
					Pattern.DOTALL);

	public String fetchPlot(Long palaceid) {
		String dvd = HttpService
				.retrieveHttpDocument("http://www.dvd-palace.de/dvd-datenbank/"
						+ palaceid + ".html");
		String plot = null;

		Matcher matcher = PLOT_PATTERN.matcher(dvd.replace("\n", "").replace(
				"\r", ""));
		if (matcher.find())
			plot = matcher.group(1);
		return plot;
	}
}
