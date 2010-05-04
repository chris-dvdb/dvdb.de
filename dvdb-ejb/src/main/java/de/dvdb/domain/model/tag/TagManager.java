package de.dvdb.domain.model.tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.social.BlogEntry;
import de.dvdb.domain.shared.DateTimeQueryHelper;

@Name("tagManager")
@Scope(ScopeType.APPLICATION)
@AutoCreate
@Startup
public class TagManager implements Serializable {

	private static final long serialVersionUID = -8209177223545019144L;

	@In
	EntityManager dvdb;

	@Logger
	Log log;

	@In
	DateTimeQueryHelper dateTimeQueryHelper;

	// item class + _id -> tags
	Map<String, List<Tag2>> tagsPerObject = new HashMap<String, List<Tag2>>();

	// horror -> list of items (dvd item or blog entry...)
	Map<String, List<Tagable>> objectsPerTag = new HashMap<String, List<Tagable>>();

	List<Tagable> tagables = new ArrayList<Tagable>();

	// horror -> 4 (scale 0 - 9)
	Map<String, Integer> weightPerTag = new HashMap<String, Integer>();

	List<String> allTags = new ArrayList<String>();

	@Create
	@SuppressWarnings("unchecked")
	public void init() {

		List<Tag2> tags = dvdb.createQuery("from Tag2").getResultList();
		for (Tag2 tag2 : tags) {
			if (tag2 instanceof BlogEntryTag) {
				addTagForTagable(((BlogEntryTag) tag2).getBlogEntry(), tag2);
			}
		}

		// refresh tag weight
		updateTagWeight();

		log.info("Initialized tagManager with " + tags.size() + " tags");

	}

	public int getWeight(String tag) {
		return weightPerTag.get(tag);
	}

	private void updateTagWeight() {
		weightPerTag = new HashMap<String, Integer>();
		int numberOfTags = objectsPerTag.size();
		for (String tag : objectsPerTag.keySet()) {

			Double d = ((double) (objectsPerTag.get(tag).size()) / numberOfTags) * 30;

			weightPerTag.put(tag, Math.min(8, d.intValue()));
		}
	}

	public List<String> suggest(Object fragment) {

		String search = (String) fragment;

		List<String> matches = new ArrayList<String>();

		for (String tag : objectsPerTag.keySet()) {

			if (tag.indexOf(search) >= 0) {
				matches.add(tag);
			}

		}

		return matches;

	}

	public List<String> getTagsForListOfTagables(List<Object> tagableIds) {
		Set<String> set = new HashSet<String>();
		for (Object s : tagableIds) {
			List<Tag2> tags = tagsPerObject.get(BlogEntry.class.getName() + "_"
					+ s);
			if (tags == null)
				continue;
			for (Tag2 tag2 : tags) {
				set.add(tag2.getValue());
			}
		}
		return new ArrayList<String>(set);
	}

	Map<String, Integer> tagMap;
	List<String> currentTags;

	public synchronized List<String> getAllCurrentTags() {
		if (currentTags == null) {
			tagMap = new HashMap<String, Integer>();
			currentTags = new ArrayList<String>();
		} else {
			return currentTags;
		}

		for (Tagable tagable : tagables) {
			if (!tagable.getDate().after(dateTimeQueryHelper.getOneWeekAgo()))
				continue;

			List<Tag2> tags = tagsPerObject.get(getKey(tagable));
			for (Tag2 tag2 : tags) {
				Integer i = tagMap.get(tag2.getValue());
				if (i == null) {
					i = 0;
					currentTags.add(tag2.getValue());
				}
				i++;
				tagMap.put(tag2.getValue(), i);
			}
		}
		return currentTags;
	}

	Map<String, Integer> frequentTagMap;
	List<String> frequentTags;

	public synchronized List<String> getFrequentTags() {
		if (frequentTags == null) {
			frequentTagMap = new HashMap<String, Integer>();
			frequentTags = new ArrayList<String>();
		} else {
			return frequentTags;
		}

		for (Tagable tagable : tagables) {
			List<Tag2> tags = tagsPerObject.get(getKey(tagable));
			for (Tag2 tag2 : tags) {
				Integer i = frequentTagMap.get(tag2.getValue());
				if (i == null) {
					i = 0;
				}
				i++;
				frequentTagMap.put(tag2.getValue(), i);
				if (i > 4 && !frequentTags.contains(tag2.getValue()))
					frequentTags.add(tag2.getValue());
			}
		}
		return frequentTags;
	}

	public int getWeightForCurrent(String value) {
		Integer weight = tagMap.get(value);
		currentTags.size();
		Double d = (((double) weight) / currentTags.size())
				* (100 / currentTags.size()) * 100;

		log.info("- " + d + " " + weight + " " + currentTags.size() + " "
				+ value);

		return Math.min(d.intValue() * 2, 9);
	}

	public List<String> getAllTags() {
		return allTags;
	}

	public List<Tag2> getTagsForTagable(Tagable tagable) {

		List<Tag2> tags = tagsPerObject.get(getKey(tagable));
		if (tags == null) {
			tags = new ArrayList<Tag2>();
		}
		return tags;

	}

	public void removeTagsForTagable(Tagable tagable) {

		List<Tag2> oldTagsPerObject = getTagsForTagable(tagable);
		for (Tag2 tag2 : oldTagsPerObject) {
			List<Tagable> tagables = objectsPerTag.get(tag2.getValue());
			if (tagables == null)
				continue;
			tagables.remove(tagable);
		}
		tagsPerObject.remove(getKey(tagable));

		dvdb.createQuery(
				"delete from BlogEntryTag t where t.blogEntry = :blogEntry")
				.setParameter("blogEntry", tagable).executeUpdate();
	}

	public void replaceTagsForTagable(Tagable tagable, List<Tag2> tags) {

		// remove old tags
		removeTagsForTagable(tagable);
		tagables.remove(tagable);

		// add new tags
		for (Tag2 tag2 : tags) {
			// tag2.setId(null);
			BlogEntryTag newTag = new BlogEntryTag();
			newTag.setAlias(tag2.isAlias());
			newTag.setValue(tag2.getValue());
			newTag.setBlogEntry(((BlogEntryTag) tag2).getBlogEntry());
			addTagForTagable(tagable, newTag);
		}

		// refresh tag weight
		updateTagWeight();

	}

	private void addTagForTagable(Tagable tagable, Tag2 tag) {

		// add tag to tagable
		List<Tag2> tags = tagsPerObject.get(getKey(tagable));
		if (tags == null) {
			tags = new ArrayList<Tag2>();
			tagsPerObject.put(getKey(tagable), tags);
		}
		tags.add(tag);

		// add tagable to tag value
		List<Tagable> objects = objectsPerTag.get(tag.getValue());
		if (objects == null) {
			objects = new ArrayList<Tagable>();
			objectsPerTag.put(tag.getValue(), objects);
		}

		objects.add(tagable);

		if (!tagables.contains(tagable))
			tagables.add(tagable);

		if (!allTags.contains(tag.getValue()))
			allTags.add(tag.getValue());

		// persist tag
		if (tag.getId() == null)
			dvdb.persist(tag);

	}

	private String getKey(Tagable tagable) {
		return tagable.getClass().getName() + "_" + tagable.getId();
	}

}
