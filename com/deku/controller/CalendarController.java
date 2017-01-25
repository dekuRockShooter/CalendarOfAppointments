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

public class CalendarController {

    private CalendarModel model;

    /**
     * Default constructor.
     */
    public CalendarController() throws SQLException {
        model = CalendarModel.getInstance();
    }

     /**
     * Insert a new appointment.
     *
     * @param date the date to insert.  This should include day, month,
     *             year, hour, and minute
     * @param ssn the SSN of the person making the appointment
     * @throws SQLException if there is any error with the database
     * @throws SQLTimeoutException if there is any error with the database
     */
    public void insert(Calendar datetime, String ssn)
            throws SQLException, SQLTimeoutException {
        // Get first name, last name, hour, minuute, day of week, month,
        // and year of all patients of the given week.
        String datetimeStr = calendarToString(datetime);
        model.insert(datetimeStr, "%Y-%m-%e %k:%i", ssn);
    }

     /**
     * Insert a new appointment.
     *
     * @param datetime the date to insert.  This should include day,
     *                 month, year, hour, and minute
     * @param formatStr the format of datetime.  This should be a format
     *                  string recognizable by MySQL (see documentation for
     *                  its STR_TO_DATE function).
     * @param ssn the SSN of the person making the appointment
     * @throws SQLException if there is any error with the database
     * @throws SQLTimeoutException if there is any error with the database
     */
    public void insert(String datetime, String formatStr, String ssn)
            throws SQLException, SQLTimeoutException {
        // Get first name, last name, hour, minuute, day of week, month,
        // and year of all patients of the given week.
        model.insert(datetime, formatStr, ssn);
    }

     /**
     * Insert a new appointment.  This uses a given date to identify the
     * person who has an appointment on it.  After identification, the
     * new appointment is made.
     *
     * @param datetime the date to insert.  This should include day, month,
     *             year, hour, and minute
     * @param date the appointment held by the person
     * @throws SQLException if there is any error with the database
     * @throws SQLTimeoutException if there is any error with the database
     */
    public void insert(Calendar datetime, Calendar date)
            throws SQLException, SQLTimeoutException {
        String datetimeStr = calendarToString(date);
        String ssn = model.getSSN(datetimeStr, "%Y-%m-%e %k:%i");
        datetimeStr = calendarToString(datetime);
        model.insert(datetimeStr, "%Y-%m-%e %k:%i", ssn);
    }

    /**
     * Delete an appointment.
     *
     * @param datetime the date to delete.  This should include day, month,
     *                 year, hour, and minute
     * @throws SQLException if there is any error with the database
     * @throws SQLTimeoutException if there is any error with the database
     */
    public void delete(Calendar datetime)
            throws SQLException, SQLTimeoutException {
        String datetimeStr = calendarToString(datetime);
        model.delete(datetimeStr, "%Y-%m-%e %k:%i");
    }

    /**
     * Copy an appointment to other dates.
     *
     * @param dateSrc the date to copy.  This should include day, month, year,
     *                hour, and minute
     * @param dateDest array of Calendar objects that denote the dates to
     *                 copy to.  These should include day, month, year, hour,
     *                 and minute
     * @throws SQLException if there is any error with the database
     * @throws SQLTimeoutException if there is any error with the database
     */
    public void copy(Calendar dateSrc, Calendar[] dateDest)
            throws SQLException, SQLTimeoutException {
        String src = calendarToString(dateSrc);
        String[] dest = new String[dateDest.length];
        int idx = 0;
        // Convert all Calendars to strings.
        while (idx < dateDest.length) {
            dest[idx] = calendarToString(dateDest[idx]);
            ++idx;
        }
        model.copy(src, dest, "%Y-%m-%e %k:%i");
    }

    private String calendarToString(Calendar cal) {
        String min = cal.get(Calendar.MINUTE) < 10 ?
                     "0" + cal.get(Calendar.MINUTE) :
                     "" + cal.get(Calendar.MINUTE);
        String ret = String.format("%s-%s-%s %s:%s",
                                   cal.get(Calendar.YEAR),
                                   cal.get(Calendar.MONTH) + 1,
                                   cal.get(Calendar.DAY_OF_MONTH),
                                   cal.get(Calendar.HOUR_OF_DAY),
                                   min);
        System.err.println("Date: " + ret);
        return ret;
    }

}
