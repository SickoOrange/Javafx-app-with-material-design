package org.tum.project.dashboard_controller.simulationPane;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import org.tum.project.dashboard_controller.SimulationController;
import org.tum.project.login_controller.MenusHolderController;
import org.tum.project.utils.Utils;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * path setting class
 * Created by heylbly on 17-6-4.
 */
public class SimulationPathSettingController implements Initializable {

    @FXML
    public JFXRadioButton defaultPath;
    @FXML
    public JFXRadioButton newPath;

    @FXML
    private JFXTextField systemCModelsNocPathEditText;
    @FXML
    private JFXTextField systemCLibPathEditText;


    public String[] getSimulationPath() {
        String[] path = new String[2];
        path[0] = systemCModelsNocPathEditText.getText();
        path[1] = systemCLibPathEditText.getText();
        return path;
    }


    /**
     * Initialize when the layout is loaded for the first time
     *
     * @param location  location
     * @param resources resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //load the default simulation path, if null then select "new path"
        System.out.println("SimulationPathSettingController  initialize");

        SimulationController.registerSelfToController(this, getClass().getName());
        String modelsNocPath = Utils.readPropValue("ModelsNocPath");
        String systemCLibPath = Utils.readPropValue("SystemCLibPath");
        if (modelsNocPath == null) {
            openNewPath();
        } else {
            //load the path string to the edit text
            systemCModelsNocPathEditText.setText(modelsNocPath);
            systemCLibPathEditText.setText(systemCLibPath);
        }
    }


    /**
     * save button, to save the new simulation path in the Environment
     *
     * @param actionEvent
     */
    @FXML
    public void saveInDefaultAction(ActionEvent actionEvent) {
        if (newPath.isSelected()) {
            Utils.updatePropValue("ModelsNocPath", systemCModelsNocPathEditText.getText());
            Utils.updatePropValue("SystemCLibPath", systemCLibPathEditText.getText());
        } else {
            // TODO: 17-6-4 material dialog tipps or snackbar
            System.out.println("only avaliable in the new path selection");
        }

    }

    /**
     * load the default simulation path in the memory
     *
     * @param actionEvent mouse click event
     */
    @FXML
    public void onDefaultPathButtonAction(ActionEvent actionEvent) {
        System.out.println("default path action");
        openDefaultPath();

    }

    private void openDefaultPath() {
        defaultPath.setSelected(true);
        String modelsNocPath = Utils.readPropValue("ModelsNocPath");
        String systemCLibPath = Utils.readPropValue("SystemCLibPath");
        systemCModelsNocPathEditText.setText(modelsNocPath);
        systemCLibPathEditText.setText(systemCLibPath);

    }

    /**
     * use the new simulation path from the path chooser selector
     *
     * @param actionEvent click event
     */
    @FXML
    public void onNewPathButtonAction(ActionEvent actionEvent) {
        System.out.println("new path action ");
        openNewPath();
    }

    /**
     * create new simulation path
     * enable the edit text and receive the new path string
     */
    private void openNewPath() {
        // defaultPath.setSelected(false);
        newPath.setSelected(true);
        systemCLibPathEditText.setText(null);
        systemCModelsNocPathEditText.setText(null);
    }


    /**
     * click the edit text and then select directory path
     *
     * @param mouseEvent
     */
    public void selectPathForModelAction(MouseEvent mouseEvent) {
        if (!defaultPath.isSelected() && newPath.isSelected()) {
            File file = Utils.openDirectorChooser("open", MenusHolderController.getDashBoardStage());
            System.out.println("selectPathForModelAction: " + file.getAbsolutePath());
            systemCModelsNocPathEditText.setText(file.getAbsolutePath());
        }
    }

    /**
     * click the edit text and then select directory path
     *
     * @param mouseEvent
     */
    public void selectPathForLibraryAction(MouseEvent mouseEvent) {
        if (!defaultPath.isSelected() && newPath.isSelected()) {
            File file = Utils.openDirectorChooser("open", MenusHolderController.getDashBoardStage());
            System.out.println("selectPathForLibraryAction: " + file.getAbsolutePath());
            systemCLibPathEditText.setText(file.getAbsolutePath());
        }
    }

}
