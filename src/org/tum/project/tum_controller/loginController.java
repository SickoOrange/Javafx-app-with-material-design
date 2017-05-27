package org.tum.project.tum_controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.eclipse.emf.ecore.resource.impl.PlatformResourceURIHandlerImpl;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * main app entry for login in with a databank
 * Created by Yin Ya on 2017/5/27.
 */
public class loginController implements Initializable {
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
    void signIn(ActionEvent event) {
        System.out.println("login");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        appDescription.setText(desciption);
    }

    public static  void main(String[] args){
        System.out.println("start");
    }

}
