package com.soy.psocbluetooth;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.soy.psocbluetooth.ConnectThread.SocketHandler;

public class MainActivity extends Activity implements OnClickListener, SocketHandler {

	//private BluetoothHeadset mBluetoothHeadset;
	private static String BTH_DEVICE_NAME = "linvor";
	private static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	// Get the default adapter
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private int REQUEST_ENABLE_BT = 2;
	private ConnectedThread mConnThread;
	private ConnectThread mConnection;
	
	
	ConnectionHandler mHandler = new ConnectionHandler(){

		@Override
		protected void onDisconnect() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void onDataReceived(int count, byte[] values) {
			// TODO Auto-generated method stub
			
		}
	} ;
	
	private BluetoothDevice getDevice(String name){
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		    	if( device.getName().equals(name)){
		    		return device;
		    	}
		    }
		}
		return null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
		}
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		Button btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	
	
	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.button1 ){			
			BluetoothDevice device = getDevice(BTH_DEVICE_NAME);
			if( device != null ){
				 mConnection = new ConnectThread(device, SPP_UUID, this);
				 mConnection.start();
			}
		}
		
	}

	//this is running on a thread on the background so be careful	
	@Override
	public void onBthConnect( BluetoothSocket socket ){
		mConnThread = new ConnectedThread( socket, mHandler );
		
		mConnThread.start();
	}
	
}