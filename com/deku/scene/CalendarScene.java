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
    private Calendar curWeek;
    private TableView table;
    private VBox vbox;
    private HBox navBar;
    private DatePicker datePicker;

    public Scene getInstance() throws SQLException {
        curWeek = Calendar.getInstance();
        calendarFactory = new CalendarFactory();
        table = calendarFactory.getThisWeek();
        vbox = new VBox();
        navBar = new HBox();
        final Button nextWeekButton = new Button("Next");
        final Button prevWeekButton = new Button("Prev");
        initButton(nextWeekButton, 'n');
        initButton(prevWeekButton, 'p');
        datePicker = new DatePicker();
        initDatePicker();

        navBar.getChildren().addAll(prevWeekButton, datePicker,
                                    nextWeekButton);
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(navBar, table);
        Scene scene = new Scene(vbox);
        return scene;
    }

    /**
     * Initialize the buttons for navigation to the next/prev week.
     *
     * @param button the button to initalize.  This should be a button
     *               for moving to the next or previous week.
     * @param direction a char for specifying which week to go to.  If
     *                  'n', then the next week's calendar is loaded.
     *                  If 'p' then the previous week's calendar is loaded.
     *                  If anything else, then the current week's calendar
     *                  is loaded.
     */
    private void initButton(Button button, char direction) {
        int dir = 0;
        if (direction == 'p') {
            dir = -7;
        }
        else if (direction == 'n') {
            dir = 7;
        }
        final int step = dir;
        button.setOnAction(e -> {
            curWeek.add(Calendar.DAY_OF_MONTH, step);
            // Throw RuntimeException because the overriden method does
            // not throw an SQLException and the code wont compile.
            table = null;
            refreshTable();
        });
    }

    /**
     * Initialize the DatePicker.
     */
    private void initDatePicker() {
        LocalDate local = LocalDate.of(curWeek.get(Calendar.YEAR),
                                       curWeek.get(Calendar.MONTH) + 1,
                                       curWeek.get(Calendar.DAY_OF_MONTH));
        datePicker.setValue(local);
        datePicker.setOnAction(ev -> {
            LocalDate date = datePicker.getValue();
            // -1 because Calendar counts months from 0 to 11, but LocalDate
            // returns does from 1 to 12.
            curWeek.set(Calendar.MONTH, date.getMonthValue() - 1);
            curWeek.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
            curWeek.set(Calendar.YEAR, date.getYear());
            // Throw RuntimeException because the overriden method does
            // not throw an SQLException and the code wont compile.
            refreshTable();
        });

    }

    private void refreshTable() {
        try {
            vbox.getChildren().clear();
            table = calendarFactory.getWeek(curWeek);
            vbox.getChildren().addAll(navBar, table);
        }
        catch (SQLException err) {
            throw new RuntimeException(err.toString());
        }
    }
}
