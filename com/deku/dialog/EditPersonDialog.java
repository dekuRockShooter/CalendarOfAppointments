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
import java.util.Optional;
import java.sql.SQLException;

import com.deku.controller.PatientsController;

// Idea for adding a layout to a cell:
// http://stackoverflow.com/questions/15661500/javafx-listview-item-with-an-image-button

public class EditPersonDialog {

    private Dialog<String> dialog;
    private GridPane grid;
    private ListView<String> optionsListView;
    private ObservableList<String> data = FXCollections.observableArrayList(
                "chocolate", "salmon", "gold", "coral", "darkorchid",
                "darkgoldenrod", "lightsalmon", "black", "rosybrown", "blue",
                "blueviolet", "brown");

    private PatientsController patientCon;

    public EditPersonDialog() {
    }

    public Dialog getDialog() throws SQLException {
        patientCon = new PatientsController();

        dialog = new Dialog<>();
        dialog.setTitle("Edit person");
        dialog.setHeaderText("Edit person");
        dialog.setResizable(true);

        initListView();

        grid = new GridPane();
        grid.add(optionsListView, 1, 1);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        return dialog;
    }

    private void initListView() {
        optionsListView = new ListView<>();
        optionsListView.setItems(data);
        optionsListView.setCellFactory((ListView<String> l) -> new Options());
    }


    static class Options extends ListCell<String> {
        private TextField textField;
        private Label label;
        private HBox hBox;

        public Options() {
            textField = new TextField();
            label = new Label();
            hBox = new HBox();
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                // The clear is needed to fix an IllegalArgumentException
                // "duplicate children added"
                hBox.getChildren().clear();
                label.setText(item);
                textField.setText(item);
                hBox.getChildren().addAll(label, textField);
                setGraphic(hBox);
            }
        }
    }



}
