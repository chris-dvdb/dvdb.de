package de.dvdb.domain.model.mediabase;

import java.io.Serializable;

import javax.persistence.Column;

@javax.persistence.Entity
@javax.persistence.Table(name = "dvdb2_mediabaseskin")
public class Skin implements Serializable {

	private static final long serialVersionUID = 253815690158331276L;

	private Long id;

	private String name;

	private String template0Columns;

	private String template2Columns;

	private String template3Columns;

	@javax.persistence.Id
	@javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "template0columns")
	public String getTemplate0Columns() {
		return template0Columns;
	}
	
	public void setTemplate0Columns(String template0Columns) {
		this.template0Columns = template0Columns;
	}

	@Column(name = "template2columns")
	public String getTemplate2Columns() {
		return template2Columns;
	}

	public void setTemplate2Columns(String template2Columns) {
		this.template2Columns = template2Columns;
	}

	@Column(name = "template3columns")
	public String getTemplate3Columns() {
		return template3Columns;
	}

	public void setTemplate3Columns(String template3Columns) {
		this.template3Columns = template3Columns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
