package controller;

import database.AppointmentDB;
import database.JDBC;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Appointment;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;

/**
 * Reports view will show:
 *  - Appointment type frequency by month
 *  - Schedule of Contacts
 *  - Pie chart showing busiest months with most appointments
 */
public class Reports implements Initializable {

    // Class created to get input for type amount report
    public static class Typer {
        private final int amount;
        private final String type;

        public Typer(int amount, String type){
            this.amount = amount;
            this.type = type;
        }

        public int getAmount() {
            return amount;
        }

        public String getType() {
            return type;
        }
    }

    private final ObservableList<Integer> values = FXCollections.observableArrayList();
    private static final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private static final ObservableList<Typer> types = FXCollections.observableArrayList();
    private static final Hashtable<Integer, String> contactDict = new Hashtable<Integer, String>();
    private static final ObservableList<String> months = FXCollections.observableArrayList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "December");

    @FXML
    private TableView<Typer> typeTable;
    @FXML
    private TableColumn<Object, Object> aptType;
    @FXML
    private TableColumn<Object, Object> aptAmount;
    @FXML
    private TableView<Appointment> scheduleTable;
    @FXML
    private TableColumn<Object, Object> AID;
    @FXML
    private TableColumn<Object, Object> title;
    @FXML
    private TableColumn<Object, Object> type;
    @FXML
    private TableColumn<Object, Object> description;
    @FXML
    private TableColumn<Object, Object> start;
    @FXML
    private TableColumn<Object, Object> end;
    @FXML
    private TableColumn<Object, Object> CID;
    @FXML
    private PieChart monthPieChart;
    @FXML
    public ComboBox<String> contactSelector = new ComboBox<>();
    @FXML
    private Slider monthSlider;
    @FXML
    private Label monthLabel;

    /**
     * Back button to return to main program
     * @param actionEvent on user clicked
     * @throws IOException
     */
    @FXML
    private void handleBackButton(ActionEvent actionEvent) throws IOException {
        final Node source = (Node) actionEvent.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Appointments.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Changed contact schedule table based on contact selected from drop down menu.
     * @param actionEvent
     */
    public void contactSelect(ActionEvent actionEvent) {
        scheduleTable.getItems().clear();
        try{
            int Contact_ID = Integer.parseInt(contactSelector.getSelectionModel().getSelectedItem().substring(contactSelector.getSelectionModel().getSelectedItem().lastIndexOf(" ")).stripLeading());
            String query = "select *, convert_tz(Start, '+00:00', ?) as StartFormat, convert_tz(End, '+00:00', ?) as EndFormat from appointments where Start > now() and Contact_ID=?";
            JDBC.makePreparedStatement(query, JDBC.getConnection());
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            ps.setString(1, User.getCurrentTimezoneOffset());
            ps.setString(2, User.getCurrentTimezoneOffset());
            ps.setInt(3, Contact_ID);
            System.out.println();
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Appointment appointment = new Appointment();
                appointment.setApptID(rs.getInt("Appointment_ID"));
                appointment.setTitle(rs.getString("Title"));
                appointment.setType(rs.getString("Type"));
                appointment.setDescription(rs.getString("Description"));
                appointment.setStart(rs.getString("StartFormat"));
                appointment.setEnd(rs.getString("EndFormat"));
                appointment.setCustomerID(rs.getInt("Customer_ID"));
                appointments.add(appointment);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void MonthChoice(MouseEvent mouseEvent) {
        types.clear();
        int mCount = (int) monthSlider.getValue();
        int year = Integer.parseInt(Year.now().toString());
        monthLabel.setText(months.get(mCount - 1));
        try {
            YearMonth yearmonth = YearMonth.of(year, mCount);
            int daysInMonth = yearmonth.lengthOfMonth();
            String start = String.format("%s-%s-01 00:00:01", year, mCount);
            String end = String.format("%s-%s-%s 23:59:59", year, mCount, daysInMonth);
            types.addAll(AppointmentDB.getMonthAppointmentType(start, end));
            typeTable.setItems(types);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Initializes and fills all tables and charts in reports view.
     *
     * Adding amount of appointments to every Pie chart label with Lambda function.
     *     pieChartData.forEach(data -> data.nameProperty().bind(
     *                 Bindings.concat(
     *                         data.getName(), " ", ((int) data.getPieValue()), " Appointments"
     *                     )
     *                 )
     *         );
     *
     *  Lambda Function to populate contactSelector with HashTable values and key that correlates to Contact_ID
     *  contactDict.forEach((key, value) -> contactSelector.getItems().add(value.concat(" | " + key)));
     *
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // int mCount is used to loop through each month, get specific monthly data, and set month index values with Pie Chart List.
        // Getting Current year and converting to String and then Int for input into getting days within month.
        int year = Integer.parseInt(Year.now().toString());
        for (int mCount = 1; mCount <= 12; ++mCount) {
            // Getting Days within current month for pie chart data
            YearMonth yearmonth = YearMonth.of(year, mCount);
            int daysInMonth = yearmonth.lengthOfMonth();
            // Setting up for SQL query function to return amount of appointment within months.
            String start = String.format("%s-%s-01 00:00:01", year, mCount);
            String end = String.format("%s-%s-%s 23:59:59", year, mCount, daysInMonth);
            // SQL query function to return amount to update pie chart data at specific month
            int appointmentAmount = AppointmentDB.getMonthAppointmentAmount(start, end);
            // Adding appointment amount to values list
            values.add(appointmentAmount);
        }
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("January", values.get(0)),
                new PieChart.Data("February", values.get(1)),
                new PieChart.Data("March", values.get(2)),
                new PieChart.Data("April", values.get(3)),
                new PieChart.Data("May", values.get(4)),
                new PieChart.Data("June", values.get(5)),
                new PieChart.Data("July", values.get(6)),
                new PieChart.Data("August", values.get(7)),
                new PieChart.Data("September", values.get(8)),
                new PieChart.Data("October", values.get(9)),
                new PieChart.Data("November", values.get(10)),
                new PieChart.Data("December", values.get(11))
        );

        monthPieChart.setData(pieChartData);

        pieChartData.forEach(data -> data.nameProperty().bind(
                Bindings.concat(
                        data.getName(), " ", ((int) data.getPieValue()), " Appointments"
                    )
                )
        );
        // Add to contact hash table
        try {
            String query = "select Contact_ID, Contact_Name from contacts";
            JDBC.makePreparedStatement(query, JDBC.getConnection());
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                contactDict.put(rs.getInt("Contact_ID"), rs.getString("Contact_Name"));
            }

            contactDict.forEach((key, value) -> contactSelector.getItems().add(value.concat(" | " + key)));
            contactSelector.getSelectionModel().selectFirst();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        // Get all appointments contact specific and add to Appointment Schedule pone
        try{
            String query = "select *, convert_tz(Start, '+00:00', ?) as StartFormat, convert_tz(End, '+00:00', ?) as EndFormat from appointments where Start > now() and Contact_ID=?";
            JDBC.makePreparedStatement(query, JDBC.getConnection());
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            ps.setString(1, User.getCurrentTimezoneOffset());
            ps.setString(2, User.getCurrentTimezoneOffset());
            ps.setInt(3, Integer.parseInt(contactSelector.getSelectionModel().getSelectedItem().substring(contactSelector.getSelectionModel().getSelectedItem().lastIndexOf(" " + 1)).stripLeading()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Appointment appointment = new Appointment();
                appointment.setApptID(rs.getInt("Appointment_ID"));
                appointment.setTitle(rs.getString("Title"));
                appointment.setType(rs.getString("Type"));
                appointment.setDescription(rs.getString("Description"));
                appointment.setStart(rs.getString("StartFormat"));
                appointment.setEnd(rs.getString("EndFormat"));
                appointment.setCustomerID(rs.getInt("Customer_ID"));
                appointments.add(appointment);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        AID.setCellValueFactory(new PropertyValueFactory<>("ApptID"));
        title.setCellValueFactory(new PropertyValueFactory<>("Title"));
        description.setCellValueFactory(new PropertyValueFactory<>("Description"));
        type.setCellValueFactory(new PropertyValueFactory<>("Type"));
        start.setCellValueFactory(new PropertyValueFactory<>("Start"));
        end.setCellValueFactory(new PropertyValueFactory<>("End"));
        CID.setCellValueFactory(new PropertyValueFactory<>("CustomerID"));
        scheduleTable.setItems(appointments);

        // Setting appointment Type frequency Table
        aptAmount.setCellValueFactory(new PropertyValueFactory<>("Amount"));
        aptType.setCellValueFactory(new PropertyValueFactory<>("Type"));
        typeTable.setItems(types);
        monthSlider.setMin(1);
        monthSlider.setMax(12);
        monthSlider.setBlockIncrement(.1);
        monthSlider.setValue(1);
        monthLabel.setText(months.get(0));
        int mCount = (int) monthSlider.getValue();
        monthLabel.setText(months.get(mCount - 1));
        try {
            YearMonth yearmonth = YearMonth.of(year, mCount);
            int daysInMonth = yearmonth.lengthOfMonth();
            String start = String.format("%s-%s-01 00:00:01", year, mCount);
            String end = String.format("%s-%s-%s 23:59:59", year, mCount, daysInMonth);
            types.addAll(AppointmentDB.getMonthAppointmentType(start, end));
            typeTable.setItems(types);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
