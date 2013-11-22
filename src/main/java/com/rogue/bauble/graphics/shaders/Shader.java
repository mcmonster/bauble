package com.rogue.bauble.graphics.shaders;

import android.content.Context;
import android.opengl.GLES20;
import com.rogue.bauble.misc.Constants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Helper class that compiles and tracks shader programs.
 * 
 * @author R. Matt McCann
 */
public abstract class Shader {
    /** Attribute keys defined in the shader program. */
    private String[] attributes;
    
    /** Current context of the application. */
    private final Context context;
    
    /** Resource id of the fragment shader program. */
    private final int fragmentShaderSourceID;
    
    /** OpenGL id of the loaded and linked shader program. */
    private int programHandle;
    
    /** Used to determine which shader is currently active 
        to prevent repetitive activations. */
    private final ShaderRegistry registry;
    
    /** Resource id of the vertex shader program. */
    private final int vertexShaderSourceID;
    
    /**
     * Compiles and links the program.
     * 
     * @param vertexSource Android resource id of raw vertex source
     * @param fragmentSource Android resource id of raw fragment source
     * @param attributes Attributes of the program.
     */
    protected Shader(final Context context,
                     final ShaderRegistry registry,
                     final int vertexShaderSourceID,
                     final int fragmentShaderSourceID,
                     final String[] attributes) {
        this.attributes = attributes;
        this.context = context;
        this.fragmentShaderSourceID = fragmentShaderSourceID;
        this.registry = registry;
        this.vertexShaderSourceID = vertexShaderSourceID;
    }
    
    /**
     * Activates this shader.
     */
    public final void activate() {
        registry.activate(this);
    }
    
    /**
     * Helper function to compile and link a program.
     * 
     * @param vertexShaderHandle 
     * OpenGL handle to an already-compiled vertex shader
     * @param fragmentShaderHandle 
     * OpenGL handle to an already-compiled fragment shader
     * @param attributes
     * Attributes that need to be bound to the program
     * @return
     * OpenGL handle to the program
     */
    private int compileAndLinkProgram(final int vertexShaderHandle,
                                      final int fragmentShaderHandle,
                                      final String[] attributes) {
        int myProgramHandle = GLES20.glCreateProgram();
        
        if (myProgramHandle != 0) {
            // Bind the vertex shader to the program
            GLES20.glAttachShader(myProgramHandle, vertexShaderHandle);
            
            // Bind the fragment shader to the program
            GLES20.glAttachShader(myProgramHandle, fragmentShaderHandle);
            
            // Bind attributes
            if (attributes != null) {
                for (int iter = 0; iter < attributes.length; iter++) {
                    GLES20.glBindAttribLocation(myProgramHandle, iter, attributes[iter]);
                }
            }
            
            // Link the two shaders together into a program
            GLES20.glLinkProgram(myProgramHandle);
            
            // Check the link status
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(myProgramHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                throw new RuntimeException("Failed to link shader programs: " + GLES20.glGetProgramInfoLog(myProgramHandle));
            }
        } else {
            throw new RuntimeException("Failed to compile and link shader programs!");
        }
        
        return myProgramHandle;    
    }

    /**
     * Compiles a provider shader source and returns the compiled program's handle.
     * 
     * @param shaderType Fragment or vertex shader
     * @param shaderSource Source code for the program
     * @return Compiled program's handle
     */
    private int compileShader(final int shaderType, final String shaderSource) {
        int shaderHandle = GLES20.glCreateShader(shaderType);
        
        if (shaderHandle != 0) {
            // Pass in the shader source
            GLES20.glShaderSource(shaderHandle, shaderSource);
            
            // Compile the shader
            GLES20.glCompileShader(shaderHandle);
            
            // Check the compilation status
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                throw new RuntimeException("Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
            }
        } else {
            throw new RuntimeException("Error compiling shader! Source: " + shaderSource);
        }
        
        return shaderHandle;
    }
    
    /** Retrieve the attributes for the program. */
    protected abstract void getAttributeHandles(); 
    
    protected final int getProgramHandle() {
        return programHandle;
    }
    
    /**
     * Reads in the source code from the specified raw file.
     * 
     * @param resourceID Android resource ID of the source code
     * @return The source code
     */
    private String getSourceFromRawFile(final int resourceID) {
        String nextLine;
        final StringBuilder body = new StringBuilder();
        
        // Retrieve the resource stream
        final BufferedReader in = new BufferedReader(new InputStreamReader(
                context.getResources().openRawResource(resourceID)));
        
        // Parse the source file
        try {
            while ((nextLine = in.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to parse source file!");
        }
        
        return body.toString();
    }
    
    /**
     * Returns a handle to the requested program.
     */
    public void init() {
        registry.register(this);
        
        // Get the source
        final String vertexShaderSource = getSourceFromRawFile(vertexShaderSourceID);
        final String fragmentShaderSource = getSourceFromRawFile(fragmentShaderSourceID);
        
        // Compile the source
        final int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, 
                vertexShaderSource);
        final int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderSource);
        
        // Link the program and store it
        programHandle = compileAndLinkProgram(vertexShaderHandle, 
                fragmentShaderHandle, attributes);
        
        // Get the attribute handles
        getAttributeHandles();
    }
    
    /**
     * Draws the loaded settings.
     */
    public abstract void draw(final int drawMode, final int numVertices);
    
    public void draw() {
        draw(GLES20.GL_TRIANGLES, Constants.NUM_VERTICES_PER_SQUARE);
    }
        
    protected final void setAttributes(final String[] attributes) {
        this.attributes = attributes;
    }
}