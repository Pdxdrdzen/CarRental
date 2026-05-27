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

public class VehiclesAdminController {

    @FXML private TableView<VehicleRow> vehiclesTable;
    @FXML private TableColumn<VehicleRow, String> colId;
    @FXML private TableColumn<VehicleRow, String> colBrand;
    @FXML private TableColumn<VehicleRow, String> colModel;
    @FXML private TableColumn<VehicleRow, String> colType;
    @FXML private TableColumn<VehicleRow, String> colPrice;
    @FXML private TableColumn<VehicleRow, String> colStatus;
    @FXML private TableColumn<VehicleRow, String> colActions;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Button addVehicleButton;
    @FXML private Button backButton;

    private ObservableList<VehicleRow> allVehicles;

    @FXML
    public void initialize() {
        setupColumns();
        setupFilters();
        loadVehicles();
        addVehicleButton.setOnAction(e -> openAddVehicleForm(null));
        backButton.setOnAction(e -> navigateTo("/fxml/admin-dashboard.fxml", "Dashboard"));
    }

    private void setupColumns() {
        colId.setCellValueFactory(d    -> new javafx.beans.property.SimpleStringProperty(d.getValue().id()));
        colBrand.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().brand()));
        colModel.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().model()));
        colType.setCellValueFactory(d  -> new javafx.beans.property.SimpleStringProperty(d.getValue().type()));
        colPrice.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().pricePerHour() + " zł/h"));
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().status()));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn   = new Button("Edytuj");
            private final Button deleteBtn = new Button("Usuń");
            private final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(6, editBtn, deleteBtn);
            {
                editBtn.getStyleClass().add("secondary-button");
                deleteBtn.getStyleClass().add("secondary-button");

                editBtn.setOnAction(e -> {
                    VehicleRow row = getTableView().getItems().get(getIndex());
                    openAddVehicleForm(row);
                });

                deleteBtn.setOnAction(e -> {
                    VehicleRow row = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Usunąć " + row.brand() + " " + row.model() + "?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(bt -> {
                        if (bt == ButtonType.YES) {
                            new Thread(() -> {
                                try {
                                    ApiClient.delete("/vehicles/" + row.id());
                                    Platform.runLater(() -> {
                                        allVehicles.remove(row);
                                        applyFilters();
                                    });
                                } catch (Exception ex) {
                                    Platform.runLater(() ->
                                            new Alert(Alert.AlertType.ERROR, "Błąd usuwania: " + ex.getMessage()).showAndWait());
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
        statusFilter.setItems(FXCollections.observableArrayList("Wszystkie", "AVAILABLE", "RENTED", "IN_SERVICE"));
        statusFilter.setValue("Wszystkie");
        searchField.textProperty().addListener((o, old, val) -> applyFilters());
        statusFilter.valueProperty().addListener((o, old, val) -> applyFilters());
    }

    private void applyFilters() {
        String search = searchField.getText().toLowerCase();
        String status = statusFilter.getValue();
        FilteredList<VehicleRow> filtered = new FilteredList<>(allVehicles, row -> {
            boolean matchSearch = search.isEmpty()
                    || row.brand().toLowerCase().contains(search)
                    || row.model().toLowerCase().contains(search);
            boolean matchStatus = status == null || status.equals("Wszystkie") || row.status().equals(status);
            return matchSearch && matchStatus;
        });
        vehiclesTable.setItems(filtered);
    }

    public void loadVehicles() {
        new Thread(() -> {
            try {
                List<Map<String, Object>> vehicles = ApiClient.get("/vehicles", ApiClient.listOf(Map.class));
                ObservableList<VehicleRow> rows = FXCollections.observableArrayList();
                for (Map<String, Object> v : vehicles) {
                    rows.add(new VehicleRow(
                            String.valueOf(v.get("id")),
                            String.valueOf(v.getOrDefault("brand", "")),
                            String.valueOf(v.getOrDefault("model", "")),
                            String.valueOf(v.getOrDefault("type", "")),
                            String.valueOf(v.getOrDefault("pricePerHour", "0")),
                            String.valueOf(v.getOrDefault("status", ""))
                    ));
                }
                allVehicles = rows;
                Platform.runLater(() -> vehiclesTable.setItems(allVehicles));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void openAddVehicleForm(VehicleRow editRow) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add-vehicle.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(addVehicleButton.getScene().getWindow());
            dialog.setTitle(editRow == null ? "Dodaj pojazd" : "Edytuj pojazd");
            dialog.setScene(new Scene(loader.load(), 520, 480));
            AddVehicleController ctrl = loader.getController();
            if (editRow != null) ctrl.prefill(editRow);
            ctrl.setOnSaved(() -> {
                dialog.close();
                loadVehicles();
            });
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public record VehicleRow(String id, String brand, String model, String type, String pricePerHour, String status) {}
}