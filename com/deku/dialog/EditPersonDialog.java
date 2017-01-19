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
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Calendar;
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
    private ListView<Map<String, String>> optionsListView;
    // Map of data to values.  The keys (names of data options) are the
    // labels, and the values (values of each data option) are editable.
    private ObservableList<Map<String, String>> data;

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

        dialog = new Dialog<>();
        dialog.setTitle("Edit person");
        dialog.setHeaderText("Edit person");
        dialog.setResizable(true);

        initAddRemoveHBox();
        initListView();

        grid = new GridPane();
        grid.add(addRemoveHBox, 1, 1);
        grid.add(optionsListView, 1, 2);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        return dialog;
    }

    /**
     * Create the buttons to modify the list of options.
     */
    private void initAddRemoveHBox() {
        addRemoveHBox = new HBox();
        Button addButton = new Button("Add");
        Button hideButton = new Button("Hide");

        addButton.setOnAction(e -> {
            TextInputDialog addDialog = new TextInputDialog();
            Optional<String> result = addDialog.showAndWait();
            if (result.isPresent()) {
                Map<String, String> m = new HashMap<>();
                m.put(result.get(), "");
                data.add(m);
                // TODO: save to config file.
            }
        });

        hideButton.setOnAction(e -> {
            int selectedIdx = optionsListView.getSelectionModel()
                .getSelectedIndex();
            data.remove(selectedIdx);
            // TODO: save to config file.
        });

        addRemoveHBox.getChildren().addAll(addButton, hideButton);
    }

    /**
     * Create the ListView that shows the editable data.  The ListView
     * uses an Options cell factory to create a custom view for each
     * cell that includes a Label and a TextField.
     */
    private void initListView() throws SQLException {
        data = FXCollections.observableArrayList();
        optionsListView = new ListView<>();
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
    static class Options extends ListCell<Map<String, String>> {
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
                hBox.getChildren().addAll(label, textField);
                setGraphic(hBox);
            }
        }
    }



}
