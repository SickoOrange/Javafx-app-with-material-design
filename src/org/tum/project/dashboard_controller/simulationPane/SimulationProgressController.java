package org.tum.project.dashboard_controller.simulationPane;

import javafx.fxml.Initializable;
import org.tum.project.dashboard_controller.SimulationController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by heylbly on 17-6-4.
 */
public class SimulationProgressController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SimulationController.registerSelfToController(this, getClass().getName());
    }
}
