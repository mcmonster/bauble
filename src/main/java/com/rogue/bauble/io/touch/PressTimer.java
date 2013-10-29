package com.rogue.bauble.io.touch;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.rogue.bauble.ProxyRenderer;
import com.rogue.bauble.graphics.MVP;
import com.rogue.unipoint.FloatPoint2D;

/**
 * Keeps time until enough has passed to 
 * 
 * @author R. Matt McCann
 */
public class PressTimer implements Runnable {
    private boolean isLongPress = false;
    
    private boolean isShuttingDown = false;
    
    private final long longPressTime = 500;
    
    private final FloatPoint2D pressLocation;
    
    private final ProxyRenderer proxyRenderer;
    
    private final long startTime;
    
    public PressTimer(ProxyRenderer proxyRenderer, long startTime, 
                      FloatPoint2D pressLocation) {
        checkArgument(startTime > 0, "StartTime must be > 0, got %s", startTime);
        
        this.proxyRenderer = checkNotNull(proxyRenderer);
        this.startTime = startTime;
        this.pressLocation = checkNotNull(pressLocation);
    }
    
    public boolean isLongPress() { return isLongPress; }
    
    @Override
    public void run() {
        while (!isShuttingDown) {
            if (System.currentTimeMillis() - startTime > longPressTime) {
                isLongPress = true;
                proxyRenderer.handleLongPress(new MVP(), pressLocation);
                break;
            }
            
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) { }
        }
    }
    
    public void setIsShuttingDown(final boolean isShuttingDown) {
        this.isShuttingDown = isShuttingDown;
    }
}
