package application;

import database.JDBC;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.*;

public class Main extends Application {

    /**
     * Starts and loads the login form.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Login.fxml")));
        primaryStage.setTitle("WGU SoftwareII Final: C195");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    /**
     * Main function starting applicaiton.
     */
    public static void main(String[] args) throws SQLException {
        JDBC.makeConnection();
        launch(args);
        JDBC.closeConnection();
    }
}
