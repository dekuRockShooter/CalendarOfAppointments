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
import java.sql.SQLException;


/**
 * A class that creates a dialog that shows a list of checkboxes.  Use
 * getDialog to get the dialog.  When the dialog is closed using the Okay
 * button, a list of the checkboxes that were checked is returned.
 */
public class CheckboxDialog {

    // Labels of each checkbox.
    private List<String> checkList;
    private String title;

    /**
     * Create a new instance of this class.  The instantiated object can
     * be used to create a dialog that shows a list of checkboxes.
     *
     * @param checkList a list of labels for each checkbox
     * @param title the title to show for the dialog
     */
    public CheckboxDialog(List<String> checkList, String title) {
        this.checkList = checkList;
        this.title = title;
    }

    /**
     * Get a dialog that shows checkboxes.  The labels for each checkbox
     * are those in the list given to the constructor.  The checkboxes
     * and labels are shown in the same order as they are in that list.
     *
     * When the dialog is closed with the Okay button, a list of strings
     * is returned.  This list is a subset of the list given in the
     * constructor, and contains only those for which the corresponding
     * checkbox was selected.
     *
     * @return a dialog that shows a list of checkboxes
     */
    public Dialog getDialog() {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        dialog.setResizable(true);
        VBox vbox = new VBox();
        ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel",
                                                     ButtonData.CANCEL_CLOSE);
        for (String label : checkList) {
            CheckBox checkbox = new CheckBox(label);
            vbox.getChildren().add(checkbox);
        }
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        // Needed to return the list.
        dialog.setResultConverter(new Callback<ButtonType, List<String>>() {
            @Override
            public List<String> call(ButtonType b) {
                if (b == buttonTypeOk) {
                    List<String> selectedList = new ArrayList<>(
                            checkList.size());
                    for (Node node : vbox.getChildren()) {
                        CheckBox cb = (CheckBox) node;
                        if (cb.isSelected()) {
                            selectedList.add(cb.getText());
                        }
                    }
                    return selectedList;
                }
                return null;
            }
        });
        return dialog;
    }


}
