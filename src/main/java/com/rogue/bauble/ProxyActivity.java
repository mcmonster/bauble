package com.rogue.bauble;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
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
    /** Interface for logging events. */
    private static final Logger logger = LoggerFactory.getLogger("ProxyActivity");
    
    /** Evaluates raw user input and passes onto the active renderer. */
    private ProxyView view;
    
    /**
     * Provides an interface for the extending class to provide the ProxyRenderer
     * implementation it desires to be used for the activity.
     * 
     * @return ProxyRenderer implementation to use. 
     */
    protected abstract ProxyRenderer getProxyRendererImpl();
    
    /** {@inheritDoc} */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set the program to full screen
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        ProxyRenderer renderer = getProxyRendererImpl();
        view = new ProxyView(this, new Device(this), renderer);
        
        renderer.setView(view);
        setContentView(view);
    }
    
    /** {@inheritDoc} */
    @Override
    public final void onPause() {
        super.onPause();
        view.onPause();
    }
    
    /** {@inheritDoc} */
    @Override
    public final void onResume() {
        logger.info("OnResume called...");
        super.onResume();
        view.onResume();
    }
}
