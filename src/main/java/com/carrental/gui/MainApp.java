package com.carrental.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application{

    @Override
    public void start(Stage stage){
        FXMLLoader loader=new FXMLLoader(
                getClass().getResource("/fxml/admin-login.fxml")
        );
        try{
            Scene scene=new Scene(loader.load(),1180,720);
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
