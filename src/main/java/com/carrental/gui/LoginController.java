package com.carrental.gui;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.Map;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Wypełnij wszystkie pola!"); return;
        }
        try {
            JsonNode response = ApiClient.post("/users/login",
                    Map.of("email", email, "password", password), JsonNode.class);
            SessionManager.login(
                    response.get("id").asLong(),
                    email,
                    response.get("role").asText(),
                    response.get("firstName").asText("") + " " + response.get("lastName").asText("")
            );
            String fxml  = SessionManager.isAdmin() ? "/fxml/admin-dashboard.fxml" : "/fxml/main-view.fxml";
            String title = SessionManager.isAdmin() ? "Panel administratora" : "Panel użytkownika";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 1100, 700));
            stage.setTitle(title);
        } catch (Exception e) {
            errorLabel.setText("Nieprawidłowy email lub hasło");
            passwordField.clear();
            e.printStackTrace();
        }
    }

    @FXML
    private void openRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register-view.fxml"));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 700, 600));
            stage.setTitle("Rejestracja");
        } catch (Exception e) {
            errorLabel.setText("Nie można otworzyć rejestracji.");
            e.printStackTrace();
        }
    }
}