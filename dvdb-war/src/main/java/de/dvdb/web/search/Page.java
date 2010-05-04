package de.dvdb.web.search;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

public class Page implements Serializable {

	private static final long serialVersionUID = 2969025827070877827L;
	private static int DEFAULT_PAGE_SIZE = 25;
	private static int DEFAULT_STEP_SIZE = 5;
	private static int MIN_PAGE_SIZE = 2;
	private static int MAX_PAGE_SIZE = 100;

	private List<Object> results;
	private int pageIndex;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private int stepSize;
	private int lastPageIndex;
	private int leftSteps;
	private int rightSteps;
	private boolean leftDots;
	private boolean rightDots;
	private boolean simplePaging;
	private double totalResults;
	private double firstResult;
	private double lastResult;

	@SuppressWarnings("unchecked")
	public Page(Query selectQuery, Query countQuery, Integer index,
			Integer size, Integer resultLimit) {

		if (index == null || index <= 0)
			index = 1;

		// If page index is not zero based
		pageIndex = index;

		// Make sure that the page size is within its limits
		if (size == null || size < MIN_PAGE_SIZE || size > MAX_PAGE_SIZE)
			pageSize = DEFAULT_PAGE_SIZE;
		else
			pageSize = size;

		// You might consider having a min and max as boundaries
		stepSize = DEFAULT_STEP_SIZE;

		// Get the total number of items in the database.
		// If count was of type Long you would lose the precision when
		// calling Math.ceil() --> count is declared as double
		totalResults = (Long) countQuery.getSingleResult();

		System.out.println("Result count " + totalResults);

		if (totalResults <= 0) {
			// You need to handle this situation. One option is to throw an
			// exception or display an error message using FacesMessages.
			return;
		}

		if (resultLimit != null && totalResults > resultLimit)
			totalResults = resultLimit;

		// calculate the number of pages and set last page index
		lastPageIndex = (int) Math.ceil(totalResults / pageSize);

		// make sure that the page index in not out of scope
		if (pageIndex > lastPageIndex)
			pageIndex = 1;

		// calculate the minimum number of paging items to show
		int minPagination = 3 + 2 * stepSize;
		if (lastPageIndex <= minPagination) {
			leftDots = rightDots = false;
			simplePaging = true;
		} else {
			simplePaging = false;
			setLeftDots();
			setRightDots();
			setLeftSteps();
			setRightSteps();
		}

		firstResult = pageSize * (pageIndex - 1);
		lastResult = Math.min(firstResult + pageSize, totalResults);
		results = selectQuery.setMaxResults(pageSize).setFirstResult(
				(int) firstResult).getResultList();

	}

	private void setLeftDots() {
		// current page - (step size + 1) > 1
		// current page - step size > 1 + 1
		// ex, current page = 5 and step size = 2
		// output 1 ... 3 4 5
		// 5 - 2 > 2 --> 3 > 2 --> true
		leftDots = pageIndex > (stepSize + 2);
	}

	private void setRightDots() {
		// current page + (step size + 1) < last page
		// current page + step size < last page - 1
		// ex, current page = 25, step size = 2 and last page = 29
		// output 25 26 27 ... 29
		// 25 + 2 < 29 - 1 --> 27 < 28 --> true
		rightDots = (pageIndex + stepSize) < (lastPageIndex - 1);
	}

	private void setLeftSteps() {
		if (leftDots)
			leftSteps = stepSize;
		else {
			// count = current page - (first + 1)
			// ex, first = 1 and page = 4
			// count = 4 - (1 + 1) = 2
			// output 1 2 3 4
			leftSteps = pageIndex - 2;
		}
	}

	private void setRightSteps() {
		if (rightDots)
			rightSteps = stepSize;
		else {
			// count = last page - ( current page + 1)
			// ex, last = 29 and current = 26
			// count = 29 - 27 = 2
			// output 26 27 28 29
			rightSteps = lastPageIndex - pageIndex - 1;
		}
	}

	public boolean isLeftDots() {
		return leftDots;
	}

	public boolean isRightDots() {
		return rightDots;
	}

	public int getLeftSteps() {
		return leftSteps;
	}

	public int getRightSteps() {
		return rightSteps;
	}

	public List<Object> getResults() {
		return results;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public int getLastPageIndex() {
		return lastPageIndex;
	}

	public boolean showPageIndex() {
		return pageIndex != 1 && pageIndex != lastPageIndex;
	}

	public boolean isSimplePaging() {
		return simplePaging;
	}

	public double getTotalResults() {
		return totalResults;
	}

	public int getPageSize() {
		return pageSize;
	}

	public double getFirstResult() {
		return firstResult + 1;
	}

	public double getLastResult() {
		return lastResult;
	}
}