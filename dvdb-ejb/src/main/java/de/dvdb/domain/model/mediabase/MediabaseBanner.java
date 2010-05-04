package de.dvdb.domain.model.mediabase;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.Length;


@Entity
@Table(name = "dvdb2_mediabase_banner")
public class MediabaseBanner implements Serializable {
	private static final long serialVersionUID = 3833189124977801528L;

	private Long id;

	private Mediabase mediabase;

	private Long bannerId;

	private Boolean displayLatestDVD;

	private String line1;

	private String line2;

	private String line3;

	@javax.persistence.Id
	@javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@javax.persistence.ManyToOne(optional = false)
	@javax.persistence.JoinColumn(name = "mediabase_id")
	public Mediabase getMediabase() {
		return mediabase;
	}

	public void setMediabase(Mediabase mediabase) {
		this.mediabase = mediabase;
	}

	@Column(name = "banner_id")
	public Long getBannerId() {
		return bannerId;
	}

	public void setBannerId(Long bannerId) {
		this.bannerId = bannerId;
	}

	@Column(name = "display_latest")
	public Boolean getDisplayLatestDVD() {
		return displayLatestDVD;
	}

	public void setDisplayLatestDVD(Boolean displayLatestDVD) {
		this.displayLatestDVD = displayLatestDVD;
	}

	@Column(name = "line1")
	@Length(max = 255)
	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	@Column(name = "line2")
	@Length(max = 255)
	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	@Column(name = "line3")
	@Length(max = 255)
	public String getLine3() {
		return line3;
	}

	public void setLine3(String line3) {
		this.line3 = line3;
	}

}
