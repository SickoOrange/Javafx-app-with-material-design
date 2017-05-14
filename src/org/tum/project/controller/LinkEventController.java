package org.tum.project.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * add link pane
 * submit to modify the document root
 * Created by Yin Ya on 2017/5/14.
 */
public class LinkEventController {
    @FXML
    private JFXTextField carriesSourceClock;

    @FXML
    private JFXTextField carriesSourceReset;

    @FXML
    private JFXTextField linkLengthEstimation;

    @FXML
    private JFXTextField auxiliaryForwardWires;

    @FXML
    private JFXTextField destinationPortId;

    @FXML
    private JFXTextField sourcePortId;

    @FXML
    private JFXTextField name;

    @FXML
    private JFXTextField id;

    @FXML
    private JFXTextField auxiliaryBackwardWires;

    @FXML
    void submit(ActionEvent event) {
        System.out.println("link submit");

    }

}
