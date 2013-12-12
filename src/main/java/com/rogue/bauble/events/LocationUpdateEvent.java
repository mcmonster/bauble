package com.rogue.bauble.events;

import static com.google.common.base.Preconditions.*;
import com.rogue.unipoint.LatLonPoint;

/**
 * Thrown when the tablet's position is updated.
 * 
 * @author R. Matt McCann
 */
public class LocationUpdateEvent {
    private final LatLonPoint location;
    
    public LocationUpdateEvent(LatLonPoint location) {
        this.location = checkNotNull(location);
    }
    
    public LatLonPoint getLocation() { return location; }
}
