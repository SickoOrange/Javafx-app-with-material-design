package org.tum.project.dashboard_controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * dashboard for simulation and analytics
 * Created by heylbly on 17-6-2.
 */
public class DashBoardController implements Initializable {


    private Node simulationPane;

    @FXML
    private AnchorPane holderPane;


    @FXML
    void openSimulationPane(ActionEvent event) {
        setCenterNode(simulationPane);
    }


    /**
     * initialize the content when loading this node
     *
     * @param location  location
     * @param resources resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createCenterNode();
        setCenterNode(simulationPane);
    }


    /**
     * Appends the specified element to the end of this pane list
     *
     * @param element element, that need to be specified to the panelist
     */
    private void setCenterNode(Node element) {
        holderPane.getChildren().clear();
        holderPane.getChildren().add(element);
    }

    /**
     * create different node, that need to be loaded into the right pane
     */
    private void createCenterNode() {
        try {
            simulationPane = FXMLLoader.load(getClass().getResource("../dashboard_controller/Simulation.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
