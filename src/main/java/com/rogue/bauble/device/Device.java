package com.rogue.bauble.device;

import android.app.Activity;
import android.graphics.Point;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Encapsulates all of the necessary device-specific details such as pixel 
 * dimensions of the screen, etc.
 * 
 * @author R. Matt McCann
 */
@Singleton
public class Device {
    private final float height;
    private final float width;
    
    @Inject
    public Device(final Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        
        height = size.y;
        width = size.x;
    }
    
    public float getAspectRatio() { return width / height; }
    public float getHeight() { return height; }
    public float getWidth() { return width; }
}