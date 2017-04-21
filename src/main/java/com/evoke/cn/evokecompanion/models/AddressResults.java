package com.evoke.cn.evokecompanion.models;

import java.util.ArrayList;

/**
 * Created by Womble on 1/20/2017.
 * Stores address locations and provides methods for interacting with them
 */

public class AddressResults {

    public String searchedAddress;

    public String fullAddress;

    public ArrayList<String> addressParts;

    public float latitude;
    public float longitude;

    //Viewport info
    public float northEastLatitude;
    public float northEastLongitude;
    public float southWestLatitude;
    public float southWestLongitude;

    public String status;

    public String place_id;

    public double distanceFromUser = -1;

    /**
     * Haversine formula for calculating estimated distances between this and another long/lat values
     * returns the estimated distance between this point and the one entered
     * in meters
     *
     * @return double
     */
    public double getDistanceHaversine(float lat2, float lon2) {
        if ((latitude == 0.0 && longitude == 0.0) || (lat2 == 0.0 && lon2 == 0.0)) {
            return -1;
        }
        double R = 6371e3; // metres
        double r1 = Math.toRadians(latitude);
        double r2 = Math.toRadians(lat2);
        double latDif = Math.toRadians((lat2 - latitude)); //Δφ
        double longDif = Math.toRadians((lon2 - longitude));

        double a = Math.sin(latDif / 2) * Math.sin(latDif / 2) +
                Math.cos(r1) * Math.cos(r2) *
                        Math.sin(longDif / 2) * Math.sin(longDif / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (R * c);
    }

    /**
     * Performs the Haversine between two address locations
     */
    public double haversineBetweenAddress(AddressResults ad2) {
        return getDistanceHaversine(ad2.latitude, ad2.longitude);
    }

    /**
     * interprets status result
     * returns true if the status code indicates a successful query
     *
     * @return boolean
     */
    public boolean wasSuccess() {
        if (status.equals("\"OK\"") || status.equals("OK")) {
            return true;
        }
        return false;
    }

    public void addPart(String part){
        addressParts.add(part);
    }
    /**
     * interprets status result
     * returns true if the status code indicates a populated result
     *
     * @return boolean
     */
    public boolean hasResults() {
        if (status.equals(" \"ZERO_RESULTS\" ") || status.equals("ZERO_RESULTS")) {
            return false;
        }
        return true;
    }

    /**
     * Test method, returns all values held by this class
     *
     * @param showAll
     * @return
     */
    public String toString(boolean showAll) {
        if (showAll) {
            return searchedAddress + "\n" + fullAddress + "\n" + longitude + "," + latitude + "\n" +
                    northEastLatitude + "," + northEastLongitude + "\n" +
                    southWestLatitude + "," + southWestLongitude + "\n" +
                    distanceFromUser + "\n"+
                    place_id;
        }
        return toString();
    }

    /**
     * Returns most basic information
     *
     * @return string
     */
    public String toString() {
        return fullAddress + " @ (" + latitude + "," + longitude + ")"+" \n"+distanceFromUser;
    }

    @Override
    public AddressResults clone() { //clone not supported
        AddressResults deepCopy = new AddressResults();

        deepCopy.distanceFromUser = this.distanceFromUser;
        deepCopy.searchedAddress = this.searchedAddress;
        deepCopy.fullAddress = this.fullAddress;
        deepCopy.latitude = this.latitude;
        deepCopy.longitude = this.longitude;
        deepCopy.northEastLatitude = this.northEastLatitude;
        deepCopy.northEastLongitude = this.northEastLongitude;
        deepCopy.southWestLatitude = this.southWestLatitude;
        deepCopy.southWestLongitude = this.southWestLongitude;
        deepCopy.status = this.status;
        deepCopy.place_id = this.place_id;
        deepCopy.distanceFromUser = this.distanceFromUser;
        deepCopy.addressParts = this.addressParts;

        return deepCopy;
    }

    /**
     * Default Constructor
     */
    public AddressResults() {
        addressParts = new ArrayList<String>();
    }
}
