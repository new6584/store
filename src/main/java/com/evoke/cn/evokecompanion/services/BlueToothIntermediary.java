package com.evoke.cn.evokecompanion.services;

import android.content.Context;
import com.evoke.cn.evokecompanion.models.BluetoothRequest;
import com.evoke.cn.evokecompanion.models.BluetoothResponse;

import java.util.ArrayList;
import java.util.Observer;


import app.akexorcist.bluetotohspp.library.BluetoothSPP;

/**
 * Created by Womble on 3/9/2017.
 * Controls interactions with the bluetooth service
 * Implementation instructions
 *  -When app starts / context won't change
 *   BlueToothIntermediary.getInstance().startBluetoothServies(getContext());
 *  -Connect to a Device using pre built intent
 *      Intent mine = BlueToothIntermediary.getInstance().makeDeviceIntent(getContext(),getActivity());
 *      startActivityForResult(mine, BlueToothIntermediary.getInstance().getDeviceIntentResponseCode());
 *        public void onActivityResult(int requestCode, int resultCode, Intent data) { BlueToothIntermediary.getInstance().onActivityResults(requestCode,resultCode,data); }
 *  -Once Connected add requests
 *      BlueToothIntermediary.getInstance().addRequest(
 */
public class BlueToothIntermediary extends BlueToothService{

    private static BlueToothIntermediary ourInstance = new BlueToothIntermediary();
    public static BlueToothIntermediary getInstance() {
        return ourInstance;
    }

    // requests that have not yet received an answer
    private ArrayList<BluetoothRequest> unansweredRequests;

    // requests in que to be sent
    private ArrayList<BluetoothRequest> requestStack;

    // requests that have been validated as answered
    private ArrayList<BluetoothResponse> validResponses;

    final private Object requestStackLock = new Object();
    final private Object unansweredLock = new Object();
    final private Object validResponseLock = new Object();

    private int failureCount;
    private static final int MAX_SINGLE_OBJ_FAILURES = 5;
    private static final int MAX_WAIT_TIME = 5 * 1000; //5 seconds

    private int nextRequestID;
    private RequestSender requestSender;

    private BlueToothIntermediary() {
        failureCount =0;
        nextRequestID = 0;
        requestSender = null;
        requestStack = new ArrayList<BluetoothRequest>();
        validResponses = new ArrayList<BluetoothResponse>();
        unansweredRequests = new ArrayList<BluetoothRequest>();
    }

    public boolean startBluetoothServies(Context appCon){
        if( init(appCon) ){
            /**
             * When is data is received from the bluetooth object
             */
            final BlueToothIntermediary  me = this;
            setListener( new BluetoothSPP.OnDataReceivedListener(){
                @Override
                public void onDataReceived(byte[] data, String message) {
                    BluetoothResponse thisResponse = new BluetoothResponse();
                    thisResponse.decodeMessage(message,data);
                    if(thisResponse.decoded){
                        markResponded(thisResponse);
                        me.notifyObservers( thisResponse);
                    }
                    else{
                        markFailure(thisResponse);
                    }
                    bLog("Notifying Sender");
                    getInstance().notify();
                }
            });
            return true;
        }
        //TODO: ENABLE BLUETOOTH

        return false;
    }

    private void markResponded(BluetoothResponse aResponse){
        int unansweredIndex = -1;
        synchronized (unansweredLock){
            unansweredIndex = getUnansweredIndex(aResponse.appRequestId);
            unansweredRequests.get(unansweredIndex).responded = true;
        }
        synchronized (validResponseLock){
            validResponses.add(aResponse);
        }
    }
    private int getUnansweredIndex(String id){
        for(int i = 0; i < unansweredRequests.size(); i++){
            if( unansweredRequests.get(i).appRequestId.equals(id) ){
                return i;
            }
        }
        return -1;
    }
    //failure getting a proper response
    private void markFailure(BluetoothResponse aResponse){
        failureCount ++;
        int index= getUnansweredIndex(aResponse.appRequestId);
        BluetoothRequest temp = null;
        synchronized (unansweredLock){
            unansweredRequests.get(index).failureCount++;
            temp = unansweredRequests.get(index);
        }
        if (temp != null && temp.failureCount < MAX_SINGLE_OBJ_FAILURES) {
            addRequest(temp);
        }
    }

