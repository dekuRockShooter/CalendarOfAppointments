package com.deku.dialog;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.paint.Color;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ColorPicker;
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
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;

import javax.json.*;

import com.deku.controller.DataOptionsController;


public class CustomizeCalendarDialog {

    private Dialog<String> dialog;
    private VBox mainVBox;
    private HBox daysHBox;
    private GridPane timesGrid;
    private HBox colorsHBox;
    private ButtonType buttonTypeOk;
    private ListView<String> optionsListView;
    private ObservableList<String> data;
    // A mapping of data values to colors.  This stores the values of the
    // currently selected data option and their associated color.
    private Map<String, Color> dataColorMap;
    private static final String ID_TIMESGRID_COMBOBOX = "timesGridComboBox";
    private static final Path calendarSettingsFilePath
        = Paths.get("./calendarSettings");

    private DataOptionsController dataOptCon;

    /**
     * Create a new instance of this class.  The instantiated object can
     * be used to create a dialog that allows the user to customize
     * various settings about the TableView that shows a calendar.
     */
    public CustomizeCalendarDialog() {
    }

    /**
     * @throws SQLException
     */
    public Dialog getDialog() throws SQLException {
        dataOptCon = new DataOptionsController();

        dialog = new Dialog<>();
        dialog.setTitle("Edit person");
        dialog.setHeaderText("Edit person");
        dialog.setResizable(true);

        initDaysHBox();
        initTimesGrid();
        initColorsHBox();
        initMainVBox();

        dialog.getDialogPane().setContent(mainVBox);

        buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        initOkayButtonHandler();

        return dialog;
    }

    /**
     * Save settings.
     */
    private void initOkayButtonHandler() {
        final Button okayButton = (Button) dialog.getDialogPane()
            .lookupButton(buttonTypeOk);
        okayButton.addEventFilter(ActionEvent.ACTION, event -> {
            JsonWriterFactory writerFactory = Json.createWriterFactory(null);
            JsonObjectBuilder objBuilder = Json.createObjectBuilder();
            try {
                BufferedWriter bw = Files.newBufferedWriter(
                        calendarSettingsFilePath,
                        Charset.forName("utf-8"));
                JsonWriter writer = writerFactory.createWriter(bw);
                // Save day settings.
                for (Node node : daysHBox.getChildren()) {
                    CheckBox cb = (CheckBox) node;
                    objBuilder.add(cb.getText(), cb.isSelected());
                }
                writer.writeObject(objBuilder.build());
                writer.close();
            }
            catch (Exception err) {
                throw new RuntimeException(err.toString());
            }
        });
    }

    /**
     * Create the container to choose which days to show by default.
     */
    private void initDaysHBox() {
        daysHBox = new HBox();
        List<CheckBox> checkboxList = new ArrayList<>(7);
        checkboxList.add(new CheckBox("Sunday"));
        checkboxList.add(new CheckBox("Monday"));
        checkboxList.add(new CheckBox("Tuesday"));
        checkboxList.add(new CheckBox("Wednesday"));
        checkboxList.add(new CheckBox("Thursday"));
        checkboxList.add(new CheckBox("Friday"));
        checkboxList.add(new CheckBox("Saturday"));
        // TODO: read settings file to preselect/deselect.
        daysHBox.getChildren().addAll(checkboxList);
    }

