package com.rogue.bauble.graphics;

import android.opengl.GLES20;
import android.opengl.Matrix;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import com.rogue.bauble.graphics.flow.GameFlowController;
import com.rogue.bauble.io.touch.ClickHandler;
import com.rogue.bauble.io.touch.DragHandler;
import com.rogue.bauble.io.touch.GlassTouchHandler;
import com.rogue.bauble.io.touch.LongPressHandler;
import com.rogue.bauble.io.touch.ZoomHandler;
import com.rogue.bauble.misc.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides common rendering work behind the scenes as convenience.
 * 
 * @author R. Matt McCann
 */
public abstract class Renderer implements ClickHandler, LongPressHandler, 
        DragHandler, GlassTouchHandler, ZoomHandler {
    /** Handles the flow of the rendering. */
    private final GameFlowController gameFlowController;
    
    /** Interface for logging events. */
    private final Logger logger = LoggerFactory.getLogger("Renderer");
    
    /** Allows the application to trigger application level events. */
    private final EventBus notifier;
    
    /**
     * @param gameFlow Must not be null.
     */
    public Renderer(GameFlowController gameFlowController,
                    EventBus notifier) {
        this.gameFlowController = checkNotNull(gameFlowController);
        this.notifier = checkNotNull(notifier);
        
        // Enable transparency blending
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        
        // Activate the game flow controller
        gameFlowController.start();
    }
    
    /** Cleans up the resources allocated by the renderer. */
    public void close() {
        gameFlowController.setIsRunning(false);
    }
    
    /** Draws the current frame. */
    public void drawFrame(final MVP mvp) {
        logger.debug("drawFrame()...");
        
        // Set up the parameters of the projection matrix
        float near = -1.0f;
        float far = 1.0f;
        float left = -0.5f;
        float right = 0.5f;
        float top = 0.5f;
        float bottom = -0.5f;
        
        // Calculate the projection matrix
        float[] projectionMatrix = new float[Constants.MATRIX_SIZE];
        Matrix.orthoM(projectionMatrix, Constants.NO_OFFSET, left, right, bottom, top, near, far);
        mvp.pushP(projectionMatrix);
        
        drawFrameExt(mvp);
    }
    
    /** Sub-class specific functionality for drawing the current frame. */
    protected abstract void drawFrameExt(final MVP mvp);
    
    public EventBus getNotifier() { return notifier; }
    
    /** {@inheritDocs} */
    @Override
    public boolean handleSwipeLeft() { return false; }
    
    /** {@inheritDocs} */
    @Override
    public boolean handleSwipeRight() { return false; }
}