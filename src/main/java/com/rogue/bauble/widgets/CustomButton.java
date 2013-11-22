package com.rogue.bauble.widgets;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.rogue.bauble.device.Device;
import com.rogue.bauble.graphics.shaders.SimpleTexturedShader;
import com.rogue.bauble.graphics.text.GlyphString.GlyphStringFactory;
import com.rogue.bauble.graphics.textures.Texture;

/**
 * Button that allows the instantiator to specify the background texture.
 *  
 * @author R. Matt McCann
 */
public class CustomButton extends Button {
    /** Guice injectable constructor. */
    @Inject
    public CustomButton(@Assisted("background") Texture background,
                        Device device,
                        @Assisted("function") Function<Void, Boolean> function,
                        GlyphStringFactory glyphFactory,
                        SimpleTexturedShader shader) {
        super(background, device, function, glyphFactory, shader);
    }
    
    public interface CustomButtonFactory {
        CustomButton create(@Assisted("background") Texture background,
                            @Assisted("function") Function<Void, Boolean> function);
    }
}
