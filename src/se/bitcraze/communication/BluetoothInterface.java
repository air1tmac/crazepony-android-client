package se.bitcraze.communication;

import java.util.LinkedHashSet;


public interface BluetoothInterface {
	void bluetoothDevicesUpdate(LinkedHashSet<BluetoothInfo> bluetoothDevices);
}
