package com.clientsphere.crm;

import com.clientsphere.crm.config.MongoDBConnection;
import com.clientsphere.crm.view.MainLayout;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        MainLayout mainLayout = new MainLayout();

        Scene scene = new Scene(
                mainLayout.getRoot(),
                1100,
                700
        );

        stage.setTitle("ClientSphere CRM");

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