package org.tum.project.tum_controller;

import com.jfoenix.controls.JFXRippler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Yin Ya on 2017/5/28.
 */
public class MenusHolderController implements Initializable{
    @FXML
    private AnchorPane NocEditor;

    @FXML
    private AnchorPane Analytics;

    @FXML
    private HBox Holder;


    @FXML
    private void openNocEditor(MouseEvent event) {
        System.out.println("click one");
    }

    @FXML
    private void openAnalytics(MouseEvent event) {
        System.out.println("click two");
    }


    private void setUpRipples() {
        JFXRippler fXRippler_Noc = new JFXRippler(NocEditor, JFXRippler.RipplerMask.RECT, JFXRippler.RipplerPos.FRONT);
        JFXRippler fXRippler_analytics = new JFXRippler(Analytics, JFXRippler.RipplerMask.RECT, JFXRippler.RipplerPos
                .FRONT);
        Holder.getChildren().addAll(fXRippler_Noc, fXRippler_analytics);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpRipples();
    }
}
