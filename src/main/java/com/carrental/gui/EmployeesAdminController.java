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

public class EmployeesAdminController {

    @FXML private TableView<Map<String, Object>> employeesTable;
    @FXML private TableColumn<Map<String, Object>, String> colId;
    @FXML private TableColumn<Map<String, Object>, String> colFirstName;
    @FXML private TableColumn<Map<String, Object>, String> colLastName;
    @FXML private TableColumn<Map<String, Object>, String> colEmail;
    @FXML private TableColumn<Map<String, Object>, String> colPhone;
    @FXML private TableColumn<Map<String, Object>, String> colRole;
    @FXML private TableColumn<Map<String, Object>, String> colActions;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilter;
    @FXML private Button addEmployeeButton;
    @FXML private Button backButton;

    private ObservableList<Map<String, Object>> allEmployees;

    @FXML
    public void initialize() {
        setupColumns();
        setupFilters();
        loadEmployees();
        addEmployeeButton.setOnAction(e -> openEmployeeForm(null));
        backButton.setOnAction(e -> navigateTo("/fxml/admin-dashboard.fxml", "Dashboard"));
    }

    private void setupColumns() {
        colId.setCellValueFactory(d        -> sp(d.getValue(), "id"));
        colFirstName.setCellValueFactory(d -> sp(d.getValue(), "firstName"));
        colLastName.setCellValueFactory(d  -> sp(d.getValue(), "lastName"));
        colEmail.setCellValueFactory(d     -> sp(d.getValue(), "email"));
        colPhone.setCellValueFactory(d     -> sp(d.getValue(), "phoneNumber"));
        colRole.setCellValueFactory(d      -> sp(d.getValue(), "role"));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn   = new Button("Edytuj");
            private final Button deleteBtn = new Button("Usuń");
            private final javafx.scene.layout.HBox box =
                    new javafx.scene.layout.HBox(6, editBtn, deleteBtn);
            {
                editBtn.getStyleClass().add("secondary-button");
                deleteBtn.getStyleClass().add("secondary-button");

                editBtn.setOnAction(e -> {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    openEmployeeForm(row);
                });

                deleteBtn.setOnAction(e -> {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    String name = row.getOrDefault("firstName","") + " " + row.getOrDefault("lastName","");
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Usunąć pracownika " + name + "?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(bt -> {
                        if (bt == ButtonType.YES) {
                            new Thread(() -> {
                                try {
                                    ApiClient.delete("/users/" + row.get("id"));
                                    Platform.runLater(EmployeesAdminController.this::loadEmployees);
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
        roleFilter.setItems(FXCollections.observableArrayList(
                "Wszyscy", "ADMIN", "EMPLOYEE", "CLIENT"));
        roleFilter.setValue("Wszyscy");
        searchField.textProperty().addListener((o, old, v) -> applyFilters());
        roleFilter.valueProperty().addListener((o, old, v) -> applyFilters());
    }

    private void applyFilters() {
        if (allEmployees == null) return;
        String search = searchField.getText().toLowerCase();
        String role   = roleFilter.getValue();
        FilteredList<Map<String, Object>> filtered = new FilteredList<>(allEmployees, row -> {
            boolean ms = search.isEmpty()
                    || String.valueOf(row.getOrDefault("firstName","")).toLowerCase().contains(search)
                    || String.valueOf(row.getOrDefault("lastName","")).toLowerCase().contains(search)
                    || String.valueOf(row.getOrDefault("email","")).toLowerCase().contains(search);
            boolean mr = role == null || role.equals("Wszyscy")
                    || role.equals(String.valueOf(row.getOrDefault("role","")));
            return ms && mr;
        });
        employeesTable.setItems(filtered);
    }

    public void loadEmployees() {
        new Thread(() -> {
            try {
                List<Map<String, Object>> list = ApiClient.get("/users", ApiClient.listOf(Map.class));
                allEmployees = FXCollections.observableArrayList(list);
                Platform.runLater(() -> {
                    employeesTable.setItems(allEmployees);
                    applyFilters();
                });
            } catch (Exception e) {
                Platform.runLater(() -> employeesTable.setPlaceholder(
                        new Label("Błąd ładowania: " + e.getMessage())));
                e.printStackTrace();
            }
        }).start();
    }

    private void openEmployeeForm(Map<String, Object> employee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add-employee.fxml"));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(addEmployeeButton.getScene().getWindow());
            dialog.setTitle(employee == null ? "Dodaj pracownika" : "Edytuj pracownika");
            dialog.setScene(new Scene(loader.load(), 520, 500));
            AddEmployeeController ctrl = loader.getController();
            if (employee != null) ctrl.prefill(employee);
            ctrl.setOnSaved(() -> {
                dialog.close();
                loadEmployees();
            });
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private javafx.beans.property.SimpleStringProperty sp(Map<String, Object> m, String key) {
        return new javafx.beans.property.SimpleStringProperty(String.valueOf(m.getOrDefault(key, "")));
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