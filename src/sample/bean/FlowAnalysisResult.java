package sample.bean;

import java.util.HashMap;


public class FlowAnalysisResult {
    private HashMap<Double, Integer> latency_counter = new HashMap<>();
    private int generatedPacketNumber = 0;
    private int successfulPacketNumber = 0;


    public HashMap<Double, Integer> getLatency_counter() {
        return latency_counter;
    }


    public void setLatency_counter(HashMap<Double, Integer> latency_counter) {
        this.latency_counter = latency_counter;
    }

    public int getGeneratedPacketNumber() {
        return generatedPacketNumber;
    }

    public void setGeneratedPacketNumber(int generatedPacketNumber) {
        this.generatedPacketNumber = generatedPacketNumber;
    }

    public int getSuccessfulPacketNumber() {
        return successfulPacketNumber;
    }

    public void setSuccessfulPacketNumber(int successfulPacketNumber) {
        this.successfulPacketNumber = successfulPacketNumber;
    }


}
