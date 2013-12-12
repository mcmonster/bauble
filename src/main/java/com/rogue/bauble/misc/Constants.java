package com.rogue.bauble.misc;

/**
 * A collection of constants definition.
 * 
 * @author R. Matt McCann
 */
public abstract class Constants {
    /** Number of bytes in a float value. */
    public static final int BYTES_PER_FLOAT = 4;
    
    /** Number of cells in a rendering matrix. */
    public static final int MATRIX_SIZE = 16;
    
    /** No offset into a array. */
    public static final int NO_OFFSET = 0;
    
    /** Number of vertices required to render a square. */
    public static final int NUM_VERTICES_PER_SQUARE = 6;
    
    /** Conversion factor for converting from radians to degrees. */
    public static final float RADIANS_TO_DEGREES = 57.2957795f;
}
