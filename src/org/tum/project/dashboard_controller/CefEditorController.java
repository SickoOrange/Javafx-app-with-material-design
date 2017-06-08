package org.tum.project.dashboard_controller;

import com.jfoenix.controls.JFXButton;
import com.mxgraph.swing.mxGraphComponent;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.tum.project.dataservice.CefVisualizationService;
import org.tum.project.login_controller.MenusHolderController;
import org.tum.project.utils.Utils;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.nio.file.FileSystem;
import java.util.ResourceBundle;

/**
 * cef editor controller
 * set the layout for cef editor pane
 * Created by heylbly on 17-6-8.
 */
public class CefEditorController implements Initializable {

    private String separator = File.separator;
    @FXML
    private Text t_openFile;

    @FXML
    private Text t_savePath;
    @FXML
    private AnchorPane fabPane;

    @FXML
    private StackPane sp_editor;

    @FXML
    private JFXButton btn_refresh;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StackPane.setAlignment(btn_refresh, Pos.TOP_RIGHT);
    }


    /**
     * open the cef ecore xml file
     *
     * @param event
     */
    @FXML
    void openAction(ActionEvent event) {
        File file = Utils.openFileChooser("open", MenusHolderController.getDashBoardStage());
        String[] split = new String[1];
        if (file != null) {
            split = file.getAbsolutePath().split("/");
        }
        t_openFile.setText("Open file: " + split[split.length - 1]);

        //visualize the cef ecore file
        CefVisualizationService cefVisualizationService = (CefVisualizationService) DashBoardController.getDataServiceInstance(CefVisualizationService.class.getName());
        mxGraphComponent mxGraphComponent = cefVisualizationService.startVisualization(file.getAbsolutePath());

        //embed the Swing Component to the javafx Application
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(mxGraphComponent);
            Platform.runLater(() -> {
                if (!sp_editor.getChildren().isEmpty()) {
                    sp_editor.getChildren().clear();
                }
                sp_editor.getChildren().add(swingNode);
            });
        });
    }

    /**
     * select the save path to save the modified cef file
     *
     * @param event
     */
    @FXML
    void saveAction(ActionEvent event) {
        File file = Utils.openFileChooser("save", MenusHolderController.getDashBoardStage());
        String[] split = new String[1];
        if (file != null) {
            split = file.getAbsolutePath().split("/");
        }
        t_savePath.setText("File save: " + split[split.length - 1]);
    }

    public static void main(String[] args) {
        String fe = "/home/heylbly/Desktop/multimedia_4x4_routed.xml";
        String[] split = fe.split("/");
        System.out.println(split[4]);
    }


}
