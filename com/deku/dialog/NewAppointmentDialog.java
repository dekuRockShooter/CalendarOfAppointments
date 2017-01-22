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
import javafx.util.Callback;
import javafx.collections.*;

import java.util.List;
import java.util.Map;
import java.sql.SQLException;

import com.deku.controller.PatientsController;

// Source for creating custom dialogs:
// https://examples.javacodegeeks.com/desktop-java/javafx/dialog-javafx/javafx-dialog-example/

public class NewAppointmentDialog {

    private Dialog<AppointmentInfo> dialog;
    private Label firstNameLabel;
    private Label lastNameLabel;
    private TextField firstNameTxtField;
    private TextField lastNameTxtField;
    private Button searchButton;
    private CheckBox fuzzyCheckBox;
    private GridPane grid;
    private ListView<Map<String, String>> searchResultsListView;

    private PatientsController patientCon;

    public NewAppointmentDialog() {
    }

    public Dialog getDialog() throws SQLException {
        patientCon = new PatientsController();

        dialog = new Dialog<>();
        dialog.setTitle("New appointment");
        dialog.setHeaderText("New Appointment");
        dialog.setResizable(true);

        firstNameTxtField = new TextField();
        lastNameTxtField = new TextField();
        searchButton = new Button("search");
        fuzzyCheckBox = new CheckBox("Fuzzy search");
        searchResultsListView = new ListView<>();

        firstNameTxtField.setPromptText("First name");
        lastNameTxtField.setPromptText("Last name");

        initButtons();
        initListView();

        grid = new GridPane();
        grid.add(firstNameTxtField, 1, 1, 2, 1);
        grid.add(lastNameTxtField, 1, 2, 2, 1);
        grid.add(searchButton, 1, 3, 1, 1);
        grid.add(fuzzyCheckBox, 2, 3, 1, 1);
        grid.add(searchResultsListView, 1, 4, 2, 1);
        grid.setMargin(fuzzyCheckBox, new Insets(0, 0, 0, 20));

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel",
                                                     ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        // Needed to return a correctly initialized AppointmentInfo object.
        dialog.setResultConverter(new Callback<ButtonType, AppointmentInfo>() {
            @Override
            public AppointmentInfo call(ButtonType b) {
                if (b == buttonTypeOk) {
                    String firstName = "";
                    String lastName = "";
                    String ssn = "";
                    Map<String, String> selection = searchResultsListView.
                        getSelectionModel().getSelectedItem();
                    if (searchResultsListView.isVisible()
                        && (selection != null)) {
                        firstName = selection.get("FirstName");
                        lastName = selection.get("LastName");
                        ssn = selection.get("SSN");
                    }
                    else {
                        firstName = firstNameTxtField.getText();
                        lastName = lastNameTxtField.getText();
                        // If this is true, then all fields are empty.
                        if ((firstName == "") || (lastName == "")) {
                            return null;
                        }
                    }
                    AppointmentInfo info = new AppointmentInfo(firstName,
                                                               lastName,
                                                               ssn);
                    return info;
                }
                return null;
            }
        });
        return dialog;
    }

    private void initListView() {
        searchResultsListView.setVisible(false);
    }

    private void initButtons() {
        searchButton.setOnAction(e -> {
            List<Map<String, String>> names;
            try {
                names = patientCon.searchNames(firstNameTxtField.getText(),
                                               lastNameTxtField.getText(),
                                               fuzzyCheckBox.isSelected());
            }
            catch (SQLException err) {
                // Throw RuntimeException because this
                // method does not throw an
                // SQLException.
                throw new RuntimeException(err.toString());
            }
            ObservableList<Map<String, String>> res =
                FXCollections.observableArrayList(names);
            searchResultsListView.setItems(res);
            searchResultsListView.setVisible(true);
            System.err.println(res.toString());
        });
    }


    /**
     * This class stores information about a single apppointment.
     */
    public static class AppointmentInfo {

        private String firstName;
        private String lastName;
        private String ssn;

        public AppointmentInfo(String firstName, String lastName, String ssn) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.ssn = ssn;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getSSN() {
            return ssn;
        }
    }

}
