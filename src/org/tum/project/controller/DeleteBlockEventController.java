package org.tum.project.controller;

import com.jfoenix.controls.JFXTextField;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.sun.xml.internal.bind.v2.TODO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import org.tum.project.CefModelEditor.CefModifyUtils;
import org.tum.project.CefModelEditor.CefVisualizationService;

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
        if (buttonType.get()== ButtonType.OK) {
            String blockName = deleteBlock_name.getText();
            //delete block from document root
            //delete a block will also delete all of its corresponding links
            cefVisualizationService.deleteBlock(blockName);
            //update the delete block ui
            cefVisualizationService.popCommand("delete_block");
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cefVisualizationService = MainController.getCefVisualizationService();
    }
}
