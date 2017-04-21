package com.evoke.cn.evokecompanion.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evoke.cn.evokecompanion.models.BluetoothConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Womble on 3/13/2017.
 *
 * Sends Requests to the Bluetooth management system
 * Does NOT receive the data from that request
 * to receive the data a class must subscribe as an observer to
 * the bluetoothintermediary subject
 */

public class StatsDataService implements BluetoothConstants{

    //Map<String,String[]> sentRequestIds;
    public StatsDataService(){
        //sentRequestIds = new HashMap<String,String[]>(100, (float) .75 );
    }

    public void requestAll(){
        requestBatteryStatus();
        requestBatteryHistory();
        requestOdometerReading();
        requestCurrentDriveDistance();
        requestMotorTemperature();
    }

    public void requestRecurring(){
        requestBatteryStatus();
        requestOdometerReading();
        requestMotorTemperature();
    }

    public void requestBatteryStatus(){
        saveKey(
                getConn().addRequest(GET_MESSAGE,GET_CONTENT[CHARGE_INDEX],false),
                CHARGE_INDEX
        );
    }

    public void requestBatteryHistory(){
        saveKey(
            getConn().addRequest(GET_MESSAGE,GET_CONTENT[C_HISTORY_INDEX],false),
                C_HISTORY_INDEX
        );
    }

    public void requestOdometerReading(){
        saveKey(
            getConn().addRequest(GET_MESSAGE,GET_CONTENT[ODOMETER_INDEX],false),
                ODOMETER_INDEX
        );
    }

    public void requestCurrentDriveDistance(){
        saveKey(
            getConn().addRequest(GET_MESSAGE,GET_CONTENT[DISTANCE_SINCE_INDEX],false),
                DISTANCE_SINCE_INDEX
        );
    }

    public void requestMotorTemperature(){
        saveKey(
            getConn().addRequest(GET_MESSAGE,GET_CONTENT[TEMPERATURE_INDEX],false),
                TEMPERATURE_INDEX
        );
    }

    private void saveKey(String key, int typeIndex){
        log("Request sent for type #"+typeIndex +"\nAssigned Key: "+key);
    }

    private BlueToothIntermediary getConn(){
        return BlueToothIntermediary.getInstance();
    }
    private void log(String message){
        Log.d("Data_Service",message);
    }
}