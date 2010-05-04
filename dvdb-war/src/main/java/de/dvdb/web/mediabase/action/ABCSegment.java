package de.dvdb.web.mediabase.action;

import java.util.ArrayList;
import java.util.List;

import de.dvdb.domain.model.mediabase.MediabaseItem;

public class ABCSegment {

	List<MediabaseItem> items;

	public ABCSegment(List<MediabaseItem> items) {
		this.items = items;
	}

	public List<MediabaseItem> getStartingWith(String prefixes) {
		List<MediabaseItem> list = new ArrayList<MediabaseItem>();

		for (MediabaseItem mediabaseItem : this.items) {

			for (int i = 0; i < prefixes.length(); i++) {
				if (mediabaseItem.getItem().getTitleLex().startsWith(
						prefixes.substring(i, i + 1)))
					list.add(mediabaseItem);
			}
		}

		return list;
	}

}
