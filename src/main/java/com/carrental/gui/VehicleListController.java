package com.carrental.gui;

import com.carrental.model.vehicle.Vehicle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

public class VehicleListController {

    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> brandCol;
    @FXML private TableColumn<Vehicle, String> modelCol;
    @FXML private TableColumn<Vehicle, Double> priceCol;
    @FXML private TableColumn<Vehicle, String> statusCol;
    @FXML private TableColumn<Vehicle, String> typeCol;

    @FXML
    public void initialize() {
        brandCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getBrand()));
        modelCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getModel()));
        priceCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPricePerHour()));
        statusCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus().name()));
        typeCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getType().name()));

        loadVehicles();
    }

    @FXML
    public void loadVehicles() {
        // Docelowo: wywołanie HTTP GET /api/vehicles do Spring Boot
        // Na razie testowe dane
        List<Vehicle> testData = List.of(
                new com.carrental.model.vehicle.Car.Builder()
                        .id(1L).brand("Toyota").model("Corolla")
                        .pricePerHour(50.0)
                        .status(com.carrental.model.vehicle.VehicleStatus.AVAILABLE)
                        .numberOfSeats(5).build(),
                new com.carrental.model.vehicle.Car.Builder()
                        .id(2L).brand("BMW").model("X5")
                        .pricePerHour(120.0)
                        .status(com.carrental.model.vehicle.VehicleStatus.RENTED)
                        .numberOfSeats(5).build()
        );
        vehicleTable.setItems(FXCollections.observableArrayList(testData));
    }

    @FXML
    public void openReservation() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/reservation.fxml")
        );
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load(), 500, 400));
        stage.setTitle("Nowa rezerwacja");
        stage.show();
    }
}