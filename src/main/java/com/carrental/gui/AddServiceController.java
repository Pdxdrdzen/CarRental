package com.carrental.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Map;

public class AddServiceController {

    @FXML private ComboBox<VehicleItem> vehicleCombo;
    @FXML private TextArea descriptionArea;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Runnable onSaved;

    @FXML
    public void initialize() {
        loadVehicles();
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> cancelButton.getScene().getWindow().hide());
    }

    private void loadVehicles() {
        new Thread(() -> {
            try {
                List<Map<String, Object>> list =
                        ApiClient.get("/vehicles", ApiClient.listOf(Map.class));
                List<VehicleItem> items = list.stream()
                        .map(v -> new VehicleItem(
                                String.valueOf(v.get("id")),
                                v.getOrDefault("brand","") + " " + v.getOrDefault("model","")))
                        .toList();
                Platform.runLater(() ->
                        vehicleCombo.setItems(FXCollections.observableArrayList(items)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void setOnSaved(Runnable callback) { this.onSaved = callback; }

    private void handleSave() {
        VehicleItem selected = vehicleCombo.getValue();
        String desc = descriptionArea.getText().trim();
        if (selected == null)   { errorLabel.setText("Wybierz pojazd!"); return; }
        if (desc.isEmpty())     { errorLabel.setText("Opis jest wymagany!"); return; }

        saveButton.setDisable(true);
        new Thread(() -> {
            try {
                // POST /api/service-requests?vehicleId=1&description=...
                ApiClient.postParams("/service-requests",
                        Map.of("vehicleId", selected.id(), "description", desc), Map.class);
                Platform.runLater(() -> { if (onSaved != null) onSaved.run(); });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    errorLabel.setText("Błąd: " + ex.getMessage());
                    saveButton.setDisable(false);
                });
            }
        }).start();
    }

    public record VehicleItem(String id, String label) {
        @Override public String toString() { return label; }
    }
}