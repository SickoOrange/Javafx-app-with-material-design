package org.tum.project.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.tum.project.dataservice.CefVisualizationService;

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
        new Thread(() -> {
            cefVisualizationService.deleteLink(linkId);
            deleteTarget(event);
        }).start();

    }

    private void deleteTarget(ActionEvent event) {
        AnchorPane anchorPane = (AnchorPane) ((JFXButton) event.getSource()).getParent().getParent();
        HBox parent = (HBox) anchorPane.getParent();
        if (parent.getChildren().contains(anchorPane)) {
            System.out.println("delete this pane");
            Platform.runLater(() -> parent.getChildren().remove(anchorPane));

        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cefVisualizationService = MainController.getCefVisualizationService();
    }
}
