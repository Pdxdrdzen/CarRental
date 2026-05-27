package com.carrental.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class ReservationsAdminController {

    @FXML private TableView<Map<String, Object>> reservationsTable;
    @FXML private TableColumn<Map<String, Object>, String> colId;
    @FXML private TableColumn<Map<String, Object>, String> colClient;
    @FXML private TableColumn<Map<String, Object>, String> colVehicle;
    @FXML private TableColumn<Map<String, Object>, String> colStart;
    @FXML private TableColumn<Map<String, Object>, String> colEnd;
    @FXML private TableColumn<Map<String, Object>, String> colStatus;
    @FXML private TableColumn<Map<String, Object>, String> colCost;
    @FXML private TableColumn<Map<String, Object>, String> colActions;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Button backButton;

    private ObservableList<Map<String, Object>> allReservations;

    @FXML
    public void initialize() {
        setupColumns();
        setupFilters();
        loadReservations();
        backButton.setOnAction(e -> navigateTo("/fxml/admin-dashboard.fxml", "Dashboard"));
    }

    private void setupColumns() {
        colId.setCellValueFactory(d      -> sp(d.getValue(), "id"));
        colClient.setCellValueFactory(d  -> sp(d.getValue(), "clientName"));
        colVehicle.setCellValueFactory(d -> sp(d.getValue(), "vehicleName"));
        colStart.setCellValueFactory(d   -> sp(d.getValue(), "startDate"));
        colEnd.setCellValueFactory(d     -> sp(d.getValue(), "endDate"));
        colStatus.setCellValueFactory(d  -> sp(d.getValue(), "status"));
        colCost.setCellValueFactory(d    -> sp(d.getValue(), "totalCost"));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button cancelBtn = new Button("Anuluj");
            {
                cancelBtn.getStyleClass().add("secondary-button");
                cancelBtn.setOnAction(e -> {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Anulować rezerwację #" + row.get("id") + "?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(bt -> {
                        if (bt == ButtonType.YES) {
                            new Thread(() -> {
                                try {
                                    ApiClient.post("/reservations/" + row.get("id") + "/cancel", Map.of(), Map.class);
                                    Platform.runLater(ReservationsAdminController.this::loadReservations);
                                } catch (Exception ex) {
                                    Platform.runLater(() ->
                                            new Alert(Alert.AlertType.ERROR, "Błąd: " + ex.getMessage()).showAndWait());
                                }
                            }).start();
                        }
                    });
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : cancelBtn);
            }
        });
    }

    private javafx.beans.property.SimpleStringProperty sp(Map<String, Object> m, String key) {
        return new javafx.beans.property.SimpleStringProperty(String.valueOf(m.getOrDefault(key, "")));
    }

    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList(
                "Wszystkie", "PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"));
        statusFilter.setValue("Wszystkie");
        searchField.textProperty().addListener((o, old, v) -> applyFilters());
        statusFilter.valueProperty().addListener((o, old, v) -> applyFilters());
    }

    private void applyFilters() {
        String search = searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        FilteredList<Map<String, Object>> filtered = new FilteredList<>(allReservations, row -> {
            boolean ms = search.isEmpty()
                    || String.valueOf(row.getOrDefault("clientName","")).toLowerCase().contains(search)
                    || String.valueOf(row.getOrDefault("vehicleName","")).toLowerCase().contains(search);
            boolean mst = status == null || status.equals("Wszystkie")
                    || status.equals(String.valueOf(row.getOrDefault("status","")));
            return ms && mst;
        });
        reservationsTable.setItems(filtered);
    }

    public void loadReservations() {
        new Thread(() -> {
            try {
                List<Map<String, Object>> list = ApiClient.get("/reservations", ApiClient.listOf(Map.class));
                allReservations = FXCollections.observableArrayList(list);
                Platform.runLater(() -> reservationsTable.setItems(allReservations));
            } catch (Exception e) {
                Platform.runLater(() -> reservationsTable.setPlaceholder(
                        new Label("Błąd ładowania: " + e.getMessage())));
                e.printStackTrace();
            }
        }).start();
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
        } catch (Exception e) { e.printStackTrace(); }
    }
}