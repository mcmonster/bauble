package com.rogue.bauble;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.rogue.bauble.device.SensorMeister;
import com.rogue.bauble.graphics.textures.Animation;
import com.rogue.bauble.graphics.textures.Texture;
import com.rogue.bauble.graphics.textures.TextureFactory;
import com.rogue.bauble.misc.Constants;
import java.util.HashMap;
import java.util.Map;

/**
 * Base Guice dependency module.
 * 
 * @author R. Matt McCann
 */
public abstract class BaseModule extends AbstractModule {
    /** References to all animations used in the module. */
    private final Map<String, Animation> animations = new HashMap<String, Animation>();
    
    /** Can be retrieved for delegated rendering purposes. */
    private final ProxyRenderer renderer;

    /** Used to load textures into the OpenGL context. */
    private final TextureFactory textureFactory;
    
    /** References to all textures used in the module. */
    private final Map<String, Texture> textures = new HashMap<String, Texture>();
    
    /** @param renderer Must not be null. */
    public BaseModule(ProxyRenderer renderer) {
        this.renderer = checkNotNull(renderer);
        this.textureFactory = new TextureFactory(renderer.getContext());
    }
    
    /** Cleans up textures allocated by the module. */
    public void cleanUp() {
        getView().queueEvent(new Runnable() {
            @Override
            public void run() {
                int[] toBeDeleted = new int[textures.size()];
                
                int texturePos = 0;
                for (Texture texture : textures.values()) {
                    toBeDeleted[texturePos++] = texture.getHandle();
                }
                
                GLES20.glDeleteTextures(textures.size(), toBeDeleted, Constants.NO_OFFSET);
            }
        });
    }
    
    /** {@inheritDocs} */
    @Override
    protected void configure() {
        bind(Activity.class).toInstance(renderer.getActivity());
        bind(Context.class).toInstance(renderer.getContext());
        bind(EventBus.class).asEagerSingleton();
        bind(GLSurfaceView.class).toInstance(renderer.getView());
        bind(ProxyActivity.class).toInstance(renderer.getActivity());
        bind(ProxyRenderer.class).toInstance(renderer);
        bind(ProxyView.class).toInstance(renderer.getView());
        bind(SensorMeister.class).asEagerSingleton();
        bind(TextureFactory.class).toInstance(textureFactory);
    }
    
    protected Map<String, Animation> getAnimations() { return animations; }
    protected Map<String, Texture> getTextures() { return textures; }
    protected ProxyView getView() { return renderer.getView(); }
    
    /** Loads the animations registered for this module. */
    protected void loadAnimations() {
        for (Map.Entry<String, Animation> entry : animations.entrySet()) { // Load the animation textures
            Animation animation = entry.getValue();
            
            for (Texture texture : animation.getFrames()) {
                texture.setHandle(textureFactory.loadTexture(texture.getRawImage()));
            }
            
            bind(Key.get(Animation.class, Names.named(entry.getKey()))).toInstance(animation);
        }
    }
    
    /** Loads the textures registered for this module. */
    protected void loadTextures() {
        for (Map.Entry<String, Texture> entry : textures.entrySet()) {
            Texture texture = entry.getValue();
            texture.setHandle(textureFactory.loadTexture(texture.getRawImage()));
            
            bind(Key.get(Texture.class, Names.named(entry.getKey()))).toInstance(texture);
        }
    }
    
    @Provides
    protected LocationManager provideLocationManager(final Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
    
    @Provides
    protected SensorManager provideSensorManager(final Context context) {
        return (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }
}

