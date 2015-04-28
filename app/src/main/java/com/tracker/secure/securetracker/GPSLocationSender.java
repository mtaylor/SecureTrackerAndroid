package com.tracker.secure.securetracker;

import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tracker.secure.securetracker.location.LocationPoint;
import com.tracker.secure.securetracker.location.LocationPointFactory;

import org.json.JSONObject;

import java.net.URI;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by mtaylor on 23/04/15.
 */
public class GPSLocationSender implements NetworkSecurityChangeListener, LocationListener, Runnable, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    public static final int PERIOD = 5000;

    private int securityLevel = 0;

    private static int queueDepth = 1000;

    private Queue<LocationPoint> locations = new LinkedList<LocationPoint>();

    private GoogleApiClient apiClient;

    private LocationPoint lastLocation;

    public GPSLocationSender(GoogleApiClient apiClient, URI endpoint)
    {
        this.apiClient = apiClient;
        this.apiClient.registerConnectionCallbacks(this);
        this.apiClient.registerConnectionFailedListener(this);
        this.apiClient.connect();
    }

    @Override
    public void onNetworkSecurityEvent(int securityLevel)
    {
        this.securityLevel = securityLevel;
    }

    @Override
    public void onLocationChanged(Location location)
    {
    }

    @Override
    public void run()
    {
        LocationPointFactory factory = new LocationPointFactory(apiClient);
        while (true)
        {
            store(factory.createLocationPoint());
            if (securityLevel >= 1)
            {
                for (int i=0;i<locations.size(); i++)
                {
                    send(locations.poll());
                }
            }

            try
            {
                Thread.sleep(PERIOD);
            }
            catch(InterruptedException e)
            {
            }
        }
    }

    private boolean send(LocationPoint locationPoint)
    {
        try {
            if (locationPoint != null) {
                JSONObject fields = new JSONObject();
                fields.put("user", "SecureTracker Demo User");
                fields.put("time", locationPoint.getTime());
                fields.put("longitude", locationPoint.getLongitude());
                fields.put("latitude", locationPoint.getLatitude());

                JSONObject location = new JSONObject();
                location.put("location", fields);
                RESTHandler.getInstance().create("/locations", location.toString());
            }
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private boolean store(LocationPoint locationPoint)
    {
        if (locationPoint != null) {
            if (locations.size() >= queueDepth)
            {
                locations.remove();
            }
            locations.add(locationPoint);
        }
        return true;
    }

    private LocationRequest createLocationRequest()
    {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(0);
        return locationRequest;
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, createLocationRequest(), this);
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        System.out.println("Connection Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        System.out.println("Connection failed");
    }
}
