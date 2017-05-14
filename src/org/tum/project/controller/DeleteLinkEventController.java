package org.tum.project.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Created by Yin Ya on 2017/5/14.
 */
public class DeleteLinkEventController {
//    @FXML
//    private JFXTextField deleteLink_carriesSourceClock;
//
//    @FXML
//    private JFXTextField deleteLink_carriesSourceReset;
//
//    @FXML
//    private JFXTextField deleteLink_linkLengthEstimation;
//
//    @FXML
//    private JFXTextField deleteLink_auxiliaryForwardWires;

    @FXML
    private JFXTextField deleteLink_destinationPortId;

    @FXML
    private JFXTextField deleteLink_sourcePortId;

    @FXML
    private JFXTextField deleteLink_name;

    @FXML
    private JFXTextField deleteLink_id;

//    @FXML
//    private JFXTextField auxiliaryBackwardWires;

    @FXML
    void deleteLinkSubmit(ActionEvent event) {
        System.out.println("delete link ");

    }

}
