package se.bitcraze.crazyfliecontrol;


import android.app.Activity;
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
				}
				// 当按钮再次被点击时候响应的事件
				else {
				}
			}
		});

	}

    @SuppressWarnings("deprecation")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            break;
        }
        return true;
    }
	

	
}


