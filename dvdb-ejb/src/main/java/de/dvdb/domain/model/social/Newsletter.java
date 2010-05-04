package de.dvdb.domain.model.social;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.jboss.seam.annotations.Name;

import de.dvdb.domain.model.user.User;

@Entity
@Table(name = "dvdb2_newsletter")
@Name("newsletter")
public class Newsletter implements Serializable {

	private static final long serialVersionUID = -4256136093566211307L;
	private Long id;
	private String body;
	private String subject;
	private String fromName = "dvdb.de";
	private String fromEmail = "newsletter@dvdb.de";
	private String toEmail;
	private Date dateOfLastEdit = new Date();
	private Integer batchSize = 1;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "batchsize")
	public Integer getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	@Column(name = "fromemail")
	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	@Column(name = "fromname")
	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	@Transient
	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(length = 50000)
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Column(name = "dateoflastedit")
	public Date getDateOfLastEdit() {
		return dateOfLastEdit;
	}

	public void setDateOfLastEdit(Date dateOfLastEdit) {
		this.dateOfLastEdit = dateOfLastEdit;
	}

	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof Newsletter))
			return false;
		User u = (User) arg0;
		return getId().equals(u.getId());
	}

}
