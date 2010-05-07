package de.dvdb.domain.model.item.type;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "dvdbase")
public class Dvdbase implements Serializable {
	private static final long serialVersionUID = 3833189124977801528L;

	private Long id;

	// 0-0031
	// private String dvdcode;

	// 100 Jahre - Die großen Bilder unseres Jahrhunderts,1940-1959
	private String dvdtitle;

	// o. A., 16, Ungeprueft, JK/SPIO
	private String dvdfsk;

	private String dvdlength;

	// keine Angabe, Paul May
	private String regie;

	// Hanley, Michael Eisenstein, Tim Bennett, Gil Junger, Daniel Kountz, Alisa
	// Mackay, Amber Matthews, Bridget O'Neill
	private String actors;

	// 30.11.2000, out now, geplant, xx.01.2003
	private String dvddate;

	// 0, 2
	private String regioncode;

	// 02, 99, 01
	private String dvdtyp;

	// 4:3 Vollbild (1.33:1)
	private String bilddata;

	// Deutsch: Dolby Digital 2.0 Stereo
	// Russisch: Dolby Digital 2.0 Stereo
	// Englisch: Dolby Digital 2.0 Stereo
	private String tondata;

	// leerstring, 26.10.1995
	private String kinostart;

	private String rereleasedate;

	// leerstring, 16.08.2001
	private String dvddaterental;

	// 0, 1
	private Boolean dvdindiziert;

	// 100 Jahre - Die großen Bilder unseres Jahrhunderts
	private String dvdoriginaltitel;

	// Großbritannien / USA / Frankreich / Spanien 1992
	private String erscheinungsjahr;

	// Hintergrundinformationen, Bewegtmenü, Soundtrack
	private String morefeatures;

	// Erstauflage inklusive Hologramm mit Micky Maus als "Zauberlehrling" auf
	// der Coverrückseite
	private String zusatzinfo;

	// 010101100000011000001000000000
	private String features;

	// 00000000000000000001000000000000000
	private String subtitles;

	// 100000000010011000000000000000
	private String katoscar;

	// 4006680019970
	private String eancode;

	// 1952
	private String jahroscar;

	// 005
	private String genre;

	// leerstring, 036
	private String genre1;

	private String genre2;

	// 002
	private String dvdcase;

	// 038
	private String studio;

	// 001
	private String label;

	// 1, 3, 4
	private String mediatype;

	// 626574746572 736578 6c696e65 313032 6c6965626573706f736974696f6e656e
	private String ftscoding;

	// leerstring, 4
	private String anzahloscar;

	private String limited;

	private Date entryupdate;

	@javax.persistence.Id
	@javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
	@javax.persistence.Column(name = "id", nullable = false, insertable = true, updatable = true)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// public String getDvdcode() {
	// return dvdcode;
	// }
	//
	// public void setDvdcode(String dvdCode) {
	// this.dvdcode = dvdCode;
	// }

	public String getDvdtitle() {
		return dvdtitle;
	}

	public void setDvdtitle(String dvdtitle) {
		this.dvdtitle = dvdtitle;
	}

	public String getEancode() {
		return eancode;
	}

	public void setEancode(String eancode) {
		this.eancode = eancode;
	}

	public String getActors() {
		return actors;
	}

	public void setActors(String actors) {
		this.actors = actors;
	}

	public String getAnzahloscar() {
		return anzahloscar;
	}

	public void setAnzahloscar(String anzahloscar) {
		this.anzahloscar = anzahloscar;
	}

	public String getBilddata() {
		return bilddata;
	}

	public void setBilddata(String bilddata) {
		this.bilddata = bilddata;
	}

	public String getDvdcase() {
		return dvdcase;
	}

	public void setDvdcase(String dvdcase) {
		this.dvdcase = dvdcase;
	}

	public String getDvddate() {
		return dvddate;
	}

