package com.tracker.secure.securetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtaylor on 23/04/15.
 */
public class NetworkMonitor extends BroadcastReceiver implements NetworkSecurityModel
{
    public static final int MIN_NETWORK_SECURITY_LEVEL = 1;

    private List<NetworkSecurityChangeListener> listeners;

    public NetworkMonitor()
    {
        listeners = new ArrayList<>();
    }

    public void registerListener(NetworkSecurityChangeListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public int determineNetworkSecurityLevel(NetworkInfo networkInfo)
    {
        return networkInfo.getType() == ConnectivityManager.TYPE_WIFI ? 1 : 0;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        synchronized (this)
        {
            for (NetworkSecurityChangeListener listener : listeners)
            {
                if (networkInfo != null) {
                    listener.onNetworkSecurityEvent(determineNetworkSecurityLevel(networkInfo));
                }
            }
        }
    }
}
