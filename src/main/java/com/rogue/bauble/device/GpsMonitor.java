package com.rogue.bauble.device;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import static com.google.common.base.Preconditions.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.rogue.bauble.events.LocationUpdateEvent;
import com.rogue.unipoint.LatLonPoint;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Monitors the GPS position of the tablet.
 * 
 * @author R. Matt McCann
 */
public class GpsMonitor implements LocationListener {
    /** Interface for accessing configurable settings. */
    private final Configuration config;

    /** Current location of the GPS. */
    private LatLonPoint location = new LatLonPoint(0, 0, 0);
    
    /** Interface for logging events. */
    private static final Logger logger = LoggerFactory.getLogger("GpsMonitor");
    
    /** Used to announce location updates. */
    private final EventBus notifier;
    
    /** Interface to the system service providing location information. */
    private final LocationManager locationManager;
    
    /** Tag used when logging events. */
    private static final String TAG = "GpsMonitor";
    
    /** Guice injection compatible constructor. */
    @Inject
    public GpsMonitor(Configuration config,
                      Context context,
                      LocationManager locationManager,
                      EventBus notifier) {
        checkArgument(config.containsKey("GpsMonitor.MinUpdateDistance"),
                "Configuration must contain definition for GpsMonitor.MinUpdateDistance!");
        checkArgument(config.containsKey("GpsMonitor.UpdatePeriod"),
                "Configuration must contain definition for GpsMonitor.UpdatePeriod!");
        
        this.config = checkNotNull(config);
        this.locationManager = checkNotNull(locationManager);
        this.notifier = checkNotNull(notifier);
        
        // Check if GPS is currently disabled
        /*if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Prompt the user to enable the GPS
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }*///TODO Experimenting
        
        notifier.register(this); // Register to receive event notifications
        
        final GpsMonitor me = this;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                logger.info("Registering GPS...");
                me.handleOnResumeEvent(new OnResumeEvent());
            }
        });
    }
    
    public LatLonPoint getLocation() { return location.clone(); }
    
    /** Handles the application resuming. */
    @Subscribe
    public void handleOnResumeEvent(final OnResumeEvent event) {
        // Attempt to retrieve the last known location
        Criteria criteria = new Criteria();
        boolean  pickOnlyEnabledProviders = false;
        String   provider = locationManager.getBestProvider(criteria, pickOnlyEnabledProviders);
        Location lastLocation = locationManager.getLastKnownLocation(provider);
        if (lastLocation != null) {
            onLocationChanged(lastLocation);
        }

        locationManager.requestLocationUpdates(provider, 
            config.getLong("GpsMonitor.UpdatePeriod"), 
            config.getFloat("GpsMonitor.MinUpdateDistance"), this);
    }

    /** {@inheritDocs} */
    @Override
    public void onLocationChanged(Location location) {
        logger.info("Location changed: " + location);
        this.location = new LatLonPoint(location.getAltitude(),
            location.getLatitude(), location.getLongitude());
        
        notifier.post(new LocationUpdateEvent(this.location));
    }

    /** {@inheritDocs} */
    @Override
    public void onStatusChanged(String string, int i, Bundle bundle) { } //TODO ?

    /** {@inheritDocs} */
    @Override
    public void onProviderEnabled(String string) { } //TODO ?

    /** {@inheritDocs} */
    @Override
    public void onProviderDisabled(String string) { } //TODO ?
    
    public void setLocation(LatLonPoint location) {
        this.location = checkNotNull(location);
    }
}