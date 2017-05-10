package sample.callback;

import sample.bean.FifoInfo;
import sample.bean.FlitsInfo;
import sample.bean.PacketTimingInfo;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * This callback interface, the purpose is to extract data from the database,
 * analysis, the results call back to the MainController,
 * Used for data visualization operations
 */

public interface DataUpdateCallback {

    //void updateFLowLatency(HashMap<Long, String> flowChartDataMap);

    /**
     * extract the result to "fifo Analyze"
     *
     * @param fifoMap
     * @param processData
     */
    void updateFifoSizeAnalyze(HashMap<String, List<FifoInfo>> fifoMap, TreeMap<String, List<String>> processData);


    /**
     * extract the result to "packet Analyze"
     *
     * @param flow_id_to_packetInfo_map
     */
    void updateFlowPacketLatency(HashMap<Integer, List<PacketTimingInfo>> flow_id_to_packetInfo_map);

    /**
     * extract the result to "flits trance"
     *
     * @param flow_id_to_flits_map
     */
    void updateFlitsTraceData(HashMap<Integer, List<HashMap<Integer, List<FlitsInfo>>>> flow_id_to_flits_map);


}
