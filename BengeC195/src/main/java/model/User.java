package model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Class User: Created to store instances of Users in memory.
 * Used to authenticate and log actions for end users of the application
 */

public class User {

    /**
     * TzOffset is used to determine amount of time differ from UTC, this value is gathered by the java application and used in SQL to perform the data conversion.
     * class User holds 8 private instance variables
     */
    private static int userID;
    private String userName;
    private String password;
    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;
    private String tzOffset;

/*
 *  @param userID userID is used as Primary key in MySQL data
 */
    public User(int userID) { User.userID = userID; }

    /**
     * @param userName name of the current user
     */
    public void setUserName (String userName) { this.userName = userName; }

    /**
     * @param password password retrieved through user input on login page
     */
    public void setPassword (String password) { this.password = password; }

    /**
     * @param createDate user creation date
     */
    public void setCreateDate (String createDate) { this.createDate = createDate; }

    /**
     * @param createdBy avenue of user creation logged
     */
    public void setCreateBy (String createdBy) { this.createdBy = createdBy; }

    /**
     * @param lastUpdate time of last update to user
     */
    public void setLastUpdate (String lastUpdate) { this.lastUpdate = lastUpdate; }

    /**
     * @param lastUpdatedBy name of user who last updated user.
     */
    public void setLastUpdatedBy (String lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }

    /**
     * @param tzOffset yields time zone offset of end user for application.
     */
    public void setTzOffset (String tzOffset) { this.tzOffset = tzOffset; }

    /**
     * @return userID
     */
    public int getUserID() { return userID; }

    /**
     * @return userName
     */
    public String getUserName() { return userName; }

    /**
     * @return password
     */
    public String getPassword() { return password; }

    /**
     * @return createdDate
     */
    public String getCreateDate() { return createDate; }

    /**
     * @return createdBy
     */
    public String getCreatedBy() { return createdBy; }

    /**
     * @return lastUpdate
     */
    public String getLastUpdate() { return lastUpdate; }

    /**
     * @return lastUpdatedBy
     */
    public String getLastUpdatedBy() { return lastUpdatedBy; }

    /**
     * @return tzOffset
     */
    public String getTzOffset() { return tzOffset; }

    /**
     * @return offset;
     * offset is calculated based on current end user timezone difference to UTC or Zulu timezone
     */
    public static String getCurrentTimezoneOffset() {
        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
        String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = (offsetInMillis >= 0 ? "+" : "-") + offset;
        return offset;
    }

}
