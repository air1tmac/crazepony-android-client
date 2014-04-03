package se.bitcraze.crazyfliecontrol;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class BlueToothDataActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_data);
        
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }
}
