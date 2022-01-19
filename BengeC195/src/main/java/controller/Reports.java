package controller;

import database.AppointmentDB;
import database.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
import java.util.Objects;
import java.util.ResourceBundle;

public class Reports implements Initializable {

    // Class created to get input for type amount report
    public static class Typer {
        private final int amount;
        private final String type;

        Typer(int amount, String type){
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

    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private ObservableList<Integer> values = FXCollections.observableArrayList();
    private static ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private static ObservableList<Typer> types = FXCollections.observableArrayList();

    @FXML
    private TableView<Typer> typeTable;
    @FXML
    private TableColumn aptType;
    @FXML
    private TableColumn aptAmount;
    @FXML
    private TableView<Appointment> scheduleTable;
    @FXML
    private TableColumn AID;
    @FXML
    private TableColumn title;
    @FXML
    private TableColumn type;
    @FXML
    private TableColumn description;
    @FXML
    private TableColumn start;
    @FXML
    private TableColumn end;
    @FXML
    private TableColumn CID;
    @FXML
    private PieChart monthPieChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // integer monthCounter is used to loop through each month, get specific monthly data, and set month index values with Pie Chart List.
        int monthCounter = 1;
        // Getting Current year and converting to String and then Int for input into getting days within month.
        int year = Integer.parseInt(Year.now().toString());
        while (monthCounter < 12) {
            // Getting Days within current month for pie chart data
            YearMonth yearmonth = YearMonth.of(year, monthCounter);
            int daysInMonth = yearmonth.lengthOfMonth();
            // Setting up for SQL query function to return amount of appointment within months.
            String start = String.format("%s-%s-01", year, monthCounter);
            String end = String.format("%s-%s-%s", year, monthCounter, daysInMonth);
            // SQL query function to return amount to update pie chart data at specific month
            int appointmentAmount = AppointmentDB.getMonthAppointmentAmount(start, end);
            // Adding appointment amount to values list
            values.add(appointmentAmount);
            monthCounter++;
        }
       pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("January", values.indexOf(1)),
                new PieChart.Data("February", values.indexOf(2)),
                new PieChart.Data("March",values.indexOf(3)),
                new PieChart.Data("April", values.indexOf(4)),
                new PieChart.Data("May", values.indexOf(5)),
                new PieChart.Data("June", values.indexOf(6)),
                new PieChart.Data("July", values.indexOf(7)),
                new PieChart.Data("August", values.indexOf(8)),
                new PieChart.Data("September",values.indexOf(9)),
                new PieChart.Data("October", values.indexOf(10)),
                new PieChart.Data("November", values.indexOf(11)),
                new PieChart.Data("December", values.indexOf(12))
        );
        monthPieChart.setData(pieChartData);

        // Get all appointments and add to Appointment Schedule pone
        try{
            String query = "select *, convert_tz(Start, '+00:00', ?) as StartFormat, convert_tz(End, '+00:00', ?) as EndFormat from appointments where Start > now()";
            JDBC.makePreparedStatement(query, JDBC.getConnection());
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            ps.setString(1, User.getCurrentTimezoneOffset());
            ps.setString(2, User.getCurrentTimezoneOffset());
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

        // Setting Type amount Table
        try{
            String query = "select Type, count(*) as Counter from appointments group by Type";
            JDBC.makePreparedStatement(query, JDBC.getConnection());
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Typer typer = new Typer(rs.getInt("Counter"), rs.getString("Type"));
                System.out.println(typer.getAmount() + typer.getType());
                types.add(typer);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        aptAmount.setCellValueFactory(new PropertyValueFactory<>("Amount"));
        aptType.setCellValueFactory(new PropertyValueFactory<>("Type"));
        typeTable.setItems(types);
    }

    public void handleBackButton(ActionEvent actionEvent) throws IOException {
        final Node source = (Node) actionEvent.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Appointments.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
