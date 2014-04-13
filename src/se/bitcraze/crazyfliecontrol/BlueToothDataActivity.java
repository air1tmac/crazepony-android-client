package se.bitcraze.crazyfliecontrol;


import java.util.ArrayList;

import se.bitcraze.communication.BluetoothService;
import se.bitcraze.communication.BluetoothService.BlueoothBinder;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BlueToothDataActivity extends Activity {
	
	private static final String TAG = "BlueToothDataActivity";
	
	private TextView mDataTextView;
	private MsgReceiver msgReceiver;
	private Button mSendButton;
	private EditText mSendDataEditText;
	
	BluetoothService mService;
	
	private ArrayList<String> btDataArrayList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_data);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		
		
        //动态注册广播接收器  
        msgReceiver = new MsgReceiver();  
        IntentFilter intentFilter = new IntentFilter();  
        intentFilter.addAction("com.crazepony.communication.RECEIVER");  
        registerReceiver(msgReceiver, intentFilter);  
        
        btDataArrayList = new ArrayList<String>();
        mDataTextView = (TextView) findViewById(R.id.dataTextView);
        mDataTextView.setText("");
        
        mSendDataEditText = (EditText) findViewById(R.id.send_data);
        
        mSendButton = (Button) findViewById(R.id.send_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Toast.makeText(BlueToothDataActivity.this, 
            			mSendDataEditText.getText().toString(),Toast.LENGTH_SHORT).show();
            	
            	byte[] out = mSendDataEditText.getText().toString().getBytes();
            	mService.write(out);
            }
        });

	}
	
	@Override
    protected void onStart() {
        super.onStart();
        // Bind to the service
        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
	
    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        unbindService(mConnection);
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
    
    /** 
     * 广播接收器 
     */  
    public class MsgReceiver extends BroadcastReceiver{  
  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            //拿到进度，更新UI  
            String data = intent.getStringExtra("data");
            Log.v(TAG,data);
            
            if (btDataArrayList.size() > 8) {
				btDataArrayList.remove(0);
			}
            btDataArrayList.add(data);
            
            String dataString = "";
            for(int i=0;i<btDataArrayList.size();i++)
            {
            	dataString = dataString + "\n" + (String)btDataArrayList.get(i);
            }
            
            mDataTextView.setText(dataString);
        }  
          
    }  
    
    
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
        	BlueoothBinder binder = (BlueoothBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
}


