package de.dvdb.web.search;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Name("basicSearchForm")
@Scope(ScopeType.CONVERSATION)
public class BasicSearchForm implements Serializable {

	private static final long serialVersionUID = 8907559561751325862L;

	@Out(required = false)
	private String order;
	private Integer resultLimit;
	private Integer pageNumber = null;
	private Integer pageSize = null;

	public BasicSearchForm() {
		reset();
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getResultLimit() {
		return resultLimit;
	}

	public void setResultLimit(Integer resultLimit) {
		this.resultLimit = resultLimit;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public void reset() {
		this.resultLimit = null;
		this.order = null;
		this.pageNumber = null;
		this.pageSize = null;
	}
}
