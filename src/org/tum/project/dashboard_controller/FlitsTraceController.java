package org.tum.project.dashboard_controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import org.omg.CORBA.INTERNAL;
import org.tum.project.bean.FlitsInfo;
import org.tum.project.bean.FlitsSummary;
import org.tum.project.callback.DataUpdateCallbackAdapter;
import org.tum.project.dataservice.FlitTraceService;

import java.net.URL;
import java.util.*;

/**
 * this class is used for flit visualization
 * Created by heylbly on 17-6-6.
 */
public class FlitsTraceController extends DataUpdateCallbackAdapter implements Initializable {
    @FXML
    private TextArea ta_flitsDetails;

    @FXML
    private JFXComboBox<String> cb_flitsid;

    @FXML
    private JFXComboBox<String> cb_flowid;


    @FXML
    private Text lb_secondTitle;

    @FXML
    private Label lb_topTitle;

    @FXML
    private Label lb_source;

    @FXML
    private Label lb_destination;

    @FXML
    private Text lb_success;

    @FXML
    private Text lb_failed;

    @FXML
    private Text lb_duration;

    @FXML
    private Text lb_rate;


    @FXML
    private Label lb_flowid;

    //integer: flow id, FlitsSummary: summary for this flow id
    private HashMap<Integer, FlitsSummary> flitsSummaries = new HashMap<>();

    //integer: flow id, StringBuffer summary visualization string to this flow id
    private HashMap<Integer, StringBuffer> traceDetailsMap = new HashMap<>();
    private StringBuffer traceDetails;
    private StringBuffer tail;
    private HashMap<Integer, List<FlitsInfo>> cloneMap = new HashMap<>();
    ;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FlitTraceService flitTraceService = (FlitTraceService) DashBoardController.getDataServiceInstance(FlitTraceService.class.getName());
        flitTraceService.setCallback(this);

        //add listener to the flow id combo box
        cb_flowid.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String id = newValue.split(":")[1];
            StringBuffer buffer = traceDetailsMap.get(Integer.valueOf(id));
            ta_flitsDetails.setText(buffer.toString());

            //display information in the pane
            FlitsSummary summary = flitsSummaries.get(Integer.valueOf(id));
            lb_topTitle.setText(String.valueOf(summary.getFlowid()));
            lb_source.setText(summary.getStartPoint());
            lb_destination.setText(summary.getEndPoint());
            lb_duration.setText(String.valueOf(summary.getEndTime() - summary.getStartTime()) + " ns");
            lb_success.setText(String.valueOf(summary.getSuccessFlitsNumber()));
            lb_failed.setText(String.valueOf(summary.getFailedFlitsNumber()));
            double rate = (double) summary.getSuccessFlitsNumber() / (summary.getSuccessFlitsNumber() + summary.getFailedFlitsNumber());
            int intRate = (int) (rate * 100);
            lb_rate.setText(String.valueOf(intRate) + "%");


            HashMap<Integer, List<HashMap<Integer, List<FlitsInfo>>>> allFlitsIDList = summary.getAllFlitsIDList();
            List<HashMap<Integer, List<FlitsInfo>>> list = allFlitsIDList.get(Integer.valueOf(id));


            for (HashMap<Integer, List<FlitsInfo>> map : list) {
                int flitID = 0;
                List<FlitsInfo> flitsInfos = null;
                for (Map.Entry<Integer, List<FlitsInfo>> entry : map.entrySet()) {
                    flitID = entry.getKey();
                    flitsInfos = entry.getValue();
                }
                cloneMap.put(flitID, flitsInfos);
                cb_flitsid.getItems().add("flit id:" + String.valueOf(flitID));
            }
        });


        cb_flitsid.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String id = newValue.split(":")[1];
            lb_secondTitle.setText(String.valueOf(id));
            List<FlitsInfo> infos = cloneMap.get(Integer.valueOf(id));
            //trace information
            for (FlitsInfo info : infos) {
                System.out.println("\n" + info);
            }
        });


    }


    @Override
    public void updateFlitsTraceData(HashMap<Integer, List<HashMap<Integer, List<FlitsInfo>>>> flow_id_to_flits_map) {

        //if  map not empty, then clear this map
        flitsSummaries.clear();
        traceDetailsMap.clear();
        cloneMap.clear();

        for (Map.Entry<Integer, List<HashMap<Integer, List<FlitsInfo>>>> entry : flow_id_to_flits_map.entrySet()) {
            FlitsSummary flitsSummary = new FlitsSummary();
            int flowid = entry.getKey();
            flitsSummary.setFlowid(flowid);

            //fill the flow id combo box with flow id
            cb_flowid.getItems().add("flow id:" + flowid);


            //indicate how many flits in the flow
            //integer:flow id list: flits list
            List<HashMap<Integer, List<FlitsInfo>>> flitsList = entry.getValue();

            flitsSummary.setFlitsNumber(flitsList.size());
            flitsSummary.setFlitsIDList(flowid, flitsList);

            traceDetails = new StringBuffer();

            //we need a sequence flag to record the first flit
            //from first flit, we can calculate transmission duration, start position and end position
            int sequenceControl = 0;
            //******************************************
            for (HashMap<Integer, List<FlitsInfo>> flitMap : flitsList) {
                sequenceControl++;
                tail = new StringBuffer();
                int header = 0;
                flitsSummary.setList(flowid, flitMap.entrySet());

                for (Map.Entry<Integer, List<FlitsInfo>> flitInfo : flitMap.entrySet()) {
                    header = flitInfo.getKey();


                    List<FlitsInfo> infos = flitInfo.getValue();
                    for (FlitsInfo info : infos) {

                        //we just need first flit, because first flit muss be translated successfully
                        //calculate transmission duration, start position and end position
                        if (sequenceControl == 1) {
                            if (infos.indexOf(info) == 0) {
                                //start position with start time
                                flitsSummary.setStartTime(info.getFlitsTime());
                                flitsSummary.setStartPoint(info.getFlitPosition().split("\\.")[0]);
                            }
                            if (infos.indexOf(info) == infos.size() - 1) {
                                //end position with end time
                                flitsSummary.setEndTime(info.getFlitsTime());
                                flitsSummary.setEndPoint(info.getFlitPosition().split("\\.")[0]);
                            }
                            double duration = flitsSummary.getEndTime() - flitsSummary.getStartTime();
                            flitsSummary.setRoutingDuration(duration);
                        }


                        double time = info.getFlitsTime();
                        String position = info.getFlitPosition();
                        String[] split = position.split("\\.");
                        if (infos.indexOf(info) == infos.size() - 1) {

                            //calculate the successfully translate flit
                            if (!split[0].equals(flitsSummary.getEndPoint())) {
                                flitsSummary.increaseFailedFlitNumber();
                            } else {
                                flitsSummary.increaseSuccessFlitNumber();
                            }

                            tail.append(time + "ns, " + split[0]);

                        } else {
                            tail.append(time + "ns, " + split[0] + "--->");
                        }


                    }
                    // append the information for flits: header-> flits id, tail->time+flit position at this time
                    traceDetails.append("Flit ID: " + header + "      Path");
                    traceDetails.append(": ");
                    traceDetails.append(tail + "\n");
                }
            }
            //******************************************

            //System.out.println("summary: " + flitsSummary);
            traceDetailsMap.put(flowid, traceDetails);
            flitsSummaries.put(flowid, flitsSummary);

        }

    }


    @FXML
    void startToTraceAction(ActionEvent event) {
    }
}
