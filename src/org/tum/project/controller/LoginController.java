package org.tum.project.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.tum.project.utils.sqlUtils;
import org.tum.project.constant.ConstantValue;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginController {
    @FXML
    private TextField nameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Text responseField;


    public static String realName = "";
    public static String realPassword = "";
    private static Stage parentStage;

    @FXML
    public void initialize() {
        passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                if (code.getName().equals("Enter")) {
                    String nameString = nameField.getText();
                    String passwordString = passwordField.getText();
                    connectMysql(nameString, passwordString);
                }
            }
        });

    }

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) throws SQLException {
        String nameString = nameField.getText();
        String passwordString = passwordField.getText();
        connectMysql(nameString, passwordString);

        System.out.println(nameString + " " + passwordString);
    }

    private void connectMysql(String name, String password) {
        // register mysql
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // obtain mysql connection
        // in the terminal, "show variables", see the mysql port
        //database name: SystemC
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", name, password);
        } catch (SQLException e) {
            responseField.setText("login failed");
            realName = "";
            realPassword = "";
            e.printStackTrace();
        }
        if (conn != null) {
            realName = name;
            realPassword = password;
            //hide the login window and show the main ui
            Platform.runLater(() -> {
                Stage mainActivityStage = null;
                try {
                    mainActivityStage = new Stage();
                    mainActivityStage.setTitle("TUM GUI Analyse Tool");
                    //  Parent root=FXMLLoader.load(getClass().getResource("../layout/main_activity_fxml.fxml"));
                    //mainActivityStage.setScene(new Scene(root));

                    //get all database and table
                    List<String> allDatabase = getAllDatabase();
                    HashMap<String, List<String>> content = new HashMap<>();
                    for (String s : allDatabase) {
                        content.put(s, getTableName(s));
                    }

                    BorderPane root = new BorderPane();
                    MainController mainController = new MainController(content);
                    mainController.setTopLayout(root);
                    mainController.setLeftLayout(root);
                    mainController.setCenter(root, null);
                    mainController.registerRoot(root);
                    mainController.registerStage(mainActivityStage);
                    mainActivityStage.setScene(new Scene(root, 1000, 600));


                } catch (Exception e) {
                    responseField.setText("Javafx open new Windows Exception");
                    e.printStackTrace();
                }
                mainActivityStage.show();
            });
            parentStage.hide();
        } else {
            realName = "";
            realPassword = "";
        }

    }

    private List<String> getTableName(String s) {
        Connection conn = null;
        List<String> tableNames = new ArrayList<String>();
        try {
            conn = sqlUtils.getMySqlConnection(s);
            Statement statement = conn.createStatement();
            String queryTable = "show tables";
            ResultSet rs = statement.executeQuery(queryTable);
            while (rs.next()) {
                String tableName = rs.getString(1);
                tableNames.add(tableName);
            }

            sqlUtils.closeConn(conn, statement, rs);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tableNames;
    }

    public static void registerStage(Stage primaryStage) {
        parentStage = primaryStage;
    }

    private List<String> getAllDatabase() {
        Connection conn = null;
        List<String> databaseName = new ArrayList<String>();
        try {
            conn = sqlUtils.getMySqlConnection("");
            Statement statement = conn.createStatement();
            String queryDatabases = "show databases";
            ResultSet rs = statement.executeQuery(queryDatabases);
            while (rs.next()) {
                String database = rs.getString(1);
                if ((!database.equals(ConstantValue.SYSTEM1)) && (!database.equals(ConstantValue.SYSTEM2)) && (!database.equals(ConstantValue.SYSTEM3)) && (!database.equals(ConstantValue.SYSTEM4))) {
                    databaseName.add(database);
                }
            }

            sqlUtils.closeConn(conn, statement, rs);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return databaseName;
    }
}