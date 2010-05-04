package de.dvdb.domain.model.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.jboss.seam.annotations.security.TokenUsername;
import org.jboss.seam.annotations.security.TokenValue;

@Entity
@Table(name = "dvdb2_authenticationtoken")
public class AuthenticationToken implements Serializable {

	private static final long serialVersionUID = -6460652292458697548L;

	private Integer tokenId;

	private String username;

	private String value;

	@Id
	@GeneratedValue
	@Column(name = "id")
	public Integer getTokenId() {

		return tokenId;

	}

	public void setTokenId(Integer tokenId) {

		this.tokenId = tokenId;

	}

	@TokenUsername
	public String getUsername() {

		return username;

	}

	public void setUsername(String username) {

		this.username = username;

	}

	@TokenValue
	public String getValue() {

		return value;

	}

	public void setValue(String value) {

		this.value = value;

	}

}