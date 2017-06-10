package org.tum.project.testbench;

import Cef.CefType;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.tum.project.utils.CefModifyUtils;
import org.tum.project.controller.MainController;
import org.tum.project.utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * generate the test bench file
 * Created by Yin Ya on 2017/5/17.
 */
public class TestBenchStageController implements Initializable {
    @FXML
    private JFXTextField cefFile;

    @FXML
    private JFXButton generateButton;

    @FXML
    private JFXTextField testBenchFile;

    @FXML
    private Label statusLabel;

    private Stage mainControllerStage;
    private File getCefFile;
    private File getTestBenchFile;
    private Stage testBenchStage;


    /**
     * generate a test bench cpp file with the select getCefFile file and save it to the target location
     *
     * @param event mouse click event
     */
    @FXML
    void generateButtonAction(ActionEvent event) {
       new Thread(new Runnable() {
           @Override
           public void run() {
               CefType cef = CefModifyUtils.getCef(getCefFile);
               Configuration config = new Configuration();
               config.setClassForTemplateLoading(TestBenchStageController.class, "");
               config.setAPIBuiltinEnabled(true);

               Template template = null;
               FileWriter out = null;
               try {
                   template = config.getTemplate("cefToSystemcTemplate.ftl");
                   //File file = new File("test.out");
                   String prefixPath = getClass().getResource("../").getFile();
                   File templateFile = new File(prefixPath, "/cefToSystemcTemplate.ftl");
                   //  template = config.getTemplate(templateFile.getAbsolutePath());
                   out = new FileWriter(getTestBenchFile);
                   template.process(cef, out);
               } catch (IOException | TemplateException e1) {
                   e1.printStackTrace();
               }
               Platform.runLater(new Runnable() {
                   @Override
                   public void run() {
                       statusLabel.setText("generate file success!");
                       System.out.println("finish !");
                   }
               });
           }
       }).start();

    }

    /**
     * Initialize the control
     * @param location location
     * @param resources resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusLabel.setText("");
        cefFile.setEditable(false);
        testBenchFile.setEditable(false);
        mainControllerStage = MainController.getStage();
        cefFile.setOnMouseClicked(event -> {
            getCefFile = Utils.openFileChooser("open", testBenchStage);
            cefFile.setText(getCefFile.getAbsolutePath());
            System.out.println(getCefFile.getAbsolutePath());
        });

        testBenchFile.setOnMouseClicked(event -> {
            getTestBenchFile = Utils.openFileChooser("save", testBenchStage);
            testBenchFile.setText(getTestBenchFile.getAbsolutePath());
            System.out.println(getTestBenchFile.getAbsolutePath());
        });

    }

    public void setStage(Stage testBenchStage) {
        this.testBenchStage = testBenchStage;
    }
}
