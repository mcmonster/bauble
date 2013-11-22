package com.rogue.bauble.graphics.text;

import android.opengl.Matrix;
import static com.google.common.base.Preconditions.*;
import com.google.inject.Inject;
import com.rogue.bauble.graphics.MVP;
import com.rogue.bauble.graphics.text.GlyphString.GlyphStringFactory;
import com.rogue.bauble.misc.Constants;
import com.rogue.bauble.properties.Renderable;
import com.rogue.unipoint.FloatPoint2D;
import com.rogue.unipoint.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Model of a renderable paragraph.
 * 
 * @author R. Matt McCann
 */
public class GlyphParagraph implements Renderable {
    /** Used to create new glyph string. */
    private final GlyphStringFactory glyphFactory;
    
    /** Height of the lines in the paragraph. */
    private float lineHeight = 1;
    
    /** Lines of text comprising the paragraph. */
    private List<GlyphString> lines = new ArrayList<GlyphString>();
    
    /** Rendering position of the paragraph. */
    private FloatPoint2D position = new FloatPoint2D(0, 0);
    
    /** Rendering size of the paragraph. */
    private FloatPoint2D size = new FloatPoint2D(1, 1);

    /** Guice injectable constructor. */
    @Inject
    public GlyphParagraph(GlyphStringFactory glyphFactory) {
        this.glyphFactory = checkNotNull(glyphFactory);
    }
    
    /** {@inheritDocs} */
    @Override
    public void render(MVP mvp) {
        float[] modelSpace = mvp.peekCopyM();
        
        // Transform into paragraph space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, position.getX(), 
                position.getY(), 0);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, size.getX(), size.getY(), 1);
        mvp.pushM(modelSpace);
        
        // Render the lines of the paragraph
        for (GlyphString line : lines) {
            line.render(mvp);
        }

        mvp.popM();
    }
    
    public void setLineHeight(float lineHeight) {
        checkArgument(lineHeight > 0, "LineHeight must be > 0, got %s", lineHeight);
        
        this.lineHeight = lineHeight;
    }
    
    public void setPosition(FloatPoint2D position) {
        this.position = checkNotNull(position);
    }
    
    public void setSize(FloatPoint2D size) {
        this.size = checkNotNull(size);
    }
    
    public void setText(List<String> text) {
        float             lineSpacing;
        List<GlyphString> newLines = new ArrayList<GlyphString>();
        float             yOffset = 0.5f;
        
        // Determine the spacing between the lines
        if (text.size() > 1) {
            lineSpacing = ((size.getY() / text.size()) % 1) / (text.size() - 1);
        } else {
            lineSpacing = 0;
        }
        
        for (String lineText : text) { // Create glyph strings for each line
            yOffset -= lineHeight / 2;
            
            GlyphString line = glyphFactory.create();
            line.setHeight(lineHeight);
            line.setPosition(new Point2D(0, yOffset));
            line.setText(lineText);
            newLines.add(line);
            
            yOffset -= lineHeight / 2 + lineSpacing;
        }
        
        // Start using the new paragraph contents
        this.lines = newLines;
    }
}
