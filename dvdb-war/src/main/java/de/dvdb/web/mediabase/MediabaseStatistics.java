package de.dvdb.web.mediabase;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

import de.dvdb.domain.shared.StatisticsContext;
import de.dvdb.web.Actor;

@Name("mediabaseStatistics")
public class MediabaseStatistics {

	@In
	Actor actor;

	@In
	protected EntityManager dvdb;

	@Logger
	Log log;

	String selectedStatistics;

	public String getSelectedStatistics() {
		return selectedStatistics;
	}

	public void setSelectedStatistics(String selectedStatistics) {
		this.selectedStatistics = selectedStatistics;
	}

	private static String[] stats = new String[] { "all", "shops", "rating",
			"genre" };

	public void selectStatistics() {

		if (getSelectedStatistics().equals("all"))
			pricesAllDVDs();
		else if (getSelectedStatistics().equals("shops"))
			shops();
		else if (getSelectedStatistics().equals("rating"))
			rating();
		else if (getSelectedStatistics().equals("genre"))
			genre();

	}

	public String[] getAvailableStatistics() {
		return stats;
	}

	@SuppressWarnings("unchecked")
	@Begin(join = true)
	public void pricesAllDVDs() {

		List<Object> result = dvdb
				.createNativeQuery(
						"select year(dateofpurchase), month(dateofpurchase), "
								+ " count(item_id), sum(purchaseprice), yearweek(dateofpurchase)"
								+ " from dvdb2_mediabase_item "
								+ " where mediabase_id = :mediabaseid "
								+ " group by year(dateofpurchase), month(dateofpurchase) "
								+ " order by year(dateofpurchase) desc, month(dateofpurchase) desc")
				.setParameter("mediabaseid",
						actor.getUser().getMediabase().getId()).getResultList();

		StatisticsContext statisticsContext = (StatisticsContext) org.jboss.seam.Component
				.getInstance(StatisticsContext.class);

		statisticsContext.setResult(result);
		statisticsContext.setContextAlias("statistics.all");
		statisticsContext.setColumnTypes(new StatisticsContext.ColumnType[] {
				StatisticsContext.ColumnType.YEAR,
				StatisticsContext.ColumnType.MONTH,
				StatisticsContext.ColumnType.NUMBER,
				StatisticsContext.ColumnType.MONEY });
		statisticsContext.setSortColumns(new int[] { 4, 4, 2, 3 });
	}

	@SuppressWarnings("unchecked")
	@Begin(join = true)
	public void shops() {

		List<Object> result = dvdb
				.createNativeQuery(
						"select purchaseshop, count(item_id), sum(purchaseprice) from dvdb2_mediabase_item where mediabase_id = :mediabaseid group by purchaseshop order by sum(purchaseprice) desc")
				.setParameter("mediabaseid",
						actor.getUser().getMediabase().getId()).getResultList();

		StatisticsContext statisticsContext = (StatisticsContext) org.jboss.seam.Component
				.getInstance(StatisticsContext.class);

		statisticsContext.setResult(result);
		statisticsContext.setContextAlias("statistics.shops");
		statisticsContext.setColumnTypes(new StatisticsContext.ColumnType[] {
				StatisticsContext.ColumnType.SHOP,
				StatisticsContext.ColumnType.NUMBER,
				StatisticsContext.ColumnType.MONEY });
		statisticsContext.setSortColumns(new int[] { 0, 1, 2 });

	}

	@SuppressWarnings("unchecked")
	@Begin(join = true)
	public void rating() {

		List<Object> result = dvdb
				.createNativeQuery(
						"select rating, count(item_id), sum(purchaseprice) from dvdb2_mediabase_item join dvdb2_item on dvdb2_item.id = dvdb2_mediabase_item.item_id where mediabase_id = :mediabaseid group by rating order by count(item_id) desc")
				.setParameter("mediabaseid",
						actor.getUser().getMediabase().getId()).getResultList();

		StatisticsContext statisticsContext = (StatisticsContext) org.jboss.seam.Component
				.getInstance(StatisticsContext.class);

		statisticsContext.setResult(result);
		statisticsContext.setContextAlias("statistics.shops");
		statisticsContext.setColumnTypes(new StatisticsContext.ColumnType[] {
				StatisticsContext.ColumnType.RATING,
				StatisticsContext.ColumnType.NUMBER,
				StatisticsContext.ColumnType.MONEY });
		statisticsContext.setSortColumns(new int[] { 0, 1, 2 });

	}

	@SuppressWarnings("unchecked")
	@Begin(join = true)
	public void genre() {

		List<Object> result = dvdb
				.createNativeQuery(
						"select value, count(dvdb2_tag.item_id), sum(purchaseprice) from dvdb2_mediabase_item join dvdb2_item on dvdb2_item.id = dvdb2_mediabase_item.item_id right join dvdb2_tag on dvdb2_tag.item_id = dvdb2_item.id where mediabase_id = :mediabaseid group by value order by count(dvdb2_tag.item_id) desc")
				.setParameter("mediabaseid",
						actor.getUser().getMediabase().getId()).getResultList();

		StatisticsContext statisticsContext = (StatisticsContext) org.jboss.seam.Component
				.getInstance(StatisticsContext.class);

		statisticsContext.setResult(result);
		statisticsContext.setContextAlias("statistics.genre");
		statisticsContext.setColumnTypes(new StatisticsContext.ColumnType[] {
				StatisticsContext.ColumnType.GENRE,
				StatisticsContext.ColumnType.NUMBER,
				StatisticsContext.ColumnType.MONEY });
		statisticsContext.setSortColumns(new int[] { 0, 1, 2 });

	}
}
