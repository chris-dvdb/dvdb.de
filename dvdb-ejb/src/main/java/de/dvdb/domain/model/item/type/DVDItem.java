package de.dvdb.domain.model.item.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import de.dvdb.domain.model.tag.GenreTag;
import de.dvdb.domain.model.tag.Tag;

@Entity
@DiscriminatorValue("1")
public abstract class DVDItem extends Item implements Serializable {

	private static final long serialVersionUID = 9132383346084271157L;

	public static String TAGREL_DVDITEM_ACTOR = "actor";

	public static String TAGREL_DVDITEM_DIRECTOR = "director";

	public static String TAGREL_DVDITEM_GENRE = "dvdgenre";

	public static int MEDIATYPE_DVD = 1;
	public static int MEDIATYPE_UMD = 2;
	public static int MEDIATYPE_HD = 3;
	public static int MEDIATYPE_BR = 4;
	public static int MEDIATYPE_WMV = 5;
	public static int MEDIATYPE_MINI = 6;

	private Integer length;

	private Character regionCode;

	private String dvdType;

	private String pictureData;

	private String audioData;

	private Date cinemaDate;

	private Date reReleaseDate;

	private Date rentalDate;

	private String countryAndYear;

	private String features;

	private String subtitles;

	private String oscars;

	private Date yearOfOscars;

	private String dvdCase;

	private String studio;

	private String label;

	private Integer mediaType;

	private String plot;

	private String actors;

	private String directors;

	@Transient
	public String getTitleWithMediatype() {
		return super.getTitle();
	}


	@Column(nullable = true)
	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	@Column(name = "regioncode", nullable = true)
	public Character getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(Character regionCode) {
		this.regionCode = regionCode;
	}

	@Column(name = "dvdtype", nullable = true, length = 2)
	public String getDvdType() {
		return dvdType;
	}

	public void setDvdType(String dvdType) {
		if (dvdType != null && dvdType.length() > 2)
			dvdType = dvdType.substring(2);
		this.dvdType = dvdType;
	}

	@Column(name = "picturedata", nullable = true, length = 1000)
	public String getPictureData() {
		return pictureData;
	}

	public void setPictureData(String pictureData) {
		if (pictureData != null && pictureData.length() > 1000)
			pictureData = pictureData.substring(1000);
		this.pictureData = pictureData;
	}

	@Column(name = "audiodata", nullable = true, length = 1000)
	public String getAudioData() {
		return audioData;
	}

	public void setAudioData(String audioData) {
		if (audioData != null && audioData.length() > 1000)
			audioData = audioData.substring(1000);
		this.audioData = audioData;
	}

	@Column(name = "cinemadate", nullable = true)
	public Date getCinemaDate() {
		return cinemaDate;
	}

	public void setCinemaDate(Date cinemaDate) {
		this.cinemaDate = cinemaDate;
	}

	@Column(name = "rereleasedate", nullable = true)
	public Date getReReleaseDate() {
		return reReleaseDate;
	}

	public void setReReleaseDate(Date reReleaseDate) {
		this.reReleaseDate = reReleaseDate;
	}

	@Column(name = "rentaldate", nullable = true)
	public Date getRentalDate() {
		return rentalDate;
	}

	public void setRentalDate(Date rentalDate) {
		this.rentalDate = rentalDate;
	}

	@Column(name = "countryandyear", nullable = true, length = 255)
	public String getCountryAndYear() {
		return countryAndYear;
	}

	public void setCountryAndYear(String countryAndYear) {
		if (countryAndYear != null && countryAndYear.length() > 255)
			countryAndYear = countryAndYear.substring(255);
		this.countryAndYear = countryAndYear;
	}

