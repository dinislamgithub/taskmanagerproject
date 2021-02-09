package com.din.cardinity.utils;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class DateUtility {
	public String taskDurationDay(Date start_createDate, Date end_dueDate) {
		if (end_dueDate != null) {
			LocalDate startDate = LocalDate.of(start_createDate.getYear(), start_createDate.getMonth(),
					start_createDate.getDay());
			LocalDate endDate = LocalDate.of(end_dueDate.getYear(), end_dueDate.getMonth(), end_dueDate.getDay());
			Period period = Period.between(startDate, endDate);
			int years = Math.abs(period.getYears());
			int months = Math.abs(period.getMonths());
			int days = Math.abs(period.getDays());
			return String.valueOf(years + " Y, " + months + " M, " + days + " D");
		}
		return null;
	}

}
