package com.clientsphere.crm;

import com.clientsphere.crm.config.MongoDBConnection;
import com.clientsphere.crm.view.LoginView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        LoginView loginView = new LoginView();

        Scene scene = new Scene(
                loginView.getRoot(),
                900,
                600
        );

        stage.setTitle("ClientSphere CRM - Login");

        stage.setScene(scene);

        stage.show();
    }

    @Override
    public void stop() {

        MongoDBConnection.closeConnection();
    }

    public static void main(String[] args) {

        launch(args);
    }
}