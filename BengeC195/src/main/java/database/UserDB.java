package database;

import application.Logger;
import model.User;
import java.sql.*;

public class UserDB {
    private static final Connection con = JDBC.getConnection();
    private static final String tzOffset = User.getCurrentTimezoneOffset();
    // the liveUser User will commonly be referenced for logging purposes on Java side and SQL
    private static User liveUser;
    public static User getLiveUser() { return liveUser; }
    // user authentication during log in form
    public static boolean authenticate(String UserIDorName, String password) throws SQLException {
        String query = "select * from users where User_ID =? AND Password =? or User_Name=? AND Password =?";
        JDBC.makePreparedStatement(query, con);
        PreparedStatement ps = JDBC.getPreparedStatement();
        assert ps != null;
        ps.setString(1, UserIDorName);
        ps.setString(2, password);
        ps.setString(3, UserIDorName);
        ps.setString(4, password);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            liveUser = new User((rs.getInt("User_ID")));
            liveUser.setUserName(rs.getString("User_Name"));
            liveUser.setPassword(rs.getString("Password"));
            liveUser.setCreateDate(rs.getString("Create_Date"));
            liveUser.setCreateBy(rs.getString("Created_By"));
            liveUser.setLastUpdate(rs.getString("Last_Update"));
            liveUser.setLastUpdatedBy(rs.getString("Last_Updated_By"));
            liveUser.setTzOffset(tzOffset);
            Logger.log(liveUser.getUserName(), Boolean.TRUE);
            return Boolean.TRUE;
        }

        else {
            Logger.log(UserIDorName,Boolean.FALSE);
            return Boolean.FALSE;
        }
    }
}

