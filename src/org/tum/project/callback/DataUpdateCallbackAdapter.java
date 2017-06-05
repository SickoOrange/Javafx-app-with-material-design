package org.tum.project.callback;

import org.tum.project.bean.FifoInfo;
import org.tum.project.bean.FlitsInfo;
import org.tum.project.bean.PacketTimingInfo;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * adapter for data call back
 * Created by heylbly on 17-6-5.
 */
public class DataUpdateCallbackAdapter implements DataUpdateCallback {
    @Override
    public void updateFLowLatency(HashMap<Long, String> flowChartDataMap, StringBuffer analyzeResult) {

    }

    @Override
    public void updateFifoSizeAnalyze(HashMap<String, List<FifoInfo>> fifoMap, TreeMap<String, List<String>> processData) {

    }

    @Override
    public void updateFlowPacketLatency(HashMap<Integer, List<PacketTimingInfo>> flow_id_to_packetInfo_map) {

    }

    @Override
    public void updateFlitsTraceData(HashMap<Integer, List<HashMap<Integer, List<FlitsInfo>>>> flow_id_to_flits_map) {

    }
}
