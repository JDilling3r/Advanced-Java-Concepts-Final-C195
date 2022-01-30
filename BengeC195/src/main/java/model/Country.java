package model;

/**
 * Country Class: defines instances of countries populated from countries table from MYSQL
 */
public class Country {

    /**
     * 2 private variables in class Country.
     */
    private final int countryID;
    private final String countryName;

    /**
     * Class Country sets structure for instances required to autopopulate user locations
     *
     * @param countryID: The primary key for countries table.
     * @param countryName: Name of the country specified.
     *
     */
    public Country(int countryID, String countryName){
        this.countryID = countryID;
        this.countryName = countryName;
    }

    /**
     * getCountryID
     * @return Int countryID
     */
    public int getCountryID() { return countryID; }

    /**
     * getCountryName
     * @return String countryName
     */
    public String getCountryName() { return countryName; }

}
