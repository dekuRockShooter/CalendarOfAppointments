package com.deku.controller;

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
import java.sql.SQLTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.nio.file.NoSuchFileException;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;

import javax.json.*;

import com.deku.model.CalendarModel;


/**
 * Singleton controller for accessing and modifying program settings.
 * Users use the load method to load the settings of the settings file.
 * Settings from the database are loaded on demand (that is, when a
 * corresponding get method is called).
 *
 * Files used to save settings are JSON encoded.  This controller uses
 * JsonObjects to give and receieve settings from the user.
 *
 * Settings that have been loaded can be accessed with the get methods.
 * Modifying these settings can be done with the set methods.
 */
public class SettingsController {

    private static final Path settingsFilePath = Paths.get("./settings");
    private static SettingsController instance = null;
    private static CalendarModel model;

    // The object that will be written to the file.
    private JsonObject mainObject;
    // Mapping of setting name to setting values.  The settings loaded from
    // disk are done so to this map.  The set and get methods operate on
    // this map.
    private Map<String, JsonObject> settingsMap;

    /**
     * Default private constructor.
     *
     * @throws SQLException
     */
    private SettingsController() throws SQLException {
        mainObject = null;
        settingsMap = null;
        model = CalendarModel.getInstance();
    }

    /**
     * Static factory method for getting an instance of this controller.
     *
     * @return the instance of this controller.  Call load() before using
     *         any other method.
     * @throws SQLException
     */
    public static SettingsController getInstance() throws SQLException {
        if (instance == null) {
            instance = new SettingsController();
        }
        return instance;
    }

    /**
     * Loads settings on disk into memory.  Call this method before any
     * other method.  If settings are already loaded, then calling this
     * method again will do nothing.
     */
    public void load() throws IOException {
        if (settingsMap != null) {
            return;
        }
        settingsMap = new HashMap<>();
        try {
            BufferedReader br = Files.newBufferedReader(
                    settingsFilePath,
                    Charset.forName("utf-8"));
            JsonReaderFactory readerFactory = Json.createReaderFactory(null);
            JsonReader reader = readerFactory.createReader(br);
            mainObject = reader.readObject();
            reader.close();
        }
        catch (NoSuchFileException err) {
            return;
        }
        for (String key : mainObject.keySet()) {
            settingsMap.put(key, mainObject.getJsonObject(key));
        }
    }

    /**
     * Get settings saved as the given key.
     *
     * @param key the indentifier under which the settings were saved
     * @return a JsonObject representing the settings for the given key.
     *         This is the same object as was given to the most recent
     *         invocation of set(...) with the same key.
     */
    public JsonObject get(String key) {
        return settingsMap.get(key);
    }

    /**
     * Modify settings.  This changes the settings stored in this controller
     * to the new one.  The changes are not written to disk!  Use the save
     * method to save all changes to disk.  All objects set with this method
     * can be retrieved with the get method and corresponding key.
     *
     * If the key is already associated with an object, the object is
     * overwritten.
     *
     * @param key an identifier for the setting
     * @param settings a JsonObject that stores settings.
     */
    public void set(String key, JsonObject settings) {
        settingsMap.put(key, settings);
    }

    /**
     * Set new colors to values for a data option.  Unlike set(String,
     * JsonObject), this saves the changes to disk immedetiately--
     * commit() does nothing to this setting.
     *
     * @param option the data option that the values belong to
     * @param valueColorMap a map whose keys should be values for the
     *                      data option.  In other words, the keys are
     *                      values that people have set the data option
     *                      to.  The values are Color objects that
     *                      specify the color to change the value to.
     *                      The color of any value that exists in the
     *                      database but not in this map is unmodified.
     * @throws SQLException if there is any error with the database
     * @throws SQLTimeoutException if there is any error with the database
     */
    public void setColors(String option, Map<String, Color> valueColorMap)
            throws SQLException, SQLTimeoutException {
        model.setColors(option, valueColorMap);
    }

    /**
     * Get the colors for all values of a data option.  This gets the colors
     * to paint each calendar cell with.
     *
     * @param option the option for which all values' colors is returned
     * @return a map of data values to Colors.  The data values (key)
     *         are all the unique values that have been assigned to the
     *         given data option.  The Colors are the color that the user
     *         assigned to the particular data value.
     */
    public Map<String, Color> getColors(String option)
            throws SQLException, SQLTimeoutException {
        List<Map<String, String>> results = model.getColors(option);
        Map<String, Color> valueColorMap = new HashMap<>();
        for (Map<String, String> rgbMap : results) {
            Color color = new Color(Double.parseDouble(rgbMap.get("ColorRed")),
                                    Double.parseDouble(rgbMap.get("ColorGreen")),
                                    Double.parseDouble(rgbMap.get("ColorBlue")),
                                    1.0);
            valueColorMap.put(rgbMap.get("Value"), color);
        }
        return valueColorMap;
    }

    /**
     * Write all changes to disk.  The set(...) method changes the internal
     * data of this controller.  The commit method writes those changes to
     * disk.
     */
    public void commit() throws IOException {
        JsonWriterFactory writerFactory = Json.createWriterFactory(null);
        BufferedWriter bw = Files.newBufferedWriter(
                settingsFilePath,
                Charset.forName("utf-8"));
        JsonWriter writer = writerFactory.createWriter(bw);
        JsonObjectBuilder builder = Json.createObjectBuilder();
        for (String key : settingsMap.keySet()) {
            builder.add(key, settingsMap.get(key));
        }
        writer.writeObject(builder.build());
        writer.close();
    }

}
