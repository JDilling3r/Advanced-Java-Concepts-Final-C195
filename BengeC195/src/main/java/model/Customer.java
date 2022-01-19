package model;


/*
 * The customer class is used to create and manipulate instances of customers stored in MYSQL database provided.
 */
public class Customer {
    // private class variables for Customer
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

    // setters
    public void setCustomerID(int customerID) { this.customerID = customerID; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setAddress(String address) { this.address = address; }
    public void setZip(String zip)  { this.zip = zip; }
    public void setPhone(String phone)  { this.phone = phone; }
    public void setCreateDate(String createDate) { this.createDate = createDate; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }
    public void setLastUpdatedBy(String updatedBy) { this.lastUpdatedBy = updatedBy; }
    public void setDivisionID(int divisionID) {this.divisionID = divisionID; }

    // getters
    public int getCustomerID() { return customerID; }
    public String getCustomerName() { return customerName; }
    public String getAddress() { return address; }
    public String getZip() { return zip; }
    public String getPhone() { return phone; }
    public String getCreateDate() { return createDate; }
    public String getCreatedBy() { return createdBy; }
    public String getLastUpdate() { return lastUpdate; }
    public String getLastUpdatedBy() { return lastUpdatedBy; }
    public int getDivisionID() {return divisionID; }

}
