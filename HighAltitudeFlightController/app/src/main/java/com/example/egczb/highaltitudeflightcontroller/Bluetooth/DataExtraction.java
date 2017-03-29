package com.example.egczb.highaltitudeflightcontroller.Bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.example.egczb.highaltitudeflightcontroller.UI.ErrorDialogFragment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by EGCZB on 3/28/2017.
 */

public class DataExtraction extends Service{
    //Get our log tag
    private final static String LOG_TAG = DataExtraction.class.getSimpleName();
    //The binder that is given to clients
    private final IBinder binder = new LocalBinder();
    //The status to determine if we are Extracting or not
    private boolean extracting = false;

    //Method to return the extraction status
    public boolean isExtracting(){
        return extracting;
    }

    //The directory of files
    private File documentsFolder;
    private File logFile;
    private PrintWriter printWriter;

    //Default empty constructor
    public DataExtraction(){
    }

    //Returns the binder on onBind
    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }

    //Cleanup files and connection on onUnbind
    @Override
    public boolean onUnbind(Intent intent){
        //Method to cleanup files and connections
        terminate();
        return super.onUnbind(intent);
    }

    //The class gets the current service for the activity
    public class LocalBinder extends Binder{
        DataExtraction getService(){
            return DataExtraction.this;
        }
    }

    //Method to initialize the files takes the file name as parameter
    public boolean initializeFiles(String fileName){
        int i = 0;
        //Method to cleanup files and connections
        terminate();
        //Check to see if we can write or read to the storage on the phone
        if(!isExternalStorageWritable()){
            Log.e(LOG_TAG, "The external storage is not readable or writeable!");
            return false;
        }

        //Assign the documentsFolder variable to the current users directory folder
        documentsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        //If the folder does not exist
        if(!documentsFolder.exists()){
            if(!documentsFolder.mkdir()){
                Log.e(LOG_TAG, "The documents folder does not exist!");
                return false;
            }
        }

        //Check if the file exists
        if(!logFile.exists()){
            ////If it doesnt Create the file
            logFile = new File(documentsFolder, fileName);
        }
        else{
            ErrorDialogFragment.buildError(this, "File Exists!", "The file already exists making appropiate changes.", "Ok");
            i++;
            fileName = fileName + Integer.toString(i);
            logFile = new File(documentsFolder, fileName);
            Log.v(LOG_TAG, "The current file name is: " + fileName);

        }

        //Create a print writer for the file this will wirte an object to a file

        try {
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
        }catch (Exception e){
            Log.e(LOG_TAG, e.toString());
            return false;
        }
        //Set extracting to true
        extracting = true;
        return true;
    }

    //Method to cleanup files and connections
    public void terminate(){
        extracting = false;
        Log.d(LOG_TAG,"Cleaning up!");
        if(printWriter != null){
            printWriter.close();
        }
    }

    //Method to append the text
    public void appendToFile(String text){
        if(!extracting || printWriter == null){
            return;
        }

        printWriter.append(text);
    }

    //Method to check if external storage is available to read and write
    public boolean isExternalStorageWritable(){
        //Takes the storage state and assigns it to variable for testing
        String storageState = Environment.getExternalStorageState();
        //If the state is equal to Media mounted then return true
        if(Environment.MEDIA_MOUNTED.equals(storageState)){
            return true;
        }
        //Else return false
        return false;
    }

}
