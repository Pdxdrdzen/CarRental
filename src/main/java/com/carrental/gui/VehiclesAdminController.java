package com.carrental.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

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

        addVehicleButton.setOnAction(e -> openAddVehicleDialog());
        backButton.setOnAction(e -> navigateTo("/fxml/admin-dashboard.fxml", "Dashboard"));
    }

    private void setupColumns() {
        colId.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().id()));
        colBrand.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().brand()));
        colModel.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().model()));
        colType.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().type()));
        colPrice.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().pricePerHour() + " zł/h"));
        colStatus.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().status()));

        // Action columns: Edytuj/Usuń screens in each row
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edytuj");
            private final Button deleteBtn = new Button("Usuń");
            private final javafx.scene.layout.HBox box =
                    new javafx.scene.layout.HBox(6, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().addAll("neon-button", "ghost-button");
                deleteBtn.getStyleClass().addAll("neon-button", "secondary-button");

                editBtn.setOnAction(e -> {
                    VehicleRow row = getTableView().getItems().get(getIndex());
                    System.out.println("Edytuj: " + row.brand() + " " + row.model());
                    // TODO: open editor dialogue
                });

                deleteBtn.setOnAction(e -> {
                    VehicleRow row = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Usunąć pojazd " + row.brand() + " " + row.model() + "?",
                            ButtonType.YES, ButtonType.NO);
                    alert.showAndWait().ifPresent(bt -> {
                        if (bt == ButtonType.YES) {
                            allVehicles.remove(row);
                            // TODO: DELETE to Spring Boot
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
                "Wszystkie", "AVAILABLE", "RENTED", "IN_SERVICE"
        ));
        statusFilter.setValue("Wszystkie");

        // Live filter
        searchField.textProperty().addListener((obs, old, val) -> applyFilters());
        statusFilter.valueProperty().addListener((obs, old, val) -> applyFilters());
    }

    private void applyFilters() {
        String search = searchField.getText().toLowerCase();
        String status = statusFilter.getValue();

        FilteredList<VehicleRow> filtered = new FilteredList<>(allVehicles, row -> {
            boolean matchesSearch = search.isEmpty()
                    || row.brand().toLowerCase().contains(search)
                    || row.model().toLowerCase().contains(search);
            boolean matchesStatus = status == null
                    || status.equals("Wszystkie")
                    || row.status().equals(status);
            return matchesSearch && matchesStatus;
        });

        vehiclesTable.setItems(filtered);
    }

    private void loadVehicles() {
        // TODO: change for HTTP GET /api/vehicles
        allVehicles = FXCollections.observableArrayList(
                new VehicleRow("1", "Toyota", "Corolla", "CAR", "12.50", "AVAILABLE"),
                new VehicleRow("2", "Skoda", "Octavia", "CAR", "14.00", "RENTED"),
                new VehicleRow("3", "Kia", "Sportage", "CAR", "19.90", "AVAILABLE"),
                new VehicleRow("4", "Mercedes", "Sprinter", "BUS", "35.00", "IN_SERVICE")
        );
        vehiclesTable.setItems(allVehicles);
    }

    private void openAddVehicleDialog() {
        // TODO: open dialo or new screen for adding new vehicle
        System.out.println("Otwieranie formularza dodawania pojazdu...");
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public record VehicleRow(
            String id,
            String brand,
            String model,
            String type,
            String pricePerHour,
            String status
    ) {}
}