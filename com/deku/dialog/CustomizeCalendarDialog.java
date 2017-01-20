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

import com.deku.controller.DataOptionsController;


public class CustomizeCalendarDialog {

    private Dialog<String> dialog;
    private VBox mainVBox;
    private HBox daysHBox;
    private GridPane timesGrid;
    private HBox colorsHBox;
    private ButtonType buttonTypeOk;
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
        TextField endHour = new TextField(/* TODO: current setting */);
        TextField startMin = new TextField(/* TODO: current setting */);
        TextField endMin = new TextField(/* TODO: current setting */);
        Label startHourLabel = new Label("Start hour");
        Label endHourLabel = new Label("Last hour");
        Label startMinLabel = new Label("Start minute");
        Label endMinLabel = new Label("Last minute");

        timesGrid.add(startHourLabel, 1, 1);
        timesGrid.add(startHour, 2, 1);
        timesGrid.add(endHourLabel, 3, 1);
        timesGrid.add(endHour, 4, 1);

        timesGrid.add(startMinLabel, 1, 2);
        timesGrid.add(startMin, 2, 2);
        timesGrid.add(endMinLabel, 3, 2);
        timesGrid.add(endMin, 4, 2);
        // TODO: read settings file to initialize text.
    }

    /**
     * Create the container for choosing colors.
     */
    private void initColorsHBox() {
        colorsHBox = new HBox();
        // TODO: read settings file to initialize text.
        //colorsHBox.getChildren().addAll(checkboxList);
    }

    /**
     * Create the main layout.
     */
    private void initMainVBox() {
        mainVBox = new VBox();
        mainVBox.getChildren().addAll(daysHBox, timesGrid, colorsHBox);
    }
}
