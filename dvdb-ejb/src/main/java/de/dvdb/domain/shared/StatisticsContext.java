package de.dvdb.domain.shared;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@AutoCreate
@Name("statisticsContext")
@Scope(ScopeType.CONVERSATION)
public class StatisticsContext implements Serializable {

	private static final long serialVersionUID = -2515101762301351002L;

	String contextAlias;
	List<Object> result;

	public String getContextAlias() {
		return contextAlias;
	}

	public void setContextAlias(String contextAlias) {
		this.contextAlias = contextAlias;
	}

	public String getHeadlineKey() {
		return contextAlias + ".headline";
	}

	public String getBrowserTitleKey() {
		return contextAlias + ".browserTitle";
	}

	public String getIntroKey() {
		return contextAlias + ".intro";
	}

	public List<Object> getResult() {
		return result;
	}

	public void setResult(List<Object> result) {
		this.result = result;
	}

	ColumnType[] columnTypes;

	int[] sortColumns;

	public int[] getSortColumns() {
		return sortColumns;
	}

	public void setSortColumns(int[] sortColumns) {
		this.sortColumns = sortColumns;
	}

	public ColumnType[] getColumnTypes() {
		return columnTypes;
	}

	public void setColumnTypes(ColumnType[] columnTypes) {
		this.columnTypes = columnTypes;
	}

	public enum ColumnType {
		MONTH, YEAR, NUMBER, MONEY, SHOP, RATING, GENRE
	};

}
