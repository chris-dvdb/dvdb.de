package de.dvdb.domain.model.mediabase;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.dvdb.domain.model.user.User;

@Entity
@Table(name = "dvdb2_mediabase_visit")
public class MediabaseVisit implements Serializable {
	private static final long serialVersionUID = 3833189124977801528L;

	private Long id;

	private Mediabase mediabase;

	private User visitor;

	private Date dateOfVisit;

	private String sessionId;

	@javax.persistence.Id
	@javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(optional = true)
	@JoinColumn(name = "visitor_id", nullable = true)
	public User getVisitor() {
		return visitor;
	}

	public void setVisitor(User visitor) {
		this.visitor = visitor;
	}

	@Column(name = "sessionid", length = 50)
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Column(name = "dateofvisit")
	public Date getDateOfVisit() {
		return dateOfVisit;
	}

	public void setDateOfVisit(Date dateOfVisit) {
		this.dateOfVisit = dateOfVisit;
	}

	@javax.persistence.ManyToOne(optional = false)
	@javax.persistence.JoinColumn(name = "mediabase_id")
	public Mediabase getMediabase() {
		return mediabase;
	}

	public void setMediabase(Mediabase mediabase) {
		this.mediabase = mediabase;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 == null)
			return false;
		if (!(arg0 instanceof MediabaseVisit))
			return false;
		MediabaseVisit u = (MediabaseVisit) arg0;
		if (u.getId() == null)
			return false;
		if (this.getId() == null)
			return false;
		return getId().equals(u.getId());
	}
}