	public void setDvddate(String dvddate) {
		this.dvddate = dvddate;
	}

	public String getDvddaterental() {
		return dvddaterental;
	}

	public void setDvddaterental(String dvddaterental) {
		this.dvddaterental = dvddaterental;
	}

	public String getDvdfsk() {
		return dvdfsk;
	}

	public void setDvdfsk(String dvdfsk) {
		this.dvdfsk = dvdfsk;
	}

	public Boolean getDvdindiziert() {
		return dvdindiziert;
	}

	public void setDvdindiziert(Boolean dvdindiziert) {
		this.dvdindiziert = dvdindiziert;
	}

	public String getDvdlength() {
		return dvdlength;
	}

	public void setDvdlength(String dvdlength) {
		this.dvdlength = dvdlength;
	}

	public String getDvdoriginaltitel() {
		return dvdoriginaltitel;
	}

	public void setDvdoriginaltitel(String dvdoriginaltitel) {
		this.dvdoriginaltitel = dvdoriginaltitel;
	}

	public String getDvdtyp() {
		return dvdtyp;
	}

	public void setDvdtyp(String dvdtyp) {
		this.dvdtyp = dvdtyp;
	}

	public String getErscheinungsjahr() {
		return erscheinungsjahr;
	}

	public void setErscheinungsjahr(String erscheinungsjahr) {
		this.erscheinungsjahr = erscheinungsjahr;
	}

	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}

	public String getFtscoding() {
		return ftscoding;
	}

	public void setFtscoding(String ftscoding) {
		this.ftscoding = ftscoding;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getGenre1() {
		return genre1;
	}

	public void setGenre1(String genre1) {
		this.genre1 = genre1;
	}

	public String getGenre2() {
		return genre2;
	}

	public void setGenre2(String genre2) {
		this.genre2 = genre2;
	}

	public String getJahroscar() {
		return jahroscar;
	}

	public void setJahroscar(String jahroscar) {
		this.jahroscar = jahroscar;
	}

	public String getKatoscar() {
		return katoscar;
	}

	public void setKatoscar(String katoscar) {
		this.katoscar = katoscar;
	}

	public String getKinostart() {
		return kinostart;
	}

	public void setKinostart(String kinostart) {
		this.kinostart = kinostart;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLimited() {
		return limited;
	}

	public void setLimited(String limited) {
		this.limited = limited;
	}

	public String getMediatype() {
		return mediatype;
	}

	public void setMediatype(String mediatype) {
		this.mediatype = mediatype;
	}

	public String getMorefeatures() {
		return morefeatures;
	}

	public void setMorefeatures(String morefeatures) {
		this.morefeatures = morefeatures;
	}

	public String getRegie() {
		return regie;
	}

	public void setRegie(String regie) {
		this.regie = regie;
	}

	public String getRegioncode() {
		return regioncode;
	}

	public void setRegioncode(String regioncode) {
		this.regioncode = regioncode;
	}

	public String getRereleasedate() {
		return rereleasedate;
	}

	public void setRereleasedate(String rereleasedate) {
		this.rereleasedate = rereleasedate;
	}

	public String getStudio() {
		return studio;
	}

	public void setStudio(String studio) {
		this.studio = studio;
	}

	public String getSubtitles() {
		return subtitles;
	}

	public void setSubtitles(String subtitles) {
		this.subtitles = subtitles;
	}

	public String getTondata() {
		return tondata;
	}

	public void setTondata(String tondata) {
		this.tondata = tondata;
	}

	public String getZusatzinfo() {
		return zusatzinfo;
	}

	public void setZusatzinfo(String zusatzinfo) {
		this.zusatzinfo = zusatzinfo;
	}

	public Date getEntryupdate() {
		return entryupdate;
	}

	public void setEntryupdate(Date entryupdate) {
		this.entryupdate = entryupdate;
	}

	@Transient
	public String[] getActorsAsArray() {
		if (actors == null)
			return new String[0];
		String[] actorsArray = actors.split(",");
		for (int i = 0; i < actorsArray.length; i++) {
			actorsArray[i] = actorsArray[i].trim();
		}
		return actorsArray;
	}

	@Transient
	public String[] getRegieAsArray() {
		if (regie == null)
			return new String[0];
		String[] regieArray = regie.split(",");
		for (int i = 0; i < regieArray.length; i++) {
			regieArray[i] = regieArray[i].trim();
		}
		return regieArray;
	}

	@Transient
	public Integer getDvdlengthAsInteger() {
		Integer l = null;
		try {
			l = Integer.valueOf(getDvdlength());
		} catch (NumberFormatException e) {
		}
		return l;
	}

	@Transient
	public Integer getMediaTypeAsInteger() {
		Integer l = null;
		try {
			l = Integer.valueOf(getMediatype());
		} catch (NumberFormatException e) {
		}
		return l;
	}

	@Transient
	public Date getRereleasedateAsDate() {
		return parseDate(getRereleasedate());
	}

	@Transient
	public Date getDvddateAsDate() {
		return parseDate(getDvddate());
	}

	@Transient
	public Date getDvddaterentalAsDate() {
		return parseDate(getDvddaterental());
	}

	@Transient
	public Date getKinostartAsDate() {
		return parseDate(getKinostart());
	}

	@Transient
	public Date getYearOfOscarsAsDate() {
		SimpleDateFormat df = new SimpleDateFormat("y", Locale.GERMANY);
		try {
			return df.parse(getJahroscar());
		} catch (ParseException e) {
		}
		return null;
	}

	private Date parseDate(String date) {
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
		try {
			Date d = df.parse(date.trim());
			long dateDiff = d.getTime() - System.currentTimeMillis();
			if (dateDiff > (1000l * 60l * 60l
					* 24l * 30l * 12l * 100l))
				return null;
			return d;
		} catch (ParseException e) {
		}
		return null;
	}

	@Transient
	public Character getRegionCodeAsChar() {
		if (getRegioncode() == null || getRegioncode().length() == 0)
			return null;
		return getRegioncode().charAt(0);
	}

	@Transient
	public String[] getFeaturesAsArray() {
		List<String> featureList = new ArrayList<String>();
		if (features.length() >= 1 && features.substring(0, 1).equals("1"))
			featureList.add("cinematrailer");
		if (features.length() >= 2 && features.substring(1, 2).equals("1"))
			featureList.add("trailer");
		if (features.length() >= 3 && features.substring(2, 3).equals("1"))
			featureList.add("crewbiography");
		if (features.length() >= 4 && features.substring(3, 4).equals("1"))
			featureList.add("selectionbychapter");
		if (features.length() >= 5 && features.substring(4, 5).equals("1"))
			featureList.add("makingof");
		if (features.length() >= 6 && features.substring(5, 6).equals("1"))
			featureList.add("animatedmenu");
		if (features.length() >= 7 && features.substring(6, 7).equals("1"))
			featureList.add("menusound");
		if (features.length() >= 8 && features.substring(7, 8).equals("1"))
			featureList.add("videoclips");
		if (features.length() >= 9 && features.substring(8, 9).equals("1"))
			featureList.add("");
		if (features.length() >= 10 && features.substring(9, 10).equals("1"))
			featureList.add("interviews");
		if (features.length() >= 11 && features.substring(10, 11).equals("1"))
			featureList.add("moretocome");
		return featureList.toArray(new String[0]);
	}

	@Transient
	public String[] getSubtitlesAsArray() {
		List<String> subtitleList = new ArrayList<String>();
		if (subtitles.length() >= 1 && subtitles.substring(0, 1).equals("1"))
			subtitleList.add("german");
		if (subtitles.length() >= 2 && subtitles.substring(1, 2).equals("1"))
			subtitleList.add("english");
		if (subtitles.length() >= 3 && subtitles.substring(2, 3).equals("1"))
			subtitleList.add("turkish");
		if (subtitles.length() >= 4 && subtitles.substring(3, 4).equals("1"))
			subtitleList.add("danish");
		if (subtitles.length() >= 5 && subtitles.substring(4, 5).equals("1"))
			subtitleList.add("swedish");
		if (subtitles.length() >= 6 && subtitles.substring(5, 6).equals("1"))
			subtitleList.add("finnish");
		if (subtitles.length() >= 7 && subtitles.substring(6, 7).equals("1"))
			subtitleList.add("norwegian");
		if (subtitles.length() >= 8 && subtitles.substring(7, 8).equals("1"))
			subtitleList.add("dutch");
		if (subtitles.length() >= 9 && subtitles.substring(8, 9).equals("1"))
			subtitleList.add("italian");
		if (subtitles.length() >= 10 && subtitles.substring(9, 10).equals("1"))
			subtitleList.add("japanese");
		if (subtitles.length() >= 11 && subtitles.substring(10, 11).equals("1"))
			subtitleList.add("belgian");
		if (subtitles.length() >= 12 && subtitles.substring(11, 12).equals("1"))
			subtitleList.add("french");
		if (subtitles.length() >= 13 && subtitles.substring(12, 13).equals("1"))
			subtitleList.add("spanish");
		if (subtitles.length() >= 14 && subtitles.substring(13, 14).equals("1"))
			subtitleList.add("portuguese");
		if (subtitles.length() >= 15 && subtitles.substring(14, 15).equals("1"))
			subtitleList.add("polish");
		if (subtitles.length() >= 16 && subtitles.substring(15, 16).equals("1"))
			subtitleList.add("greek");
		if (subtitles.length() >= 17 && subtitles.substring(16, 17).equals("1"))
			subtitleList.add("czech");
		if (subtitles.length() >= 18 && subtitles.substring(17, 18).equals("1"))
			subtitleList.add("hungarian");
		if (subtitles.length() >= 19 && subtitles.substring(18, 19).equals("1"))
			subtitleList.add("croatian");
		if (subtitles.length() >= 20 && subtitles.substring(19, 20).equals("1"))
			subtitleList.add("nosubtitles");
		if (subtitles.length() >= 21 && subtitles.substring(20, 21).equals("1"))
			subtitleList.add("moretocome");
		if (subtitles.length() >= 22 && subtitles.substring(21, 22).equals("1"))
			subtitleList.add("hebrew");
		if (subtitles.length() >= 23 && subtitles.substring(22, 23).equals("1"))
			subtitleList.add("arabian");
		if (subtitles.length() >= 24 && subtitles.substring(23, 24).equals("1"))
			subtitleList.add("icelandic");
		if (subtitles.length() >= 25 && subtitles.substring(24, 25).equals("1"))
			subtitleList.add("romanian");
		if (subtitles.length() >= 26 && subtitles.substring(25, 26).equals("1"))
			subtitleList.add("slovenian");
		if (subtitles.length() >= 27 && subtitles.substring(26, 27).equals("1"))
			subtitleList.add("hindi");
		if (subtitles.length() >= 28 && subtitles.substring(27, 28).equals("1"))
			subtitleList.add("bulgarian");
		if (subtitles.length() >= 29 && subtitles.substring(28, 29).equals("1"))
			subtitleList.add("cantonese");
		if (subtitles.length() >= 30 && subtitles.substring(29, 30).equals("1"))
			subtitleList.add("estonian");
		if (subtitles.length() >= 31 && subtitles.substring(30, 31).equals("1"))
			subtitleList.add("russian");
		if (subtitles.length() >= 32 && subtitles.substring(31, 32).equals("1"))
			subtitleList.add("korean");
		return subtitleList.toArray(new String[0]);
	}

	@Transient
	public String[] getOscarsAsArray() {
		List<String> oscarList = new ArrayList<String>();
		if (katoscar.length() >= 1 && katoscar.substring(0, 1).equals("1"))
			oscarList.add("bestleadingactor");
		if (katoscar.length() >= 2 && katoscar.substring(1, 2).equals("1"))
			oscarList.add("bestsupportingactor");
		if (katoscar.length() >= 3 && katoscar.substring(2, 3).equals("1"))
			oscarList.add("bestleadingactress");
		if (katoscar.length() >= 4 && katoscar.substring(3, 4).equals("1"))
			oscarList.add("bestsupportingactress");
		if (katoscar.length() >= 5 && katoscar.substring(4, 5).equals("1"))
			oscarList.add("bestartdirection");
		if (katoscar.length() >= 6 && katoscar.substring(5, 6).equals("1"))
			oscarList.add("bestcinematography");
		if (katoscar.length() >= 7 && katoscar.substring(6, 7).equals("1"))
			oscarList.add("bestcostumedesign");
		if (katoscar.length() >= 8 && katoscar.substring(7, 8).equals("1"))
			oscarList.add("bestdirector");
		if (katoscar.length() >= 9 && katoscar.substring(8, 9).equals("1"))
			oscarList.add("bestdocumentary");
		if (katoscar.length() >= 10 && katoscar.substring(9, 10).equals("1"))
			oscarList.add("bestdocumentaryshort");
		if (katoscar.length() >= 11 && katoscar.substring(10, 11).equals("1"))
			oscarList.add("bestfilmediting");
		if (katoscar.length() >= 12 && katoscar.substring(11, 12).equals("1"))
			oscarList.add("bestforeignlanguagefilm");
		if (katoscar.length() >= 13 && katoscar.substring(12, 13).equals("1"))
			oscarList.add("bestmakeup");
		if (katoscar.length() >= 14 && katoscar.substring(13, 14).equals("1"))
			oscarList.add("bestoriginalmusicscore");
		if (katoscar.length() >= 15 && katoscar.substring(14, 15).equals("1"))
			oscarList.add("bestoriginalsong");
		if (katoscar.length() >= 16 && katoscar.substring(15, 16).equals("1"))
			oscarList.add("bestpicture");
		if (katoscar.length() >= 17 && katoscar.substring(16, 17).equals("1"))
			oscarList.add("bestadaptedscreenplay");
		if (katoscar.length() >= 18 && katoscar.substring(17, 18).equals("1"))
			oscarList.add("bestoriginalscreenplay");
		if (katoscar.length() >= 19 && katoscar.substring(18, 19).equals("1"))
			oscarList.add("bestanimatedfeature");
		if (katoscar.length() >= 20 && katoscar.substring(19, 20).equals("1"))
			oscarList.add("bestanimatedshortfilm");
		if (katoscar.length() >= 21 && katoscar.substring(20, 21).equals("1"))
			oscarList.add("bestsoundmixing");
		if (katoscar.length() >= 22 && katoscar.substring(21, 22).equals("1"))
			oscarList.add("bestsoundediting");
		if (katoscar.length() >= 23 && katoscar.substring(22, 23).equals("1"))
			oscarList.add("bestvisualeffects");
		return oscarList.toArray(new String[0]);
	}

	@Transient
	public String[] getGenreAsArray() {
		List<String> genres = new ArrayList<String>();
		genres.addAll(getGenreAsList(getGenre()));
		genres.addAll(getGenreAsList(getGenre1()));
		genres.addAll(getGenreAsList(getGenre2()));
		return genres.toArray(new String[0]);
	}

	private List<String> getGenreAsList(String palaceGenreCode) {
		List<String> genres = new ArrayList<String>();
		if (palaceGenreCode == null)
			return genres;
		if (palaceGenreCode.equals("001"))
			genres.add("adventure");
		if (palaceGenreCode.equals("002"))
			genres.add("action");
		if (palaceGenreCode.equals("003"))
			genres.add("animation");
		if (palaceGenreCode.equals("004"))
			genres.add("anime");
		if (palaceGenreCode.equals("005"))
			genres.add("documentation");
		if (palaceGenreCode.equals("006"))
			genres.add("drama");
		if (palaceGenreCode.equals("008"))
			genres.add("eastern");
		if (palaceGenreCode.equals("009"))
			genres.add("erotic");
		if (palaceGenreCode.equals("011"))
			genres.add("family");
		if (palaceGenreCode.equals("012"))
			genres.add("fantasy");
		if (palaceGenreCode.equals("013"))
			genres.add("filmoperette");
		if (palaceGenreCode.equals("014"))
			genres.add("history");
		if (palaceGenreCode.equals("015"))
			genres.add("horror");
		if (palaceGenreCode.equals("016"))
			genres.add("horrorcomedy");
		if (palaceGenreCode.equals("017"))
			genres.add("splatter");
		if (palaceGenreCode.equals("018"))
			genres.add("children");
		if (palaceGenreCode.equals("019"))
			genres.add("classic");
		if (palaceGenreCode.equals("020"))
			genres.add("comedy");
		if (palaceGenreCode.equals("022"))
			genres.add("war");
		if (palaceGenreCode.equals("023"))
			genres.add("criminal");
		if (palaceGenreCode.equals("024"))
			genres.add("monumental");
		if (palaceGenreCode.equals("025"))
			genres.add("music");
		if (palaceGenreCode.equals("026"))
			genres.add("musicdocumentation");
		if (palaceGenreCode.equals("027"))
			genres.add("musicmovie");
		if (palaceGenreCode.equals("028"))
			genres.add("revue");
		if (palaceGenreCode.equals("029"))
			genres.add("roadmovie");
		if (palaceGenreCode.equals("030"))
			genres.add("romance");
		if (palaceGenreCode.equals("031"))
			genres.add("satire");
		if (palaceGenreCode.equals("032"))
			genres.add("sciencefiction");
		if (palaceGenreCode.equals("033"))
			genres.add("serie");
		if (palaceGenreCode.equals("034"))
			genres.add("special");
		if (palaceGenreCode.equals("035"))
			genres.add("tvserie");
		if (palaceGenreCode.equals("036"))
			genres.add("thriller");
		if (palaceGenreCode.equals("038"))
			genres.add("trickfilm");
		if (palaceGenreCode.equals("039"))
			genres.add("tvmovie");
		if (palaceGenreCode.equals("040"))
			genres.add("entertainment");
		if (palaceGenreCode.equals("041"))
			genres.add("western");
		if (palaceGenreCode.equals("042"))
			genres.add("westerncomedy");
		if (palaceGenreCode.equals("043"))
			genres.add("animation");
		if (palaceGenreCode.equals("045"))
			genres.add("popmusic");
		if (palaceGenreCode.equals("046"))
			genres.add("opera");
		if (palaceGenreCode.equals("047"))
			genres.add("actioncomedy");
		if (palaceGenreCode.equals("049"))
			genres.add("rockmusic");
		if (palaceGenreCode.equals("050"))
			genres.add("biography");
		if (palaceGenreCode.equals("051"))
			genres.add("sports");
		if (palaceGenreCode.equals("052"))
			genres.add("travel");
		if (palaceGenreCode.equals("053"))
			genres.add("mystery");
		if (palaceGenreCode.equals("054"))
			genres.add("mystery");
		if (palaceGenreCode.equals("055"))
			genres.add("bollywood");
		if (palaceGenreCode.equals("056"))
			genres.add("tragiccomedy");
		return genres;
	}

	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof Dvdbase))
			return false;
		Dvdbase dvd = (Dvdbase) arg0;
		return getId().equals(dvd.getId());
	}

	@Override
	public String toString() {
		return "DVD: " + getId();
	}
}
