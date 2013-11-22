package com.rogue.bauble.graphics.text;

import android.opengl.GLES20;
import android.opengl.Matrix;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.inject.Inject;
import com.rogue.bauble.ProxyView;
import com.rogue.bauble.graphics.MVP;
import com.rogue.bauble.graphics.shaders.SimpleTexturedShader;
import com.rogue.bauble.graphics.textures.TextureFactory;
import com.rogue.bauble.misc.Constants;

/**
 * Glyph string implemented using generated bitmaps.
 * 
 * @author R. Matt McCann
 */
public class BitmapGlyphString extends GlyphString {
    /** Dimensions of the texture. */
    private final float[] aspectRatio = new float[1];
    
    /** Handle of the raw texture. */
    private int rawTextureHandle = -1;
    
    /** Used to draw the string. */
    private final SimpleTexturedShader shader;
    
    private static final String TAG = "BitmapGlyphString";
    
    /** Used to create new bitmaps. */
    private final TextureFactory textureFactory;
    
    /** Used to retrieve the context for deleting textures. */
    private final ProxyView view;
    
    /** Guice injectable constructor. */
    @Inject
    public BitmapGlyphString(SimpleTexturedShader shader,
                             TextureFactory textureFactory,
                             ProxyView view) {
        this.shader = checkNotNull(shader);
        this.textureFactory = checkNotNull(textureFactory);
        this.view = checkNotNull(view);
    }
    
    /** {@inheritDocs} */
    @Override
    public void delete() {
        if (rawTextureHandle != -1) { // If a texture has already been loaded
            view.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int numTextures = 1;
                    GLES20.glDeleteTextures(numTextures, new int[] {rawTextureHandle}, Constants.NO_OFFSET);
                    rawTextureHandle = -1;
                }
            });
        }
    }
    
    /** {@inheritDocs} */
    @Override
    public float getWidth() {
        return getHeight() * aspectRatio[0];
    }
    
    /** {@inheritDocs} */
    @Override
    public synchronized void render(MVP mvp) {
        if (rawTextureHandle != -1) { // If the texture has been loaded
            shader.activate();
            
            float[] model = mvp.peekCopyM();
            Matrix.translateM(model, Constants.NO_OFFSET, (float) getPosition().getX(), (float) getPosition().getY(), 0.0f);
            Matrix.scaleM(model, Constants.NO_OFFSET, getHeight() * aspectRatio[0], getHeight(), 1.0f);
            shader.setMVPMatrix(mvp.collapseM(model));
            shader.setOpacity(getOpacity());
            shader.setTexture(rawTextureHandle);
            shader.draw();
        }
    }

    /** {@inheritDocs} */
    @Override
    protected synchronized void updateRendering() {
        final String text = getText();
        
        view.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (rawTextureHandle != -1) { // If a texture has already been loaded
                    int numTextures = 1;
                    GLES20.glDeleteTextures(numTextures, new int[] {rawTextureHandle}, Constants.NO_OFFSET);
                }
                
                rawTextureHandle = textureFactory.texturizeText(text, getColor(),
                        getAlignment(), 60.0f, aspectRatio);
            }
        });
    }
}
