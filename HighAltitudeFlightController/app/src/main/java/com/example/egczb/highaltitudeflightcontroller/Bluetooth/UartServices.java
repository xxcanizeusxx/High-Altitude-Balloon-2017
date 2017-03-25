package com.example.egczb.highaltitudeflightcontroller.Bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.sql.Connection;
import java.util.UUID;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by EGCZB on 3/24/2017.
 */

public class UartServices extends Service {
    //The tag for the log
    private final static String LOG_TAG = UartServices.class.getSimpleName();
    //The binder which will bind to clients UUID assigned to class
    private final IBinder binder = new AvailableBinder();

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private String bluetoothAddress;

    //The state the connection is in
    public enum ConnectionStatus{
        CONNECTING, CONNECTED, DISCONNECTED
    }
    //Default connection state
    private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;

    //The BLE Actions
    public final static String ACTION_GATT_CONNECTED =
            "com.example.egczb.highaltitudeflightcontroller.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.egczb.highaltitudeflightcontroller.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.egczb.highaltitudeflightcontroller.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.egczb.highaltitudeflightcontroller.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.egczb.highaltitudeflightcontroller.EXTRA_DATA";
    private static final String EXTRA_DATA_RX =
                "com.example.egczb.highaltitudeflightcontroller.EXTRA_DATA_RX";
    private static final String EXTRA_DATA_TX = "com.example.egczb.highaltitudeflightcontroller.EXTRA_DATA_TX";

    // UUIDs for the BTLE services
    public static final UUID TX_POWER_UUID = UUID.fromString("00001804-0000-1000-8000-00805f9b34fb");
    public static final UUID TX_POWER_LEVEL_UUID = UUID.fromString("00002a07-0000-1000-8000-00805f9b34fb");
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID FIRMWARE_REVISON_UUID = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    public static final UUID DIS_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

    //Constructor
    public UartServices(){

    }

    //On bind service call
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    //On unbind service call
    @Override
    public boolean onUnbind(Intent intent){
        //Close the bluetooth connection when unbind
        closeConnection();
        return super.onUnbind(intent);
    }


    //Initialize the bluetooth manager
    public boolean intializeBle() {
        //If the manager is null, initialize it
        if (bluetoothAdapter == null) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            //If it still did not initialize tag on log and return false
            if (bluetoothManager == null) {
                Log.e(LOG_TAG, "Unable to initialize the BLE manager!");
                return false;
            }
        }
        //If the manager was initialized then assign it to adapter
        if (bluetoothAdapter == null) {
            Log.e(LOG_TAG, "Could not attach to adapter!");
        }
        //If all was well return true
        return true;
    }
    /*Method that establishes a connection. This method takes the String address
     * of the device and tries to connect to it.
     */
    //Establish a connection
    public boolean OpenConnection(final String bleAddress){
        //If the adapter or address is null throw an error to the Log
        if (bluetoothAdapter == null ||bleAddress == null){
            Log.w(LOG_TAG, "Bluetooth adapter was not initialized or unspecified address!");
            return false;
        }
        //If the address is equal to device address and the gatt connection is not null
        if (bleAddress.equals(bluetoothAddress) && bluetoothGatt != null){
            //Check for the status of gatt and re-use if existing address matches
            if (bluetoothGatt.connect()){
                connectionStatus = ConnectionStatus.CONNECTED;
                return true;
            }else{
                return false;
            }
        }
        //Get the address of the device
        final BluetoothDevice bleDevice = bluetoothAdapter.getRemoteDevice(bleAddress);
        if (bleDevice == null){
            Log.w(LOG_TAG, "Device was not found unable to connect!");
            return false;
        }
        //Assigns the device connection. This line calls bluetoothGattCallBack
        bluetoothGatt = bleDevice.connectGatt(this, false, bluetoothGattCallback);
        Log.d(LOG_TAG, "Connecting to device");
        bluetoothAddress = bleAddress;
        connectionStatus = ConnectionStatus.CONNECTING;
        return true;
    }

    //Method to disconnect connection
    public void bluetoothDisconnect(){
        if(bluetoothAdapter == null || bluetoothGatt == null){
            Log.w(LOG_TAG, "Cannot disconnect!");
            return;
        }
        bluetoothGatt.disconnect();
    }

    //Method to close connection
    private void closeConnection() {
        if (bluetoothAdapter == null){
            return;
        }
        Log.w(LOG_TAG, "Bluetooth Gatt closed");
        bluetoothAddress = null;
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

    private class AvailableBinder extends Binder {
        UartServices getService(){
            return  UartServices.this;
        }
    }

    //Method that takes the inent and sends it to the broadcastUpdate
    private void broadcastUpdate(final String action){
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //Takes the intent and broadcast it
    private void broadcastUpdate(final String action, BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        //If transmit has the same uuid as the device then put the read value
        if(TX_CHAR_UUID.equals(characteristic.getUuid())) {
            intent.putExtra(EXTRA_DATA_RX, characteristic.getValue());
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    //The BluetoothGattCallback
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(newState == BluetoothProfile.STATE_CONNECTED) {
                connectionStatus = ConnectionStatus.CONNECTED;
                broadcastUpdate(ACTION_GATT_CONNECTED);

                // start service discovery
                bluetoothGatt.discoverServices();
            }
            else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectionStatus = ConnectionStatus.DISCONNECTED;
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            }
            else {
                Log.w(LOG_TAG, "Services discovered a failure: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    public void enableRXNotifications() {
        if(bluetoothGatt == null) {
            Log.e(LOG_TAG, "Can't enable RX notifications - btGatt is null!");
        }

        BluetoothGattService rxService = bluetoothGatt.getService(RX_SERVICE_UUID);
        if(rxService == null) {
            Log.e(LOG_TAG, "Device doesn't have an RX service!");
            return;
        }

        BluetoothGattCharacteristic txChar = rxService.getCharacteristic(TX_CHAR_UUID);
        if(txChar == null) {
            Log.e(LOG_TAG, "Device doesn't have a TX characteristic!");
            return;
        }

        bluetoothGatt.setCharacteristicNotification(txChar, true);

        BluetoothGattDescriptor descriptor = txChar.getDescriptor(CCCD);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
        Log.i(LOG_TAG, "RX notifications enabled!");
    }
}