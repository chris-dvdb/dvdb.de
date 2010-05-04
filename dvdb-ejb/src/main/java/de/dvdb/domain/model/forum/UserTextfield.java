package de.dvdb.domain.model.forum;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "usertextfield")
public class UserTextfield implements Serializable {
	private static final long serialVersionUID = 3833189124977801528L;

	Integer userid;

	String signature = "";

	String searchprefs = "";

	String ignorelist = "";

	String buddylist = "";

	String pmfolders = "";

	String subfolders = "";

	@Id
	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer id) {
		this.userid = id;
	}

	@Column(length = 100000, columnDefinition="MEDIUMTEXT")
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	@Column(length = 100000, columnDefinition="MEDIUMTEXT")
	public String getSearchprefs() {
		return searchprefs;
	}

	public void setSearchprefs(String searchprefs) {
		this.searchprefs = searchprefs;
	}

	@Column(length = 100000, columnDefinition="MEDIUMTEXT")
	public String getIgnorelist() {
		return ignorelist;
	}

	public void setIgnorelist(String ignorelist) {
		this.ignorelist = ignorelist;
	}

	@Column(length = 100000, columnDefinition="MEDIUMTEXT")
	public String getBuddylist() {
		return buddylist;
	}

	public void setBuddylist(String buddylist) {
		this.buddylist = buddylist;
	}

	@Column(length = 100000, columnDefinition="MEDIUMTEXT")
	public String getPmfolders() {
		return pmfolders;
	}

	public void setPmfolders(String pmfolders) {
		this.pmfolders = pmfolders;
	}

	@Column(length = 100000, columnDefinition="MEDIUMTEXT")
	public String getSubfolders() {
		return subfolders;
	}

	public void setSubfolders(String subfolders) {
		this.subfolders = subfolders;
	}

}
