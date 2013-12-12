package com.rogue.bauble.misc;

/**
 * Contains a few simple math routines that are useful in graphics code.
 * 
 * @author R. Matt McCann
 */
public abstract class MathHelper {
    private MathHelper() { }
    
    public static float clamp(float currentValue, float min, float max) {
        float finalValue = currentValue;
        
        if (finalValue < min) finalValue = min;
        if (finalValue > max) finalValue = max;
        
        return finalValue;
    }
    
    public static boolean isBetween(float source, float min, float max) {
        return ((min <= source) && (source <= max));
    }
}
