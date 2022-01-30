package model;

/**
 * Class Appointment defines instances of customer appointments gathered from MYSQL table appointments.
 */
public class Appointment {

    /**
     * class Appointment holds 16 private instance variables
     */
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

    /*
     * The appointment class is used to create and store instances of appointments from the MYSQL database
     *
     * @param apptID: is the appointment database primary key
     * @param title: title of the appointment
     * @param description: description of the appointment
     * @param location: location of the appointment
     * @param contactID: this is a foreign key used to reference the contact database
     * @param type: this denotes the type of appointment
     * @param start: time and date of the appointment
     * @param end: time and date the appointment ends
     * @param customerID: foreign key used to reference the customers table
     * @param userID: foreign key used to reference the users table
     * @param contact: this value is the result of a join SQL query grabbing the contact name
     *
     */
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

    /**
     * @param ID primary key for appointment row
     */
    public void setApptID(int ID){ this.apptID = ID; }

    /*
     * @param title string set by customer for appointment
     */
    public void setTitle(String title){ this.title = title; }

    /**
     * @param description appointment description
     */
    public void setDescription(String description){ this.description = description; }

    /**
     * @param location appointment location.
     */
    public void setLocation(String location){ this.location = location; }
    /**
     * @param contactID Foreign key for contacts
     */
    public void setContactID(int contactID){ this.contactID = contactID; }

    /**
     * @param type descriptor for appointment type
     */
    public void setType(String type){ this.type = type; }

    /**
     * @param start appointment start time.
     */
    public void setStart(String start){ this.start = start; }

    /**
     * @param end appointment end time
     */
    public void setEnd(String end){ this.end = end; }

    /**
     * @param createDate appointment creation date
     */
    public void setCreateDate(String createDate){ this.createDate = createDate; }

    /**
     * @param createdBy logging who created appointment
     */
    public void setCreatedBy(String createdBy){ this.createdBy = createdBy; }

    /**
     * @param lastUpdate time appointment was last updated
     */
    public void setLastUpdate(String lastUpdate){ this.lastUpdate = lastUpdate; }

    /**
     * @param lastUpdatedBy logging who last updated the appointment
     */
    public void setLastUpdatedBy(String lastUpdatedBy){ this.lastUpdatedBy = lastUpdatedBy; }

    /**
     * @param customerName customer name on appointment
     */
    public void setCustomerName(String customerName) {this.customerName = customerName; }

    /**
     * @param customerID foreign key referencing customer table
     */
    public void setCustomerID(int customerID){ this.customerID = customerID; }

    /**
     * @param userID foreign key referencing user table
     */
    public void setUserID(int userID){ this.userID = userID; }

    /**
     * @param contact name of contact gathered from SQL join to contact table
     */
    public void setContact(String contact){ this.contact = contact;}

    /**
     * @return apptID
     */
    public int getApptID(){ return apptID; }

    /**
     * @return title
     */
    public String getTitle(){ return title;}

    /**
     * @return description
     */
    public String getDescription(){ return description; }

    /**
     * @return location
     */
    public String getLocation(){ return location; }

    /**
     * @return customerName
     */
    public String getCustomerName(){ return customerName; }

    /**
     * @return contactID
     */
    public int getContactID(){ return contactID; }

    /**
     * @return type
     */
    public String getType(){ return type; }
    /**
     * @return start
     */
    public String getStart(){ return start; }

    /**
     * @return end
     */
    public String getEnd(){ return end; }

    /**
     * @return createDate
     */
    public String getCreateDate(){ return createDate; }

    /**
     * @return createBy
     */
    public String getCreatedBy(){ return createdBy; }

    /**
     * @return lastUpdate
     */
    public String getLastUpdate(){ return lastUpdate; }

    /**
     * @return lastUpdatedBy
     */
    public String getLastUpdatedBy(){ return lastUpdatedBy; }

    /**
     * @return customerID
     */
    public int getCustomerID(){ return customerID; }

    /**
     * @return userID
     */
    public int getUserID(){ return userID; }

    /**
     * @return contact
     */
    public String getContact(){return contact;}

}




