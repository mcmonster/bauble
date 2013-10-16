package com.rogue.bauble.graphics.textures;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Contains the texture reference as well as some additional meta-data.
 * 
 * @author R. Matt McCann
 */
public class Texture {
    /** Width-by-height aspect ratio of the image. */
    private float aspectRatio = 1.0f;
    
    /** OpenGL engine reference to the loaded texture. */
    private int handle;
    
    /** Raw image reference. */
    private final int rawImage;
    
    /** @param rawImage Resource id of the texture image. */
    public Texture(int rawImage) {
        this.rawImage = rawImage;
    }
    
    /**
     * @param aspectRatio Must be greater than 0.0
     * @param rawImage Resource id of the texture image.
     */
    public Texture(float aspectRatio, int rawImage) {
        checkArgument(aspectRatio > 0.0f, "Expected aspect ratio > 0.0f, got %s", aspectRatio);
        
        this.aspectRatio = aspectRatio;
        this.rawImage = rawImage;
    }
    
    public float getAspectRatio() { return aspectRatio; }
    
    public int getHandle() { return handle; }
    
    public int getRawImage() { return rawImage; }
    
    /** @param handle Must be OpenGL engine reference. Must be > 0. */
    public void setHandle(int handle) {
        checkArgument(handle > 0, "Handle must be > 0, got %s", handle);
        
        this.handle = handle;
    }
}
