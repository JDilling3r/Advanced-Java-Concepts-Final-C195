package model;

/*
 * The customer class is used to create and manipulate instances of customers stored in MYSQL database provided.
 */
public class Customer {

    /**
     * Customer class holds 10 private variables
     */
    private int customerID;
    private String customerName;
    private String address;
    private String zip;
    private String phone;
    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;
    private int divisionID;

    public Customer(){}

    /*
     * @param customerID: this is the primary key of the customer table
     * @param customerName: this is the customer's full name
     * @param address: customers address
     * @param zip: postal code for customer
     * @param phone: this is the mobile phone for the customer
     * @param divisionID: this is a foreign key used to reference a table with location data
     *
     */
    public Customer(int customerID, String customerName, String address, String zip, String phone, int divisionID){
        this.customerID = customerID;
        this.customerName = customerName;
        this.address = address;
        this.zip = zip;
        this.phone = phone;
        this.divisionID = divisionID;
    }

    /**
     * @param customerID primary key for customer reference
     */
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    /**
     * @param customerName name of customer
     */
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    /**
     * @param address customer location of residence
     */
    public void setAddress(String address) { this.address = address; }

    /**
     * @param zip postal code of customer residence
     */
    public void setZip(String zip)  { this.zip = zip; }

    /**
     * @param phone customer mobile number
     */
    public void setPhone(String phone)  { this.phone = phone; }

    /**
     * @param createDate creation date of customer in customers table
     */
    public void setCreateDate(String createDate) { this.createDate = createDate; }

    /**
     * @param createdBy who created customer log input
     */
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    /**
     * @param lastUpdate date of last update to customer
     */
    public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }

    /**
     * @param updatedBy used to log who last updated customer
     */
    public void setLastUpdatedBy(String updatedBy) { this.lastUpdatedBy = updatedBy; }

    /**
     * @param divisionID foreign key used to reference location data in a separate table
     */
    public void setDivisionID(int divisionID) {this.divisionID = divisionID; }

    /**
     * @return customerID
     */
    public int getCustomerID() { return customerID; }

    /**
     * @return customerName
     */
    public String getCustomerName() { return customerName; }

    /**
     * @return address
     */
    public String getAddress() { return address; }

    /**
     * @return zip
     */
    public String getZip() { return zip; }

    /**
     * @return phone
     */
    public String getPhone() { return phone; }

    /**
     * @return createdBy
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
     * @return divisionID
     */
    public int getDivisionID() {return divisionID; }

}
