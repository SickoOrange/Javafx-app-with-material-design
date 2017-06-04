package org.tum.project.login_controller;

import com.jfoenix.controls.JFXRippler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * menus holder controller to control the logic after user login in success.
 * Created by Yin Ya on 2017/5/28.
 */
public class MenusHolderController implements Initializable {
    @FXML
    private Pane NocEditor;

    @FXML
    private Pane Analytics;

    @FXML
    private HBox Holder;
    private Parent dashBoard;
    private static Stage primaryStage;
    private static Stage dashboardStage;


    /**
     * open the noc editor dashboardStage
     *
     * @param event mouse click event
     */
    @FXML
    private void openNocEditor(MouseEvent event) {
        System.out.println("click one");
        //openStage();
    }

    /**
     * open the analytics and simulation dashboardStage
     *
     * @param event mouse click event
     */
    @FXML
    private void openAnalytics(MouseEvent event) {
        System.out.println(primaryStage);
        openStage(dashBoard);
    }

    private void openStage(Parent node) {
        dashboardStage = new Stage();
        dashboardStage.setScene(new Scene(node));
        dashboardStage.setTitle("Analytics Dashboard");
        dashboardStage.show();
        primaryStage.close();
    }

    public static Stage getDashBoardStage() {
        return dashboardStage;
    }


    private void setUpRipples() {
        JFXRippler fXRippler_Noc = new JFXRippler(NocEditor, JFXRippler.RipplerMask.RECT, JFXRippler.RipplerPos.FRONT);
        JFXRippler fXRippler_analytics = new JFXRippler(Analytics, JFXRippler.RipplerMask.RECT, JFXRippler.RipplerPos
                .FRONT);
        Holder.getChildren().addAll(fXRippler_Noc, fXRippler_analytics);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpRipples();
        createStage();
    }


    /**
     * create dashboardStage that need to be switch
     */
    private void createStage() {
        try {
            dashBoard = FXMLLoader.load(getClass().getResource("../dashboard_controller/DashBoard.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void registerPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

}
