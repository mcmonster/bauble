package com.rogue.bauble.io.touch;

import com.rogue.bauble.graphics.MVP;
import com.rogue.unipoint.FloatPoint2D;


/**
 * Handles the user input click events.
 * 
 * @author R. Matt McCann
 */
public interface ClickHandler {
    /**
     * Handles the click event.
     * 
     * @param transformationSpace Transformation that have led to this click handler.
     * @param clickLocation Location in screen coordinates where the click occurred.
     * @return Whether or not the click event was handled.
     */
    boolean handleClick(MVP transformationSpace, final FloatPoint2D clickLocation);
}
