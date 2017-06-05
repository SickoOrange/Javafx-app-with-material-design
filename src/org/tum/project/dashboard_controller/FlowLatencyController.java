package org.tum.project.dashboard_controller;

import com.jfoenix.controls.JFXTextArea;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;
import javafx.util.Duration;
import org.tum.project.callback.DataUpdateCallbackAdapter;
import org.tum.project.dataservice.FlowLatencyService;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * class for handle the flow latency pane
 * include widget layout and
 * Created by heylbly on 17-6-5.
 */
public class FlowLatencyController extends DataUpdateCallbackAdapter implements Initializable {
    @FXML
    private BarChart<String, Number> chart_latency;

    @FXML
    private BarChart<String, Number> chart_package;

    @FXML
    private TextArea tf_flowResults;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tf_flowResults.setEditable(false);
        FlowLatencyService instance = (FlowLatencyService) DashBoardController.getDataServiceInstance(FlowLatencyService.class.getName());
        instance.setCallback(this);
        initChart();
    }

    private void initChart() {
        chart_latency.getXAxis().setLabel("Flow Index");
        chart_latency.getYAxis().setLabel("Latency (ns)");
        chart_latency.setTitle("Flow Latency analyze chart");
        chart_latency.setPadding(new Insets(5, 5, 5, 5));

        chart_package.getXAxis().setLabel("Flow Index");
        chart_package.getYAxis().setLabel("Latency (ns)");
        chart_package.setTitle("Flow Package analyze chart");
        chart_package.setPadding(new Insets(5, 5, 5, 5));

    }

    @Override
    public void updateFLowLatency(HashMap<Long, String> flowChartDataMap, StringBuffer analyzeResult) {
        tf_flowResults.clear();
        tf_flowResults.setText(analyzeResult.toString());


        chart_latency.getData().clear();
        chart_package.getData().clear();
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("latency");

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("total_pkt");
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("total_pkt");

        if (flowChartDataMap.size() > 1) {
            for (Map.Entry<Long, String> entry : flowChartDataMap.entrySet()) {
                String flow_id = String.valueOf(entry.getKey());
                String metaData = entry.getValue();
                String[] split = metaData.split("_");
                System.out.println(split[0]);
                Float latency = Float.valueOf(split[0]);
                int total_pkt = Integer.valueOf(split[1]);
                int success_pkt = Integer.valueOf(split[2]);

                series1.getData().add(new XYChart.Data(flow_id, latency));

                series2.getData().add(new XYChart.Data(flow_id, total_pkt));
                series3.getData().add(new XYChart.Data(flow_id, success_pkt));
            }

            chart_latency.getData().add(series1);

            chart_package.getData().add(series2);
            chart_package.getData().add(series3);


            chart_latency.setBarGap(1.0);

            chart_package.setBarGap(1.0);

            System.out.println("flow latency pane finish");

        }

    }


    /**
     * show the latency chart
     *
     * @param actionEvent
     */
    public void showLatencyChartAction(ActionEvent actionEvent) {
        chart_package.setVisible(false);
        chart_latency.setVisible(true);

    }

    /**
     * show the package chart
     *
     * @param actionEvent
     */
    public void showPackageChartAction(ActionEvent actionEvent) {
        chart_package.setVisible(true);
        chart_latency.setVisible(false);

    }
}
