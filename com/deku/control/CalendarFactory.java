package com.deku.control;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.event.EventHandler.*;
import javafx.event.EventHandler;
import javafx.event.*;
import javafx.event.Event.*;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ButtonType;
import javafx.util.Callback;

import java.util.Optional;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;
import java.sql.SQLException;

import com.deku.controller.*;
import com.deku.dialog.NewAppointmentDialog;
import com.deku.model.AppointmentInfo;


public class CalendarFactory {

    // Table whose rows show data in TimeSlot.
    private TableView<TimeSlot> table;
    private DateController dateCon;
    private CalendarController calendarCon;
    private PatientsController patientsCon;
    private Calendar curWeek;
    // Store table rows.
    private ObservableList<TimeSlot> data;
    // Column headers.
    private static final String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu",
                                          "Fri", "Sat"};

    /**
     * Factory method for getting a calendar for the current week.
     *
     * @return a TableView set to hold data for the current week
     * @throws SQLException
     */
    public TableView getThisWeek() throws SQLException {
        curWeek = Calendar.getInstance();
        return init();
    }

    /**
     * Factory method for getting a calendar for any week.
     *
     * @param cal a Calendar for a date of the week to show
     * @return a TableView set to hold data for the current week
     * @throws SQLException
     */
    public TableView getWeek(Calendar cal) throws SQLException {
        curWeek = cal;
        return init();
    }

    /**
     * Create the TableView.
     *
     * @return a TableView set to hold data for the given week
     * @throws SQLException
     */
    private TableView init() throws SQLException {
        table = new TableView<>();
        final Label label = new Label("Cal");
        label.setFont(new Font("Arial", 20));
        dateCon = new DateController();
        calendarCon = new CalendarController();
        patientsCon = new PatientsController();

        // Cell selection instead of default row selection.
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.setEditable(true);
        initData();
        initColumns();
        table.setItems(data);
        return table;
    }

    /**
     * Create the dates to show on the headers.
     *
     * @return an array of strings to show in the header row.  The
     *         first element is time, while the others are the date
     *         of a particular day.
     */
    private String[]initHeaders() throws SQLException {
        String[] headers = new String[8];
        headers[0] = "Time";
        Calendar week = dateCon.getWeek(curWeek);
        int curHeader = 1;
        for (String day : days) {
            headers[curHeader] = String.format("%s %d %d, %d",
                                               day,
                                               week.get(Calendar.MONTH) + 1,
                                               week.get(Calendar.DAY_OF_MONTH),
                                               week.get(Calendar.YEAR));
            week.add(Calendar.DAY_OF_MONTH, 1);
            ++curHeader;
        }

        return headers;
    }

    /**
     * Create columns.  This attaches columns to the table and defines
     * what data in TimeSlot is shown for a specific column.
     */
    private void initColumns() throws SQLException {
        String[] headers = initHeaders();
        int j = 0;
        final int NUM_DAYS = 8;
        // Create tables.
        while (j < NUM_DAYS) {
            TableColumn<TimeSlot, String> col = new TableColumn<>(headers[j]);
            col.setMinWidth(100);
            final int day_idx = j;
            // Define what each column returns.
            col.setCellValueFactory(
                    new Callback<CellDataFeatures<TimeSlot, String>,
                    ObservableValue<String>>() {
                        public ObservableValue<String> call(
                                        CellDataFeatures<TimeSlot, String> p) {
                            // Return value stored at the day_idx'th column
                            return p.getValue().getDay(day_idx);
                        }
                    });
            table.getColumns().add(col);
            ++j;
        }
    }

    /**
     * Initialize data to show in the TableView.
     */
    private void initData() throws SQLException {
        // The data to show in the table is done by rows.  Give the table rows
        // and it displays them.  The rows are held in 'data'.  In this table,
        // the rows denote a specific time.  The columns are days.  So each
        // row in 'data' holds a TimeSlot object that has data for each day
        // of that time.
        List<Map<String, String>> appointments =
            dateCon.getAppointments(curWeek);
        Map<String, String> curAppointment = Collections.<String, String>emptyMap();
        if (!appointments.isEmpty()) {
            curAppointment = appointments.get(0);
        }
        data = FXCollections.observableArrayList();
        int curDay = 1; // iterate through all the days.
        int lastDay = 8;
        int curHour = 7; // TODO: make this customizable.
        int lastHour = 18; // TODO: make this customizable.
        int curMin = 0; // TODO: make this customizable.
        int minStep = 15; // How much to increment the minutes by.
        int hourMin = 0; // hhmm
        int appointmentIdx = 0; // The index in the list of the current map.
        String name = "";
        // Iterate through all times (these are the rows).
        while (curHour < lastHour) {
            curDay = 1; // 1 is Sunday in MySQL.
            lastDay = 8; // 7 is Saturday in MySQL.
            hourMin = curHour*100 + curMin;
            TimeSlot curTimeSlot = new TimeSlot();
            curTimeSlot.setDay(0, curHour + ":" + curMin);
            // For each time, iterate through all days from Sun to Sat.  If
            // there is an appointment for the current time and day, then
            // store it,
            while (curDay < lastDay) {
                // Time and days will never be negative, so if the list is
                // empty, set everything to -1 to ensure failure of the
                // conditional expressions.
                int hm = -1;
                int d = -1;
                if (curAppointment != Collections.<String, String>emptyMap()) {
                    hm = Integer.parseInt(curAppointment.get("hour_min"));
                    d = Integer.parseInt(curAppointment.get("day"));
                }
                // Current time and day has an appointment.
                if ((hm == hourMin) && (d == curDay)) {
                    name = curAppointment.get("FirstName")
                           + curAppointment.get("LastName");
                    curTimeSlot.setDay(curDay, name);
                    ++appointmentIdx;
                    try {
                        curAppointment = appointments.get(appointmentIdx);
                    }
                    catch (IndexOutOfBoundsException e) {
                        curAppointment = Collections.<String, String>emptyMap();
                    }
                    System.out.println(curTimeSlot);
                }
                else {
                    curTimeSlot.setDay(curDay, "");
                }
                ++curDay;
            }
            // Go to next time.
            data.add(curTimeSlot);
            curMin = curMin + minStep;
            if (curMin >= 60) {
                curMin = 0;
                ++curHour;
            }
        }
    }


    // TODO: Try to change this to use PropertyValueFactory.
    /**
     * A class to hold appointment data for a time.  This represents all
     * columns of a specifit time and is meant to be used as the data that
     * the TableView shows.
     */
    public static class TimeSlot {
        // days[0] is the time associated with all columns.  The other elements
        // are the full names of the people who have appointments at that time.
        // days[1] is for Sunday, ..., days[8] is for Saturday.
        private final SimpleStringProperty[] days;

        private TimeSlot() {
            this.days = new SimpleStringProperty[8];
        }

        /**
         * Get the name of the person who has an appointment on the given day.
         * If day is 0, then the time is returned.
         */
        // TODO: throw out of bound error
        public ObservableValue<String> getDay(int day) {
            return days[day];
        }

        /**
         * Set the name of the person who has an appointment on the given day.
         * If day is 0, then fName is a HH:MM 24-hour formatted time.
         */
        // TODO: throw out of bound error
        public void setDay(int day, String fName) {
            days[day] = new SimpleStringProperty(fName);
        }
    }
}













