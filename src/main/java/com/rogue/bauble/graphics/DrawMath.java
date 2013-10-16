package com.rogue.bauble.graphics;

/**
 * Utility class containing a variety of mathematical functions.
 * 
 * @author R. Matt McCann
 */
public abstract class DrawMath {
    /**
     * Finds the smallest power of two larger than x.
     * 
     * @param x Value to find the power of two ceiling for.
     * @return Power of two ceiling.
     */
    public static float findCeilingPowerOfTwo(final float x) {
        float ceiling = 2;
        
        while (ceiling <= x) {
            ceiling *= 2;
        }
        
        return ceiling;
    }    
}
