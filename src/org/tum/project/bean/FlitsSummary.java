package org.tum.project.bean;

import java.util.HashMap;
import java.util.List;

/**
 * Represents a flit summary of flow
 * Created by heylbly on 17-6-7.
 */
public class FlitsSummary {
    private int flowid;
    private int flitsNumber;
    private String startPoint;
    private String endPoint;
    private int successFlitsNumber = 0;
    private int failedFlitsNumber = 0;
    private double startTime;
    private double endTime;


    private HashMap<Integer, List<FlitsInfo>> allFlitsIDList = new HashMap<>();

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }


    public int getFlowid() {
        return flowid;
    }

    public void setFlowid(int flowid) {
        this.flowid = flowid;
    }

    public int getFlitsNumber() {
        return flitsNumber;
    }

    public void setFlitsNumber(int flitsNumber) {
        this.flitsNumber = flitsNumber;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public int getSuccessFlitsNumber() {
        return successFlitsNumber;
    }


    public int getFailedFlitsNumber() {
        return failedFlitsNumber;
    }


    public double getRoutingDuration() {
        return routingDuration;
    }

    public void setRoutingDuration(double routingDuration) {
        this.routingDuration = routingDuration;
    }

    private double routingDuration;

    public void increaseSuccessFlitNumber() {
        this.successFlitsNumber++;
    }

    public void increaseFailedFlitNumber() {
        this.failedFlitsNumber++;
    }

    public void setFlitsIDList(int flitID, List<FlitsInfo> infos) {
        allFlitsIDList.put(flitID, infos);
    }

    @Override
    public String toString() {
        return "FlitsSummary{" +
                "flowid=" + flowid +
                ", flitsNumber=" + flitsNumber +
                ", startPoint='" + startPoint + '\'' +
                ", endPoint='" + endPoint + '\'' +
                ", successFlitsNumber=" + successFlitsNumber +
                ", failedFlitsNumber=" + failedFlitsNumber +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", allFlitsIDList=" + allFlitsIDList +
                ", size=" + allFlitsIDList.size() +
                ", routingDuration=" + routingDuration +
                '}';
    }

    public HashMap<Integer, List<FlitsInfo>> getAllFlitsIDList() {
        return allFlitsIDList;
    }

}
