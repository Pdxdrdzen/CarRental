package com.carrental.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.Map;

public class RegisterController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField phoneField;
    @FXML private Label errorLabel;
    @FXML private Button registerButton;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        registerButton.setOnAction(e -> handleRegister());
        backButton.setOnAction(e -> goToLogin());
    }

    private void handleRegister() {
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String email     = emailField.getText().trim();
        String password  = passwordField.getText();
        String confirm   = confirmPasswordField.getText();
        String phone     = phoneField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            errorLabel.setText("Wypełnij wszystkie pola!"); return;
        }
        if (!password.equals(confirm)) { errorLabel.setText("Hasła nie są zgodne!"); return; }
        if (!email.contains("@"))      { errorLabel.setText("Nieprawidłowy email!"); return; }

        registerButton.setDisable(true);
        new Thread(() -> {
            try {
                ApiClient.post("/users/register", Map.of(
                        "firstName", firstName, "lastName", lastName,
                        "email", email, "password", password,
                        "phoneNumber", phone, "role", "CLIENT"
                ), Map.class);
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.INFORMATION, "Konto utworzone! Możesz się zalogować.", ButtonType.OK).showAndWait();
                    goToLogin();
                });
            } catch (Exception e) {
                Platform.runLater(() -> { errorLabel.setText("Błąd: " + e.getMessage()); registerButton.setDisable(false); });
            }
            goToLogin();
        }).start();
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 500, 400));
            stage.setTitle("Logowanie");
        } catch (Exception e) { e.printStackTrace(); }
    }

}