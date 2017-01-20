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
import javafx.scene.control.Alert;
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
import java.sql.SQLIntegrityConstraintViolationException;

import com.deku.controller.*;
import com.deku.dialog.NewAppointmentDialog;
import com.deku.dialog.NewAppointmentDialog.AppointmentInfo;
import com.deku.dialog.EditPersonDialog;


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
    private final static List<TableCell<TimeSlot, String>> pasteCellList =
        new ArrayList<>();
    private static Calendar copyDatetime = null; // Date to copy.
    private static String copyFullName = ""; // Name to copy.
    private int pasteCount; // How many times ctrl+click was done on this table
    private MenuItem copy_item;
    private MenuItem paste_item;
    private MenuItem delete_item;
    private MenuItem lock_item;
    private static final int COL_WIDTH_DEFAULT = 150;
    private static final int COL_WIDTH_ZOOM = 400;
    private static final int COL_WIDTH_TIME = 64;

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
        table.setTableMenuButtonVisible(true);
        // This needs to be after initData, initColumns, and table.setItems!
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
                                if (c.getText().equals("XXXXXX")) {
                                    // No selecting allowed for locked cells.
                                }
                                else if (c.getStyle() == "") {
                                    c.setStyle("-fx-background-color:#00a000");
                                    c.setOpacity(0.4);
                                    pasteList.add(getCalendar(c));
                                    pasteCellList.add(c);
                                    ++pasteCount;
                                }
                                else {
                                    c.setStyle("");
                                    c.setOpacity(1.0);
                                    pasteList.remove(getCalendar(c));
                                    pasteCellList.remove(c);
                                    --pasteCount;
                                }
                            }
                            // Double click.
                            else if (event.getClickCount() > 1) {
                                TableCell cell = (TableCell) event.getSource();
                                if (cell.getText().equals("XXXXXX")) {
                                    // No editing of locked cells.
                                    return;
                                }
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
                                            insertIntoCal(result.get(),
                                                          cell);
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
                                    Dialog<String> dial;
                                    EditPersonDialog editPerson =
                                        new EditPersonDialog(getCalendar(cell));
                                    try {
                                        dial = editPerson.getDialog();
                                        dial.showAndWait();
                                    }
                                    catch (SQLException err) {
                                        // Throw RuntimeException because this
                                        // method does not throw an
                                        // SQLException.
                                        throw new RuntimeException(
                                                    err.toString());
                                    }
                                }
                            }
                            else if (event.getButton()
                                     == MouseButton.SECONDARY) {
                                contextCell = cell;
                                enableContextMenuItems();
                            }
                        }

                    private void insertIntoCal(AppointmentInfo info,
                                               TableCell<TimeSlot, String> cell)
                            throws SQLException {
                        // TODO: convert date and time to Calendar
                        String ssn = info.getSSN();
                        if (ssn == "") {
                             ssn = patientsCon.insert(info.getFirstName(),
                                                     info.getLastName(),
                                                     "");
                        }
                        Calendar cal = getCalendar(cell);
                        calendarCon.insert(cal, ssn);
                        setName(cell, info.getFirstName(), info.getLastName());
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
            col.setPrefWidth(COL_WIDTH_DEFAULT);
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
            col.setContextMenu(initColumnContextMenu(day_idx));
            table.getColumns().add(col);
            ++j;
        }
        table.getColumns().get(0).setPrefWidth(COL_WIDTH_TIME);
        table.getColumns().get(0).setContextMenu(null);
    }

    /**
     * Sets the name of a given cell.  Given a cell, this method updates
     * the name that it displays.
     *
     * @param cell the TableCell to update the name of
     * @param firstName the first name to show
     * @param lastName the last name to show
     */
    private void setName(TableCell<TimeSlot, String> cell,
                    String firstName,
                    String lastName) {
        Calendar cal = getCalendar(cell);
        TableRow<TimeSlot> row = cell.getTableRow();
        TimeSlot timeSlot = row.getItem();
        // Given a Calendar, get its column index.
        int dayIdx = -1;
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY: dayIdx = 2; break;
            case Calendar.TUESDAY: dayIdx = 3; break;
            case Calendar.WEDNESDAY: dayIdx = 4; break;
            case Calendar.THURSDAY: dayIdx = 5; break;
            case Calendar.FRIDAY: dayIdx = 6; break;
            case Calendar.SATURDAY: dayIdx = 7; break;
            case Calendar.SUNDAY: dayIdx = 1; break;
            default: break;
        }
        timeSlot.setDay(dayIdx, firstName + lastName);
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
                    pasteCellList.add(contextCell);
                    pasteCount = 1;
                }
                try {
                    calendarCon.copy(copyDatetime,
                                     pasteList.toArray(new Calendar[0]));
                }
                catch (SQLException err) {
                    throw new RuntimeException(err.toString());
                }
                ListIterator<TableCell<TimeSlot, String>> iter =
                    pasteCellList.listIterator(pasteCellList.size()
                                               - pasteCount);
                TableCell<TimeSlot, String> cell = null;
                while (iter.hasNext()) {
                    cell = iter.next();
                    setName(cell, copyFullName, "");
                }
                copyDatetime = null;
                copyFullName = "";
                pasteList.clear();
                pasteCellList.clear();
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
                setName(contextCell, "", "");
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
     * Create the context menu for the header row columns.  The context
     * menu for the table does not work properly for the header row.
     * There is no way to get the Node object that represents a cell
     * in the header row (contextCell is null even if the click is on
     * a header row cell).  The workaround is to set a context menu for
     * each individual cell and attach an identifier to it in order to
     * know which cell the menu was opened for.
     *
     * @param colIdx the index of the cell in the header row
     * @return the context menu for the cell at the given index
     */
    private ContextMenu initColumnContextMenu(final int colIdx) {
        return (new ColumnContextMenu(colIdx)).getInstance(table);
    }


    /**
     * A context menu for cells in the header row.  This class is needed to
     * create a context menu that can identify for which header row cell
     * the context menu was opened (there is currently no direct way of
     * getting the row header cell Node object).
     *
     * This class only has one method: getInstance(...) which is what
     * should be called in order to get the actual context menu.
     *
     * This context menu is tailored for a given cell.  Specifically, the
     * zoom option zooms in on the cell over which the context menu was
     * opened.
     */
    private class ColumnContextMenu {
        private boolean zoomOut = true; // Show all columns or one.
        private int idx; // Index of the cell in the header row.
        private MenuItem zoom_item;
        private MenuItem hide_item;
        private MenuItem lock_item;

        /**
         * Create a context menu for the header row cell at the given index.
         *
         * @param colIdx the index of the header row cell
         */
        public ColumnContextMenu(int colIdx) {
            idx = colIdx;
        }

        /**
         * Get the context menu for the header row cell.  This creates
         * the context menu for the header row cell at the index given
         * on object instantiation.
         *
         * @param table the TableView to which the header row cell belongs
         * @return a ContextMenu object made for the header row cell
         *         at the assiciated index
         */
        public ContextMenu getInstance(TableView table) {
            ContextMenu columnContextMenu = new ContextMenu();
            zoom_item = new MenuItem("Zoom");
            hide_item = new MenuItem("Hide");
            lock_item = new MenuItem("Lock");
            zoom_item.setOnAction(new EventHandler<ActionEvent>() {

                public void handle(ActionEvent ev) {
                    ObservableList<TableColumn<TimeSlot, ?>> cols
                        = table.getColumns();
                    zoomOut = !zoomOut;
                    int j = 1; // Skip Time column.
                    while (j < cols.size()) {
                        if (j != idx) {
                            cols.get(j).setVisible(zoomOut);
                        }
                        else {
                            cols.get(j).setPrefWidth(zoomOut ?
                                                     COL_WIDTH_DEFAULT :
                                                     COL_WIDTH_ZOOM);
                        }
                        ++j;
                    }
                }
            });

            hide_item.setOnAction((ActionEvent ev) -> {
                ObservableList<TableColumn<TimeSlot, ?>> cols
                    = table.getColumns();
                cols.get(idx).setVisible(false);
            });

            lock_item.setOnAction((ActionEvent ev) -> {
                ObservableList<TableColumn<TimeSlot, ?>> cols
                    = table.getColumns();
                String colDate = cols.get(idx).getText();
                Calendar curDate = getCalendar(colDate);
                boolean hasNonEmptyCells = false;
                for (TimeSlot row : data) {
                    String[] curTime = row.getDay(0).getValue().split(":");
                    curDate.set(Calendar.HOUR_OF_DAY,
                                Integer.parseInt(curTime[0]));
                    curDate.set(Calendar.MINUTE,
                                Integer.parseInt(curTime[1]));
                    try {
                        calendarCon.insert(curDate, "0");
                    }
                    catch (SQLIntegrityConstraintViolationException err) {
                        hasNonEmptyCells = true;
                    }
                    catch (SQLException err) {
                        throw new RuntimeException(err.toString());
                    }
                }
                if (hasNonEmptyCells) {
                    String msg = "Appointments for some times have already been " +
                        "made.  These have not been locked.";
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
                    alert.showAndWait();
                }
            });

            columnContextMenu.getItems().addAll(zoom_item,
                                                hide_item,
                                                lock_item);
            return columnContextMenu;
        }
    };

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













