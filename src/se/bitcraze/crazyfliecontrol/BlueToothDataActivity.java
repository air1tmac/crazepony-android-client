package se.bitcraze.crazyfliecontrol;


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
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

public class BlueToothDataActivity extends Activity {

	private ToggleButton togglebutton;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_data);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

		togglebutton = (ToggleButton) findViewById(R.id.bluetoothButton);
		togglebutton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 当按钮第一次被点击时候响应的事件
				if (togglebutton.isChecked()) {
					startBluetoothService();
				}
				// 当按钮再次被点击时候响应的事件
				else {
					stopBluetoothService();
				}
			}
		});

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
	
}


