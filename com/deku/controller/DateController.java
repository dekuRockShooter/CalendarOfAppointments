package com.deku.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import com.deku.model.CalendarModel;


// Notice, do not import com.mysql.jdbc.*
// or you will have problems!

public class DateController {

    private static CalendarModel model;

    public DateController() throws SQLException {
        model = CalendarModel.getInstance();
    }

    /**
     * Return all appointments in a given week.
     * Arg must be of format YYYY-MM-DD or YYYY-M-D
     *
     * @param date a Calendar instance specifying the week to get all
     *             appointments of.  This should contain year, month,
     *             and day.
     * @return a list of maps with keys day, hour_min, month, year,
     *         FirstName, and LastName.  Each map holds information
     *         about the appointment for one person.  The maps are
     *         ordered from earliest to latest time and day.  If the
     *         appointments are 8 on Sun, 8 on Tue, 1400 on Sun, 11
     *         on Thu, then the order they are stored in is 8 on Sun,
     *         8 on Tue, 11 on Thu, 1400 on Sun.
     *         The value of day is the day of the week (1 to 7, where
     *         1 denotes Sunday, and 7 denotes Saturday) that the
     *         appointment is on.
     *         The value of hour_min is the time of the appointment
     *         and is formatted as HHMM, 24-hour time.
     *         The value of month is the month of the appointment (1-12).
     *         The value of year is the year of the appointment (yyyy).
     *         The value of FirstName and LastName is the first and last
     *         name, respectively, of the person making the appointment.
     */
    public List<Map<String, String>> getAppointments(Calendar date)
            throws SQLException, SQLTimeoutException {
        // Get first name, last name, hour, minuute, day of week, month,
        // and year of all patients of the given week.
        String ymd = String.format("%s-%s-%s", date.get(Calendar.YEAR),
                                               date.get(Calendar.MONTH) + 1,
                                               date.get(Calendar.DAY_OF_MONTH));
        List<Map<String, String>> rsList = model.getAppointments(ymd);
        return rsList;
    }

    /**
     * Get the dates for a week.
     *
     * @param date a date.  This should include day, month, and year.
     * @return a map with keys last_day_of_month, first_day_of_week,
     *         cur_month, and cur_year.
     *         The value of last_day_of_month is the last day of the
     *         month that the week starts on (1 to 31).
     *         The value of first_day_of_week is the day of the month
     *         that the week starts on (1 to 31).
     *         The value of cur_month is the month (1 to 12) that the
     *         week starts on.
     *         The value of cur_year is the year that the week starts
     *         on (yyyy).
     */
    public Calendar getWeek(Calendar date)
            throws SQLException, SQLTimeoutException {
        // Get first name, last name, hour, minuute, day of week, month,
        // and year of all patients of the given week.
        // Use this instead of Java's Calendar in order to be consistent
        // with the data in MySQL.
        String ymd = String.format("%d-%d-%d", date.get(Calendar.YEAR),
                                               date.get(Calendar.MONTH) + 1,
                                               date.get(Calendar.DAY_OF_MONTH));
        Map<String, String> week = model.getWeek(ymd).get(0);
        System.out.println(week);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(week.get("cur_year")));
        cal.set(Calendar.MONTH, Integer.parseInt(week.get("cur_month")));
        cal.set(Calendar.DAY_OF_MONTH,
                Integer.parseInt(week.get("first_day_of_week")));
        return cal;
    }

}
