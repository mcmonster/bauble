package com.rogue.bauble.io.touch;

import com.rogue.unipoint.Point2D;


/**
 * Handles user input long press events.
 * 
 * @author R. Matt McCann
 */
public interface LongPressHandler {
    /**
     * Handles the long press event.
     * 
     * @param pressLocation Location in screen coordinates where the long press occurred.
     * @return Whether or not the long press event was handled.
     */
    boolean handleLongPress(final Point2D pressLocation);
}
