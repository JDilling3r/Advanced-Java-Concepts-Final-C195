package controller;

import database.AppointmentDB;
import database.CustomerDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the main view of the program, showing all customer and appointment data and buttons used to manage them.
 */
public class Appointments implements Initializable {

    public static Customer customer;
    public static Appointment appointment;
    private Parent scene;
    private int CID = 0;
    private int AID = 0;

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> customerId;
    @FXML private TableColumn<Customer, String> customerName;
    @FXML private TableColumn<Customer, String > customerPhone;
    @FXML private TableColumn<Customer, String > customerAddress;
    @FXML private TableColumn<Customer, String > customerZip;
    @FXML private Label customerLabel;
    @FXML private TableView<Appointment> apptTable;
    @FXML private TableColumn<Appointment, Integer> apptID;
    @FXML private TableColumn<Appointment, String> Title;
    @FXML private TableColumn<Appointment, String> Description;
    @FXML private TableColumn<Appointment, String> Location;
    @FXML private TableColumn<Appointment, String> Contact;
    @FXML private TableColumn<Appointment, String> Type;
    @FXML private TableColumn<Appointment, String> Start;
    @FXML private TableColumn<Appointment, String> End;
    @FXML private TableColumn<Appointment, String> CustomerID;
    @FXML private TableColumn<Appointment, String> UserID;
    @FXML private RadioButton week;
    @FXML private RadioButton month;

    public void handleAppointmentSelection(MouseEvent mouseEvent) {
        appointment = apptTable.getSelectionModel().getSelectedItem();
        if (appointment != null) {
            AID = appointment.getApptID();
        }
    }

    public void handleCustomerSelection(MouseEvent mouseEvent) {
        customer = customerTable.getSelectionModel().getSelectedItem();
        if (customer != null) {
            CID = customer.getCustomerID();
            customerLabel.setText(customer.getCustomerName());
        }

        if (week.isSelected()){
            apptTable.setItems(AppointmentDB.getAppointments(CID, 7));
        }
        if (month.isSelected()){
            apptTable.setItems(AppointmentDB.getAppointments(CID, 30));
        }
    }

