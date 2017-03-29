package com.example.egczb.highaltitudeflightcontroller.Bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.egczb.highaltitudeflightcontroller.R;
import com.example.egczb.highaltitudeflightcontroller.UI.ErrorDialogFragment;

/**
 * Created by EGCZB on 3/26/2017.
 * Adapter that populates the listview based on discovered
 * bluetooth devices. This follows the same example as the
 * BLEDataLogger app and Android Developer Guide on the Bluetooth
 * Low Energy section.
 */

public class BluetoothScanAdapter extends BaseAdapter{
    //The Log tag for logging
    private final static String LOG_TAG = BluetoothScanAdapter.class.getSimpleName();
    //The BluetoothScanActivities host
    private BluetoothScanActivity hostActivity;
    //Constructor that takes the current hostActivity a.k.a the context
    public BluetoothScanAdapter(BluetoothScanActivity hostActivity){
        this.hostActivity = hostActivity;
    }
    //Our Error dialog instance
    ErrorDialogFragment errorDialogFragment;

    /*System override methods that will implement our adapter.
     * This is the same as our custom adapter used in UiAdapter under
     * UI package. For more details refer to that class as the methods are
     * explained. In this case our adapter will return the hostActivity.
     */
    @Override
    public int getCount() {
        //Return the hostActivity size
        return hostActivity.bluetoothDevices.size();
    }

    @Override
    public Object getItem(int position) {
        //Gets the current hostActivity for the device
        return hostActivity.bluetoothDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        //Returns the id of the position
        return position;
    }
    //This will inflate our layout
    @Override
    public View getView(int position, View view, ViewGroup group) {
        //If the view is null inflate the layout
        if (view == null){
            LayoutInflater inflater = (LayoutInflater)hostActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.blutooth_scan_listview, group, false);
        }
        //Initiate the Ui
        TextView txtDeviceName = (TextView)view.findViewById(R.id.txt_device_title);
        TextView txtDeviceAdd = (TextView)view.findViewById(R.id.txt_device_description);
        TextView txtDeviceRssi = (TextView)view.findViewById(R.id.txt_device_rssi);
        ImageView icon = (ImageView)view.findViewById(R.id.ble_icon_listview);
        //Set the Ui and initiate the device to get info
        final BluetoothDevice bluetoothDevice = hostActivity.bluetoothDevices.get(position);
        txtDeviceName.setText(bluetoothDevice.getName());
        txtDeviceAdd.setText(bluetoothDevice.getAddress());
        txtDeviceRssi.setText(hostActivity.deviceRSSIs.get(bluetoothDevice).toString());

        //Make the icon clickable
        icon.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //Check if the bluetooth device is null before connecting
                if(bluetoothDevice == null) {
                    errorDialogFragment = new ErrorDialogFragment();
                    errorDialogFragment.buildError(hostActivity, "Error #01", "The device appears to be null! Cannot connect to device at this moment!", "Ok");
                    return;
                }

                //Start our payload interface activity
                final Intent intent = new Intent(hostActivity, PayloadInterfaceActivity.class);
                intent.putExtra(PayloadInterfaceActivity.EXTRAS_DEVICE_NAME, bluetoothDevice.getName());
                intent.putExtra(PayloadInterfaceActivity.EXTRAS_DEVICE_ADDRESS, bluetoothDevice.getAddress());

                //Stop scanning if still scanning for a device
                //Calls boolean method isScanning and sets boolean ScanBTLE to false to stop
                if(hostActivity.isScanning()){
                    hostActivity.scanBTLEDevices(false);
                }

                hostActivity.startActivity(intent);
            }
        });

        return view;
    }
}