    /**
     *
     * @param message data for the bike
     * @param type type of data
     * @param giving true if give request false if get request
     * @return String representing the requestID
     */
    public String addRequest(String message,String type, boolean giving){
        bLog("Creating request: "+ nextRequestID);
        String strRequest = Integer.toString(nextRequestID);
        BluetoothRequest newItem = new BluetoothRequest(message,type, strRequest, giving);
        nextRequestID++;
        addRequest(newItem);
        return strRequest;
    }
    private void addRequest(BluetoothRequest resend){
        addToStack(resend);
        notifyRequestSender();
    }
    private void notifyRequestSender(){
        if( requestSender == null || !requestSender.isAlive()){
            bLog("Activating Request Sender");
            requestSender = new RequestSender();
            requestSender.start();
        }
    }

    private void addToStack(BluetoothRequest me){
        synchronized (requestStackLock){
            requestStack.add(me);
        }
    }

    public ArrayList<BluetoothResponse> getResponses(){
        synchronized (validResponseLock){
            return validResponses;
        }
    }

    public BluetoothResponse getResponse(int index){
        synchronized (validResponseLock){
            return validResponses.get(index);
        }
    }

    public ArrayList<BluetoothResponse> getResponses(int start, int end){
        ArrayList<BluetoothResponse> temp = new ArrayList<BluetoothResponse>();
        if( start < 0)
            return null;

        synchronized (validResponseLock){
            if( end > validResponses.size())
                end = validResponses.size();
            //Can't separate in case response is removed?
            for(int i = start; i < end; i++ ){
                temp.add( validResponses.get(i));
            }
        }
        return temp;
    }

    public void subscribe(Observer me){
        bLog("Adding new observer");
        this.addObserver(me);
    }

    public void unsubscribe(Observer me){
        this.deleteObserver(me);
    }


/*---START REQUEST SENDER CLASS -> THREAD WHICH SENDS REQUESTS THROUGH BLUETOOTH FIFO---*/
    private class RequestSender extends Thread{
        private static final int FIRST_INDEX = 0;
        private int failureCount=0; //in sending and receiving
        private int successCount=0; //in sending

        @Override
        public void run(){
            bLog("Thread Starting");
            while(getCount() > 0) {
                bLog("Sending Next Request");
                sendNextRequest();
                try {
                    bLog("waiting for response");
                    synchronized (getInstance()) {
                        getInstance().wait(MAX_WAIT_TIME);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //failure sending
        private void markFailure(BluetoothRequest aRequest){
            failureCount++;
            if(aRequest.failureCount < MAX_SINGLE_OBJ_FAILURES){
                addRequest(aRequest);
            }
        }

        //request que
        private void sendNextRequest(){
            if(getCount() < 1){
                return;
            }
            String currentRequest = getFirstRequestMessage();
            if(! send(currentRequest.getBytes()) ){
                markFailure(getFirstRequest());
            }else{
                markSuccessOnFirst();
            }
            removeRequest();
        }

        private void markSuccessOnFirst(){
            successCount++;
            synchronized (requestStackLock){
                requestStack.get(FIRST_INDEX).sent = true;
            }
        }

        private String getFirstRequestMessage(){
            synchronized (requestStackLock){
                return requestStack.get(FIRST_INDEX).message;
            }
        }
        private BluetoothRequest getFirstRequest(){
            synchronized (requestStackLock){
                return requestStack.get(FIRST_INDEX);
            }
        }

        private int getCount(){
            synchronized (requestStackLock){
                return requestStack.size();
            }
        }

        private void removeRequest(){
            synchronized (requestStackLock){
                requestStack.remove(FIRST_INDEX);
            }
        }
    }
}
