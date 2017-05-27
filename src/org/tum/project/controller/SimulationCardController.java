package org.tum.project.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.tum.project.CefModelEditor.CefModifyUtils;
import org.tum.project.bean.ProjectInfo;
import org.tum.project.testbench.TestBenchStageController;
import org.tum.project.utils.SystemCSimExcecute;
import org.tum.project.utils.xmlUtils;

import java.io.IOException;
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
    private JFXButton simulationCard_testBenchButton;

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

    /**
     * start to simulation the system
     *
     * @param event mouse event
     */
    @FXML
    void simulationAction(ActionEvent event) {
        //SystemcModels-Noc
        System.out.println("simulation start");
        String compilePath = "";
        SystemCSimExcecute.compile(compilePath);
        System.out.println("simulation compile finish");
        String cmd = "./nocSim";
        SystemCSimExcecute.execute(cmd, compilePath);
        System.out.println("simulation finish");
    }

    @FXML
    void testBenchAction(ActionEvent event) {
        //generate test bench cpp file for simulation
        Stage testBenchStage = new Stage();
        Parent textBenchRoot = null;
        try {
            textBenchRoot = FXMLLoader.load(getClass().getResource("../layout/test_bench_stage.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        testBenchStage.setScene(new Scene(textBenchRoot, 550, 300));
        testBenchStage.setTitle("generate test bench cpp file");
        testBenchStage.toFront();
        testBenchStage.show();
        TestBenchStageController controller = new TestBenchStageController();
        controller.setStage(testBenchStage);

    }
}
