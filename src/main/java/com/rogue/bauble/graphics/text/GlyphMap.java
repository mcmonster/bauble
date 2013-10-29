package com.rogue.bauble.graphics.text;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps individual characters to an atlas of glyphs stored in a single image.
 * 
 * @author R. Matt McCann
 */
public class GlyphMap {
    /** Characters defined in the glyph map. */
    private final Map<Character, Glyph> map = new HashMap<Character, Glyph>();
}
