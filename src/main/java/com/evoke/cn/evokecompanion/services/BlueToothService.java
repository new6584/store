package com.evoke.cn.evokecompanion.services;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.util.Log;

import com.evoke.cn.evokecompanion.models.BluetoothConstants;
import com.evoke.cn.evokecompanion.models.BluetoothResponse;

import java.util.Observable;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


/**
 * @author Womble  2/5/2017
 *
 */
abstract public class BlueToothService extends Observable implements BluetoothConstants{

    private BluetoothSPP bt =null;

    public boolean init(Context appCon){
        bt = new BluetoothSPP(appCon);
        if(! bt.isBluetoothEnabled() ){
            return false;
        }
        //Startup
        bLog("Bluetooth Enabled lessgo");
        startBtServices();
        return true;
    }
    final public void setListener(BluetoothSPP.OnDataReceivedListener myListener ){
        bt.setOnDataReceivedListener( myListener );
    }

    private void startBtServices(){
        bt.setupService();
        bt.startService(BluetoothState.DEVICE_OTHER);
        addListeners();
    }

    final public boolean send(byte[] message){
        bLog("Sending as bytes: " + message);
        if( bt !=null && bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
            bt.send(message, false);
            return true;
        }else{
            bLog("Message not sent");
            return false;
        }
    }

    final public void stopBluetoothServices(){
        if(bt !=null){
            bt.stopService();
        }
    }

    final public Intent makeDeviceIntent(Context _appCon, Activity appActivity){
        bLog("Sending Device List Intent");
        return new Intent(_appCon, DeviceList.class);
        //startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }

    final public int getDeviceIntentResponseCode(){
        return BluetoothState.REQUEST_CONNECT_DEVICE;
    }

    final public void onActivityResults(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bLog("connecting to: " + data.getDataString());
            bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                startBtServices();

            } else {
                //TODO: Do something if user doesn't choose any device (Pressed back)
            }
        }
    }

    private void addListeners(){
        /*INCOMING DATA*/
        final BlueToothService self = this;
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
            }
        });
        /*CONNECTION STATUS*/
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                // Do something when successfully connected
            }
            public void onDeviceDisconnected() {
                // Do something when connection was disconnected
                bLog("Disconnecting from device");
            }

            public void onDeviceConnectionFailed() {
                // Do something when connection failed
                bLog("connection failed");
            }
        });

        /*STATE*/
        bt.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            public void onServiceStateChanged(int state) {
                if(state == BluetoothState.STATE_CONNECTED){
                    bLog("bluetooth state Connected");
                }
                // Do something when successfully connected
                else if(state == BluetoothState.STATE_CONNECTING){
                    bLog("BT state Connecting");
                }
                // Do something while connecting
                else if(state == BluetoothState.STATE_LISTEN){
                    bLog("BT state Listening");
                }
                // Do something when device is waiting for connection
                else if(state == BluetoothState.STATE_NONE){
                    bLog("BT state no state?");
                }
                // Do something when device don't have any connection
            }
        });
    }
    public int getState(){
        return bt.getServiceState();
    }
    final public boolean isConnected(){
        if( getState() == BluetoothState.STATE_CONNECTED){
            return true;
        }
        return false;
    }
    public void bLog(String message) {
        Log.d("Btooth", message);
    }
}

/* IF WE WANT AUTO CONNECTION
        bt.setAutoConnectionListener(new AutoConnectionListener() {
        public void onNewConnection(String name, String address) {
            // Do something when earching for new connection device
        }

        public void onAutoConnectionStarted() {
            // Do something when auto connection has started
        }
        });
        bt.autoConnect("Keyword for filter paired device");
 */
