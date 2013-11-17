package com.soy.psocbluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private ConnectionHandler mhandler;
    
    public final int MESSAGE_READ = 4;
 
    interface SocketHandler {
    	void onBthConnect( BluetoothSocket socket );    	
    }
    
    
    
    public ConnectedThread(BluetoothSocket socket, ConnectionHandler handler ) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }
 
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }
 
    private void mySleep(long mil ){
    	try {
			Thread.sleep(mil);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
 
        byte val[] = new byte[1];
        val[0] = 'a';
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {	     
            	write( val );
            	mySleep(100);
                // Read from the InputStream            	
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                //mhandler.obtainMessage( MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                mhandler.onDataReceived(bytes, buffer);                
                mySleep(2000);
            } catch (IOException e) {
                break;
            }
        }
        mhandler.onDisconnect();
    }
 
    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }
 
    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
