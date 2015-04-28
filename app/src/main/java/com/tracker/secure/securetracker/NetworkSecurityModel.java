package com.tracker.secure.securetracker;

import android.net.NetworkInfo;

/**
 * Created by mtaylor on 23/04/15.
 */
public interface NetworkSecurityModel
{
    int determineNetworkSecurityLevel(NetworkInfo networkInfo);
}
