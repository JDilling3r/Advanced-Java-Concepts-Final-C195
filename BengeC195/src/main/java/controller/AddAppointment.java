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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controller for the add appointment view.
 */
public class AddAppointment implements Initializable {

    private final ObservableList<String> locations = FXCollections.observableArrayList();
    private final ObservableList<String> times = FXCollections.observableArrayList();
    private final ObservableList<String> contacts = FXCollections.observableArrayList();
    private final ObservableList<Integer> minutes = FXCollections.observableArrayList(IntStream.range(15, 91).boxed().collect(Collectors.toList()));

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
    private ComboBox<Integer> minute;
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
        alert.setTitle("Create Appointment");
        alert.setContentText("Are you sure you would like to create this appoinment for " + name.getText() + "?");
        Optional<ButtonType> choice = alert.showAndWait();

        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String chosenDate = date.getValue().format(dateFormatter);
            String aptType = type.getText();
            String aptDescription = description.getText();
            String aptTitle = title.getText();
            String aptStartTime = String.format("%s %s",chosenDate, time.getValue());
            int contactID = contact.getSelectionModel().getSelectedIndex() + 1;
            // adding one because Index starts at 0 and list starts at 1 for correlation.
            int aptLength = minute.getSelectionModel().getSelectedIndex() + 15;
            if (chosenDate.equals("") || aptType.equals("") || aptTitle.equals("") || aptDescription.equals("")
                || aptStartTime.equals(" ") || aptLength <= 0){
                Alert eAlert = new Alert(Alert.AlertType.NONE);
                eAlert.setTitle("Empty Text Field");
                eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                eAlert.setContentText("All fields must contain data.");
                eAlert.showAndWait();
            }
            else {
                if (AppointmentDB.overlapCheck(aptStartTime, aptLength)){
                    Alert eAlert = new Alert(Alert.AlertType.NONE);
                    eAlert.setTitle("Appointment Overlap");
                    eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    eAlert.setContentText("Another Appointment is Scheduled during this Date and Time.");
                    eAlert.showAndWait();
                }
                else{
                    AppointmentDB.addAppointment(aptTitle, aptDescription, location.getValue(), contactID, aptType, aptStartTime, aptLength, Appointments.customer.getCustomerID());
                    Alert eAlert = new Alert(Alert.AlertType.NONE);
                    eAlert.setTitle("Success!");
                    eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    eAlert.setContentText("Appointment Successfully created.");
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
                        Alert erAlert = new Alert(Alert.AlertType.NONE);
                        erAlert.setTitle("Incorrect data entered");
                        erAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                        erAlert.setContentText("Try again.");
                        erAlert.showAndWait();
                    }
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

    // Validating date selected
    public void handleDateChoice(ActionEvent actionEvent) {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), date.getValue());
        if (days < 0){
            Alert eAlert = new Alert(Alert.AlertType.NONE);
            eAlert.setTitle("Date error");
            eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
            eAlert.setContentText("Cannot select a date before: " + LocalDate.now());
            eAlert.showAndWait();
            date.getEditor().clear();
        }
    }

    // Validating time selected
    public void handleTimeChoice(ActionEvent actionEvent) {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), date.getValue());
        if (days == 0){
            long timer = ChronoUnit.MINUTES.between(LocalTime.now(), LocalTime.parse(time.getValue()));
            if (timer < 60){
                Alert eAlert = new Alert(Alert.AlertType.NONE);
                eAlert.setTitle("Time error");
                eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                eAlert.setContentText("Cannot select a start time today before: " + LocalTime.now().plusMinutes(60).format(DateTimeFormatter.ofPattern("HH:mm")));
                eAlert.showAndWait();
                date.getEditor().clear();
            }
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
        minute.setItems(minutes);
        UID.setText(String.valueOf(UserDB.getLiveUser().getUserID()));
        CID.setText(String.valueOf(Appointments.customer.getCustomerID()));
        name.setText(Appointments.customer.getCustomerName());
        location.getSelectionModel().select(Appointments.customer.getDivisionID() - 1);
    }

}