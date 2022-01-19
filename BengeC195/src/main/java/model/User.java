package model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Class User: Created to store instances of Users in memory.
 * Used to authenticate and log actions of end users of this application
 */

public class User {

    private static int userID;
    private String userName;
    private String password;
    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;
    // TzOffset is used to determine amount of time differ from UTC, this value is gathered by the java application and used in SQL to perform the data conversion.
    private String tzOffset;

/*
 *  @param userID userID is used as Primary key in MySQL data
 */
    public User(int userID) { User.userID = userID; }

    // setters
    public void setUserName (String userName) { this.userName = userName; }
    public void setPassword (String password) { this.password = password; }
    public void setCreateDate (String createDate) { this.createDate = createDate; }
    public void setCreateBy (String createdBy) { this.createdBy = createdBy; }
    public void setLastUpdate (String lastUpdate) { this.lastUpdate = lastUpdate; }
    public void setLastUpdatedBy (String lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }
    public void setTzOffset (String tzOffset) { this.tzOffset = tzOffset; }

    //getters
    public int getUserID() { return userID; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getCreateDate() { return createDate; }
    public String getCreatedBy() { return createdBy; }
    public String getLastUpdate() { return lastUpdate; }
    public String getLastUpdatedBy() { return lastUpdatedBy; }
    public String getTzOffset() { return tzOffset; }

    //The getCurrentTimeOffset functions exists for the purpose of determining time offset from Zulu or UTC so time conversions can be completed during SQL query time.
    public static String getCurrentTimezoneOffset() {
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
        String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = (offsetInMillis >= 0 ? "+" : "-") + offset;
        return offset;
    }

}
