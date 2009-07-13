/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.miv.graphstream.ui2.graphicGraph.stylesheet;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The various constants and static constant conversion methods used for styling.
 */
public class StyleConstants
{
// Constants

	/**
	 * The available units for numerical values.
	 */
	public static enum Units { PX, GU, PERCENTS };
	
	/**
	 * How to fill the contents of the element.
	 */
	public static enum FillMode {
		NONE, PLAIN, DYN_PLAIN,
		GRADIENT_RADIAL, GRADIENT_HORIZONTAL,
		GRADIENT_VERTICAL,
		GRADIENT_DIAGONAL1,
		GRADIENT_DIAGONAL2,
		IMAGE_TILED, IMAGE_SCALED,
		IMAGE_SCALED_RATIO };
	
	/**
	 * How to draw the contour of the element.
	 */
	public static enum StrokeMode {
		NONE, PLAIN, DASHES, DOTS
	}

	/**
	 * How to draw the shadow of the element.
	 */
	public static enum ShadowMode {
		NONE, PLAIN, GRADIENT_RADIAL,
		GRADIENT_HORIZONTAL,
		GRADIENT_VERTICAL,
		GRADIENT_DIAGONAL1,
		GRADIENT_DIAGONAL2
	}

	/**
	 * How to show an element.
	 */
	public static enum VisibilityMode {
		NORMAL, HIDDEN, AT_ZOOM, UNDER_ZOOM, OVER_ZOOM,
		ZOOM_RANGE, ZOOMS		
	}

	/**
	 * How to draw the text of an element.
	 */
	public static enum TextMode {
		NORMAL, TRUNCATED, HIDDEN
	}

	/**
	 * How to show the text of an element.
	 */
	public static enum TextVisibilityMode {
		NORMAL, HIDDEN, AT_ZOOM, UNDER_ZOOM, OVER_ZOOM,
		ZOOM_RANGE, ZOOMS
	}
	
	/**
	 * Variant of the font. 
	 */
	public static enum TextStyle {
		NORMAL, ITALIC, BOLD, BOLD_ITALIC
	}
	
	/**
	 * Where to place the icon around the text (or instead of the text).
	 */
	public static enum IconMode {
		NONE, AT_LEFT, AT_RIGHT, UNDER, ABOVE
	}
	
	/**
	 * How to set the size of the element.
	 */
	public static enum SizeMode {
		NORMAL, DYN_SIZE
	}

	/**
	 * How to align words around their attach point.
	 */
	public static enum TextAlignment {
		CENTER, LEFT, RIGHT,
		AT_LEFT, AT_RIGHT,
		UNDER, ABOVE, JUSTIFY,
		
		ALONG
	}

	/**
	 * Possible shapes for elements.
	 */
	public static enum Shape {
		CIRCLE, BOX, DIAMOND, POLYGON, TRIANGLE, CROSS,
		TEXT_BOX, TEXT_PARAGRAPH, TEXT_CIRCLE, TEXT_DIAMOND,
		TEXT_ELLIPSE, JCOMPONENT,
		
		PIE_CHART, FLOW, ARROW, IMAGES,
		
		LINE, ANGLE, CUBIC_CURVE, POLYLINE, BLOB
	}

	/**
	 * Orientation of a sprite toward its attachment point.
	 */
	public static enum SpriteOrientation {
		NONE, FROM, NODE0, TO, NODE1, PROJECTION
	}
	
	/**
	 * Possible shapes for arrows on edges.
	 */
	public static enum ArrowShape {
		NONE, ARROW, CIRCLE, DIAMOND, IMAGE
	}
	
// Static

	/**
	 * A set of colour names mapped to real AWT Colour objects.
	 */
	protected static HashMap<String, Color> colorMap;

	/**
	 * Pattern to ensure a "#FFFFFF" colour is recognised.
	 */
	protected static Pattern sharpColor;

	/**
	 * Pattern to ensure a CSS style "rgb(1,2,3)" colour is recognised.
	 */
	protected static Pattern cssColor;

