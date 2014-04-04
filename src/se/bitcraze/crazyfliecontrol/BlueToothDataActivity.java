package se.bitcraze.crazyfliecontrol;


import se.bitcraze.communication.BluetoothService;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

public class BlueToothDataActivity extends Activity {

	private ToggleButton togglebutton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_data);

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
	
	private void startBluetoothService() {
		startService(new Intent(this, BluetoothService.class));
	}
	
	private void stopBluetoothService() {
		stopService(new Intent(this, BluetoothService.class));
	}
	
}
