package com.evoke.cn.evokecompanion.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

/**
 * @author Womble on 2/7/2017.
 *         Model for routing steps (aka Turns)
 */

public class RouteStep implements NavigationConstants {
    public String distance;
    public String duration;
    public String instruction;
    public String maneuver;
    private int parsedManeuver = NULL_VALUE;

    public LatLng endLoc;
    public LatLng startLoc;

    public String polyLine;


    public Polyline myOnMapPolyLine;

    public String toString() {
        return "turn Instruction: " + instruction + "\n" +
                "turn distance: " + distance + "\n" +
                "turn duration: " + duration + "\n" +
                "turn endLoc: " + endLoc + "\n" +
                "turn startLoc: " + startLoc + "\n" +
                "turn polyline: " + polyLine + "\n";
    }

    public int parseManeuver() {
        if (this.parsedManeuver != NULL_VALUE) {
            return parsedManeuver;
        }
        switch (this.maneuver) {
            /* Statements indicating turn left */
            case LEFT_STRING:
            case LEFT_FORK:
            case LEFT_RAMP:
            case LEFT_SHARP:
            case LEFT_SLIGHT:
            case KEEP_LEFT_STRING:
            case LEFT_ROUND: {
                parsedManeuver = TURN_LEFT;
                return TURN_LEFT;
            }
            /* Statements indicating turn right */
            case RIGHT_STRING:
            case RIGHT_FORK:
            case RIGHT_RAMP:
            case RIGHT_ROUND:
            case RIGHT_SHARP:
            case RIGHT_SLIGHT:
            case KEEP_RIGHT_STRING: {
                parsedManeuver = TURN_RIGHT;
                return TURN_RIGHT;
            }
            /* Statements indicating go straight */
            case STRAIGHT_STRING:
            case MERGE_STRING: {
                parsedManeuver = GO_STRAIGHT;
                return GO_STRAIGHT;
            }
            /* UTURN Statements */
            case UTURN_RIGHT:
            case UTURN_LEFT:{
                parsedManeuver = UTURN;
                return UTURN;
            }
            default:
                return DEFAULT;
        }
    }
}
