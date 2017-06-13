package org.tum.project.dashboard_controller;

import com.mxgraph.swing.mxGraphComponent;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.tum.project.dataservice.CefVisualizationService;
import org.tum.project.login_controller.MenusHolderController;
import org.tum.project.thread.TaskExecutorPool;
import org.tum.project.utils.Utils;

import javax.swing.*;
import java.io.File;
import java.net.URL;
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
    private StackPane sp_editor;


    private CefVisualizationService cefVisualizationService;
    private File openFilePath;

    @FXML
    private Text t_loadingLabel;

    @FXML
    private Pane addPropertiesContainer;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DashBoardController.getServiceInstanceMap().put(this.getClass().getName(), this);
    }


    /**
     * open the cef ecore xml file
     *
     * @param event
     */
    @FXML
    void openAction(ActionEvent event) {
        t_loadingLabel.setVisible(true);
        openFilePath = Utils.openFileChooser("open", MenusHolderController.getDashBoardStage());
        String[] split = new String[1];
        if (openFilePath != null) {
            split = openFilePath.getAbsolutePath().split("/");
        }
        t_openFile.setText("Open file: " + split[split.length - 1]);

        TaskExecutorPool.getExecutor().execute(() -> {
            //visualize the cef ecore file
            cefVisualizationService = (CefVisualizationService) DashBoardController.getDataServiceInstance(CefVisualizationService.class.getName());
            mxGraphComponent mxGraphComponent = cefVisualizationService.startVisualization(openFilePath.getAbsolutePath(), sp_editor);
            Platform.runLater(() -> displayContent(mxGraphComponent));
        });

    }

    /**
     * display the graph of the ecore in the javafx application
     *
     * @param mxGraphComponent
     */
    private void displayContent(mxGraphComponent mxGraphComponent) {
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

        Object[] objects = cefVisualizationService.saveFile();

        String fileName = (String) objects[0];
        String[] split = new String[1];
        if (fileName != null) {
            split = fileName.split("/");
        }
        t_savePath.setText("File save: " + split[split.length - 1]);

        displayContent((mxGraphComponent) objects[1]);
    }

    /**
     * refresh the graph after edit
     *
     * @param event
     */
    @FXML
    void refreshAction(ActionEvent event) {
        TaskExecutorPool.getExecutor().execute(() -> {
            mxGraphComponent mxGraphComponent = cefVisualizationService.refresh();
            Platform.runLater(() -> displayContent(mxGraphComponent));
        });
    }

    public static void main(String[] args) {
        String fe = "/home/heylbly/Desktop/multimedia_4x4_routed.xml";
        String[] split = fe.split("/");
        System.out.println(split[4]);
    }


    public Pane getAddPropertiesContainer() {
        return addPropertiesContainer;
    }
}
