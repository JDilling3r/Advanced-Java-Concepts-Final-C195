package database;

import controller.Reports;
import model.Appointment;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Class AppointmentDB extends Appointment: maintains all functions interfacing MYSQL API
 */
public class AppointmentDB extends Appointment {
    private static final Connection con = JDBC.getConnection();
    private static final String tzOffset = UserDB.getLiveUser().getTzOffset();

    /**
     * SQL query to get specific appoitnemnts from appointment table.
     * @param customerID primary key for customer identified
     * @param days amount of days for SQL query
     * @return ObservableList Appointments
     */
    public static ObservableList<Appointment> getAppointments(int customerID, int days) {
        Appointment appointment;
        ObservableList<Appointment> Appts = FXCollections.observableArrayList();
        /*
          This query will collect all appointment data, convert the specifica user's local time to zulu, and join contactnames from contacts table.
         */
        try {
            String query = "select  c.Contact_Name, a.*, convert_tz(a.Start, '+00:00', ?) as StartFormat, convert_tz(a.End, '+00:00', ?) as EndFormat\n" +
                    " from appointments a join contacts c \n" +
                    " on a.Contact_ID = c.Contact_ID \n" +
                    " where a.Customer_ID = ? \n" +
                    " AND a.Start between now() and (now() + interval ? day);";
            JDBC.makePreparedStatement(query, con);
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            ps.setString(1, tzOffset);
            ps.setString(2, tzOffset);
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

    /**
     * @return appointment data for appointments within next 15 minutes.
     */
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

    /**
     * function to create row in appointments table.
     * @param title title of appointment
     * @param description description of appointment
     * @param location location of appointment
     * @param contactID contact ID reference for appointment
     * @param type type of appointment
     * @param start start date for appointment
     * @param minutes minute total of appointment
     * @param customerID customer id of referenced appointment
     * @return boolean TRUE if successful / False if unsuccessful
     */
    public static boolean addAppointment(String title, String description, String location, int contactID, String type, String start, int minutes, int customerID) {
        int apptID = 1;
        boolean overlap = Boolean.FALSE;
        // Selecting max appointments and adding 1 should yield an appointment ID that has not been created yet.
        try {
            Statement id = con.createStatement();
            ResultSet rs = id.executeQuery("select max(Appointment_ID) as Last_Appointment from appointments");
            if (rs.next()) {
                apptID = rs.getInt("Last_Appointment") + 1;
            }
            id.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        try {
            // Prepared Statement Set Index Numbers:                    1        2              3           4       5                   6  7                             8  9                       10                                       11             12         13            14                 15
            String query = "insert into appointments set Appointment_ID=?, Title=?, Description=?, Location=?, Type=?, Start=convert_tz(?, ?, '+00:00'), End=(convert_tz(?, ?, '+00:00') + interval ? minute ), Create_Date=now(), Created_By=?, Customer_ID=?, User_ID=?, Contact_ID=?, Last_Updated_by=?, Last_Update=now();";
            JDBC.makePreparedStatement(query, con);
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            ps.setInt(1, apptID);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, location);
            ps.setString(5, type);
            ps.setString(6, start);
            ps.setString(7, tzOffset);
            ps.setString(8, start);
            ps.setString(9, tzOffset);
            ps.setInt(10, minutes);
            ps.setString(11, UserDB.getLiveUser().getUserName());
            ps.setInt(12, customerID);
            ps.setInt(13, UserDB.getLiveUser().getUserID());
            ps.setInt(14, contactID);
            ps.setString(15, UserDB.getLiveUser().getUserName());
            ps.execute();
            ps.close();
            return Boolean.TRUE;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * Function to check appointment overlap
     * @param start start time of appointment
     * @param minutes amount of minutes of appointment
     * @return Boolean value for verification of overlap
     */
    public static boolean overlapCheck (String start, int minutes) {
        boolean overlap = Boolean.FALSE;
        try {
            String query = "select * from appointments where convert_tz(?, ?, '+00:00') Between Start and End" +
                    " or (convert_tz(?, ?, '+00:00') + interval ? minute) between Start and End" +
                    " or convert_tz(?, ?, '+00:00') <= Start and (convert_tz(?, ?, '+00:00') + interval ? minute) >= End;";
            JDBC.makePreparedStatement(query, con);
            PreparedStatement ps = JDBC.getPreparedStatement();

            assert ps != null;
            ps.setString(1, start);
            ps.setString(2, tzOffset);
            ps.setString(3, start);
            ps.setString(4, tzOffset);
            ps.setInt(5, minutes);
            ps.setString(6, start);
            ps.setString(7, tzOffset);
            ps.setString(8, start);
            ps.setString(9, tzOffset);
            ps.setInt(10, minutes);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                overlap = Boolean.TRUE;
            }
            ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return overlap;
    }

    /**
     * function to edit row in appointments table.
     * @param title title of appointment
     * @param description description of appointment
     * @param location location of appointment
     * @param contactID contact ID reference for appointment
     * @param type type of appointment
     * @param start start date for appointment
     * @param minutes minute total of appointment
     * @param customerID customer id of referenced appointment
     * @return boolean TRUE if successful / False if unsuccessful
     */
    public static boolean editAppointment(int apptID, String title, String description, String location, int contactID, String type, String start, int minutes, int customerID) {
        try {
            // Prepared Statement Set Index Numbers:      1              2           3       4                   5  6                             7  8                       9                     10          11            12                13                                          14
            String query = "update appointments set Title=?, Description=?, Location=?, Type=?, Start=convert_tz(?, ?, '+00:00'), End=(convert_tz(?, ?, '+00:00') + interval ? minute), Customer_ID=?, User_ID=?, Contact_ID=?, Last_Updated_by=?, Last_Update=now() where Appointment_ID=?";
            JDBC.makePreparedStatement(query, con);
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, location);
            ps.setString(4, type);
            ps.setString(5, start);
            ps.setString(6, tzOffset);
            ps.setString(7, start);ps.setString(8, tzOffset);
            ps.setInt(9, minutes);
            ps.setInt(10, customerID);
            ps.setInt(11, UserDB.getLiveUser().getUserID());
            ps.setInt(12, contactID);
            ps.setString(13, UserDB.getLiveUser().getUserName());
            ps.execute();
            ps.close();
            return Boolean.TRUE;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * delete row from appointments table
     * @param apptID primary key
     */
    public static void deleteAppointment(int apptID){
        try {
            String query = "delete from appointments where Appointment_ID=?;";
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

    /**
     * populate pie chart data under reports
     * @param start start date and time of appointment
     * @param end end date and time of appoitnemnt
     * @return appointment data
     */
    public static int getMonthAppointmentAmount(String start, String end){
        int aptAmount = 0;
        try {
            String query = "select count(Appointment_ID) as count from appointments where Start > ? AND Start < ?";
            JDBC.makePreparedStatement(query, con);
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            ps.setString(1, start);
            ps.setString(2, end);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                aptAmount = rs.getInt("count");
            }
            ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return aptAmount;
    }

    /**
     * SQL query to return appointment types and count of each
     * @param start start date and time of appointment
     * @param end end date and time of appoitnemnt
     * @return appointment data
     */
    public static ObservableList<Reports.Typer> getMonthAppointmentType(String start, String end){
        int typeAmount = 0;
        String type;
        ObservableList<Reports.Typer> typee= FXCollections.observableArrayList();
        try {
            String query = "select Type, count(*) as Counter from appointments where Start > ? AND Start < ? group by Type";
            JDBC.makePreparedStatement(query, con);
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            ps.setString(1, start);
            ps.setString(2, end);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                typeAmount = rs.getInt("Counter");
                type = rs.getString("Type");
                Reports.Typer typer = new Reports.Typer(typeAmount, type);
                typee.add(typer);
            }
            ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return typee;
    }

}
