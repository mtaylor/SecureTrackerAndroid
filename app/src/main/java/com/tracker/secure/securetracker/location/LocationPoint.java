package com.tracker.secure.securetracker.location;

/**
 * Created by mtaylor on 24/04/15.
 */
public class LocationPoint
{
    private double longitude;

    private double latitude;

    private long time;

    public LocationPoint(double longitude, double latitude, long time)
    {
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public long getTime()
    {
        return time;
    }
}
