package com.rogue.bauble;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import static com.google.common.base.Preconditions.checkNotNull;
import com.rogue.bauble.device.Device;
import com.rogue.bauble.graphics.MVP;
import com.rogue.bauble.io.touch.PressTimer;
import com.rogue.bauble.misc.PointHelper;
import com.rogue.unipoint.FloatPoint2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Evaluates raw user input, passing the input gestures to the proxying renderer.
 * 
 * @author R. Matt McCann
 */
public class ProxyView extends GLSurfaceView {
    /** Used when translating input events into normalized space. */
    private final Device device;
    
    /** How far the movement action has to drag before being classified as a drag gesture. */
    private final float dragActivationDistance = 20.0f; 
    
    /** Whether or not the action has been classified as a drag gesture. */
    private boolean isDragAction = false;
    
    /** Last location touched on the screen. Used for calculating gesture vectors. */
    private FloatPoint2D lastTouchLocation;
    
    /** Interface for logging events. */
    private static final Logger logger = LoggerFactory.getLogger("ProxyView");
    
    /** Used for handling drag related input. */
    private final ProxyRenderer renderer;
    
    /** Timing thread used to detect long press events. */
    private PressTimer pressTimer;
    
    /** Detects zoom gestures. */
    private final ScaleGestureDetector zoomGestureDetector;
    
    /** Constructor. */
    public ProxyView(Activity activity, Device device, ProxyRenderer renderer) {
        super(activity.getApplicationContext());
        
        this.device = checkNotNull(device);
        this.renderer = checkNotNull(renderer);
        
        // Set the OpenGL context to be preserved when the application is paused
        setPreserveEGLContextOnPause(true);
        
        // Enable OpenGL ES 2.0
        setEGLContextClientVersion(2);
        
        // Bind the renderer
        setRenderer(renderer);
        
        // Enable manual rendering control by the game loop controller
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        
        // Set up the input detectors
        this.zoomGestureDetector = new ScaleGestureDetector(
                activity.getApplicationContext(), new ZoomGestureDetector(renderer));
    }
    
    /** {@inheritDoc} */
    @Override
    public void onPause() {
        super.onPause();
        renderer.onPause();
    }
    
    /** {@inheritDoc} */
    @Override
    public void onResume() {
        logger.info("OnResume called...");
        super.onResume();
        renderer.onResume();
    }
    
    /** {@inheritDoc} */
    @Override
    public synchronized final boolean onTouchEvent(final MotionEvent event) {
        FloatPoint2D touchLocation = PointHelper.normalize(device, event);
        
        // Check if a scale gesture occurred
        zoomGestureDetector.onTouchEvent(event);
        
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                lastTouchLocation = touchLocation;
                isDragAction = false;
                
                pressTimer = new PressTimer(renderer, System.currentTimeMillis(),
                        touchLocation.scaleXBy(1 / device.getWidth())
                                     .scaleYBy(1 / device.getHeight()));
                new Thread(pressTimer).start();
                
                break;
            case MotionEvent.ACTION_MOVE:
                if (((lastTouchLocation.distanceTo(touchLocation) > dragActivationDistance) || isDragAction)) {
                    pressTimer.setIsShuttingDown(true);
                    
                    if (!isDragAction) { 
                        isDragAction = renderer.handlePickUp(new MVP(),
                                touchLocation.scaleXBy(1 / device.getWidth())
                                             .scaleYBy(1 / device.getHeight()));
                    }
                    
                    FloatPoint2D moveVector = touchLocation.subtract(lastTouchLocation);
                    lastTouchLocation = touchLocation;
                    
                    if (isDragAction) {
                        return renderer.handleDrag(
                                moveVector.scaleXBy(1 / device.getWidth())
                                          .scaleYBy(1 / device.getHeight()));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                pressTimer.setIsShuttingDown(true);
                
                // If the action was long press
                if (pressTimer.isLongPress()) {
                    return true;
                // If the action was a drag gesture
                } else if (isDragAction) {
                    isDragAction = false;
                    return renderer.handleDrop(
                            touchLocation.scaleXBy(1 / device.getWidth())
                                         .scaleYBy(1 / device.getHeight()));
                }
                // Otherwise the action was a simple click gesture
                else {
                    return renderer.handleClick(new MVP(),
                            touchLocation.scaleXBy(1 / device.getWidth())
                                         .scaleYBy(1 / device.getHeight()));
                }
        }
        
        return true;
    }
    
    /** Simple zoom gesture detector. */
    final class ZoomGestureDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private final ProxyRenderer handler;
        
        public ZoomGestureDetector(final ProxyRenderer handler) {
            this.handler = handler;
        }
        
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d("Input", "Zoom of scale " + detector.getScaleFactor());
            return handler.handleZoom(detector.getScaleFactor());
        }
    }
}
