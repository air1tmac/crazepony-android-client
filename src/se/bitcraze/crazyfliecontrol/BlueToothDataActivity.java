package se.bitcraze.crazyfliecontrol;


import se.bitcraze.communication.BluetoothService;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

public class BlueToothDataActivity extends Activity {

	private ToggleButton togglebutton;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	private void startBluetoothService() {
		startService(new Intent(this, BluetoothService.class));
	}
	
	private void stopBluetoothService() {
		stopService(new Intent(this, BluetoothService.class));
	}
	
}
