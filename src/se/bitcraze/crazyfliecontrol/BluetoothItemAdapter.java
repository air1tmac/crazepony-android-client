package se.bitcraze.crazyfliecontrol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import se.bitcraze.communication.BluetoothInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BluetoothItemAdapter extends BaseAdapter {
	private LayoutInflater mLayoutInflater = null;
	private View mInflater = null;
	private List<BluetoothInfo> mData = null;
	private Context context = null;

	BluetoothItemAdapter(Context context, HashSet<BluetoothInfo> data) {
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mData = new ArrayList<BluetoothInfo>(data);
		this.context = context;
	}

	public int getCount() {
		return mData.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (mData != null && mData.size() > 0) {
			mInflater = mLayoutInflater.inflate(R.layout.view_bluetooth_line_item, null);
			holder = new ViewHolder();
			
			holder.txtDeviceName = (TextView) mInflater.findViewById(R.id.device_name);
			holder.txtDeviceMac = (TextView) mInflater.findViewById(R.id.device_mac);
			holder.imgConnectState = (ImageView) mInflater.findViewById(R.id.connect_state);
			mInflater.setTag(holder);
			
			holder.txtDeviceName.setText(mData.get(position).getDeviceName());
			holder.txtDeviceMac.setText(mData.get(position).getDeviceMac());
			if(false == mData.get(position).getConnectState()){
				holder.imgConnectState.setVisibility(View.INVISIBLE);
			}else{
				holder.imgConnectState.setVisibility(View.VISIBLE);
			}
		}
		return mInflater;
	}
	
	
    static class ViewHolder {
        TextView txtDeviceName;
        TextView txtDeviceMac;
        ImageView imgConnectState;
        
    }

}
