package com.rogue.bauble.graphics.shaders;

import android.opengl.GLES20;
import com.google.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Keeps a reference to all of the known shaders for ensuring that only one is active at a time.
 * 
 * @author R. Matt McCann
 */
@Singleton
public class ShaderRegistry {
    private final Map<Shader, Boolean> shaders = new HashMap<Shader, Boolean>();
    
    public void activate(final Shader shader) {
        if (!shaders.get(shader)) {
            GLES20.glUseProgram(shader.getProgramHandle());
            
            for (Shader key : shaders.keySet()) {
                shaders.put(key, false);
            }
            
            shaders.put(shader, true);
        }
    }
    
    public void register(final Shader shader) {
        shaders.put(shader, false);
    }
}
