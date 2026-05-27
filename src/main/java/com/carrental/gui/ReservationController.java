package com.carrental.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationController {

    @FXML private ComboBox<String> vehicleCombo;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextArea notesArea;
    @FXML private Label totalCostLabel;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private List<Map<String, Object>> availableVehicles;

    @FXML
    public void initialize() {
        paymentMethodCombo.setItems(FXCollections.observableArrayList("CARD", "CASH", "TRANSFER"));
        paymentMethodCombo.setValue("CARD");
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(1));
        startDatePicker.valueProperty().addListener((o, old, val) -> recalculateCost());
        endDatePicker.valueProperty().addListener((o, old, val) -> recalculateCost());
        vehicleCombo.valueProperty().addListener((o, old, val) -> recalculateCost());
        cancelButton.setOnAction(e -> goBack());
        saveButton.setOnAction(e -> handleSave());
        loadVehicles();
    }

    private void loadVehicles() {
        new Thread(() -> {
            try {
                availableVehicles = ApiClient.get("/vehicles", ApiClient.listOf(Map.class));
                List<String> labels = availableVehicles.stream()
                        .filter(v -> "AVAILABLE".equals(v.get("status")))
                        .map(v -> v.get("id") + " | " + v.get("brand") + " " + v.get("model")
                                + " (" + v.get("pricePerHour") + " zł/h)")
                        .toList();
                Platform.runLater(() -> vehicleCombo.setItems(FXCollections.observableArrayList(labels)));
            } catch (Exception e) {
                Platform.runLater(() -> errorLabel.setText("Błąd ładowania pojazdów: " + e.getMessage()));
            }
        }).start();
    }

    private void recalculateCost() {
        if (vehicleCombo.getValue() == null || startDatePicker.getValue() == null || endDatePicker.getValue() == null) return;
        LocalDate start = startDatePicker.getValue();
        LocalDate end   = endDatePicker.getValue();
        if (!end.isAfter(start)) { totalCostLabel.setText("Błędny zakres dat"); return; }
        String selected = vehicleCombo.getValue();
        long vehicleId = Long.parseLong(selected.split("\\|")[0].trim());
        availableVehicles.stream()
                .filter(v -> vehicleId == ((Number) v.get("id")).longValue())
                .findFirst().ifPresent(v -> {
                    double price = ((Number) v.get("pricePerHour")).doubleValue();
                    long days = start.until(end).getDays();
                    totalCostLabel.setText(String.format("Szacowany koszt: %.2f zł", price * days * 24));
                });
    }

    private void handleSave() {
        errorLabel.setText("");
        if (vehicleCombo.getValue() == null)        { errorLabel.setText("Wybierz pojazd!"); return; }
        if (paymentMethodCombo.getValue() == null)  { errorLabel.setText("Wybierz metodę płatności!"); return; }
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) { errorLabel.setText("Podaj daty!"); return; }
        if (!endDatePicker.getValue().isAfter(startDatePicker.getValue())) { errorLabel.setText("Data końca musi być po dacie startu!"); return; }

        long vehicleId = Long.parseLong(vehicleCombo.getValue().split("\\|")[0].trim());
        Map<String, Object> body = new HashMap<>();
        body.put("vehicleId",     vehicleId);
        body.put("userId",        SessionManager.getUserId());
        body.put("startDate",     startDatePicker.getValue().toString());
        body.put("endDate",       endDatePicker.getValue().toString());
        body.put("paymentMethod", paymentMethodCombo.getValue());
        body.put("notes",         notesArea.getText());

        saveButton.setDisable(true);
        new Thread(() -> {
            try {
                ApiClient.post("/reservations", body, Map.class);
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.INFORMATION, "Rezerwacja zapisana!", ButtonType.OK).showAndWait();
                    goBack();
                });
            } catch (Exception e) {
                Platform.runLater(() -> { errorLabel.setText("Błąd zapisu: " + e.getMessage()); saveButton.setDisable(false); });
            }
        }).start();
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vehicles.fxml"));
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.setTitle("Lista pojazdów");
        } catch (Exception e) { e.printStackTrace(); }
    }
}