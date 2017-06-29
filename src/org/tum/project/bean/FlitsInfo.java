package org.tum.project.bean;

/**
 * Created by SickoOrange on 2017/5/9.
 */
public class FlitsInfo {
    public double getFlitId() {
        return flitId;
    }

    public void setFlitId(int flitId) {
        this.flitId = flitId;
    }

    public String getFlitPosition() {
        return flitPosition;
    }

    public void setFlitPosition(String flitPosition) {
        this.flitPosition = flitPosition;
    }

    public double getFlitsTime() {
        return flitsTime;
    }

    public void setFlitsTime(double flitsTime) {
        this.flitsTime = flitsTime;
    }

    private double flitId;
    private String flitPosition;
    private double flitsTime;


    @Override
    public String toString() {
        return "FlitsInfo{" +
                "flitId=" + flitId +
                ", flitPosition='" + flitPosition + '\'' +
                ", flitsTime=" + flitsTime +
                '}';
    }
}
