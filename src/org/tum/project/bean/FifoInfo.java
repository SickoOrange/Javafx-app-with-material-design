package org.tum.project.bean;


public class FifoInfo {

    private double timestamp;
    private String fifoName;
    private int fifoSize;



    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public String getFifoName() {
        return fifoName;
    }

    public void setFifoName(String fifoName) {
        this.fifoName = fifoName;
    }

    public int getFifoSize() {
        return fifoSize;
    }

    public void setFifoSize(int fifoSize) {
        this.fifoSize = fifoSize;
    }


}
