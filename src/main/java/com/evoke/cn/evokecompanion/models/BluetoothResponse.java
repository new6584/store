package com.evoke.cn.evokecompanion.models;

import android.preference.PreferenceActivity;

/**
 * Created by Womble on 3/8/2017.
 */

public class BluetoothResponse implements BluetoothConstants{

    public byte[] raw;
    public String btConverted;

    public String message = null;
    public String communicationTypeCode = null;
    public String contentTypeCode = null;
    public String appRequestId = null;


    public String errorMessage = null;
    public boolean decoded = false;

    public BluetoothResponse(){
    }

    public boolean decodeMessage(String _message, byte[] _bMessage){
        btConverted = _message;
        raw = _bMessage;
        return decodeMessage(btConverted);
    }

    /**
     * Takes a string and parses out the elements of the message as defined by our API
     * @param myMessage api string
     * @return boolean, true if message was completely decoded
     */
    private boolean decodeMessage(String myMessage){
        if( decoded && validateDecode()){
            return true;
        }
        //Check Header Statement
        if(! myMessage.substring(0,HEADER.length()).equals(HEADER)){
            errorMessage = "Header did not match expected value.";
            return false;
        }

        communicationTypeCode = myMessage.substring(HEADER.length(), HEADER.length()+1 );

        String myTerminator = myMessage.substring(HEADER.length()+1, HEADER.length()+1+TERMINATOR.length());
        if(! myTerminator.equals(TERMINATOR)){
            errorMessage = "Terminator not found in expected location or did not match expected value.";
            return false;
        }
        //Start Content Decode
        String myContent = myMessage.substring(HEADER.length()+1+TERMINATOR.length());

        if(! myContent.substring(0,HEADER.length()).equals(HEADER)){
            errorMessage ="Content header did not match expected value.";
            return false;
        }
        int lindex = myContent.indexOf(LENGTH_TERMINATOR);
        if (lindex < 1){
            errorMessage = "Malformed message, no length terminator";
            return false;
        }
        String strLength = myContent.substring(HEADER.length(), lindex);
        int intLength = -1;
        try{
            intLength = Integer.parseInt(strLength);
        } catch (Exception notInt){
            errorMessage = "Content Length was not an Integer. Error: "+ strLength ;
            return false;
        }
        if( intLength == -1 ){
            errorMessage = "Length error";
            return false;
        }
        contentTypeCode = myContent.substring(lindex+1,lindex+2);

        int cindex = myContent.indexOf(CONTENT_TERMINATOR);
        if( cindex < 1){
            errorMessage = "Malformed message, no content terminator";
            return false;
        }

        message = myContent.substring(lindex+2,cindex);

        int idTerminator = myContent.indexOf("=");
        if( idTerminator  < 1){
            errorMessage = "Missing app request ID";
            return false;
        }

        appRequestId = myContent.substring(cindex+1, idTerminator -1);

        int lastTerminator = myContent.indexOf(TERMINATOR);
        if(lastTerminator < 1){
            errorMessage = "Missing terminator";
            return false;
        }
        String strCheckSum = myContent.substring(idTerminator + appRequestId.length(), lastTerminator-1);
        int checkSum = -1;
        try{
            checkSum = Integer.parseInt(strCheckSum);
        } catch (Exception notInt){
            errorMessage = "Checksum was not an Integer. Error: "+strCheckSum;
            return false;
        }
        if(checkSum == -1){
            errorMessage = "Malformed message, no checkSum terminator";
            return false;
        }

        decoded = true;
        return true;
    }

    /**
     * Checks if there is content in the communication variables
     * returns true if all are not null
     * @return boolean
     */
    private boolean validateDecode(){
        if (message != null && communicationTypeCode !=null && contentTypeCode !=null){
            return true;
        }
        decoded = false;
        return false;
    }

}
