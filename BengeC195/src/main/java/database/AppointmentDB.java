package database;

import model.Appointment;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppointmentDB {
    private static final Connection con = JDBC.getConnection();
    private static final String tzOffset = UserDB.getLiveUser().getTzOffset();

    public static ObservableList<Appointment> getAppointments(int customerID, int days) {
        Appointment appointment;
        ObservableList<Appointment> Appts = FXCollections.observableArrayList();
        try {
            // This query will collect all appointment data, convert the specifica user's local time to zulu, and join contactnames from contacts table.
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

    /*public static Appointment getAppointment(int AID) throws SQLException {
        Appointment appointment = new Appointment();
        try {
            // This query will collect appointment data for specific appointment.
            String query = "select * from appointments where Appointment_ID=?";
            JDBC.makePreparedStatement(query, con);
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            ps.setString(1, String.valueOf(AID));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                appointment.setTitle(rs.getString("Title"));
                appointment.setDescription("Description");
                appointment.setType("Type");
            }
            ps.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
        return appointment;
    }
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

    public static boolean addAppointment(String title, String description, String location, int contactID, String type, String start, int customerID) {
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
        // Checking for overlapping appointments
        try {
            String query = "select Appointment_ID from appointments where \n" +
                    "Start <= convert_tz(?, ?, '+00:00') \n" +
                    "and end >= convert_tz(?, ?, '+00:00') \n" +
                    "or Start <= (convert_tz(?, ?, '+00:00') + interval 1 hour)\n" +
                    "and end >= (convert_tz(?, ?, '+00:00') + interval 1 hour)";
            JDBC.makePreparedStatement(query, con);
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            int i = 1;
            while (i < 8) {
                ps.setString(i, start);
                i++;
                ps.setString(i, tzOffset);
                i++;
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                overlap = Boolean.TRUE;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if (overlap){
            return Boolean.FALSE;
        }

        else {
            try {
                // Prepared Statement Set Index Numbers:                    1        2              3           4       5                   6  7                             8  9                                                              10             11         12            13                 14             15
                String query = "insert into appointments set Appointment_ID=?, Title=?, Description=?, Location=?, Type=?, Start=convert_tz(?, ?, '+00:00'), End=(convert_tz(?, ?, '+00:00') + interval 1 hour), Create_Date=now(), Created_By=?, Customer_ID=?, User_ID=?, Contact_ID=?, Last_Updated_by=?, Last_Update=now();";
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
                ps.setString(10, UserDB.getLiveUser().getUserName());
                ps.setInt(11, customerID);
                ps.setInt(12, UserDB.getLiveUser().getUserID());
                ps.setInt(13, contactID);
                ps.setString(14, UserDB.getLiveUser().getUserName());
                ps.execute();
                ps.close();
                return Boolean.TRUE;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return false;
    }


    public static boolean editAppointment(int apptID, String title, String description, String location, int contactID, String type, String start, int customerID) {
        boolean overlap = Boolean.FALSE;

        try {
            String query = "select Appointment_ID from appointments where \n" +
                    "Start <= convert_tz(?, ?, '+00:00') \n" +
                    "and end >= convert_tz(?, ?, '+00:00') \n" +
                    "or Start <= (convert_tz(?, ?, '+00:00') + interval 1 hour)\n" +
                    "and end >= (convert_tz(?, ?, '+00:00') + interval 1 hour)";
            JDBC.makePreparedStatement(query, con);
            PreparedStatement ps = JDBC.getPreparedStatement();
            assert ps != null;
            int i = 1;
            while (i < 8) {
                ps.setString(i, start);
                i++;
                ps.setString(i, tzOffset);
                i++;
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                overlap = Boolean.TRUE;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        if (overlap){
            return Boolean.FALSE;
        }

        else {
            try {
                // Prepared Statement Set Index Numbers:      1              2           3       4                   5  6                             7  8                                            9         10            11                 12                                          13
                String query = "update appointments set Title=?, Description=?, Location=?, Type=?, Start=convert_tz(?, ?, '+00:00'), End=(convert_tz(?, ?, '+00:00') + interval 1 hour), Customer_ID=?, User_ID=?, Contact_ID=?, Last_Updated_by=?, Last_Update=now(); where Appointment_ID=?";
                JDBC.makePreparedStatement(query, con);
                PreparedStatement ps = JDBC.getPreparedStatement();
                assert ps != null;
                ps.setString(1, title);
                ps.setString(2, description);
                ps.setString(3, location);
                ps.setString(4, type);
                ps.setString(5, start);
                ps.setString(6, tzOffset);
                ps.setString(7, start);
                ps.setString(8, tzOffset);
                ps.setInt(9, customerID);
                ps.setInt(10, UserDB.getLiveUser().getUserID());
                ps.setInt(11, contactID);
                ps.setString(12, UserDB.getLiveUser().getUserName());
                ps.setInt(13,apptID);
                ps.execute();
                ps.close();
                return Boolean.TRUE;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return false;
    }

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

    // getMonthAppointmentAmount is used to populate pie chart in Reports view.
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return aptAmount;
    }

}
