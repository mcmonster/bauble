package com.rogue.bauble.widgets;

import android.opengl.Matrix;
import static com.google.common.base.Preconditions.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.rogue.bauble.graphics.MVP;
import com.rogue.bauble.graphics.flow.GameTickEvent;
import com.rogue.bauble.graphics.shaders.SimpleTexturedShader;
import com.rogue.bauble.graphics.textures.Texture;
import com.rogue.bauble.misc.Constants;
import com.rogue.bauble.properties.Renderable;
import com.rogue.bauble.properties.Stateful;
import com.rogue.unipoint.FloatPoint2D;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Widget displaying rotating orbs with an optional progress message
 * in the middle.
 * 
 * @author R. Matt McCann
 */
public class LoadingOrbs implements Renderable, Stateful {
    /** Interface for logging events. */
    private static final Logger logger = LoggerFactory.getLogger("LoadingOrbs");
    
    /** Used to add/remove this object from the pub/sub bus. */
    private final EventBus notifier;
    
    /** Number of orbs in the widget. */
    private int numOrbs;
    
    /** Texture of the orb used to create the spinning orbs effect. */
    private final Texture orb;
    
    /** Radial spacing between the orbs in degrees. */
    private float orbSpacing;
    
    /** Rendering position of the loading orbs widget. */
    private FloatPoint2D position = new FloatPoint2D(0, 0);
    
    /** Rotational position of the primary orb in degrees. */
    private float rotationPos;
    
    /** How long it takes to complete a full rotation in seconds. */
    private float rotationPeriod;
    
    /** Used to draw the loading orbs. */
    private final SimpleTexturedShader shader;
    
    /** Rendering size of the loading orbs widget. */
    private FloatPoint2D size = new FloatPoint2D(1, 1);
    
    /** Guice injectable constructor. */
    @Inject
    public LoadingOrbs(Configuration config,
                       EventBus notifier,
                       @Named("Orb") Texture orb,
                       SimpleTexturedShader shader) {
        this.notifier = checkNotNull(notifier);
        this.numOrbs = config.getInt("LoadingOrbs.NumOrbs", 9);
        this.orb = checkNotNull(orb);
        this.orbSpacing = config.getFloat("LoadingOrbs.OrbSpacing", 30);
        this.rotationPeriod = config.getFloat("LoadingOrbs.RotationPeriod", 1);
        this.shader = checkNotNull(shader);
    }
    
    /** Guice factory. */
    public interface LoadingOrbsFactory {
        LoadingOrbs create();
    }
    
    /** {@inheritDocs} */
    @Override
    public void cleanUp() { }
    
    /**
     * This is called every time a game tick event is published by the 
     * game flow controller. Used to rotate the orbs.
     * 
     * @param gameTick Must not be null. 
     */
    @Subscribe
    public void onGameTickEvent(GameTickEvent gameTick) {
        float distanceMoved = 360.0f / gameTick.getTicksPerSecond();
        distanceMoved /= rotationPeriod;
        
        float newRotationPos = rotationPos + distanceMoved;
        if (newRotationPos > 360) newRotationPos -= 360;
        
        rotationPos = newRotationPos;
    }
    
    /** {@inheritDocs} */
    @Override
    public void render(MVP mvp) {
        float[] modelSpace = mvp.peekCopyM();
        
        shader.activate(); // Turn on our shader
        
        // Move into loading orbs widget space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, position.getX(), position.getY(), 0);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, size.getX(), size.getY(), 1);
        mvp.pushM(modelSpace);
        
        // Render each of the loading orbs
        for (int orbIter = 0; orbIter < numOrbs; orbIter++) {
            logger.info("Rendering orb " + orbIter + "...");
            float angularOffset     = rotationPos - orbIter * orbSpacing; 
            float largestOrbSize    = 0.1f;
            float orbSize           = largestOrbSize * (float) Math.pow(0.85f, orbIter);
            float translationOffset = 0.5f - largestOrbSize / 2.0f; 
            
            modelSpace = mvp.peekCopyM(); // Undo the last orbs transformations
            
            // Transform into orb space
            Matrix.rotateM(modelSpace, Constants.NO_OFFSET, angularOffset, 0, 0, 1);
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, translationOffset, 0, 0);
            Matrix.scaleM(modelSpace, Constants.NO_OFFSET, orbSize, orbSize, 1);
            
            // Render the orb
            shader.setMVPMatrix(mvp.collapseM(modelSpace));
            shader.setTexture(orb.getHandle());
            shader.draw();
        }
        
        mvp.popM();
    }
    
    public void setPosition(FloatPoint2D position) {
        this.position = checkNotNull(position);
    }
    
    public void setSize(FloatPoint2D size) {
        this.size = checkNotNull(size);
    }
    
    /** {@inheritDocs} */
    @Override
    public void start() {
        rotationPos = 0.0f;
        notifier.register(this);
    }
    
    /** {@inheritDocs} */
    @Override
    public void stop() {
        notifier.unregister(this);
    }
}
