package org.tum.project.controller;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.tum.project.bean.FifoInfo;
import org.tum.project.bean.FlitsInfo;
import org.tum.project.dataservice.FifoSizeService;
import org.tum.project.dataservice.FlowLatencyService;
import org.tum.project.bean.PacketTimingInfo;
import org.tum.project.callback.DataUpdateCallback;
import org.tum.project.constant.ConstantValue;
import org.tum.project.dataservice.FlitTraceService;
import org.tum.project.dataservice.FlowPacketLatencyService;

import java.util.*;


public class MainController implements DataUpdateCallback {

    private HashMap<String, List<String>> content;
    private BorderPane root;
    private BarChart<String, Number> flowLatencyChart1;
    private BarChart<String, Number> flowLatencyChart2;
    private EventHandler<ActionEvent> buttonEvent;
    private int startFlag = 0;

    private List<String> dataModelName = new ArrayList<>();
    private String currentDataBase;
    private final FlowLatencyService flowLatencyService;
    private Label flowBottomLabel;
    private final FifoSizeService fifoSizeService;
    private NumberAxis lxAxis;
    private NumberAxis lyAxis;
    private LineChart<Number, Number> lineChart;
    private List<String> fifoChartData = new ArrayList<>();

    //store the id of the element, that need to be recover for content 2
    private HashSet<String> recoverElementMarkForContent2 = new HashSet<>();


    //definition the parent container Accordion for the title Pane in the fifo size Analyze
    private Accordion parentAccordionForFifoSize = new Accordion();



    private final Label label_1 = new Label("start:");
    private final Label label_2 = new Label("end:");
    private final TextField startTime = new TextField();
    private final TextField endTime = new TextField();
    private HashMap<String, List<FifoInfo>> fifoMapClone;
    private final FlowPacketLatencyService flowPacketLatencyService;
    private NumberAxis xAxis_3;
    private NumberAxis yAxis_3;
    private LineChart<Number, Number> thirdLineChart;
    private TextArea packetLatencyInformation = new TextArea();
    private int recoverChoiceButtonIndex = -1;
    private String recoverTextAreaInformation = "";
    private FlitTraceService flitTraceService;

    MainController(HashMap<String, List<String>> content) {

        this.content = content;
        buttonEvent = new ButtonEvents();
        flowLatencyService = new FlowLatencyService();
        fifoSizeService = new FifoSizeService();
        fifoSizeService.setCallback(this);
        flowPacketLatencyService = new FlowPacketLatencyService();
        flowPacketLatencyService.setCallback(this);

        flitTraceService = new FlitTraceService();
        flitTraceService.setCallback(this);
        initContent1();
        initContent2();
        initContent3();
        initContent4();
    }

    private void initContent4() {

    }

    private void initContent3() {
        xAxis_3 = new NumberAxis();
        yAxis_3 = new NumberAxis();
        xAxis_3.setLabel("packet id");
        yAxis_3.setLabel("packet latency");
        thirdLineChart = new LineChart<Number, Number>(xAxis_3, yAxis_3);
        thirdLineChart.setTitle("Flow Packet Latency Analyze");
    }

    private void initContent2() {
        lxAxis = new NumberAxis();
        lyAxis = new NumberAxis();
        lxAxis.setLabel("time (ns)");
        lyAxis.setLabel("fifo size");
        lineChart = new LineChart<Number, Number>(lxAxis, lyAxis);
        lineChart.setTitle("Fifo Analyze");

    }

    private void initContent1() {
        CategoryAxis mxAxis1 = new CategoryAxis();
        NumberAxis myAxis1 = new NumberAxis();
        mxAxis1.setLabel("Flow Index");
        myAxis1.setLabel("Latency (ns)");
        flowLatencyChart1 = new BarChart<>(mxAxis1, myAxis1);
        flowLatencyChart1.setTitle("FLow Latency analyze chart");
        flowLatencyChart1.setPadding(new Insets(5, 5, 5, 5));

        CategoryAxis mxAxis2 = new CategoryAxis();
        NumberAxis myAxis2 = new NumberAxis();
        mxAxis2.setLabel("Flow Index");
        myAxis2.setLabel("total and success package");
        flowLatencyChart2 = new BarChart<>(mxAxis2, myAxis2);
        flowLatencyChart2.setTitle("FLow Latency analyze chart");
        flowLatencyChart2.setPadding(new Insets(5, 5, 5, 5));
    }

