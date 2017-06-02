package org.tum.project.dashboard_controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * dashboard for simulation and analytics
 * Created by heylbly on 17-6-2.
 */
public class DashBoardController implements Initializable{

    @FXML
    private JFXButton btnSimulation;


    @FXML
    void openSimulationPane(ActionEvent event) {
        setCenterNode();
    }

    private void setCenterNode() {

    }


    /**
     * initialize the content when loading this node
     * @param location location
     * @param resources resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createCenterNode();

    }

    private void createCenterNode() {

    }
}
