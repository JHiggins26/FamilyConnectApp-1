package familyconnect.familyconnect;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import static android.content.Context.LOCATION_SERVICE;

/**
 * GPSLocation.java - a class that detects the gps signal's latitude and longitude from the device.
 *
 * @author  Jawan Higgins
 * @version 1.0
 * @created 2017-11-23
 */
public class GPSLocation implements LocationListener {

    private Context context;

    /**
     * @method GPSLocation()
     *
     * This constructor method initializes the context of the activity.
     *
     * @param context
     */
    public GPSLocation(Context context) {
        super();
        this.context = context;
    }

    /**
     * @method getLocation()
     *
     * This method provide access to find the location of the device.
     *
     * @return null
     */
    public Location getLocation(){

        if (ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        try {
            LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled){
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000,10,this);
                Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                return loc;
            }else{

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @method onLocationChanged()
     *
     * This method listens for a change in the devices location.
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
    }

    /**
     * @method onStatusChanged()
     *
     * This method gets the status of the location when the device's location has changed.
     *
     * @param provider
     * @param status
     * @param extras
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /**
     * @method onProviderEnabled()
     *
     * This method enables the GPS Providers access.
     *
     * @param provider
     */
    @Override
    public void onProviderEnabled(String provider) {
    }

    /**
     * @method onProviderDisabled()
     *
     * This method disables the GPS Providers access.
     *
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider) {
    }
}