	/**
	 * Pattern to ensure a CSS style "rgba(1,2,3,4)" colour is recognised.
	 */
	protected static Pattern cssColorA;

	/**
	 * Pattern to ensure that java.awt.Color.toString() strings are recognised as colour.
	 */
	protected static Pattern awtColor;

	/**
	 * Pattern to ensure an hexadecimal number is a recognised colour.
	 */
	protected static Pattern hexaColor;

	static
	{
		// Prepare some pattern matchers.

		sharpColor = Pattern
				.compile( "#(\\p{XDigit}\\p{XDigit})(\\p{XDigit}\\p{XDigit})(\\p{XDigit}\\p{XDigit})((\\p{XDigit}\\p{XDigit})?)" );
		hexaColor = Pattern
				.compile( "0[xX](\\p{XDigit}\\p{XDigit})(\\p{XDigit}\\p{XDigit})(\\p{XDigit}\\p{XDigit})((\\p{XDigit}\\p{XDigit})?)" );
		cssColor = Pattern.compile( "rgb\\s*\\(\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*\\)" );
		cssColorA = Pattern.compile( "rgba\\s*\\(\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*,\\s*([0-9]+)\\s*\\)" );
		awtColor = Pattern.compile( "java.awt.Color\\[r=([0-9]+),g=([0-9]+),b=([0-9]+)\\]" );
		colorMap = new HashMap<String, Color>();

		// Load all the X11 predefined colour names and their RGB definition
		// from a file stored in the graphstream.jar. This allows the DOT
		// import to correctly map colour names to real AWT Color objects.
		// There are more than 800 such colours...

		URL url = StyleConstants.class.getResource( "rgb.properties" );

		if( url == null )
			throw new RuntimeException( "corrupted graphstream.jar ? the org/miv/graphstream/ui/graphicGraph/rgb.properties file is not found" );
		
		Properties p = new Properties();

		try
		{
			p.load( url.openStream() );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}

		for( Object o: p.keySet() )
		{
			String key = (String) o;
			String val = p.getProperty( key );
			Color  col = Color.decode( val );

			colorMap.put( key.toLowerCase(), col );
		}
	}

	/**
	 * Try to convert the given string value to a colour. It understands the 600
	 * colour names of the X11 RGB data base. It also understands colours given
	 * in the "#FFFFFF" format and the hexadecimal "0xFFFFFF" format. Finally,
	 * it understands colours given as a "rgb(1,10,100)", CSS-like format. If the
	 * input value is null, the result is null.
	 * @param anyValue The value to convert.
	 * @return the converted colour or null if the conversion failed.
	 */
	public static Color convertColor( Object anyValue )
	{
		if( anyValue == null )
			return null;

		if( anyValue instanceof Color )
			return (Color) anyValue;

		if( anyValue instanceof String )
		{
			Color c = null;
			String value = (String) anyValue;

			if( value.startsWith( "#" ) )
			{
				Matcher m = sharpColor.matcher( value );

				if( m.matches() )
				{
					if( value.length() == 7 )
					{
						try
						{
							c = Color.decode( value );

							return c;
						}
						catch( NumberFormatException e )
						{
							c = null;
						}
					}
					else if( value.length() == 9 )
					{
						String r = m.group( 1 );
						String g = m.group( 2 );
						String b = m.group( 3 );
						String a = m.group( 4 );

						c = new Color( Integer.parseInt( r, 16 ), Integer.parseInt( g, 16 ), Integer.parseInt( b, 16 ), Integer.parseInt(
								a, 16 ) );

						return c;
					}
				}
			}
			else if( value.startsWith( "rgb" ) )
			{
				Matcher m = cssColorA.matcher( value );

				if( m.matches() )
				{
					int r = Integer.parseInt( m.group( 1 ) );
					int g = Integer.parseInt( m.group( 2 ) );
					int b = Integer.parseInt( m.group( 3 ) );
					int a = Integer.parseInt( m.group( 4 ) );

					c = new Color( r, g, b, a );

					return c;
				}

				m = cssColor.matcher( value );

				if( m.matches() )
				{
					int r = Integer.parseInt( m.group( 1 ) );
					int g = Integer.parseInt( m.group( 2 ) );
					int b = Integer.parseInt( m.group( 3 ) );

					c = new Color( r, g, b );

					return c;
				}
			}
			else if( value.startsWith( "0x" ) || value.startsWith( "0X" ) )
			{
				Matcher m = hexaColor.matcher( value );

				if( m.matches() )
				{
					if( value.length() == 8 )
					{
						try
						{
							c = Color.decode( value );

							return c;
						}
						catch( NumberFormatException e )
						{
							c = null;
						}
					}
					else if( value.length() == 10 )
					{
						String r = m.group( 1 );
						String g = m.group( 2 );
						String b = m.group( 3 );
						String a = m.group( 4 );

						c = new Color( Integer.parseInt( r, 16 ), Integer.parseInt( g, 16 ), Integer.parseInt( b, 16 ), Integer.parseInt(
								a, 16 ) );

						return c;
					}
				}
			}
			else if( value.startsWith( "java.awt.Color[" ) )
			{
				Matcher m = awtColor.matcher( value );
				
				if( m.matches() )
				{
					int r = Integer.parseInt( m.group( 1 ) );
					int g = Integer.parseInt( m.group( 2 ) );
					int b = Integer.parseInt( m.group( 3 ) );

					c = new Color( r, g, b );

					return c;
				}
			}

			c = colorMap.get( value.toLowerCase() );

			return c;
		}

		return null;
	}

