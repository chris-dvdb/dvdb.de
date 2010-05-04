package de.dvdb.domain.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.jboss.seam.annotations.security.management.RoleName;

@Entity
@Table(name = "dvdb2_role")
@org.hibernate.annotations.Table(appliesTo = "dvdb2_role", indexes = {})
public class Role {
	private Integer id;
	private String rolename;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@RoleName
	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
}