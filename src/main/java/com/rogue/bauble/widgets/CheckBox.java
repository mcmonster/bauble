package com.rogue.bauble.widgets;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rogue.bauble.graphics.MVP;
import com.rogue.bauble.graphics.RenderableObject;
import com.rogue.bauble.graphics.shaders.SimpleTexturedShader;
import com.rogue.bauble.graphics.textures.Texture;
import com.rogue.bauble.io.touch.InputHelper;
import com.rogue.unipoint.FloatPoint2D;

/**
 * Simple check box widget.
 * 
 * @author R. Matt McCann
 */
public class CheckBox extends RenderableObject {
    /** Texture displaying a checked checkbox. */
    private final Texture checkedBox;
    
    /** Whether or not the check box is checked. */
    private boolean isChecked = false;
    
    /** Function rans when the checkbox is checked/unchecked. */
    private Optional<Function<Boolean, Boolean>> onChangeFunc = Optional.absent();
    
    /** Used to draw the check box. */
    private final SimpleTexturedShader shader;
    
    /** Texture displaying an unchecked checkbox. */
    private final Texture uncheckedBox;
    
    /** Guice compatible constructor. */
    @Inject
    public CheckBox(@Named("CheckedBox") Texture checkedBox,
                    SimpleTexturedShader shader,
                    @Named("UncheckedBox") Texture uncheckedBox) {
        this.checkedBox = checkNotNull(checkedBox);
        this.shader = checkNotNull(shader);
        this.uncheckedBox = checkNotNull(uncheckedBox);
    }
    
    /** {@inheritDocs} */
    @Override
    protected boolean handleClickExt(MVP mvp, FloatPoint2D clickPos) {
        // If the checkbox is clicked
        if (InputHelper.isTouched(mvp, clickPos)) {
            isChecked = !isChecked; // Check/uncheck the box
            
            if (onChangeFunc.isPresent()) { // If an onchange function is defined
                return onChangeFunc.get().apply(isChecked); // Execute it
            }
            
            return true;
        }
        
        return false;
    }
    
    public boolean isChecked() { return isChecked; }

    /** {@inheritDocs} */
    @Override
    public void renderExt(MVP mvp) {
        shader.activate();
        
        shader.setMVPMatrix(mvp.collapse());
        shader.setTexture(isChecked ? checkedBox.getHandle(): uncheckedBox.getHandle());
        shader.draw();
    }
    
    public void setOnChangeFunc(Function<Boolean, Boolean> onChangeFunc) {
        this.onChangeFunc = Optional.of(onChangeFunc);
    }
}
