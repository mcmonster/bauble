package com.rogue.bauble.graphics.text;

import android.graphics.Paint;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.rogue.bauble.graphics.Color;
import com.rogue.bauble.properties.Renderable;
import com.rogue.unipoint.Point2D;

/**
 * Model of a renderable string.
 * 
 * @author R. Matt McCann
 */
public abstract class GlyphString implements Renderable {
    /** Alignment of the text inside the bitmap. */
    private Paint.Align alignment = Paint.Align.CENTER;
    
    /** Color of the text. */
    private Color color = Color.BLACK;
    
    /** Rendering height of the text. */
    private float height = 1.0f;
    
    /** Opacity of the string. */
    private float opacity = 1.0f;
    
    /** Position of the string. */
    private Point2D position = new Point2D(0.0f, 0.0f);
    
    /** The current text comprising the string. */
    private String text;

    /** Releases any resources allocated for this glyph string. */
    public abstract void delete();
    
    public Paint.Align getAlignment() {
        return alignment;
    }    
    
    public Color getColor() {
        return color;
    }

    public float getHeight() { 
        return height;
    }
    
    public float getOpacity() {
        return opacity;
    }
    
    public Point2D getPosition() {
        return position;
    }
    
    public String getText() {
        return text;
    }
    
    public abstract float getWidth();
    
    public void setAlignment(Paint.Align alignment) {
        Paint.Align oldAlignment = this.alignment;
        
        this.alignment = checkNotNull(alignment);
        
        // If the text has already been rendered
        if ((getText() != null) && (alignment != oldAlignment)) {
            updateRendering();
        }
    }
    
    public void setColor(Color color) {
        Color oldColor = this.color;
        
        this.color = checkNotNull(color);
        
        // If the text has already een rendered
        if ((getText() != null) && (color != oldColor)) {
            updateRendering();
        }
    }

    public void setHeight(float height) {
        checkArgument(height > 0.0f, "Height must be > 0.0f, got %s", height);
        
        this.height = height;
    }
    
    public void setOpacity(float opacity) {
        checkArgument(0 <= opacity && opacity <= 1, "Expected 0 <= opacity <= "
                + "1, got %s", opacity);
        
        this.opacity = opacity;
    }
    
    public void setPosition(Point2D position) {
        this.position = checkNotNull(position);
    }
    
    public void setText(String text) {
        checkArgument(text != null, "Text must not be null!");
        
        if (this.text == null || !this.text.equals(text)) { // If the text is different
            this.text = text;
            updateRendering();
        }
    }
    
    protected abstract void updateRendering();
    
    public interface GlyphStringFactory {
        GlyphString create();
    }
}
