package com.rogue.bauble.graphics.shader;

import android.content.Context;
import com.rogue.bauble.graphics.shaders.ShaderRegistry;
import com.rogue.bauble.graphics.shaders.SimpleColorShader;
import org.mockito.Mockito;

/**
 * Test implementation of Shader class.
 * 
 * @author R. Matt McCann
 */
public class TestSimpleColorShader extends SimpleColorShader {
    public TestSimpleColorShader(ShaderRegistry registry) {
        super(Mockito.mock(Context.class), 0, registry, 0);
    }
}