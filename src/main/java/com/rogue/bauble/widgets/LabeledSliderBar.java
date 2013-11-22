package com.rogue.bauble.widgets;

import android.graphics.Paint;
import android.opengl.Matrix;
import static com.google.common.base.Preconditions.*;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.rogue.bauble.device.Device;
import com.rogue.bauble.graphics.MVP;
import com.rogue.bauble.graphics.shaders.SimpleTexturedShader;
import com.rogue.bauble.graphics.text.GlyphString;
import com.rogue.bauble.graphics.text.GlyphString.GlyphStringFactory;
import com.rogue.bauble.graphics.textures.Texture;
import com.rogue.bauble.io.touch.ClickHandler;
import com.rogue.bauble.io.touch.DragHandler;
import com.rogue.bauble.io.touch.InputHelper;
import com.rogue.bauble.misc.Constants;
import com.rogue.bauble.properties.Renderable;
import com.rogue.unipoint.FloatPoint2D;
import com.rogue.unipoint.Point2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A slider bar with a label for the value type and a label for its current
 * setting.
 * 
 * @author R. Matt McCann
 */
public class LabeledSliderBar implements ClickHandler, DragHandler, Renderable {
    /** Texture of the slider bar's bar. */
    private final Texture bar;
    
    /** Size of the clickable minus/plus buttons. */
    private final float buttonSize = 0.1f;
    
    /** Current value of the slider bar. */
    private float currentValue;
    
    /** Used to adjust for the screen's aspect ratio. */
    private final Device device;
    
    /** Interface for logging events. */
    private static final Logger logger = LoggerFactory.getLogger("LabeledSliderBar");
    
    /** Maximum value of the slider bar. */
    private final float maxValue;
    
    /** Texture of the minus button. */
    private final Texture minusTexture;
    
    /** Minimum value of the slider bar. */
    private final float minValue;
    
    /** Texture of the plus button. */
    private final Texture plusTexture;
    
    /** Rendering position of the slider bar. */
    private Point2D position = new Point2D(0.0f, 0.0f);
    
    /** Used to draw the slider bar. */
    private final SimpleTexturedShader shader;
    
    /** Rendering size of the slider bar. */
    private float size = 1.0f;
    
    /** Slider icon texture. */
    private final Texture slider;
    
    /** Y offset of the slider icon. */
    private float sliderPosition = 0.0f;
    
    /** Label displaying the type of value handled by the slider bar. */
    private final GlyphString typeLabel;
    
    /** Anonymous sub-class providing the new value handling functionality. */
    private final UpdatedValueHandler updatedValueHandler;
    
    /** Label displaying the current value of the slider bar. */
    private final GlyphString valueLabel;
    
    /** Guice injectable constructor. */
    @Inject
    public LabeledSliderBar(@Named("SliderBar") Texture bar,
                            Device device,
                            GlyphStringFactory glyphFactory,
                            @Assisted("maxValue") float maxValue,
                            @Named("SliderMinusButton") Texture minusTexture,
                            @Assisted("minValue") float minValue,
                            @Named("SliderPlusButton") Texture plusTexture,
                            SimpleTexturedShader shader,
                            @Named("Slider") Texture slider,
                            @Assisted("typeLabel") String typeLabel,
                            @Assisted("updatedValueHandler") UpdatedValueHandler updatedValueHandler) {
        checkArgument(minValue < maxValue, "Expected minValue < maxValue but got "
                + "minValue = %s, maxValue = %s", minValue, maxValue);
        
        this.currentValue = minValue;
        this.bar = checkNotNull(bar);
        this.device = checkNotNull(device);
        this.maxValue = maxValue;
        this.minusTexture = checkNotNull(minusTexture);
        this.minValue = minValue;
        this.plusTexture = checkNotNull(plusTexture);
        this.shader = checkNotNull(shader);
        this.slider = checkNotNull(slider);
        this.updatedValueHandler = checkNotNull(updatedValueHandler);
        
        this.typeLabel = glyphFactory.create();
        this.typeLabel.setHeight(0.035f);
        this.typeLabel.setAlignment(Paint.Align.LEFT);
        this.typeLabel.setText(typeLabel);
        
        this.valueLabel = glyphFactory.create();
        this.valueLabel.setHeight(0.035f);
        this.valueLabel.setAlignment(Paint.Align.RIGHT);
    }
    
