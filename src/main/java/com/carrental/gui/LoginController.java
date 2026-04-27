package com.carrental.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() throws Exception {
        String email = emailField.getText();
        String password = passwordField.getText();

        if(email.isEmpty()||password.isEmpty()){
            errorLabel.setText("Wypelnij wszystkie pola!");
            return;
        }
        // temp hardcoded login(should be http request from spring)
        if (email.equals("admin@car.pl") && password.equals("admin")) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/vehicles.fxml")
                );
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(new Scene(loader.load(), 900, 600));
                stage.setTitle("Lista pojazdów");
            }catch (Exception e) {
                errorLabel.setText("Error ladowania ekranu");
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Nieprawidłowy email lub hasło");
            passwordField.clear();
        }
    }
}