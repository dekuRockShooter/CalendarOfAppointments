package com.deku.model;

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


public class CalendarModel {

    private static CalendarModel model = null;
    private Connection dbCon;

    private CalendarModel() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/calendar";
        String user = "deku";
        String password = " n    mddd";
        dbCon = DriverManager.getConnection(url, user, password);
    }

    /**
     * Open the connection the database.
     * @throws SQLException on failure to connect to the database
     */
    public static CalendarModel getInstance() throws SQLException {
        if (model == null) {
            model = new CalendarModel();
        }
        return model;
    }

    /**
     * Return all appointments in a given week.
     * Arg must be of format YYYY-MM-DD or YYYY-M-D
     *
     * @param date a Calendar instance specifying the week
     */
    public void insert(String datetime, String formatStr, String ssn)
            throws SQLException, SQLTimeoutException {
        String cmd1 = String.format(""
            + "SELECT STR_TO_DATE('%s', '%s')", datetime, formatStr);
        String cmd = String.format(""
            +"INSERT INTO Calendar"
            + "    (Time, SSN) "
            + "VALUES ((%s), '%s')", cmd1, ssn);
        Statement stmt = dbCon.createStatement();
        stmt.executeUpdate(cmd);
    }

    public void delete(String datetime, String formatStr)
        throws SQLException, SQLTimeoutException {
        String cmdTime = String.format(""
            + "SELECT STR_TO_DATE('%s', '%s')", datetime, formatStr);
        String cmd = "DELETE FROM Calendar WHERE Time = (" + cmdTime + ")";
        Statement stmt = dbCon.createStatement();
        stmt.executeUpdate(cmd);
    }

    public void copy(String datetimeSrc, String[] datetimeDest, String formatStr)
            throws SQLException, SQLTimeoutException {
        Statement stmt = dbCon.createStatement();
        String cmdTime = String.format(""
            + "SELECT STR_TO_DATE('%s', '%s')", datetimeSrc, formatStr);
        String cmd = "SELECT SSN FROM Calendar WHERE Time = (" + cmdTime + ")";
        ResultSet rs = stmt.executeQuery(cmd);
        rs.first();
        String ssn = rs.getString("SSN");
        // Start insertions.
        dbCon.setAutoCommit(false);
        for(String datetime : datetimeDest) {
            cmdTime = String.format(""
                    + "SELECT STR_TO_DATE('%s', '%s')", datetime, formatStr);
            cmd = String.format(""
                    + "INSERT INTO Calendar "
                    + "    (Time, SSN) "
                    + "VALUES ((%s), '%s')", cmdTime, ssn);
            stmt.executeUpdate(cmd);
        };
        dbCon.commit();
        dbCon.setAutoCommit(true);
    }

    /**
     * Return all appointments in a given week.
     * Arg must be of format YYYY-MM-DD or YYYY-M-D
     *
     * @param date a Calendar instance specifying the week
     */
    public List<Map<String, String>> getAppointments(String date) 
            throws SQLException, SQLTimeoutException {
        // Get first name, last name, hour, minuute, day of week, month,
        // and year of all patients of the given week.
        String[] keys = {"day", "hour_min", 
                         "month", "year", "FirstName", "LastName"};
        String cmd = String.format(""
            + "SELECT "
            + "    DAYOFWEEK(c.Time) AS %s, "
            + "    EXTRACT(HOUR_MINUTE FROM c.Time) AS %s, "
            + "    EXTRACT(MONTH FROM c.Time) AS %s, "
            + "    EXTRACT(YEAR FROM c.Time) AS %s, "
            + "    p.FirstName AS FirstName, "
            + "    p.LastName AS LastName "
            + "FROM Calendar AS c "
            + "INNER JOIN "
            + "    Patients AS p "
            + "    ON c.SSN = p.SSN "
            + "WHERE WEEK(c.Time) = WEEK('%s') "
            + "ORDER BY hour_min, day", keys[0], keys[1], keys[2], keys[3],
              date);
        Statement stmt = dbCon.createStatement();
        ResultSet rs = stmt.executeQuery(cmd);
        List<Map<String, String>> rsList = resultSetToMapList(keys, rs);
        rs.close();
        return rsList;
    }

    public List<Map<String, String>> getWeek(String date)
            throws SQLException, SQLTimeoutException {
        // Get first day of week, the month the week starts in, the year the
        // week starts in, and the last day of the month that the week start
        // in from a given week.
        String[] keys = {"last_day_of_month", "first_day_of_week", 
                         "cur_month", "cur_year"};
        String cmd = String.format(""
            + "SELECT "
            + "    DAY(LAST_DAY(a.week_date)) AS %s, "
            + "    DAY(a.week_date) AS %s, "
            + "    MONTH(a.week_date) AS %s, "
            + "    YEAR(a.week_date) AS %s "
            + "FROM (SELECT DATE_ADD('%s', "
            + "      INTERVAL 1 - (SELECT DAYOFWEEK('%s')) DAY) "
            + "      AS week_date) AS a", keys[0], keys[1], keys[2], keys[3],
              date, date);
        Statement stmt = dbCon.createStatement();
        ResultSet rs = stmt.executeQuery(cmd);
        List<Map<String, String>> rsList = resultSetToMapList(keys, rs);
        rs.close();
        return rsList;
    }

    public List<Map<String, String>> searchNames(String firstName,
                                                 String lastName)
            throws SQLException, SQLTimeoutException {
        String[] keys = {"FirstName", "LastName", "SSN"};
        String cmd = String.format(""
            + "SELECT "
            + "    FirstName, "
            + "    LastName, "
            + "    SSN "
            + "FROM Patients "
            + "WHERE FirstName LIKE '%s' AND LastName LIKE '%s'",
            firstName, lastName);
        Statement stmt = dbCon.createStatement();
        ResultSet rs = stmt.executeQuery(cmd);
        List<Map<String, String>> rsList = resultSetToMapList(keys, rs);
        rs.close();
        return rsList;
    }

    private List<Map<String, String>> resultSetToMapList(String[] keys,
                                                         ResultSet rs)
            throws SQLException {
        List<Map<String, String>> rsList = new ArrayList<>();
        rs.beforeFirst();
        while (rs.next()) {
            Map<String, String> map = new HashMap<>();
            for (String key : keys) {
                map.put(key, rs.getString(key));
            }
            rsList.add(map);
            rs.next();
        }
        return rsList;
    }


}
