package de.dvdb.web.item.action;

import java.io.Serializable;
import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("dvdSearchForm")
@Scope(ScopeType.CONVERSATION)
public class DVDSearchForm implements Serializable {

	public static final String DOMAIN_TITLE = "DOMAIN_TITLE";
	public static final String DOMAIN_TITLE_BEGIN = "DOMAIN_TITLE_BEGIN";
	public static final String DOMAIN_COUNTRY = "DOMAIN_COUNTRY";
	public static final String DOMAIN_ACTOR = "DOMAIN_ACTOR";
	public static final String DOMAIN_DIRECTOR = "DOMAIN_DIRECTOR";
	public static final String DOMAIN_EAN = "DOMAIN_EAN";
	public static final String DOMAIN_DVDBID = "DOMAIN_DVDBID";
	public static final String DOMAIN_GENRE = "DOMAIN_GENRE";

	private static final long serialVersionUID = 8907559561751325862L;

	String keyword1;

	String domain1 = DOMAIN_TITLE;

	String keyword2;

	String domain2 = DOMAIN_ACTOR;

	String keyword3;

	String domain3 = DOMAIN_DIRECTOR;

	String genre;

	Long genreId;

	Integer ratingContent;

	Integer ratingMastering;

	Boolean oscar;

	Boolean indiziert;

	Boolean upcoming;

	Boolean blockbuster;

	Date releaseDateAfter;

	Date releaseDateBefore;

	Integer minPriceChange;

	Integer minNumberOfWishes;

	Boolean mediatypeDvd;
	Boolean mediatypeBR;
	Boolean mediatypeHD;
	Boolean mediatypeUMD;

	Boolean showPrices;

	Boolean cheaper;

	public DVDSearchForm() {
		reset();
	}

	public Integer getMinNumberOfWishes() {
		return minNumberOfWishes;
	}

	public void setMinNumberOfWishes(Integer minNumberOfWishes) {
		this.minNumberOfWishes = minNumberOfWishes;
	}

	public Integer getMinPriceChange() {
		return minPriceChange;
	}

	public void setMinPriceChange(Integer minPriceChange) {
		this.minPriceChange = minPriceChange;
	}

	public String getDomain1() {
		return domain1;
	}

	public void setDomain1(String domain1) {
		this.domain1 = domain1;
	}

	public String getDomain3() {
		return domain3;
	}

	public void setDomain3(String domain3) {
		this.domain3 = domain3;
	}

	public String getKeyword1() {
		return keyword1;
	}

	public void setKeyword1(String keyword1) {
		this.keyword1 = keyword1;
	}

	public String getKeyword3() {
		return keyword3;
	}

	public void setKeyword3(String keyword3) {
		this.keyword3 = keyword3;
	}

	public String getDomain2() {
		return domain2;
	}

	public void setDomain2(String domain2) {
		this.domain2 = domain2;
	}

	public String getKeyword2() {
		return keyword2;
	}

	public void setKeyword2(String keyword2) {
		this.keyword2 = keyword2;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public Integer getRatingMastering() {
		return ratingMastering;
	}

	public void setRatingMastering(Integer ratingMastering) {
		this.ratingMastering = ratingMastering;
	}

	public Integer getRatingContent() {
		return ratingContent;
	}

	public void setRatingContent(Integer ratingContent) {
		this.ratingContent = ratingContent;
	}

	public Boolean getIndiziert() {
		return indiziert;
	}

	public void setIndiziert(Boolean indiziert) {
		this.indiziert = indiziert;
	}

	public Boolean getOscar() {
		return oscar;
	}

	public void setOscar(Boolean oscar) {
		this.oscar = oscar;
	}

	public Long getGenreId() {
		return genreId;
	}

	public void setGenreId(Long genreId) {
		this.genreId = genreId;
	}

	public Boolean getUpcoming() {
		return upcoming;
	}

	public void setUpcoming(Boolean upcoming) {
		this.upcoming = upcoming;
	}

	public Boolean getBlockbuster() {
		return blockbuster;
	}

	public void setBlockbuster(Boolean blockbuster) {
		this.blockbuster = blockbuster;
	}

	public Boolean getMediatypeDVD() {
		return mediatypeDvd;
	}

	public void setMediatypeDVD(Boolean mediatypeDvd) {
		this.mediatypeDvd = mediatypeDvd;
	}

	public Boolean getMediatypeHD() {
		return mediatypeHD;
	}

	public void setMediatypeHD(Boolean mediatypeHD) {
		this.mediatypeHD = mediatypeHD;
	}

	public Boolean getMediatypeBR() {
		return mediatypeBR;
	}

	public void setMediatypeBR(Boolean mediatypeBR) {
		this.mediatypeBR = mediatypeBR;
	}

	public Date getReleaseDateAfter() {
		return releaseDateAfter;
	}

	public void setReleaseDateAfter(Date releaseDateAfter) {
		this.releaseDateAfter = releaseDateAfter;
	}

	public Date getReleaseDateBefore() {
		return releaseDateBefore;
	}

	public void setReleaseDateBefore(Date releaseDateBefore) {
		this.releaseDateBefore = releaseDateBefore;
	}

	public Boolean getShowPrices() {
		return showPrices;
	}

	public void setShowPrices(Boolean showPrices) {
		this.showPrices = showPrices;
	}

	public Boolean getCheaper() {
		return cheaper;
	}

	public void setCheaper(Boolean cheaper) {
		this.cheaper = cheaper;
	}

	public void reset() {
		this.blockbuster = false;
		this.domain1 = DOMAIN_TITLE;
		this.keyword1 = "";
		this.domain2 = DOMAIN_ACTOR;
		this.keyword2 = "";
		this.domain3 = DOMAIN_DIRECTOR;
		this.keyword3 = "";
		this.genreId = null;
		this.genre = null;
		this.indiziert = false;
		this.oscar = false;
		this.ratingContent = null;
		this.ratingMastering = null;
		this.upcoming = false;
		this.showPrices = true;
		this.setMediatypeBR(true);
		this.setMediatypeHD(true);
		this.setMediatypeDVD(true);
		this.setReleaseDateAfter(null);
		this.setReleaseDateBefore(null);
		this.setCheaper(false);
	}
}
