package com.rogue.bauble.widgets;

import static com.google.common.base.Preconditions.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rogue.bauble.graphics.MVP;
import com.rogue.bauble.graphics.RenderableObject;
import com.rogue.bauble.graphics.shaders.SimpleTexturedShader;
import com.rogue.bauble.graphics.textures.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple drop-shadowed panel creating a pleasant pop-up.
 * 
 * @author R. Matt McCann
 */
public class PopUp extends RenderableObject {
    /** Texture of the drop shadow background. */
    private final Texture dropShadow;
    
    /** Interface for logging events. */
    private static final Logger logger = LoggerFactory.getLogger("PopUp");

    /** Used to draw the pop-up. */
    private final SimpleTexturedShader shader;
    
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
    public final void renderExt(MVP mvp) {
        // Render the drop-shadow background
        logger.info("Rendering drop-shadow background...");
        shader.activate();
        shader.setMVPMatrix(mvp.collapse());
        shader.setTexture(dropShadow.getHandle());
        shader.draw();
        
        // Render the pop-up contents
        renderPopUpExt(mvp);
    }

    /** Interface for extending classes to render the pop-up contents. */
    protected void renderPopUpExt(MVP mvp) { }
}
