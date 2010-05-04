package de.dvdb.domain.model.item.palace;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import de.dvdb.domain.model.item.DVDItem;


@Entity
@DiscriminatorValue("2")
public class PalaceDVDItem extends DVDItem implements Serializable {

	private static final long serialVersionUID = -4077732947803570902L;

	private Long dvdId;

	@Transient
	public String getTitleWithMediatype() {
		if (getMediaType() == MEDIATYPE_BR)
			return super.getTitle() + " [Blu-ray]";
		else if (getMediaType() == MEDIATYPE_HD)
			return super.getTitle() + " [HD DVD]";
		else if (getMediaType() == MEDIATYPE_DVD)
			return super.getTitle() + " [DVD]";
		else
			return super.getTitle();
	}

	@Column(nullable = true, unique = true, name = "source_dvdid")
	public Long getDvdId() {
		return dvdId;
	}

	public void setDvdId(Long dvdId) {
		this.dvdId = dvdId;
	}

	@Transient
	public String getUrlImageLarge() {
		return "http://www.dvdb.de/dvdpix/big/" + getDvdId() + ".jpg";
	}

	@Transient
	public String getUrlImageSmall() {
		return "http://www.dvdb.de/dvdpix/small/" + getDvdId() + ".jpg";
	}

	@Transient
	public String getUrlImageMedium() {
		return "http://www.dvdb.de/dvdpix/medium/" + getDvdId() + ".jpg";
	}

	@Transient
	public String getUrlImage40() {
		return "http://www.dvdb.de/dvdpix/40/" + getDvdId() + ".jpg";
	}

	@Override
	@Transient
	public void setUrlImageLarge(String url) {
	}

	@Override
	@Transient
	public void setUrlImageMedium(String url) {
	}

	@Override
	@Transient
	public void setUrlImageSmall(String url) {
	}

	@Override
	public void setUrlImage40(String url) {
	}
	
	@Override
	public String toString() {
		return "#" + getId() + ", " + getTitle() + " [" + getAsin() + "] - "
				+ this.getClass().getName();
	}

	@Override
	@Transient
	public String getWikiPageURL() {
		if (getCountryAndYear() == null || getCountryAndYear().equals(""))
			return null;
		if (getOriginalTitle() == null || getOriginalTitle().equals(""))
			return null;

		String wikiti = "Film:"
				+ ((getOriginalTitle() + " " + getCountryAndYear()).replaceAll(
						"'", "").replaceAll("\u00e4", "ae").replaceAll(
						"\u00f6", "oe").replaceAll("\u00fc", "ue").replaceAll(
						"\u00df", "ss").replaceAll("\u00c4", "Ae").replaceAll(
						"\u00d6", "Oe").replaceAll("\u00dc", "Ue").replaceAll(
						"[^a-zA-Z0-9]", "_").replaceAll("__", "_").replaceAll(
						"__", "_"));
		return wikiti;

	}

	@Override
	@Transient
	public String getAbstract() {
		return super.getPlot();
	}
}
