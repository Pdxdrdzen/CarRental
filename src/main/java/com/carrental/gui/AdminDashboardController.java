package com.carrental.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdminDashboardController {

    @FXML private Label totalVehiclesLabel;
    @FXML private Label activeReservationsLabel;
    @FXML private Label inServiceLabel;
    @FXML private Label totalClientsLabel;
    @FXML private Label loggedUserLabel;

    @FXML private TableView<ReservationRow> recentReservationsTable;
    @FXML private TableColumn<ReservationRow, String> colResId;
    @FXML private TableColumn<ReservationRow, String> colResClient;
    @FXML private TableColumn<ReservationRow, String> colResVehicle;
    @FXML private TableColumn<ReservationRow, String> colResDate;
    @FXML private TableColumn<ReservationRow, String> colResStatus;

    @FXML private Button logoutButton;
    @FXML private Button navVehicles;
    @FXML private Button navReservations;
    @FXML private Button navEmployees;
    @FXML private Button navService;
    @FXML private Button navReports;
    @FXML private Button seeAllReservationsButton;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadMetrics();
        loadRecentReservations();
        setupNavigation();
    }

    private void setupTableColumns() {
        colResId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().id()));
        colResClient.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().client()));
        colResVehicle.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().vehicle()));
        colResDate.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().date()));
        colResStatus.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().status()));
    }

    private void loadMetrics() {
        // TODO: change for HTTP in SpringBoot
        totalVehiclesLabel.setText("24");
        activeReservationsLabel.setText("8");
        inServiceLabel.setText("3");
        totalClientsLabel.setText("57");
    }

    private void loadRecentReservations() {
        // TODO: change for HTTP in SpringBoot
        ObservableList<ReservationRow> data = FXCollections.observableArrayList(
                new ReservationRow("1", "Jan Kowalski", "Toyota Corolla", "01.05 – 03.05", "CONFIRMED"),
                new ReservationRow("2", "Anna Nowak", "Kia Sportage", "02.05 – 05.05", "PENDING"),
                new ReservationRow("3", "Piotr Zając", "Skoda Octavia", "04.05 – 06.05", "COMPLETED")
        );
        recentReservationsTable.setItems(data);
    }

    private void setupNavigation() {
        navVehicles.setOnAction(e -> navigateTo("/fxml/vehicles-admin.fxml", "Pojazdy"));
        navReservations.setOnAction(e -> navigateTo("/fxml/reservations-admin.fxml", "Rezerwacje"));
        navEmployees.setOnAction(e -> navigateTo("/fxml/employees-admin.fxml", "Pracownicy"));
        seeAllReservationsButton.setOnAction(e -> navigateTo("/fxml/reservations-admin.fxml", "Rezerwacje"));
        logoutButton.setOnAction(e -> navigateTo("/fxml/admin-login.fxml", "Logowanie"));
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Record — for tables
    public record ReservationRow(
            String id,
            String client,
            String vehicle,
            String date,
            String status
    ) {}
}