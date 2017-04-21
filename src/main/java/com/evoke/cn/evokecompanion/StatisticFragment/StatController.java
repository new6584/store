package com.evoke.cn.evokecompanion.StatisticFragment;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.evoke.cn.evokecompanion.R;
import com.evoke.cn.evokecompanion.models.BluetoothConstants;
import com.evoke.cn.evokecompanion.models.BluetoothResponse;
import com.evoke.cn.evokecompanion.services.BlueToothIntermediary;
import com.evoke.cn.evokecompanion.views.SixDigitOdometer;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatController} interface
 * to handle interaction events.
 * Use the {@link StatController#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatController extends Fragment implements Observer, BluetoothConstants{

    private static final String BATTERY_FILE_PREFIX = "batt_charge_";

    private static final String CHARGE_UNITS ="kwh";
    private static final String DISTANCE_UNITS = "km";

    private static final int baseColor = Color.parseColor("#563717");
    private static final int baseFillColor = Color.parseColor("#b3563717");
    private static final int CHARGE_FILL_COLOR = baseFillColor;
    private static final int CHARGE_COLOR = baseColor;
    private static final int DISTANCE_FILL_COLOR = Color.parseColor("#52473d");
    private static final int DISTANCE_COLOR = Color.parseColor("#b352473d");
    private static final int LINE_THICKNESS = 5;
    private static final int MOTO_TEMP_FILL_COLOR = baseFillColor;
    private static final int MOTO_TEMP_COLOR = baseColor;
    //Margins order: top, left, bottom, right
    private static final int[] MARGINS = new int[] {0, 0 , 0,0};
    private static final int MARGIN_COLOR = Color.argb(0x00, 0xff, 0x00, 0x00);
    private static final boolean SHOWING_CHART_VALUES = true;
    private static final boolean SHOW_LEGEND = false ;
    private static final boolean SHOW_AXIS_LINES = false;

    private GraphicalView distanceChargeGraph = null;
    private XYMultipleSeriesRenderer distanceChargeRenderer = null;
    private XYMultipleSeriesDataset chargeDistance_dataset = null;
    private static final int chargeDataIndex =0;
    private static final int distanceDataIndex =1;

    private GraphicalView motorTempGraph = null;
    private XYMultipleSeriesRenderer motorTempRenderer = null;
    private XYMultipleSeriesDataset motorTemp_dataset = null;
    private static final int motorDataIndex = 0;

    private SixDigitOdometer chargeOdometer = null;
    private SixDigitOdometer distanceOdometer = null;

    private final int NOT_AN_INT_ERROR_CODE = -55;

    private double lastOdometerReading = 0;
    private int currentChargeState = 10; // 0-10 ~ 0 being empty 10 full

   // private OnFragmentInteractionListener mListener;

    public StatController() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static StatController newInstance() {
        StatController fragment = new StatController();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_stat_controller, container, false);
        startUp();
        initUserInterface(myView);
        return myView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addTestData();
    }

    private void initUserInterface(View myView){
        distanceChargeGraph = createChargeDistanceGraph();
        motorTempGraph = createMotorTempGraph();

        ((ViewGroup) myView.findViewById(R.id.charge_distance)).addView(distanceChargeGraph,0);
        ((ViewGroup) myView.findViewById(R.id.motor_temp)).addView(motorTempGraph,0);

        chargeOdometer = (SixDigitOdometer) myView.findViewById((R.id.charge_odometer));
        chargeOdometer.setUnits(false,false);//kwh
        distanceOdometer = (SixDigitOdometer) myView.findViewById((R.id.distance_odometer));
        distanceOdometer.setUnits(false,true);//km
    }

    private GraphicalView createChargeDistanceGraph(){
        //Organize Data into series
        chargeDistance_dataset = new XYMultipleSeriesDataset();

        XYSeries chargeSeries= new XYSeries("Charge");
        XYSeries distanceSeries = new XYSeries("Distance");

        chargeDistance_dataset.addSeries(chargeDataIndex, chargeSeries);
        chargeDistance_dataset.addSeries(distanceDataIndex, distanceSeries);

        XYSeriesRenderer.FillOutsideLine fill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
        //Create Renderers -- rules for axis
        XYSeriesRenderer chargeRenderer = new XYSeriesRenderer();
        chargeRenderer.setColor(CHARGE_COLOR);
        fill.setColor(CHARGE_FILL_COLOR);
        chargeRenderer.addFillOutsideLine(fill);
        chargeRenderer.setPointStyle(PointStyle.CIRCLE);
        chargeRenderer.setFillPoints(true);
        chargeRenderer.setLineWidth(LINE_THICKNESS);
        chargeRenderer.setDisplayChartValues(true);
        chargeRenderer.setDisplayBoundingPoints(true);

        XYSeriesRenderer.FillOutsideLine fill2 = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
        XYSeriesRenderer distanceRenderer = new XYSeriesRenderer();
        distanceRenderer.setColor(DISTANCE_COLOR);
        fill2.setColor(DISTANCE_FILL_COLOR);
        distanceRenderer.addFillOutsideLine(fill2);
        distanceRenderer.setPointStyle(PointStyle.CIRCLE);
        distanceRenderer.setFillPoints(true);
        distanceRenderer.setLineWidth(LINE_THICKNESS);
        distanceRenderer.setDisplayChartValues(SHOWING_CHART_VALUES);

        //Combine Renderers -- rules for whole graph
        distanceChargeRenderer = new XYMultipleSeriesRenderer(2);
        distanceChargeRenderer.setXLabels(0);
        distanceChargeRenderer.setZoomButtonsVisible(false);
        distanceChargeRenderer.setMargins(MARGINS);
        distanceChargeRenderer.setMarginsColor(MARGIN_COLOR);
        distanceChargeRenderer.setShowLegend(SHOW_LEGEND);
        distanceChargeRenderer.setShowAxes(SHOW_AXIS_LINES);
        distanceChargeRenderer.setBarSpacing(2);
        distanceChargeRenderer.setShowGridX(true);
        //distanceChargeRenderer.setShowGridY(true);

        distanceChargeRenderer.addSeriesRenderer(chargeRenderer);
        distanceChargeRenderer.addSeriesRenderer(distanceRenderer);
        distanceChargeRenderer.setBarSpacing(4);
        distanceChargeRenderer.setYLabelsColor(0, DefaultRenderer.NO_COLOR);
        distanceChargeRenderer.setXLabelsColor(DefaultRenderer.NO_COLOR);
        distanceChargeRenderer.setLabelsTextSize(15);
        distanceChargeRenderer.setYAxisMin(0);
        distanceChargeRenderer.setYAxisAlign(Paint.Align.RIGHT, 0);
        distanceChargeRenderer.setPanEnabled(false,false);
        distanceChargeRenderer.setZoomEnabled(false,false);
        return ChartFactory.getLineChartView(getActivity(),chargeDistance_dataset, distanceChargeRenderer);
    }

    private GraphicalView createMotorTempGraph(){
        motorTemp_dataset = new XYMultipleSeriesDataset();

        XYSeries motorTempSeries= new XYSeries("Motor Temperature");

        motorTemp_dataset.addSeries(motorDataIndex, motorTempSeries);

        XYSeriesRenderer.FillOutsideLine fill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
        //Create Renderers -- rules for axis
        XYSeriesRenderer motorRenderer = new XYSeriesRenderer();
        motorRenderer.setColor(MOTO_TEMP_COLOR);
        fill.setColor(MOTO_TEMP_FILL_COLOR);
        motorRenderer.addFillOutsideLine(fill);
        motorRenderer.setPointStyle(PointStyle.CIRCLE);
        motorRenderer.setFillPoints(true);
        motorRenderer.setLineWidth(LINE_THICKNESS);
        motorRenderer.setDisplayChartValues(SHOWING_CHART_VALUES);
        motorRenderer.setDisplayBoundingPoints(true);

        //Combine Renderers -- rules for whole graph
        motorTempRenderer = new XYMultipleSeriesRenderer(1);
        motorTempRenderer.setXLabels(0);
        motorTempRenderer.setZoomButtonsVisible(false);
        motorTempRenderer.setMargins(MARGINS);
        motorTempRenderer.setMarginsColor(MARGIN_COLOR);
        motorTempRenderer.setShowLegend(SHOW_LEGEND);
        motorTempRenderer.setShowAxes(SHOW_AXIS_LINES);
        motorTempRenderer.setShowGridX(true);
        //motorTempRenderer.setShowGridY(true);

        motorTempRenderer.addSeriesRenderer(motorRenderer);
        motorTempRenderer.setBarSpacing(4);
        motorTempRenderer.setYLabelsColor(0, DefaultRenderer.NO_COLOR);
        motorTempRenderer.setXLabelsColor(DefaultRenderer.NO_COLOR);
        motorTempRenderer.setYAxisAlign(Paint.Align.RIGHT, 0);
        motorTempRenderer.setPanEnabled(false,false);

        return ChartFactory.getLineChartView(getActivity(),motorTemp_dataset, motorTempRenderer);
    }

    private void startUp(){
        BlueToothIntermediary.getInstance().subscribe(this);
        //startDataUpdates();
        //TODO: add database for persistent data?
    }

    private void redrawMotorTempGraph(double newestValue){
        if( newestValue > motorTempRenderer.getYAxisMax() ){
            motorTempRenderer.setYAxisMax(newestValue);
        }
        motorTempGraph.repaint();
    }
    private void redrawDistanceChargeGraph(double newestValue){
        if( newestValue > distanceChargeRenderer.getYAxisMax() ){
            distanceChargeRenderer.setYAxisMax(newestValue);
        }
        distanceChargeGraph.repaint();
    }
    private void updateOdometerDisplay(String strReading){
        int newValue = convertToInt(strReading);
        log("New Odometer Update: "+ newValue);
        if(validateConversion(newValue)){

            final int forThread = newValue;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    distanceOdometer.setValue(forThread);
                }
            });

            XYSeries distanceSeries = chargeDistance_dataset.getSeriesAt(distanceDataIndex);
            double distanceBetweenTimeIntervals = newValue - lastOdometerReading;
            if( !(distanceBetweenTimeIntervals < 0) ) {
                distanceSeries.add(distanceSeries.getItemCount(),distanceBetweenTimeIntervals);
                if(distanceSeries.getItemCount() > 1) {
                    redrawDistanceChargeGraph(distanceBetweenTimeIntervals);
                }
            }
        }
    }
    private void updateChargeDisplay(String newCharge){
        int newValue = convertToInt(newCharge);
        if(validateConversion(newValue)) {
            final int forThread = newValue;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chargeOdometer.setValue(forThread);
                }
            });

            XYSeries chargeSeries = chargeDistance_dataset.getSeriesAt(chargeDataIndex);
            chargeSeries.add(chargeSeries.getItemCount(), convertKWHtoPercent(newValue));
            log("NEW CHARGE: "+ chargeSeries.getItemCount() +"\n"+newValue);
            if(chargeSeries.getItemCount() > 1) {
                redrawDistanceChargeGraph(newValue);
            }
            int firstDigit = -1;
            if (newValue > 99) { //special case for more then 2 digits
                firstDigit = 10;
            } else if ( newValue < 10){ //special case for lass than 2 digits or negatives
                firstDigit = 0;
            }else{//Use the String Value after validating it is a proper number
                firstDigit = Integer.parseInt(newCharge.substring(0, 1));
            }
            //if state value needs to change
            if(firstDigit < currentChargeState) {
                updateChargeGraphic(firstDigit);
            }
        }
    }
    //TODO: Convert to Percent if receiving KWH
    private double convertKWHtoPercent(int newValue) {
        return newValue;
    }

    private void updateChargeGraphic(int newState){
        currentChargeState = newState;
        final String file = BATTERY_FILE_PREFIX + newState;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log("New Battery Image: "+file);
                ImageView chargeGraphic  = (ImageView) getView().findViewById(R.id.charge_graphic);
                chargeGraphic.setImageResource(getResources().getIdentifier(file, "drawable", getActivity().getPackageName()));
            }
        });
    }
    private void setChargeHistory(String data){

    }
    private void updateTemperatureDisplay(String newTemp){
        int newValue = convertToInt(newTemp);
        log("Updating temp: "+newValue);
        if(validateConversion(newValue)) {
            XYSeries tempSeries = motorTemp_dataset.getSeriesAt(motorDataIndex);
            tempSeries.add(tempSeries.getItemCount(),newValue);
            if(tempSeries.getItemCount() > 1) {
                redrawMotorTempGraph(newValue);
            }
        }
    }

    private int convertToInt(String theNumber){
        try{
            return Integer.parseInt( theNumber );
        } catch (Exception notAnInt){
            log("Tried to convert a non number to an int");
            return NOT_AN_INT_ERROR_CODE;
        }
    }
    private boolean validateConversion(int result){
        return ( result != NOT_AN_INT_ERROR_CODE );
    }
    /**
     * Update for Bluetooth Observable
     */
    @Override
    public void update(Observable o, Object btResponse) {
        log("Receiving Observable update");
        if(motorTemp_dataset      == null || chargeDistance_dataset == null ||
           distanceChargeGraph    == null || motorTempGraph         == null ||
           distanceChargeRenderer == null || motorTempRenderer      == null ||
           chargeOdometer         == null || distanceOdometer       == null){
            log("Graphs not init");
            return;
        }
        if (((BluetoothResponse) btResponse).contentTypeCode .equals(GET_CONTENT[CHARGE_INDEX]) ){
            updateChargeDisplay(((BluetoothResponse) btResponse).message);
        }else if(((BluetoothResponse) btResponse).contentTypeCode.equals(GET_CONTENT[C_HISTORY_INDEX])){
            setChargeHistory(((BluetoothResponse) btResponse).message);
        }else if(((BluetoothResponse) btResponse).contentTypeCode.equals(GET_CONTENT[ODOMETER_INDEX])){
            updateOdometerDisplay(((BluetoothResponse) btResponse).message);
        }else if(((BluetoothResponse) btResponse).contentTypeCode.equals(GET_CONTENT[DISTANCE_SINCE_INDEX])){
            //TODO: are we actually having the bike track this?
        }else if(((BluetoothResponse) btResponse).contentTypeCode.equals(GET_CONTENT[TEMPERATURE_INDEX])){
            updateTemperatureDisplay(((BluetoothResponse) btResponse).message);
        }
    }

    private void log(String message){
        Log.d("Stat Controller",message);
    }
    private void addTestData(){
        updateChargeDisplay("100");
        updateChargeDisplay("94");
        updateChargeDisplay("87");
        updateChargeDisplay("88");
        updateChargeDisplay("25");

        updateOdometerDisplay("5");
        updateOdometerDisplay("25");
        updateOdometerDisplay("2");
        updateOdometerDisplay("0");
        updateOdometerDisplay("0");
        updateOdometerDisplay("15");

        updateTemperatureDisplay("5");
        updateTemperatureDisplay("15");
        updateTemperatureDisplay("50");
        updateTemperatureDisplay("50");
        updateTemperatureDisplay("50");
        updateTemperatureDisplay("50");
        updateTemperatureDisplay("50");
    }
}
