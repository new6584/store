package com.evoke.cn.evokecompanion.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * @author  Womble on 2/7/2017.
 * Model for a single Route found
 */

public class Route {

    public String status;
    public String fullPolyline;

    public String startAddress;
    public String endAddress;
    public LatLng startLoc;
    public LatLng endLoc;

    public String totalDistance;
    public String totalDuration;
    public String totalDurationValue;
    public String totalDistanceValue;

    public LatLng neBounds;
    public LatLng swBounds;

    /* Stores the most recent value of the odometer when route began */
    public double startingOdometer = 0;
    /* Stores the totals of each step on finish, based off step distance value NOT odometer reading */
    public double totalCompletedStepsDistance = 0;

    public ArrayList<RouteStep> turns ;
    public int currentStepIndex;

    public Route(){
        turns = new ArrayList<RouteStep>();
        currentStepIndex = 0;
    }


    public void addTurn(String distance, String duration, String instruction,
                        LatLng endLoc, LatLng startLoc, String polyline, String maneuver){
        RouteStep toAdd = new RouteStep();
        toAdd.distance = distance;
        toAdd.duration = duration;
        toAdd.instruction = instruction;
        toAdd.endLoc = endLoc;
        toAdd.startLoc = startLoc;
        toAdd.polyLine = polyline;
        toAdd.maneuver = maneuver;
        turns.add(toAdd);
    }
    public RouteStep getNextStep(){
        int index = currentStepIndex;
        if(turns.size() < currentStepIndex){
            currentStepIndex ++;
            return turns.get(index);
        }
        return null;
    }
    public RouteStep getCurrentStep(){
        return turns.get(currentStepIndex);
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
    public static boolean wasSuccess(String stat) {
        if (stat.equals("\"OK\"") || stat.equals("OK")) {
            return true;
        }
        return false;
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

    public String toString(){
        return "status: "+ status + "\n" +
                "f-poly: "+fullPolyline+ "\n" +
                "start: "+startAddress+ "\n" +
                "end: "+endAddress+ "\n" +
                "startLoc: "+startLoc+ "\n" +
                "endLoc: "+endLoc+ "\n" +
                "distance: "+totalDistance+ "\n" +
                "duration: "+totalDuration+ "\n" +
                "ne Bound: "+neBounds+ "\n" +
                "sw Bound: "+swBounds+ "\n"+
                "STEPS: " + "\n"+
                stepsToString();
    }
    public String stepsToString(){
        String stepsStringHolder="";
        for(int i=0; i < turns.size(); i++){
            stepsStringHolder += turns.get(i).toString();
        }
        return stepsStringHolder;
    }

    public int getCount() {
        return turns.size();
    }
    public RouteStep getStep(int index){
        return turns.get(index);
    }
}
