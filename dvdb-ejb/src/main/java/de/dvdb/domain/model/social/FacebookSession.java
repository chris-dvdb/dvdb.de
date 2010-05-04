package de.dvdb.domain.model.social;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dvdb2_fbsession")
public class FacebookSession implements Serializable {

	private static final long serialVersionUID = -8753714944734959457L;

	private Long id;

	private String sessionKey;

	private Long user;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "user_id")
	public Long getUser() {
		return user;
	}

	public void setUser(Long user) {
		this.user = user;
	}

	@Column(name = "sessionkey")
	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

}
