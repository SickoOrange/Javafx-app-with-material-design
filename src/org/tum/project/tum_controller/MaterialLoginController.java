package org.tum.project.tum_controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.tum.project.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * main app entry for login in with a databank
 * Created by Yin Ya on 2017/5/27.
 */
public class MaterialLoginController implements Initializable {
    private final String desciption = "Technische Universität München\n" +
            "Department of Electrical Engineering and Information Technology\n" +
            "Institute for Electronic Design Automation";

    @FXML
    private Text appDescription;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXCheckBox rememberMe;

    @FXML
    private AnchorPane loginRoot;

    @FXML
    private HBox menusHolder;
    private Parent menusCard;

    @FXML
    private HBox progressBar;


    /**
     * determine whether to automatically fill the account password
     *
     * @param location  location
     * @param resources resource
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            menusCard = FXMLLoader.load(getClass().getResource("../tum_controller/MenusHolder.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        appDescription.setText(desciption);
        String isRemember = Utils.readPropValue("isRemember");
        if (Boolean.valueOf(isRemember)) {
            //get the user name and password
            String userName = Utils.readPropValue("userName");
            String userPassword = Utils.readPropValue("userPassword");
            rememberMe.setSelected(true);
            name.setText(userName);
            password.setText(userPassword);
            name.setFocusTraversable(false);
            password.setFocusTraversable(false);
        }

        rememberMe.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Utils.updatePropValue("isRemember", Boolean.toString(newValue));
        });


    }

    /**
     * login to the databank
     *
     * @param event click event
     */
    @FXML
    void signIn(ActionEvent event) {
        new Thread(() -> {
            String userName = name.getText();
            String userPassword = password.getText();
            connectMysql(userName, userPassword);
        }).start();


    }

    private void connectMysql(String userName, String userPassword) {
        // register mysql
        Connection conn = null;
        try {

            Class.forName("com.mysql.jdbc.Driver");

            // obtain mysql connection
            // in the terminal, "show variables", see the mysql port
            // database name: SystemC


            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", userName, userPassword);

        } catch (ClassNotFoundException | SQLException e) {
            // TODO: 2017/5/28  snack notification
            e.printStackTrace();
        }

        if (conn != null) {
            //login success
            Utils.updatePropValue("userName", userName);
            Utils.updatePropValue("userPassword", userPassword);
            System.out.println("login successfully data: " + userName + "," + userPassword);
            switchMenusHolder(loginRoot, menusCard);


        }
    }

    private void switchMenusHolder(Node original, Node target) {
        FadeTransition inTransition = new FadeTransition();
        inTransition.setNode(original);
        inTransition.setDuration(Duration.millis(600));
        inTransition.setFromValue(1.0);
        inTransition.setToValue(0.0);
        inTransition.play();
        inTransition.setOnFinished(event -> {
            menusHolder.getChildren().clear();
            menusHolder.getChildren().add(target);
//            FadeTransition outTransition = new FadeTransition();
//            outTransition.setNode(target);
//            outTransition.setDuration(Duration.millis(1000));
//            outTransition.setFromValue(0.0);
//            outTransition.setToValue(1.0);
//            outTransition.play();
        });

    }

}
