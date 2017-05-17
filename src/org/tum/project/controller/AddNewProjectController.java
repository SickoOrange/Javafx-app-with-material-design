package org.tum.project.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Created by Yin Ya on 2017/5/16.
 */
public class AddNewProjectController {
    @FXML
    private JFXButton add_new_project;

    @FXML
    void newProjectAction(ActionEvent event) {
        System.out.println("add new project card");
    }
}
