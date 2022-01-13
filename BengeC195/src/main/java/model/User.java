package model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class User {

    private static int userID;
    private String userName;
    private String password;
    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;
    private String tzOffset;

    public User(int userID) { User.userID = userID; }

    public void setUserName (String userName) { this.userName = userName; }
    public void setPassword (String password) { this.password = password; }
    public void setCreateDate (String createDate) { this.createDate = createDate; }
    public void setCreateBy (String createdBy) { this.createdBy = createdBy; }
    public void setLastUpdate (String lastUpdate) { this.lastUpdate = lastUpdate; }
    public void setLastUpdatedBy (String lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }
    public void setTzOffset (String tzOffset) { this.tzOffset = tzOffset; }

    public int getUserID() { return userID; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getCreateDate() { return createDate; }
    public String getCreatedBy() { return createdBy; }
    public String getLastUpdate() { return lastUpdate; }
    public String getLastUpdatedBy() { return lastUpdatedBy; }
    public String getTzOffset() { return tzOffset; }

    public static String getCurrentTimezoneOffset() {
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
        String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = (offsetInMillis >= 0 ? "+" : "-") + offset;
        return offset;
    }

}
