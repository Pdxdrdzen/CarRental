package com.carrental.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class UserDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label accountEmail;
    @FXML private Label accountRole;
    @FXML private Label statActive;
    @FXML private Label statCompleted;
    @FXML private Label statVehicles;
    @FXML private Button logoutButton;

    @FXML private TableView<Map<String, Object>> recentTable;
    @FXML private TableColumn<Map<String, Object>, String> colVehicle;
    @FXML private TableColumn<Map<String, Object>, String> colStart;
    @FXML private TableColumn<Map<String, Object>, String> colEnd;
    @FXML private TableColumn<Map<String, Object>, String> colCost;
    @FXML private TableColumn<Map<String, Object>, String> colStatus;

    @FXML
    public void initialize() {
        setupColumns();
        loadData();

        welcomeLabel.setText("Witaj, " + SessionManager.getUserFullName() + "!");
        accountEmail.setText("✉  " + SessionManager.getUserEmail());
        accountRole.setText("  " + SessionManager.getUserRole());

        logoutButton.setOnAction(e -> {
            SessionManager.clear();
            navigateTo("/fxml/login.fxml", "Logowanie", 600, 450);
        });
    }

    private void setupColumns() {
        colVehicle.setCellValueFactory(d -> sp(d.getValue(), "vehicleName"));
        colStart.setCellValueFactory(d   -> sp(d.getValue(), "startDate"));
        colEnd.setCellValueFactory(d     -> sp(d.getValue(), "endDate"));
        colCost.setCellValueFactory(d    -> sp(d.getValue(), "totalCost"));
        colStatus.setCellValueFactory(d  -> sp(d.getValue(), "status"));

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "CONFIRMED", "PENDING" -> setStyle("-fx-text-fill: #58d8ff; -fx-font-weight: 700;");
                    case "COMPLETED"            -> setStyle("-fx-text-fill: #6daa45; -fx-font-weight: 700;");
                    case "CANCELLED"            -> setStyle("-fx-text-fill: #ff8e8e; -fx-font-weight: 700;");
                    default                     -> setStyle("-fx-text-fill: #dff6ff;");
                }
            }
        });
    }

    private void loadData() {
        new Thread(() -> {
            try {
                // Rezerwacje użytkownika
                Long userId = SessionManager.getUserId();
                List<Map<String, Object>> reservations = ApiClient.get(
                        "/reservations/client/" + userId, ApiClient.listOf(Map.class));

                long active    = reservations.stream().filter(r -> {
                    String s = String.valueOf(r.getOrDefault("status", ""));
                    return s.equals("PENDING") || s.equals("CONFIRMED");
                }).count();
                long completed = reservations.stream()
                        .filter(r -> "COMPLETED".equals(r.getOrDefault("status", ""))).count();

                // Ostatnie 5
                List<Map<String, Object>> recent = reservations.stream()
                        .limit(5).toList();

                // Dostępne pojazdy
                List<Map<String, Object>> vehicles = ApiClient.get(
                        "/vehicles", ApiClient.listOf(Map.class));
                long available = vehicles.stream()
                        .filter(v -> "AVAILABLE".equals(v.getOrDefault("status", ""))).count();

                Platform.runLater(() -> {
                    statActive.setText(String.valueOf(active));
                    statCompleted.setText(String.valueOf(completed));
                    statVehicles.setText(String.valueOf(available));
                    recentTable.setItems(FXCollections.observableArrayList(recent));
                });

            } catch (Exception e) {
                Platform.runLater(() -> statActive.setText("błąd"));
                e.printStackTrace();
            }
        }).start();
    }

    private SimpleStringProperty sp(Map<String, Object> m, String key) {
        return new SimpleStringProperty(String.valueOf(m.getOrDefault(key, "")));
    }

    @FXML private void goToVehicles() {
        navigateTo("/fxml/vehicles.fxml", "Lista pojazdów", 900, 600);
    }

    @FXML private void goToNewReservation() {
        navigateTo("/fxml/reservation.fxml", "Nowa rezerwacja", 800, 650);
    }

    @FXML private void goToMyReservations() {
        navigateTo("/fxml/my-reservations.fxml", "Moje rezerwacje", 900, 600);
    }

    private void navigateTo(String fxml, String title, int w, int h) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), w, h));
            stage.setTitle(title);
        } catch (Exception e) { e.printStackTrace(); }
    }
}