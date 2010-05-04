package de.dvdb.web.social.blog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("blogSearchForm")
@Scope(ScopeType.CONVERSATION)
public class BlogSearchForm implements Serializable {

	private static final long serialVersionUID = 8907559561751325862L;

	private Boolean published;

	private Date endDate;

	private Date startDate;

	private String byUser;

	private List<String> tags;

	public BlogSearchForm() {
		reset();
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getByUser() {
		return byUser;
	}

	public void setByUser(String byUser) {
		this.byUser = byUser;
	}

	public void reset() {
		tags = new ArrayList<String>();
		published = true;
		startDate = null;
		endDate = null;
		byUser = null;
	}

	@Override
	public String toString() {
		return "Search criteria: " + tags.toString();
	}
}
