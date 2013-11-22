package com.rogue.bauble.widgets;

import android.opengl.Matrix;
import static com.google.common.base.Preconditions.*;
import com.google.inject.Inject;
import com.rogue.bauble.device.Device;
import com.rogue.bauble.graphics.MVP;
import com.rogue.bauble.graphics.text.GlyphString;
import com.rogue.bauble.graphics.text.GlyphString.GlyphStringFactory;
import com.rogue.bauble.misc.Constants;
import com.rogue.bauble.properties.Renderable;
import com.rogue.unipoint.Point2D;

/**
 * Widget that displays the running average frames per second.
 * 
 * @author R. Matt McCann
 */
public class FpsCounter implements Renderable {
    /** Used to adjust for the aspect ratio. */
    private final Device device;
    
    /** Glyph string that displays the current FPS. */
    private final GlyphString fps;
    
    /** Last FPS value displayed. */
    private float lastFps;
    
    /** When the last frame was rendered. */
    private long lastFrameTime = System.currentTimeMillis();
    
    /** Guice injectable constructor. */
    @Inject
    public FpsCounter(Device device,
                      GlyphStringFactory glyphFactory) {
        this.device = checkNotNull(device);
        
        fps = glyphFactory.create();
        fps.setText("FPS: 0");
        fps.setPosition(new Point2D(0, 0));
        fps.setHeight(1.0f);
        
        lastFps = 0;
    }
    
    public float getWidth() {
        return fps.getWidth();
    }
    
    /** {@inheritDocs} */
    @Override
    public void render(MVP mvp) {
        float   newFps;
        float[] model = mvp.peekCopyM();
        
        // Calculate the new FPS
        if (lastFps >= 0) {
            newFps = (float) (lastFps * 0.9 + (1000 / (System.currentTimeMillis() - lastFrameTime)) * 0.1);
        } else {
            newFps = 1000.0f / (System.currentTimeMillis() - lastFrameTime);
        }
        lastFrameTime = System.currentTimeMillis();
        
        // Update the FPS texture if necessary
        if (Math.floor(newFps) != Math.floor(lastFps)) {
            fps.setText("FPS: " + Math.floor(newFps));
        }
        lastFps = newFps;
        
        // Render the FPS
        Matrix.scaleM(model, Constants.NO_OFFSET, 1.0f / device.getAspectRatio(), 1.0f, 1.0f);
        mvp.pushM(model);
        fps.render(mvp);
        mvp.popM();
    }
}
