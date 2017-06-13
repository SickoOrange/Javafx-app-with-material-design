package org.tum.project.dashboard_controller.simulationPane;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.tum.project.bean.ProjectInfo;
import org.tum.project.dashboard_controller.DashBoardController;
import org.tum.project.dashboard_controller.SimulationController;
import org.tum.project.dataservice.FifoSizeService;
import org.tum.project.dataservice.FlitTraceService;
import org.tum.project.dataservice.FlowLatencyService;
import org.tum.project.dataservice.FlowPacketLatencyService;
import org.tum.project.login_controller.MenusHolderController;
import org.tum.project.utils.SimulationUtils;
import org.tum.project.utils.Utils;
import org.tum.project.utils.xmlUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/**
 * project setting class
 * Created by heylbly on 17-6-4.
 */
public class SimulationProjectSettingController implements Initializable {

    @FXML
    private JFXButton btnSimulation;

    @FXML
    private JFXRadioButton rb_new;
    @FXML
    private JFXComboBox<String> cb_file;

    @FXML
    private JFXTextField et_loadFactor;

    @FXML
    private JFXTextField et_frequency;


    @FXML
    private JFXTextField et_dbName;
    @FXML
    private JFXTextField et_ftName;
    @FXML
    private JFXTextField et_fileName;
    @FXML
    private JFXTextField et_mtName;
    @FXML
    private JFXTextField et_fftName;

    @FXML
    private JFXTextField et_cefFile;
    @FXML
    private JFXTextField et_testBench;


    private File xmlFile;
    private HashMap<String, ProjectInfo> infosMap;
    private Thread simulationThread;
    private SimulationPathSettingController simulationPathSettingController;
    private SimulationProgressController simulationProgressController;


    /**
     * select default test bench path
     *
     * @param actionEvent
     */
    public void loadDefaultTestBenchPathAction(ActionEvent actionEvent) {
        et_cefFile.setEditable(false);
        et_testBench.setEditable(false);
        String cefPath = Utils.readPropValue("CefFilePath");
        String saveCppPath = Utils.readPropValue("SaveCppPath");
        et_cefFile.setText(cefPath);
        et_testBench.setText(saveCppPath);
    }

    class SimulationTask implements Runnable {

        @Override
        public void run() {
            try {
                System.out.println("start simulation");
                btnSimulation.setDisable(true);
                simulationProgressController = (SimulationProgressController) SimulationController.getControllerInstance(SimulationProgressController.class.getName());
                simulationProgressController.clear();
                simulationProgressController.startAnimation1("Checking for Start");


                //Collect the necessary path information
                simulationPathSettingController = (SimulationPathSettingController) SimulationController.getControllerInstance(SimulationPathSettingController.class.getName());

                String[] simulationPath = simulationPathSettingController.getSimulationPath();

                //Collect the necessary simulation dank bank information
                String db_name = et_dbName.getText();
                String mt_name = et_mtName.getText();
                String ft_name = et_ftName.getText();
                String fft_name = et_fftName.getText();

                String loadFactor = et_loadFactor.getText();
                String sampleFrequency = et_frequency.getText();

                //Collect the necessary cef model and save test bench test c++ file path
                String cefFilePath = et_cefFile.getText();
                String testBenchSavePath = et_testBench.getText();


                // TODO: 17-6-4  validate all string, can‘be null
                System.out.println("start collect information");
                simulationProgressController.startAnimation2("Collecting\n" + "Info\n");


                ProjectInfo info = new ProjectInfo();
                info.setSimulationFile(et_fileName.getText());
                info.setDataBankName(db_name);
                info.setModuleTableName(mt_name);
                info.setFifoTableName(ft_name);
                info.setFastfifoTabelName(fft_name);
                info.setLoadFactor(loadFactor);
                info.setSampleFrequency(sampleFrequency);

                //only when the new project is active, then write the project info to the app intern
                if (rb_new.isSelected()) {
                    xmlUtils.writeToDocument(info);
                }


                //write the relative information to the json file in the simulation path.
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                String parametersPath = simulationPath[0] + "/params.json";
                try {
                    FileWriter writer = new FileWriter(parametersPath);
                    writer.write(gson.toJson(info));
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //generate the test bench c++ file
                System.out.println("Start generate c++ file");
                simulationProgressController.startAnimation3("Generate\n" + "file\n");
                SimulationUtils.generateTestFile(cefFilePath, testBenchSavePath);

                //compile the module
                System.out.println("start compile");
                simulationProgressController.startAnimation4("Start\n" + "to\n" + "compile\n");
                SimulationUtils.compile(simulationPath[0]);

                //execute the simulation
                System.out.println("start simulation");
                simulationProgressController.startAnimation5("Start\n" + "to\n" + "simulate\n");
                String cmd = "./nocSim " + et_loadFactor.getText();
                SimulationUtils.execute(cmd, simulationPath[0], simulationPath[1]);


                //start to analyze
                System.out.println("start to analyze");
                simulationProgressController.startAnimation6("Start\n" + "to\n" + "analyze\n");


                //execute the analysis for flow latency
                //module table name list is needed for the execution
                FlowLatencyService flowLatencyInstance = (FlowLatencyService) DashBoardController.getDataServiceInstance(FlowLatencyService.class.getName());
                List<String> moduleTableList = new ArrayList<>();
                //moduleTableList.add(mt_name);
                //flowLatencyInstance.startAnalyze(moduleTableList, db_name);
                moduleTableList.add("module_simulation_2017_6_10_14_21_58");
                flowLatencyInstance.startAnalyze(moduleTableList, "SystemC");


                //execute the analysis for flow packet details
                //module table name list is needed for the execution
                FlowPacketLatencyService flowPacketLatencyService = (FlowPacketLatencyService) DashBoardController.getDataServiceInstance(FlowPacketLatencyService.class.getName());
                flowPacketLatencyService.startAnalyze(moduleTableList, "SystemC");


                //execute the analysis for fifo size analyse details
                //fifo size table name list is needed for the execution
                FifoSizeService fifoSizeService = (FifoSizeService) DashBoardController.getDataServiceInstance(FifoSizeService.class.getName());
                List<String> fifoTabelList = new ArrayList<>();
                //fifoTabelList.add(ft_name);
                fifoTabelList.add("fifo_simulation_2017_6_10_14_21_58");
                fifoSizeService.startAnalyze(fifoTabelList, "SystemC");

                //execute the analysis for trace flits details
                FlitTraceService flitTraceService = (FlitTraceService) DashBoardController.getDataServiceInstance(FlitTraceService.class.getName());
                List<String> fastfifoTabelList = new ArrayList<>();
                //fifoTabelList.add(fft_name);
                fastfifoTabelList.add("fastfiforw_simulation_2017_6_10_14_21_58");
                flitTraceService.startAnalyze(fastfifoTabelList, "SystemC");
                btnSimulation.setDisable(false);

                //load the simulation to the default
                Utils.updatePropValue("ModelsNocPath", simulationPath[0]);
                Utils.updatePropValue("SystemCLibPath", simulationPath[1]);

                Utils.updatePropValue("CefFilePath", cefFilePath);
                Utils.updatePropValue("SaveCppPath", testBenchSavePath);


                //finish
                System.out.println("finish");
                simulationProgressController.stopAnimation6();
            } catch (Exception e) {
                System.out.println("Exception simulation aborting");
                SimulationProgressController simulationProgressController = (SimulationProgressController) DashBoardController.getDataServiceInstance(SimulationProgressController.class.getName());
                simulationProgressController.stopALL();
                simulationProgressController.setError("An exception occurs \n" +
                        "Emulation is terminated");


                btnSimulation.setDisable(false);
            }

        }
    }

