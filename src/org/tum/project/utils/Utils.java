package org.tum.project.utils;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by Yin Ya on 2017/5/17.
 */
public class Utils {

    public static File openFileChooser(String flag, Stage stage) {
        FileChooser chooser = new FileChooser();
        File file = null;
        if (flag.equals("open")) {
            file = chooser.showOpenDialog(stage);
        }
        if (flag.equals("save")) {
            file = chooser.showSaveDialog(stage);
        }
        return file;
    }
}
