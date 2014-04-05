package se.bitcraze.crazyfliecontrol;


import java.util.LinkedHashSet;

import se.bitcraze.communication.BluetoothService;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BluetoothActivity extends Activity {

	private ProgressDialog progressDialog;
	
	private LinkedHashSet<String> bluetoothDevicesName;
	private ListView mBluetoothList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		
		bluetoothDevicesName = new LinkedHashSet<String>();
		bluetoothDevicesName.add("tttt" + "\n" + "bbbbbb");
		bluetoothDevicesName.add("mmmtt" + "\n" + "bbbbbb");
		mBluetoothList = (ListView) findViewById(R.id.bluetooth_devices_list);
		mBluetoothList.setAdapter(new BluetoothAdapter(this, bluetoothDevicesName));
		mBluetoothList.setOnItemClickListener(listListener);
		
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_bluetooth, menu);
        return super.onCreateOptionsMenu(menu);
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
	
	private void startBluetoothService() {
		startService(new Intent(this, BluetoothService.class));
	}
	
	private void stopBluetoothService() {
		stopService(new Intent(this, BluetoothService.class));
	}
	
	OnItemClickListener listListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
		}
	};
	
}



