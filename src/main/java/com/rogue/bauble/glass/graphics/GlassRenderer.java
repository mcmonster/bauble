package com.rogue.bauble.glass.graphics;

import com.google.common.eventbus.EventBus;
import com.rogue.bauble.graphics.MVP;
import com.rogue.bauble.graphics.Renderer;
import com.rogue.bauble.graphics.flow.GameFlowController;
import com.rogue.unipoint.FloatPoint2D;

/**
 * Provides an interface for handling common rendering details.
 * 
 * @author R. Matt McCann
 */
public abstract class GlassRenderer extends Renderer {
    /**
     * @param gameFlow Must not be null.
     */
    public GlassRenderer(GameFlowController gameFlowController,
                         EventBus notifier) {
        super(gameFlowController, notifier);
    }

    @Override
    public boolean handleClick(MVP transformationSpace, FloatPoint2D clickLocation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean handleLongPress(MVP transformationSpace, FloatPoint2D pressLocation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean handlePickUp(MVP transformationSpace, FloatPoint2D touchLocation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean handleDrag(FloatPoint2D moveVector) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean handleDrop(FloatPoint2D dropLocation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean handleZoom(float zoomFactor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
