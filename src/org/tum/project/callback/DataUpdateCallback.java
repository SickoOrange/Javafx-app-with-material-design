package org.tum.project.callback;

import org.tum.project.bean.FifoInfo;
import org.tum.project.bean.FlitsInfo;
import org.tum.project.bean.PacketTimingInfo;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * This callback interface, the purpose is to extract data from the database,
 * analysis, the results call back to the MainController,
 * Used for data visualization operations
 */

public interface DataUpdateCallback {


    /**
     * extract the result to "flow latency analysis"
     *
     * @param flowChartDataMap
     * @param analyzeResult
     */
    void updateFLowLatency(HashMap<Long, String> flowChartDataMap, StringBuffer analyzeResult);

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
