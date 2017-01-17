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
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

import java.util.Optional;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;
import java.sql.SQLException;

import com.deku.controller.*;
import com.deku.dialog.NewAppointmentDialog;
import com.deku.dialog.NewAppointmentDialog.AppointmentInfo;


public class CalendarFactory {

    // Table whose rows show data in TimeSlot.
    private TableView<TimeSlot> table;
    private DateController dateCon;
    private CalendarController calendarCon;
    private PatientsController patientsCon;
    private Calendar curWeek;
    private ContextMenu contextMenu;
    // Store table rows.
    private ObservableList<TimeSlot> data;
    // Column headers.
    private static final String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu",
                                          "Fri", "Sat"};
    private static final String[] months = {"Jan", "Feb", "Mar", "Apr", "May",
                                          "Jun", "Jul", "Aug", "Sep", "Oct",
                                          "Nov", "Dec"};
    private final Calendar[] dates = new Calendar[7];
    private TableCell<TimeSlot, String> contextCell;
    // List of dates to copy to (those selected with ctrl + click).
    // These can span calendars, so the static makes the data viewable
    // by all instances and ensures that pasting will paste to all selected
    // dates.
    private final static List<Calendar> pasteList = new ArrayList<>();
    private static Calendar copyDatetime = null; // Date to copy.
    private static String copyFullName = ""; // Name to copy.
    private int pasteCount; // How many times ctrl+click was done on this table
    private MenuItem copy_item;
    private MenuItem paste_item;
    private MenuItem delete_item;
    private MenuItem lock_item;

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
        pasteCount = 0;
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
        initContextMenu();
        table.setContextMenu(contextMenu);
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
        int datesIdx = 0;
        for (String day : days) {
            dates[datesIdx] = (Calendar) week.clone();
            headers[curHeader] = String.format("%s %s %d, %d",
                                               day,
                                               months[week.get(Calendar.MONTH)],
                                               week.get(Calendar.DAY_OF_MONTH),
                                               week.get(Calendar.YEAR));
            week.add(Calendar.DAY_OF_MONTH, 1);
            ++curHeader;
            ++datesIdx;
        }

        return headers;
    }

    /**
     * Create columns.  This attaches columns to the table and defines
     * what data in TimeSlot is shown for a specific column.
     */
    private void initColumns() throws SQLException {
        // This factory makes each cell listen to mouse events.
        // Source: http://stackoverflow.com/questions/12499269/javafx-tableview-detect-a-doubleclick-on-a-cell
        Callback<TableColumn<TimeSlot, String>,
                 TableCell<TimeSlot, String>> cellFactory =
            new Callback<TableColumn<TimeSlot, String>,
                         TableCell<TimeSlot, String>>() {
                public TableCell<TimeSlot, String> call(TableColumn<TimeSlot,
                                                        String> p) {
                    TableCell<TimeSlot, String> cell = new TableCell<TimeSlot,
                                                                     String>() {
                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty ? null : getString());
                            setGraphic(null);
                        }

                        private String getString() {
                            return getItem() == null ? "" : getItem().toString();
                        }
                    };
                    // Here is where mouse clicks are handled.
                    cell.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            // Ctrl + click
                            if (event.getClickCount() == 1
                                && event.isControlDown()) {
                                TableCell c = (TableCell) event.getSource();
                                if (c.getStyle() == "") {
                                    c.setStyle("-fx-background-color:#00a000");
                                    c.setOpacity(0.4);
                                    pasteList.add(getCalendar(c));
                                    ++pasteCount;
                                }
                                else {
                                    c.setStyle("");
                                    c.setOpacity(1.0);
                                    pasteList.remove(getCalendar(c));
                                    --pasteCount;
                                }
                            }
                            // Double click.
                            else if (event.getClickCount() > 1) {
                                TableCell cell = (TableCell) event.getSource();
                                TableColumn<TimeSlot, String> col =
                                    cell.getTableColumn();
                                String name = cell.getText();
                                String date = col.getText();
                                // Get time.
                                TableRow<TimeSlot> row = cell.getTableRow();
                                TimeSlot timeSlot = row.getItem();
                                String time = timeSlot.getDay(0).getValue();
                                Dialog<AppointmentInfo> dialog;
                                if (name == "") {
                                    NewAppointmentDialog newAppointment =
                                        new NewAppointmentDialog();
                                    try {
                                        dialog = newAppointment.getDialog();
                                        Optional<AppointmentInfo> result =
                                            dialog.showAndWait();
                                        if (result.isPresent()) {
                                            insertIntoCal(result.get(), date,
                                                          time, timeSlot);
                                        }
                                    }
                                    catch (SQLException err) {
                                        // Throw RuntimeException because this
                                        // method does not throw an
                                        // SQLException.
                                        throw new RuntimeException(
                                                    err.toString());
                                    }
                                }
                                else {
                                    System.err.println("edit");
                                }
                            }
                            else if (event.getButton()
                                     == MouseButton.SECONDARY) {
                                contextCell = cell;
                                enableContextMenuItems();
                            }
                        }

                    private void insertIntoCal(AppointmentInfo info,
                                               String date,
                                               String time,
                                               TimeSlot timeSlot)
                            throws SQLException {
                        // TODO: convert date and time to Calendar
                        String ssn = info.getSSN();
                        if (ssn == "") {
                             ssn = patientsCon.insert(info.getFirstName(),
                                                     info.getLastName(),
                                                     "");
                        }
                        calendarCon.insert(date + " " + time,
                                           "%a %b %e, %Y %H:%i",
                                           ssn);
                        // Display inserted name on the calendar.
                        int dayIdx = -1;
                        if (date.startsWith("M")) dayIdx = 2;
                        else if (date.startsWith("Tu")) dayIdx = 3;
                        else if (date.startsWith("W")) dayIdx = 4;
                        else if (date.startsWith("Th")) dayIdx = 5;
                        else if (date.startsWith("F")) dayIdx = 6;
                        else if (date.startsWith("Sa")) dayIdx = 7;
                        else if (date.startsWith("Su")) dayIdx = 1;
                        else {}
                        timeSlot.setDay(dayIdx,
                                        info.getFirstName()
                                        + info.getLastName());
                    }
                });


                return cell;
            }
        };
        String[] headers = initHeaders();
        int j = 0;
        final int NUM_DAYS = 8;
        // Create tables.
        while (j < NUM_DAYS) {
            TableColumn<TimeSlot, String> col = new TableColumn<>(headers[j]);
            col.setMinWidth(100);
            final int day_idx = j;
            col.setCellFactory(cellFactory);
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
        String minStr = "";
        String hourStr = "";
        // Iterate through all times (these are the rows).
        while (curHour < lastHour) {
            curDay = 1; // 1 is Sunday in MySQL.
            lastDay = 8; // 7 is Saturday in MySQL.
            hourMin = curHour*100 + curMin;
            TimeSlot curTimeSlot = new TimeSlot();
            // Prepend single digits with a zero before displaying them.
            hourStr = curHour < 10 ? "0" + curHour : "" + curHour;
            minStr = curMin < 10 ? "0" + curMin : "" + curMin;
            curTimeSlot.setDay(0, hourStr + ":" + minStr);
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

    /**
     * Get the Calendar object of a specific day.
     *
     * @param headerDate the date shown in a column in the header row
     * @return the Calendar object associated with the given date
     */
    private Calendar getCalendar(String headerDate) {
        int dayIdx = -0;
        if (headerDate.startsWith("M")) dayIdx = 1;
        else if (headerDate.startsWith("Tu")) dayIdx = 2;
        else if (headerDate.startsWith("W")) dayIdx = 3;
        else if (headerDate.startsWith("Th")) dayIdx = 4;
        else if (headerDate.startsWith("F")) dayIdx = 5;
        else if (headerDate.startsWith("Sa")) dayIdx = 6;
        else if (headerDate.startsWith("Su")) dayIdx = 0;
        else {}
        return dates[dayIdx];
    }

    /**
     * Get the Calendar object of a given TableCell.
     *
     * @param cell the TableCell to get the Calendar for
     * @return a Calendar instance that holds the date and time of
     *         the given TableCell
     */
    private Calendar getCalendar(TableCell<TimeSlot, String> cell) {
        TableColumn<TimeSlot, String> col = cell.getTableColumn();
        Calendar date = (Calendar) getCalendar(col.getText()).clone();
        // Get time.
        TableRow<TimeSlot> row = cell.getTableRow();
        TimeSlot timeSlot = row.getItem();
        String[] time = timeSlot.getDay(0).getValue().split(":");
        date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        date.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        return date;
    }

    private void initContextMenu() {
        contextMenu = new ContextMenu();
        copy_item = new MenuItem("Copy");
        copy_item.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ev) {
                copyDatetime = getCalendar(contextCell);
                copyFullName = contextCell.getText();
                contextCell = null;
            }
        });
        paste_item = new MenuItem("Paste");
        paste_item.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                // No selected cells, just a simple copy and paste into
                // one cell.
                if (pasteList.isEmpty()) {
                    pasteList.add(getCalendar(contextCell));
                    pasteCount = 1;
                }
                try {
                    calendarCon.copy(copyDatetime,
                                     pasteList.toArray(new Calendar[0]));
                }
                catch (SQLException err) {
                    throw new RuntimeException(err.toString());
                }
                ListIterator<Calendar> iter =
                    pasteList.listIterator(pasteList.size() - pasteCount);
                Calendar cal = null;
                while (iter.hasNext()) {
                    cal = iter.next();
                }
                copyDatetime = null;
                copyFullName = "";
                pasteList.clear();
                pasteCount = 0;
                contextCell = null;
            }
        });
        delete_item = new MenuItem("Delete");
        delete_item.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    calendarCon.delete(getCalendar(contextCell));
                }
                catch (SQLException err) {
                    throw new RuntimeException(err.toString());
                }
                contextCell = null;
            }
        });
        lock_item = new MenuItem("Lock");
        lock_item.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    calendarCon.insert(getCalendar(contextCell), "0");
                }
                catch (SQLException err) {
                    throw new RuntimeException(err.toString());
                }
            }
        });
        contextMenu.getItems().addAll(copy_item,
                                      paste_item,
                                      delete_item,
                                      lock_item);
    }

    /**
     * Set the availability of context menu items.  This disables or
     * enables all context menu items.  This should be called whenever
     * the context enu is opened.
     */
    private void enableContextMenuItems() {
        // What to enable depending on the contents of the cell and if
        // there is something to copy.
        // empty | copy null || copy | delete | paste | lock
        //   T   |   T       ||  F   |   F    |   F   |  T
        //   T   |   F       ||  F   |   F    |   T   |  T
        //   F   |   T       ||  T   |   T    |   F   |  F
        //   F   |   F       ||  T   |   T    |   F   |  F
        //
        // Enable paste iff the cell is empty and copyDatetime is not null.
        // Enable copy iff the cell is not empty.
        // Enable delete iff the cell is not empty.
        // Disable all if cell is XXXXXX.
        // Disable lock iff  cell is not empty.
        boolean isEmpty = contextCell.getText() == "";
        boolean isLocked = contextCell.getText().equals("XXXXXX");
        delete_item.setDisable(isEmpty || isLocked);
        copy_item.setDisable(isEmpty || isLocked);
        paste_item.setDisable(!isEmpty || (copyDatetime == null) || isLocked);
        lock_item.setDisable(!isEmpty);
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
            int j = 0;
            while (j < 8) {
                days[j] = new SimpleStringProperty("");
                ++j;
            }
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
            days[day].setValue(fName);
        }
    }
}













