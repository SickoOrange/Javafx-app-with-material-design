package org.tum.project.dataservice;

import org.tum.project.bean.PacketTimingInfo;
import org.tum.project.callback.DataUpdateCallback;
import org.tum.project.constant.ConstantValue;
import org.tum.project.sqlutils.sqlUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowPacketLatencyService {

    private static HashMap<Integer, PacketTimingInfo> packet_id_to_info_map = new HashMap<Integer, PacketTimingInfo>();



    private DataUpdateCallback callback;


    private static HashMap<Integer, List<PacketTimingInfo>> flow_id_to_packetInfo_map = new HashMap<>();

    public void setCallback(DataUpdateCallback callback) {
        if (callback != null) {
            this.callback = callback;
        }
    }
    public void startAnalyze(List<String> dataModelName, String currentDataBase) {
        initVariables();

        //parse  PacketTimingInfo
        parseProducer(dataModelName, currentDataBase);

        //parse   Consumer
        parseConsumer(dataModelName, currentDataBase);

        //analyze all flows
        analyzeAllFlows();
        callback.updateFlowPacketLatency(flow_id_to_packetInfo_map);
    }


    private void analyzeAllFlows() {

        for (Map.Entry<Integer, PacketTimingInfo> entry : packet_id_to_info_map.entrySet()) {

            List<PacketTimingInfo> successfulPacketList;
            if (flow_id_to_packetInfo_map.containsKey(entry.getValue().getFlow_id())) {

                successfulPacketList = flow_id_to_packetInfo_map.get(entry.getValue().getFlow_id());
            } else {

                successfulPacketList = new ArrayList<>();
                flow_id_to_packetInfo_map.put(entry.getValue().getFlow_id(), successfulPacketList);
            }


            if (entry.getValue().getConsume_time() <= entry.getValue().getProduce_time()) {
                System.out.println("missing flit id= " + entry.getValue().getPacket_id() + " flow_id= " + entry.getValue()
                        .getFlow_id() + " produce_time= " + entry.getValue().getProduce_time());
            } else {

                //record information for successful packet
                successfulPacketList.add(entry.getValue());
            }
        }
        System.out.println(flow_id_to_packetInfo_map);
    }

    private void parseConsumer(List<String> table, String database) {
        Connection conn = null;
        try {
            conn = sqlUtils.getMySqlConnection(database);
            Statement statement = conn.createStatement();
            ResultSet rs = null;
            for (String name : table) {
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

    private void parseProducer(List<String> table, String database) {
        Connection conn = null;
        try {
            conn = sqlUtils.getMySqlConnection(database);
            Statement statement = conn.createStatement();
            ResultSet rs = null;
            for (String name : table) {
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

    private void initVariables() {
        packet_id_to_info_map.clear();
       flow_id_to_packetInfo_map.clear();
    }

    public void getContent() {
        callback.updateFlowPacketLatency(flow_id_to_packetInfo_map);
    }
}
