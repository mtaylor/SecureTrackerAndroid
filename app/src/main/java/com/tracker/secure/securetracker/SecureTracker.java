package com.tracker.secure.securetracker;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.net.URI;


public class SecureTracker extends ActionBarActivity implements NetworkSecurityChangeListener
{
    public static final String endpoint = "http://localhost:3000";

    private Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_tracker_home);
        setupApplicationComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_secure_tracker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        return true;
    }

    private void setupApplicationComponents()
    {
        try
        {
            URI endpointUri = new URI(endpoint);
            GPSLocationSender locationSender = new GPSLocationSender(createApiClient(), endpointUri);

            NetworkMonitor networkMonitor = new NetworkMonitor();

            // Set the initial security level
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            int initialNetworkSecurityLevel = networkMonitor.determineNetworkSecurityLevel(cm.getActiveNetworkInfo());
            onNetworkSecurityEvent(initialNetworkSecurityLevel);
            locationSender.onNetworkSecurityEvent(initialNetworkSecurityLevel);

            networkMonitor.registerListener(locationSender);
            networkMonitor.registerListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            getApplicationContext().registerReceiver(networkMonitor, intentFilter);

            t = new Thread(locationSender);
            t.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void onNetworkSecurityEvent(int securityLevel)
    {
        if (securityLevel >= 1)
        {
            setContentView(R.layout.activity_secure_tracker_network_secure);
        }
        else
        {
            setContentView(R.layout.activity_secure_tracker_network_not_secure);
        }
    }

    private GoogleApiClient createApiClient()
    {
        return new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
    }

}
