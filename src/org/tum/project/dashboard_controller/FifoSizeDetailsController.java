package org.tum.project.dashboard_controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.tum.project.bean.FifoInfo;
import org.tum.project.callback.DataUpdateCallbackAdapter;
import org.tum.project.dataservice.FifoSizeService;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * class for handle the fifo size analysis
 * include widget layout
 * Created by heylbly on 17-6-6.
 */
public class FifoSizeDetailsController extends DataUpdateCallbackAdapter implements Initializable {

    @FXML
    private JFXTextField et_start;

    @FXML
    private JFXTextField et_end;

    @FXML
    private LineChart<Number, Number> chart_fifoSize;

    @FXML
    private NumberAxis na_xAxis;


    @FXML
    private VBox vb_combo;

    @FXML
    private HBox hb_tag;

    @FXML
    private JFXComboBox<String> cb_selectElement;
    private HashMap<String, List<VBox>> contentMap;

    private List<String> fifoChartData = new ArrayList<>();
    private HashMap<String, List<FifoInfo>> fifoMapCopy;


    //container for collect all tag label counter
    HashMap<String, Label> counterLabelContainer = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FifoSizeService fifoSizeService = (FifoSizeService) DashBoardController.getDataServiceInstance(FifoSizeService.class.getName());
        fifoSizeService.setCallback(this);

    }


    @Override
    public void updateFifoSizeAnalyze(HashMap<String, List<FifoInfo>> fifoMap, TreeMap<String, List<String>> processData) {

        //initialize
        fifoChartData.clear();
        fifoMapCopy = fifoMap;

        cb_selectElement.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {


            //click the item of the combo, then display the content of this processing element in the right pane
            List<VBox> boxList = contentMap.get(newValue.split(":")[1]);
            if (vb_combo.getChildren().size() != 0) {
                vb_combo.getChildren().clear();
            }
            if (boxList.size() != 0) {
                for (VBox box : boxList) {
                    vb_combo.getChildren().add(box);
                }
            }

        });

        contentMap = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : processData.entrySet()) {
            System.out.println(entry.getKey() + "  ::: " + entry.getValue());
            //add the process element name to the combo box
            cb_selectElement.getItems().add("Processing:" + entry.getKey());
            List<VBox> contentBoxList = new ArrayList<>();
            List<String> value = entry.getValue();
            for (String s : value) {

                VBox box = new VBox(5);
                box.setPadding(new Insets(5, 5, 5, 5));

                // for example: s="producer.dataFifo"
                String[] split = s.split("\\.");

                //for example: fifo="ASIC1.producer.dataFifo"
                String fifo = entry.getKey() + "." + s;

                //for example: text="from producer: "
                Text text = new Text("from " + split[0] + ":");
                text.setFont(new Font(13));

                //for example： checkbox="dataFifo"
                JFXCheckBox checkBox = new JFXCheckBox(split[1]);
                checkBox.setId(fifo);

                box.getChildren().add(text);
                box.getChildren().add(checkBox);
                contentBoxList.add(box);
                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    updateChart(fifoMap, fifo, checkBox);
                });

            }
            contentMap.put(entry.getKey(), contentBoxList);
        }
    }


    /**
     * set the data series to the line chart
     *
     * @param fifoMap
     * @param fifo
     * @param checkBox
     */
    private void updateChart(HashMap<String, List<FifoInfo>> fifoMap, String fifo, JFXCheckBox checkBox) {
        String LabelName = fifo.split("\\.")[0];

        //collect all the selected element in a new list, that will be displayed in the chart later
        if (checkBox.isSelected()) {
            //for example: fifo="ASIC1.producer.dataFifo"
            if (!fifoChartData.contains(fifo)) {
                System.out.println("add a chart element");
                fifoChartData.add(fifo);
                addTagLabel(LabelName);

                //update the counter label
                increaseLabelCounter(LabelName);
            }
        } else {
            if (fifoChartData.contains(fifo)) {
                System.out.println("remove a chart element");
                fifoChartData.remove(fifo);

                //update the counter label
                decreaseLabelCounter(LabelName);

                deleteTagLabel(LabelName, checkBox);
            }
        }


        //add the element to the chart
        //first clear all existing element in the chart
        chart_fifoSize.getData().clear();
        na_xAxis.setAutoRanging(true);
        if (fifoChartData.size() != 0) {
            for (String name : fifoChartData) {
                if (fifoMap.containsKey(name)) {
                    List<FifoInfo> fifoInfos = fifoMap.get(name);
                    XYChart.Series series = new XYChart.Series();
                    series.setName(name);
                    for (FifoInfo info : fifoInfos) {
                        series.getData().add(new XYChart.Data<>(info.getTimestamp(), info.getFifoSize()));
                    }
                    chart_fifoSize.getData().add(series);
                }
            }
        }


    }


    /**
     * decrease 1 to t he specific label counter
     *
     * @param labelName label name
     */
    private void decreaseLabelCounter(String labelName) {
        Label label = counterLabelContainer.get(labelName);
        int i = Integer.valueOf(label.getText());
        i--;
        label.setText(String.valueOf(i));
    }

    /**
     * add 1 to the specific label counter
     *
     * @param labelName label name
     */
    private void increaseLabelCounter(String labelName) {
        Label label = counterLabelContainer.get(labelName);
        int i = Integer.valueOf(label.getText());
        // label.setText(String.valueOf(i++));
        i++;
        label.setText(String.valueOf(i));
    }

    /**
     * add a tag label to the pane
     *
     * @param name label name, that need to be added
     */
    private void addTagLabel(String name) {
        //Check whether the label is repeated
        ObservableList<Node> childrens = hb_tag.getChildren();
        for (Node children : childrens) {
            if (children.getId().equals(name)) {
                System.out.println("labelBox is repeated");
                return;
            }
        }


        //add the tag label to the pane
        HBox box = null;
        try {
            //for example name="ASCI1"
            box = FXMLLoader.load(getClass().getResource("../dashboard_controller/TagLabel.fxml"));
            box.setId(name);
            Label label = (Label) box.getChildren().get(0);
            label.setText(name);
            Label counter = (Label) box.getChildren().get(1);

            counterLabelContainer.put(name, counter);
            hb_tag.getChildren().add(box);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * delete a tag label to the pane
     *
     * @param name     name, that need to be deleted
     * @param checkBox
     */
    private void deleteTagLabel(String name, JFXCheckBox checkBox) {
        int index = -1;
        ObservableList<Node> childrens = hb_tag.getChildren();
        for (Node children : childrens) {
            if (children.getId().equals(name)) {
                //record the index of the label, that need to be deleted
                index = childrens.indexOf(children);
                System.out.println("need to remove " + index);
            }
        }

        //To determine whether it can be deleted
        //Detects whether this tag group has other check box has been selected
        //If it is not deleted if not deleted
        List<VBox> boxList = contentMap.get(name);
        for (VBox box : boxList) {
            for (Node node : box.getChildren()) {
                if (node instanceof JFXCheckBox) {
                    JFXCheckBox existingBox = (JFXCheckBox) node;
                    if (existingBox.isSelected()) {
                        System.out.println("existing box is selected, delete cancel");
                        return;
                    }
                }
            }
        }


        if (index != -1) {
            System.out.println("remove this label " + index);
            childrens.remove(index);
        }

        counterLabelContainer.remove(name);


    }

    /**
     * update the chart to display the specific x axis time interval
     *
     * @param actionEvent
     */
    public void updateAction(ActionEvent actionEvent) {
        double start = Double.valueOf(et_start.getText());
        double end = Double.valueOf(et_end.getText());

        if (et_start.getText().equals("")) {
            return;
        }
        if (et_start.getText().equals("")) {
            return;
        }

        if (start >= end) {
            // FIXME: 17-6-6 replace with meterial dialog
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Look, a Warning Dialog");
            alert.setContentText("The start time must be less than the end time");
            alert.showAndWait();
            return;
        }

        chart_fifoSize.getData().clear();
        na_xAxis.setLowerBound(start);
        na_xAxis.setUpperBound(end);
        na_xAxis.setAutoRanging(false);
        if (fifoChartData.size() != 0) {
            for (String name : fifoChartData) {
                if (fifoMapCopy.containsKey(name)) {
                    List<FifoInfo> fifoInfos = fifoMapCopy.get(name);
                    XYChart.Series series = new XYChart.Series();
                    series.setName(name);
                    for (FifoInfo info : fifoInfos) {
                        double timestamp = info.getTimestamp();
                        if (timestamp >= start && timestamp < end) {
                            series.getData().add(new XYChart.Data<>(timestamp, info.getFifoSize()));
                        }
                    }
                    chart_fifoSize.getData().add(series);
                }
            }
        }

    }


    /**
     * clear all the element from that chart
     *
     * @param actionEvent
     */
    public void clearAction(ActionEvent actionEvent) {
        counterLabelContainer.clear();
        chart_fifoSize.getData().clear();
        et_start.setText("");
        et_end.setText("");

        for (Map.Entry<String, List<VBox>> entry : contentMap.entrySet()) {
            List<VBox> value = entry.getValue();
            for (VBox box : value) {
                for (Node node : box.getChildren()) {
                    if (node instanceof JFXCheckBox) {
                        JFXCheckBox checkBox = (JFXCheckBox) node;
                        if (checkBox.isSelected()) {
                            checkBox.setSelected(false);
                        }
                    }
                }
            }
        }
        //clear the tag label box
        hb_tag.getChildren().clear();
    }
}