    public void handleBackButton(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setContentText("You will be logged out.");
        Optional<ButtonType> choice = alert.showAndWait();
        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            final Node source = (Node) actionEvent.getSource();
            final Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Login.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void handleAddAppointment(ActionEvent actionEvent) throws IOException {
        if (CID == 0) {
            Alert eAlert = new Alert(Alert.AlertType.NONE);
            eAlert.setTitle("No Customer Selected.");
            eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
            eAlert.setContentText("Must select a customer to create an appointment for.");
            eAlert.showAndWait();
        }
        else {
            customer = customerTable.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Appointment Create");
            alert.setContentText("Do you want to add an appointment for " + customer.getCustomerName() + "?");
            Optional<ButtonType> choice = alert.showAndWait();
            if (choice.isPresent() && choice.get() == ButtonType.OK) {
                Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/AddAppointment.fxml")));
                stage.setScene(new Scene(scene));
                stage.show();
            }
        }
    }

    public void handleEditAppointments(ActionEvent actionEvent) throws IOException {
        if (AID ==0) {
            Alert eAlert = new Alert(Alert.AlertType.NONE);
            eAlert.setTitle("No Appointment Selected.");
            eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
            eAlert.setContentText("Must select an appointment to edit.");
            eAlert.showAndWait();
        }
        else {
            appointment = apptTable.getSelectionModel().getSelectedItem();
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/EditAppointment.fxml")));
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    public void handleDeleteAppointment(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Appointment Type: " + appointment.getType());
        alert.setContentText("You want to Delete Appointment_ID: " + appointment.getApptID() + " with " + customer.getCustomerName() + "?");
        Optional<ButtonType> choice = alert.showAndWait();
        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            if (AID ==0) {
                Alert eAlert = new Alert(Alert.AlertType.NONE);
                eAlert.setTitle("No Appointment Selected.");
                eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                eAlert.setContentText("Must select an appointment to delete.");
                eAlert.showAndWait();
            }
            else {
                AppointmentDB.deleteAppointment(AID);
                if (week.isSelected()) {
                    apptTable.setItems(AppointmentDB.getAppointments(CID, 7));
                }
                if (month.isSelected()) {
                    apptTable.setItems(AppointmentDB.getAppointments(CID, 30));
                }
            }
        }
    }

    public void handleAddCustomer(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/AddCustomer.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    public void handleEditCustomer(ActionEvent actionEvent) throws IOException {
        if (CID == 0) {
            Alert eAlert = new Alert(Alert.AlertType.NONE);
            eAlert.setTitle("No Customer Selected.");
            eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
            eAlert.setContentText("Must select a customer to edit.");
            eAlert.showAndWait();
        }
        else {
            customer = customerTable.getSelectionModel().getSelectedItem();
            customer.setCustomerID(CID);
            appointment = apptTable.getSelectionModel().getSelectedItem();
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/EditCustomer.fxml")));
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    public void handleDeleteCustomer(ActionEvent actionEvent) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setContentText("Do you want to delete " + customer.getCustomerName() + "?");
        Optional<ButtonType> choice = alert.showAndWait();
        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            if (CID == 0){
                Alert eAlert = new Alert(Alert.AlertType.NONE);
                eAlert.setTitle("No Customer Selected.");
                eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                eAlert.setContentText("Must select a customer to delete.");
                eAlert.showAndWait();
            }
            if (CustomerDB.deleteCustomer(CID)) {
                customerTable.setItems(CustomerDB.getAllCustomers());
            }
            else {
                Alert eAlert = new Alert(Alert.AlertType.NONE);
                eAlert.setTitle("Not Allowed");
                eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                eAlert.setContentText("Cannot delete customer with scheduled appointment.");
                eAlert.showAndWait();
                month.setSelected(true);
            }
        }
    }

    public void handleReports(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Reports.fxml")));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    public void toggleWeekMonth(ActionEvent actionEvent) {
        if (week.isSelected()){
            apptTable.setItems(AppointmentDB.getAppointments(CID, 7));
        }
        if (month.isSelected()){
            apptTable.setItems(AppointmentDB.getAppointments(CID, 30));
        }
    }


    // Gather language information and change properties based on data & reminder for upcoming appointment.
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Appointment remind = AppointmentDB.getReminder();
            if (remind.getCustomerName() != null){
                Alert eAlert = new Alert(Alert.AlertType.NONE);
                eAlert.setTitle("Appointment within 15 minutes!");
                eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                eAlert.setContentText("You have an upcoming appointment with " + remind.getCustomerName() + " at " + remind.getStart());
                eAlert.showAndWait();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
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

        apptID.setCellValueFactory(new PropertyValueFactory<>("ApptID"));
        UserID.setCellValueFactory(new PropertyValueFactory<>("UserID"));
        Title.setCellValueFactory(new PropertyValueFactory<>("Title"));
        Description.setCellValueFactory(new PropertyValueFactory<>("Description"));
        Type.setCellValueFactory(new PropertyValueFactory<>("Type"));
        Location.setCellValueFactory(new PropertyValueFactory<>("Location"));
        Contact.setCellValueFactory(new PropertyValueFactory<>("Contact"));
        Start.setCellValueFactory(new PropertyValueFactory<>("Start"));
        End.setCellValueFactory(new PropertyValueFactory<>("End"));
        CustomerID.setCellValueFactory(new PropertyValueFactory<>("CustomerID"));

        if (week.isSelected()){
            apptTable.setItems(AppointmentDB.getAppointments(CID, 7));
        }
        if (month.isSelected()){
            apptTable.setItems(AppointmentDB.getAppointments(CID, 30));
        }
    }

}
