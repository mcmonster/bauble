package com.rogue.bauble.io.touch;

import com.rogue.bauble.graphics.MVP;
import com.rogue.unipoint.FloatPoint2D;

/**
 * Handles user input drag events.
 * 
 * @author R. Matt McCann
 */
public interface DragHandler {
    /**
     * Handles the pick up, or start of the drag, event.
     * 
     * @param transformationSpace The current transformations applied to the model space
     * @param touchLocation Where on the screen the drag began in normalized 
     * coordinates
     * @return Whether or not the event was handled.
     */
    boolean handlePickUp(MVP transformationSpace, final FloatPoint2D touchLocation);
    
    /**
     * Handles the continuing drag event.
     * 
     * @param moveVector How far the drag has moved since the last drag event in
     * normalized coordinates
     * @return Whether or no the event was handled.
     */
    boolean handleDrag(final FloatPoint2D moveVector);
    
    /**
     * Handles the termination of a drag gesture.
     * 
     * @param dropLocation Where on the screen the drag was terminated in
     * normalized coordinates.
     * @return Whether or not the event was handled.
     */
    boolean handleDrop(final FloatPoint2D dropLocation);
}
