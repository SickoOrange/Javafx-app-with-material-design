package sample.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.controller.LoginController;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../layout/login_fxml.fxml"));
            primaryStage.setTitle("TUM GUI Analyse Tool");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();
            LoginController.registerStage(primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