    public interface LabeledSliderBarFactory {
        LabeledSliderBar create(@Assisted("maxValue") float maxValue,
                                @Assisted("minValue") float minValue,
                                @Assisted("typeLabel") String typeLabel,
                                @Assisted("updatedValueHandler") UpdatedValueHandler updatedValueHandler);
    }
    
    /** {@inheritDocs} */
    @Override
    public boolean handleClick(MVP transformationSpace, FloatPoint2D clickLocation) {
        float[] modelSpace = transformationSpace.peekCopyM();
        float[] transformationMatrix;
        
        // Move into labeled slider bar space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, (float) position.getX(),
                (float) position.getY() - 0.6125f * typeLabel.getHeight(), 0.0f);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, size, size, 1.0f);
        transformationSpace.pushM(modelSpace);
      
        // Move into minus button space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, -0.5f + buttonSize / 2, 0, 0);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, buttonSize, buttonSize, 1);
        transformationMatrix = transformationSpace.collapseM(modelSpace);

        // If the minus button is clicked
        if (InputHelper.isTouched(transformationMatrix, clickLocation)) {
            currentValue = updatedValueHandler.handleDecreasedValue(currentValue, valueLabel);
            updateSliderPosition();
            transformationSpace.popM();
            return true;
        }
        
        // Move into plus button space
        modelSpace = transformationSpace.popM();
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, 0.5f - buttonSize / 2, 0, 0);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, buttonSize, buttonSize, 1);
        transformationMatrix = transformationSpace.collapseM(modelSpace);
        
        // If the plus button is clicked
        if (InputHelper.isTouched(transformationMatrix, clickLocation)) {
            currentValue = updatedValueHandler.handleIncreasedValue(currentValue, valueLabel);
            updateSliderPosition();
            return true;
        }
        
        return false;
    }

    /** {@inheritDocs} */
    @Override
    public boolean handlePickUp(MVP transformationSpace, FloatPoint2D touchLocation) {
        float[] modelSpace = transformationSpace.peekCopyM();
        float[] transformationMatrix;

        // Move into labeled slider bar space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, (float) position.getX(),
                (float) position.getY() - 0.6125f * typeLabel.getHeight(), 0.0f);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, size, size, 1.0f);
        
        // Move into slider space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, sliderPosition, 0, 0);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, buttonSize, buttonSize, 1);
        transformationMatrix = transformationSpace.collapseM(modelSpace);
        
        // Check if the slider is being picked up
        return InputHelper.isTouched(transformationMatrix, touchLocation);
    }

    /** {@inheritDocs} */
    @Override
    public boolean handleDrag(FloatPoint2D moveVector) {
        float range = maxValue - minValue;
        float renderWidth = 1 - 2.5f * buttonSize;
        
        moveVector = moveVector.scaleXBy(1 / size);
        sliderPosition = sliderPosition + moveVector.getX();
        
        if (sliderPosition < -renderWidth / 2) sliderPosition = -renderWidth / 2;
        if (sliderPosition > renderWidth / 2) sliderPosition = renderWidth / 2;
        
        currentValue = updatedValueHandler.handleDraggedValue(
                currentValue, minValue + (sliderPosition + renderWidth / 2) / renderWidth * range, valueLabel);
        
        return true;
    }

    /** {@inheritDocs} */
    @Override
    public boolean handleDrop(FloatPoint2D dropLocation) {  
        float range = maxValue - minValue;
        float renderWidth = 1 - 2.5f * buttonSize;
        
        currentValue = updatedValueHandler.handleDraggedValue(
                currentValue, minValue + (sliderPosition + renderWidth / 2) / renderWidth * range, valueLabel);
        updateSliderPosition();
        
        return true;
    }

    public float getCurrentValue() { return currentValue; }
    
    /** {@inheritDocs} */
    @Override
    public void render(MVP transformationSpace) {
        float[] modelSpace = transformationSpace.peekCopyM();
        
        // Move into labeled slider bar space
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, (float) position.getX(),
                (float) position.getY() - 0.6125f * typeLabel.getHeight(), 0.0f);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, size, size, 1.0f);
        transformationSpace.pushM(modelSpace);
        
        // Render the type label
        typeLabel.setPosition(new Point2D(-0.49f + typeLabel.getWidth() / 2, 
                                          typeLabel.getHeight() * .75f + buttonSize / 2));
        typeLabel.render(transformationSpace);
        
        // Render the value label
        valueLabel.setPosition(new Point2D(0.49f - valueLabel.getWidth() / 2, 
                                           typeLabel.getHeight() * .75f + buttonSize / 2));
        valueLabel.render(transformationSpace);
        
        // Move into slider bar space
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, 1 - buttonSize * 2, buttonSize / 5, 1);
        
        // Render the slider bar body
        shader.setMVPMatrix(transformationSpace.collapseM(modelSpace));
        shader.setTexture(bar.getHandle());
        shader.draw();
        
        // Move into minus button space
        modelSpace = transformationSpace.peekCopyM();
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, -0.5f + buttonSize / 2, 0, 0);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, buttonSize, buttonSize, 1);
        
        // Render the minus button
        shader.setMVPMatrix(transformationSpace.collapseM(modelSpace));
        shader.setTexture(minusTexture.getHandle());
        shader.draw();
        
        // Move into plus button space
        modelSpace = transformationSpace.peekCopyM();
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, 0.5f - buttonSize / 2, 0, 0);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, buttonSize, buttonSize, 1);
        
        // Render the minus button
        shader.setMVPMatrix(transformationSpace.collapseM(modelSpace));
        shader.setTexture(plusTexture.getHandle());
        shader.draw();
        
        // Move into slider space
        modelSpace = transformationSpace.popM();
        Matrix.translateM(modelSpace, Constants.NO_OFFSET, sliderPosition, 0, 0);
        Matrix.scaleM(modelSpace, Constants.NO_OFFSET, buttonSize, buttonSize, 1);
        
        // Render the slider
        shader.setMVPMatrix(transformationSpace.collapseM(modelSpace));
        shader.setTexture(slider.getHandle());
        shader.draw();
    }
    
    public void setCurrentValue(float currentValue) {
        checkArgument((minValue <= currentValue) && (currentValue <= maxValue),
                "Expected minValue <= currentValue <= maxValue, got currentValue = "
                + "%s, (minValue = %s, maxValue = %s)", currentValue, minValue,
                maxValue);
        
        this.currentValue = updatedValueHandler.setCurrentValue(currentValue,
                valueLabel);
        
        updateSliderPosition();
    }
    
    private void updateSliderPosition() {
        float value = (currentValue - minValue) / (maxValue - minValue);
        sliderPosition = -0.5f + 1.25f * buttonSize + (1 - 2.5f * buttonSize) * value;
    }
    
    public void setPosition(Point2D position) {
        this.position = checkNotNull(position);
    }
    
    
    public void setWidth(float width) {
        checkArgument(width != 0.0f, "Width must not be 0");
        
        this.size = width;
    }
    
    public static interface UpdatedValueHandler {
        float handleDecreasedValue(float currentValue, GlyphString valueLabel);
        float handleDraggedValue(float currentValue, float updatedValue, GlyphString valueLabel);
        float handleIncreasedValue(float currentValue, GlyphString valueLabel);
        float setCurrentValue(float newValue, GlyphString valueLabel);
    }
}
