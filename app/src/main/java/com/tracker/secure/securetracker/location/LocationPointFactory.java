package com.tracker.secure.securetracker.location;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by mtaylor on 24/04/15.
 */
public class LocationPointFactory
{
    private GoogleApiClient client;

    public LocationPointFactory(GoogleApiClient client)
    {
        this.client = client;
    }

    public LocationPoint createLocationPoint()
    {
        if (client.isConnected()) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(client);
            return new LocationPoint(location.getLongitude(), location.getLatitude(), System.currentTimeMillis());
        }
        return null;
    }
}
