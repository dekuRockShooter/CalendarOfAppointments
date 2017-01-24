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
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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
     * Get all available appointments in a given week.
     *
     * @param date the week to get all available appointments.  This should
     *             have Calendar's MONTH, YEAR, and, DAY_OF_MONTH fields
     *             set.
     * @return a list of Calendars that represent one available appointment
     *         in the given week.  The list is sorted in increasing time
     *         and date (Sun@7, Mon@7, ..., Sat@7, Sun@7:15, Mon@7:15, ...).
     *         The calendars have the Calendar's MONTH, YEAR, DAY_OF_MONTH,
     *         MINUTE, and HOUR_OF_DAY fields.
     */
    public List<Calendar> getFreeAppointments(Calendar date)
            throws SQLException, SQLTimeoutException {
        List<Map<String, String>> rsList = getAppointments(date);
        List<Calendar> freeList = new ArrayList<>(rsList.size());
        int startHour = 7;
        int lastHour = 19;
        int startMin = 0;
        int lastMin = 0;
        int step = 15;
        Map<String, String> curAppointment;
        if (rsList.isEmpty()) {
            curAppointment = new HashMap<>();
            curAppointment.put("day", "-1");
            curAppointment.put("hour_min", "-1");
            curAppointment.put("month", "-1");
            curAppointment.put("year", "-1");
            curAppointment.put("FirstName", "-1");
            curAppointment.put("LastName", "-1");
        }
        else {
            curAppointment = rsList.get(0);
        }
        int curAppointmentDay = Integer.parseInt(curAppointment.get("day"));
        int curAppointmentHrMin = Integer.parseInt(curAppointment.get("hour_min"));
        int curAppointmentIdx = 0;
        int curDay = 1;
        int lastDay = 8;
        int hourMin = 0;
        // This calendar has the date for the beginning of the week (Sunday).
        Calendar curDayCal = (Calendar) date.clone();
        curDayCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        // for time
        //   for day
        //     if curDayTime == appDayTime
        //       appDayTime = nextAppDayTime
        //     else
        //       add curDayTime to freeList
        while (startHour < lastHour) {
            curDay = 1;
            lastDay = 8;
            hourMin = startHour*100 + startMin;
            // Reset to beginning of week.
            curDayCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            while (curDay < lastDay) {
                if ((curAppointmentHrMin == hourMin)
                    && (curDay == curAppointmentDay)) {
                        ++curAppointmentIdx;
                        if (curAppointmentIdx == rsList.size()) {
                            curAppointmentHrMin = -1;
                            curAppointmentDay = -1;
                        }
                        else {
                            curAppointment = rsList.get(curAppointmentIdx);
                            curAppointmentDay = Integer.parseInt(
                                    curAppointment.get("day"));
                            curAppointmentHrMin = Integer.parseInt(
                                    curAppointment.get("hour_min"));
                        }
                }
                else {
                    curDayCal.set(Calendar.MINUTE, startMin);
                    curDayCal.set(Calendar.HOUR_OF_DAY, startHour);
                    freeList.add((Calendar) curDayCal.clone());
                }
                curDayCal.add(Calendar.DAY_OF_MONTH, 1);
                ++curDay;
            }
            startMin = startMin + 15;
            if (startMin >= 60) {
                startMin = 0;
                ++startHour;
            }
        }
        return freeList;
    }

    /**
     * Get the dates for a week.
     *
     * @param date a date.  This should include day, month, and year.
     * @return a Calendar whose YEAR, MONTH, and DAY_OF_MONTH are set
     *         to the beginning of the week that contains the 'date'
     *         parameter.  The beginning of the week in a Sunday.
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
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(week.get("cur_year")));
        cal.set(Calendar.MONTH, Integer.parseInt(week.get("cur_month")) - 1);
        cal.set(Calendar.DAY_OF_MONTH,
                Integer.parseInt(week.get("first_day_of_week")));
        return cal;
    }

}