	@Column(nullable = true, length = 4000)
	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}

	@Column(nullable = true, length = 4000)
	public String getSubtitles() {
		return subtitles;
	}

	public void setSubtitles(String subtitles) {
		this.subtitles = subtitles;
	}

	@Column(nullable = true, length = 4000)
	public String getOscars() {
		return oscars;
	}

	public void setOscars(String oscars) {
		this.oscars = oscars;
	}

	@Column(name = "yearofoscars", nullable = true)
	public Date getYearOfOscars() {
		return yearOfOscars;
	}

	public void setYearOfOscars(Date yearOfOscars) {
		this.yearOfOscars = yearOfOscars;
	}

	@Column(nullable = true, length = 3)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Column(nullable = true, length = 3)
	public String getStudio() {
		return studio;
	}

	public void setStudio(String studio) {
		this.studio = studio;
	}

	@Column(nullable = true, length = 3, name = "dvdcase")
	public String getDvdCase() {
		return dvdCase;
	}

	public void setDvdCase(String dvdCase) {
		this.dvdCase = dvdCase;
	}

	@Column(nullable = false, name = "mediatype")
	public Integer getMediaType() {
		return mediaType;
	}

	public void setMediaType(Integer mediaType) {
		this.mediaType = mediaType;
	}

	@Column(nullable = true, length = 5000, name = "plot")
	public String getPlot() {
		return plot;
	}

	public void setPlot(String plot) {
		this.plot = plot;
	}

	@Column(length = 5000, nullable = true)
	public String getActors() {
		return actors;
	}

	public void setActors(String actors) {
		this.actors = actors;
	}

	@Transient
	public String[] getActorsAsArray() {
		if (getActors() == null)
			return new String[0];
		return getActors().split(",");
	}

	@Transient
	public String[] getDirectorsAsArray() {
		if (getDirectors() == null)
			return new String[0];
		return getDirectors().split(",");
	}

	@Column(length = 5000, nullable = true)
	public String getDirectors() {
		return directors;
	}

	public void setDirectors(String directors) {
		this.directors = directors;
	}

	@Transient
	public void setDirectors(String[] directors) {
		this.directors = "";
		for (String dir : directors) {
			this.directors += dir + ",";
		}
		if (this.directors.charAt(this.directors.length() - 1) == ',') {
			this.directors = this.directors.substring(0, this.directors
					.length() - 1);
		}
	}

	@Transient
	public void setActors(String[] actors) {
		this.actors = "";
		for (String dir : actors) {
			this.actors += dir + ",";
		}
		if (this.actors.charAt(this.actors.length() - 1) == ',') {
			this.actors = this.actors.substring(0, this.actors.length() - 1);
		}
	}

	@Transient
	public String getSubTitle() {
		return countryAndYear;
	}

	@Transient
	public List<String> getSubtitlesAsList() {
		List<String> sls = new ArrayList<String>();
		if (this.getSubtitles() == null)
			return sls;
		for (String subtitle : this.getSubtitles().split(",")) {
			sls.add(subtitle);
		}
		return sls;
	}

	public void setSubtitlesAsList(List<String> subtitles) {
		String s = "";
		for (String string : subtitles) {
			s += string + " ";
		}
		this.subtitles = s.trim().replaceAll(" ", ",");
	}

	@Transient
	public List<String> getGenresAsList() {
		List<String> sls = new ArrayList<String>();
		if (this.getTags() == null)
			return sls;
		for (Tag tag : this.getTags()) {
			if (!(tag instanceof GenreTag))
				continue;
			sls.add(tag.getValue());
		}
		return sls;
	}

	public void setGenresAsList(List<String> genres) {
		List tags = getTags();
		List<GenreTag> tagsToRemove = new ArrayList<GenreTag>();
		for (Tag tag : getTags()) {
			if (tag instanceof GenreTag)
				tagsToRemove.add((GenreTag) tag);
		}

		for (GenreTag tag : tagsToRemove) {
			tags.remove(tag);
		}

		String tagString = "";
		for (String string : genres) {
			GenreTag tag = new GenreTag();
			tag.setValue(string);
			tag.setAlias(true);
			tag.setItem(this);
			tags.add(tag);

			if(tagString.length()+3+string.length()<255)
				tagString += " ; " + string;
			setTagString(tagString);
		}

	}

	@Transient
	public List<String> getOscarsAsList() {
		List<String> sls = new ArrayList<String>();
		if (this.getOscars() == null)
			return sls;
		for (String oscar : this.getOscars().split(",")) {
			sls.add(oscar);
		}
		return sls;
	}

	public void setOscarsAsList(List<String> oscars) {
		String s = "";
		for (String string : oscars) {
			s += string + " ";
		}
		this.oscars = s.trim().replaceAll(" ", ",");
	}

	@Transient
	public List<String> getFeaturesAsList() {
		List<String> sls = new ArrayList<String>();
		if (this.getFeatures() == null)
			return sls;
		for (String feature : this.getFeatures().split(",")) {
			sls.add(feature);
		}
		return sls;
	}

	public void setFeaturesAsList(List<String> features) {
		String s = "";
		for (String string : features) {
			s += string + " ";
		}
		this.features = s.trim().replaceAll(" ", ",");
	}

	@Transient
	public List getAvailableOscars() {
		String[] oscars = new String[] {

		"bestleadingactor", "bestsupportingactor", "bestleadingactress",
				"bestsupportingactress", "bestartdirection",
				"bestcinematography", "bestcostumedesign", "bestdirector",
				"bestdocumentary", "bestdocumentaryshort", "bestfilmediting",
				"bestforeignlanguagefilm", "bestmakeup",
				"bestoriginalmusicscore", "bestoriginalsong", "bestpicture",
				"bestadaptedscreenplay", "bestoriginalscreenplay",
				"bestanimatedfeature", "bestanimatedshortfilm",
				"bestsoundmixing", "bestsoundediting", "bestvisualeffects"

		};

		return Arrays.asList(oscars);
	}

	@Transient
	public List getAvailableSubtitles() {
		String[] languages = new String[] {

		"german", "english", "turkish", "danish", "swedish", "finnish",
				"norwegian", "dutch", "italian", "japanese", "belgian",
				"french", "spanish", "portuguese", "polish", "greek", "czech",
				"hungarian", "croatian", "nosubtitles", "moretocome", "hebrew",
				"arabian", "icelandic", "romanian", "slovenian", "hindi",
				"bulgarian", "cantonese", "estonian", "russian", "korean"

		};

		return Arrays.asList(languages);
	}

	@Transient
	public List getAvailableFeatures() {
		String[] features = new String[] { "cinematrailer", "trailer",
				"crewbiography", "selectionbychapter", "makingof",
				"animatedmenu", "menusound", "videoclips", "interviews",
				"moretocome" };

		return Arrays.asList(features);
	}
}
