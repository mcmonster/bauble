package com.rogue.bauble.properties;

/**
 * Provides a simple interface for starting, stopping, and cleaning up
 * the state of a stateful object.
 * 
 * @author R. Matt McCann
 */
public interface Stateful {
    /**
     * Cleans up and finishes the state of the object. The object does not need
     * to support restarting after cleanUp() is called.
     */
    void cleanUp();
    
    /** Starts the state of the object. */
    void start();
    
    /** 
     * Stops the state of the object. The object should be able to successfully
     * start() again after stop() is called.
     */
    void stop();
}
