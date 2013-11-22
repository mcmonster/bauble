package com.rogue.bauble.widgets;

import android.opengl.Matrix;
import com.google.common.base.Function;
import static com.google.common.base.Preconditions.*;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.rogue.bauble.device.Device;
import com.rogue.bauble.graphics.MVP;
import com.rogue.bauble.graphics.RenderableObject;
import com.rogue.bauble.graphics.shaders.SimpleTexturedShader;
import com.rogue.bauble.graphics.text.GlyphString;
import com.rogue.bauble.graphics.text.GlyphString.GlyphStringFactory;
import com.rogue.bauble.graphics.textures.Texture;
import com.rogue.bauble.io.touch.InputHelper;
import com.rogue.bauble.misc.Constants;
import com.rogue.unipoint.FloatPoint2D;
import com.rogue.unipoint.Point2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clickable button widget.
 * 
 * @author R. Matt McCann
 */
public class Button extends RenderableObject {
    /** Background texture of the button. */
    private final Texture background;
    
    /** Used to adjust for the screen's aspect ratio. */
    private final Device device;
    
    /** The function this button is to perform when clicked. */
    private final Function<Void, Boolean> function;
    
    /** Interface for logging events. */
    private static final Logger logger = LoggerFactory.getLogger("Button");
    
    /** Used to render the button. */
    private final SimpleTexturedShader shader;
    
    /** Text label of the button. */
    private final GlyphString text;
    
    /** Guice injectable constructor. */
    @Inject
    public Button(@Named("Button") Texture background,
                  Device device,
                  @Assisted Function<Void, Boolean> function,
                  GlyphStringFactory glyphFactory,  
                  SimpleTexturedShader shader) {
        this.background = checkNotNull(background);
        this.device = checkNotNull(device);
        this.function = checkNotNull(function);
        this.shader = checkNotNull(shader);
        
        this.text = glyphFactory.create();
        this.text.setHeight(0.27f);
        this.text.setPosition(new Point2D(0.0f, 0.0f));
    }

    public interface ButtonFactory {
        Button create(Function<Void, Boolean> function);
    }
    
    /** Triggers the button's functionality. */
    public void click() {
        function.apply(null);
    }
    
    /** {@inheritDocs} */
    @Override
    public boolean handleClickExt(MVP transformationSpace, FloatPoint2D clickLocation) {
        logger.info("Click Position:     " + clickLocation);
        logger.info("Approve Button Pos: " + transformationSpace.peekM()[12] + " " + transformationSpace.peekM()[13]);
        
        if (InputHelper.isTouched(transformationSpace, clickLocation)) {
            return function.apply(null);
        }
        
        return false;
    }

    /** {@inheritDocs} */
    @Override
    public void renderExt(MVP mvp) {
        float[]      modelSpace = mvp.peekCopyM();
        FloatPoint2D size       = super.getSize();
        
        shader.activate();
        
        // Render the body of the button
        shader.setMVPMatrix(mvp.collapse());
        shader.setTexture(background.getHandle());
        shader.draw();
        
        // Render the text
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, size.getY() / size.getX(), 1, 1);
        mvp.pushM(modelSpace);
        text.render(mvp);
        mvp.popM();
    }

    public void setText(String text) {
        this.text.setText(text);
    }
    
    public void setTextHeight(float height) {
        text.setHeight(height);
    }
}