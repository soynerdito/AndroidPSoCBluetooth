package com.soy.psocbluetooth;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.soy.psocbluetooth.ConnectThread.SocketHandler;

public class MainActivity extends Activity implements OnClickListener, SocketHandler, OnSeekBarChangeListener {

	private static String BTH_DEVICE_NAME = "linvor";	
	private static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	private Button mConnectButton;
	// Get the default adapter
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private int REQUEST_ENABLE_BT = 2;
	private ConnectedThread mConnThread;
	private ConnectThread mConnection;

	private static final String STATUS = "STATUS";
	
	private void setStatus(boolean connected ){
		enableSeekBar( connected );
		mConnectButton.setText(connected?"Disconnect":"Connect");
	}
	
	private void enableSeekBar(boolean enable ){
		SeekBar seek = (SeekBar)findViewById(R.id.seekBar1);
		seek.setEnabled(enable);
	}
	ConnectionHandler mHandler = new ConnectionHandler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle b = msg.getData();
			setStatus( b.getInt(STATUS, 0)>0 );
			
		}

		@Override
		protected void onDisconnect() {
			postMessage( false );
			mConnThread = null;
		}

		@Override
		protected void onDataReceived(int count, byte[] values) {
			
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
		
		mConnectButton = (Button)findViewById(R.id.button1);
		mConnectButton.setOnClickListener(this);
		
		SeekBar seek = (SeekBar)findViewById(R.id.seekBar1);
		seek.setOnSeekBarChangeListener(this);
		
		setStatus(false);
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
			if( mConnThread !=null ){
				mConnThread.cancel();
				mConnThread = null;
			}
			if( mConnection!=null ){
				mConnection.cancel();
				mConnection = null;
				setStatus(false);
			}else{
				BluetoothDevice device = getDevice(BTH_DEVICE_NAME);
				if( device != null ){
					 mConnection = new ConnectThread(device, SPP_UUID, this);
					 mConnection.start();
				}
			}
		}
		
	}

	//this is running on a thread on the background so be careful	
	@Override
	public void onBthConnect( BluetoothSocket socket ){
		mConnThread = new ConnectedThread( socket, mHandler );		
		mConnThread.start();
		
		postMessage(true);
	}
	
	private void postMessage(boolean connected ){
		Message msg = new Message();
		Bundle b = new Bundle();
	    b.putInt(STATUS, (connected?1:0));
	    msg.setData(b);
	    mHandler.sendMessage(msg);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		sendMessage( seekBar.getProgress() );		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		sendMessage( seekBar.getProgress() );		
	}

	private void sendMessage(int progress) {
		// TODO Auto-generated method stub
		if( mConnThread!=null ){
			mConnThread.write( new byte[]{(byte) progress} );	
		}
		
	}
	
}