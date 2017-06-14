package org.tum.project.dataservice;


import javafx.scene.Node;
import org.tum.project.bean.FlitsInfo;
import org.tum.project.utils.sqlUtils;
import org.tum.project.callback.DataUpdateCallback;
import org.tum.project.constant.ConstantValue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FlitTraceService {
    private DataUpdateCallback callback;

    private HashMap<Integer, List<HashMap<Integer, List<FlitsInfo>>>> flow_id_to_flits_map = new HashMap();
    //private  HashMap<Integer,List<FlitsInfo>>


    /**
     * Accept the database table name with the database name,
     * Extract data from it and call back to MainController
     *
     * @param dataModelName
     * @param currentDataBase
     */
    public void startAnalyze(List<String> dataModelName, String currentDataBase) {

        initVariables();

        readData(dataModelName, currentDataBase);

        callback.updateFlitsTraceData(flow_id_to_flits_map);

    }

    private void initVariables() {
        flow_id_to_flits_map.clear();
    }


    private void readData(List<String> dataModelName, String currentDataBase) {
        Connection conn = null;
        try {
            conn = sqlUtils.getMySqlConnection(currentDataBase);
            Statement statement = conn.createStatement();
            ResultSet rs = null;
            for (String name : dataModelName) {
                String queryProducer = "select * from " + name;
                rs = statement.executeQuery(queryProducer);
                while (rs.next()) {

                    String accessMode = rs.getString("accessMode");
                    if (accessMode != null && accessMode.equals("Read")) {
                        int flowId = rs.getInt(ConstantValue.FLOWID);
                        int flitId = rs.getInt(ConstantValue.FLITID);
                        String fifoName = rs.getString(ConstantValue.FIFONAME);
                        double timeStamp = rs.getDouble(ConstantValue.TIMESTAMP);
                        FlitsInfo info = new FlitsInfo();
                        info.setFlitId(flitId);
                        info.setFlitPosition(fifoName);
                        info.setFlitsTime(timeStamp);
                        System.out.println("hello:\n"+info);
                        if (!flow_id_to_flits_map.containsKey(flowId)) {
                            List flitsList = new ArrayList();
                            HashMap<Integer, List<FlitsInfo>> flitsMap = new HashMap<>();

                            List<FlitsInfo> infoList = new ArrayList<>();
                            infoList.add(info);
                            flitsMap.put(flitId, infoList);

                            flitsList.add(flitsMap);
                            flow_id_to_flits_map.put(flowId, flitsList);


                        } else {
                            List<HashMap<Integer, List<FlitsInfo>>> flitsList = flow_id_to_flits_map.get(flowId);
                            boolean contain = false;
                            for (HashMap<Integer, List<FlitsInfo>> map : flitsList) {
                                if (map.containsKey(flitId)) {
                                    List<FlitsInfo> list = map.get(flitId);
                                    list.add(info);
                                    contain = true;
                                }
                            }

                            if (!contain) {
                                HashMap<Integer, List<FlitsInfo>> newFlits = new HashMap<>();
                                List<FlitsInfo> flitsInfoList = new ArrayList<>();
                                flitsInfoList.add(info);
                                newFlits.put(flitId, flitsInfoList);
                                flitsList.add(newFlits);
                            }


                        }
                    }

                }
            }
            sqlUtils.closeConn(conn, statement, rs);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCallback(DataUpdateCallback callback) {
        if (callback != null) {
            this.callback = callback;
        }
    }

    public void getContent() {
        if (callback != null) {
            callback.updateFlitsTraceData(flow_id_to_flits_map);
        }
    }




}
