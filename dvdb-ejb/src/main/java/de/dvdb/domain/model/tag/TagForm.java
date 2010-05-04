package de.dvdb.domain.model.tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;


@Name("tagForm")
@Scope(ScopeType.CONVERSATION)
public class TagForm implements Serializable {

	private static final long serialVersionUID = 1465137947727289943L;

	private String newTag;

	private List<Tag2> tagValues = new ArrayList<Tag2>();

	public String getNewTag() {
		return newTag;
	}

	public void setNewTag(String newTag) {
		this.newTag = newTag.trim().toLowerCase();
	}

	public List<Tag2> getTags() {
		return tagValues;
	}

	public void setTags(List<Tag2> tags) {
		this.tagValues = tags;
	}
	
	public boolean containsTagValue(String value) {
		for (Tag2 tag : getTags()) {
			if(tag.getValue().equalsIgnoreCase(value)) return true;
		}
		return false;
	}

}
