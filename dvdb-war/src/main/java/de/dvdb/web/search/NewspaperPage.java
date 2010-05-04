package de.dvdb.web.search;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class NewspaperPage extends Page {

	private Integer cols;

	public NewspaperPage(Query selectQuery, Query countQuery, Integer index,
			Integer size, Integer resultLimit, Integer cols) {
		super(selectQuery, countQuery, index, size, resultLimit);
		this.cols = cols;
	}

	public List<Object> getResultRows() {
		
		List outer = new ArrayList();
				
		List inner = new ArrayList();

		for (Object object : getResults()) {
			
			if(inner.size()==cols || outer.size()==0) {
				inner = new ArrayList();
				outer.add(inner);
			}
			
			inner.add(object);
			
		}

		return outer;
	}

}
