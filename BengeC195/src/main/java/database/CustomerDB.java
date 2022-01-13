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
