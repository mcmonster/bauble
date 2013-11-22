package com.rogue.bauble.graphics.shader;

import android.content.Context;
import com.rogue.bauble.graphics.shaders.ShaderRegistry;
import com.rogue.bauble.graphics.shaders.SimpleTexturedShader;
import org.mockito.Mockito;

/**
 * Test implementation of SimpleTexturedShader.
 * 
 * @author R. Matt McCann
 */
public class TestSimpleTexturedShader extends SimpleTexturedShader {
    public TestSimpleTexturedShader() {
        super(Mockito.mock(Context.class), 0, new TestShaderRegistry(), 0);
    }
    
    public TestSimpleTexturedShader(ShaderRegistry registry) {
        super(Mockito.mock(Context.class), 0, registry, 0);
    }
}
