package org.tum.project.dashboard_controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.tum.project.bean.FifoInfo;
import org.tum.project.callback.DataUpdateCallbackAdapter;
import org.tum.project.dataservice.FifoSizeService;

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
    private LineChart<?, ?> chart_fifoSize;


    @FXML
    private VBox vb_combo;

    @FXML
    private HBox hb_tag;

    @FXML
    private JFXComboBox<String> cb_selectElement;
    private HashMap<String, List<VBox>> contentMap;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FifoSizeService fifoSizeService = (FifoSizeService) DashBoardController.getDataServiceInstance(FifoSizeService.class.getName());
        fifoSizeService.setCallback(this);
    }


    @Override
    public void updateFifoSizeAnalyze(HashMap<String, List<FifoInfo>> fifoMap, TreeMap<String, List<String>> processData) {
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
//                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
//                    updateMarkFlag(checkBox, pane);
//                    updateLineChart(checkBox, fifo, fifoMap);
//                    fifoMapClone = fifoMap;
//
//                    if (checkBox.isSelected()) {
//                        System.out.println("read add " + fifo + ".c");
//                        recoverElementMarkForContent2.add(fifo + ".c");
//                    } else {
//                        System.out.println("read remove " + fifo + ".c");
//                        if (recoverElementMarkForContent2.contains(fifo + ".c")) {
//                            recoverElementMarkForContent2.remove(fifo + ".c");
//                        }
//                    }
//                });

            }
            contentMap.put(entry.getKey(), contentBoxList);
        }
    }

    public void updateAction(ActionEvent actionEvent) {
        HBox box = new HBox(5);
        box.getChildren().add(new Button("fu ck "));


    }

    public void clearAction(ActionEvent actionEvent) {


    }
}
