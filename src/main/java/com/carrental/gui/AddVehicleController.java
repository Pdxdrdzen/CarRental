package com.carrental.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashMap;
import java.util.Map;

public class AddVehicleController {

    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    @FXML private TextField priceField;
    @FXML private TextField plateField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Runnable onSaved;
    private String editId = null;

    @FXML
    public void initialize() {
        typeCombo.setItems(FXCollections.observableArrayList("CAR", "VAN", "BUS", "TRUCK", "MOTORCYCLE"));
        typeCombo.setValue("CAR");
        statusCombo.setItems(FXCollections.observableArrayList("AVAILABLE", "RENTED", "IN_SERVICE"));
        statusCombo.setValue("AVAILABLE");
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> cancelButton.getScene().getWindow().hide());
    }

    public void prefill(VehiclesAdminController.VehicleRow row) {
        editId = row.id();
        brandField.setText(row.brand());
        modelField.setText(row.model());
        priceField.setText(row.pricePerHour());
        typeCombo.setValue(row.type());
        statusCombo.setValue(row.status());
    }

    public void setOnSaved(Runnable callback) { this.onSaved = callback; }

    private void handleSave() {
        String brand = brandField.getText().trim();
        String model = modelField.getText().trim();
        String year  = yearField.getText().trim();
        String price = priceField.getText().trim();
        String plate = plateField.getText().trim();

        if (brand.isEmpty() || model.isEmpty() || price.isEmpty()) {
            errorLabel.setText("Wypełnij wymagane pola (marka, model, cena)!"); return;
        }
        try { Double.parseDouble(price); } catch (NumberFormatException ex) {
            errorLabel.setText("Cena musi być liczbą!"); return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("brand",        brand);
        body.put("model",        model);
        body.put("year",         year.isEmpty() ? null : Integer.parseInt(year));
        body.put("pricePerHour", Double.parseDouble(price));
        body.put("licensePlate", plate);
        body.put("type",         typeCombo.getValue());
        body.put("status",       statusCombo.getValue());

        saveButton.setDisable(true);
        new Thread(() -> {
            try {
                if (editId == null) {
                    ApiClient.post("/vehicles", body, Map.class);
                } else {
                    ApiClient.post("/vehicles/" + editId, body, Map.class); // PUT jeśli backend wymaga
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