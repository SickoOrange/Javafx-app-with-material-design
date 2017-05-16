package org.tum.project.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import org.tum.project.bean.FlowAnalysisResult;
import org.tum.project.bean.PacketTimingInfo;
import org.tum.project.utils.sqlUtils;
import org.tum.project.constant.ConstantValue;

import java.sql.*;
import java.util.*;


public class MainActivityController {



    @FXML
    private BarChart flowLatencyChart1;
    @FXML
    private CategoryAxis mxAxis1;
    @FXML
    private NumberAxis myAxis1;


    @FXML
    private BarChart flowLatencyChart2;
    @FXML
    private CategoryAxis mxAxis2;
    @FXML
    private NumberAxis myAxis2;


    private List<String> moduleTableList = new ArrayList<>();
    private List<String> fifoList = new ArrayList<>();
    private List<String> fastFiFoList = new ArrayList<>();
    //analyse for module table
    private HashMap<Integer, PacketTimingInfo> packet_id_to_info_map = new HashMap<Integer, PacketTimingInfo>();
    private HashMap<Integer, FlowAnalysisResult> flow_id_to_analysis_map = new HashMap<Integer, FlowAnalysisResult>();
    private HashMap<Long, String> flowChartDataMap = new HashMap<>();

    @FXML
    private void initialize() {
        System.out.println("initialize");
        mxAxis1.setLabel("Flow Index");
        myAxis1.setLabel("latency (ns)");
        flowLatencyChart1.setTitle("Flow Latency analyze in 4x4 Mesh");

        mxAxis2.setLabel("Flow Index");
        myAxis2.setLabel("total and success package");
        flowLatencyChart2.setTitle("Flow Latency analyze in 4x4 Mesh");

    }


    @FXML
    public void handleAnalyseAction(ActionEvent actionEvent) {
        init();

        //get all table name from database
        getAllTableName();
        // TODO: 03.04.17 根据提供的时间来找到具体某一次仿真的所有 moduletable fifo 和 fastfifo的表
        //pickSimulationTable(timestamp,moduleTableList);
        //get all module name from database
        HashSet<String> moduleNameSet = getAllModuleName(moduleTableList);

        //parse  PacketTimingInfo
        parseProducer();

        //parse   Consumer
        parseConsumer();

        //analyze all flows
        analyzeAllFlows();

        //report all flows
        reportAllFlows();

        //show in the linechart
        showInBarChart();
    }

    private void init() {
        moduleTableList = new ArrayList<>();
        fifoList = new ArrayList<>();
        fastFiFoList = new ArrayList<>();
        //analyse for module table
        packet_id_to_info_map = new HashMap<Integer, PacketTimingInfo>();
        flow_id_to_analysis_map = new HashMap<Integer, FlowAnalysisResult>();
        flowChartDataMap = new HashMap<>();

        flowLatencyChart1.getData().clear();
        flowLatencyChart2.getData().clear();

    }

    private void showInBarChart() {
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("latency");
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("total_pkt");
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("success_pkt");


        for (Map.Entry<Long, String> entry : flowChartDataMap.entrySet()) {
            String flow_id = String.valueOf(entry.getKey());
            String metaData = entry.getValue();
            String[] split = metaData.split("_");
            Float latency = Float.valueOf(split[0]);
            int total_pkt = Integer.valueOf(split[1]);
            int success_pkt = Integer.valueOf(split[2]);
            series1.getData().add(new XYChart.Data(flow_id, latency));
            series2.getData().add(new XYChart.Data(flow_id, total_pkt));
            series3.getData().add(new XYChart.Data(flow_id, success_pkt));

        }

        flowLatencyChart1.getData().add(series1);
        flowLatencyChart2.getData().add(series2);
        flowLatencyChart2.getData().add(series3);

        flowLatencyChart1.setBarGap(1.0);

        flowLatencyChart2.setBarGap(1.0);

    }

    private void reportAllFlows() {
        int flow_number = 0;
        double total_noc_latency = 0;
        int total_noc_packet_number = 0;
        int total_noc_success_packet_number = 0;

        for (Map.Entry<Integer, FlowAnalysisResult> entry : flow_id_to_analysis_map.entrySet()) {
            long flow_id = entry.getKey();
            FlowAnalysisResult flowAnalysis = entry.getValue();
            double total_flow_latency = 0;
            for (Map.Entry<Double, Integer> set : flowAnalysis.getLatency_counter().entrySet()) {
                double latency = set.getKey();
                int number = set.getValue();
                total_flow_latency = latency * number + total_flow_latency;
            }
            float average_flow_latency = (float) (total_flow_latency / (flowAnalysis.getSuccessfulPacketNumber()));
            System.out.println("Flow" + flow_id + ": average_latency=" + average_flow_latency
                    + " total_pkt=" + flowAnalysis.getGeneratedPacketNumber() + " success_pkt=" + flowAnalysis.getSuccessfulPacketNumber());
            String s = average_flow_latency + "_" + flowAnalysis.getGeneratedPacketNumber() + "_" + flowAnalysis.getSuccessfulPacketNumber();
            flowChartDataMap.put(flow_id, s);


            flow_number++;
            total_noc_latency = total_noc_latency + total_flow_latency;
            total_noc_packet_number = total_noc_packet_number + flowAnalysis.getGeneratedPacketNumber();
            total_noc_success_packet_number = total_noc_success_packet_number + flowAnalysis.getSuccessfulPacketNumber();
        }
        System.out.println("SUMMARY: flow number " + flow_number + " total_pkt " + total_noc_packet_number
                + " success_pkt " + total_noc_success_packet_number + " average_latency " + (float) total_noc_latency / total_noc_success_packet_number);
    }

