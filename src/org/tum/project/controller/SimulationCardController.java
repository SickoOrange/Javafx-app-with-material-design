package org.tum.project.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import org.tum.project.CefModelEditor.CefModifyUtils;
import org.tum.project.bean.ProjectInfo;
import org.tum.project.utils.xmlUtils;

import java.util.Optional;

/**
 * this class is used for generate new simulation project and simulate the select simulation
 * generate new project name, date bank information to storage the simulation information to the date bank
 * Created by Yin Ya on 2017/5/16.
 */
public class SimulationCardController {
    @FXML
    private JFXTextField simulationCard_fifoTable;

    @FXML
    private JFXButton simulationCard_simulationButton;

    @FXML
    private JFXTextField simulationCard_fastfifoTable;

    @FXML
    private JFXTextField simulationCard_projectName;

    @FXML
    private JFXButton simulationCard_generateButton;

    @FXML
    private JFXTextField simulationCard_dataBankName;

    @FXML
    private JFXTextField simulationCard_moduleTable;

    @FXML
    void generationAction(ActionEvent event) {

        String dialog = "Once a new project is created, the information can not be changed any more,\n" +
                "Please check that the information is correct";
        Optional<ButtonType> buttonType = CefModifyUtils.alertDialog(dialog);
        if (buttonType.get() == ButtonType.OK) {
            if (!simulationCard_projectName.getText().equals("") && (!simulationCard_dataBankName.getText().equals("")) &&
                    (!simulationCard_moduleTable.getText().equals("")) && (!simulationCard_fifoTable.getText().equals
                    ("")) && (!simulationCard_fastfifoTable.getText().equals(""))) {
                ProjectInfo info = new ProjectInfo();
                info.setProjectName(simulationCard_projectName.getText());
                info.setDataBankName(simulationCard_dataBankName.getText());
                info.setModuleTableName(simulationCard_moduleTable.getText());
                info.setFifoTableName(simulationCard_fifoTable.getText());
                info.setFastfifoTabelName(simulationCard_fastfifoTable.getText());

                xmlUtils.writeToDocument(info);

                setElementDisable();
                //generate test bench
            } else {
                CefModifyUtils.alertDialog("All options can not be empty!");
                return;
            }
        }


    }

    /**
     * set edit text and button disable
     */
    private void setElementDisable() {
        simulationCard_projectName.setEditable(false);
        simulationCard_dataBankName.setEditable(false);
        simulationCard_moduleTable.setEditable(false);
        simulationCard_fifoTable.setEditable(false);
        simulationCard_fastfifoTable.setEditable(false);
        simulationCard_generateButton.setDisable(true);

    }

    @FXML
    void simulationAction(ActionEvent event) {

    }
}
