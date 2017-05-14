package org.tum.project.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
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
    private JFXButton openFileButton;

    @FXML
    private JFXButton deleteBlockButton;


    @FXML
    private JFXButton addPortButton;

    @FXML
    private JFXTextField openFilePath;

    @FXML
    private JFXButton addBlockButton;

    @FXML
    private JFXButton deleteLinkButton;

    @FXML
    private JFXButton saveFileButton;

    @FXML
    private JFXTextField saveFilePath;

    @FXML
    private JFXButton addLinkButton;
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
    void addBlock(ActionEvent event) throws IOException {

        cefVisualizationService.addCommand("add_block");
    }

    @FXML
    void deleteBlock(ActionEvent event) {
        cefVisualizationService.addCommand("delete_block");

    }

    @FXML
    void addPort(ActionEvent event) {
        cefVisualizationService.addCommand("add_port");

    }

    @FXML
    void addLink(ActionEvent event) {
        cefVisualizationService.addCommand("add_link");
    }

    @FXML
    void deleteLink(ActionEvent event) {
        cefVisualizationService.addCommand("delete_link");

    }

    @FXML
    void saveFile(ActionEvent event) {

    }


}
