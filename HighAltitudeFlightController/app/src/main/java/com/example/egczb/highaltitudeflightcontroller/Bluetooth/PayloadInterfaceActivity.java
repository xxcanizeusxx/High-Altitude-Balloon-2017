package com.example.egczb.highaltitudeflightcontroller.Bluetooth;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.egczb.highaltitudeflightcontroller.R;

public class PayloadInterfaceActivity extends AppCompatActivity {
    // Call out Log tag for logging
    private final static String lOG_TAG = UartServices.class.getSimpleName();


    //The keyWords used to fetch our intent data from another activity
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payload_interface);

    }

}