    /**
     * Create the container to choose the default start and end times.
     */
    private void initTimesGrid() {
        timesGrid = new GridPane();
        TextField startHour = new TextField(/* TODO: current setting */);
        TextField lastHour = new TextField(/* TODO: current setting */);
        TextField startMin = new TextField(/* TODO: current setting */);
        TextField lastMin = new TextField(/* TODO: current setting */);
        startHour.setId("startHour");
        lastHour.setId("lastHour");
        startMin.setId("startMin");
        lastMin.setId("lastMin");
        Label startHourLabel = new Label("Start hour");
        Label lastHourLabel = new Label("Last hour");
        Label startMinLabel = new Label("Start minute");
        Label lastMinLabel = new Label("Last minute");

        timesGrid.add(startHourLabel, 1, 1);
        timesGrid.add(startHour, 2, 1);
        timesGrid.add(lastHourLabel, 3, 1);
        timesGrid.add(lastHour, 4, 1);

        timesGrid.add(startMinLabel, 1, 2);
        timesGrid.add(startMin, 2, 2);
        timesGrid.add(lastMinLabel, 3, 2);
        timesGrid.add(lastMin, 4, 2);

        /**
         * Class for listening to focus change on a TextField.  When a
         * TextField loses focus, its input is checked for validity.
         */
        class FocusListener implements ChangeListener<Boolean> {
            private int min; // Least time possible.
            private int max; // Greatest time possible.
            // True if the associated TextField gets info for last times.
            private boolean isLast;
            // True if the associated TextField gets info for hours.
            private boolean isHour;


            /**
             * Create a listener that can be used in 
             * timeTextField.focusProperty.addListener().  This validates
             * the input to make sure the times are properly ordered (start
             * times less than end times).  If this is not true, then the
             * text will be set to the empty string.
             *
             * Currently, minutes are only valid if they are multiples of 15.
             * This may change in the future if the user is allowed to change
             * the increment between minutes, but for now, if a minute is not
             * a multiple of 15, then the field is set to the empty string,
             */
            public FocusListener(int min, int max,
                                 boolean isLast, boolean isHour) {
                this.min = min;
                this.max = max;
                this.isLast = isLast;
                this.isHour = isHour;
            }

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0,
                                Boolean oldPropertyValue,
                                Boolean newPropertyValue) {
                // Gained focus.
                if (newPropertyValue) {
                    return;
                }
                // Lost focus, check for valid input.
                int value = 0;
                TextField curTextField = null;
                TextField prevTextField = null;
                // Try to convert input to int.  Remember the TextFields for
                // start and last values to be able to ensure that start is
                // less than last.
                try {
                    if (isHour) {
                        if (isLast) {
                            curTextField = lastHour;
                            value = Integer.parseInt(lastHour.getText());
                            prevTextField = startHour;
                        }
                        else {
                            curTextField = startHour;
                            value = Integer.parseInt(startHour.getText());
                            prevTextField = lastHour;
                        }
                    }
                    else {
                        if (isLast) {
                            curTextField = lastMin;
                            value = Integer.parseInt(lastMin.getText());
                            prevTextField = startMin;
                        }
                        else {
                            curTextField = startMin;
                            value = Integer.parseInt(startMin.getText());
                            prevTextField = lastMin;
                        }
                        // Minutes can only be in multiples of 15 (for now,
                        // but this might change in the future).
                        if ((value % 15) != 0) {
                            curTextField.setText("");
                            return;
                        }
                    }
                }
                // Input was not a number.
                catch (NumberFormatException err) {
                    curTextField.setText("");
                    return;
                }
                if ((value < min) || (value > max)) {
                    curTextField.setText("");
                }
                // The complement TextField is empty so the current value is
                // valid.
                if (prevTextField.getText().equals("")) {
                    return;
                }
                // Make sure last value is greater than start value.
                if (isLast) {
                    if (value <= Integer.parseInt(prevTextField.getText())) {
                        curTextField.setText("");
                    }
                }
                // Make sure start value is less than last value.
                else {
                    if (value >= Integer.parseInt(prevTextField.getText())) {
                        curTextField.setText("");
                    }
                }
            }
        }


        startHour.focusedProperty().addListener(
            new FocusListener(0, 23, false, true));
        lastHour.focusedProperty().addListener(
            new FocusListener(0, 23, true, true));
        startMin.focusedProperty().addListener(
            new FocusListener(0, 45, false, false));
        lastMin.focusedProperty().addListener(
            new FocusListener(0, 45, true, false));
        // TODO: read settings file to initialize text.
    }

    /**
     * Create the container for choosing colors.
     */
    private void initColorsHBox() throws SQLException {
        ObservableList<String> options = FXCollections.observableArrayList();
        data = FXCollections.observableArrayList();
        options.addAll(dataOptCon.getAllOptions());
        dataColorMap = new HashMap<>(); // TODO: initialize based on settings
        colorsHBox = new HBox();
        optionsListView = new ListView<>();
        ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setId(ID_TIMESGRID_COMBOBOX);
        // TODO: read settings file to initialize text.

        // Update the color listview to the values of the selected option.
        comboBox.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                data.clear();
                dataColorMap.clear();
                try {
                    data.addAll(dataOptCon.getAllData(newValue));
                }
                catch (SQLException err) {
                    throw new RuntimeException(err.toString());
                }
                // TODO: initialize map to saved settings
            });

        optionsListView.setItems(data);
        optionsListView.setCellFactory( (ListView<String> l) -> {
            return new Options();
        });
        colorsHBox.getChildren().addAll(comboBox, optionsListView);
    }

    /**
     * Create the main layout.
     */
    private void initMainVBox() {
        mainVBox = new VBox();
        mainVBox.getChildren().addAll(daysHBox, timesGrid, colorsHBox);
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
    private class Options extends ListCell<String> {
        private ColorPicker colorPicker;
        private Label label;
        private HBox hBox;
        private int colorButtonWidth;
        private int labelWidth;

        /**
         * Default constructor.
         */
        public Options() {
            colorPicker = new ColorPicker();
            label = new Label();
            hBox = new HBox();
            this.colorButtonWidth = 100;
            this.labelWidth = 100;
        }

        /**
         * Constructor for defining the widths of this cell's Nodes.
         *
         * @param textFieldWidth the preferred width of the TextField
         * @param labelWidth the preferred width of the label
         */
        public Options(int colorButtonWidth, int labelWidth) {
            colorPicker = new ColorPicker();
            label = new Label();
            hBox = new HBox();
            this.colorButtonWidth = colorButtonWidth;
            this.labelWidth = labelWidth;
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                // The clear is needed to fix an IllegalArgumentException
                // "duplicate children added"
                hBox.getChildren().clear();
                label.setText(item);
                label.setPrefWidth(labelWidth);
                label.setWrapText(true);
                colorPicker.setPrefWidth(colorButtonWidth);
                colorPicker.setOnAction( event -> {
                    dataColorMap.put(item, colorPicker.getValue());
                });
                hBox.getChildren().addAll(label, colorPicker);
                setGraphic(hBox);
            }
            // Erase all context of the cell.  This is needed to truly remove
            // a cell when its date is removed from the underlying list.
            else {
                setGraphic(null);
            }
        }
    }
}
