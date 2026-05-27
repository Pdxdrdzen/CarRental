package com.carrental.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashMap;
import java.util.Map;

public class AddEmployeeController {

    @FXML private Label titleLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label passwordNote;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Runnable onSaved;
    private String editId = null;

    @FXML
    public void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList("ADMIN", "EMPLOYEE", "CLIENT"));
        roleCombo.setValue("EMPLOYEE");
        passwordNote.setVisible(false);
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> cancelButton.getScene().getWindow().hide());
    }

    public void prefill(Map<String, Object> data) {
        editId = String.valueOf(data.get("id"));
        titleLabel.setText("Edytuj pracownika");
        firstNameField.setText(String.valueOf(data.getOrDefault("firstName", "")));
        lastNameField.setText(String.valueOf(data.getOrDefault("lastName", "")));
        emailField.setText(String.valueOf(data.getOrDefault("email", "")));
        phoneField.setText(String.valueOf(data.getOrDefault("phoneNumber", "")));
        String role = String.valueOf(data.getOrDefault("role", "EMPLOYEE"));
        roleCombo.setValue(role);
        passwordNote.setVisible(true);
        passwordField.setPromptText("Zostaw puste = bez zmiany");
        confirmPasswordField.setPromptText("Zostaw puste = bez zmiany");
    }

    public void setOnSaved(Runnable callback) { this.onSaved = callback; }

    private void handleSave() {
        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();
        String email     = emailField.getText().trim();
        String phone     = phoneField.getText().trim();
        String password  = passwordField.getText();
        String confirm   = confirmPasswordField.getText();
        String role      = roleCombo.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            errorLabel.setText("Imię, nazwisko i email są wymagane!"); return;
        }
        if (!email.contains("@")) {
            errorLabel.setText("Nieprawidłowy adres email!"); return;
        }
        if (editId == null && password.isEmpty()) {
            errorLabel.setText("Hasło jest wymagane przy dodawaniu!"); return;
        }
        if (!password.isEmpty() && !password.equals(confirm)) {
            errorLabel.setText("Hasła nie są zgodne!"); return;
        }
        if (!password.isEmpty() && password.length() < 6) {
            errorLabel.setText("Hasło musi mieć co najmniej 6 znaków!"); return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("firstName",   firstName);
        body.put("lastName",    lastName);
        body.put("email",       email);
        body.put("phoneNumber", phone);
        body.put("role",        role);
        if (!password.isEmpty()) body.put("password", password);

        saveButton.setDisable(true);
        new Thread(() -> {
            try {
                if (editId == null) {
                    ApiClient.post("/users/register", body, Map.class);
                } else {
                    ApiClient.post("/users/" + editId, body, Map.class); // PUT jeśli backend wymaga
                }
                Platform.runLater(() -> { if (onSaved != null) onSaved.run(); });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    errorLabel.setText("Błąd: " + ex.getMessage());
                    saveButton.setDisable(false);
                });
            }
        }).start();
    }
}