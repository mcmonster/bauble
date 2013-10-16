package com.rogue.bauble.graphics;

import android.opengl.Matrix;
import static com.google.common.base.Preconditions.checkArgument;
import com.rogue.bauble.misc.Constants;
import java.util.Stack;

/**
 * Model-View-Projection matrices manager. This class manages 
 * the transformations applied to each of these matrices using 
 * stacks. Note: Model and View are treated as one entity
 * 
 * @author R. Matt McCann
 */
public class MVP {
    /** Transformation history of the model matrix. */
    private Stack<float[]> modelMatrices = new Stack<float[]>();
    
    /** Transformation history of the projection matrix. */
    private Stack<float[]> projectionMatrices = new Stack<float[]>();
    
    /** Constructor. */
    public MVP() {
        float[] identityMatrix = new float[Constants.MATRIX_SIZE];
        Matrix.setIdentityM(identityMatrix, Constants.NO_OFFSET);
        modelMatrices.push(identityMatrix);
        projectionMatrices.push(identityMatrix);
    }

    /**
     * Collapses the model, view, and projection matrices into a MVP
     * matrix for direct use with OpenGL.
     * 
     * @return Collapsed MVP matrix.
     */
    public float[] collapse() {
        return collapse(modelMatrices.peek(), projectionMatrices.peek());
    }
    
    /**
     * Collapses the model, view, and projection matrices into a MVP
     * matrix for direct use with OpenGL.
     * 
     * @param model Must not be null. Must have a length of 16.
     * @param projection Must not be null. Must have a length of 16.
     * 
     * @return Collapsed MVP matrix.
     */
    public float[] collapse(final float[] model,
                            final float[] projection) {
        // Verify the function arguments are valid
        checkArgument(model != null, "Model matrix must not be null!");
        checkArgument(model.length == Constants.MATRIX_SIZE,
                "Model matrix must have a length of 16, got %s", model.length);
        checkArgument(projection != null, "Projection matrix must not be null!");
        checkArgument(projection.length == Constants.MATRIX_SIZE,
                "Projection matrix must have a length of 16, got %s", projection.length);
        
        float[] mvp = new float[Constants.MATRIX_SIZE];
       
        // Factor in the model-view and projection matrices
        Matrix.multiplyMM(mvp, 0, projection, 0, model, 0);
        
        return mvp;
    }
    
    /**
     * Collapses the provided model matrix with the projection and view matrices
     * on top of their respective stacks.
     * 
     * @param modelMatrix Must not be null. Must have a length of 16.
     * @return Collapsed MVP matrix.
     */
    public float[] collapseM(final float[] modelMatrix) {
        return collapse(modelMatrix, projectionMatrices.peek());
    }
    
    /**
     * Removes the matrix on the top of the model-view stack.
     * 
     * @return The matrix on the top of the model-view stack.
     */
    public float[] popM() { return modelMatrices.pop(); }
    
    /**
     * Removes the matrix on top of the projection stack.
     * 
     * @return The matrix on top of the projection stack. 
     */
    public float[] popP() { return projectionMatrices.pop(); }
    
    /**
     * Pushes a matrix onto the top of the model-view stack.
     * 
     * @param matrix Must not be null. Must have a length of 16 
     */
    public void pushM(float[] matrix) {
        checkArgument(matrix.length == Constants.MATRIX_SIZE, 
                "Matrix must have a length of 16, got %s", matrix.length);
        
        // Copy the matrix
        float[] copy = new float[Constants.MATRIX_SIZE];
        System.arraycopy(matrix, Constants.NO_OFFSET,
                         copy, Constants.NO_OFFSET, matrix.length);
        
        // Push the matrix
        modelMatrices.push(copy);
    }
    
    /**
     * Pushes a matrix onto the top of the projection stack.
     * 
     * @param matrix Must not be null. Must have a length of 16.
     */
    public void pushP(float[] matrix) {
        checkArgument(matrix.length == Constants.MATRIX_SIZE, 
                "Matrix must have a length of 16, got %s", matrix.length);
        
        // Copy the matrix
        float[] copy = new float[Constants.MATRIX_SIZE];
        System.arraycopy(matrix, Constants.NO_OFFSET,
                         copy, Constants.NO_OFFSET, matrix.length);
        
        // Push the matrix
        projectionMatrices.push(copy);
    }
    
    /**
     * Returns a copy of the matrix on top of the model-view stack.
     * 
     * @return Copy of the peeked matrix.
     */
    public float[] peekCopyM() {
        final float[] copy = new float[Constants.MATRIX_SIZE];    
        System.arraycopy(modelMatrices.peek(), Constants.NO_OFFSET, 
                         copy, Constants.NO_OFFSET, copy.length);
        return copy;
    }
    
    /**
     * Returns a copy of the matrix on top of the projection stack.
     * 
     * @return Copy of the peeked matrix.
     */
    public float[] peekCopyP() {
        final float[] copy = new float[Constants.MATRIX_SIZE];    
        System.arraycopy(projectionMatrices.peek(), Constants.NO_OFFSET, 
                         copy, Constants.NO_OFFSET, copy.length);
        return copy;
    }
    
    /**
     * Returns a reference to the matrix on top of the model-view stack.
     * 
     * @return Matrix on top of the stack.
     */
    public float[] peekM() {
        return modelMatrices.peek();
    }
    
    /**
     * Returns a reference to the matrix on top of the projection stack.
     * 
     * @return Matrix on top of the stack.
     */
    public float[] peekP() {
        return projectionMatrices.peek();
    }
}
