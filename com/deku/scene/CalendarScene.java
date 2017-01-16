package com.deku.scene;

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
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;

import java.util.Optional;
import java.util.Calendar;
import java.time.LocalDate;
import java.sql.SQLException;

import com.deku.control.CalendarFactory;


public class CalendarScene {

    private CalendarFactory calendarFactory;

    public Scene getInstance() throws SQLException {
        calendarFactory = new CalendarFactory();
        TableView table = calendarFactory.getThisWeek();
        final VBox vbox = new VBox();
        final HBox navBar = new HBox();
        final Button nextWeekButton = new Button("Next");
        final Button prevWeekButton = new Button("Prev");
        initButton(nextWeekButton, 'n');
        initButton(prevWeekButton, 'p');
        final DatePicker datePicker = new DatePicker();
        datePicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                LocalDate date = datePicker.getValue();
                System.err.println("Selected date: " + date);
            }
        });

        navBar.getChildren().addAll(prevWeekButton, datePicker,
                                    nextWeekButton);
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(navBar, table);
        Scene scene = new Scene(vbox);
        return scene;
    }

    private void initButton(Button button, char direction) {
        button.setOnAction(e -> {
            System.err.println("click");
        });
    }

}
