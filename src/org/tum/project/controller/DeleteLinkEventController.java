package org.tum.project.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.tum.project.CefModelEditor.CefVisualizationService;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * delete a link from document root
 * Created by Yin Ya on 2017/5/14.
 */
public class DeleteLinkEventController implements Initializable {
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
    private CefVisualizationService cefVisualizationService;

//    @FXML
//    private JFXTextField auxiliaryBackwardWires;

    @FXML
    void deleteLinkSubmit(ActionEvent event) throws IOException {
        BigInteger linkId = new BigInteger(deleteLink_id.getText());
        cefVisualizationService.deleteLink(linkId);
        //update the layout ui
        cefVisualizationService.popCommand("delete_link");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cefVisualizationService = MainController.getCefVisualizationService();
    }
}