    public void setTop(BorderPane root) {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(15, 12, 15, 12));
        hBox.setSpacing(10);
        hBox.setStyle("-fx-background-color: #336699");
        Label topLabel = new Label("Technische Universität München");
        topLabel.setFont(new Font(35));
        topLabel.setTextFill(Color.BLACK);
        hBox.getChildren().add(topLabel);

        VBox vBox = new VBox();
        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);
        //  separator.setStyle(" -fx-background-color: #e79423; -fx-background-radius: 2;");
        vBox.getChildren().addAll(hBox, separator);

         root.setTop(vBox);
       // root.setBottom(vBox);
    }


    public void setLeft(BorderPane root) {

        Accordion accordion = new Accordion();
        if (content != null) {
            for (Map.Entry<String, List<String>> entry : content.entrySet()) {

                VBox box = new VBox(5);
                box.setPadding(new Insets(15, 12, 15, 12));
                List<String> tableList = entry.getValue();
                TitledPane node = new TitledPane();
                for (String name : tableList) {
                    CheckBox checkBox = new CheckBox(name);
                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {

                        if (checkBox.isSelected()) {
                            dataModelName.add(checkBox.getText());
                        } else {
                            dataModelName.remove(checkBox.getText());
                        }
                        updateMarkFlag(checkBox, node);

                    });
                    box.getChildren().add(checkBox);
                    box.getChildren().add(getSeparator(Orientation.HORIZONTAL));
                }

                node.setText(entry.getKey());
                node.setContent(box);
                node.expandedProperty().addListener((observable, oldValue, newValue) -> {
                    if (node.isExpanded()) {
                        currentDataBase = node.getText();
                    }
                });
                accordion.getPanes().add(node);
            }


        } else {
            //no database, need to create database then open this tool
            TitledPane pane = new TitledPane();
            pane.setText("No Database");
            accordion.getPanes().add(pane);

        }

        ScrollPane pane = new ScrollPane();
        pane.setContent(accordion);
        pane.setPannable(true);
        root.setLeft(pane);
    }

    private HBox getContentTop() {
        HBox hBox = new HBox(15);
        hBox.setPadding(new Insets(15, 12, 15, 12));
        for (int i = 0; i < 4; i++) {
            Button button = new Button();
            if (i == 0) {
                button.setText("Flow Analyze");
            } else if (i == 1) {
                button.setText("Fifo Analyze ");
            } else if (i == 2) {
                button.setText("Packet Analyze");
            } else {
                button.setText("Trace Flits");
            }


            button.setId(String.valueOf(i));
            button.setOnAction(buttonEvent);
            hBox.getChildren().add(button);
        }
        Button start = new Button("Start");
        start.setId("start");
        start.setOnAction(buttonEvent);
        hBox.getChildren().add(getSeparator(Orientation.VERTICAL));
        hBox.getChildren().add(start);

        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

    private ScrollPane getFirstContentCenter() {
        ScrollPane scrollPane = new ScrollPane();
        VBox vBox = new VBox(5);
        vBox.setPadding(new Insets(15, 12, 15, 12));
        flowLatencyChart1.prefWidthProperty().bind(vBox.widthProperty());

        flowBottomLabel = new Label();
        flowBottomLabel.setFont(new Font(20));
        flowBottomLabel.setTextFill(Color.BLACK);
        flowBottomLabel.setText(flowLatencyService.getRecoverInformation().toString());

        vBox.getChildren().addAll(flowLatencyChart1, flowLatencyChart2, flowBottomLabel);
        vBox.prefWidthProperty().bind(scrollPane.widthProperty());
        scrollPane.setContent(vBox);
        scrollPane.setPannable(true);
        return scrollPane;
    }

    private ScrollPane getFifoSizeAnalyzeContentCenter(HashMap<String, List<FifoInfo>> fifoMap, TreeMap<String, List<String>> processData) {
        ScrollPane parentScrollPane = new ScrollPane();
        VBox vBox = new VBox(8);
        vBox.setPadding(new Insets(15, 12, 15, 12));
        lineChart.prefWidthProperty().bind(vBox.widthProperty());
        List<TitledPane> list = new ArrayList<>();


        parentAccordionForFifoSize.getPanes().clear();

        for (Map.Entry<String, List<String>> entry : processData.entrySet()) {
            List<String> value = entry.getValue();
            VBox childBox = new VBox();
            childBox.setSpacing(5);
            childBox.setPadding(new Insets(15, 12, 15, 12));
            TitledPane pane = new TitledPane();
            pane.setId(entry.getKey());
            for (String s : value) {
                String[] split = s.split("\\.");
                String fifo = entry.getKey() + "." + s;
                Label label = new Label("from " + split[0] + ":");
                label.setFont(new Font(13));
                label.setTextFill(Color.BLACK);
                childBox.getChildren().add(label);
                CheckBox checkBox = new CheckBox(split[1]);
                checkBox.setId(fifo);
                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    updateMarkFlag(checkBox, pane);
                    updateLineChart(checkBox, fifo, fifoMap);
                    fifoMapClone = fifoMap;

                    if (checkBox.isSelected()) {
                        System.out.println("read add " + fifo + ".c");
                        recoverElementMarkForContent2.add(fifo + ".c");
                    } else {
                        System.out.println("read remove " + fifo + ".c");
                        if (recoverElementMarkForContent2.contains(fifo + ".c")) {
                            recoverElementMarkForContent2.remove(fifo + ".c");
                        }
                    }
                });
                childBox.getChildren().add(checkBox);
                childBox.getChildren().add(getSeparator(Orientation.HORIZONTAL));
            }
            pane.setText(entry.getKey());
            pane.setContent(childBox);
            list.add(pane);
            parentAccordionForFifoSize.getPanes().add(pane);
        }

        vBox.getChildren().add(lineChart);

        HBox hBox = new HBox(15);
        hBox.setPadding(new Insets(15, 12, 15, 12));
        label_1.setFont(new Font(18));
        label_1.setAlignment(Pos.CENTER);
        label_1.setTextFill(Color.BLACK);
        label_2.setFont(new Font(18));
        label_2.setAlignment(Pos.CENTER);
        label_2.setTextFill(Color.BLACK);
        Button update = new Button("update");
        update.setId("update");
        update.setOnAction(new ButtonEvents());
        Button clear = new Button("clear");
        clear.setId("clear");
        clear.setOnAction(new ButtonEvents());
        hBox.getChildren().addAll(label_1, startTime, label_2, endTime, getSeparator(Orientation.VERTICAL), update, clear);
        hBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(hBox);


        if (list.size() != 0) {
            for (TitledPane pane : list) {
                vBox.getChildren().add(pane);
            }
        }
        recoverElementMark(parentAccordionForFifoSize, recoverElementMarkForContent2);
        vBox.prefWidthProperty().bind(parentScrollPane.widthProperty());
        parentScrollPane.setPrefHeight(1000);
        parentScrollPane.setContent(vBox);
        parentScrollPane.setPannable(true);

        return parentScrollPane;
    }

    private Node getPacketAnalyzeContentCenter(HashMap<Integer, List<PacketTimingInfo>> flow_id_to_packetInfo_map) {

        ScrollPane scrollPane = new ScrollPane();
        VBox vBox = new VBox(5);
        vBox.setPadding(new Insets(15, 12, 15, 12));
        thirdLineChart.prefWidthProperty().bind(vBox.widthProperty());

        vBox.getChildren().add(thirdLineChart);

        vBox.getChildren().add(new Label("Please select the flow id"));

        List<List<PacketTimingInfo>> lists = new ArrayList<>();

        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.setTooltip(new Tooltip("please select the flow id "));
        ObservableList<Object> list = FXCollections.observableArrayList();
        if (flow_id_to_packetInfo_map != null) {
            if (flow_id_to_packetInfo_map.size() != 0) {
                for (Map.Entry<Integer, List<PacketTimingInfo>> entry : flow_id_to_packetInfo_map.entrySet()) {
                    Integer flowId = entry.getKey();
                    lists.add(entry.getValue());
                    list.add("flow id: " + flowId + " successful packet: " + entry.getValue().size());

                }
            }
        }

        choiceBox.setItems(list);
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int index = newValue.intValue();
            recoverChoiceButtonIndex = index;
            if (lists != null) {
                List<PacketTimingInfo> packetTimingInfos = lists.get(index);
                popDataToThirdChart(packetTimingInfos, packetLatencyInformation);
            }

        });
        choiceBox.getSelectionModel().select(recoverChoiceButtonIndex);


        vBox.getChildren().add(choiceBox);

        packetLatencyInformation.setText(recoverTextAreaInformation);
        packetLatencyInformation.setScrollTop(Double.MAX_VALUE);
        packetLatencyInformation.setFont(new Font(20));
        vBox.getChildren().add(packetLatencyInformation);

        vBox.prefWidthProperty().bind(scrollPane.widthProperty());
        vBox.setAlignment(Pos.CENTER);
        //scrollPane.setContent(vBox);
        //scrollPane.setPannable(true);
        return vBox;
    }


    /**
     * build the content for the flits trace analyze
     *
     * @param flow_id_to_flits_map source data
     * @return content node layout
     */
    @SuppressWarnings("unchecked")
    private ScrollPane getFlitsTraceContentCenter(HashMap<Integer, List<HashMap<Integer, List<FlitsInfo>>>> flow_id_to_flits_map) {
        ScrollPane rootScroll = new ScrollPane();
        VBox rootBox = new VBox(8);
        rootBox.setPadding(new Insets(15, 12, 15, 12));
        rootBox.getChildren().clear();


        for (Map.Entry<Integer, List<HashMap<Integer, List<FlitsInfo>>>> entry : flow_id_to_flits_map.entrySet()) {


            TitledPane titledPane = new TitledPane();

            //flow id as every titledPane id
            titledPane.setId(String.valueOf(entry.getKey()));
            titledPane.setText("Flow ID: " + entry.getKey());
            titledPane.setExpanded(false);


            //set content for every titledPaned
            VBox titledPaneContent = new VBox(5);
            List<HashMap<Integer, List<FlitsInfo>>> flitsList = entry.getValue();
            for (HashMap<Integer, List<FlitsInfo>> flitMap : flitsList) {
                StringBuffer traceDetails = new StringBuffer();
                StringBuffer tail = new StringBuffer();
                int header = 0;

                for (Map.Entry<Integer, List<FlitsInfo>> flitInfo : flitMap.entrySet()) {
                    header = flitInfo.getKey();

                    List<FlitsInfo> infos = flitInfo.getValue();
                    for (FlitsInfo info : infos) {
                        double time = info.getFlitsTime();
                        String position = info.getFlitPosition();
                        String[] split = position.split("\\.");
                        if (infos.indexOf(info) == infos.size() - 1) {
                            tail.append(time + "ns, " + split[0]);
                        } else {
                            tail.append(time + "ns, " + split[0] + "--->");
                        }

                    }
                }

                traceDetails.append("Flit ID: " + header+"      Path");
                traceDetails.append(": ");
                traceDetails.append(tail);
                Label traceInfo = new Label(traceDetails.toString());
                titledPaneContent.getChildren().add(traceInfo);
            }


            titledPane.setContent(titledPaneContent);
            titledPane.prefWidthProperty().bind(rootBox.widthProperty());

            //add new titlePane in the parent
            rootBox.getChildren().add(titledPane);

        }

        rootBox.prefHeightProperty().bind(rootScroll.heightProperty());
        rootBox.prefWidthProperty().bind(rootScroll.widthProperty());
        rootScroll.setContent(rootBox);
        rootScroll.setPannable(true);
        rootScroll.setPrefHeight(1000);

        return rootScroll;
    }

    private void popDataToThirdChart(List<PacketTimingInfo> packetTimingInfos, TextArea label) {
        thirdLineChart.getData().clear();
        label.setText("");
        XYChart.Series series = new XYChart.Series();
        StringBuffer buffer = new StringBuffer();
        //xAxis_3.setLowerBound(packetTimingInfos.get(0).getPacket_id());
        //xAxis_3.setUpperBound(packetTimingInfos.get(packetTimingInfos.size()-1).getPacket_id());
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
        recoverTextAreaInformation = buffer.toString();
        label.appendText(buffer.toString());


        xAxis_3.setLowerBound(lower);
        xAxis_3.setUpperBound(upper);
        xAxis_3.setAutoRanging(false);
        thirdLineChart.getData().add(series);

    }

    private void recoverElementMark(Accordion parentAccordionForFifo, HashSet<String> recoverElementMarkForContent2) {
        ObservableList<TitledPane> panes = parentAccordionForFifo.getPanes();
        for (TitledPane pane : panes) {
            String paneKey = pane.getId() + ".t";
            if (recoverElementMarkForContent2.contains(paneKey)) {
                pane.setText(pane.getId() + ConstantValue.MARK);
            }
            ObservableList<Node> children = ((VBox) pane.getContent()).getChildren();
            for (Node child : children) {
                if (child instanceof CheckBox) {
                    String checkBoxKey = child.getId() + ".c";
                    if (recoverElementMarkForContent2.contains(checkBoxKey)) {
                        ((CheckBox) child).setSelected(true);
                    }
                }
            }
        }


    }

    private void updateLineChart(CheckBox checkBox, String fifo, HashMap<String, List<FifoInfo>> fifoMap) {
        if (checkBox.isSelected()) {
            if (!fifoChartData.contains(fifo)) {
                System.out.println("add a chart element");
                fifoChartData.add(fifo);
            }
        } else {
            if (fifoChartData.contains(fifo)) {
                System.out.println("remove a chart element");
                fifoChartData.remove(fifo);
            }
        }
        popDataToChart(fifoChartData, fifoMap, fifo);
    }


    private void popDataToChart(List<String> fifoChartData, HashMap<String, List<FifoInfo>> fifoMap, String fifo) {
        lineChart.getData().clear();
        lxAxis.setAutoRanging(true);
        if (fifoChartData.size() != 0) {
            for (String name : fifoChartData) {
                if (fifoMap.containsKey(name)) {
                    List<FifoInfo> fifoInfos = fifoMap.get(name);
                    XYChart.Series series = new XYChart.Series();
                    series.setName(name);
                    for (FifoInfo fifoInfo : fifoInfos) {
                        series.getData().add(new XYChart.Data<>(fifoInfo.getTimestamp(), fifoInfo.getFifoSize()));
                    }
                    lineChart.getData().add(series);
                }
            }
        }
    }

    private void updateMarkFlag(CheckBox checkBox, TitledPane pane) {
        String title = pane.getText();
        if (!title.contains("#")) {
            if (checkBox.isSelected()) {
                title = title + ConstantValue.MARK;
                pane.setText(title);
                recoverElementMarkForContent2.add(pane.getText() + ".t");
            }
        } else {
            //determine whether the need to eliminate #
            boolean eliminate = true;
            VBox box = (VBox) pane.getContent();
            ObservableList<Node> children = box.getChildren();
            for (Node child : children) {
                if (child instanceof CheckBox) {
                    if (((CheckBox) child).isSelected()) {
                        eliminate = false;
                        break;
                    }
                }
            }
            if (eliminate) {
                pane.setText(pane.getText().substring(0, pane.getText().length() - 5));
                if (recoverElementMarkForContent2.contains(pane.getText() + ".t")) {
                    recoverElementMarkForContent2.remove(pane.getText() + ".t");
                }
            }
        }


    }


    private Separator getSeparator(Orientation horizontal) {

        Separator separator = new Separator();
        separator.setOrientation(horizontal);
        return separator;

    }


    public void setCenter(BorderPane root, Node node) {
        VBox vBox = new VBox();

        if (node != null) {
            vBox.getChildren().addAll(getContentTop(), node);
        } else {

            vBox.getChildren().addAll(getContentTop(), getFirstContentCenter());

        }
        root.setCenter(vBox);
    }

    public void registerRoot(BorderPane root) {
        this.root = root;
    }


    private class ButtonEvents implements EventHandler {
        @Override
        public void handle(Event event) {
            Button target = (Button) event.getTarget();
            switch (target.getId()) {
                case "0":
                    startFlag = 0;
                    setCenter(root, getFirstContentCenter());
                    // flowLatencyService.getContent();
                    break;
                case "1":
                    startFlag = 1;
                    fifoSizeService.getContent();
                    break;
                case "2":
                    startFlag = 2;
                    //setCenter(root, getPacketAnalyzeContentCenter(null));
                    flowPacketLatencyService.getContent();
                    break;
                case "3":
                    startFlag = 3;
                    flitTraceService.getContent();
                    break;
                case "start":
                    switch (startFlag) {
                        case 0:
                            clearContent2();
                            flowLatencyService.startAnalyze(dataModelName, currentDataBase, flowLatencyChart1, flowLatencyChart2, flowBottomLabel);
                            break;
                        case 1:
                            fifoSizeService.startAnalyze(dataModelName, currentDataBase);
                            break;
                        case 2:
                            flowPacketLatencyService.startAnalyze(dataModelName, currentDataBase);
                            break;
                        case 3:
                            flitTraceService.startAnalyze(dataModelName, currentDataBase);
                            break;
                    }
                    break;
                case "update":
                    double start = Double.valueOf(startTime.getText());
                    double end = Double.valueOf(endTime.getText());

                    if (startTime.getText().equals("")) {
                        return;
                    }
                    if (endTime.getText().equals("")) {
                        return;
                    }

                    if (start >= end) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning Dialog");
                        alert.setHeaderText("Look, a Warning Dialog");
                        alert.setContentText("The start time must be less than the end time");
                        alert.showAndWait();
                        return;
                    }

                    lineChart.getData().clear();
                    lxAxis.setLowerBound(start);
                    lxAxis.setUpperBound(end);
                    lxAxis.setAutoRanging(false);
                    if (fifoChartData.size() != 0) {
                        for (String name : fifoChartData) {
                            if (fifoMapClone.containsKey(name)) {
                                List<FifoInfo> fifoInfos = fifoMapClone.get(name);
                                XYChart.Series series = new XYChart.Series();
                                series.setName(name);
                                for (FifoInfo info : fifoInfos) {
                                    double timestamp = info.getTimestamp();
                                    if (timestamp >= start && timestamp < +end) {
                                        series.getData().add(new XYChart.Data<>(timestamp, info.getFifoSize()));
                                    }
                                }
                                lineChart.getData().add(series);
                            }
                        }
                    }
                    break;
                case "clear":
                    clearContent2();
                    break;
                default:
                    break;
            }
        }
    }


    private void clearContent2() {
        lineChart.getData().clear();
        if (label_1 != null) {
            label_1.setText("");
        }
        if (label_2 != null) {
            label_2.setText("");
        }
        ObservableList<TitledPane> panes = parentAccordionForFifoSize.getPanes();
        if (panes.size() != 0) {

            for (TitledPane pane : panes) {
                if (pane.getText().contains("#")) {
                    pane.setText(pane.getText().substring(0, pane.getText().length() - 5));
                    VBox content = (VBox) pane.getContent();
                    ObservableList<Node> children = content.getChildren();
                    if (children.size() != 0) {
                        for (Node node : children) {
                            if (node instanceof CheckBox) {
                                ((CheckBox) node).setSelected(false);
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * call back method
     * data visualization operations for fifo Analyze
     *
     * @param fifoMap     fifo information
     * @param processData processed data
     */
    @Override
    public void updateFifoSizeAnalyze(HashMap<String, List<FifoInfo>> fifoMap, TreeMap<String, List<String>> processData) {
        setCenter(root, getFifoSizeAnalyzeContentCenter(fifoMap, processData));
    }


    /**
     * call back method
     * data visualization operations for packet analyze
     *
     * @param flow_id_to_packetInfo_map packet information in flow
     */
    @Override
    public void updateFlowPacketLatency(HashMap<Integer, List<PacketTimingInfo>> flow_id_to_packetInfo_map) {

        setCenter(root, getPacketAnalyzeContentCenter(flow_id_to_packetInfo_map));
    }


    /**
     * call back method
     * data visualization operations for flits trace
     *
     * @param flow_id_to_flits_map flits information in flows
     */
    @Override
    public void updateFlitsTraceData(HashMap<Integer, List<HashMap<Integer, List<FlitsInfo>>>> flow_id_to_flits_map) {

        setCenter(root, getFlitsTraceContentCenter(flow_id_to_flits_map));

    }


}
