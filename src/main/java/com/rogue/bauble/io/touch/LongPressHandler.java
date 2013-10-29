package com.rogue.bauble.io.touch;

import com.rogue.bauble.graphics.MVP;
import com.rogue.unipoint.FloatPoint2D;

/**
 * Handles user input long press events.
 * 
 * @author R. Matt McCann
 */
public interface LongPressHandler {
    /**
     * Handles the long press event.
     * 
     * @param transformationSpace Current view-space transformations.
     * @param pressLocation Location in screen coordinates where the long press occurred.
     * @return Whether or not the long press event was handled.
     */
    boolean handleLongPress(MVP transformationSpace, FloatPoint2D pressLocation);
}
