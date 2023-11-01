package com.example.locationpinned;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocoderMaker {
    private final double lat;
    private final double longit;

    Context context;

    /*GeocodeMaker object receives a latitude, longitude and application context,
    returns geocoded address as String*/
    public GeocoderMaker(double lat, double longit, Context context) {

        this.lat = lat;
        this.longit = longit;
        this.context = context;

    }

    //Receives latitude and longitude parameters from constructor, retrieves geocoded address
    public String geocodedAddress() {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            // Perform reverse geocoding
            List<Address> addresses = geocoder.getFromLocation(lat, longit, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);

            } else {
                // Handle no address found
                return "Unknown Address";
            }
        } catch (IOException e) {
            return "Unknown Address";
        } catch (IllegalArgumentException e) {
            // Handles invalid coordinate inputs
            return "Invalid location";
        }


    }


}
