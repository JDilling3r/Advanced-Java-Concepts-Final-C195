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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
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

public class EditCustomer implements Initializable {

    private static Customer customerEdit;
    private final ObservableList<String> locations = FXCollections.observableArrayList();

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
    private ChoiceBox<String> location;

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
            if (cName.equals("") || cPhone.equals("") || cZip.equals("") || cAddress.equals("") || location.getSelectionModel().getSelectedIndex() <= 0){
                Alert eAlert = new Alert(Alert.AlertType.NONE);
                eAlert.setTitle("Empty Text Field");
                eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                eAlert.setContentText("All fields must contain data.");
                eAlert.showAndWait();
            }
            else {
                int divID = location.getSelectionModel().getSelectedIndex();
                CustomerDB.editCustomer(customerEdit.getCustomerID(), cName, cAddress, cZip, cPhone, divID);
                Alert eAlert = new Alert(Alert.AlertType.NONE);
                eAlert.setTitle("Success!");
                eAlert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                eAlert.setContentText("Customer Successfully edited.");
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
        customerEdit = Appointments.customer;
        try {
            String cityQuery = "select Division from first_level_divisions";
            JDBC.makePreparedStatement(cityQuery, JDBC.getConnection());
            PreparedStatement cities = JDBC.getPreparedStatement();
            assert cities != null;
            ResultSet cityList = cities.executeQuery();
            while (cityList.next()){
                locations.add(cityList.getString("Division"));
            }
            cities.close();
            location.setItems(locations);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        CID.setText(String.valueOf(customerEdit.getCustomerID()));
        UID.setText(String.valueOf(UserDB.getLiveUser().getUserID()));
        phone.setText(customerEdit.getPhone());
        zip.setText(customerEdit.getZip());
        address.setText(customerEdit.getAddress());
        name.setText(customerEdit.getCustomerName());
    }

}
