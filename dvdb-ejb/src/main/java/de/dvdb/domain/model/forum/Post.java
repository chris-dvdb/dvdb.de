package de.dvdb.domain.model.forum;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "post")
public class Post implements Serializable {

	private static final long serialVersionUID = -1687106705246575246L;
	private Integer postid;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getPostid() {
		return postid;
	}

	public void setPostid(Integer postid) {
		this.postid = postid;
	}

}
