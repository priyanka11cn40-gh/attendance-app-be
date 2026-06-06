package com.example.attendance.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public final class WorkingCalendarUtil {

    private WorkingCalendarUtil() {
    }

    public static LocalDate getLastWorkingDayOfMonth(LocalDate dateInMonth) {
        LocalDate lastDay = dateInMonth.with(TemporalAdjusters.lastDayOfMonth());
        while (lastDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
            lastDay = lastDay.minusDays(1);
        }
        return lastDay;
    }

    public static boolean isLastWorkingDayOfMonth(LocalDate date) {
        return date.equals(getLastWorkingDayOfMonth(date));
    }
}
