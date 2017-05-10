package org.tum.project.bean;

import javafx.beans.property.*;

/**
 * this data mode is used for the tabel view
 * show the information for the flits trance
 * Created by SickoOrange on 2017/5/10.
 */
public class FlitsDataMode {

    private SimpleStringProperty flitsId;
    private SimpleStringProperty time;
    private SimpleStringProperty visualization;
    private SimpleStringProperty position;


    public FlitsDataMode(String flitsId,String visualization ,String time, String position) {
        this.flitsId = new SimpleStringProperty(flitsId);
        this.visualization=new SimpleStringProperty(visualization);
        this.time = new SimpleStringProperty(time);
        this.position = new SimpleStringProperty(position);
    }

    public String getFlitsId() {
        return flitsId.get();
    }

    public SimpleStringProperty flitsIdProperty() {
        return flitsId;
    }

    public void setFlitsId(String flitsId) {
        this.flitsId.set(flitsId);
    }

    public String getTime() {
        return time.get();
    }

    public SimpleStringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public String getPosition() {
        return position.get();
    }

    public SimpleStringProperty positionProperty() {
        return position;
    }

    public void setPosition(String position) {
        this.position.set(position);
    }

    public String getVisualization() {
        return visualization.get();
    }

    public SimpleStringProperty visualizationProperty() {
        return visualization;
    }

    public void setVisualization(String visualization) {
        this.visualization.set(visualization);
    }
}
