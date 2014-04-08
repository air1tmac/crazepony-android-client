package se.bitcraze.communication;

import java.util.LinkedHashSet;

import android.bluetooth.BluetoothAdapter;


public interface BluetoothInterface {
	void bluetoothDevicesUpdate(LinkedHashSet<String> bluetoothDevices);
	void hasConnected(String bluetoothDevices);
	void turnOnBluetooth(BluetoothAdapter bluetoothAdapter);
}
