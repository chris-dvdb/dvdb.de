package de.dvdb.domain.model.item.type;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.amazon.soap.ecs.BrowseNode;
import com.amazon.soap.ecs.BrowseNodes;
import com.amazon.soap.ecs.Item;

import de.dvdb.domain.model.item.palace.PalaceDVDItem;

@Entity
@DiscriminatorValue("4")
public class AmazonDVDItem extends DVDItem implements Serializable {
	private static final long serialVersionUID = 3833189124977801528L;

	public static String PRODUCTURL = "http://www.amazon.de/exec/obidos/ASIN/#ASIN#/vooshde-21";
	public static final String BROWSENODE_DVD = "284266";

	public static final String SYSTEM_DVD = "284266";
	public static final String SYSTEM_DVDHD = "240179011";
	public static final String SYSTEM_DVDBR = "240171011";

	private String url;

	private String urlImageMedium;

	private String urlImageSmall;

	private String urlImageLarge;

	private Date releaseDateAmazon;

	private PalaceDVDItem palaceDVDItem;

	public AmazonDVDItem() {
	}

	@Transient
	public PalaceDVDItem getPalaceDVDItem() {
		return palaceDVDItem;
	}

	public void setPalaceDVDItem(PalaceDVDItem palaceDVDItem) {
		this.palaceDVDItem = palaceDVDItem;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

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

	// @ManyToMany(cascade = { CascadeType.ALL })
	// @JoinTable(name = "dvdb2_amazonitem_tag", joinColumns = @JoinColumn(name
	// = "amazonitem_id", referencedColumnName = "id"), inverseJoinColumns =
	// @JoinColumn(name = "tag_id", referencedColumnName = "id"))
	// public Set<STag> getTags() {
	// return tags;
	// }
	//
	// public void addTag(STag tag) {
	// this.tags.add(tag);
	// // tag.getAmazonItems().add(this);
	// }
	//
	// public void removeTag(STag tag) {
	// // tag.getAmazonItems().remove(this);
	// tags.remove(tag);
	// }
	//
	// public void setTags(Set<STag> tags) {
	// this.tags = tags;
	// }
	//
	// @Transient
	// public STag[] getTagsAsArray() {
	// return tags.toArray(new STag[0]);
	// }
	//
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

	// public static final String SYSTEM_UMD = "301129";

	@javax.persistence.Transient
	public static boolean isBR(String browseNode) {
		if (browseNode.equals(SYSTEM_DVDBR))
			return true;
		return false;
	}

	@javax.persistence.Transient
	public static boolean isHD(String browseNode) {
		if (browseNode.equals(SYSTEM_DVDHD))
			return true;
		return false;
	}

	@javax.persistence.Transient
	public static boolean isDVD(String browseNode) {
		if (browseNode.equals(SYSTEM_DVD) || browseNode.equals(SYSTEM_DVDHD)
				|| browseNode.equals(SYSTEM_DVDBR))
			return true;
		return false;
	}

	@javax.persistence.Transient
	public static boolean isDVD(Set<String> browseNodes) {
		for (String string : browseNodes) {
			if (isDVD(string))
				return true;
		}
		return false;
	}

	@javax.persistence.Transient
	public static boolean isHD(Set<String> browseNodes) {
		for (String string : browseNodes) {
			if (isHD(string))
				return true;
		}
		return false;
	}

	@javax.persistence.Transient
	public static boolean isBR(Set<String> browseNodes) {
		for (String string : browseNodes) {
			if (isBR(string))
				return true;
		}
		return false;
	}

	public AmazonDVDItem(Item item) {
		// browsenodes holen
		BrowseNodes bns = item.getBrowseNodes();
		List<BrowseNode> nods = bns.getBrowseNode();
		Set<String> allBrowseNodes = new HashSet<String>();
		for (BrowseNode browseNode : nods) {
			addNodes(allBrowseNodes, browseNode);
		}

		if (AmazonDVDItem.isBR(allBrowseNodes)) {
			setMediaType(DVDItem.MEDIATYPE_BR);
		} else if (AmazonDVDItem.isHD(allBrowseNodes)) {
			setMediaType(DVDItem.MEDIATYPE_HD);
		} else if (AmazonDVDItem.isDVD(allBrowseNodes)) {
			setMediaType(DVDItem.MEDIATYPE_DVD);
		} else {
			throw new InstantiationError(
					"Cannot instantiate AmazonDVDItem. Item from Amazon is no DVD.");
		}

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
		if (item.getItemAttributes().getAudienceRating() != null)
			setRating(item.getItemAttributes().getAudienceRating());
		// if (item.getItemAttributes().getRunningTime() != null
		// && item.getItemAttributes().getRunningTime().getUnits()
		// .contains("inute"))
		// setLength(item.getItemAttributes().getRunningTime().get_value()
		// .intValue());
		// if (item.getItemAttributes().getManufacturer() != null)
		// setLabel(item.getItemAttributes().getManufacturer());
		if (item.getItemAttributes().getRegionCode() != null
				&& item.getItemAttributes().getRegionCode().length() == 1)
			setRegionCode(item.getItemAttributes().getRegionCode().charAt(0));
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

		String cinemaDateString = item.getItemAttributes()
				.getTheatricalReleaseDate();
		if (cinemaDateString != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date cinemaDate = sdf.parse(cinemaDateString);
				setCinemaDate(cinemaDate);
			} catch (Exception e) {
				System.out.println("Unable to parse release date "
						+ cinemaDateString + ". " + e.getMessage());
			}
		}
	}

	private void addNodes(Set<String> cats, BrowseNode node) {
		cats.add(node.getBrowseNodeId());
		if (node.getAncestors() != null
				&& node.getAncestors().getBrowseNode().size() > 0) {
			for (BrowseNode bn : node.getAncestors().getBrowseNode()) {
				addNodes(cats, bn);
			}
		}
	}

}
