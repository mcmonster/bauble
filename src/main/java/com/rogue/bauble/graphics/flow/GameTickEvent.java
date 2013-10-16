package com.rogue.bauble.graphics.flow;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Event triggered when a game unit of test elapses.
 * 
 * @author R. Matt McCann
 */
public class GameTickEvent {
    private final int ticksPerSecond;
    
    /**
     * @param ticksPerSecond Must be greater than 0. 
     */
    public GameTickEvent(int ticksPerSecond) {
        checkArgument(ticksPerSecond > 0, "Expected ticksPerSecond > 0, "
                + "got %s", ticksPerSecond);
        
        this.ticksPerSecond = ticksPerSecond;
    }
    
    public int getTicksPerSecond() { return ticksPerSecond; }
}