    @FXML
    public void startSimulationAction(ActionEvent actionEvent) {
        simulationThread = new Thread(new SimulationTask());
        simulationThread.start();

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("SimulationProjectSettingController initialize");
        SimulationController.registerSelfToController(this, getClass().getName());

        //find the xml file, from this file, we can find the project information
        //if file does't exist, then create it.
        //if file already exist, then do nothing.
        xmlFile = xmlUtils.createAndGetProjectXmlFile();


        //add the click item listener to the item of combo box
        cb_file.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //item click, set content to the UI
            ProjectInfo info = infosMap.get(newValue);
            if (info == null) {
                return;
            }
            et_fileName.setText(info.getSimulationFile());
            et_dbName.setText(info.getDataBankName());
            et_mtName.setText(info.getModuleTableName());
            et_ftName.setText(info.getFifoTableName());
            et_fftName.setText(info.getFastfifoTabelName());

            et_loadFactor.setText(info.getLoadFactor());
            et_frequency.setText(info.getSampleFrequency());
        });


        et_cefFile.setOnMouseClicked(event -> {
            File file = Utils.openFileChooser("open", MenusHolderController.getDashBoardStage());
            if (file != null) {
                et_cefFile.setText(file.getAbsolutePath());
                System.out.println(file.getAbsolutePath());
            }

        });

        et_testBench.setOnMouseClicked(event -> {
            File file = Utils.openDirectorChooser("open", MenusHolderController.getDashBoardStage());
            if (file != null) {
                et_testBench.setText(file.getAbsolutePath());
                System.out.println(file.getAbsolutePath());
            }
        });
    }


    public static void main(String[] args) {
        System.out.println("test");
        SimulationUtils.generateTestFile("/home/heylbly/Desktop/multimedia_4x4_routed.xml", "/home/heylbly/Desktop");
        System.out.println("finish");
    }


    @FXML
    void rb_loadAction(ActionEvent event) {
        setEditTextEnable(false);
        loadingIntoComboBox();
    }


    /**
     * loading project information from existing xml file into the combo box
     */
    private void loadingIntoComboBox() {
        try {
            cb_file.getItems().clear();
            Document document = xmlUtils.readDocument(xmlFile.getAbsolutePath());
            ArrayList<ProjectInfo> infos = xmlUtils.getAllProjectFromDocument(document);
            infosMap = new HashMap<>();
            for (ProjectInfo info : infos) {
                cb_file.getItems().add(info.getSimulationFile());
                infosMap.put(info.getSimulationFile(), info);
            }
            cb_file.show();
        } catch (MalformedURLException | DocumentException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void rb_newAction(ActionEvent event) {
        setEditTextEnable(true);
        clearEditTextContent();
    }

    /**
     * enable or not enable all edit text that Project Setting include
     *
     * @param editTextEnable whether enable or not enable
     */
    public void setEditTextEnable(boolean editTextEnable) {
        et_fileName.setEditable(editTextEnable);
        et_dbName.setEditable(editTextEnable);
        et_mtName.setEditable(editTextEnable);
        et_fftName.setEditable(editTextEnable);
        et_ftName.setEditable(editTextEnable);

        et_loadFactor.setEditable(editTextEnable);
        et_frequency.setEditable(editTextEnable);
    }

    /**
     * clear the content of all the edit text field
     */
    public void clearEditTextContent() {
        et_fileName.setText(null);
        et_dbName.setText(null);
        et_mtName.setText(null);
        et_fftName.setText(null);
        et_ftName.setText(null);

        et_loadFactor.setText(null);
        et_frequency.setText(null);
    }


}


