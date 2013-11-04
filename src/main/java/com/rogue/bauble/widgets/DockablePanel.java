package com.rogue.bauble.widgets;

import android.opengl.Matrix;
import static com.google.common.base.Preconditions.*;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.rogue.bauble.device.Device;
import com.rogue.bauble.graphics.Color;
import com.rogue.bauble.graphics.DrawUtils;
import com.rogue.bauble.graphics.MVP;
import com.rogue.bauble.graphics.shaders.SimpleColorShader;
import com.rogue.bauble.graphics.shaders.SimpleTexturedShader;
import com.rogue.bauble.graphics.textures.Texture;
import com.rogue.bauble.io.touch.ClickHandler;
import com.rogue.bauble.io.touch.DragHandler;
import com.rogue.bauble.io.touch.InputHelper;
import com.rogue.bauble.io.touch.LongPressHandler;
import com.rogue.bauble.misc.Constants;
import com.rogue.bauble.properties.Renderable;
import com.rogue.unipoint.FloatPoint2D;
import com.rogue.unipoint.Point2D;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retractable panel that can be loaded with content.
 * 
 * @author R. Matt McCann
 */
public class DockablePanel implements ClickHandler, DragHandler, LongPressHandler, Renderable {
    /** Backgound VBO of the panel. */
    private final int backgroundVBO;
    
    /** Texture of the panel's border. */
    private final Texture borderTexture;
    
    /** Width of the panel border. */
    private final float borderWidth;
    
    /** Used to draw the background of the panel. */
    private final SimpleColorShader colorShader;
    
    /** Offset positioning of the content area. */
    private Point2D contentPos;
    
    /** Width of the content area. */
    private final float contentWidth = 0.79f;
    
    /** Used to compensate for the screen's aspect ratio. */
    private final Device device;
    
    /** Whether or not the panel is currently being dragged. */
    private boolean isBeingDragged = false;
    
    /** Whether or not the panel is currently being scrolled. */
    private boolean isBeingScrolled = false;
    
    /** Whether or not the panel content is being dragged. */
    private boolean isContentBeingDragged = false;
    
    /** 
     * Whether or not the panel is mirrored. When not mirrored, the panel is 
     * oriented to the left side of the screen whereas when mirrored, it is 
     * oriented to the right side of the screen.
     */
    private final boolean isMirrored;
    
    /** Interface for logging events. */
    private final Logger logger = LoggerFactory.getLogger("DockablePanel");
    
    /** Current center point position of the panel. */
    private Point2D position;
    
    /** Used to to draw the pull tab and border of the panel. */
    private final SimpleTexturedShader textureShader;
    
    /** Texture of the draggable tab. */
    private final Texture tabTexture;
    
    /** Size of the draggable tab. */
    private final Point2D tabSize = new Point2D(0.1f, 0.2f);
    
    /** X position of the panel when it is fully expanded. */
    private final float xPosExpanded;
    
    /** X position of the panel when it is fully retracted. */
    private final float xPosRetracted;
   
    /** Guice injectable constructor. */
    @Inject
    public DockablePanel(@Named("PanelBorder") Texture borderTexture,
                         SimpleColorShader colorShader,
                         Device device,
                         @Assisted("isMirrored") boolean isMirrored,
                         @Named("PanelTab") Texture tabTexture,
                         SimpleTexturedShader textureShader) {
        this.backgroundVBO = DrawUtils.buildUnitSquarePcVbo(Color.PANEL_GRAY);
        this.colorShader = checkNotNull(colorShader);
        this.device = checkNotNull(device);
        this.isMirrored = isMirrored;
        this.borderTexture = checkNotNull(borderTexture);
        this.tabTexture = checkNotNull(tabTexture);
        this.textureShader = checkNotNull(textureShader);
        
        if (isMirrored) { // If the panel is right aligned
            contentPos    = new Point2D(0.0f, 0.0f);
            xPosRetracted = 0.5f - (0.5f - contentWidth) / device.getAspectRatio();
            xPosExpanded  = xPosRetracted - contentWidth / device.getAspectRatio(); 
        } else { // If the panel is left aligned
            contentPos    = new Point2D(-0.0f, 0.0f);
            xPosRetracted = -0.5f + (0.5f - contentWidth) / device.getAspectRatio();
            xPosExpanded  = xPosRetracted + contentWidth / device.getAspectRatio();
        }
        
        logger.info("IsMirrored: " + isMirrored);
        logger.info("Retracted:  " + xPosRetracted);
        logger.info("Expanded:   " + xPosExpanded);
        
        borderWidth = (float) tabSize.getX() / 2;
        position = new Point2D(xPosRetracted, 0.0f);
    }
    
