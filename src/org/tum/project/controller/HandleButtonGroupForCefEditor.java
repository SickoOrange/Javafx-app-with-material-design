package org.tum.project.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.tum.project.CefModelEditor.CefVisualizationService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * this class ist handle button click event controller for handle_button_group.fxml
 * it's used to handle click event for cef Editor Button Group
 * link open file, save file, add block, delete bock, add link, delete link
 * Created by Yin Ya on 2017/5/14.
 */
public class HandleButtonGroupForCefEditor implements Initializable {
    private String saveFilePathString;


    @FXML
    private JFXTextField openFilePath;
    @FXML
    private JFXTextField  saveFilePath;


    private CefVisualizationService cefVisualizationService;
    private Stage mainStage;
    private String openFilePathString = "";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //init
        cefVisualizationService = MainController.getCefVisualizationService();
        mainStage = MainController.getStage();

    }

    /**
     * open the xml file with the file chooser
     *
     * @param event
     */
    @FXML
    void openFile(ActionEvent event) {
        openFilePathString = cefVisualizationService.handleCefEditor(mainStage, "open");
        openFilePath.setText(openFilePathString);


    }


    /**
     * add a block type to the cef document root
     * attention: add a block should add some port
     *
     * @param event
     */
    @FXML
    void modifyBlock(ActionEvent event){

        cefVisualizationService.popCommand("add_block");
    }

    @FXML
    void deleteBlock(ActionEvent event) {
        cefVisualizationService.popCommand("delete_block");

    }

    @FXML
    void modifyPort(ActionEvent event) {
        cefVisualizationService.popCommand("add_port");

    }

    @FXML
    void modifyLink(ActionEvent event) {
        cefVisualizationService.popCommand("add_link");
    }

    @FXML
    void deleteLink(ActionEvent event) {
        cefVisualizationService.popCommand("delete_link");

    }

    @FXML
    void saveFile(ActionEvent event) {
        //save the element to the document root, just effective for that be double clicked element
        //in the graph
        System.out.println("save to the document root");
        cefVisualizationService.save();

    }


}
