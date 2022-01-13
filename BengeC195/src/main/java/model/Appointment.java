package model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Appointment {

    private int apptID;
    private String title;
    private String contact;
    private String description;
    private String location;
    private int contactID;
    private String type;
    private String start;
    private String end;
    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;
    private String customerName;
    private int customerID;
    private int userID;


    public Appointment (){}
    public Appointment(int apptID, String title, String description, String location, int contactID, String type, String start, String end, int customerID, int userID, String contact) {
        this.apptID = apptID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contactID = contactID;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerID = customerID;
        this.userID = userID;
        this.contact = contact;
    }

    public void setApptID(int ID){ this.apptID = ID; }
    public void setTitle(String title){ this.title = title; }
    public void setDescription(String description){ this.description = description; }
    public void setLocation(String location){ this.location = location; }
    public void setContactID(int contactID){ this.contactID = contactID; }
    public void setType(String type){ this.type = type; }
    public void setStart(String start){ this.start = start; }
    public void setEnd(String end){ this.end = end; }
    public void setCreateDate(String createDate){ this.createDate = createDate; }
    public void setCreatedBy(String createdBy){ this.createdBy = createdBy; }
    public void setLastUpdate(String lastUpdate){ this.lastUpdate = lastUpdate; }
    public void setLastUpdatedBy(String lastUpdatedBy){ this.lastUpdatedBy = lastUpdatedBy; }
    public void setCustomerName(String customerName) {this.customerName = customerName; }
    public void setCustomerID(int customerID){ this.customerID = customerID; }
    public void setUserID(int userID){ this.userID = userID; }
    public void setContact(String contact){ this.contact = contact;}

    public int getApptID(){ return apptID; }
    public String getTitle(){ return title;}
    public String getDescription(){ return description; }
    public String getLocation(){ return location; }
    public String getCustomerName(){ return customerName; }
    public int getContactID(){ return contactID; }
    public String getType(){ return type; }
    public String getStart(){ return start; }
    public String getEnd(){ return end; }
    public String getCreateDate(){ return createDate; }
    public String getCreatedBy(){ return createdBy; }
    public String getLastUpdate(){ return lastUpdate; }
    public String getLastUpdatedBy(){ return lastUpdatedBy; }
    public int getCustomerID(){ return customerID; }
    public int getUserID(){ return userID; }
    public String getContact(){return contact;}

}




