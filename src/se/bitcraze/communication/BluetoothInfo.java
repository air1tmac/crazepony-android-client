package se.bitcraze.communication;


public class BluetoothInfo {
    String deviceName;
    String deviceMac;
    Boolean connectState;
    
    public void setDeviceName(String name) {
        this.deviceName = name;
    }
   
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceMac(String mac) {
        this.deviceMac = mac;
    }
   
    public String getDeviceMac() {
        return deviceMac;
    }
    
    public void setConnectState(Boolean state) {
        this.connectState = state;
    }
   
    public Boolean getConnectState() {
        return connectState;
    }
}
