package com.evoke.cn.evokecompanion.GoogleMapFragment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.evoke.cn.evokecompanion.R;

import com.evoke.cn.evokecompanion.models.BluetoothConstants;
import com.evoke.cn.evokecompanion.services.BlueToothIntermediary;
import com.evoke.cn.evokecompanion.views.AddressResultView;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.services.android.location.LostLocationEngine;
import com.mapbox.services.android.navigation.v5.AlertLevelChangeListener;
import com.mapbox.services.android.navigation.v5.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.NavigationEventListener;
import com.mapbox.services.android.navigation.v5.ProgressChangeListener;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.ui.geocoder.GeocoderAutoCompleteView;
import com.mapbox.services.api.directions.v5.models.DirectionsResponse;
import com.mapbox.services.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.api.directions.v5.models.LegStep;
import com.mapbox.services.api.directions.v5.models.RouteLeg;
import com.mapbox.services.api.directions.v5.models.StepManeuver;
import com.mapbox.services.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.services.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.services.api.navigation.v5.RouteProgress;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.api.ServicesException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.services.android.Constants.*;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapController.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapController#newInstance} factory method to
 * create an instance of this fragment.
 * Think I need a new Design
 * https://www.mapbox.com/android-sdk/examples/support-fragment/
 * https://github.com/mapbox/mapbox-gl-native/issues/7749
 */
public class MapController extends Fragment implements OnMapReadyCallback, BluetoothConstants{

    private static final int PERMISSIONS_LOCATION = 0;
    private static LatLng START_LOC = new LatLng(39.9042, 116.4074);
    private MapboxMap map = null;
    private OnFragmentInteractionListener mListener;
    private DirectionsRoute chosenRoute;
    private FloatingActionButton myFloatingLocationButton;
    private MarkerViewOptions myLocationMarkerOptions;
    private Marker myLocationMarker;
    private LatLng myLastLocation = null;
    private AddressResultView myAddressView = null;

    private boolean isRouting = false;
    private MapboxNavigation navigationClient;
    private LocationEngine locationEngine;

    public MapController() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapController.
     */
    // TODO: Rename and change types and number of parameters
    public static MapController newInstance(String param1, String param2) {
        MapController fragment = new MapController();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IconFactory iconFactory = IconFactory.getInstance(getActivity());
        //TODO: make a my location marker
        Icon icon = iconFactory.fromResource(R.drawable.stupid);
        myLocationMarkerOptions = new MarkerViewOptions()
                .position(new LatLng())
                .icon(icon);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_controller, container, false);
        myAddressView = (AddressResultView) view.findViewById(R.id.search_res_direction_list);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Add the map api key set in main activity
        SupportMapFragment mapFragment;
        final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        MapboxMapOptions options = new MapboxMapOptions();
        options.styleUrl(Style.MAPBOX_STREETS);
        options.camera(new CameraPosition.Builder()
                .target(START_LOC)
                .zoom(9)
                .build());
        mapFragment = SupportMapFragment.newInstance(options);
        transaction.add(R.id.mapbox, mapFragment, "com.mapbox.map");
        transaction.commit();
        mapFragment.getMapAsync(this);
        //init direction view variable

        //Add MapBox autocomplete widget
        GeocoderAutoCompleteView autocomplete = (GeocoderAutoCompleteView) getActivity().findViewById(R.id.query);
        autocomplete.setAccessToken(getString(R.string.access_token));
        autocomplete.setType(GeocodingCriteria.TYPE_POI);
        autocomplete.setOnFeatureListener(new GeocoderAutoCompleteView.OnFeatureListener() {
            @Override
            public void onFeatureClick(CarmenFeature feature) {
                Position position = feature.asPosition();
                removeLocationListener();
                clearMarkers();
                addSearchMarkerOnClick(
                        addMarkerUpdateCenter(position.getLatitude(), position.getLongitude(), feature.getPlaceName()),
                        feature
                );
            }
        });

