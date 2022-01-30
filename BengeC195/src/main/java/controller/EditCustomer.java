package controller;

import database.CustomerDB;
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
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import model.Country;

public class EditCustomer implements Initializable {

    private final Customer customerEdit = Appointments.customer;
    private final ObservableList<Country> countries = FXCollections.observableArrayList();
    private final ObservableList<String> locations = FXCollections.observableArrayList();

    @FXML
    private ComboBox<String> country;
    @FXML
    private TextField CID;
    @FXML
    private TextField UID;
    @FXML
    private TextField name;
    @FXML
    private TextField phone;
    @FXML
    private TextField zip;
    @FXML
    private TextField address;
    @FXML
    private ComboBox<String> location;

    public void handleCountrySelection(ActionEvent actionEvent) {
        int countryID = country.getSelectionModel().getSelectedIndex() + 1;
        location.getSelectionModel().clearSelection();
        locations.clear();

        try {
            String cityQuery = "select Division from first_level_divisions where Country_ID=?";
            JDBC.makePreparedStatement(cityQuery, JDBC.getConnection());
            PreparedStatement cities = JDBC.getPreparedStatement();
            assert cities != null;
            cities.setInt(1, countryID);
            ResultSet cityList = cities.executeQuery();
            while (cityList.next()){
                locations.add(cityList.getString("Division"));
            }
            cities.close();
            location.setItems(locations);
            // Getting the count of Customer list plus the value of 1 will yield the new Customer_ID.
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void handleSaveButton(ActionEvent actionEvent) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Edit");
        alert.setContentText("Are you sure you would like to make these changes to  " + customerEdit.getCustomerName() + "?");
        Optional<ButtonType> choice = alert.showAndWait();
        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            String cName = name.getText();
            String cPhone = phone.getText();
            String cZip = zip.getText();
            String cAddress = address.getText();
            // Because correlating Division_IDs start at 1 and the list starts at 0, so our list is -1 off.
            int divID = location.getSelectionModel().getSelectedIndex() + 1;
            if (cName.equals("") || cPhone.equals("") || cZip.equals("") || cAddress.equals("") || divID <= 0){
                Alert eAlert = new Alert(Alert.AlertType.NONE);
                eAlert.setTitle("Empty Text Field");
                eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                eAlert.setContentText("All fields must contain data.");
                eAlert.showAndWait();
            }
            else {
                CustomerDB.editCustomer(customerEdit.getCustomerID(), cName, cAddress, cZip, cPhone, divID);
                Alert eAlert = new Alert(Alert.AlertType.NONE);
                eAlert.setTitle("Success!");
                eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                eAlert.setContentText("Customer: " + cName + " Successfully edited.");
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
        int country_ID = 0;
        try{
            String query = "select Country_ID from first_level_divisions where Division_ID=?;";
            JDBC.makePreparedStatement(query, JDBC.getConnection());
            PreparedStatement countryQ = JDBC.getPreparedStatement();
            assert countryQ != null;
            countryQ.setInt(1, Appointments.customer.getDivisionID());
            ResultSet countryid = countryQ.executeQuery();
            if (countryid.next()){
                country_ID = countryid.getInt("Country_ID");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            String countryQuery = "select Country_ID, Country from countries;";
            JDBC.makePreparedStatement(countryQuery, JDBC.getConnection());
            PreparedStatement countryS = JDBC.getPreparedStatement();
            assert countryS != null;
            ResultSet countryList = countryS.executeQuery();
            while (countryList.next()){
                Country country = new Country(countryList.getInt("Country_ID"), countryList.getString("Country"));
                countries.add(country);
            }
            // This is a lambda function / method reference -> that sets the combobox country to all countryName values of Olist class "countries"
            country.setItems(FXCollections.observableArrayList(countries.stream().map(Country::getCountryName).collect(Collectors.toList())));
            country.getSelectionModel().select(country_ID - 1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            int countryID = country.getSelectionModel().getSelectedIndex() + 1;
            location.getSelectionModel().clearSelection();
            String cityQuery = "select Division from first_level_divisions where Country_ID=?";
            JDBC.makePreparedStatement(cityQuery, JDBC.getConnection());
            PreparedStatement cities = JDBC.getPreparedStatement();
            assert cities != null;
            cities.setInt(1, countryID);
            ResultSet cityList = cities.executeQuery();
            while (cityList.next()){
                locations.add(cityList.getString("Division"));
                }
            cities.close();
            location.setItems(locations);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println(customerEdit.getCustomerID());
        CID.setText(String.valueOf(customerEdit.getCustomerID()));
        UID.setText(String.valueOf(UserDB.getLiveUser().getUserID()));
        phone.setText(customerEdit.getPhone());
        zip.setText(customerEdit.getZip());
        address.setText(customerEdit.getAddress());
        name.setText(customerEdit.getCustomerName());
        // Minus one to Division ID here because it is being set based on an ID that starts at one, where our list starts at 0.
        location.getSelectionModel().select(Appointments.customer.getDivisionID() - 1);
    }

}
