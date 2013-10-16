package com.rogue.bauble.widgets;

import android.opengl.Matrix;
import com.google.common.base.Function;
import static com.google.common.base.Preconditions.*;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.rogue.bauble.device.Device;
import com.rogue.bauble.graphics.MVP;
import com.rogue.bauble.graphics.shaders.SimpleTexturedShader;
import com.rogue.bauble.graphics.text.BitmapGlyphString.GlyphStringFactory;
import com.rogue.bauble.graphics.text.GlyphString;
import com.rogue.bauble.graphics.textures.Texture;
import com.rogue.bauble.io.touch.ClickHandler;
import com.rogue.bauble.io.touch.InputHelper;
import com.rogue.bauble.misc.Constants;
import com.rogue.bauble.properties.Renderable;
import com.rogue.unipoint.Point2D;

/**
 * Clickable button widget.
 * 
 * @author R. Matt McCann
 */
public class Button implements ClickHandler, Renderable {
    /** Background texture of the button. */
    private final Texture background;
    
    /** Used to adjust for the screen's aspect ratio. */
    private final Device device;
    
    /** The function this button is to perform when clicked. */
    private final Function<Void, Boolean> function;
    
    /** Rendering position of the button. */
    private Point2D position = new Point2D(0.0f, 0.0f);
    
    /** Used to render the button. */
    private final SimpleTexturedShader shader;
    
    /** Rendering size of the button. */
    private Point2D size = new Point2D(1.0f, 1.0f);
    
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
    }

    public interface ButtonFactory {
        Button create(Function<Void, Boolean> function);
    }
    
    /** {@inheritDocs} */
    @Override
    public boolean handleClick(MVP transformationSpace, Point2D clickLocation) {
        Point2D buttonClickPos;
        Point2D buttonClickSize;
        float[] modelSpace = transformationSpace.peekCopyM();
        float[] transformationMatrix;
        
        // Move into button space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, (float) position.getX(), 
                (float) position.getY(), 0.0f);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, (float) size.getX(),
                (float) size.getY(), 1.0f);
        transformationMatrix = transformationSpace.collapseM(modelSpace);
        buttonClickPos = new Point2D(transformationMatrix[12], transformationMatrix[13]);
        buttonClickSize = new Point2D(transformationMatrix[0], transformationMatrix[5]);
        
        if (InputHelper.isTouched(buttonClickPos, (float) buttonClickSize.getX(), 
                (float) buttonClickSize.getY(), clickLocation)) {
            return function.apply(null);
        }
        
        return false;
    }

    /** {@inheritDocs} */
    @Override
    public void render(MVP mvp) {
        float[] model = mvp.peekCopyM();
        
        shader.activate();
        
        // Position the button
        Matrix.translateM(model, Constants.NO_OFFSET, (float) position.getX(), 
                (float) position.getY(), 0.0f);
        mvp.pushM(model);
        
        // Render the background of the button
        Matrix.scaleM(model, Constants.NO_OFFSET, (float) size.getX() / device.getAspectRatio(),
                (float) size.getY(), 1.0f);
        shader.setMVPMatrix(mvp.collapseM(model));
        shader.setTexture(background.getHandle());
        shader.draw();
        
        // Render the text
        model = mvp.popM();
        Matrix.scaleM(model, Constants.NO_OFFSET, (float) size.getY(), (float) size.getY(), 1.0f);
        mvp.pushM(model);
        text.render(mvp);
        mvp.popM();
    }

    public void setPosition(Point2D position) {
        this.position = checkNotNull(position);
    }
    
    public void setText(String text) {
        this.text.setHeight(0.27f);
        this.text.setPosition(new Point2D(0.0f, 0.0f));
        this.text.setText(text);
    }
    
    public void setSize(Point2D size) {
        checkArgument((size.getX() > 0) && (size.getY() > 0), "Expected size.x"
                + " and size.y both to be greater than 0, got size.x = %s, "
                + "size.y = %s", size.getX(), size.getY());
        
        this.size = size;
    }
}