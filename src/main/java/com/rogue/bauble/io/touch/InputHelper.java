package com.rogue.bauble.io.touch;

import com.rogue.bauble.graphics.MVP;
import com.rogue.unipoint.FloatPoint2D;

public class InputHelper {
    public static boolean isTouched(final MVP mvp, final FloatPoint2D touchLocation) {
        return isTouched(mvp.collapse(), touchLocation);
    }
    
    public static boolean isTouched(final float[] transformationSpace, 
                                    final FloatPoint2D touchLocation) {
        float        height   = transformationSpace[5];
        FloatPoint2D position = new FloatPoint2D(transformationSpace[12], 
                                                 transformationSpace[13]);
        float        width    = transformationSpace[0];
        
        return isTouched(position, width, height, touchLocation);
    }
    
    public static boolean isTouched(final FloatPoint2D position, 
                                    final FloatPoint2D size, 
                                    final FloatPoint2D touchLocation) {
        return isTouched(position, size.getX(), size.getY(), touchLocation);
    }
    
    public static boolean isTouched(final FloatPoint2D position, final float width, final float height,
            final FloatPoint2D touchLocation) {
        return ((position.getX() - width / 2.0f <= touchLocation.getX()) &&
                (position.getX() + width / 2.0f >= touchLocation.getX()) && 
                (position.getY() - height / 2.0f <= touchLocation.getY()) &&
                (position.getY() + height / 2.0f >= touchLocation.getY()));
    }
}