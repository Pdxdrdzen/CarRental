package com.carrental.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class ReportsAdminController {

    @FXML private Label totalReservations;
    @FXML private Label confirmedReservations;
    @FXML private Label completedReservations;
    @FXML private Label cancelledReservations;

    @FXML private Label servicePending;
    @FXML private Label serviceResolved;

    @FXML private TableView<Map<String, Object>>           topVehiclesTable;
    @FXML private TableColumn<Map<String, Object>, String> colTopVehicle;
    @FXML private TableColumn<Map<String, Object>, String> colTopCount;

    @FXML private TableView<Map<String, Object>>           allReservationsTable;
    @FXML private TableColumn<Map<String, Object>, String> colResId;
    @FXML private TableColumn<Map<String, Object>, String> colResClient;
    @FXML private TableColumn<Map<String, Object>, String> colResVehicle;
    @FXML private TableColumn<Map<String, Object>, String> colResStart;
    @FXML private TableColumn<Map<String, Object>, String> colResEnd;
    @FXML private TableColumn<Map<String, Object>, String> colResStatus;
    @FXML private TableColumn<Map<String, Object>, String> colResAmount;

    @FXML private Button refreshButton;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        setupColumns();
        loadAll();
        refreshButton.setOnAction(e -> loadAll());
        backButton.setOnAction(e -> navigateTo("/fxml/admin-dashboard.fxml", "Dashboard"));
    }

    private void setupColumns() {
        colTopVehicle.setCellValueFactory(d -> {
            Object v = d.getValue().get("vehicle");
            if (v instanceof Map<?, ?> vm) {
                Object brand = vm.get("brand");
                Object model = vm.get("model");
                return sp(String.valueOf(brand != null ? brand : "") + " " +
                        String.valueOf(model != null ? model : ""));
            }
            return sp(String.valueOf(v));
        });
        colTopCount.setCellValueFactory(d -> sp(d.getValue(), "count"));

        colResId.setCellValueFactory(d     -> sp(d.getValue(), "id"));
        colResStart.setCellValueFactory(d  -> sp(d.getValue(), "startDate"));
        colResEnd.setCellValueFactory(d    -> sp(d.getValue(), "endDate"));
        colResStatus.setCellValueFactory(d -> sp(d.getValue(), "status"));

        colResClient.setCellValueFactory(d -> {
            Object c = d.getValue().get("client");
            if (c instanceof Map<?, ?> cm) {
                Object first = cm.get("firstName");
                Object last  = cm.get("lastName");
                return sp(String.valueOf(first != null ? first : "") + " " +
                        String.valueOf(last  != null ? last  : ""));
            }
            return sp(String.valueOf(c));
        });

        colResVehicle.setCellValueFactory(d -> {
            Object v = d.getValue().get("vehicle");
            if (v instanceof Map<?, ?> vm) {
                Object brand = vm.get("brand");
                Object model = vm.get("model");
                return sp(String.valueOf(brand != null ? brand : "") + " " +
                        String.valueOf(model != null ? model : ""));
            }
            return sp(String.valueOf(v));
        });

        colResAmount.setCellValueFactory(d -> {
            Object p = d.getValue().get("payment");
            if (p instanceof Map<?, ?> pm) {
                Object amount = pm.get("amount");
                return sp(String.valueOf(amount != null ? amount : "—") + " zł");
            }
            return sp("—");
        });
    }

    private void loadAll() {
        new Thread(() -> {
            try {
                List<Map<String, Object>> reservations =
                        ApiClient.get("/reservations", ApiClient.listOf(Map.class));

                long total     = reservations.size();
                long confirmed = reservations.stream().filter(r -> "CONFIRMED".equals(r.get("status"))).count();
                long completed = reservations.stream().filter(r -> "COMPLETED".equals(r.get("status"))).count();
                long cancelled = reservations.stream().filter(r -> "CANCELLED".equals(r.get("status"))).count();

                // Top pojazdy — zliczamy z listy rezerwacji
                Map<String, Long> vehicleCounts = new java.util.LinkedHashMap<>();
                Map<String, Map<String, Object>> vehicleObjects = new java.util.LinkedHashMap<>();
                for (Map<String, Object> res : reservations) {
                    Object v = res.get("vehicle");
                    if (v instanceof Map<?, ?> vm) {
                        Object brand = vm.get("brand");
                        Object model = vm.get("model");
                        String key = String.valueOf(brand != null ? brand : "") + " " +
                                String.valueOf(model != null ? model : "");
                        vehicleCounts.merge(key, 1L, Long::sum);
                        if (!vehicleObjects.containsKey(key)) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> vmCast = (Map<String, Object>) vm;
                            vehicleObjects.put(key, vmCast);
                        }
                    }
                }
                List<Map<String, Object>> topVehicles = vehicleCounts.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(5)
                        .map(e -> {
                            Map<String, Object> row = new java.util.HashMap<>();
                            row.put("vehicle", vehicleObjects.get(e.getKey()));
                            row.put("count", String.valueOf(e.getValue()));
                            return row;
                        })
                        .toList();

                // Serwis
                List<Map<String, Object>> serviceList =
                        ApiClient.get("/service-requests", ApiClient.listOf(Map.class));
                long sPending  = serviceList.stream().filter(r -> "PENDING".equals(r.get("status"))).count();
                long sResolved = serviceList.stream().filter(r -> "RESOLVED".equals(r.get("status"))).count();

                ObservableList<Map<String, Object>> resObs = FXCollections.observableArrayList(reservations);
                ObservableList<Map<String, Object>> topObs = FXCollections.observableArrayList(topVehicles);

                Platform.runLater(() -> {
                    totalReservations.setText(String.valueOf(total));
                    confirmedReservations.setText(String.valueOf(confirmed));
                    completedReservations.setText(String.valueOf(completed));
                    cancelledReservations.setText(String.valueOf(cancelled));
                    servicePending.setText(String.valueOf(sPending));
                    serviceResolved.setText(String.valueOf(sResolved));
                    allReservationsTable.setItems(resObs);
                    topVehiclesTable.setItems(topObs);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    totalReservations.setText("Err");
                    allReservationsTable.setPlaceholder(
                            new Label("Błąd ładowania: " + e.getMessage()));
                });
                e.printStackTrace();
            }
        }).start();
    }

    private javafx.beans.property.SimpleStringProperty sp(Map<String, Object> m, String key) {
        Object val = m.get(key);
        return new javafx.beans.property.SimpleStringProperty(
                String.valueOf(val != null ? val : ""));
    }

    private javafx.beans.property.SimpleStringProperty sp(String value) {
        return new javafx.beans.property.SimpleStringProperty(value != null ? value : "");
    }

    private void navigateTo(String path, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}