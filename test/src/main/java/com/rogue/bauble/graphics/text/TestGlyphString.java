package com.rogue.bauble.graphics.text;

import com.rogue.bauble.graphics.MVP;

/**
 * Test implementation of GlyphString.
 * 
 * @author R. Matt McCann
 */
public class TestGlyphString extends GlyphString {
    /** {@inheritDocs} */
    @Override
    public void delete() { }

    /** {@inheritDocs} */
    @Override
    public float getWidth() { return 1; }

    /** {@inheritDocs} */
    @Override
    protected void updateRendering() { }

    /** {@inheritDocs} */
    @Override
    public void render(MVP mvp) { }
    
    public static class TestGlyphStringFactory implements GlyphStringFactory {
        /** {@inheritDocs} */
        @Override
        public TestGlyphString create() {
            return new TestGlyphString();
        }
    }
}
