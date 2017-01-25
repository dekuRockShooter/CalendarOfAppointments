package com.deku.dialog;

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
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Pagination;
import javafx.scene.Node;
import javafx.util.Callback;
import javafx.collections.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Calendar;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Function;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;

import com.deku.controller.PatientsController;
import com.deku.controller.DataOptionsController;
import com.deku.controller.CalendarController;
import com.deku.controller.DateController;

// Idea for adding a layout to a cell:
// http://stackoverflow.com/questions/15661500/javafx-listview-item-with-an-image-button


// TODO: This class contains too many overlapping loops.  That is, one loop
// iterates over a list, and a loop in the same scope iterates over another
// list that contains a subset of the list in the first loop.  This needs
// to be fixed.  Use Sets?
public class EditPersonDialog {

    private String ssn; // SSN of the person whose data is shown.
    private Calendar date; // Date of the person whose data is shown.
    private Dialog<String> dialog;
    private GridPane grid;
    private TabPane tabPane;
    private HBox addRemoveHBox;
    private HBox pagerHBox;
    private VBox appointmentVBox;
    private ButtonType buttonTypeOk;
    private Pagination pager;
    private ListView<Calendar> pagerListView;
    private ListView<Map<String, String>> optionsListView;
    // Map of data to values.  The keys (names of data options) are the
    // labels, and the values (values of each data option) are editable.
    // This changes when the user hides or adds options.
    private ObservableList<Map<String, String>> data;
    private ObservableList<Calendar> dates;
    // A map that hold data-value pairs of all data options of the person.
    // This will never decrease in size, but may increase if the user adds
    // data options.
    private Map<String, String> originalDataMap;
    private Map<String, TextField> dirtyMap;
    private static final Path visibleOptionsFilePath
        = Paths.get("./visibleOptions");

    private PatientsController patientCon;

    /**
     * Create a new instance of this class.  The instantiated object can
     * be used to create a dialog that shows data about the person with
     * the given SSN.
     *
     * @param ssn the SSN of the person to show data for
     */
    public EditPersonDialog(String ssn) {
        this.ssn = ssn;
        this.date = null;
    }

    /**
     * Create a new instance of this class.  The instantiated object can
     * be used to create a dialog that shows data about the person who
     * has an appointment on the given day.
     *
     * @param datetime the date of an appointment held by the person for
     *                 who data will shown.  This must have Calendar's
     *                 HOUR_OF_DAY, MINUTE, DAY_OF_MONTH, WEEK, and,
     *                 MONTH fields.
     */
    public EditPersonDialog(Calendar datetime) {
        this.date = datetime;
        this.ssn = null;
    }

    /**
     * Get a dialog that shows data for a person.  The data is shown for
     * the person whose SSN is given in the constructor.  The dialog has
     * editable values so that each piece of data can be modified.
     *
     * @return a dialog that shows data for a person
     * @throws SQLException
     */
    public Dialog getDialog() throws SQLException {
        patientCon = new PatientsController();
        dirtyMap = new HashMap<>();

        dialog = new Dialog<>();
        dialog.setTitle("Edit person");
        dialog.setHeaderText("Edit person");
        dialog.setResizable(true);

        initAddRemoveHBox();
        initData();
        initListView();
        initPager();
        initPagerButtons();

        grid = new GridPane();
        grid.add(addRemoveHBox, 1, 1);
        grid.add(optionsListView, 1, 2);

        appointmentVBox = new VBox();
        appointmentVBox.getChildren().addAll(pagerHBox, pager);
        // These are needed to fix the issue of the pager collapsing
        // on page change.
        appointmentVBox.setPrefHeight(600);
        pager.setPrefHeight(400);

        Tab dataTab = new Tab("Data", grid);
        Tab appointmentsTab = new Tab("Appointments", appointmentVBox);
        dataTab.setClosable(false);
        appointmentsTab.setClosable(false);
        tabPane = new TabPane();
        tabPane.getTabs().addAll(dataTab, appointmentsTab);

        dialog.getDialogPane().setContent(tabPane);

        buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        initOkayButtonHandler();

        return dialog;
    }

