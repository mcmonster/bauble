package com.rogue.bauble.widgets;

import android.graphics.Paint;
import android.opengl.Matrix;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rogue.bauble.graphics.MVP;
import com.rogue.bauble.graphics.shaders.SimpleTexturedShader;
import com.rogue.bauble.graphics.text.GlyphString;
import com.rogue.bauble.graphics.text.GlyphString.GlyphStringFactory;
import com.rogue.bauble.graphics.textures.Texture;
import com.rogue.bauble.misc.Constants;

/**
 * Labeled version of the check box.
 * 
 * @author R. Matt McCann
 */
public class LabeledCheckBox extends CheckBox {
    /** Label for the check box indicates what it represents. */
    private final GlyphString label;
    
    /** Guice compatible constructor. */
    @Inject
    public LabeledCheckBox(@Named("CheckedBox") Texture checkedBox,
                           GlyphStringFactory glyphFactory,
                           SimpleTexturedShader shader,
                           @Named("UncheckedBox") Texture uncheckedBox) {
        super(checkedBox, shader, uncheckedBox);
        
        label = glyphFactory.create();
        label.setAlignment(Paint.Align.LEFT);
        label.setHeight(0.8f);
    }
    
    /** {@inheritDocs} */
    @Override
    public void renderExt(MVP mvp) {
        float[] modelSpace = mvp.peekCopyM();
        float   xOffset;

        super.renderExt(mvp); // Render the check box
        
        // Move into label space
        xOffset = 0.625f + label.getWidth() / 2;
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, xOffset, 0, 0);

        // Render the label
        mvp.pushM(modelSpace);
        label.render(mvp);
        mvp.popM();
    }
    
    public void setLabel(String text) {
        label.setText(text);
    }
}
