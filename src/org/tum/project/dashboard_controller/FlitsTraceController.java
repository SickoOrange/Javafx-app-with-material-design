package org.tum.project.dashboard_controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
    private JFXComboBox<?> cb_flitsid;

    @FXML
    private JFXComboBox<String> cb_flowid;


    @FXML
    private Label lb_flowid;

    //integer: flow id, FlitsSummary: summary for this flow id
    private HashMap<Integer, FlitsSummary> flitsSummaries = new HashMap<>();

    //integer: flow id, StringBuffer summary visualization string to this flow id
    private HashMap<Integer, StringBuffer> traceDetailsMap = new HashMap<>();
    private StringBuffer traceDetails;
    private StringBuffer tail;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FlitTraceService flitTraceService = (FlitTraceService) DashBoardController.getDataServiceInstance(FlitTraceService.class.getName());
        flitTraceService.setCallback(this);

        //add listener to the flow id combo box
        cb_flowid.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String id = newValue.split(":")[1];
                StringBuffer buffer = traceDetailsMap.get(Integer.valueOf(id));
                ta_flitsDetails.setText(buffer.toString());
            }
        });


    }

    @Override
    public void updateFlitsTraceData(HashMap<Integer, List<HashMap<Integer, List<FlitsInfo>>>> flow_id_to_flits_map) {

        //if  map not empty, then clear this map
        flitsSummaries.clear();
        traceDetailsMap.clear();

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

            traceDetails = new StringBuffer();

            //we need a sequence flag to record the first flit
            //from first flit, we can calculate transmission duration, start position and end position
            int sequenceControl = 0;
            //******************************************
            for (HashMap<Integer, List<FlitsInfo>> flitMap : flitsList) {
                sequenceControl++;
                tail = new StringBuffer();
                int header = 0;
                for (Map.Entry<Integer, List<FlitsInfo>> flitInfo : flitMap.entrySet()) {
                    header = flitInfo.getKey();


                    List<FlitsInfo> infos = flitInfo.getValue();
                    flitsSummary.setFlitsIDList(header, infos);
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
    void testAction(ActionEvent event) {

    }


    @FXML
    void startToTraceAction(ActionEvent event) {
    }
}
