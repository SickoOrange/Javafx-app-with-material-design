package org.tum.project.dashboard_controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * simulation controller
 * Created by heylbly on 17-6-2.
 */
public class SimulationController implements Initializable {
    @FXML
    private AnchorPane root;


    private Node simulationPathSetting;
    private Node simulationProjectSetting;
    private Node simulationProgress;

    private static HashMap<String, Object> instanceMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("SimulationController initialize");
        createContent();
        setContent();
    }

    /**
     * set the node into the simulation pane
     */
    private void setContent() {
        root.getChildren().clear();
        root.getChildren().add(simulationPathSetting);
        root.getChildren().add(simulationProjectSetting);
        root.getChildren().add(simulationProgress);
    }

    /**
     * create content for Simulation layout
     */
    private void createContent() {
        try {
            simulationPathSetting = FXMLLoader.load(getClass().getResource("./simulationPane/SimulationPathSetting.fxml"));
            simulationPathSetting.setLayoutX(22.0);
            simulationPathSetting.setLayoutY(14.0);
            simulationProjectSetting = FXMLLoader.load(getClass().getResource("./simulationPane/SimulationProjectSetting.fxml"));
            simulationProjectSetting.setLayoutX(22.0);
            simulationProjectSetting.setLayoutY(189.0);
            simulationProgress = FXMLLoader.load(getClass().getResource("./simulationPane/SimulationProgress.fxml"));
            simulationProgress.setLayoutX(22.0);
            simulationProgress.setLayoutY(580);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        URL resource = SimulationController.class.getResource("./simulationPane");
        System.out.println(resource);
    }

    public static void registerSelfToController(Object clazz, String name) {
        instanceMap.put(name, clazz);
    }

    public static Object getControllerInstance(String key) {
        return instanceMap.get(key);
    }
}
