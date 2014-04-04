package se.bitcraze.communication;

import java.util.LinkedHashSet;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BluetoothService extends Service {
	
	private static final String TAG = "BTService";
	
	private BluetoothAdapter mBluetoothAdapter = null;
	private final int REQUEST_ENABLE_BT = 1;
	private LinkedHashSet<String> bluetoothDevicesName;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.v(TAG, "start BTService");
		
		bluetoothDevicesName = new LinkedHashSet<String>();
		
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
		
		if(checkBluetooth()){
			mBluetoothAdapter.startDiscovery();
		}
		
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return Service.START_STICKY;
	}
	
	/**
	 * 判斷藍芽裝置是否正常及開啟
	 * @return 藍芽裝置是否正常及開啟 (true = 沒問題)
	 */
	private boolean checkBluetooth() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// 裝置不支援藍芽
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "本裝置不支援藍芽", Toast.LENGTH_SHORT).show();
			return false;
		}

		// 藍芽沒有開啟
		if (!mBluetoothAdapter.isEnabled()) {
			Toast.makeText(this, "藍芽没有开启", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}
	
	
	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array
	            bluetoothDevicesName.add(device.getName() + "\n" + device.getAddress());
	            
	            Log.v(TAG, device.getName() + "\n" + device.getAddress());
	        }
	    }
	};
	

}