    public interface DockablePanelFactory {
        DockablePanel create(@Assisted("isMirrored") boolean isMirrored);
    }
    
    /** 
     * Interface for extending classes to tell the dockable panel how tall the
     * content is for scrolling purposes.
     */
    protected float getContentHeight() { return 0.0f; }
    
    /** {@inheritDocs} */
    @Override
    public boolean handleClick(MVP transformationSpace, FloatPoint2D clickLocation) {
        float[] modelSpace = transformationSpace.peekCopyM();
        boolean result;
        
        // Transform into panel space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, (float) position.getX(),
                (float) position.getY(), 0.0f);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, 1 / device.getAspectRatio(), 1.0f, 1.0f);
        
        // Transform into background space
        if (isMirrored) {
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, 0.5f - contentWidth / 2.0f, 0, 0);
        } else {
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, -0.5f + contentWidth / 2.0f, 0, 0);
        }
        
        // Transform into content space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, (float) contentPos.getX(), 
                    (float) contentPos.getY(), 0.0f);
        
        // Check if the extending content is clicked
        transformationSpace.pushM(modelSpace);
        result = handleClickExt(transformationSpace, clickLocation);
        transformationSpace.popM();
        
        return result;
    }
    
    /** Interface for extending classes to add click handler functionality. */
    protected boolean handleClickExt(MVP transformationSpace, FloatPoint2D clickLocation) {
        return false;
    }
    
    /** {@inheritDocs} */
    @Override
    public boolean handleLongPress(MVP transformationSpace, FloatPoint2D pressLocation) {
        float[] modelSpace = transformationSpace.peekCopyM();
        boolean result;
        
        // Transform into panel space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, (float) position.getX(),
                (float) position.getY(), 0.0f);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, 1 / device.getAspectRatio(), 1.0f, 1.0f);
        
        // Transform into background space
        if (isMirrored) {
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, 0.5f - contentWidth / 2.0f, 0, 0);
        } else {
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, -0.5f + contentWidth / 2.0f, 0, 0);
        }
        
        // Transform into content space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, (float) contentPos.getX(), 
                    (float) contentPos.getY(), 0.0f);
        
        // Check if the extending content is clicked
        transformationSpace.pushM(modelSpace);
        result = handleLongPressExt(transformationSpace, pressLocation);
        transformationSpace.popM();
        
        return result;
    }
    
    /** Interface for extending classes to handle long press events. */
    protected boolean handleLongPressExt(MVP transformationSpace, FloatPoint2D pressLocation) {
        return false;
    }

    /** {@inheritDocs} */
    @Override
    public boolean handlePickUp(MVP transformationSpace, FloatPoint2D touchLocation) {
        float[] modelSpace = transformationSpace.peekCopyM();
        float[] transformationMatrix;
        
        // Transform into panel space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, (float) position.getX(), 
                (float) position.getY(), 0.0f);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, 1.0f / device.getAspectRatio(),
                1.0f, 1.0f);
        
        // Transform into background space
        if (isMirrored) {
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, 0.5f - contentWidth / 2.0f, 0, 0);
        } else {
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, -0.5f + contentWidth / 2.0f, 0, 0);
        }
        transformationSpace.pushM(modelSpace);
        
        // Transform into border space
        if (isMirrored) {
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, -0.5f * contentWidth, 0, 0);
        } else {
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, 0.5f * contentWidth, 0, 0);
        }
        
        // Transform into tab space
        if (isMirrored) {
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, 
                    -0.5f * (float) (tabSize.getX() - borderWidth), 0, 0);
        } else {
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, 
                    0.5f * (float) (tabSize.getX() - borderWidth), 0, 0);
        }
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, (float) tabSize.getX(),
                (float) tabSize.getY(), 1);
        transformationMatrix = transformationSpace.collapseM(modelSpace);

        // If the tab is picked up
        if (InputHelper.isTouched(transformationMatrix, touchLocation)) {
            isBeingDragged = true; // Start dragging the panel
            transformationSpace.popM();
            
            return true;
        }
        
        //  Transform into content space
        modelSpace = transformationSpace.popM();
        
        // Check if any of the contents are being dragged
        transformationSpace.pushM(modelSpace);
        if (handlePickUpExt(transformationSpace, touchLocation)) {
            transformationSpace.popM();
            isContentBeingDragged = true;
            return true;
        }
        transformationSpace.popM();

        // Check if the content is being scrolled
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, contentWidth, 1.0f, 1.0f);
        transformationMatrix = transformationSpace.collapseM(modelSpace);
        logger.debug("Content Transformation Matrix: " + Arrays.toString(transformationMatrix));
        logger.debug("Touch Location: " + touchLocation);
        
        float        height   = transformationMatrix[0];
        FloatPoint2D position = new FloatPoint2D(transformationMatrix[12], 
                                                 transformationMatrix[13]);
        float        width    = transformationMatrix[5];
        logger.debug("Size:     " + width + ", " + height);
        logger.debug("Position: " + position);
        if (InputHelper.isTouched(transformationMatrix, touchLocation)) {
            logger.debug("Content is being scrolled!");
            isBeingScrolled = true;
            return true;
        }
        
        return false;
    }
    
    /** Provides an interface for extending classes to handle picking up the content. */
    protected boolean handlePickUpExt(MVP transformationSpace, FloatPoint2D touchLocation) {
        return false;
    }

    /** {@inheritDocs} */
    @Override
    public boolean handleDrag(FloatPoint2D moveVector) {
        if (isBeingDragged) { // If the panel itself is being dragged
            position = position.plusX(moveVector.getX());
            
            if (isMirrored) { // If the panel is mirrored
                position.setX(Math.min(position.getX(), xPosRetracted));
                position.setX(Math.max(position.getX(), xPosExpanded));
            } else { // If the panel is not mirrored
                position.setX(Math.max(position.getX(), xPosRetracted));
                position.setX(Math.min(position.getX(), xPosExpanded));
            }
            
            return true;
        } else if (isBeingScrolled) { // If the panel is being scrolled
            float contentHeight = getContentHeight();
            
            if (contentHeight > 1.0f) { // If there is more content than can be displayed
                contentPos = contentPos.plusY(moveVector.getY());
                contentPos.setY(Math.max(contentPos.getY(), 0));
                contentPos.setY(Math.min(contentPos.getY(), contentHeight - 1));
            } else {
                if (isMirrored) { // If the panel is right aligned
                    contentPos = new Point2D(0.0f, 0.0f);
                } else { // If the panel is left aligned
                    contentPos = new Point2D(0.0f, 0.0f);
                }
            }
            
            return true;
        } else if (isContentBeingDragged) { // If the panel contents are being dragged
            // Transform into content space
            moveVector = moveVector.scaleXBy(device.getAspectRatio());
            
            return handleDragExt(moveVector);
        }
        
        return true;
    }
    
    /** Provides an interface for extending classes to handle contents being dragged. */
    protected boolean handleDragExt(FloatPoint2D moveVector) {
        return true;
    }

    /** {@inheritDocs} */
    @Override
    public boolean handleDrop(FloatPoint2D dropLocation) {
        // If the panel itself is being dragged and is mirrored
        if (isBeingDragged && isMirrored) { 
            float movementRange = xPosRetracted - xPosExpanded;
            
            // If the panel is closer to being retracted
            if (position.getX() > xPosRetracted - movementRange / 2.0f) {
                position.setX(xPosRetracted); // Retract the panel
            } else { // If the panel is closed to being expanded
                position.setX(xPosExpanded); // Expand the panel
            }
            
            isBeingDragged = false; // Mark the panel as no longer being dragged
            
            return true;
        } 
        // If the panel itself is being dragged and is not mirrored
        else if (isBeingDragged && !isMirrored) {
            float movementRange = xPosExpanded - xPosRetracted;

            if (position.getX() < xPosRetracted + movementRange / 2.0f) {
                position.setX(xPosRetracted);
            } else {
                position.setX(xPosExpanded);
            }
            
            isBeingDragged = false; // Mark the panel as no longer being dragged
            
            return true;
        } else if (isBeingScrolled) { // If the panel contents are being scrolled
            isBeingScrolled = false;
            return true;
        } else if (isContentBeingDragged) { // If the panel contents are being dragged
            //TODO: Transform into content space. Not needed for now
            isContentBeingDragged = false;
            return handleDropExt(dropLocation);
        }
        
        return true;
    }
    
    /** Provides an interface for extending classes to handle contents being dropped. */
    protected boolean handleDropExt(FloatPoint2D dropLocation) {
        return true;
    }

    /** {@inheritDocs} */
    @Override
    public void render(MVP mvp) {
        float[] modelSpace = mvp.peekCopyM();

        // Transform into panel space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, (float) position.getX(),
                (float) position.getY(), 0.0f);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, 1 / device.getAspectRatio(), 1.0f, 1.0f);
        
        // Transform into background space
        if (isMirrored) {
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, 0.5f - contentWidth / 2.0f, 0, 0);
        } else {
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, -0.5f + contentWidth / 2.0f, 0, 0);
        }
        mvp.pushM(modelSpace);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, contentWidth, 1, 1);
        
        // Render the background of the panel
        colorShader.activate();
        colorShader.setMVPMatrix(mvp.collapseM(modelSpace));
        colorShader.setVBO(backgroundVBO);
        colorShader.draw();
        
        // Move into border space
        modelSpace = mvp.peekCopyM();
        if (isMirrored) {
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, -0.5f * contentWidth, 0, 0);
            Matrix.rotateM(modelSpace, Constants.NO_OFFSET, 180.0f, 0, 1, 0);
        } else {
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, 0.5f * contentWidth, 0, 0);
        }
        mvp.pushM(modelSpace);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, borderWidth, 1, 1);
        
        // Render the top portion of the panel border
        mvp.pushM(modelSpace);
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, 0,
                0.5f - (float) tabSize.getY() / 2, 0);
        textureShader.activate();
        textureShader.setMVPMatrix(mvp.collapseM(modelSpace));
        textureShader.setTexture(borderTexture.getHandle());
        textureShader.draw();
        
        // Render the bottom portion of the panel border
        modelSpace = mvp.popM();
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, 0,
                -0.5f + (float) tabSize.getY() / 2, 0);
        textureShader.setMVPMatrix(mvp.collapseM(modelSpace));
        textureShader.setTexture(borderTexture.getHandle());
        textureShader.draw();
        
        // Move into tab space
        modelSpace = mvp.popM();
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, 
                0.5f * (float) (tabSize.getX() - borderWidth), 0, 0);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, (float) tabSize.getX(),
                (float) tabSize.getY(), 1);
        
        // Render the tab
        textureShader.setMVPMatrix(mvp.collapseM(modelSpace));
        textureShader.setTexture(tabTexture.getHandle());
        textureShader.draw();
        
        modelSpace = mvp.popM();
        if (position.getX() != xPosRetracted) { // If the panel is not completely retracted
            // Transform into content space
            Matrix.translateM(modelSpace, Constants.NO_OFFSET, (float) contentPos.getX(), 
                    (float) contentPos.getY(), 0.0f);

            // Render the contents of the panel
            mvp.pushM(modelSpace);
            renderExt(mvp, contentWidth * 0.9f);
            mvp.popM();
        }
    }
    
    /** Provides an interface for extending classes to render the panel contents. */
    protected void renderExt(MVP mvp, float contentWidth) { }
}
