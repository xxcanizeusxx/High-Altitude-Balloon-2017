package com.example.egczb.highaltitudeflightcontroller.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.example.egczb.highaltitudeflightcontroller.R;
import com.example.egczb.highaltitudeflightcontroller.UI.ErrorDialogFragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BluetoothScanActivity extends AppCompatActivity {
    //The request status
    private static final int ENABLE_REQUEST = 1;
    //The text to be displayed to help the user.
    private TextView txtHint;
    //The handler for the activity
    private Handler handler = new Handler();
    //The boolean variable indicating the scanning status
    private boolean scanning = false;
    //The floating action button
    private FloatingActionButton floatingScanButton;
    //The list that will house our devices
    public List<BluetoothDevice> bluetoothDevices;
    //The devices RSSI in integer
    public HashMap<BluetoothDevice, Integer> deviceRSSIs;
    //The  scan adapter for the scan activity
    private BluetoothScanAdapter scanAdapter;
    //The bluetooth adapter
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scan);

        //Make sure that the user is able to use bluetooth Low Energy
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            //Display error dialog if they cant and finish the activity.
            ErrorDialogFragment.buildError(this, "Bluetooth Low Energy Not Supported!", "It appears your device is not bluetooth low energy compotabile, sorry!", "Ok");
            finish();
        }

        //Initialize the bluetooth scanAdapter
        final BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        //Initialize the hint
        txtHint = (TextView)findViewById(R.id.txt_hint);

        // initialize the device list
        bluetoothDevices = new ArrayList<BluetoothDevice>();
        deviceRSSIs = new HashMap<BluetoothDevice, Integer>();

        //Initialize the floating action button and perform the scan on click and stop on click
        floatingScanButton = (FloatingActionButton)findViewById(R.id.scan_floating_button);
        floatingScanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startScan(v);
            }
        });
    }


    //Android activity lifecycle methods
    @Override
    protected void onResume() {
        super.onResume();

        //Make sure that bluetooth is still enabled
        if(!bluetoothAdapter.isEnabled()) {
            //If it is not request to enable it
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, ENABLE_REQUEST);
        }

        //Initialize the listView
        final ListView lv = (ListView)findViewById(R.id.scan_listview);
        //Create the adapter based on the BluetoothScanAdapter
        scanAdapter = new BluetoothScanAdapter(this);
        //Set the adapter to the listView
        lv.setAdapter(scanAdapter);

        // start scanning
        //scanBTLEDevices(true);
    }

    //When the activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        //Set the scan to false impeding scans
        scanBTLEDevices(false);
    }

    //This is called when the user clicks the floating scan button
    public void startScan(View view) {
        //If we are scanning and the user clicks the button
        if(scanning) {
            //Stop the scan
            scanBTLEDevices(false);
        }
        else {
            //Start the scan
            scanBTLEDevices(true);
        }
    }

    //Method to return the scanning status
    public boolean isScanning() {
        return scanning;
    }
    //Method to set the status of the hint
    private void setScanning(final boolean enable) {
        scanning = enable;
        if(scanning) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtHint.setText("Press to scan --->");
                }
            });
        }
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtHint.setText("Press again to stop --->");
                }
            });
        }
    }
    //To control the Activity based on requestCode
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ENABLE_REQUEST && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Method To scan for devices
    public void scanBTLEDevices(final boolean enable) {
        if(bluetoothAdapter == null || bluetoothAdapter.getBluetoothLeScanner() == null) {
            ErrorDialogFragment.buildError(this, "Null Pointer Exception", "The adapters were null, please try again", "Ok");
            return;
        }

        if(enable) {
            //Only scan for 5 seconds
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanBTLEDevices(false);
                }
            }, 5000);

            //scanning = true;
            setScanning(true);

            bluetoothDevices.clear();
            deviceRSSIs.clear();
            scanAdapter.notifyDataSetChanged();

            //Filter explicitly for the UART
            List<ScanFilter> filters = new ArrayList<ScanFilter>();
            filters.add(new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")).build());

            //Setup the scan settings
            ScanSettings settings = new ScanSettings.Builder().setReportDelay(0).setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            bluetoothAdapter.getBluetoothLeScanner().startScan(filters, settings, scanCallback);
        }
        else {
            //scanning = false;
            setScanning(false);
            bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
        }
    }

    //Scan Callback here we add to the list
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if(!bluetoothDevices.contains(device)) {
                bluetoothDevices.add(device);
                scanAdapter.notifyDataSetChanged();
                deviceRSSIs.put(device, result.getRssi());
            }
        }
        //Method will be called if scan fails
        @Override
        public void onScanFailed(final int errorCode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ErrorDialogFragment.buildError(getApplicationContext(),"Scanning Error!", "The system encountered an error while scanning!", "Ok");
                    Log.d("BTLE", "Scan failed (" + errorCode + ")");
                    finish();
                    return;
                }
            });
        }
    };
}