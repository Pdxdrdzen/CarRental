package com.carrental.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class ServiceAdminController {

    @FXML private TableView<Map<String, Object>> serviceTable;
    @FXML private TableColumn<Map<String, Object>, String> colId;
    @FXML private TableColumn<Map<String, Object>, String> colVehicle;
    @FXML private TableColumn<Map<String, Object>, String> colDescription;
    @FXML private TableColumn<Map<String, Object>, String> colStatus;
    @FXML private TableColumn<Map<String, Object>, String> colCreated;
    @FXML private TableColumn<Map<String, Object>, String> colActions;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Button addServiceButton;
    @FXML private Button backButton;

    @FXML private Label pendingCount;
    @FXML private Label inProgressCount;
    @FXML private Label resolvedCount;

    private ObservableList<Map<String, Object>> allRequests;

    @FXML
    public void initialize() {
        setupColumns();
        setupFilters();
        loadRequests();
        addServiceButton.setOnAction(e -> openAddServiceForm());
        backButton.setOnAction(e -> navigateTo("/fxml/admin-dashboard.fxml", "Dashboard"));
    }

    private void setupColumns() {
        colId.setCellValueFactory(d          -> sp(d.getValue(), "id"));
        colDescription.setCellValueFactory(d -> sp(d.getValue(), "description"));
        colStatus.setCellValueFactory(d      -> sp(d.getValue(), "status"));
        colCreated.setCellValueFactory(d     -> sp(d.getValue(), "createdAt"));

        colVehicle.setCellValueFactory(d -> {
            Object v = d.getValue().get("vehicle");
            if (v instanceof Map<?,?> vm) {
                return new javafx.beans.property.SimpleStringProperty(
                        vm.getOrDefault("brand","") + " " + vm.getOrDefault("model",""));
            }
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(v));
        });

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button resolveBtn = new Button("✔ Rozwiąż");
            private final Button deleteBtn  = new Button("Usuń");
            private final javafx.scene.layout.HBox box =
                    new javafx.scene.layout.HBox(6, resolveBtn, deleteBtn);
            {
                resolveBtn.getStyleClass().add("primary-button");
                deleteBtn.getStyleClass().add("secondary-button");

                resolveBtn.setOnAction(e -> {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    if ("RESOLVED".equals(row.get("status"))) return;
                    new Thread(() -> {
                        try {
                            ApiClient.patch("/service-requests/" + row.get("id") + "/resolve", null, Map.class);
                            Platform.runLater(ServiceAdminController.this::loadRequests);
                        } catch (Exception ex) {
                            Platform.runLater(() ->
                                    new Alert(Alert.AlertType.ERROR, "Błąd: " + ex.getMessage()).showAndWait());
                        }
                    }).start();
                });

                deleteBtn.setOnAction(e -> {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Usunąć zgłoszenie #" + row.get("id") + "?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(bt -> {
                        if (bt == ButtonType.YES) {
                            new Thread(() -> {
                                try {
                                    ApiClient.delete("/service-requests/" + row.get("id"));
                                    Platform.runLater(ServiceAdminController.this::loadRequests);
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
                setGraphic(empty ? null : box);
            }
        });
    }

    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList(
                "Wszystkie", "PENDING", "IN_PROGRESS", "RESOLVED"));
        statusFilter.setValue("Wszystkie");
        searchField.textProperty().addListener((o, old, v) -> applyFilters());
        statusFilter.valueProperty().addListener((o, old, v) -> applyFilters());
    }

    private void applyFilters() {
        if (allRequests == null) return;
        String search = searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        FilteredList<Map<String, Object>> filtered = new FilteredList<>(allRequests, row -> {
            String desc = String.valueOf(row.getOrDefault("description", "")).toLowerCase();
            Object v = row.get("vehicle");
            String vName = (v instanceof Map<?,?> vm)
                    ? (vm.getOrDefault("brand","") + " " + vm.getOrDefault("model","")).toLowerCase()
                    : "";
            boolean ms = search.isEmpty() || desc.contains(search) || vName.contains(search);
            boolean mst = status == null || status.equals("Wszystkie")
                    || status.equals(String.valueOf(row.getOrDefault("status","")));
            return ms && mst;
        });
        serviceTable.setItems(filtered);
        updateKpis();
    }

    public void loadRequests() {
        new Thread(() -> {
            try {
                List<Map<String, Object>> list =
                        ApiClient.get("/service-requests", ApiClient.listOf(Map.class));
                allRequests = FXCollections.observableArrayList(list);
                Platform.runLater(() -> {
                    serviceTable.setItems(allRequests);
                    applyFilters();
                });
            } catch (Exception e) {
                Platform.runLater(() ->
                        serviceTable.setPlaceholder(new Label("Błąd ładowania: " + e.getMessage())));
                e.printStackTrace();
            }
        }).start();
    }

    private void updateKpis() {
        if (allRequests == null) return;
        long pending    = allRequests.stream().filter(r -> "PENDING".equals(r.get("status"))).count();
        long inProgress = allRequests.stream().filter(r -> "IN_PROGRESS".equals(r.get("status"))).count();
        long resolved   = allRequests.stream().filter(r -> "RESOLVED".equals(r.get("status"))).count();
        pendingCount.setText(String.valueOf(pending));
        inProgressCount.setText(String.valueOf(inProgress));
        resolvedCount.setText(String.valueOf(resolved));
    }

    private void openAddServiceForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add-service.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(addServiceButton.getScene().getWindow());
            dialog.setTitle("Nowe zgłoszenie serwisowe");
            dialog.setScene(new Scene(loader.load(), 500, 360));
            AddServiceController ctrl = loader.getController();
            ctrl.setOnSaved(() -> { dialog.close(); loadRequests(); });
            dialog.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private javafx.beans.property.SimpleStringProperty sp(Map<String, Object> m, String key) {
        return new javafx.beans.property.SimpleStringProperty(String.valueOf(m.getOrDefault(key, "")));
    }

    private void navigateTo(String path, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
        } catch (Exception e) { e.printStackTrace(); }
    }
}