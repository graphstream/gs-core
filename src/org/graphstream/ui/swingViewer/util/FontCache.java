/*
 * Copyright 2006 - 2013
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.ui.swingViewer.util;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;

/**
 * A cache for fonts.
 * 
 * <p>
 * This cache allows to avoid reloading fonts and allows to quickly lookup a
 * font based on its name, its style (bold, italic) and its size.
 * </p>
 */
public class FontCache {
	/**
	 * The default font.
	 */
	protected Font defaultFont;

	/**
	 * Cached fonts.
	 */
	protected HashMap<String, FontSlot> cache = new HashMap<String, FontSlot>();

	/**
	 * The default font cache.
	 */
	public static FontCache defaultFontCache;

	/**
	 * New empty font cache.
	 */
	public FontCache() {
		defaultFont = new Font("SansSerif", Font.PLAIN, 11);
		// This works only in JDK 1.6 :
		// defaultFont = new Font( Font.SANS_SERIF, Font.PLAIN, 11 );
	}

	/**
	 * The default font.
	 * 
	 * @return A font.
	 */
	public Font getDefaultFont() {
		return defaultFont;
	}

	/**
	 * Default singleton instance for shared font cache. This method and cache
	 * can only be used in the Swing thread.
	 * 
	 * @return The default singleton font cache instance.
	 */
	public static FontCache defaultFontCache() {
		if (defaultFontCache == null)
			defaultFontCache = new FontCache();

		return defaultFontCache;
	}

	public Font getDefaultFont(StyleConstants.TextStyle style, int size) {
		return getFont("SansSerif", style, size);
	}

	/**
	 * Lookup a font, and if not found, try to load it, if still not available,
	 * return the default font.
	 * 
	 * @param name
	 *            The font name.
	 * @param style
	 *            A style, taken from the styles available in the style sheets.
	 * @param size
	 *            The font size in points.
	 * @return A font.
	 */
	public Font getFont(String name, StyleConstants.TextStyle style, int size) {
		FontSlot slot = cache.get(name);

		if (slot == null) {
			slot = new FontSlot(name, style, size);
			cache.put(name, slot);
		}

		return slot.getFont(style, size);
	}
}

/**
 * simple container for a font name.
 * 
 * <p>
 * This container allows to group all the fonts that match a name. It stores the
 * font for sizes and styles.
 * </p>
 */
class FontSlot {
	String name;

	public HashMap<Integer, Font> normal;

	public HashMap<Integer, Font> bold;

	public HashMap<Integer, Font> italic;

	public HashMap<Integer, Font> boldItalic;

	public FontSlot(String name, StyleConstants.TextStyle style, int size) {
		this.name = name;
		insert(style, size);
	}

	protected Map<Integer, Font> mapFromStyle(StyleConstants.TextStyle style) {
		switch (style) {
		case BOLD:
			if (bold == null)
				bold = new HashMap<Integer, Font>();
			return bold;
		case ITALIC:
			if (italic == null)
				italic = new HashMap<Integer, Font>();
			return italic;
		case BOLD_ITALIC:
			if (boldItalic == null)
				boldItalic = new HashMap<Integer, Font>();
			return boldItalic;
		case NORMAL:
		default:
			if (normal == null)
				normal = new HashMap<Integer, Font>();
			return normal;
		}
	}

	protected int toJavaStyle(StyleConstants.TextStyle style) {
		switch (style) {
		case BOLD:
			return Font.BOLD;
		case ITALIC:
			return Font.ITALIC;
		case BOLD_ITALIC:
			return Font.BOLD + Font.ITALIC;
		case NORMAL:
		default:
			return Font.PLAIN;
		}
	}

	public Font insert(StyleConstants.TextStyle style, int size) {
		return insert(mapFromStyle(style), toJavaStyle(style), size);
	}

	protected Font insert(Map<Integer, Font> map, int style, int size) {
		Font font = map.get(size);

		if (font == null) {
			// System.err.printf( "new font %s %s %d%n", name, style, size );
			font = new Font(name, style, size);

			map.put(size, font);
		}

		return font;
	}

	protected Font getFont(StyleConstants.TextStyle style, int size) {
		Map<Integer, Font> map = mapFromStyle(style);

		Font font = map.get(size);

		if (font == null)
			font = insert(map, toJavaStyle(style), size);

		return font;
	}
}