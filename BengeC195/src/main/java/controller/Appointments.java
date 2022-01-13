package controller;

import database.AppointmentDB;
import database.CustomerDB;
import database.JDBC;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Appointment;
import model.Customer;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Appointments implements Initializable {

    private Parent scene;
    private Customer customer;
    private Appointment appointment;

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> customerId;
    @FXML private TableColumn<Customer, String> customerName;
    @FXML private TableColumn<Customer, String > customerPhone;
    @FXML private TableColumn<Customer, String > customerAddress;
    @FXML private TableColumn<Customer, String > customerZip;
    @FXML private Label customerLabel;
    @FXML private TableView<Appointment> apptTable;
    @FXML private TableColumn<Appointment, Integer> apptID;
    @FXML private TableColumn<Appointment, String> apptTitle;
    @FXML private TableColumn<Appointment, String> apptDescription;
    @FXML private TableColumn<Appointment, String> apptLocation;
    @FXML private TableColumn<Appointment, String> apptContact;
    @FXML private TableColumn<Appointment, String> apptType;
    @FXML private TableColumn<Appointment, String> apptStart;
    @FXML private TableColumn<Appointment, String> apptEnd;
    @FXML private TableColumn<Appointment, String> apptCID;
    @FXML private TableColumn<Appointment, String> apptUID;
    @FXML private RadioButton week;
    @FXML private RadioButton month;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            customerTable.setItems(CustomerDB.getAllCustomers());
            customerId.setCellValueFactory(new PropertyValueFactory<>("CustomerID"));
            customerName.setCellValueFactory(new PropertyValueFactory<>("CustomerName"));
            customerAddress.setCellValueFactory(new PropertyValueFactory<>("Address"));
            customerPhone.setCellValueFactory(new PropertyValueFactory<>("Phone"));
            customerZip.setCellValueFactory(new PropertyValueFactory<>("Zip"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            Appointment reminder = AppointmentDB.getReminder();
            if (reminder.getCustomerName() != null){
                String remind = String.format("Appointment with %s at %s", reminder.getCustomerName(), reminder.getStart());
                Alert alert = new Alert(Alert.AlertType.NONE);
                alert.setTitle("Reminder");
                alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                alert.setContentText(remind);
                alert.showAndWait();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void handleCustomerSelection(MouseEvent mouseEvent) {

        customer = customerTable.getSelectionModel().getSelectedItem();
        int CID = customer.getCustomerID();

        customerLabel.setText(customer.getCustomerName());

    }

    public void handleBackButton(ActionEvent actionEvent) {
    }

    public void handleAddAppointments(ActionEvent actionEvent) {
    }

    public void handleEditAppointments(ActionEvent actionEvent) {
    }

    public void handleDeleteAppointment(ActionEvent actionEvent) {
    }

    public void radioButtonChanges(ActionEvent actionEvent) {
    }

    public void handleAddCustomer(ActionEvent actionEvent) {
    }

    public void handleEditCustomer(ActionEvent actionEvent) {
    }

    public void handleDeleteCustomer(ActionEvent actionEvent) {
    }

    public void handleReports(ActionEvent actionEvent) {
    }
}
