package com.rogue.bauble.misc;

import android.view.MotionEvent;
import static com.google.common.base.Preconditions.checkArgument;
import com.rogue.bauble.device.Device;
import com.rogue.unipoint.Point2D;

/**
 * Utility class providing a variety of useful point operations.
 * 
 * @author R. Matt McCann
 */
public abstract class PointHelper {
    /**
     * Constructs a 2D point using the event coordinates which are normalized
     * against the screens dimensions.
     * 
     * @param device Must not be null.
     * @param event Must not be null.
     * @return 2D point consisting of the screen normalized event coordinates.
     */
    public static Point2D normalize(Device device, MotionEvent event) {
        checkArgument(device != null, "Device must not be null!");
        checkArgument(event != null, "Event must not be null!");
        
        Point2D point = new Point2D();
        
        point.setX(event.getX() - device.getWidth() / 2.0f);
        point.setY(-event.getY() + device.getHeight() / 2.0f);
        
        return point;
    }
}
