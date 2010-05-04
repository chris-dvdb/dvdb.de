package de.dvdb.domain.model.item;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;


@Entity
@DiscriminatorValue("8")
public class AmazonGameItem extends Item implements Serializable {

	private static final long serialVersionUID = 9132383346084271157L;

	public static String PRODUCTURL = "http://www.amazon.de/exec/obidos/ASIN/#ASIN#/vooshde-21";

	public static String TAGREL_CDITEM_GENRE = "gamegenre";

	public static final String PLATFORM_PC = "301129";
	public static final String PLATFORM_PS1 = "300993";
	public static final String PLATFORM_PS2 = "516838";
	public static final String PLATFORM_PS3 = "195012011";
	public static final String PLATFORM_WII = "195015011";
	public static final String PLATFORM_NDS = "13840371";
	public static final String PLATFORM_PSP = "14055571";
	public static final String PLATFORM_GBA = "569128";
	public static final String PLATFORM_GC = "639320";
	public static final String PLATFORM_XB = "575708";
	public static final String PLATFORM_XB360 = "15855671";
	public static final String PLATFORM_MAC = "403352";

	private Integer platform;

	private String url;

	private String urlImageMedium;

	private String urlImageSmall;

	private String urlImageLarge;

	private Date releaseDateAmazon;

	@Column(name = "urlimagelarge")
	public String getUrlImageLarge() {
		return urlImageLarge;
	}

	public void setUrlImageLarge(String urlImageLarge) {
		this.urlImageLarge = urlImageLarge;
	}

	@Column(name = "urlimagemedium")
	public String getUrlImageMedium() {
		return urlImageMedium;
	}

	public void setUrlImageMedium(String urlImageMedium) {
		this.urlImageMedium = urlImageMedium;
	}

	@Column(name = "urlimagesmall")
	public String getUrlImageSmall() {
		return urlImageSmall;
	}

	public void setUrlImageSmall(String urlImageSmall) {
		this.urlImageSmall = urlImageSmall;
	}

	@Transient
	@Override
	public String getUrlImage40() {
		return getUrlImageSmall();
	}

	@Override
	@Transient
	public void setUrlImage40(String url) {

	}

	@Column(name = "releasedateamazon")
	public Date getReleaseDateAmazon() {
		return releaseDateAmazon;
	}

	public void setReleaseDateAmazon(Date releaseDateAmazon) {
		this.releaseDateAmazon = releaseDateAmazon;
	}

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	@Override
	@Transient
	public String getSubTitle() {
		return null;
	}

	public AmazonGameItem(com.amazon.soap.ecs.Item item) {
		// browsenodes holen
		// BrowseNodes bns = item.getBrowseNodes();
		// BrowseNode[] nods = bns.getBrowseNode();
		// Set<String> allBrowseNodes = new HashSet<String>();
		// for (BrowseNode browseNode : nods) {
		// addNodes(allBrowseNodes, browseNode);
		// }
		//
		// if (AmazonDVDItem.isBR(allBrowseNodes)) {
		// setMediaType(DVDItem.MEDIATYPE_BR);
		// } else if (AmazonDVDItem.isHD(allBrowseNodes)) {
		// setMediaType(DVDItem.MEDIATYPE_HD);
		// } else if (AmazonDVDItem.isDVD(allBrowseNodes)) {
		// setMediaType(DVDItem.MEDIATYPE_DVD);
		// } else {
		// throw new InstantiationError(
		// "Cannot instantiate AmazonDVDItem. Item from Amazon is no DVD.");
		// }

		// else if (AmazonGame.isGame(allBrowseNodes)) {
		// amazonItem = new AmazonGame();
		// String pf = "";
		// String[] pfs = item.getItemAttributes().getPlatform();
		// for (String string : pfs) {
		// pf = pf + "<li>" + string + "</li>";
		// }
		// ((AmazonGame) amazonItem).setPlattform(pf);
		// }
		//			
		// else
		// amazonItem = new AmazonDVDItem();

		if (item.getItemAttributes().getEAN() != null)
			setEan(item.getItemAttributes().getEAN());
		if (item.getItemAttributes().getTitle() != null)
			setTitle(item.getItemAttributes().getTitle());
		setAsin(item.getASIN());
		setUrl(PRODUCTURL.replaceAll("#ASIN#", item.getASIN()));
		// if (item.getItemAttributes().getDirector() != null)
		// setDirectors(item.getItemAttributes().getDirector());
		// if (item.getItemAttributes().getActor() != null)
		// setActors(item.getItemAttributes().getActor());
		// if (item.getItemAttributes().getAudienceRating() != null)
		// setRating(item.getItemAttributes().getAudienceRating());
		// if (item.getItemAttributes().getRunningTime() != null
		// && item.getItemAttributes().getRunningTime().getUnits()
		// .contains("inute"))
		// setLength(item.getItemAttributes().getRunningTime().get_value()
		// .intValue());
		// if (item.getItemAttributes().getManufacturer() != null)
		// setLabel(item.getItemAttributes().getManufacturer());
		// if (item.getItemAttributes().getRegionCode() != null
		// && item.getItemAttributes().getRegionCode().length() == 1)
		// setRegionCode(item.getItemAttributes().getRegionCode().charAt(0));
		System.out.println("Mediatype "
				+ item.getItemAttributes().getMediaType());

		if (item.getLargeImage() != null)
			setUrlImageLarge(item.getLargeImage().getURL());
		if (item.getMediumImage() != null)
			setUrlImageMedium(item.getMediumImage().getURL());
		if (item.getSmallImage() != null)
			setUrlImageSmall(item.getSmallImage().getURL());
		if (item.getSalesRank() != null) {
			try {
				int salesRank = Integer.parseInt(item.getSalesRank()
						.replaceAll("\\.", "").replaceAll(",", ""));
				setSalesRank(salesRank);
			} catch (Exception e) {
				System.out.println("Unable to parse sales rank "
						+ item.getSalesRank().replaceAll("\\.", "").replaceAll(
								",", ""));
			}
		}

		String releaseDateString = item.getItemAttributes().getReleaseDate();
		if (releaseDateString != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date releaseDate = sdf.parse(releaseDateString);
				setReleaseDateAmazon(releaseDate);
				setReleaseDate(releaseDate);
			} catch (Exception e) {
				System.out.println("Unable to parse release date "
						+ releaseDateString + ". " + e.getMessage());
			}
		}

		// String cinemaDateString = item.getItemAttributes()
		// .getTheatricalReleaseDate();
		// if (cinemaDateString != null) {
		// try {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// Date cinemaDate = sdf.parse(cinemaDateString);
		// setCinemaDate(cinemaDate);
		// } catch (Exception e) {
		// System.out.println("Unable to parse release date "
		// + cinemaDateString + ". " + e.getMessage());
		// }
		// }
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof AmazonDVDItem))
			return false;
		AmazonDVDItem off = (AmazonDVDItem) arg0;
		return getAsin().equals(off.getAsin());
	}

	@Override
	public String toString() {
		return getTitle() + " [" + getAsin() + "] - "
				+ this.getClass().getName();
	}

}
