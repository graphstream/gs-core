package org.graphstream.ui.swing.util;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.TextStyle;

public class FontCache {
	
	protected static Font defFont = new Font("SansSerif", Font.PLAIN, 11);
	
	protected static HashMap<String, FontSlot> cache = new HashMap<>();
	
	public static Font getDefaultFont() {
		return defFont;
	}
	
	public static Font getDefaultFont(TextStyle style, int size) {
		return getFont( "SansSerif", style, size );
	}

	public static Font getFont(String name, TextStyle style, int size) {
		if ( cache.get(name) == null ) {
			FontSlot slot = new FontSlot(name, style, size);
			cache.put(name, slot);
			return slot.getFont(style, size);
		}
		else {
			return cache.get(name).getFont(style, size);
		}
	}
}

class FontSlot {
	
	HashMap<Integer, Font> normal = null ;
	HashMap<Integer, Font> bold = null ;
	HashMap<Integer, Font> italic = null ;
	HashMap<Integer, Font> boldItalic = null ;
	
	private String name ;
	
	public FontSlot(String name, TextStyle style, int size) {
		this.name = name ;
		insert(style, size);
	}

	private Font insert(TextStyle style, int size) {
		return insert(mapFromStyle(style), toJavaStyle(style), size);
	}

	private Font insert(Map<Integer, Font> map, int style, int size) {
		if (map.get(size) == null) {
			Font font = new Font(name, style, size);
			map.put(size, font);
			return font ;
		}
		else {
			return map.get(size);
		}
	}
	
	public Font getFont(TextStyle style, int size) {
		Map<Integer, Font> map = mapFromStyle(style);
		
		if (map.get(size) == null) {
			return insert( map, toJavaStyle( style ), size );
		}
		else {
			return map.get(size);
		}
	}

	private int toJavaStyle(TextStyle style) {
		switch (style) {
		case BOLD:
			return Font.BOLD;
		case ITALIC:
			return Font.ITALIC;
		case BOLD_ITALIC:
			return Font.BOLD + Font.ITALIC;
		case NORMAL:
			return Font.PLAIN;
		default:
			return Font.PLAIN;
		}
	}

	private Map<Integer, Font> mapFromStyle(TextStyle style) {
		switch (style) {
			case BOLD:
				if (bold == null) {
					bold = new HashMap<Integer, Font>();
				}
				return bold ;
			case ITALIC:
				if (italic == null) {
					italic = new HashMap<Integer, Font>();
				}
				return italic ;
			case BOLD_ITALIC:
				if (boldItalic == null) {
					boldItalic = new HashMap<Integer, Font>();
				}
				return boldItalic ;
			case NORMAL:
				if (normal == null) {
					normal = new HashMap<Integer, Font>();
				}
				return normal ;
			default:
				if (normal == null) {
					normal = new HashMap<Integer, Font>();
				}
				return normal ;
		}
	}
}