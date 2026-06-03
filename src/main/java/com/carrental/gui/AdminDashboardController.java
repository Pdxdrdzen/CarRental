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
        if (SessionManager.isLoggedIn()) {
            loggedUserLabel.setText("👤 " + SessionManager.getUserFullName());
        }
    }


    private void setupTableColumns() {
        colResId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().id()));
        colResClient.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().client()));
        colResVehicle.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().vehicle()));
        colResDate.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().date()));
        colResStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().status()));
    }

    private void loadMetrics() {
        // TODO: zamienić na HTTP GET
        totalVehiclesLabel.setText("24");
        activeReservationsLabel.setText("8");
        inServiceLabel.setText("3");
        totalClientsLabel.setText("57");
    }

    private void loadRecentReservations() {
        // TODO: zamienić na HTTP GET
        ObservableList<ReservationRow> data = FXCollections.observableArrayList(
                new ReservationRow("1", "Jan Kowalski",  "Toyota Corolla", "01.05 – 03.05", "CONFIRMED"),
                new ReservationRow("2", "Anna Nowak",    "Kia Sportage",   "02.05 – 05.05", "PENDING"),
                new ReservationRow("3", "Piotr Zając",   "Skoda Octavia",  "04.05 – 06.05", "COMPLETED")
        );
        recentReservationsTable.setItems(data);
    }

    private void setupNavigation() {
        navVehicles.setOnAction(e          -> navigateTo("/fxml/vehicles-admin.fxml",      "Pojazdy"));
        navReservations.setOnAction(e      -> navigateTo("/fxml/reservations-admin.fxml",  "Rezerwacje"));
        navEmployees.setOnAction(e         -> navigateTo("/fxml/employees-admin.fxml",     "Pracownicy"));
        navService.setOnAction(e  -> navigateTo("/fxml/service-admin.fxml",  "Serwis"));
        navReports.setOnAction(e  -> navigateTo("/fxml/reports-admin.fxml",  "Raporty"));
        seeAllReservationsButton.setOnAction(e -> navigateTo("/fxml/reservations-admin.fxml", "Rezerwacje"));
        logoutButton.setOnAction(e -> {
            SessionManager.logout();
            navigateTo("/fxml/login.fxml", "Logowanie");
        });
    }

    private void showPlaceholder(String name) {
        new Alert(Alert.AlertType.INFORMATION,
                "Moduł \"" + name + "\" jest w przygotowaniu.", ButtonType.OK).showAndWait();
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

    public record ReservationRow(String id, String client, String vehicle, String date, String status) {}
}