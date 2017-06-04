package org.tum.project.dataservice;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.tum.project.bean.ProjectInfo;
import org.tum.project.callback.SimulationCallBack;
import org.tum.project.utils.xmlUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * this class is used for generated the simulation Test bench with C++ Source Code for Noc System
 * Created by Yin Ya on 2017/5/16.
 */
public class SimulationService {
    SimulationCallBack simulationCallBack;
    private VBox simulation_button_group;
    private VBox simulationVBox;
    private ScrollPane simulationScrollPane;
    private FlowPane simulationFlowPane;
    private ArrayList<ProjectInfo> projectInfos;

    public void setSimulationCallBack(SimulationCallBack simulationCallBack) {
        if (simulationCallBack != null) {
            this.simulationCallBack = simulationCallBack;
        }
    }


    private void updateContent() {
        popCommand(null);
    }


    public void getProject() {
        popCommand("add_project");
    }

    private void popCommand(String command) {
        simulationCallBack.popSimulationData(command);
    }


    /**
     * get the simulation layout,
     *
     * @param command command
     * @return node layout
     */
    public Node getSimulationLayout(String command) {
        if (simulationScrollPane == null) {
            initView();
        }
        if (command != null) {
            switch (command) {
                case "add_new_project":
                    AnchorPane newProjectPane = null;
                    try {
                        newProjectPane = FXMLLoader.load(getClass().getResource
                                ("../layout/simulation_card" +
                                        ".fxml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    simulationFlowPane.getChildren().add(0, newProjectPane);
                    break;
            }
        }
        return simulationScrollPane;
    }

    private void initView() {
        simulationScrollPane = new ScrollPane();
        simulationVBox = new VBox(5);
        simulationFlowPane = new FlowPane(10,10);
        try {
            if (simulation_button_group == null) {
                simulation_button_group = FXMLLoader.load(getClass().getResource
                        ("../layout/simulation_button_group" +
                                ".fxml"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ((projectInfos != null)&&(projectInfos.size()!=0)) {
        for (ProjectInfo info : projectInfos) {
            AnchorPane existProject = null;
            try {
                existProject = FXMLLoader.load(getClass().getResource
                        ("../layout/simulation_card" +
                                ".fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ObservableList<Node> children = ((Pane) existProject.getChildren().get(0)).getChildren();
            for (Node child : children) {
                if (child instanceof JFXTextField) {
                    JFXTextField textField = (JFXTextField) child;
                    textField.setEditable(false);
                    String id = textField.getId().split("_")[1];
                    System.out.println("id: "+id);
                    switch (id) {
                        case "projectName":
                            textField.setText(info.getSimulationFile());
                            break;
                        case "dataBankName":
                            textField.setText(info.getDataBankName());
                            break;
                        case "moduleTable":
                            textField.setText(info.getModuleTableName());
                            break;
                        case "fifoTable":
                            textField.setText(info.getFifoTableName());
                            break;
                        case "fastfifoTable":
                            textField.setText(info.getFastfifoTabelName());
                            break;

                    }
                }
                if (child instanceof JFXButton) {
                    JFXButton button = (JFXButton) child;
                    String id = button.getId().split("_")[1];
                    switch (id) {
                        case "generateButton":
                            button.setDisable(true);
                            break;
                        case "simulationButton":
                            break;
                    }
                }
            }
            simulationFlowPane.getChildren().add(existProject);
        }
        }

        simulationVBox.getChildren().add(simulation_button_group);
        simulationVBox.getChildren().add(simulationFlowPane);
        simulationVBox.setPadding(new Insets(15, 12, 15, 12));

        simulationVBox.prefHeightProperty().bind(simulationScrollPane.heightProperty());
        simulationVBox.prefWidthProperty().bind(simulationScrollPane.widthProperty());
        simulationScrollPane.setContent(simulationVBox);
        simulationScrollPane.setPannable(true);
        simulationScrollPane.setPrefHeight(1000);
    }

    /**
     * read project configuration xml and generate the layout
     */
    public void getSimulationContent() {
        File projectXml = xmlUtils.createAndGetProjectXmlFile();
        try {
            Document document = xmlUtils.readDocument(projectXml.getAbsolutePath());
            //if the project is empty or not
            boolean hasContent = document.getRootElement().hasContent();
            if (hasContent) {
                //has project, show all project on the dashboard
                projectInfos = xmlUtils.getAllProjectFromDocument(document);

            }
            updateContent();
        } catch (MalformedURLException | DocumentException e) {
            e.printStackTrace();
        }
    }




    /**
     * create a new project card pane to the layout
     */
    public void createNewProjectCard() {
        popCommand("add_new_project");

    }
}
