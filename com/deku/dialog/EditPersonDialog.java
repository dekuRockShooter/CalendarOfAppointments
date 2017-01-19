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

import com.deku.controller.PatientsController;

// Idea for adding a layout to a cell:
// http://stackoverflow.com/questions/15661500/javafx-listview-item-with-an-image-button


public class EditPersonDialog {

    private String ssn; // SSN of the person whose data is shown.
    private Calendar date; // Date of the person whose data is shown.
    private Dialog<String> dialog;
    private GridPane grid;
    private HBox addRemoveHBox;
    private ButtonType buttonTypeOk;
    private ListView<Map<String, String>> optionsListView;
    // Map of data to values.  The keys (names of data options) are the
    // labels, and the values (values of each data option) are editable.
    // This changes when the user hides or adds options.
    private ObservableList<Map<String, String>> data;
    // A map that hold data-value pairs of all data options of the person.
    // This will never decrease in size, but may increase if the user adds
    // data options.
    private Map<String, String> originalDataMap;
    private Map<String, TextField> dirtyMap;

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

        grid = new GridPane();
        grid.add(addRemoveHBox, 1, 1);
        grid.add(optionsListView, 1, 2);
        dialog.getDialogPane().setContent(grid);

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
            for (Map<String, String> optionMap : data) {
                System.err.println(optionMap);
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
            List<String> optionsList = new ArrayList<>();
            optionsList.addAll(originalDataMap.keySet());
            CheckboxDialog dialog = new CheckboxDialog(optionsList,
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
        data.addAll(res);
        originalDataMap = new HashMap<>();
        for (Map<String, String> map : res) {
            originalDataMap.put(map.get("option"), map.get("value"));
        }
    }

    /**
     * Create the ListView that shows the editable data.  The ListView
     * uses an Options cell factory to create a custom view for each
     * cell that includes a Label and a TextField.  The data in 'data'
     * must have already been initialized.
     */
    private void initListView() {
        optionsListView = new ListView<>();
        optionsListView.setItems(data);
        optionsListView.setCellFactory(
                (ListView<Map<String, String>> l) -> new Options());
    }


    /**
     * A class that defines a layout for each cell in the ListView.  This
     * is a cell factory for the ListView that should be set using
     * listView.setCellFactory(...).  It consists of a Label and a
     * TextField.  The label shows the name of a piece of data, and
     * the textfield shows the current value of the data that can
     * also be edited.
     */
    private class Options extends ListCell<Map<String, String>> {
        private TextField textField;
        private Label label;
        private HBox hBox;

        /**
         * Default constructor.
         */
        public Options() {
            textField = new TextField();
            label = new Label();
            hBox = new HBox();
        }

        @Override
        public void updateItem(Map<String, String> item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                // The clear is needed to fix an IllegalArgumentException
                // "duplicate children added"
                hBox.getChildren().clear();
                label.setText(item.get("option")); // TODO: no hardcode.
                textField.setText(item.get("value"));
                textField.setOnKeyPressed( event -> {
                    dirtyMap.put(item.get("option"), textField);
                });
                hBox.getChildren().addAll(label, textField);
                setGraphic(hBox);
            }
        }
    }



}
