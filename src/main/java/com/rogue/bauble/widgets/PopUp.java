package com.rogue.bauble.widgets;

import android.opengl.Matrix;
import static com.google.common.base.Preconditions.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rogue.bauble.graphics.MVP;
import com.rogue.bauble.graphics.shaders.SimpleTexturedShader;
import com.rogue.bauble.graphics.textures.Texture;
import com.rogue.bauble.misc.Constants;
import com.rogue.bauble.properties.Renderable;
import com.rogue.unipoint.FloatPoint2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple drop-shadowed panel creating a pleasant pop-up.
 * 
 * @author R. Matt McCann
 */
public class PopUp implements Renderable {
    /** Texture of the drop shadow background. */
    private final Texture dropShadow;
    
    /** Interface for logging events. */
    private static final Logger logger = LoggerFactory.getLogger("PopUp");
    
    /** Rendering position of the pop-up. */
    private FloatPoint2D position = new FloatPoint2D(0, 0);
    
    /** Used to draw the pop-up. */
    private final SimpleTexturedShader shader;
    
    /** Rendering size of the pop-up. */
    private FloatPoint2D size = new FloatPoint2D(1, 1);
    
    /** Guice injectable constructor. */
    @Inject
    public PopUp(@Named("PopUpDropShadow") Texture dropShadow,
                 SimpleTexturedShader shader) {
        this.dropShadow = checkNotNull(dropShadow);
        this.shader = checkNotNull(shader);
    }
    
    /** Guice factory. */
    public interface PopUpFactory {
        PopUp create();
    }
    
    /** {@inheritDocs} */
    @Override
    public void render(MVP mvp) {
        float[] modelSpace = mvp.peekCopyM();
        
        // Move into pop-up space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, position.getX(),
                position.getY(), 0);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, size.getX(), size.getY(), 1);
        
        // Render the drop-shadow background
        logger.info("Rendering drop-shadow background...");
        shader.activate();
        shader.setMVPMatrix(mvp.collapseM(modelSpace));
        shader.setTexture(dropShadow.getHandle());
        shader.draw();
        
        // Render the pop-up contents
        mvp.pushM(modelSpace);
        renderExt(mvp);
        mvp.popM();
    }

    /** Interface for extending classes to render the pop-up contents. */
    protected void renderExt(MVP mvp) { }
    
    public void setPosition(FloatPoint2D position) {
        this.position = checkNotNull(position);
    }
    
    public void setSize(FloatPoint2D size) {
        this.size = checkNotNull(size);
    }
}
