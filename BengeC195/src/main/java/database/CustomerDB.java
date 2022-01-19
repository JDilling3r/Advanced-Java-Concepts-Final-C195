package database;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;

public class CustomerDB {
    private static final Connection con = JDBC.getConnection();
    private static final ObservableList<Customer> customers = FXCollections.observableArrayList();

    public static ObservableList<Customer> getAllCustomers() throws SQLException {
        customers.clear();
        try{
        String query = "select * from customers";
        JDBC.makePreparedStatement(query, con);
        PreparedStatement ps = JDBC.getPreparedStatement();
        assert ps != null;
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Customer customer = new Customer(rs.getInt("Customer_ID"), rs.getString("Customer_Name"),
                    rs.getString("Address"), rs.getString("Postal_Code"),
                    rs.getString("Phone"), rs.getInt("Division_ID"));
            customers.add(customer);
        }
        ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return customers;
    }

    public static void addCustomer (String customerName, String address, String zip, String phone, int divisionID) throws SQLException {
        int CID = 1;
        try {
            Statement id = con.createStatement();
            ResultSet rs = id.executeQuery("select max(Customer_ID) as Last_Customer from customers");
            if (rs.next()) {
                CID = rs.getInt("Last_Customer") + 1;
            }
            id.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        //Prepared Statement Set Index Numbers:               1                2          3              4        5                                6                  7                                 8
        String query = "insert into customers set Customer_ID=?, Customer_Name=?, Address=?, Postal_Code=?, Phone=?, Create_Date=now(), Created_By=?, Last_Updated_by=?, Last_Update=now(), Division_ID=?;";
        JDBC.makePreparedStatement(query, con);
        PreparedStatement ps = JDBC.getPreparedStatement();
        assert ps != null;
        ps.setInt(1,CID);
        ps.setString(2, customerName);
        ps.setString(3, address);
        ps.setString(4, zip);
        ps.setString(5, phone);
        ps.setString(6, UserDB.getLiveUser().getUserName());
        ps.setString(7, UserDB.getLiveUser().getUserName());
        ps.setInt(8, divisionID);
        ps.execute();
        ps.close();
        getAllCustomers();
    }

    public static void editCustomer (int CID, String customerName, String address, String zip, String phone, int divisionID) throws SQLException {
        //Prepared Statement Set Index Numbers:            1          2              3        4                  5                                 6                   7
        String query = "update customers set Customer_Name=?, Address=?, Postal_Code=?, Phone=?, Last_Updated_by=?, Last_Update=now(), Division_ID=? where Customer_ID=?;";
        JDBC.makePreparedStatement(query, con);
        PreparedStatement ps = JDBC.getPreparedStatement();
        assert ps != null;
        ps.setString(1, customerName);
        ps.setString(2, address);
        ps.setString(3, zip);
        ps.setString(4, phone);
        ps.setString(5, UserDB.getLiveUser().getUserName());
        ps.setInt(6, divisionID);
        ps.setInt(7, CID);
        ps.execute();
        ps.close();
        getAllCustomers();
    }

    public static boolean  deleteCustomer (int customerID) {
     try {
         String check = "select Customer_ID from appointments where Customer_ID=?";
         JDBC.makePreparedStatement(check, con);
         PreparedStatement checker = JDBC.getPreparedStatement();
         assert checker != null;
         checker.setInt(1, customerID);
         ResultSet checked = checker.executeQuery();
         if (checked.next()) {
             checker.close();
             return Boolean.FALSE;
         }
         else {
             checker.close();
             String query = "delete from customers where Customer_ID=?";
             JDBC.makePreparedStatement(query, con);
             PreparedStatement ps = JDBC.getPreparedStatement();
             assert ps != null;
             ps.setInt(1, customerID);
             ps.execute();
             ps.close();
             getAllCustomers();
             return Boolean.TRUE;
         }
     }catch (SQLException throwables) {
         throwables.printStackTrace();
     }
     return Boolean.FALSE;
    }

}
