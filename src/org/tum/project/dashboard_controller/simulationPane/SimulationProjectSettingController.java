package org.tum.project.dashboard_controller.simulationPane;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleGroup;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.tum.project.bean.ProjectInfo;
import org.tum.project.dashboard_controller.SimulationController;
import org.tum.project.login_controller.MenusHolderController;
import org.tum.project.utils.SimulationUtils;
import org.tum.project.utils.Utils;
import org.tum.project.utils.xmlUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * project setting class
 * Created by heylbly on 17-6-4.
 */
public class SimulationProjectSettingController implements Initializable {
    @FXML
    private JFXButton generateButton;

    @FXML
    private ToggleGroup level;


    @FXML
    private JFXRadioButton rb_load;
    @FXML
    private JFXRadioButton rb_new;
    @FXML
    private JFXComboBox<String> cb_file;
    @FXML
    private JFXTextField et_loadFactor;


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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("SimulationProjectSettingController initialize");
        SimulationController.registerSelfToController(this, getClass().getName());

        //find the xml file, from this file, we can find the project information
        //if file does't exist, then create it.
        //if file already exist, then do nothing.
        xmlFile = xmlUtils.createAndGetProjectXmlFile();


        //add the click item listener to the item of combo box
        cb_file.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //item click, set content to the UI
                ProjectInfo info = infosMap.get(newValue);
                et_fileName.setText(info.getSimulationFile());
                et_dbName.setText(info.getDataBankName());
                et_mtName.setText(info.getModuleTableName());
                et_ftName.setText(info.getFifoTableName());
                et_fftName.setText(info.getFastfifoTabelName());
            }
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
    public void startSimulationAction(ActionEvent actionEvent) {
        System.out.println("start simulation");

        //Collect the necessary path information
        SimulationPathSettingController simulationPathSettingController = (SimulationPathSettingController) SimulationController.getControllerInstance(SimulationPathSettingController.class.getName());
        String[] simulationPath = simulationPathSettingController.getSimulationPath();

        //Collect the necessary simulation dank bank information
        String db_name = et_dbName.getText();
        String mt_name = et_mtName.getText();
        String ft_name = et_ftName.getText();
        String fft_name = et_fftName.getText();

        //Collect the necessary cef model and save test bench test c++ file path
        String cefFilePath = et_cefFile.getText();
        String testBenchSavePath = et_testBench.getText();


        // TODO: 17-6-4  validate all string, can‘be null
        System.out.println("start collect information");
        //load the simulation to the default
        Utils.updatePropValue("ModelsNocPath", simulationPath[0]);
        Utils.updatePropValue("SystemCLibPath", simulationPath[1]);

        //only when the new project is active, then write the project info to the software
        if (rb_new.isSelected()) {
            ProjectInfo info = new ProjectInfo();
            info.setSimulationFile(et_fileName.getText());
            info.setDataBankName(db_name);
            info.setModuleTableName(mt_name);
            info.setFifoTableName(ft_name);
            info.setFastfifoTabelName(fft_name);
            xmlUtils.writeToDocument(info);
        }

        //generate the test bench c++ file
        System.out.println("start generate c++ file");
        SimulationUtils.generateTestFile(cefFilePath, testBenchSavePath);

        //compile the module
        System.out.println("start compile");
        SimulationUtils.compile(simulationPath[0]);

        //execute the simulation
        System.out.println("start simulation");
        String cmd = "./nocSim";
        SimulationUtils.execute(cmd, simulationPath[0],simulationPath[1]);

        System.out.println("simulation finish");


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
                System.out.println(info.getSimulationFile());
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
    }


}


