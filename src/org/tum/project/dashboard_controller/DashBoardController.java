package org.tum.project.dashboard_controller;

import com.jfoenix.controls.JFXButton;
import com.sun.deploy.trace.Trace;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.tum.project.dataservice.*;

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
    private AnchorPane holderPane;
    private AnchorPane flowLatencyPane;
    private static HashMap<String, Object> serviceInstanceMap;
    private AnchorPane flowPacketDetailsPane;
    private AnchorPane fifoSizeDetailsPane;
    private AnchorPane flitsTraceDetailsPane;


    /**
     * initialize the content when loading this node
     *
     * @param location  location
     * @param resources resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createDataService();
        createCenterNode();
        setCenterNode(simulationPane);
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
        serviceInstanceMap = new HashMap<>();
        serviceInstanceMap.put(FlowLatencyService.class.getName(), flowLatencyService);
        serviceInstanceMap.put(FlowPacketLatencyService.class.getName(), flowPacketLatencyService);
        serviceInstanceMap.put(FifoSizeService.class.getName(), fifoSizeService);
        serviceInstanceMap.put(FlitTraceService.class.getName(), flitTraceService);
        serviceInstanceMap.put(TagLabelController.class.getName(), tagLabelController);
        serviceInstanceMap.put(TraceJframe.class.getName(), traceJframe);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openSimulationPane(ActionEvent event) {
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
     * Appends the specified element to the end of this pane list
     *
     * @param element element, that need to be specified to the panelist
     */
    private void setCenterNode(Node element) {
        holderPane.getChildren().clear();
        holderPane.getChildren().add(element);
    }


}
