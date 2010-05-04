package de.dvdb.domain.model.forum;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "userfield")
public class UserField implements Serializable {
	private static final long serialVersionUID = 3833189124977801528L;

	Integer userid;

	String field5 = "";

	String field6 = "";

	String field7 = "0";

	String temp = "";

	@Id
	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer id) {
		this.userid = id;
	}

	@Column(length = 50000, columnDefinition="MEDIUMTEXT")
	public String getField5() {
		return field5;
	}

	public void setField5(String field5) {
		this.field5 = field5;
	}

	@Column(length = 50000, columnDefinition="MEDIUMTEXT")
	public String getField7() {
		return field7;
	}

	public void setField7(String field7) {
		this.field7 = field7;
	}

	@Column(length = 50000, columnDefinition="MEDIUMTEXT")
	public String getField6() {
		return field6;
	}

	public void setField6(String field6) {
		this.field6 = field6;
	}

	@Column(length = 50000, columnDefinition="MEDIUMTEXT")
	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}
}