    /**
     * Commit all changes and remember which options were visible.
     */
    private void initOkayButtonHandler() {
        final Button okayButton = (Button) dialog.getDialogPane()
            .lookupButton(buttonTypeOk);
        okayButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                BufferedWriter writer = Files.newBufferedWriter(
                        visibleOptionsFilePath,
                        Charset.forName("utf-8"));
                for (Map<String, String> options : data) {
                    String curOption = options.get("option");
                    // Save changed data.
                    if (dirtyMap.containsKey(curOption)) {
                        String newValue = dirtyMap.get(curOption).getText();
                        try {
                            if (date == null) {
                                patientCon.setData(ssn, curOption, newValue);
                            }
                            else {
                                patientCon.setData(date, curOption, newValue);
                            }
                        }
                        catch (SQLException err) {
                            throw new RuntimeException(err.toString());
                        }
                    }
                    // Save data about which options are visible.
                    writer.write(options.get("option"));
                    writer.newLine();
                }
                writer.close();
            }
            catch (IOException err) {
                throw new RuntimeException(err.toString());
            }
        });

    }

    /**
     * Create the buttons to modify the list of options.
     */
    private void initAddRemoveHBox() {
        addRemoveHBox = new HBox();
        Button addButton = new Button("Add");
        Button hideButton = new Button("Hide");
        Button showButton = new Button("Show");

        addButton.setOnAction(e -> {
            TextInputDialog addDialog = new TextInputDialog();
            Optional<String> result = addDialog.showAndWait();
            if (result.isPresent()) {
                // Insert option into database
                boolean optCreationSuccessful = false;
                try {
                    DataOptionsController dataOpsCon
                        = new DataOptionsController();
                    optCreationSuccessful = dataOpsCon.createOption(
                            result.get());
                }
                catch (SQLException err) {
                    throw new RuntimeException(err.toString());
                }
                if (!optCreationSuccessful) {
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                                            "Data option already exists");
                    alert.showAndWait();
                    e.consume();
                    return;
                }
                Map<String, String> m = new HashMap<>();
                m.put("option", result.get());
                m.put("value", null);
                data.add(m);
                originalDataMap.put(result.get(), null);
                // Make a new ListView to add the new option.  Theoritcally,
                // data.add(m) should be enough to append the cell and update
                // the ListView.  Although this does happen, a copy of
                // everything in 'data' is shown at the bottom of the list.
                // Why does appending one element append the entire list?
                // I don't know.  The fix for this is creating a new ListView
                // with the new data.  Fortunately, this add operation wont
                // be used too frequently, so the extra time involved in
                // doing this isn't much of a concern (JavaFX 8 is concerning,
                // though).
                initListView();
                grid.add(optionsListView, 1, 2);
                // TODO: on Okay, save the options in 'data' (the ones that
                // are visible) to a file and commit all changes.
            }
        });

        hideButton.setOnAction(e -> {
            int selectedIdx = optionsListView.getSelectionModel()
                .getSelectedIndex();
            Map<String, String> optionMap = data.remove(selectedIdx);
            dirtyMap.remove(optionMap.get("option"));
            // A new ListView is created for pretty much the same reason
            // why one was created above in addButton's handler.
            initListView();
            grid.add(optionsListView, 1, 2);
            // TODO: on Okay, save the options in 'data' (the ones that
            // are visible) to a file and commit all changes.
        });

        showButton.setOnAction(e -> {
            List<String> visibleOptList = new ArrayList<>();
            List<String> hiddenOptList = new ArrayList<>();
            hiddenOptList.addAll(originalDataMap.keySet());
            // Move visible options from the hidden list to the visible list.
            for (Map<String, String> optMap : data) {
                String opt = optMap.get("option");
                hiddenOptList.remove(opt);
                visibleOptList.add(opt);
            }
            CheckboxDialog dialog = new CheckboxDialog(visibleOptList,
                                                       hiddenOptList,
                                                       "Data to show");
            Dialog<List<String>> cbDialog = dialog.getDialog();
            Optional<List<String>> result = cbDialog.showAndWait();
            if (result.isPresent()) {
                // If this ever gets too slow, then some set theory might
                // be helpful:
                //
                //   options to show:
                //     everything in optionstoshow and not in showing
                //     => optionstoshow - showing
                //
                //   options to not show:
                //     everything not in optionstoshow and in showing
                //     => showing - optionstoshow
                //
                // But for now, a simple clear and add is good enough.
                List<String> optionsToShow = result.get();
                data.clear();
                for (String option : optionsToShow) {
                    Map<String, String> m = new HashMap<>();
                    m.put("option", option);
                    m.put("value", originalDataMap.get(option));
                    data.add(m);
                }
                initListView();
                grid.add(optionsListView, 1, 2);
                // TODO: on Okay, save the options in 'data' (the ones that
                // are visible) to a file and commit all changes.
            }
        });

        addRemoveHBox.getChildren().addAll(addButton, hideButton, showButton);
    }

    /**
     * Initialize the data to show in the ListView.
     */
    private void initData() throws SQLException {
        data = FXCollections.observableArrayList();
        List<Map<String, String>> res;
        // No need to test for ssn == null because either one or the
        // other has to be initialized via the constructors.  The
        // SSN may be incorrect, but at this point, that is the
        // model's job to handle.
        if (date == null) {
            res = patientCon.getData(ssn);
        }
        else {
            res = patientCon.getData(date);
        }
        originalDataMap = new HashMap<>();
        for (Map<String, String> map : res) {
            originalDataMap.put(map.get("option"), map.get("value"));
        }
        String visibleOption = "";
        try {
            BufferedReader reader = Files.newBufferedReader(
                    visibleOptionsFilePath,
                    Charset.forName("utf-8"));
            visibleOption = reader.readLine();
            while (visibleOption != null) {
                Map<String, String> map = new HashMap<>();
                map.put("option", visibleOption);
                map.put("value", originalDataMap.get(visibleOption));
                data.add(map);
                visibleOption = reader.readLine();
            }
            reader.close();
        }
        catch (IOException err) {
            //System.err.println("Error while reading file visibleOptions: "
                    //+ err.toString());
            //System.err.println("Showing all options instead.");
            data.clear();
            data.addAll(res);
        }
    }

    /**
     * Create the ListView that shows the editable data.  The ListView
     * uses an Options cell factory to create a custom view for each
     * cell that includes a Label and a TextField.  The data in 'data'
     * must have already been initialized.
     */
    private void initListView() {
        final int listViewWidth = 400;
        optionsListView = new ListView<>();
        optionsListView.setPrefWidth(listViewWidth);
        optionsListView.setItems(data);
        optionsListView.setCellFactory( (ListView<Map<String, String>> l) -> {
            return new Options(listViewWidth / 2, listViewWidth / 2);

        });
    }

    /**
     * Create the buttons to add and remove appointments.
     */
    private void initPagerButtons() {
        pagerHBox = new HBox();
        Button addButton = new Button("Add");
        Button removeButton = new Button("Remove");

        // Show free appointments.
        addButton.setOnAction(e -> {
            final ObservableList<Node> vboxChildren
                = appointmentVBox.getChildren();
            final VBox freeAppLay;
            try {
                FreeAppointmentLayout fal = new FreeAppointmentLayout();
                freeAppLay = fal.getLayout();
            }
            catch (SQLException err) {
                throw new RuntimeException(err.toString());
            }
            // Hide all Nodes and show the free appointments pagination.
            for (Node node : vboxChildren) {
                node.setVisible(false); // Invisible, but still takes up space.
                node.setManaged(false); // No longer takes up space.
            }
            vboxChildren.add(freeAppLay);

            // Show all Nodes and remove the free appointments pagination.
            Button backButton = new Button("Back");
            HBox sortHBox = (HBox) freeAppLay.lookup("#"
                    + FreeAppointmentLayout.ID_BUTTON_BAR);
            backButton.setOnAction(event -> {
                vboxChildren.remove(freeAppLay);
                for (Node node : vboxChildren) {
                    node.setVisible(true);
                    node.setManaged(true);
                }
            });

            sortHBox.getChildren().add(backButton);
        });

        removeButton.setOnAction(e -> {
            Calendar selectedCal = pagerListView
                .getSelectionModel()
                .getSelectedItem();
            try {
                CalendarController calCon = new CalendarController();
                calCon.delete(selectedCal);
            }
            catch (SQLException err) {
                throw new RuntimeException(err.toString());
            }
            dates.remove(selectedCal);
        });

        pagerHBox.getChildren().addAll(addButton, removeButton);
    }


    /**
     * A Pagination factory to show lists of dates.  This factory is
     * meant to be used in a Pagination object's setPageFactory(...)
     * method.
     */
    private class PagerFactory implements Callback<Integer, Node> {

        private ListView<Calendar> listView;
        private ObservableList<Calendar> dates;
        private List<Calendar> calList;
        private Function<Integer, List<Calendar>> listFunction;

        /**
         * Create a Pagination for showing lists of Calendar objects.
         * When the n'th page is opened, the elements in indeces
         * 10(n - 1) through but not including 10(n - 1) + 10 in the
         * given List of Calendars are shown (ten Calendars per page).
         *
         * The given ListView is what is shown on each page of the
         * Pagination.  The ListView displays the contents in the given
         * ObservableList.  This list's data changes whenever a page
         * is changed to show the next ten elements in the given List.
         *
         * @param listView the ListView to show in the Pagination
         * @param dates the ObservableList that holds the data to show
         *              in the ListView
         * @param calList a List that stores all Calendars to show (the
         *                universal set).  The Calendars of this list
         *                are shown in blocks of ten.
         */
        public PagerFactory(ListView<Calendar> listView,
                            ObservableList<Calendar> dates,
                            List<Calendar> calList) {
            this.listView = listView;
            this.calList = calList;
            this.dates = dates;
            this.listFunction = null;
        }

        /**
         * Create a Paginator for showing lists of Calendar objects.
         * This constructor lets the caller define what to show when
         * the n'th page is opened.
         *
         * The given ListView is what is shown on each page of the
         * Paginator.  The ListView displays the contents in the given
         * ObservableList.  This list's data changes whenever a page
         * is changed.  The data to display when navigating to the
         * n'th page is defined by the given Function object.
         *
         * @param listView the ListView to show in the Paginator
         * @param dates the ObservableList that holds the data to show
         *              in the ListView
         * @param listFunction the Function that takes a page index (0 or
         *                     greater) and returns a list of Calendars
         *                     to show for that page
         */
        public PagerFactory(ListView<Calendar> listView,
                            ObservableList<Calendar> dates,
                            Function<Integer, List<Calendar>> listFunction) {
            this.listView = listView;
            this.calList = calList;
            this.dates = dates;
            this.listFunction = listFunction;
        }

        // Called when a new page is opened.  Show appointments.
        @Override
        public Node call(Integer pageIndex) {
            dates.clear();
            // Caller defined behavior on page change.
            if (listFunction != null) {
                dates.addAll(listFunction.apply(pageIndex));
            }
            else {
                int begIdx = pageIndex * 10;
                int endIdx = begIdx + 10;
                if (begIdx >= calList.size()) {
                    begIdx = calList.size();
                }
                if (endIdx >= calList.size()) {
                    endIdx = calList.size();
                }
                dates.addAll(calList.subList(begIdx, endIdx));
            }
            listView.setCellFactory( (lv) -> {
                return new ListCell<Calendar>() {
                    @Override
                    public void updateItem(Calendar item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            return;
                        }
                        String month = "";
                        String day = "";
                        String hour = "";
                        String minute = "";
                        int m = item.get(Calendar.MONTH) + 1;
                        int d = item.get(Calendar.DAY_OF_MONTH);
                        int h = item.get(Calendar.HOUR_OF_DAY);
                        int min = item.get(Calendar.MINUTE);
                        month = (m < 10) ? ("0" + m) : ("" + m);
                        day = (d < 10) ? ("0" + d) : ("" + d);
                        hour = (h < 10) ? ("0" + h) : ("" + h);
                        minute = (min < 10) ? ("0" + min) : ("" + min);
                        setText(String.format("%d-%s-%s %s:%s",
                                    item.get(Calendar.YEAR),
                                    month,
                                    day,
                                    hour,
                                    minute));
                    }
                };
            });
            listView.setItems(dates);
            return listView;
        }
    }


    private void initPager() throws SQLException {
        pagerListView = new ListView<>();
        dates = FXCollections.observableArrayList();
        final List<Calendar> calList;
        if (date == null) {
            calList = patientCon.getAllAppointments(ssn);
        }
        else {
            calList = patientCon.getAllAppointments(date);
        }
        pager = new Pagination(1 + calList.size() / 10, 0);
        pager.setPageFactory(new PagerFactory(pagerListView, dates, calList));
    }


    /**
     * A class that defines a layout for each cell in the ListView.  This
     * is a cell factory for the ListView that should be set using
     * listView.setCellFactory(...).  It consists of a Label and a
     * TextField.  The label shows the name of a piece of data, and
     * the textfield shows the current value of the data that can
     * also be edited.  If the text shown by a label is longer than
     * the label's width, then the text is wrapped.
     */
    private class Options extends ListCell<Map<String, String>> {
        private TextField textField;
        private Label label;
        private HBox hBox;
        private int textFieldWidth;
        private int labelWidth;

        /**
         * Default constructor.
         */
        public Options() {
            textField = new TextField();
            label = new Label();
            hBox = new HBox();
            this.textFieldWidth = 100;
            this.labelWidth = 100;
        }

        /**
         * Constructor for defining the widths of this cell's Nodes.
         *
         * @param textFieldWidth the preferred width of the TextField
         * @param labelWidth the preferred width of the label
         */
        public Options(int textFieldWidth, int labelWidth) {
            textField = new TextField();
            label = new Label();
            hBox = new HBox();
            this.textFieldWidth = textFieldWidth;
            this.labelWidth = labelWidth;
        }

        @Override
        public void updateItem(Map<String, String> item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                // The clear is needed to fix an IllegalArgumentException
                // "duplicate children added"
                hBox.getChildren().clear();
                label.setText(item.get("option")); // TODO: no hardcode.
                label.setPrefWidth(labelWidth);
                label.setWrapText(true);
                textField.setText(item.get("value"));
                textField.setPrefWidth(textFieldWidth);
                textField.setOnKeyPressed( event -> {
                    dirtyMap.put(item.get("option"), textField);
                });
                hBox.getChildren().addAll(label, textField);
                setGraphic(hBox);
            }
        }
    }


    /**
     * This class creates a layout to show all available appointments.
     * The layout consists of a Paginator to display the data, and a
     * few buttons to sort it and make an appointment.
     *
     * Use the getLayout() method to get the layout.
     *
     * Some Nodes have an ID that can be used to look them up if desired.
     */
    private class FreeAppointmentLayout {
        /**
         * The ID of the button bar.
         */
        public static final String ID_BUTTON_BAR = "id_button_bar";
        /**
         * The ID of the Paginator.
         */
        public static final String ID_PAGINATOR = "id_paginator";

        // Show a list of available appointments.
        private Pagination freeAppointmentPager;
        // Stores the list of available appointments.
        private ListView<Calendar> freeAppointmentListView;
        // Data for freeAppointmentListView.
        private ObservableList<Calendar> freeDates;
        // Controller for getting the list of free appointments.
        private DateController dateCon;
        // Buttons for sorting the data.
        private HBox sortHBox;

        /**
         * Default constructor.
         *
         * @throws SQLException
         */
        public FreeAppointmentLayout() throws SQLException {
            dateCon = new DateController();
            freeAppointmentPager = new Pagination();
            freeAppointmentListView = new ListView<>();
            sortHBox = new HBox();
            freeDates = FXCollections.observableArrayList();
        }

        /**
         * Create the Paginator.  Dimensions and data to show on page change.
         *
         * @throws SQLException
         */
        private void initPaginator() throws SQLException {
            List<Calendar> calList = dateCon
                .getFreeAppointments(Calendar.getInstance());
            freeAppointmentPager.setPageFactory(new PagerFactory(
                                                  freeAppointmentListView,
                                                  freeDates,
                                                  pageIndex -> {
                try {
                    // Get available appointments for the pageIndex'th week
                    // after the current week.
                    Calendar week = Calendar.getInstance();
                    week.add(Calendar.DAY_OF_MONTH, 7 * pageIndex);
                    return dateCon.getFreeAppointments(week);
                }
                catch (SQLException err) {
                    throw new RuntimeException(err.toString());
                }
            }));

            freeAppointmentPager.setId(ID_PAGINATOR);
            freeAppointmentPager.setPrefHeight(300);
        }

        /**
         * Create the bar that has buttons to sort the data in the Paginator.
         */
        private void initButtonBar() {
            Label sortLabel = new Label("Sort by: ");
            Button sortTimeButton = new Button("Time");
            Button sortDayButton = new Button("Day");
            Button addAppointmentButton = new Button("Add appointment");

            sortDayButton.setOnAction(ev -> {
                // Sort the data by increasing date.
                freeDates.sort((cal1, cal2) -> {
                    int cal1Month = cal1.get(Calendar.MONTH);
                    int cal2Month = cal2.get(Calendar.MONTH);
                    int cal1Day = cal1.get(Calendar.DAY_OF_MONTH);
                    int cal2Day = cal2.get(Calendar.DAY_OF_MONTH);
                    if (cal1Month < cal2Month) {
                        return -1;
                    }
                    if (cal1Month > cal2Month) {
                        return 1;
                    }
                    // These are for equal months, but different days.
                    if (cal1Day < cal2Day) {
                        return -1;
                    }
                    if (cal1Day == cal2Day) {
                        return 0;
                    }
                    return 1;
                });
            });

            sortTimeButton.setOnAction(ev -> {
                // Sort the data by increasing time.
                freeDates.sort((cal1, cal2) -> {
                    int cal1Hour = cal1.get(Calendar.HOUR_OF_DAY);
                    int cal2Hour = cal2.get(Calendar.HOUR_OF_DAY);
                    int cal1Min = cal1.get(Calendar.MINUTE);
                    int cal2Min = cal2.get(Calendar.MINUTE);
                    if (cal1Hour < cal2Hour) {
                        return -1;
                    }
                    if (cal1Hour > cal2Hour) {
                        return 1;
                    }
                    // These are for equal hours, but different minutes.
                    if (cal1Min < cal2Min) {
                        return -1;
                    }
                    if (cal1Min == cal2Min) {
                        return 0;
                    }
                    return 1;
                });
            });

            sortHBox.setId(ID_BUTTON_BAR);
            sortHBox.getChildren().addAll(sortLabel, sortDayButton,
                                          sortTimeButton, addAppointmentButton);
        }

        /**
         * Get a VBox that shows a Paginator of available appointments.
         * Each page in the Paginator shows all available appointments
         * for a specific week.  The first page shows available appointments
         * for the current week, the second page shows them for the
         * next week, and so on.  In general, the n'th page shows the
         * available appointments for the (n-1)'th week after the current
         * week.
         *
         * The VBox also contains buttons to sort the appointments in
         * increasing time or date, as well as a button to add the
         * currently selected date as an appointment for the person
         * whose information is being edited.
         *
         * @return a VBox that contains a list of available appointments
         */
        public VBox getLayout() throws SQLException {
            initPaginator();
            initButtonBar();
            VBox addVBox = new VBox();

            addVBox.getChildren().addAll(sortHBox, freeAppointmentPager);
            return addVBox;
            //try {
                //CalendarController calCon = new CalendarController();
                //if (date == null) {
                    //calCon.insert(selectedCal, ssn);
                //}
                //else {
                    //calCon.insert(selectedCal, date);
                //}
            //}
            //catch (SQLException err) {
                //throw new RuntimeException(err.toString());
            //}
        }
    }

}
