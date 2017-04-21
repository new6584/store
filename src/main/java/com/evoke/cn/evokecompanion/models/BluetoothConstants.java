package com.evoke.cn.evokecompanion.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Womble on 3/8/2017.
 * Constants for communication with a bluetooth Device
 */

public interface BluetoothConstants {
    public static final String HEADER = "Evoke";
    public static final String TERMINATOR ="ekovE";
    public static final String GET_CODE= "e";
    public static final String GIVE_CODE = "v";

    public static final String LENGTH_TERMINATOR = "#";
    public static final String CONTENT_TERMINATOR = "@";
    public static final String REQUEST_ID_TERMINATOR = "=";

    public static final String GET_MESSAGE = "";

    public static final String GET_HEADER = HEADER + GET_CODE + TERMINATOR;
    public static final String GIVE_HEADER = HEADER + GIVE_CODE + TERMINATOR;

    public static final int CHARGE_INDEX = 0;
    public static final int C_HISTORY_INDEX = 1;//are we actually keeping track of this bike side?
    public static final int ODOMETER_INDEX= 2;
    public static final int DISTANCE_SINCE_INDEX = 3; //are we actually keeping track of this?
    public static final int TEMPERATURE_INDEX = 4;
    public static final String[] GET_CONTENT = {
            "c", //Current charge
            "h",//Charge History
            "o",//odometer
            "d",//Distance traveled since bike on
            "t" //motor temperature
    };

    //ALL_INDEX sent as: maneuver summary distance 
    public static final int ALL_INDEX = 0;
    public static final int DIRECTION_INDEX = 1;
    public static final int DISTANCE_INDEX = 2;
    public static final int INSTRUCTION_INDEX = 3;
    public static final String[] GIVE_CONTENT = {
            "a",//All Direction Instructions
            "d",//only Direction data
            "s",//only distance data
            "i" //only instructions data
    };
    public static final String ITEM_SEPERATOR = "&";

    //Maneuver int codes
    public static final int MAN_STRAIGHT = 0;
    public static final int MAN_LEFT = 1;
    public static final int MAN_RIGHT = 2;
    public static final int MAN_UTURN = 3;
    public static final int MAN_TERMINATE = 4;//used for start end or null
        

}
