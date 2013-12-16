package com.rogue.bauble;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import static com.google.common.base.Preconditions.*;
import com.google.common.eventbus.EventBus;
import com.rogue.bauble.device.OnPauseEvent;
import com.rogue.bauble.device.OnResumeEvent;
import com.rogue.bauble.graphics.MVP;
import com.rogue.bauble.graphics.Renderer;
import com.rogue.bauble.io.touch.ClickHandler;
import com.rogue.bauble.io.touch.DragHandler;
import com.rogue.bauble.io.touch.GlassTouchHandler;
import com.rogue.bauble.io.touch.LongPressHandler;
import com.rogue.bauble.io.touch.ZoomHandler;
import com.rogue.unipoint.FloatPoint2D;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delegates rendering and user input handling to the currently activated
 * rendering component.
 * 
 * @author R. Matt McCann
 */
public abstract class ProxyRenderer implements ClickHandler, DragHandler, 
        GlassTouchHandler, GLSurfaceView.Renderer, LongPressHandler, ZoomHandler {
    /** Activity registered with the Android OS. */
    private final ProxyActivity activity;
    
    /** Currently active renderer. */
    private Renderer currentRenderer;
    
    /** Interface for logging events. */
    private static final Logger logger = LoggerFactory.getLogger("ProxyRenderer");
    
    /** Android rendering interface. */
    private ProxyView view;
    
    /**
     * Constructor.
     * 
     * @param activity This android activity. Must not be null. 
     */
    public ProxyRenderer(final ProxyActivity activity) {
        this.activity = checkNotNull(activity);
    }
    
    public ProxyActivity getActivity() { return activity; }
    
    public Context getContext() { return activity.getApplicationContext(); }
    
    public EventBus getNotifier() { return currentRenderer.getNotifier(); }
    
    public ProxyView getView() { return view; }
    
    /** {@inheritDocs} */
    @Override
    public boolean handleClick(MVP transformationSpace, FloatPoint2D clickLocation) {
        if (currentRenderer != null) {
            return currentRenderer.handleClick(transformationSpace, clickLocation);
        }
        
        return false;
    }
    
    /** {@inheritDocs} */
    @Override
    public boolean handleDrag(FloatPoint2D moveVector) {
        return currentRenderer.handleDrag(moveVector);
    }

    /** {@inheritDocs} */
    @Override
    public boolean handleDrop(FloatPoint2D dropLocation) {
        return currentRenderer.handleDrop(dropLocation);
    }
    
    /** {@inheritDocs} */
    @Override
    public boolean handleLongPress(MVP transformationSpace, FloatPoint2D pressLocation) {
        if (currentRenderer != null) {
            return currentRenderer.handleLongPress(transformationSpace, pressLocation);
        }
        
        return false;
    }
    
    /** {@inheritDocs} */
    @Override
    public boolean handlePickUp(MVP transformationSpace, FloatPoint2D touchLocation) {
        if (currentRenderer != null) {
            return currentRenderer.handlePickUp(transformationSpace, touchLocation);
        }
        
        return false;
    }
    
    /** {@inheritDocs} */
    @Override
    public boolean handleSwipeLeft() {
        return currentRenderer.handleSwipeLeft();
    }
    
    /** {@inheritDocs} */
    @Override
    public boolean handleSwipeRight() {
        return currentRenderer.handleSwipeRight();
    }
    
    /** {@inheritDocs} */
    @Override
    public boolean handleZoom(float zoomFactor) {
        return currentRenderer.handleZoom(zoomFactor);
    }
    
    /** {@inheritDocs} */
    @Override
    public synchronized void onDrawFrame(GL10 arg0) {
        logger.debug("onDrawFrame()...");
        
        // Draw the background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        
        currentRenderer.drawFrame(new MVP());
    }

    /** Posts a pause event when the activity is paused. */
    public void onPause() {
        if (currentRenderer != null) {
            currentRenderer.getNotifier().post(new OnPauseEvent());
        }
    }
    
    /** Posts a resume event when the activity is resumed. */
    public void onResume() {
        logger.info("onResume called...");
        if (currentRenderer != null) {
            logger.info("Proxied renderer exists, OnResume called.");
            currentRenderer.getNotifier().post(new OnResumeEvent());
        }
    }
    
    /** {@inheritDocs} */
    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        logger.debug("onSurfaceChanged(width=" + width + ", height=" + height + ")...");
        
        // Set up the view-port
        GLES20.glViewport(0, 0, width, height);
    }

    /** {@inheritDocs} */
    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        logger.debug("onSurfaceCreated()...");
        currentRenderer = onSurfaceCreatedExt();
    }
    
    /** 
     * Provides an interface for the extending class to instantiate the initial
     * renderer instance. This instance should be created using a Guice injector.
     * 
     * @return Starting renderer instance. 
     */
    protected abstract Renderer onSurfaceCreatedExt();
    
    public synchronized void setRenderer(final Renderer renderer) {
        logger.debug("setRenderer()...");
        currentRenderer.close();
        currentRenderer = renderer;
    }

    public void setView(final ProxyView view) { this.view = view; }
}
