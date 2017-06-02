package org.tum.project.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tum.project.controller.LoginController;
import org.tum.project.login_controller.MaterialLoginController;
import org.tum.project.login_controller.MenusHolderController;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            //Parent root = FXMLLoader.load(getClass().getResource("../layout/login_fxml.fxml"));
            Parent root = FXMLLoader.load(getClass().getResource("../login_controller/MaterialLogin.fxml"));
            primaryStage.setTitle("SystemC NoCs Analytics Tool");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            // LoginController.registerStage(primaryStage);
            MenusHolderController.registerPrimaryStage(primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
