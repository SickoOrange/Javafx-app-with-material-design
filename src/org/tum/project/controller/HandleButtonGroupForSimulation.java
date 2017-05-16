package org.tum.project.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.tum.project.dataservice.SimulationService;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Yin Ya on 2017/5/16.
 */
public class HandleButtonGroupForSimulation implements Initializable{
    @FXML
    private JFXButton simulation_new;

    @FXML
    private JFXTextField simulation_path1;

    @FXML
    private JFXCheckBox simulation_check;

    @FXML
    private JFXTextField simulation_path2;

    @FXML
    private JFXTextField simulation_path3;

    @FXML
    private JFXTextField simulation_path4;
    private SimulationService simulationService;


    @FXML
    void createAction(ActionEvent event) {
        System.out.println("add new project");
        simulationService.createNewProjectCard();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        simulationService = MainController.getSimulationService();
    }
}
