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

}
