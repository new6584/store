package com.evoke.cn.evokecompanion.services;

import android.os.AsyncTask;
import android.util.Log;

//import com.evoke.cn.evokecompanion.GoogleMapFragment.MapController;
import com.evoke.cn.evokecompanion.models.APIConstants;
import com.evoke.cn.evokecompanion.models.AddressResults;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Evoke on 1/19/2017.
 * TODO: On Pause reset http traffic; try blocks;
 * Todo: remove Markers on new search
 * This Class creates an asynchronus request to google web service.
 * Each instance of this class is intended to handle only one request
 */

public class UriGeocodingService extends AsyncTask<String, Void, String>
        implements APIConstants {

    private String originalRequestString;

    private double currentLat;
    private double currentLong;
    private boolean hasCurrLoc;
//    private MapController controller ;
//
//    public UriGeocodingService(MapController _c) {
//        hasCurrLoc = false;
//        controller = _c;
//    }
//
//    /**
//     * Constructor with the current lat and lng included
//     *
//     * @param lat
//     * @param lng
//     */
//    public UriGeocodingService(double lat, double lng, MapController _c) {
//        currentLat = lat;
//        currentLong = lng;
//        hasCurrLoc = true;
//
//        controller = _c;
//    }

    /**
     * generates a html request and uses this instance to process the request asynchronously
     *
     * @param address
     */
    public void findAddress(String address) {
        originalRequestString = address;
        //1600+Amphitheatre+Parkway,+Mountain+View,+CA
        log("Making URL request for:" + address);
        String requestUri;
        String[] addressParts = address.split(" ");
        requestUri = baseGeocodeUri;
        if (addressParts.length > 1) {
            requestUri = requestUri + addressParts[0];
            for (int i = 1; i < addressParts.length; i++) {
                requestUri = requestUri + "+" + addressParts[i];
            }
        } else if (addressParts.length == 1) {
            requestUri = requestUri + addressParts[0];
        } else {
            requestUri = null;
            return;
        }
        preformExecute(requestUri);
    }

    /**
     * executes the http request
     *
     * @param leadingUri
     */
    private void preformExecute(String leadingUri) {
        String fullUri = leadingUri + apiKeyUri;
        String[] params = new String[2];
        params[0] = fullUri;
        this.execute(params);
    }

    @Override
    protected String doInBackground(String... urls) {
        String response = "";
        try {
            log("Sending Url request to: " + urls[0]);
            URL aURL = new URL(urls[0]);

            final HttpURLConnection aHttpURLConnection = (HttpURLConnection) aURL.openConnection();

            InputStream aInputStream = aHttpURLConnection.getInputStream();
            BufferedInputStream in = new BufferedInputStream(
                    aInputStream);
            byte[] contents = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = in.read(contents)) != -1) {
                response += new String(contents, 0, bytesRead);
            }
        } catch (IOException e) {
            log(e.toString());
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        JsonObject jelement = new JsonParser().parse(result).getAsJsonObject();
        log("URI Geocoding responded with status: " + jelement.get("status").toString());

        AddressResults theResultObject = new AddressResults();

        theResultObject.status = jelement.get("status").toString();
        theResultObject.searchedAddress = originalRequestString;

        if (!theResultObject.wasSuccess()) {
            //TODO: failed request
            log("Request Failed");
            return;
        } else if (!theResultObject.hasResults()) {
            //TODO: no results
            log("Request had no results");
            return;
        }

        JsonArray masterTag = jelement.get("results").getAsJsonArray();
        AddressResults lastClosest = new AddressResults();
        ArrayList<AddressResults> resSet = new ArrayList<>();

        lastClosest.distanceFromUser = -1;
        for (int i = 0; i < masterTag.size(); i++) {
            log("counter:" + i);
            //log(jelement.get("results").getAsJsonArray().get(i).getAsString());

            JsonObject resTag = jelement.get("results").getAsJsonArray().get(i).getAsJsonObject(); // each address
            log(resTag.toString());
            log(resTag.get("formatted_address").toString());
            theResultObject.fullAddress = resTag.get("formatted_address").getAsString();

            JsonObject locationTag = resTag.get("geometry").getAsJsonObject().get("location").getAsJsonObject();
            theResultObject.latitude = locationTag.get("lat").getAsFloat();
            theResultObject.longitude = locationTag.get("lng").getAsFloat();

            JsonObject viewportTag = resTag.get("geometry").getAsJsonObject().get("viewport").getAsJsonObject();
            theResultObject.northEastLatitude = viewportTag.get("northeast").getAsJsonObject().get("lat").getAsFloat();
            theResultObject.northEastLongitude = viewportTag.get("northeast").getAsJsonObject().get("lng").getAsFloat();
            theResultObject.southWestLatitude = viewportTag.get("southwest").getAsJsonObject().get("lat").getAsFloat();
            theResultObject.southWestLongitude = viewportTag.get("southwest").getAsJsonObject().get("lng").getAsFloat();

            theResultObject.place_id = resTag.get("place_id").getAsString();
            log("location found: \n" + theResultObject.toString(true));
            if (hasCurrLoc) {
                theResultObject.distanceFromUser = theResultObject.getDistanceHaversine((float) currentLat,(float) currentLong);
                if ((lastClosest.distanceFromUser == -1) || (theResultObject.distanceFromUser < lastClosest.distanceFromUser)) {
                    log("Cloning for Last Closest");
                    lastClosest = theResultObject.clone();
                }
            }
            //make a marker
            resSet.add(theResultObject);
            //new results
            theResultObject = initResultObj(jelement.get("status").toString());
        }

        log("Closest");
        log(lastClosest.toString(true));

        if(lastClosest.fullAddress != null) {
            //capture in frame
            passToController(resSet,lastClosest);
        }else{
            passToController(resSet,null);
        }
    }
    private void passToController(ArrayList<AddressResults> resSet, AddressResults closest){
        //controller.showAddressesOnMap(resSet, closest);
    }

    private AddressResults initResultObj(String stat) {
        AddressResults theResultObject = new AddressResults();
        theResultObject.status = stat;
        theResultObject.searchedAddress = originalRequestString;

        return theResultObject;
    }

    private void log(String message) {
        Log.d("URI_Geo: ", message);
    }

    public void testURI() {
        originalRequestString = "Test String";
        //preformExecute(baseGeocodeUri +"1+Tiantan+E+Rd,+Dongcheng+Qu,+Beijing+Shi,+China" );
        //preformExecute(baseGeocodeUri + "123+haappy+path+gilboa+ny+usa");
        preformExecute(baseGeocodeUri + "smith+street");
    }
}