	/**
	 * Check if the given value is an instance of CharSequence (String is) and
	 * return it as a string. Else return null. If the input value is null, the
	 * return value is null. If the value returned is larger than 128 characters,
	 * this method cuts it to 128 characters.
	 * TODO: allow to set the max length of these strings.
	 * @param value The value to convert.
	 * @return The corresponding string, or null.
	 */
	public static String convertLabel( Object value )
	{
		String label = null;

		if( value != null )
		{
			if( value instanceof CharSequence )
				label = ((CharSequence)value).toString();
			else label = value.toString();

			if( label.length() > 128 )
				label = String.format( "%s...", label.substring( 0, 128 ) );
		}
		
		return label;
	}

	/**
	 * Try to convert an arbitrary value to a float. If it is a descendant of
	 * Number, the float value is returned. If it is a string, a conversion is
	 * tried to change it into a number and if successful, this number is
	 * returned as a float. Else, the -1 value is returned as no width can be
	 * negative to indicate the conversion failed. If the input is null, the
	 * return value is -1.
	 * @param value The input to convert.
	 * @return The value or -1 if the conversion failed.
	 */
	public static float convertWidth( Object value )
	{
		if( value instanceof CharSequence )
		{
			try
			{
				float val = Float.parseFloat( ( (CharSequence) value ).toString() );

				return val;
			}
			catch( NumberFormatException e )
			{
				return -1;
			}
		}
		else if( value instanceof Number )
		{
			return ( (Number) value ).floatValue();
		}

		return -1;
	}

	/*
	 * Try to convert an arbitrary value to a EdgeStyle. If the value is a
	 * descendant of CharSequence, it is used and parsed to see if it maps to
	 * one of the possible values.
	 * @param value The value to convert.
	 * @return The converted edge style or null if the value does not identifies
	 *         an edge style.
	public static EdgeStyle convertEdgeStyle( Object value )
	{
		if( value instanceof CharSequence )
		{
			String s = ( (CharSequence) value ).toString().toLowerCase();

			if( s.equals( "dots" ) )
			{
				return EdgeStyle.DOTS;
			}
			else if( s.equals( "dashes" ) )
			{
				return EdgeStyle.DASHES;
			}
			else
			{
				return EdgeStyle.PLAIN;
			}
		}

		return null;
	}
	 */
}