        //User Locations
//        locationEngine = LocationSource.getLocationEngine(getActivity());
//        locationEngine.activate();


        myFloatingLocationButton = (FloatingActionButton) getActivity().findViewById(R.id.location_toggle_fab);
        myFloatingLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    toggleGps(!map.isMyLocationEnabled());
//                    Position portMonmouth = Position.fromLngLat(-74.110688,40.441652);
//                    Position Gilboa = Position.fromLngLat(-74.285959,42.385872);
//                    getDirections(portMonmouth,Gilboa);
                }
            }
        });
    }
    private void addSearchMarkerOnClick(Marker resMarker, CarmenFeature feature){
        //TODO: load new view with
        // directions option
        // exit option
        // route details

        //Check we have the settings and data we need
        if(! map.isMyLocationEnabled() ){
            //TODO: Toast message location disabled then reset to location search or ask to turn on location
            directionsError(getString(R.string.direction_location_disabled));
            return;
        }
        if( myLastLocation == null ){
            directionsError(getString(R.string.direction_location_not_found));
            return;
        }
        //init UI
        initAddressResult(feature.getPlaceName());
        //get directions
        Position myPosition = Position.fromLngLat(myLastLocation.getLongitude(),myLastLocation.getLatitude());
        getDirections(myPosition, feature.asPosition());


    }
    private void initAddressResult(String placeName){
        showAddressResult();
        //set place name
        myAddressView.setHeader(placeName);
        //exit button
        myAddressView.getExitButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDirectionService();
            }
        });

    }
    private void showAddressResult(){
        myFloatingLocationButton.setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.mapbox).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.query).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.search_res_direction_list).setVisibility(View.VISIBLE);
    }
    private void hideAddressResult(){
        myFloatingLocationButton.setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.mapbox).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.query).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.search_res_direction_list).setVisibility(View.INVISIBLE);
    }
    private void stopDirectionService(){
        isRouting = false;
        hideAddressResult();
    }
    private void directionsError(String error){
        Toast.makeText(getActivity(),error, Toast.LENGTH_LONG).show();
        stopDirectionService();
    }
    private void clearMarkers(){
        map.clear();
        if(map.isMyLocationEnabled()) {
            addUserLocationMarker();
        }
    }
    private Marker addMarkerUpdateCenter(double lat, double lng, String placeName){
        updateMapCenter(lat,lng);
        return addMarkers(lat,lng,placeName);
    }
    private Marker addMarkers(double latitude, double longitude, String placeName){
       return map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(placeName));
    }
    private void updateMapCenter(double latitude, double longitude) {
        // Animate camera to geocoder result location
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(15)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
    }

    private void getDirections(Position origin, Position destination) {

        // Get route from API
        try {
            getRoute(origin, destination);
        } catch (ServicesException servicesException) {
            directionsError(getString(R.string.services_exception));
            servicesException.printStackTrace();
        }
    }

    private void getRoute(Position origin, Position destination) throws ServicesException {

        navigationClient = new MapboxNavigation(getContext(), getString(R.string.direction_no_route));
        locationEngine = LostLocationEngine.getLocationEngine(getContext());
        navigationClient.setLocationEngine(locationEngine);

        navigationClient.setOrigin(origin);
        navigationClient.setDestination(destination);

        navigationClient.getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null || response.body().getRoutes().size() < 1 ) {
                    directionsError(getString(R.string.direction_no_route));
                    return;
                }

                //Route options indexes
                int distanceIndex = -1;
                int timeIndex = -1;
                int ecoIndex =-1;

                //current best
                double bestDistance =-1;
                double bestTime =-1;
                double bestEco = -1; //short distance long time.. small is good

                for(int chosenIndex = 0; chosenIndex < response.body().getRoutes().size(); chosenIndex++){
                    DirectionsRoute currRoute = response.body().getRoutes().get(chosenIndex);

                    double distance = currRoute.getDistance();
                    double time = currRoute.getDuration();
                    double eco = distance/time;
                    if(eco < bestEco || bestEco < 0){
                        bestEco = eco;
                        ecoIndex = chosenIndex;
                    }
                    if(distance < bestDistance || bestDistance < 0){
                        bestDistance = distance;
                        distanceIndex = chosenIndex;
                    }
                    if(time < bestTime || bestTime < 0){
                        bestTime = time;
                        timeIndex = chosenIndex;
                    }
                }
                //TODO: populate elem with route results, pass found routes to the onclick method
                //TODO: add startNavigation on click with the index chosen and distance/ duration summaries
                //consider choosing route as starting navigation

            }

            private void log(String message){
                Log.d("MAPCON",message);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                //TODO: FAILED TO FIND ROUTE
                Log.e("mapbox", "Error: " + throwable.getMessage());
                directionsError(throwable.getMessage());
            }
        });
    }
    private void startNavigation(DirectionsRoute currentRoute){
        chosenRoute = currentRoute;
        showNavigation(chosenRoute);
        addNavigationLocationListener();
        //tell bluetooth to buckle up
    }
    private void endNavigation(){
        chosenRoute=null;
        removeNavigationListener();
        hideAddressResult();
    }

    private void showNavigation(DirectionsRoute currentRoute){
        myAddressView.setRouteInfo(Double.toString(currentRoute.getDuration()),Double.toString(currentRoute.getDistance()));
        for ( int i =0; i < currentRoute.getLegs().size(); i++) {
            //TODO ADD MAP Stuff Draw the route on the map
            RouteLeg currentLeg = currentRoute.getLegs().get(0);
            for(int j = 0; j < currentLeg.getSteps().size(); j++){
                LegStep currentStep = currentLeg.getSteps().get(j);
                StepManeuver maneuver = currentStep.getManeuver();
                String summary = maneuver.getModifier()+" "+getString(R.string.direction_grammer)+" "+ currentStep.getName();
                myAddressView.addLeg(summary,Double.toString( currentStep.getDistance()),parseManeuver(maneuver.getModifier()),j );
            }
        }
        addNavigationLocationListener();
    }

    private void toggleGps(boolean enableGps) {
        if (enableGps) {
            // Check if user has granted location permission
            if (ActivityCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
            } else {
                enableLocation(true);
            }
        } else {
            enableLocation(false);
        }
    }

    // https://www.mapbox.com/android-sdk/examples/user-location/
    private void enableLocation(boolean enabled) {
        if (enabled) {
            Location lastLocation = map.getMyLocation();
            addUserLocationMarker();
            if (lastLocation != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), 16));
            }
            //TODO: set up map location listener and updates
            addStaticLocationListener();
            myFloatingLocationButton.setImageResource(R.drawable.ic_location_disabled_24dp);
        } else {
            //reset map location state
            myLocationMarker = null;
            removeLocationListener();
            myFloatingLocationButton.setImageResource(R.drawable.ic_my_location_24dp);
        }
        // Enable or disable the location layer on the map
        map.setMyLocationEnabled(enabled);

    }
    private void animateMarker(Marker thisMarker, LatLng newPos){
        ValueAnimator markerAnimator = ObjectAnimator.ofObject(thisMarker, "position",
                new LatLngEvaluator(), thisMarker.getPosition(), newPos);
        markerAnimator.setDuration(1000);
        markerAnimator.start();
    }

    private void addUserLocationMarker(){
        myLocationMarker = map.addMarker(myLocationMarkerOptions);
    }

    private void addStaticLocationListener(){
        map.setOnMyLocationChangeListener(new MapboxMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(@Nullable Location location) {
                if(location != null) {
                    LatLng newPos= new LatLng(location);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos, 16));
                    //Animate position marker to move to the new position
                    animateMarker(myLocationMarker,newPos);
                    myLastLocation = newPos;
                    removeLocationListener();
                }
            }
        });
    }
    private void addNavigationLocationListener(){
//        final MapController  self = this;
        navigationClient.setNavigationEventListener(new NavigationEventListener() {
            @Override
            public void onRunning(boolean running) {
                //on start on stop
                if(!running){
                    endNavigation();
                }
            }
        });

        navigationClient.setAlertLevelChangeListener(new AlertLevelChangeListener() {
            @Override
            public void onAlertLevelChange(int alertLevel, RouteProgress routeProgress) {
                switch (alertLevel) {
                    case HIGH_ALERT_LEVEL:
                        break;
                    case MEDIUM_ALERT_LEVEL:
                        break;
                    case LOW_ALERT_LEVEL:
                        break;
                    case ARRIVE_ALERT_LEVEL:
                        break;
                    case NONE_ALERT_LEVEL:
                        break;
                    case DEPART_ALERT_LEVEL:
                        break;
                }
            }
        });

        navigationClient.setProgressChangeListener(new ProgressChangeListener() {
            @Override
            public void onProgressChange(Location location, RouteProgress routeProgress) {
                if(routeProgress == null){
                    return;
                }
                if( routeProgress.getFractionTraveledOnRoute() == 1.0){
                    //if route finished but still receiving updates
                    endNavigation();
                }
                //update ui
                updateNavigationUI(routeProgress.getStepIndex());
                //update bike
                LegStep currStep = routeProgress.getCurrentStep();
                double distanceToEnd = routeProgress.getDistanceRemainingOnStep();
                String summary = currStep.getManeuver().getModifier()+" "+getString(R.string.direction_grammer)+" "+ currStep.getName();
                shareDirections(parseManeuver(currStep.getManeuver().getModifier()),summary,Double.toString(distanceToEnd));
            }
        });
    }

    private void updateNavigationUI(int currentStepIndex) {
        if(myAddressView.getFirstShowingIndex() !=currentStepIndex) {
            myAddressView.removeLeg(currentStepIndex);
        }
    }

    private void removeNavigationListener(){
        navigationClient.setProgressChangeListener(null);
        navigationClient.setAlertLevelChangeListener(null);
        navigationClient.setNavigationEventListener(null);
    }

    private void removeLocationListener(){
        map.setOnMyLocationChangeListener(null);
    }
    /**
    * Prepares a message containing all direction info for bluetooth
    */
    private void shareDirections(int parsedManeuver, String summary, String distance){
        String message = Integer.toString(parsedManeuver) + ITEM_SEPERATOR
                        +summary+ITEM_SEPERATOR+distance;
        sendDirection(message,GIVE_CONTENT[ALL_INDEX]);
    }
    /**
    * Prepares a message containing only the distance info for bluetooth
    */
    private void shareDirections(String distance){
        sendDirection(distance, GIVE_CONTENT[DISTANCE_INDEX]);
    }
    /**
    * Sends a message to the bluetooth system
    * Should not be directly accesses use shareDirections(x) instead
    */
    private void sendDirection(String message, String typeCode){
        //true=giving and not expecting a response
        BlueToothIntermediary.getInstance().addRequest(message,typeCode,true);
    }
    private int parseManeuver(String maneuver){
        //TODO: get maneuver value
        maneuver = maneuver.toLowerCase();
        switch(maneuver){
            case "uturn":
                return MAN_UTURN;
            case "sharp right":
                return MAN_RIGHT;
            case "right":
                return MAN_RIGHT;
            case "slight right":
                return MAN_RIGHT;
            case "straight":
                return MAN_STRAIGHT;
            case "slight left":
                return MAN_LEFT;
            case "left":
                return MAN_LEFT;
            case "sharp left":
                return MAN_LEFT;
            default:
                return MAN_TERMINATE;
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation(true);
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private static class LatLngEvaluator implements TypeEvaluator<LatLng> {
        // Method is used to interpolate the marker animation.

        private LatLng latLng = new LatLng();

        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude()
                    + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude()
                    + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    }

}
