package controller;

import database.UserDB;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.stage.Stage;
import model.User;

public class Login implements Initializable {

    ResourceBundle lan;

    @FXML Label countryLabel;
    @FXML Label languageLabel;
    @FXML Button buttonLogin;
    @FXML Button buttonExit;
    @FXML TextField userID;
    @FXML PasswordField password;

    public void login(ActionEvent actionEvent) throws IOException, SQLException {

        try {
            int iUserID = Integer.parseInt(userID.getText());
            String iPassword = password.getText();
            boolean authenticated = UserDB.authenticate(iUserID, iPassword);
            if (authenticated) {
                final Node source = (Node) actionEvent.getSource();
                final Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Appointments.fxml")));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.NONE);
                alert.setTitle(lan.getString("fail"));
                alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
                alert.setContentText(lan.getString("incorrect"));
                alert.showAndWait();
            }
        }
        catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle(lan.getString("fail"));
            alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
            alert.setContentText(lan.getString("notValid"));
            alert.showAndWait();
        }
    }

    public void cancel(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(lan.getString("uSure"));
        alert.setContentText(lan.getString("appClose"));
        Optional<ButtonType> choice = alert.showAndWait();
        if (choice.isPresent() && choice.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Locale locale = new Locale("fr", "france"); To test properties file (auto language change)
        Locale locale = Locale.getDefault();

        try {
            lan  = ResourceBundle.getBundle("Lang", locale);
            countryLabel.setText(lan.getString("country") + ": " + locale.getCountry());
            languageLabel.setText(lan.getString("language") + ": " + lan.getString(locale.getDisplayLanguage().toLowerCase(Locale.ROOT)));
            buttonLogin.setText(lan.getString("login"));
            buttonExit.setText(lan.getString("exit"));
            userID.setPromptText(lan.getString("enterID"));
            password.setPromptText(lan.getString("enterPassword"));
        } catch (Exception defaultEnglish) {
            lan = ResourceBundle.getBundle("Lang");
        }
    }
}
