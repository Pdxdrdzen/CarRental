package com.carrental.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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

public class MyReservationsController {

    @FXML private TableView<Map<String, Object>> reservationsTable;
    @FXML private TableColumn<Map<String, Object>, String> colId;
    @FXML private TableColumn<Map<String, Object>, String> colVehicle;
    @FXML private TableColumn<Map<String, Object>, String> colStart;
    @FXML private TableColumn<Map<String, Object>, String> colEnd;
    @FXML private TableColumn<Map<String, Object>, String> colCost;
    @FXML private TableColumn<Map<String, Object>, String> colStatus;
    @FXML private TableColumn<Map<String, Object>, String> colActions;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Button backButton;
    @FXML private Label activeCount;
    @FXML private Label completedCount;
    @FXML private Label cancelledCount;

    private ObservableList<Map<String, Object>> allReservations;

    @FXML
    public void initialize() {
        setupColumns();
        setupFilters();
        loadReservations();
        backButton.setOnAction(e -> navigateTo("/fxml/main-view.fxml", "CarRental"));
    }

    private void setupColumns() {
        colId.setCellValueFactory(d      -> sp(d.getValue(), "id"));
        colVehicle.setCellValueFactory(d -> sp(d.getValue(), "vehicleName"));
        colStart.setCellValueFactory(d   -> sp(d.getValue(), "startDate"));
        colEnd.setCellValueFactory(d     -> sp(d.getValue(), "endDate"));
        colCost.setCellValueFactory(d    -> sp(d.getValue(), "totalCost"));
        colStatus.setCellValueFactory(d  -> sp(d.getValue(), "status"));

        // Kolorowanie statusu
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

        // Przycisk Anuluj
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button cancelBtn = new Button("Anuluj rezerwację");
            {
                cancelBtn.getStyleClass().add("secondary-button");
                cancelBtn.setOnAction(e -> {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Czy na pewno chcesz anulować rezerwację #" + row.get("id") + "?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Potwierdzenie");
                    confirm.showAndWait().ifPresent(bt -> {
                        if (bt == ButtonType.YES) {
                            new Thread(() -> {
                                try {
                                    ApiClient.post("/reservations/" + row.get("id") + "/cancel",
                                            Map.of(), Map.class);
                                    Platform.runLater(MyReservationsController.this::loadReservations);
                                } catch (Exception ex) {
                                    Platform.runLater(() ->
                                            new Alert(Alert.AlertType.ERROR,
                                                    "Błąd: " + ex.getMessage()).showAndWait());
                                }
                            }).start();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Map<String, Object> row = getTableView().getItems().get(getIndex());
                String status = String.valueOf(row.getOrDefault("status", ""));
                boolean canCancel = status.equals("PENDING") || status.equals("CONFIRMED");
                setGraphic(canCancel ? cancelBtn : null);
            }
        });
    }

    private SimpleStringProperty sp(Map<String, Object> m, String key) {
        return new SimpleStringProperty(String.valueOf(m.getOrDefault(key, "")));
    }

    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList(
                "Wszystkie", "PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"));
        statusFilter.setValue("Wszystkie");
        searchField.textProperty().addListener((o, old, v) -> applyFilters());
        statusFilter.valueProperty().addListener((o, old, v) -> applyFilters());
    }

    private void applyFilters() {
        if (allReservations == null) return;
        String search = searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        FilteredList<Map<String, Object>> filtered = new FilteredList<>(allReservations, row -> {
            boolean matchSearch = search.isEmpty()
                    || String.valueOf(row.getOrDefault("vehicleName", "")).toLowerCase().contains(search);
            boolean matchStatus = status == null || status.equals("Wszystkie")
                    || status.equals(String.valueOf(row.getOrDefault("status", "")));
            return matchSearch && matchStatus;
        });
        reservationsTable.setItems(filtered);
    }

    private void loadReservations() {
        new Thread(() -> {
            try {
                Long userId = SessionManager.getUserId();
                List<Map<String, Object>> list = ApiClient.get(
                        "/reservations/client/" + userId, ApiClient.listOf(Map.class));
                allReservations = FXCollections.observableArrayList(list);

                long active    = list.stream().filter(r -> {
                    String s = String.valueOf(r.getOrDefault("status", ""));
                    return s.equals("PENDING") || s.equals("CONFIRMED");
                }).count();
                long completed = list.stream().filter(r ->
                        "COMPLETED".equals(r.getOrDefault("status", ""))).count();
                long cancelled = list.stream().filter(r ->
                        "CANCELLED".equals(r.getOrDefault("status", ""))).count();

                Platform.runLater(() -> {
                    reservationsTable.setItems(allReservations);
                    activeCount.setText(String.valueOf(active));
                    completedCount.setText(String.valueOf(completed));
                    cancelledCount.setText(String.valueOf(cancelled));
                });
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
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.setTitle(title);
        } catch (Exception e) { e.printStackTrace(); }
    }
}