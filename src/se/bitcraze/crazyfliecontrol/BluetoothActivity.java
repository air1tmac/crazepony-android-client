package se.bitcraze.crazyfliecontrol;


import java.util.LinkedHashSet;

import se.bitcraze.communication.BluetoothInterface;
import se.bitcraze.communication.BluetoothService;
import se.bitcraze.communication.BluetoothService.BlueoothBinder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BluetoothActivity extends Activity {

	private ProgressDialog progressDialog;
	
	BluetoothService mService;
    boolean mBound = false;
	
	private LinkedHashSet<String> bluetoothDevicesName;
	private ListView mBluetoothList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		
		mBluetoothList = (ListView) findViewById(R.id.bluetooth_devices_list);
		mBluetoothList.setOnItemClickListener(listListener);

	}
	
	@Override
    protected void onStart() {
        super.onStart();
        // Bind to the service
        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_bluetooth, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        unbindService(mConnection);
        mBound = false;
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            break;
	    case R.id.action_search:
	    	progressDialog = new ProgressDialog(this);
	    	progressDialog.setTitle(R.string.bluetooth_scan_title);
	    	progressDialog.setMessage("Please wait...");
	    	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    	progressDialog.setCancelable(false);
	    	progressDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int i){
	             }
	         });
	    	
	    	progressDialog.show();
	    	
			break;
        }
        return true;
    }
    
    private void updateBluetoothList() {
    	if (null == bluetoothDevicesName) {
			return;
		}
    	mBluetoothList.setAdapter(new BluetoothAdapter(this, bluetoothDevicesName));
	}
	

	
	OnItemClickListener listListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			mService.connect(position);
		}
	};
	
	
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
        	BlueoothBinder binder = (BlueoothBinder) service;
            mService = binder.getService();
            mBound = true;
            
            //注册回调接口来接收BluetoothService的变化  
            mService.setBluetoothInterface(new BluetoothInterface() {  
                  
                @Override  
                public void bluetoothDevicesUpdate(LinkedHashSet<String> bluetoothDevices) {  
                	bluetoothDevicesName = bluetoothDevices;
                	updateBluetoothList();
                }  
            }); 
            
            //开启第一次蓝牙扫描
            mService.startBluetoothDiscovery();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
	
}



