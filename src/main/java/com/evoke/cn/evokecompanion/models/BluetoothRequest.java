package com.evoke.cn.evokecompanion.models;

/**
 * Created by Womble on 3/9/2017.
 */

public class BluetoothRequest implements BluetoothConstants {

    public String message;
    public String appRequestId;
    public boolean sent = false;
    public boolean responded = false;
    public int failureCount = 0 ;

    public BluetoothRequest(String input,String typeCode,String requestId, boolean isGiving){
        appRequestId= requestId;
        if(isGiving){
            prepareAsGive(input,typeCode);
        }else{
            prepareAsGet(typeCode);
        }
    }

    private void prepareAsGive(String input, String contentTypeCode){
        String fullMessage = GIVE_HEADER +
                HEADER + input.length() + LENGTH_TERMINATOR+
                contentTypeCode + input + CONTENT_TERMINATOR+
                appRequestId+ REQUEST_ID_TERMINATOR;
        fullMessage = fullMessage + fullMessage.length() + TERMINATOR;

        message = fullMessage;
    }

    private void prepareAsGet(String contentTypeCode){
        String fullMessage = GET_HEADER +
                HEADER + "0"+ LENGTH_TERMINATOR+
                contentTypeCode + CONTENT_TERMINATOR+
                appRequestId+ REQUEST_ID_TERMINATOR;
        fullMessage = fullMessage + fullMessage.length() + TERMINATOR;

        message = fullMessage;
    }
}
