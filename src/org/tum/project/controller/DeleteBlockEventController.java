package org.tum.project.controller;

import com.jfoenix.controls.JFXTextField;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

/**
 * Created by Yin Ya on 2017/5/14.
 */
public class DeleteBlockEventController {

    @FXML
    private JFXTextField blockType;

    @FXML
    private JFXTextField name;

    @FXML
    private JFXTextField id;

    @FXML
    private JFXTextField layer;

    @FXML
    void deleteBlockAction(ActionEvent event) {
        System.out.println("delete block type");

    }


}
