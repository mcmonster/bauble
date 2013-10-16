package com.rogue.bauble.properties;

import com.rogue.bauble.graphics.MVP;

/**
 * Interface for objects that can be rendered graphically.
 * 
 * @author R. Matt McCann
 */
public interface Renderable {
    /**
     * Renders the objects.
     * 
     * @param mvp Model-View-Projection matrices to be used for rendering.
     */
    void render(final MVP mvp);
}