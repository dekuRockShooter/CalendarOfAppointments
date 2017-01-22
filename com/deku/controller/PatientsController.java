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

public class PatientsController {

    private static CalendarModel model;

    public PatientsController() throws SQLException {
        model = CalendarModel.getInstance();
    }

    /**
     * Search for people.  The database is queried for all names that
     * match the given first and last names.  By default, names match
     * if they exactly match the given names.  Fuzzy search can be
     * used instead to be more lenient.
     *
     * @param firstName the first name of the people to search for
     * @param lastName the last name of the people to search for
     * @param fuzzy true to use fuzzy search, false to normal search
     * @return a list of maps with the keys FirstName, LastName, and
     *         SSN that hold the corresponding values of matching
     *         people.
     */
    public List<Map<String, String>> searchNames(String firstName,
                                                 String lastName,
                                                 boolean fuzzy)
            throws SQLException, SQLTimeoutException {
        if (fuzzy) {
            firstName = firstName.replace("", "%");
            lastName = lastName.replace("", "%");
        }
        return model.searchNames(firstName, lastName);
    }

     /**
     * Insert a new person.
     *
     * @param firstName the first name of the person
     * @param lastName the last name of the person
     * @param ssn the SSN of the person
     * @throws SQLException if there is any error with the database
     * @throws SQLTimeoutException if there is any error with the database
     */
    public String insert(String firstName, String lastName, String ssn)
            throws SQLException, SQLTimeoutException {
        return model.insertPatient(firstName, lastName, ssn);
    }

     /**
     * Get all available data about a person.
     *
     * @param ssn the SSN of the person
     * @return a list of maps that represent a different piece of
     *         data for the person.  The keys for each map are option
     *         and value.
     *         The value of the 'option' key is the name of the data option.
     *         The value of the 'value' key is the value of the data option.
     *         If the person does not have a piece of data initialized,
     *         then value of 'value' is "NULL".
     * @throws SQLException if there is any error with the database
     * @throws SQLTimeoutException if there is any error with the database
     */
    public List<Map<String, String>> getData(String ssn)
            throws SQLException, SQLTimeoutException {
        return model.getData(ssn);
    }

     /**
     * Get all available data about a person.
     *
     * @param datetime the date of an appointment held by the person for
     *                 who data will shown.  This must have Calendar's
     *                 HOUR_OF_DAY, MINUTE, DAY_OF_MONTH, WEEK, and,
     *                 MONTH fields.
     * @return a list of maps that represent a different piece of
     *         data for the person.  The keys for each map are option
     *         and value.
     *         The value of the 'option' key is the name of the data option.
     *         The value of the 'value' key is the value of the data option.
     *         If the person does not have a piece of data initialized,
     *         then value of 'value' is "NULL".
     * @throws SQLException if there is any error with the database
     * @throws SQLTimeoutException if there is any error with the database
     */
    public List<Map<String, String>> getData(Calendar datetime)
            throws SQLException, SQLTimeoutException {
        String datetimeStr = calendarToString(datetime);
        String ssn = model.getSSN(datetimeStr, "%Y-%m-%e %k:%i");
        return model.getData(ssn);
    }

     /**
     * Get a piece of data about a person.
     *
     * @param option the name of the data option to get the value of
     * @param datetime the date of an appointment held by the person for
     *                 who data will shown.  This must have Calendar's
     *                 HOUR_OF_DAY, MINUTE, DAY_OF_MONTH, WEEK, and,
     *                 MONTH fields.
     * @return the value of the data option for the person who has an
     *         appointment on the given day.
     * @throws SQLException if there is any error with the database
     * @throws SQLTimeoutException if there is any error with the database
     */
    public String getData(String option, Calendar datetime)
            throws SQLException, SQLTimeoutException {
        String datetimeStr = calendarToString(datetime);
        String ssn = model.getSSN(datetimeStr, "%Y-%m-%e %k:%i");
        return model.getData(option, ssn);
    }

    /**
     * Set the value of a data option for a person.
     *
     * @param ssn the SSN of the person to set the data for
     * @param option the data option to change
     * @param newValue the new value to set the data option to
     * @throws SQLException if there is any error with the database
     * @throws SQLTimeoutException if there is any error with the database
     */
    public void setData(String ssn, String option, String newValue)
            throws SQLException, SQLTimeoutException {
        model.setData(ssn, option, newValue);
    }

    /**
     * Set the value of a data option for a person.
     *
     * @param datetime the date of an appointment held by the person for
     *                 who data will be set.  This must have Calendar's
     *                 HOUR_OF_DAY, MINUTE, DAY_OF_MONTH, WEEK, and,
     *                 MONTH fields.
     * @param option the data option to change
     * @param newValue the new value to set the data option to
     * @throws SQLException if there is any error with the database
     * @throws SQLTimeoutException if there is any error with the database
     */
    public void setData(Calendar datetime, String option, String newValue)
            throws SQLException, SQLTimeoutException {
        String datetimeStr = calendarToString(datetime);
        String ssn = model.getSSN(datetimeStr, "%Y-%m-%e %k:%i");
        model.setData(ssn, option, newValue);
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
        return ret;
    }

}
