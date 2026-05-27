package com.carrental.gui;

import com.carrental.entity.VehicleEntity;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.fasterxml.jackson.core.type.TypeReference;
import java.math.BigDecimal;
import java.util.List;

public class VehicleListController {

    @FXML private TableView<VehicleEntity> vehicleTable;
    @FXML private TableColumn<VehicleEntity, String> brandCol;
    @FXML private TableColumn<VehicleEntity, String> modelCol;
    @FXML private TableColumn<VehicleEntity, BigDecimal> priceCol;
    @FXML private TableColumn<VehicleEntity, String> statusCol;
    @FXML private TableColumn<VehicleEntity, String> typeCol;

    @FXML
    public void initialize() {
        brandCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getBrand()));
        modelCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getModel()));
        priceCol.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getPricePerHour()));
        statusCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus()));
        typeCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getType()));
        loadVehicles();
    }

    @FXML
    public void loadVehicles() {
        try {
            List<com.carrental.entity.VehicleEntity> vehicles = ApiClient.get(
                    "/vehicles",
                    new TypeReference<List<VehicleEntity>>() {}
            );
            vehicleTable.setItems(FXCollections.observableArrayList(vehicles));
        } catch (Exception e) {
            System.err.println("Błąd ładowania pojazdów: " + e.getMessage());
        }
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