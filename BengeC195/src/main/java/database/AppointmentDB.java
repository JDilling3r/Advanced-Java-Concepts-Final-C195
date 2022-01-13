package database;

import model.Appointment;

import java.sql.*;
import java.time.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;
import model.User;

public class AppointmentDB {
    private static final Connection con = JDBC.getConnection();

    public static ObservableList<Appointment> getAppointments(int customerID, int days) {
        Appointment appointment;
        ObservableList<Appointment> Appts = FXCollections.observableArrayList();
        String timeZoneOffset = User.getCurrentTimezoneOffset();
        try {
            String query = "select  c.Contact_Name, a.*, convert_tz(a.Start, '+00:00', ?) as StartFormat, convert_tz(a.End, '+00:00', ?) as EndFormat\n" +
                    " from appointments a join contacts c \n" +
                    " on a.Contact_ID = c.Contact_ID \n" +
                    " where a.Customer_ID = ? \n" +
                    " AND a.Start between now() and (now() + interval ? day);";
            JDBC.makePreparedStatement(query, con);
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            ps.setString(1, timeZoneOffset);
            ps.setString(2, timeZoneOffset);
            ps.setInt(3, customerID);
            ps.setInt(4, days);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                appointment = new Appointment(rs.getInt("Appointment_ID"), rs.getString("Title"),
                        rs.getString("Description"), rs.getString("Location"), rs.getInt("Contact_ID"),
                        rs.getString("Type"), rs.getString("StartFormat"), rs.getString("EndFormat"),
                        rs.getInt("Customer_ID"), rs.getInt("User_ID"), rs.getString("Contact_Name"));
                Appts.add(appointment);
            }
            ps.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
        return Appts;
    }

    public static void checkConflict(int customer, LocalDateTime dateTime) throws SQLException {

    }

    public static Appointment getReminder(){
        Appointment appointment = new Appointment();
        try {
            String check = "select  c.Customer_Name, a.Start from appointments a " +
                   "join customers c on a.Customer_ID = c.Customer_ID " +
                   "AND a.Start between now() and (now() + interval 15 minute);";
            JDBC.makePreparedStatement(check, JDBC.getConnection());
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                appointment.setCustomerName(rs.getString("Customer_Name"));
                appointment.setStart(rs.getString("Start"));
            }
            ps.close();
       } catch (SQLException throwables) {
           throwables.printStackTrace();
       }
       return appointment;
    }

    public static void addAppointment(String title, String description, String location, int contactID, String type, String start, int customerID, int userID){
        int apptID = 1;
        try {
            Statement id = con.createStatement();
            ResultSet rs = id.executeQuery("select max(Appointment_ID)");
            if (rs.next()) {
                apptID = rs.getInt("Appointment_ID") + 1;
            }
            id.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        try {
            // Prepared Statement Set Index Numbers:                    1        2              3           4       5        6      7                                8              9          10            11
            String query = "insert into appointments set Appointment_ID=?, Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Create_Date=now(), Created_By=?, Customer_ID=?, User_ID=?, Contact_ID=?";
            JDBC.makePreparedStatement(query, con);
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            ps.setInt(1, apptID);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, location);
            ps.setString(5, type);
            ps.setString(6, start ); // fix
            ps.setString(7, start ); // fix
            ps.setString(8, UserDB.getLiveUser().getUserName());
            ps.setInt(9, customerID);
            ps.setInt(10, userID);
            ps.setInt(11, contactID);

            //Figure out START and END SQL and Application exchange / variable manipulation


            ResultSet rs = ps.executeQuery();
            ps.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void deleteAppointment(int apptID){
        try {
            String query = "delete from appointments where Appointment_ID=?";
            JDBC.makePreparedStatement(query, con);
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            ps.setInt(1, apptID);
            ps.execute();
            ps.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

}
