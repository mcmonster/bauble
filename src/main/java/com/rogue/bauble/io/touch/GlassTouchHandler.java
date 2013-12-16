package com.rogue.bauble.io.touch;

/**
 * Defines the functionality required to handle Google Glass touch events.
 * 
 * @author R. Matt McCann
 */
public interface GlassTouchHandler {
    boolean handleSwipeLeft();
    boolean handleSwipeRight();
}
