package com.rogue.bauble.graphics;

import android.opengl.Matrix;
import static com.google.common.base.Preconditions.*;
import com.rogue.bauble.io.touch.ClickHandler;
import com.rogue.bauble.io.touch.DragHandler;
import com.rogue.bauble.io.touch.LongPressHandler;
import com.rogue.bauble.misc.Constants;
import com.rogue.bauble.properties.Renderable;
import com.rogue.unipoint.FloatPoint2D;

/**
 * Provides basic position and sizing properties of a renderable object.
 * 
 * @author R. Matt McCann
 */
public abstract class RenderableObject 
        implements ClickHandler, DragHandler, LongPressHandler, Renderable {
    /** Rendering position of the object. */
    private FloatPoint2D position = new FloatPoint2D(0, 0);
    
    /** Rendering size of the object. */
    private FloatPoint2D size = new FloatPoint2D(1, 1);
    
    public FloatPoint2D getPosition() { return position; }
    
    public FloatPoint2D getSize() { return size; }
    
    /** {@inheritDocs} */
    @Override
    public boolean handleClick(MVP transformationSpace, FloatPoint2D clickLocation) {
        float[] modelSpace = transformationSpace.peekCopyM();
        boolean result;
        
        // Move into object space
        transform(modelSpace);
        
        // Check if the object was clicked
        transformationSpace.pushM(modelSpace);
        result = handleClickExt(transformationSpace, clickLocation);
        transformationSpace.popM();
        
        return result;
    }
    
    /** Interface for extending class to handle clicks. */
    protected boolean handleClickExt(MVP transformationSpace, 
                                     FloatPoint2D clickLocation) { 
        return false; 
    }
    
    /** {@inheritDocs} */
    @Override
    public boolean handleDrag(FloatPoint2D dragVector) { return false; }
    
    /** {@inheritDoc} */
    @Override
    public boolean handleDrop(FloatPoint2D dropLocation) { return false; }
    
    /** {@inheritDoc} */
    @Override
    public boolean handleLongPress(MVP transformationSpace, FloatPoint2D pressLocation) {
        float[] modelSpace = transformationSpace.peekCopyM();
        boolean result;
        
        // Move into object space
        transform(modelSpace);
        
        // Check if the object was clicked
        transformationSpace.pushM(modelSpace);
        result = handleLongPressExt(transformationSpace, pressLocation);
        transformationSpace.popM();
        
        return result;
    }
    
    /** Interface for extending class to handle long press events. */
    protected boolean handleLongPressExt(MVP transformationSpace, 
                                         FloatPoint2D pressLocation) {
        return false;
    }
    
    /** {@inheritDocs} */
    @Override
    public boolean handlePickUp(MVP transformationSpace, 
                                FloatPoint2D touchLocation) {
        float[] modelSpace = transformationSpace.peekCopyM();
        boolean result;
        
        // Move into object space
        transform(modelSpace);
        
        // Check if the object was clicked
        transformationSpace.pushM(modelSpace);
        result = handlePickUpExt(transformationSpace, touchLocation);
        transformationSpace.popM();
        
        return result;
    }
    
    /** Interface for extending class to handle pick up events. */
    protected boolean handlePickUpExt(MVP transformationSpace, 
                                      FloatPoint2D touchLocation) {
        return false;
    }
    
    /** {@inheritDocs} */
    @Override
    public final void render(MVP mvp) {
        float[] modelSpace = mvp.peekCopyM();
        
        // Move into object space
        transform(modelSpace);
        
        // Render the object
        mvp.pushM(modelSpace);
        renderExt(mvp);
        mvp.popM();
    }
    
    /** Interface for extending classes to render themselves. */
    protected abstract void renderExt(MVP mvp);
    
    public void setPosition(float x, float y) {
        this.position = new FloatPoint2D(x, y);
    }
    
    public void setPosition(FloatPoint2D position) {
        this.position = checkNotNull(position);
    }
    
    public void setSize(float width, float height) {
        setSize(new FloatPoint2D(width, height));
    }
    
    public void setSize(FloatPoint2D size) {
        checkArgument(size.getX() > 0, "Size.x must be > 0, got %s", size.getX());
        checkArgument(size.getY() > 0, "Size.y must be > 0, got %s", size.getY());
        
        this.size = size;
    }
    
    /**
     * Applies the rendering transformations to the MVP matrices.
     * 
     * @param mvp Must not be null. 
     */
    private void transform(float[] modelSpace) {
        // Move into object space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, position.getX(),
                position.getY(), 0);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, size.getX(), size.getY(), 1);
    }
}
