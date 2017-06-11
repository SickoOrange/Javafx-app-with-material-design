package org.tum.project.dashboard_controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.sun.org.apache.bcel.internal.generic.LADD;
import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.tum.project.dataservice.*;
import org.tum.project.login_controller.MaterialLoginController;

import javax.xml.soap.Text;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * dashboard for simulation and analytics
 * Created by heylbly on 17-6-2.
 */
public class DashBoardController implements Initializable {


    private Node simulationPane;


    @FXML
    private StackPane sp_root;
    @FXML
    private AnchorPane holderPane;
    private AnchorPane flowLatencyPane;
    private static HashMap<String, Object> serviceInstanceMap;
    private AnchorPane flowPacketDetailsPane;
    private AnchorPane fifoSizeDetailsPane;
    private AnchorPane flitsTraceDetailsPane;
    private AnchorPane cefEditorPane;
    private JFXDialog dialog;


    @FXML
    private JFXButton btn_cefEditor;

    @FXML
    private JFXButton btn_Simulation;

    @FXML
    private JFXButton btn_fifosize;

    @FXML
    private JFXButton btn_flitsTrace;

    @FXML
    private JFXButton btn_flowLatency;

    @FXML
    private JFXButton btn_flowPacketLatency;


    /**
     * initialize the content when loading this node
     *
     * @param location  location
     * @param resources resources
     */
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createDataService();
        createCenterNode();
        setCenterNode(simulationPane);

    }

    public static HashMap<String, Object> getServiceInstanceMap() {
        return serviceInstanceMap;
    }

    /**
     * create service instance for the data analysis
     */
    private void createDataService() {
        FlowLatencyService flowLatencyService = new FlowLatencyService();
        FlowPacketLatencyService flowPacketLatencyService = new FlowPacketLatencyService();
        FifoSizeService fifoSizeService = new FifoSizeService();
        FlitTraceService flitTraceService = new FlitTraceService();
        TagLabelController tagLabelController = new TagLabelController();
        TraceJframe traceJframe = new TraceJframe();
        CefVisualizationService cefVisualizationService = new CefVisualizationService();
        CefEditorController cefEditorController = new CefEditorController();
        serviceInstanceMap = new HashMap<>();
        serviceInstanceMap.put(FlowLatencyService.class.getName(), flowLatencyService);
        serviceInstanceMap.put(FlowPacketLatencyService.class.getName(), flowPacketLatencyService);
        serviceInstanceMap.put(FifoSizeService.class.getName(), fifoSizeService);
        serviceInstanceMap.put(FlitTraceService.class.getName(), flitTraceService);
        serviceInstanceMap.put(TagLabelController.class.getName(), tagLabelController);
        serviceInstanceMap.put(TraceJframe.class.getName(), traceJframe);
        serviceInstanceMap.put(CefVisualizationService.class.getName(), cefVisualizationService);
        serviceInstanceMap.put(CefEditorController.class.getName(), cefEditorController);
        serviceInstanceMap.put(this.getClass().getName(), this);
    }

    public static Object getDataServiceInstance(String key) {
        return serviceInstanceMap.get(key);
    }

    /**
     * create different node, that need to be loaded into the right pane
     */
    private void createCenterNode() {
        try {
            simulationPane = FXMLLoader.load(getClass().getResource("../dashboard_controller/Simulation.fxml"));
            flowLatencyPane = FXMLLoader.load(getClass().getResource("../dashboard_controller/FlowLatency.fxml"));
            flowPacketDetailsPane = FXMLLoader.load(getClass().getResource("../dashboard_controller/FlowLatencyForPacket.fxml"));
            fifoSizeDetailsPane = FXMLLoader.load(getClass().getResource("../dashboard_controller/FifoSizeDetails.fxml"));
            flitsTraceDetailsPane = FXMLLoader.load(getClass().getResource("../dashboard_controller/FlitsTrace.fxml"));
            cefEditorPane = FXMLLoader.load(getClass().getResource("../dashboard_controller/CefEditor.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openSimulationPane(ActionEvent event) {
        btn_Simulation.setTextFill(Color.RED);
        System.out.println( );
        javafx.scene.text.Text graphic = (javafx.scene.text.Text) btn_Simulation.getGraphic();
        graphic.setFill(Color.RED);

        setCenterNode(simulationPane);
    }

    @FXML
    public void openFlowLatencyPane(ActionEvent actionEvent) {
        setCenterNode(flowLatencyPane);

    }

    @FXML
    public void openFlowPacketLatencyPane(ActionEvent actionEvent) {
        setCenterNode(flowPacketDetailsPane);
    }

    @FXML
    public void openFlowSizePane(ActionEvent actionEvent) {
        setCenterNode(fifoSizeDetailsPane);


    }

    @FXML
    public void openFlitsTrance(ActionEvent actionEvent) {
        setCenterNode(flitsTraceDetailsPane);

    }


    /**
     * open the cef editor pane
     *
     * @param actionEvent
     */
    public void openCefEditor(ActionEvent actionEvent) {
        setCenterNode(cefEditorPane);
    }

    /**
     * Appends the specified element to the end of this pane list
     *
     * @param element element, that need to be specified to the panelist
     */
    private void setCenterNode(Node element) {
        holderPane.getChildren().clear();
        holderPane.getChildren().add(element);
    }


    public static void putDataServiceInstance(String name, CefEditorController cefEditorController) {
    }

    @FXML
    void showInfoAction(javafx.scene.input.MouseEvent event) {
        // TODO: 17-6-11 show information
        System.out.println("information");

        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label("Information about this Application"));
        JFXButton btn_dialog_ok = new JFXButton("ok");
        btn_dialog_ok.setOnAction(event1 -> {
            dialog.close();
        });
        layout.setActions(btn_dialog_ok);

        Label label_content = new Label("Fuck U");
        layout.setBody(label_content);
        dialog = new JFXDialog(sp_root, layout, JFXDialog.DialogTransition.CENTER, true);
        dialog.show();

    }

    @FXML
    void showHomeAction(javafx.scene.input.MouseEvent event) throws IOException {
        AnchorPane node = FXMLLoader.load(getClass().getResource("../login_controller/MaterialLogin.fxml"));
        Stage mainStage = MaterialLoginController.getMainStage();
        mainStage.setScene(new Scene(node));
    }

    @FXML
    void showExitAction(javafx.scene.input.MouseEvent event) {
        MaterialLoginController.getMainStage().close();
    }


}
