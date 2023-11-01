package com.example.locationpinned;

import java.io.Serializable;

public class AddressObject implements Serializable {

    double latitude;
    double longitude;
    String locationName;
    int addressID;

    //Getter method for getting database id of address associated with AddressObject instance
    public int getAddressID() {
        return this.addressID;
    }

    //Setter method for setting database id of address associated with AddressObject instance
    public void setAddressID(int id) {
        this.addressID = id;
    }

    //Getter method for obtaining geocoded location name
    public String getLocationName() {
        return this.locationName;
    }

    //Setter method for setting geocoded location name
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    //Getter method for getting latitude of address
    public double getLatitude() {
        return this.latitude;
    }

    //Setter method for setting latitude of address
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    //Getter method for getting longitude of address
    public double getLongitude() {
        return this.longitude;
    }

    //Setter method for setting longitude of address
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
