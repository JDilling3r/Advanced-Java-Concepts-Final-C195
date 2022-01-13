package database;

import model.User;
import java.sql.*;

public class UserDB {
    private static final Connection con = JDBC.getConnection();
    private static User liveUser;
    public static User getLiveUser() { return liveUser; }

    public static boolean authenticate(int userID, String password) throws SQLException {
        String query = "select * from users where User_ID =? AND Password =?";
        JDBC.makePreparedStatement(query, con);
        PreparedStatement ps = JDBC.getPreparedStatement();
        assert ps != null;
        ps.setInt(1, userID);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            liveUser = new User((rs.getInt("User_ID")));
            liveUser.setUserName(rs.getString("User_Name"));
            liveUser.setPassword(rs.getString("Password"));
            liveUser.setCreateDate(rs.getString("Create_Date"));
            liveUser.setCreateBy(rs.getString("Created_By"));
            liveUser.setLastUpdate(rs.getString("Last_Update"));
            liveUser.setLastUpdatedBy(rs.getString("Last_Updated_By"));
            liveUser.setTzOffset(User.getCurrentTimezoneOffset());

            return Boolean.TRUE;
        }

        else {
            return Boolean.FALSE;
        }
    }
}

