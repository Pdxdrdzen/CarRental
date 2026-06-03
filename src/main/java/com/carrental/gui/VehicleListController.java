package com.carrental.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;
import java.util.Map;

public class VehicleListController {

    @FXML private TableView<Map<String, Object>> vehicleTable;
    @FXML private TableColumn<Map<String, Object>, String> brandCol;
    @FXML private TableColumn<Map<String, Object>, String> modelCol;
    @FXML private TableColumn<Map<String, Object>, String> priceCol;
    @FXML private TableColumn<Map<String, Object>, String> statusCol;
    @FXML private TableColumn<Map<String, Object>, String> typeCol;

    @FXML
    public void initialize() {
        brandCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getOrDefault("brand", ""))));
        modelCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getOrDefault("model", ""))));
        priceCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getOrDefault("pricePerHour", "") + " zł/h"));
        statusCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getOrDefault("status", ""))));
        typeCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getOrDefault("type", ""))));
        loadVehicles();
    }

    @FXML
    public void loadVehicles() {
        new Thread(() -> {
            try {
                List<Map<String, Object>> vehicles = ApiClient.get("/vehicles", ApiClient.listOf(Map.class));
                Platform.runLater(() -> vehicleTable.setItems(FXCollections.observableArrayList(vehicles)));
            } catch (Exception e) {
                Platform.runLater(() -> vehicleTable.setPlaceholder(new Label("Błąd ładowania: " + e.getMessage())));
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void openReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reservation.fxml"));
            Stage stage = (Stage) vehicleTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 800, 650));
            stage.setTitle("Nowa rezerwacja");
        } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-view.fxml"));
            Stage stage = (Stage) vehicleTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 1100, 700));
            stage.setTitle("Panel użytkownika");
        } catch (Exception e) { e.printStackTrace(); }
    }
}