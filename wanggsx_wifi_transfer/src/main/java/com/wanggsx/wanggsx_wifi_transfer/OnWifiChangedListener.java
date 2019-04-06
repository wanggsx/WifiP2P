package com.wanggsx.wanggsx_wifi_transfer;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.Collection;

public interface OnWifiChangedListener extends WifiP2pManager.ChannelListener {

    void wifiP2pEnabled(boolean enabled);

    void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo);

    void onDisconnection();

    void onSelfDeviceAvailable(WifiP2pDevice wifiP2pDevice);

    void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList);

}