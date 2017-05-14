package org.tum.project.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * add port pane
 * submit to modify the document root
 * Created by Yin Ya on 2017/5/14.
 */
public class BlockEventController implements Initializable {


    @FXML
    void submit(ActionEvent event) {
        System.out.println("block submit");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
