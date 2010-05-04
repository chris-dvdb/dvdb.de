package de.dvdb.domain.shared;

import java.util.Calendar;
import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("dateTimeQueryHelper")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class DateTimeQueryHelper {

	public Date getYesterday() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.add(Calendar.DATE, -1);
		return today.getTime();
	}

	public Date getToday() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		return today.getTime();
	}

	public Date getTomorrow() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.add(Calendar.DATE, 1);
		return today.getTime();
	}

	public Date getNextDay(Date todayDate) {
		Calendar today = Calendar.getInstance();
		today.setTime(todayDate);
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.add(Calendar.DATE, 1);
		return today.getTime();
	}

	public Date getOneMonthAgo() {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date());
		c1.add(Calendar.MONTH, -1);
		return c1.getTime();
	}

	public Date getOneWeekAgo() {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date());
		c1.add(Calendar.DAY_OF_MONTH, -7);
		return c1.getTime();
	}

	public Date getOneWeek() {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date());
		c1.add(Calendar.DAY_OF_MONTH, 7);
		return c1.getTime();
	}

	public Date getTwoWeeksAgo() {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date());
		c1.add(Calendar.DAY_OF_MONTH, -14);
		return c1.getTime();
	}

	public Date getThreeMonthAgo() {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date());
		c1.add(Calendar.MONTH, -3);
		return c1.getTime();
	}

	public Integer remainingDays(Date date) {
		if (date == null)
			return null;

		double remaining = (double) date.getTime() - getToday().getTime();
		if (remaining == 0d)
			return 0;

		return (int) (remaining / 1000d / 60d / 60d / 24d);

	}

	public String getTimezone() {
		return "GMT+2";
	}

	public java.util.TimeZone getTimeZone() {
		return java.util.TimeZone.getTimeZone(getTimezone());
	}
}
