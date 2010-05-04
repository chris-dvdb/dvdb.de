package de.dvdb.domain.model.forum;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "thread")
public class Thread implements Serializable {

	private static final long serialVersionUID = -1687106705246575246L;
	private String title;
	private Integer replyCount;
	private String lastPoster;
	private Integer forumid;
	private Integer lastPost;
	private Integer threadid;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getThreadid() {
		return threadid;
	}

	public void setThreadid(Integer threadid) {
		this.threadid = threadid;
	}

	@Column(name = "title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "replycount")
	public Integer getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(Integer replyCount) {
		this.replyCount = replyCount;
	}

	@Column(name = "lastposter")
	public String getLastPoster() {
		return lastPoster;
	}

	public void setLastPoster(String lastPoster) {
		this.lastPoster = lastPoster;
	}

	@Column(name = "lastpost")
	public Integer getLastPost() {
		return lastPost;
	}

	public void setLastPost(Integer lastPost) {
		this.lastPost = lastPost;
	}

	public Integer getForumid() {
		return forumid;
	}

	public void setForumid(Integer forumid) {
		this.forumid = forumid;
	}
}
