package com.deku.controller;

import javafx.scene.paint.Color;

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
import java.util.HashMap;
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

    /**
     * Get all data options available in the database.
     *
     * @throws SQLException if there is any error with the database
     * @throws SQLTimeoutException if there is any error with the database
     */
    public List<String> getAllOptions()
            throws SQLException, SQLTimeoutException {
        return model.getAllOptions();
    }

    /**
     * Get all data for a specific data option.  All people specify a
     * value for a data option.  For a given data option, this method
     * gets all the different values that people have assigned to it.
     *
     * @param option the option for which all unique data values will
     *               be returned
     * @return a list of all unique values of the data option
     */
    public List<String> getAllData(String option)
            throws SQLException, SQLTimeoutException {
        return model.getAllData(option);
    }

    /**
     * Get the colors for all values of a data option.  This gets the colors
     * to paint each calendar cell with.
     *
     * @param option the option for which all values' colors is returned
     * @return a map of data values to Colors.  The data values (key)
     *         are all the unique values that have been assigned to the
     *         given data option.  The Colors are the color that the user
     *         assigned to the particular data value.
     */
    public Map<String, Color> getColors(String option)
            throws SQLException, SQLTimeoutException {
        List<Map<String, String>> results = model.getColors(option);
        Map<String, Color> valueColorMap = new HashMap<>();
        for (Map<String, String> rgbMap : results) {
            Color color = Color.rgb(Integer.parseInt(rgbMap.get("ColorRed")),
                                    Integer.parseInt(rgbMap.get("ColorGreen")),
                                    Integer.parseInt(rgbMap.get("ColorBlue")));
            valueColorMap.put(rgbMap.get("Value"), color);
        }
        return valueColorMap;
    }

}
