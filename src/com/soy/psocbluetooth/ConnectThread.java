package com.soy.psocbluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private SocketHandler mHandler;
    
    interface SocketHandler {
    	void onBthConnect( BluetoothSocket socket );    	
    }
    
    public ConnectThread(BluetoothDevice device, String uuid, SocketHandler handler ) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
        mHandler = handler;
 
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // 
            tmp = mmDevice.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
        } catch (IOException e) { }
        mmSocket = tmp;
    }
 
    public void run() {
    	BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        // Cancel discovery because it will slow down the connection
    	adapter.cancelDiscovery();
 
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
            
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }
 
        // Do work to manage the connection (in a separate thread)
        mHandler.onBthConnect(mmSocket);
    }
 
    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
