package com.rogue.bauble.graphics.flow;

import android.opengl.GLSurfaceView;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls the update-render flow of the game.
 * 
 * @author R. Matt McCann
 */
@Singleton
public class GameFlowController extends Thread {
    /** Whether or not the game flow is running. */
    private boolean isRunning = false;
    
    /** Interface for logging events. */
    private final Logger logger = LoggerFactory.getLogger("GameFlowController");
    
    /** Used to notify interested objects of a rendering event. */
    private final EventBus notifier;
    
    /** Interface for triggering a render. */
    private final GLSurfaceView renderer;
    
    /**
     * Injectable constructor.
     * 
     * @param notifier Used to notify interested objects of a rendering event. 
     * Must not be null.
     * @param renderer Interface for triggering a render. Must not be null.
     */
    @Inject
    public GameFlowController(EventBus notifier, GLSurfaceView renderer) {
        this.notifier = checkNotNull(notifier);
        this.renderer = checkNotNull(renderer);
    }
    
    /** {@inheritDocs} */
    @Override
    public void run() {
        int stateUpdatesSinceLastRender;
        isRunning = true; // Set the game loop as running
        
        while (isRunning) { // While the controller has not been shut down
            long flowStart = System.currentTimeMillis();
            int  maxSkippedRenders = 20;
            long timeLeftInFlow;
            
            // Render a frame
            logger.debug("Requesting render...");
            renderer.requestRender();
            stateUpdatesSinceLastRender = 0;
            
            // Keep updating the game state until we have enough time left to perform a rendering
            do {
                int ticksPerSecond = 50; //TODO This should fluxuate!!
                int oneSecondInMillis = 1000;
                int tickPeriod = oneSecondInMillis / ticksPerSecond;
                
                // Notify interested objects that a frame has been rendered
                notifier.post(new GameTickEvent(ticksPerSecond));
                
                // Update the number up state updates that have occurred
                stateUpdatesSinceLastRender++;
                
                // Calculate how much time is left over after updating the state
                final long flowStopDeadline = flowStart + tickPeriod * stateUpdatesSinceLastRender;
                timeLeftInFlow = flowStopDeadline - System.currentTimeMillis();
            } while ((timeLeftInFlow < 0) && 
                     (stateUpdatesSinceLastRender < maxSkippedRenders));
            
            // Sleep until its time to render another frame
            if (timeLeftInFlow > 0) {
                try {
                    Thread.sleep(timeLeftInFlow);
                } catch (InterruptedException ex) { }
            }
        }
    }
    
    public void setIsRunning(boolean isRunning) { this.isRunning = isRunning; }
}
