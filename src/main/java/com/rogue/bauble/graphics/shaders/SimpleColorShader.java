package com.rogue.bauble.graphics.shaders;

import android.content.Context;
import android.opengl.GLES20;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.rogue.bauble.misc.Constants;
import javax.inject.Named;

/**
 * Shader program that draws the image defined by a VBO packed with vertex 
 * position and color data.
 * 
 * @author R. Matt McCann
 */
@Singleton
public class SimpleColorShader extends Shader {
    /** Attribute keys defined in the shader programs. */
    private static final String[] ATTRIBUTES = new String[] {"a_Position", "a_Color"};
    
    /** OpenGL reference to the shader program's color attribute. */
    private int colorHandle;
    
    /** Transformations applied to the image to be rendered. */
    private Optional<float[]> mvpMatrix = Optional.absent();
    
    /** OpenGL reference to the shader program's transformations attribute. */
    private int mvpMatrixHandle;
    
    /** Current opacity level of image. */
    private float opacity = 1.0f;
    
    /** Handle for the opacity property. */
    private int opacityHandle;
    
    /** OpenGL reference to the shader program's position attribute. */
    private int positionHandle;
    
    /** Packed data offset of the position data. */
    private static final int POSITION_OFFSET = 0;
    
    /** Size of the packed position data. */
    private static final int POSITION_DATA_SIZE = 3;
    
    /** Packed data offset of the color data. */
    private static final int COLOR_OFFSET = POSITION_DATA_SIZE * Constants.BYTES_PER_FLOAT;
    
    /** Size of the packed color data. */
    private static final int COLOR_DATA_SIZE = 4;
    
    /** Size of the complete packed vertex data. */
    private static final int STRIDE = (POSITION_DATA_SIZE + COLOR_DATA_SIZE) * Constants.BYTES_PER_FLOAT;
    
    /** OpenGL reference to a vertex buffer object loaded with rendering details. */
    private Optional<Integer> vbo = Optional.absent();
    
    /** Guice injectable constructor. */
    @Inject
    public SimpleColorShader(final Context context,
                             @Named("SimpleColorFragmentShader") int fragmentShader,
                             final ShaderRegistry registry,
                             @Named("SimpleColorVertexShader") int vertexShader) {
        super(context, registry, vertexShader, fragmentShader, ATTRIBUTES);
    }

    /** {@inheritDocs} */
    @Override
    protected void getAttributeHandles() {
        final int programHandle = getProgramHandle();
        
        // Get the uniforms
        mvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        opacityHandle = GLES20.glGetUniformLocation(programHandle, "u_Opacity");

        // Get the attributes
        positionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        colorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");
    }
    
    /** {@inheritDocs} */
    @Override
    public void draw(final int drawMode, final int numVertices) {
        // Check that all the required parameters have been set
        checkState(mvpMatrix.isPresent(), "You must call setMVPMatrix() before drawing!");
        checkState(vbo.isPresent(), "You must call setVBO() before drawing!");
        
        // Pass in the MVP Matrix
        final int matrixCount = 1;
        final boolean willTranspose = false;
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, matrixCount, willTranspose, 
                mvpMatrix.get(), Constants.NO_OFFSET);
                
        // Pass in the opacity
        GLES20.glUniform1f(opacityHandle, opacity);
        
        // Pass in the positions
        final boolean willNormalize = false;
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo.get());
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, 
                willNormalize, STRIDE, POSITION_OFFSET);
        
        // Pass in the colors
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo.get());
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT,
                willNormalize, STRIDE, COLOR_OFFSET);
        
        GLES20.glDrawArrays(drawMode, Constants.NO_OFFSET, numVertices);
        
        // Clean up
        mvpMatrix = Optional.absent();
        opacity = 1.0f;
        vbo = Optional.absent();
    }
    
    public void setMVPMatrix(final float[] mvpMatrix) {
        this.mvpMatrix = Optional.of(mvpMatrix);
    }

    public void setOpacity(float opacity) {
        checkArgument((0.0f <= opacity) && (opacity <= 1.0f), "Expected 0 <= "
                + "opacity <= 1.0, got %s", opacity);
        
        this.opacity = opacity;
    }
    
    public void setVBO(final int vbo) {
        this.vbo = Optional.of(vbo);
    }
}
