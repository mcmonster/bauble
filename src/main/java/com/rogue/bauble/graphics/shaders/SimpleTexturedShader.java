package com.rogue.bauble.graphics.shaders;

import com.rogue.bauble.graphics.DrawUtils;
import android.content.Context;
import android.opengl.GLES20;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.rogue.bauble.misc.Constants;
import javax.inject.Named;

/**
 * Shader program that renders the provided texture.
 * 
 * @author R. Matt McCann
 */
@Singleton
public class SimpleTexturedShader extends Shader {
    /** Attribute keys defined in the shader programs. */
    private static final String[] ATTRIBUTES = new String[] {"a_Position", "a_TexCoord"};
    
    /** Transformations applied to the image to be rendered. */
    private Optional<float[]> mvpMatrix = Optional.absent();
    
    /** OpenGL reference to the shader program's transformations attribute. */
    private int mvpMatrixHandle;
    
    /** Size of the packed position data. */
    private static final int POSITION_DATA_SIZE = 3;
    
    /** OpenGL reference to the shader program's position attribute. */
    private int positionHandle;
    
    /** OpenGL reference to the shader program's texture coordinate attribute. */
    private int texCoordHandle;
    
    /** Packed data offset of the texture coordinate data. */
    private static final int TEX_COORD_OFFSET = POSITION_DATA_SIZE * Constants.BYTES_PER_FLOAT;
    
    /** Size of the packed texture coordinate data. */
    private static final int TEX_COORD_DATA_SIZE = 2;
    
    /** OpenGL reference to the loaded texture to be drawn. */
    private Optional<Integer> texture = Optional.absent();
    
    /** OpenGL reference to the shader program's texture attribute. */
    private int textureHandle;
    
    /** Size of the complete packed vertex data. */
    private static final int STRIDE = (POSITION_DATA_SIZE + TEX_COORD_DATA_SIZE) * Constants.BYTES_PER_FLOAT;
    
    /** OpenGL reference to a vertex buffer object loaded with rendering details. */
    private Optional<Integer> vbo = Optional.<Integer>absent();
    
    /** Current opacity level of image. */
    private float opacity = 1.0f;
    
    /** Handle for the opacity property. */
    private int opacityHandle;
    
    /** Guice injectable constructor. */
    @Inject
    public SimpleTexturedShader(final Context context,
                                @Named("SimpleTexturedFragmentShader") int fragmentShader,
                                final ShaderRegistry registry,
                                @Named("SimpleTexturedVertexShader") int vertexShader) {
        super(context, registry, vertexShader, fragmentShader, ATTRIBUTES);
    }
    
    /** {@inheritDocs} */
    @Override
    protected void getAttributeHandles() {
        final int programHandle = getProgramHandle();
        
        // Get the uniforms
        mvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        opacityHandle = GLES20.glGetUniformLocation(programHandle, "u_Opacity");
        textureHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture");
        
        // Get the attributes
        positionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        texCoordHandle = GLES20.glGetAttribLocation(programHandle, "a_TexCoord");
    }

    /** {@inheritDocs} */
    @Override
    public void draw(final int drawMode, final int numVertices) {
        // Check that all the rquired parameters have been set
        checkState(mvpMatrix.isPresent(), "You must call setMVPMatrix() before drawing!");
        checkState(texture.isPresent(), "You must call setTexture() before drawing!");
        
        final int myVbo;
        if (vbo.isPresent()) {
            myVbo = vbo.get();
        } else {
            myVbo = DrawUtils.getUnitSquarePtVbo();
        }
        
        // Pass in the MVP matrix
        final int matrixCount = 1;
        final boolean willTranspose = false;
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, matrixCount, willTranspose, 
                mvpMatrix.get(), Constants.NO_OFFSET);
                
        // Pass in the opacity
        GLES20.glUniform1f(opacityHandle, opacity);
        
        // Pass in the texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.get());
        final int activeTexture = 0;
        GLES20.glUniform1i(textureHandle, activeTexture);
        
        // Pass in the positions
        final boolean willNormalize = false;
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, myVbo);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, 
                willNormalize, STRIDE, Constants.NO_OFFSET);
        
        // Pass in the texture coordinates
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, myVbo);
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, TEX_COORD_DATA_SIZE, GLES20.GL_FLOAT, 
                willNormalize, STRIDE, TEX_COORD_OFFSET);
        
        GLES20.glDrawArrays(drawMode, Constants.NO_OFFSET, numVertices);
        
        // Clean up
        mvpMatrix = Optional.absent();
        opacity = 1.0f;
        texture = Optional.absent();
        vbo = Optional.<Integer>absent();
    }

    public void setMVPMatrix(final float[] matrix) {
        mvpMatrix = Optional.of(matrix);
    }
    
    public void setOpacity(float opacity) {
        checkArgument((0.0f <= opacity) && (opacity <= 1.0f), "Expected 0 <= "
                + "opacity <= 1.0, got %s", opacity);
        
        this.opacity = opacity;
    }

    public void setTexture(final int texture) {
        this.texture = Optional.of(texture);
    }
    
    public void setVBO(final int vbo) { this.vbo = Optional.<Integer>of(vbo); }
}