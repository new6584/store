package com.evoke.cn.evokecompanion.models;

/**
 * Created by Womble on 3/28/2017.
 *
 */

public interface NavigationConstants {
    /* Route Option Type Codes */
    int ECO = 0;
    int TIME = 1;
    int DISTANCE = 2;

    /* Narrow Responses Down To These */
    public static final int DEFAULT = -1;
    public static final int NULL_VALUE = -2;
    public static final int TURN_RIGHT = 0;
    public static final int TURN_LEFT = 1;
    public static final int GO_STRAIGHT = 2;
    public static final int END = 3;
    public static final int UTURN = 4;

    /* Possible Google Api Responses for Maneuver Field */
    public static final String LEFT_STRING = "turn-left";
    public static final String LEFT_SHARP = "turn-sharp-left";
    public static final String UTURN_RIGHT = "uturn-right";
    public static final String RIGHT_SLIGHT = "turn-slight-right";
    public static final String LEFT_ROUND = "roundabout-left";
    public static final String RIGHT_ROUND = "roundabout-right";
    public static final String UTURN_LEFT = "uturn-left";
    public static final String LEFT_SLIGHT = "turn-slight-left";
    public static final String RIGHT_RAMP = "ramp-right";
    public static final String RIGHT_FORK = "fork-right";
    public static final String LEFT_FORK = "fork-left";
    public static final String FERRY_TRAIN = "ferry-train";
    public static final String RIGHT_SHARP = "turn-sharp-right";
    public static final String LEFT_RAMP = "ramp-left";
    public static final String FERRY = "ferry";
    public static final String RIGHT_STRING = "turn-right";
    public static final String STRAIGHT_STRING = "straight";
    public static final String KEEP_LEFT_STRING = "keep-left";
    public static final String KEEP_RIGHT_STRING = "keep-right";
    public static final String MERGE_STRING = "merge";

}
