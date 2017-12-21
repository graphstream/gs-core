package org.graphstream.ui.graphicGraph.stylesheet;

/**
 * GraphStream representation of Color
 * Usable by all UI
 */
public class Color {
	private int r ;
	private int g;
	private int b; 
	private int a;
	
	public final static Color white     = new Color(255, 255, 255);
	public final static Color WHITE = white;
	public final static Color lightGray = new Color(192, 192, 192);
	public final static Color LIGHT_GRAY = lightGray;
	public final static Color gray      = new Color(128, 128, 128);
	public final static Color GRAY = gray;
	public final static Color darkGray  = new Color(64, 64, 64);
	public final static Color DARK_GRAY = darkGray;
	public final static Color black     = new Color(0, 0, 0);
	public final static Color BLACK = black;
	public final static Color red       = new Color(255, 0, 0);
	public final static Color RED = red;
	public final static Color pink      = new Color(255, 175, 175);
	public final static Color PINK = pink;
	public final static Color orange    = new Color(255, 200, 0);
	public final static Color ORANGE = orange;
	public final static Color yellow    = new Color(255, 255, 0);
	public final static Color YELLOW = yellow;
	public final static Color green     = new Color(0, 255, 0);
	public final static Color GREEN = green;
	public final static Color magenta   = new Color(255, 0, 255);
	public final static Color MAGENTA = magenta;
	public final static Color cyan      = new Color(0, 255, 255);
	public final static Color CYAN = cyan;
	public final static Color blue      = new Color(0, 0, 255);
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
		Color c = (Color)o;
		return (this.r == c.r && this.g == c.g && this.b == c.b && this.a == c.a);
	}
}
