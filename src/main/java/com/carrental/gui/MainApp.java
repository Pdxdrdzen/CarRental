package com.carrental.gui;

import io.micrometer.observation.annotation.ObservationKeyValue;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOError;
import java.io.IOException;
import java.util.logging.Logger;

public class MainApp extends Application{

    @Override
    public void start(Stage stage){
        FXMLLoader loader=new FXMLLoader(
                getClass().getResource("/fxml/login.fxml")
        );
        try{
            Scene scene=new Scene(loader.load(),400,300);
            stage.setTitle("Wypozyczalnia Samochodowa - panel logowania");
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println("Error: Exception: ");
        }



    }
    public static void main(String[] args){
        launch(args);
    }
}
