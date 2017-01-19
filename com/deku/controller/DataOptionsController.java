package com.deku.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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

public class DataOptionsController {

    private static CalendarModel model;

    public DataOptionsController() throws SQLException {
        model = CalendarModel.getInstance();
    }

    /**
     * Create a new data option.  The new data option becomes a piece of
     * data that can be applied to any person.
     *
     * @param option the name of the data option to create
     * @throws SQLException if there is any error with the database
     * @throws SQLTimeoutException if there is any error with the database
     */
    public boolean createOption(String option)
            throws SQLException, SQLTimeoutException {
        try {
            model.setData(option);
        }
        catch (SQLIntegrityConstraintViolationException err) {
            return false;
        }
        return true;
    }

}
