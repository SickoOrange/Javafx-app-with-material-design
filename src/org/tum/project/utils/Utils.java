package org.tum.project.utils;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * utils
 * Created by Yin Ya on 2017/5/17.
 */
public class Utils {

    public static File openFileChooser(String flag, Stage stage) {
        FileChooser chooser = new FileChooser();
        switch (flag) {
            case "open":
                return chooser.showOpenDialog(stage);
            case "save":
                return chooser.showSaveDialog(stage);
        }
        return null;
    }


    public static File openDirectorChooser(String flag, Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        switch (flag) {
            case "open":
                return directoryChooser.showDialog(stage);
            case "save":
                return null;
        }
        return null;
    }


    /**
     * get the prop file
     *
     * @return prop file
     */
    private static File getPropFile() {
        String basePath = Utils.class.getResource("../").getFile();
        File propertiesFile = new File(basePath, File.separator + "prop.properties");
        if (!propertiesFile.exists()) {
            try {
                propertiesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return propertiesFile;
    }

    /**
     * get the prop file, that container the user name and
     * user password for a successfully login
     *
     * @return prop file
     */
    private static Properties getProp() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(getPropFile()));
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * read the target value with the given key
     *
     * @param key key
     * @return return value, if not found then return null
     */
    public static String readPropValue(String key) {
        Properties prop = getProp();
        return (String) prop.get(key);
    }

    /**
     * update the target value with the given key
     *
     * @param key   given key
     * @param value given value with this key
     */
    public static void updatePropValue(String key, String value) {
        Properties prop = getProp();
        prop.put(key, value);
        try {
            prop.store(new FileOutputStream(getPropFile()), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
