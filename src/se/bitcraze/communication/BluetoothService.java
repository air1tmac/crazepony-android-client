package se.bitcraze.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;



public class BluetoothService extends Service {
	
	// Binder given to clients
    private final IBinder mBinder = new BlueoothBinder();
	
	private static final String TAG = "BTService";
	
	private BluetoothAdapter mBluetoothAdapter = null;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	
	private LinkedHashSet<BluetoothInfo> bluetoothDevicesInfoList;
	
	private BluetoothInterface bluetoothInterface;
	private Set<BluetoothDevice> bluetoothDevices;
	private final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private Intent intent = new Intent("com.crazepony.communication.RECEIVER");  
	
	// Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    
    // Constants that indicate the current connection state
	public int mState = STATE_NONE;
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_SCANNING = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
	
	public class BlueoothBinder extends Binder {
		public BluetoothService getService() {
            return BluetoothService.this;
        }
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.w(TAG, "onCreate BTService");
		
		bluetoothDevices = new HashSet<BluetoothDevice>();
		bluetoothDevicesInfoList = new LinkedHashSet<BluetoothInfo>();
		BluetoothInfo bluetoothInfo = new BluetoothInfo();
        bluetoothInfo.setDeviceName("Test_01");
        bluetoothInfo.setDeviceMac("00:00:00:00:00:01");
        bluetoothInfo.setConnectState(false);
        bluetoothDevicesInfoList.add(bluetoothInfo);
        bluetoothInfo = new BluetoothInfo();
        bluetoothInfo.setDeviceName("Test_02");
        bluetoothInfo.setDeviceMac("00:00:00:00:00:02");
        bluetoothInfo.setConnectState(false);
        bluetoothDevicesInfoList.add(bluetoothInfo);
		
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
	}

	@Override
	public void onDestroy() {
		//关闭所有的线程
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
		
		super.onDestroy();
		Log.w(TAG, "onDestroy BTService");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return Service.START_STICKY;
	}
	
	
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
            	if (null != bluetoothInterface) {
                	bluetoothInterface.bluetoothDevicesUpdate(bluetoothDevicesInfoList);
        		}
                break;
            }
        }
    };

	
	public void  setBluetoothInterface(BluetoothInterface bluetoothInter) {
		this.bluetoothInterface = bluetoothInter;
		
		if (null != bluetoothInterface) {
			bluetoothInterface.bluetoothDevicesUpdate(bluetoothDevicesInfoList);
		}
	}
	
	
	public void startBluetoothDiscovery() {
		if(checkBluetooth()){
			//扫描新的设备
			mState = STATE_SCANNING;
			mBluetoothAdapter.startDiscovery();
		}
	}
	
	public void cancelBluetoothDiscovery() {
		mBluetoothAdapter.cancelDiscovery();
		
		//由扫描状态恢复到NONE状态，或者有连接状态
		for (BluetoothInfo bluetoothInfo : bluetoothDevicesInfoList) {
            if(true == bluetoothInfo.getConnectState()){
            	mState = STATE_CONNECTED;
            	return;
            }
        }
		mState = STATE_NONE;
	}
	
    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(int position) {
    	
    	Object[] deviceses = bluetoothDevices.toArray();
		BluetoothDevice mBluetoothDevice = (BluetoothDevice) deviceses[position - 2];

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(mBluetoothDevice);
        mConnectThread.start();
        mState = STATE_CONNECTING;
    }
	
	
    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device) {

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket,"Insecure");
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
    	for (BluetoothInfo bluetoothInfo : bluetoothDevicesInfoList) {
            if(bluetoothInfo.getDeviceMac().equals(device.getAddress())){
            	bluetoothInfo.setConnectState(true);
            }
        }
        
        Message msg = mHandler.obtainMessage(BluetoothService.MESSAGE_STATE_CHANGE);
        mHandler.sendMessage(msg);
        mState = STATE_CONNECTED;
    }
    
    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }
    
    
    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
    	mState = STATE_NONE;

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
    }
	
	/**
	 * 判斷藍芽裝置是否正常及開啟
	 * @return 藍芽裝置是否正常及開啟 (true = 沒問題)
	 */
	private boolean checkBluetooth() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "本设备不支持蓝牙", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Toast.makeText(this, "蓝牙没有开启", Toast.LENGTH_SHORT).show();
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
	            
	            // 以mac地址作为唯一判断标准，防止重复添加
	            if (null != bluetoothInterface) {
	            	for (BluetoothInfo bluetoothInfo : bluetoothDevicesInfoList) {
	                    if(bluetoothInfo.getDeviceMac().equals(device.getAddress())){
	                    	return;
	                    }
	                }
	    		}
	            
	            // Add the name and address to an array
	            BluetoothInfo bluetoothInfo = new BluetoothInfo();
	            bluetoothInfo.setDeviceName(device.getName());
	            bluetoothInfo.setDeviceMac(device.getAddress());
	            bluetoothInfo.setConnectState(false);
	            bluetoothDevicesInfoList.add(bluetoothInfo);
	            bluetoothDevices.add(device);
	            
	            Message msg = mHandler.obtainMessage(BluetoothService.MESSAGE_STATE_CHANGE);
	            mHandler.sendMessage(msg);
	            
	            Log.v(TAG, device.getName() + "\n" + device.getAddress());
	        }
	    }
	};    
	
	
    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(
                		SPP_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }
	
	/**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    
                    Log.v(TAG,new String(buffer, 0, bytes - 1));
                    
                  //发送Action为com.example.communication.RECEIVER的广播  
                    intent.putExtra("data", new String(buffer, 0, bytes - 1));  
                    sendBroadcast(intent); 
                    
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
                
                //断开连接，发送消息给ui
            	for (BluetoothInfo bluetoothInfo : bluetoothDevicesInfoList) {
                	bluetoothInfo.setConnectState(false);
                }
                
                Message msg = mHandler.obtainMessage(BluetoothService.MESSAGE_STATE_CHANGE);
                mHandler.sendMessage(msg);
                mState = STATE_CONNECTED;
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
	
	
	
	

}
