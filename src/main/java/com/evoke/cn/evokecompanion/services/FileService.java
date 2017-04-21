package com.evoke.cn.evokecompanion.services;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class FileService{
	private static String rootDir = "";
	//Environment.getExternalStorageDirectory() + "/Folder"
	private static final String MOTOR_TEMP_DATA_LOCATION = "";
	private static final String BATTERY_CHARGE_DATA_LOCATION ="";
	private static final String DISTANCE_DATA_LOCATION = "";
	
	private AssetManager assetManager;

	public FileService(Context context){
		AssetManager assetManager = context.getAssets();
		File folder = new File(rootDir);
		if (!folder.exists()){
			boolean thing = folder.mkdir();
        }
	}
	public boolean saveMotorTemp(String[] items){
//		try{
//
//		}catch(IOException){
//
//		}
		return false;
	}

	public ArrayList<String> getMotorTemp(){
		try{
			return readCsv(MOTOR_TEMP_DATA_LOCATION);
		}catch(IOException ioe){
			ioe.printStackTrace();
			return null;
		}
	}
	public ArrayList<String> getBattery(){
		try{
			return readCsv(BATTERY_CHARGE_DATA_LOCATION);
		}catch(IOException ioe){
			ioe.printStackTrace();
			return null;
		}
	}
	public ArrayList<String> getDistance(){
		try{
			return readCsv(DISTANCE_DATA_LOCATION);
		}catch(IOException ioe){
			ioe.printStackTrace();
			return null;
		}
	}
	private ArrayList<String> readCsv(String csv_path) throws IOException {
		ArrayList<String> values = new ArrayList<String>();

		InputStream csvStream = assetManager.open(csv_path);
		InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
        BufferedReader reader = new BufferedReader(csvStreamReader);

        String line;
        while ((line = reader.readLine()) != null) {
            String[] lineData = line.split(",");
            for (String aLineData : lineData) {
                values.add(aLineData);
            }
        }

        reader.close();
        csvStreamReader.close();
        csvStream.close();

		return values;
	}
	private void saveCsv(String csv_path, String[] saveItems)throws IOException{
        //TODO: how android deals with file storage

        for(int i = 0; i <saveItems.length; i ++){
			fw.append(saveItems[i]);
			fw.append(",");
		}
		fw.flush();
		fw.close();
	}
	private InputStream getStream(String loc) throws IOException{
        return assetManager.open(loc);
    }
}