    private void analyzeAllFlows() {

        for (Map.Entry<Integer, PacketTimingInfo> entry : packet_id_to_info_map.entrySet()) {

            FlowAnalysisResult flowAnalysis;
            if (flow_id_to_analysis_map.containsKey(entry.getValue().getFlow_id())) {
                flowAnalysis = flow_id_to_analysis_map.get(entry.getValue().getFlow_id());
            } else {
                flowAnalysis = new FlowAnalysisResult();
                flow_id_to_analysis_map.put(entry.getValue().getFlow_id(), flowAnalysis);
            }
            Integer generatedPacketNumber = flowAnalysis.getGeneratedPacketNumber();
            generatedPacketNumber++;
            flowAnalysis.setGeneratedPacketNumber(generatedPacketNumber);

            if (entry.getValue().getConsume_time() <= entry.getValue().getProduce_time()) {
                System.out.println("missing flit id= " + entry.getValue().getPacket_id() + " flow_id= " + entry.getValue()
                        .getFlow_id() + " produce_time= " + entry.getValue().getProduce_time());
            } else {
                Integer successfulPacketNumber = flowAnalysis.getSuccessfulPacketNumber();
                successfulPacketNumber++;
                flowAnalysis.setSuccessfulPacketNumber(successfulPacketNumber);
                double latency = entry.getValue().getConsume_time() - entry.getValue().getProduce_time();
                if (flowAnalysis.getLatency_counter().containsKey(latency)) {
                    int n = flowAnalysis.getLatency_counter().get(latency);
                    n++;
                    flowAnalysis.getLatency_counter().put(latency, n);
                } else {
                    flowAnalysis.getLatency_counter().put(latency, 1);
                }
            }
        }
    }


    private void parseConsumer() {
        Connection conn = null;
        try {
            conn = sqlUtils.getMySqlConnection("SystemC");
            Statement statement = conn.createStatement();
            ResultSet rs = null;
            for (String name : moduleTableList) {
                String queryProducer = "select timestamp, PacketId, flitId, isTail from " + name + " where moduleType='consumer'";
                rs = statement.executeQuery(queryProducer);
                while (rs.next()) {
                    double consumer_time = rs.getDouble(ConstantValue.TIMESTAMP);
                    int packetId = rs.getInt(ConstantValue.PACKETID);
                    int flitId = rs.getInt(ConstantValue.FLITID);
                    // 1: Tail 0: not Tail
                    int isTail = rs.getInt(ConstantValue.ISTAIL);
                    if (isTail != 1) {
                        packet_id_to_info_map.get(packetId).getFlit_ids().add(flitId);
                        continue;
                    }
                    if (packet_id_to_info_map.containsKey(packetId)) {
                        packet_id_to_info_map.get(packetId).setConsume_time(consumer_time);
                        packet_id_to_info_map.get(packetId).getFlit_ids().add(flitId);
                    } else {
                        System.out.println("packet id is not found! " + packetId);
                    }
                }
            }
            sqlUtils.closeConn(conn, statement, rs);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void parseProducer() {
        Connection conn = null;
        try {
            conn = sqlUtils.getMySqlConnection("SystemC");
            Statement statement = conn.createStatement();
            ResultSet rs = null;
            for (String name : moduleTableList) {
                String queryProducer = "select timestamp, PacketId,flowId from " + name + " where moduleType='producer'";
                rs = statement.executeQuery(queryProducer);
                while (rs.next()) {
                    PacketTimingInfo packetTimingInfo = new PacketTimingInfo();
                    packetTimingInfo.setProduce_time(rs.getDouble(ConstantValue.TIMESTAMP));
                    packetTimingInfo.setPacket_id(rs.getInt(ConstantValue.PACKETID));
                    packetTimingInfo.setFlow_id(rs.getInt(ConstantValue.FLOWID));
                    packet_id_to_info_map.put(packetTimingInfo.getPacket_id(), packetTimingInfo);
                }
            }
            sqlUtils.closeConn(conn, statement, rs);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private HashSet<String> getAllModuleName(List<String> moduleList) {
        HashSet<String> moduleNameSet = new HashSet<>();
        Connection conn = null;
        try {
            conn = sqlUtils.getMySqlConnection("SystemC");
            Statement statement = conn.createStatement();
            ResultSet rs = null;
            for (String name : moduleList) {
                String queryModuleName = "select moduleName from " + name;
                rs = statement.executeQuery(queryModuleName);
                while (rs.next()) {
                    moduleNameSet.add(rs.getString(ConstantValue.MODULENAME));
                }
            }
            sqlUtils.closeConn(conn, statement, rs);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return moduleNameSet;
    }

    private void getAllTableName() {
        Connection conn = null;
        try {
            conn = sqlUtils.getMySqlConnection("");
            Statement statement = conn.createStatement();
            String queryTable = "show tables";
            ResultSet rs = statement.executeQuery(queryTable);
            while (rs.next()) {
                String tableName = rs.getString(1);
                if (tableName.contains("module")) {
                    moduleTableList.add(tableName);
                } else if (tableName.contains("fast")) {
                    fastFiFoList.add(tableName);
                } else if (tableName.contains("fifo") && (!tableName.contains("fast"))) {
                    fifoList.add(tableName);
                }
            }
            sqlUtils.closeConn(conn, statement, rs);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
