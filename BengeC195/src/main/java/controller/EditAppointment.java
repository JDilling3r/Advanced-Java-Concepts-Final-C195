package controller;

import database.AppointmentDB;
import database.JDBC;
import database.UserDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EditAppointment implements Initializable {

    private final ObservableList<String> locations = FXCollections.observableArrayList();
    private final ObservableList<String> times = FXCollections.observableArrayList();
    private final ObservableList<String> contacts = FXCollections.observableArrayList();
    private static int AID;

    @FXML
    private ComboBox<String> location;
    @FXML
    private TextField name;
    @FXML
    private DatePicker date;
    @FXML
    private ComboBox<String> time;
    @FXML
    private ComboBox<String> contact;
    @FXML
    private TextField type;
    @FXML
    private TextField title;
    @FXML
    private TextField description;
    @FXML
    private TextField UID;
    @FXML
    private TextField CID;

    public void handleSaveButton(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Edit Appointment");
        alert.setContentText("Are you sure you would like to edit this appoinment for " + name.getText() + "?");
        Optional<ButtonType> choice = alert.showAndWait();

        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            String chosenDate = date.getValue().format(dateFormatter);
            String aptType = type.getText();
            String aptDescription = description.getText();
            String aptTitle = title.getText();
            String aptStartTime = String.format("%s %s",chosenDate, time.getValue());

            if (chosenDate.equals("") || aptType.equals("") || aptTitle.equals("") || aptDescription.equals("")){
                Alert eAlert = new Alert(Alert.AlertType.NONE);
                eAlert.setTitle("Empty Text Field");
                eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                eAlert.setContentText("All fields must contain data.");
                eAlert.showAndWait();
            }
            else {
                if (AppointmentDB.editAppointment(AID, aptTitle, aptDescription, location.getValue(), contact.getSelectionModel().getSelectedIndex(), aptType, aptStartTime, Appointments.appointment.getCustomerID()))
                {
                    Alert eAlert = new Alert(Alert.AlertType.NONE);
                    eAlert.setTitle("Success!");
                    eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    eAlert.setContentText("Appointment Successfully Edited.");
                    eAlert.showAndWait();
                    try {
                        final Node source = (Node) actionEvent.getSource();
                        final Stage stage = (Stage) source.getScene().getWindow();
                        stage.close();
                        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Appointments.fxml")));
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Alert eAlert = new Alert(Alert.AlertType.NONE);
                    eAlert.setTitle("Failure!");
                    eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    eAlert.setContentText("Check time and date, try again.");
                    eAlert.showAndWait();
                }
            }
        }
    }

    public void handleBackButton(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setContentText("Any progress will not be saved.");
        Optional<ButtonType> choice = alert.showAndWait();
        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            final Node source = (Node) actionEvent.getSource();
            final Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Appointments.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            String cityQuery = "select Division from first_level_divisions";
            JDBC.makePreparedStatement(cityQuery, JDBC.getConnection());
            PreparedStatement cities = JDBC.getPreparedStatement();
            assert cities != null;
            ResultSet cityList = cities.executeQuery();
            while (cityList.next()) {
                locations.add(cityList.getString("Division"));
            }
            location.setItems(locations);
            cities.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            // Creating a loop that will add times from 08:00 to 22:00 in 30 minute increments to list for creating Time Combo box.
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String timer = "08:00:00";
            while (!timer.equals("22:00:00")) {
                Date d = df.parse(timer);
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                cal.add(Calendar.MINUTE, 30);
                times.add(df.format(cal.getTime()));
                timer = df.format(cal.getTime());
            }
            time.setItems(times);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            String contactQuery = "select Contact_Name from contacts";
            JDBC.makePreparedStatement(contactQuery, JDBC.getConnection());
            PreparedStatement contactStatement = JDBC.getPreparedStatement();
            assert contactStatement != null;
            ResultSet contactList = contactStatement.executeQuery();
            while (contactList.next()) {
                contacts.add(contactList.getString("Contact_Name"));
            }
            contact.setItems(contacts);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        AID = Appointments.appointment.getApptID();
        UID.setText(String.valueOf(UserDB.getLiveUser().getUserID()));
        CID.setText(String.valueOf(Appointments.customer.getCustomerID()));
        name.setText(Appointments.customer.getCustomerName());
        type.setText(Appointments.appointment.getType());
        title.setText(Appointments.appointment.getTitle());
        description.setText(Appointments.appointment.getDescription());
    }
}