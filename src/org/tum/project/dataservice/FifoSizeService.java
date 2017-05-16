package org.tum.project.dataservice;

import javafx.scene.control.TitledPane;
import org.tum.project.bean.FifoInfo;
import org.tum.project.utils.sqlUtils;
import org.tum.project.callback.DataUpdateCallback;
import org.tum.project.constant.ConstantValue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class FifoSizeService {


    private HashMap<String, List<FifoInfo>> fifoMap = new HashMap<>();
    private TreeMap<String, List<String>> processData = new TreeMap<>();

    public void setCallback(DataUpdateCallback callback) {
        if (callback != null) {
            this.callback = callback;
        }
    }

    private DataUpdateCallback callback;


    public void startAnalyze(List<String> dataModelName, String currentDataBase) {
        initVariables();
        start(dataModelName, currentDataBase);
        processing(fifoMap);
        callback.updateFifoSizeAnalyze(fifoMap, processData);

    }

    private void processing(HashMap<String, List<FifoInfo>> fifoMap) {
        if ((fifoMap != null) && (fifoMap.size() > 0)) {
            List<String> list;

            for (Map.Entry<String, List<FifoInfo>> entry : fifoMap.entrySet()) {
                TitledPane pane = new TitledPane();
                pane.setPrefWidth(50);
                String fifoName = entry.getKey();
                String[] strings = fifoName.split("\\.");
                String moduleName = strings[0];
                //R11.in_port_4.input_channel_0.fifo
                //ASIC1.producer.dataFifo

                if (processData.containsKey(moduleName)) {
                    list = processData.get(moduleName);
                } else {
                    list = new ArrayList<>();
                }
                list.add(fifoName.substring(strings[0].length()+1));
                processData.put(moduleName, list);

            }
        }
    }


    private void start(List<String> dataModelName, String currentDataBase) {
        Connection conn;
        try {
            conn = sqlUtils.getMySqlConnection(currentDataBase);
            Statement statement = conn.createStatement();
            ResultSet rs = null;
            for (String name : dataModelName) {
                String queryProducer = "select * from " + name;
                rs = statement.executeQuery(queryProducer);
                List<FifoInfo> fifoInfosList;
                while (rs.next()) {
                    FifoInfo fifoInfo = new FifoInfo();
                    String fifoName = rs.getString(ConstantValue.FIFONAME);
                    if (fifoMap.containsKey(fifoName)) {
                        fifoInfosList = fifoMap.get(fifoName);

                    } else {
                        fifoInfosList = new ArrayList<>();
                    }

                    fifoInfo.setTimestamp(rs.getDouble(ConstantValue.TIMESTAMP));
                    fifoInfo.setFifoName(rs.getString(ConstantValue.FIFONAME));
                    fifoInfo.setFifoSize(rs.getInt(ConstantValue.CURRENTFIFOSIZE));
                    fifoInfosList.add(fifoInfo);
                    fifoMap.put(fifoName, fifoInfosList);
                }
            }
            sqlUtils.closeConn(conn, statement, rs);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    private void initVariables() {
        fifoMap.clear();
        processData.clear();
    }

    public void getContent() {
        if (callback != null) {
            callback.updateFifoSizeAnalyze(fifoMap,processData);
        }
    }
}
