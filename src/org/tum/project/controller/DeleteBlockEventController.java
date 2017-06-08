package org.tum.project.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.tum.project.CefModelEditor.CefModifyUtils;
import org.tum.project.dataservice.CefVisualizationService;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * this class is user to delete a block from document root
 * Created by Yin Ya on 2017/5/14.
 */
public class DeleteBlockEventController implements Initializable {

    @FXML
    private JFXTextField deleteBlock_blockType;

    @FXML
    private JFXTextField deleteBlock_name;

    @FXML
    private JFXTextField deleteBlock_id;

    @FXML
    private JFXTextField deleteBlock_layer;
    private CefVisualizationService cefVisualizationService;

    @FXML
    void deleteBlockAction(ActionEvent event) throws IOException {
        String dialog = "Deleting a Block will delete all of its corresponding links\n" +
                "confirm delete?";
        Optional<ButtonType> buttonType = CefModifyUtils.alertDialog(dialog);
        if (buttonType.get() == ButtonType.OK) {
            String blockName = deleteBlock_name.getText();
            new Thread(() -> {
                cefVisualizationService.deleteBlock(blockName);
                deleteTarget(event);
            }).start();
        }
    }

    private void deleteTarget(ActionEvent event) {
        AnchorPane anchorPane = (AnchorPane) ((Pane) ((JFXButton) event.getSource()).getParent()).getParent();
        HBox parent = (HBox) anchorPane.getParent();
        if (parent.getChildren().contains(anchorPane)) {
            System.out.println("delete this block");
            Platform.runLater(() -> parent.getChildren().remove(anchorPane));
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cefVisualizationService = MainController.getCefVisualizationService();
    }
}
