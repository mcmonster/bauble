package com.rogue.bauble;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.rogue.bauble.device.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point activity used by the application. Delegates view and rendering
 * behavior out to the currently activated components.
 * 
 * @author R. Matt McCann
 */
public abstract class ProxyActivity extends Activity {
    /** Gesture detector for Google Glass touch gestures. */
    private GestureDetector glassGestureDetector;
    
    /** Interface for logging events. */
    private static final Logger logger = LoggerFactory.getLogger("ProxyActivity");
    
    /** Used to pass on activity level input events. */
    private ProxyRenderer renderer;
    
    /** Evaluates raw user input and passes onto the active renderer. */
    private ProxyView view;
    
    /**
     * Provides an interface for the extending class to provide the ProxyRenderer
     * implementation it desires to be used for the activity.
     * 
     * @return ProxyRenderer implementation to use. 
     */
    protected abstract ProxyRenderer getProxyRendererImpl();
    
    public ProxyView getView() { return view; }
    
    /** {@inheritDoc} */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        logger.debug("onCreate()...");
        super.onCreate(savedInstanceState);
        
        // Set the program to full screen
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        glassGestureDetector = new GlassGestureDetector(this);
        renderer = getProxyRendererImpl();
        view = new ProxyView(this, new Device(this), renderer);
        
        renderer.setView(view);
        setContentView(view);
    }
    
    /** Handles the motion events triggered by Google Glass. */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        logger.debug("Generic motion event has occurred!");
        return glassGestureDetector.onMotionEvent(event);
    }
    
    /** {@inheritDoc} */
    @Override
    public final void onPause() {
        logger.debug("onPause()...");
        super.onPause();
        view.onPause();
    }
    
    /** {@inheritDoc} */
    @Override
    public final void onResume() {
        logger.debug("OnResume called...");
        super.onResume();
        view.onResume();
    }
    
    /** Handles Google glass touch gestures. */
    class GlassGestureDetector extends GestureDetector {
        public GlassGestureDetector(Context context) {
            super(context);
            
            setBaseListener(new GestureDetector.BaseListener() {
                /** {@inheritDocs} */
                @Override
                public boolean onGesture(Gesture gesture) {
                    if (gesture == Gesture.SWIPE_LEFT) {
                        logger.debug("Left swipe has occurred!");
                        return renderer.handleSwipeLeft();
                    } else if (gesture == Gesture.SWIPE_RIGHT) {
                        logger.debug("Right swipe has occurred!");
                        return renderer.handleSwipeRight();
                    }
                    
                    return false;
                }
            });
        }
    }
}
