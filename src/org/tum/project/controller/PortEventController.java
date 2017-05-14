package org.tum.project.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Created by Yin Ya on 2017/5/14.
 */
public class PortEventController {
    @FXML
    private JFXTextField positionY;

    @FXML
    private JFXTextField writeDataWidth;

    @FXML
    private JFXTextField protocol;

    @FXML
    private JFXTextField maxOutStandingTransaction;

    @FXML
    private JFXButton submit;

    @FXML
    private JFXTextField blockName;

    @FXML
    private JFXTextField id;

    @FXML
    private JFXTextField flitWidth;

    @FXML
    private JFXTextField readDataWidth;

    @FXML
    private JFXTextField adressWidth;

    @FXML
    private JFXTextField domainId;

    @FXML
    private JFXTextField positionX;


    @FXML
    void submitPortInfo(ActionEvent event) {
        System.out.println("port submit");

    }


}
