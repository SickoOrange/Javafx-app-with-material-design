package org.tum.project.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.tum.project.controller.LoginController;

import java.io.IOException;

/**
 * Created by Yin Ya on 2017/5/14.
 */
public class TestUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../layout/Editor.fxml"));
            primaryStage.setTitle("TUM GUI Analyse Tool");
            VBox box=new VBox(5);
            box.getChildren().add(root);
            primaryStage.setScene(new Scene(box, 800, 600));
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
