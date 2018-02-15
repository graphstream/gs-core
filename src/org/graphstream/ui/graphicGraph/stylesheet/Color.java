/*
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

/**
 * @since 2017-12-21
 * 
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph.stylesheet;

/**
 * GraphStream representation of Color Usable by all UI
 */
public class Color {
	private int r;
	private int g;
	private int b;
	private int a;

	public final static Color white = new Color(255, 255, 255);
	public final static Color WHITE = white;
	public final static Color lightGray = new Color(192, 192, 192);
	public final static Color LIGHT_GRAY = lightGray;
	public final static Color gray = new Color(128, 128, 128);
	public final static Color GRAY = gray;
	public final static Color darkGray = new Color(64, 64, 64);
	public final static Color DARK_GRAY = darkGray;
	public final static Color black = new Color(0, 0, 0);
	public final static Color BLACK = black;
	public final static Color red = new Color(255, 0, 0);
	public final static Color RED = red;
	public final static Color pink = new Color(255, 175, 175);
	public final static Color PINK = pink;
	public final static Color orange = new Color(255, 200, 0);
	public final static Color ORANGE = orange;
	public final static Color yellow = new Color(255, 255, 0);
	public final static Color YELLOW = yellow;
	public final static Color green = new Color(0, 255, 0);
	public final static Color GREEN = green;
	public final static Color magenta = new Color(255, 0, 255);
	public final static Color MAGENTA = magenta;
	public final static Color cyan = new Color(0, 255, 255);
	public final static Color CYAN = cyan;
	public final static Color blue = new Color(0, 0, 255);
	public final static Color BLUE = blue;

	public Color(int r, int g, int b, int a) {
		super();
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Color(int r, int g, int b) {
		super();
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 255;
	}

	public int getRed() {
		return r;
	}

	public void setRed(int r) {
		this.r = r;
	}

	public int getGreen() {
		return g;
	}

	public void setGreen(int g) {
		this.g = g;
	}

	public int getBlue() {
		return b;
	}

	public void setBlue(int b) {
		this.b = b;
	}

	public int getAlpha() {
		return a;
	}

	public void setAlpha(int a) {
		this.a = a;
	}

	public static Color decode(String nm) throws NumberFormatException {
		Integer intval = Integer.decode(nm);
		int i = intval.intValue();
		return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
	}

	@Override
	public boolean equals(Object o) {
		Color c = (Color) o;
		return (this.r == c.r && this.g == c.g && this.b == c.b && this.a == c.a);
	}
}
