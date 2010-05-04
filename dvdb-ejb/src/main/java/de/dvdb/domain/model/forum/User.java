package de.dvdb.domain.model.forum;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "user")
public class User implements Serializable {
	private static final long serialVersionUID = 3833189124977801528L;

	Integer userid;

	String username;

	String password;

	String email;

	String parentemail = "0";

	String homepage = "";

	String icq = "";

	String aim = "";

	String yahoo = "";

	String msn = "";

	String skype = "";

	String usertitle = "Newbie";

	// anzahl sekunden seit 1.1.1970
	Integer joindate;

	Short daysprune = -1;

	// anzahl sekunden seit 1.1.1970
	Integer lastactivity;

	// anzahl sekunden seit 1.1.1970
	Integer lastvisit;

	Short usergroupid = 3;

	String timezoneoffset = "+1";

	Integer options = 6583;

	Date birthday_search;

	String birthday = "";

	Short maxposts = -1;

	Short startofweek = 2;

	String ipaddress = "";

	Integer referrerid = 1;

	Short avatarid = 0;

	String salt = "";

	Date passworddate = new Date();

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer id) {
		this.userid = id;
	}

	@Column(name = "username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String nickname) {
		this.username = nickname;
	}

	@NotNull
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAim() {
		return aim;
	}

	public void setAim(String aim) {
		this.aim = aim;
	}

	public Short getAvatarid() {
		return avatarid;
	}

	public void setAvatarid(Short avatarid) {
		this.avatarid = avatarid;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	@Column(name = "birthday_search")
	public Date getBirthdaySearch() {
		return birthday_search;
	}

	public void setBirthdaySearch(Date birthday_search) {
		this.birthday_search = birthday_search;
	}

	public Short getDaysprune() {
		return daysprune;
	}

	public void setDaysprune(Short daysprune) {
		this.daysprune = daysprune;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getIcq() {
		return icq;
	}

	public void setIcq(String icq) {
		this.icq = icq;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public Integer getJoindate() {
		return joindate;
	}

	public void setJoindate(Integer joindate) {
		this.joindate = joindate;
	}

	public Integer getLastactivity() {
		return lastactivity;
	}

	public void setLastactivity(Integer lastactivity) {
		this.lastactivity = lastactivity;
	}

	public Integer getLastvisit() {
		return lastvisit;
	}

	public void setLastvisit(Integer lastvisit) {
		this.lastvisit = lastvisit;
	}

	public Short getMaxposts() {
		return maxposts;
	}

	public void setMaxposts(Short maxposts) {
		this.maxposts = maxposts;
	}

	public Integer getOptions() {
		return options;
	}

	public void setOptions(Integer options) {
		this.options = options;
	}

	public String getParentemail() {
		return parentemail;
	}

	public void setParentemail(String parentemail) {
		this.parentemail = parentemail;
	}

	public Date getPassworddate() {
		return passworddate;
	}

	public void setPassworddate(Date passworddate) {
		this.passworddate = passworddate;
	}

	public Integer getReferrerid() {
		return referrerid;
	}

	public void setReferrerid(Integer referrerid) {
		this.referrerid = referrerid;
	}

	@Type(type = "de.dvdb.domain.model.forum.TrimmedStringType")
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Short getStartofweek() {
		return startofweek;
	}

	public void setStartofweek(Short startofweek) {
		this.startofweek = startofweek;
	}

	public String getTimezoneoffset() {
		return timezoneoffset;
	}

	public void setTimezoneoffset(String timezoneoffset) {
		this.timezoneoffset = timezoneoffset;
	}

	public Short getUsergroupid() {
		return usergroupid;
	}

	public void setUsergroupid(Short usergroupid) {
		this.usergroupid = usergroupid;
	}

	public String getUsertitle() {
		return usertitle;
	}

	public void setUsertitle(String usertitle) {
		this.usertitle = usertitle;
	}

	public String getYahoo() {
		return yahoo;
	}

	public void setYahoo(String yahoo) {
		this.yahoo = yahoo;
	}

	public String getMsn() {
		return msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	@Type(type = "de.dvdb.domain.model.forum.TrimmedStringType")
	public String getSkype() {
		return skype;
	}

	public void setSkype(String skype) {
		this.skype = skype;
	}

	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof User))
			return false;
		User u = (User) arg0;
		return getUsername().equals(u.getUsername());
	}

	@Override
	public String toString() {
		return "User: " + getUsername();
	}

}
