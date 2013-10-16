package com.rogue.bauble.graphics.textures;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the frames and rendering logic for animations.
 * 
 * @author R. Matt McCann
 */
public class Animation {
    /** Current frame being rendered. */
    private int currentFrame = 0;
    
    /** Frames comprising the animation. */
    private final List<Texture> frames = new ArrayList<Texture>(); 
    
    /** Frame rate of the animation. */
    private final int framesPerSecond;
    
    /** Last time a frame was requested. */
    private long lastFrameTime;
    
    /**
     * @param frames Must not be null. Must have length > 0.
     * @param framesPerSecond Must be > 0.
     */
    public Animation(int[] rawFrames,
                     int framesPerSecond) {
        checkArgument(rawFrames != null, "RawFrames must not be null!");
        checkArgument(rawFrames.length > 0, "RawFrames.length must be > 0, got %s", rawFrames.length);
        checkArgument(framesPerSecond > 0, "FramesPerSEcond must be > 0, got %s", framesPerSecond);
        
        for (int rawFrame : rawFrames) { // Pack the raw frames into a Texture model
            frames.add(new Texture(rawFrame));
        }
        this.framesPerSecond = framesPerSecond;
    }
    
    /**
     * @param aspectRatio Must be > 0.0f.
     * @param frames Must not be null. Must have length > 0.
     * @param framesPerSecond Must be > 0.
     */
    public Animation(float aspectRatio,
                     int[] rawFrames,
                     int framesPerSecond) {
        checkArgument(aspectRatio > 0.0f, "AspectRatio must be > 0.0f, got %s", aspectRatio);
        checkArgument(rawFrames != null, "RawFrames must not be null!");
        checkArgument(rawFrames.length > 0, "RawFrames.length must be > 0, got %s", rawFrames.length);
        checkArgument(framesPerSecond > 0, "FramesPerSEcond must be > 0, got %s", framesPerSecond);
        
        for (int rawFrame : rawFrames) { // Pack the raw frames into a Texture model
            frames.add(new Texture(aspectRatio, rawFrame));
        }
        this.framesPerSecond = framesPerSecond;
    }
    
    public List<Texture> getFrames() { return frames; }
    
    /**
     * @param framesPerSecond Must be > 0.
     * @return The current frame of the animation.
     */
    public Texture getNextFrame() {
        final long timeBetweenFrames = (long) (1000.0f / framesPerSecond);
        
        // If enough time has passed
        if (System.currentTimeMillis() - lastFrameTime > timeBetweenFrames) {
            // Move to the next frame
            lastFrameTime = System.currentTimeMillis();
            currentFrame = (currentFrame + 1) % frames.size();
            return frames.get(currentFrame);
        }
        // If not enough time has passed
        else {
            // Keep the current frame
            return frames.get(currentFrame);
        }
    }
}