package org.tum.project.dashboard_controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.tum.project.bean.PacketTimingInfo;
import org.tum.project.callback.DataUpdateCallbackAdapter;
import org.tum.project.dataservice.FlowPacketLatencyService;

import javax.print.DocFlavor;
import java.net.URL;
import java.util.*;

/**
 * class for handle the flow packet details pane
 * include widget layout and
 * Created by heylbly on 17-6-6.
 */
public class FlowLatencyForPacketController extends DataUpdateCallbackAdapter implements Initializable {

    @FXML
    private LineChart<Number, Number> chart_lineChart;

    @FXML
    private JFXTextArea ta_details;

    @FXML
    private JFXComboBox<String> cb_selectID;
    private List<List<PacketTimingInfo>> lists;

    @FXML
    private NumberAxis xAxis;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FlowPacketLatencyService flowPacketLatencyService = (FlowPacketLatencyService) DashBoardController.getDataServiceInstance(FlowPacketLatencyService.class.getName());
        flowPacketLatencyService.setCallback(this);
        /**
         * add the listener to the select item.
         */
        cb_selectID.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int index = newValue.intValue();
            System.out.println(index);
            if (lists != null) {
                List<PacketTimingInfo> packetTimingInfos = lists.get(index);
                popDataToChart(packetTimingInfos);
            }
        });

        ta_details.setEditable(false);
    }

    private void popDataToChart(List<PacketTimingInfo> packetTimingInfos) {
        chart_lineChart.getData().clear();
        ta_details.clear();
        XYChart.Series series = new XYChart.Series();
        series.setName(cb_selectID.getSelectionModel().getSelectedItem());
        StringBuffer buffer = new StringBuffer();
        int lower = packetTimingInfos.get(0).getPacket_id();
        int upper = packetTimingInfos.get(0).getPacket_id();
        for (PacketTimingInfo info : packetTimingInfos) {
            int packet_id = info.getPacket_id();
            if (info.getPacket_id() <= lower) {
                lower = info.getPacket_id();
            }
            if (info.getPacket_id() >= upper) {
                upper = info.getPacket_id();
            }
            double latency = info.getConsume_time() - info.getProduce_time();
            buffer.append("Packet Id: " + info.getPacket_id() + " Packet Latency: " + latency + " ns");
            buffer.append("\n");
            series.getData().add(new XYChart.Data<>(packet_id, latency));
        }
        System.out.println(lower + " " + upper);
        ta_details.setText(buffer.toString());


        xAxis.setLowerBound(lower);
        xAxis.setUpperBound(upper);
        xAxis.setAutoRanging(false);
        chart_lineChart.getXAxis().setLabel("packet id");
        chart_lineChart.getYAxis().setLabel("packet latency");
        chart_lineChart.setTitle("Flow Packet Analysis");
        chart_lineChart.getData().add(series);

    }

    @Override
    public void updateFlowPacketLatency(HashMap<Integer, List<PacketTimingInfo>> flow_id_to_packetInfo_map) {
        lists = new ArrayList<>();
        ObservableList<String> list = FXCollections.observableArrayList();
        if (flow_id_to_packetInfo_map != null) {
            if (flow_id_to_packetInfo_map.size() != 0) {
                for (Map.Entry<Integer, List<PacketTimingInfo>> entry : flow_id_to_packetInfo_map.entrySet()) {
                    Integer flowId = entry.getKey();
                    lists.add(entry.getValue());
                    list.add("flow id: " + flowId + " successful packet: " + entry.getValue().size());
                }
                cb_selectID.getItems().addAll(list);
            }
        }
    }
}
