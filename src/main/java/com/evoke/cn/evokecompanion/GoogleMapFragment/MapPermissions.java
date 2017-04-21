package com.evoke.cn.evokecompanion.GoogleMapFragment;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * @author Womble 2/5/2017.
 */

public class MapPermissions {
//    public static final int FINE_LOCATION_REQ_CODE = 1;
//    private MapController controller;
//
//    public MapPermissions(MapController _c) {
//        controller = _c;
//    }
//
//    public boolean requestPrimaryPermissions(){// returns true if has to ask user
//        if( ! hasLocationPermission() ){
//             requestFineLocation();
//            return true;
//        }
//        return false;
//    }
//
//    public void requestFineLocation() {
//        log("Checking Location permissions");
//        if ((ActivityCompat.checkSelfPermission(controller.getActivity(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(controller.getActivity().getApplicationContext(),
//                android.Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED)) {
//            log("Permission not granted, requesting");
//            requestAPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_REQ_CODE);
//        }
//    }
//
//    /**
//     * ask user for permission for a feature
//     */
//    private void requestAPermission(String permCode, int reqCode) {
//        String[] aPerm = new String[]{permCode};
//        ActivityCompat.requestPermissions(controller.getActivity(),
//                aPerm,
//                reqCode);
//    }
//
//
//    //checkers
//
//    /**
//     * This method checks if the application has fine location permissions
//     * returns true if permission has been granted
//     *
//     * @return boolean
//     */
//    private boolean hasLocationPermission() {
//        if ((ActivityCompat.checkSelfPermission(controller.getActivity(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(controller.getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED)) {
//            return false;
//        }
//        return true;
//    }
//
//    //CancleableSubject methods
//    @Override
//    public void onFail(String reason) {
//        //no critical failures handled here
//    }
//
//    @Override
//    public void notifyFatalError(String reason) {
//        log("Permission received fail notice");
//    }
//
//    private void log(String msg) {
//        Log.d("MapPerm", msg);
//    }
}