package com.carrental.gui;

import com.carrental.entity.UserEntity;
import com.fasterxml.jackson.core.type.TypeReference;
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
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Wypełnij wszystkie pola!");
            return;
        }

        try {
            //Spring Boot: POST /api/users/login
            LoginRequest req = new LoginRequest(email, password);
            UserEntity user = ApiClient.post("/users/login", req, UserEntity.class);

            //Save logged user globally
            SessionManager.setCurrentUser(user);

            // Dependent on the role, forward
            String fxml = user.getRole().equals("ADMIN")
                    ? "/fxml/admin-view.fxml"
                    : "/fxml/vehicles.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.setTitle("Wypożyczalnia Samochodowa");

        } catch (Exception e) {
            errorLabel.setText("Nieprawidłowy email lub hasło");
            passwordField.clear();
        }
    }

    // DTO for login sendingg
    public record LoginRequest(String email, String password) {}
}