/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.miv.graphstream.ui.graphicGraph.stylesheet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.miv.util.geom.Point3;

/**
 * A style is a whole set of settings for a graphic element.
 * 
 * <p>
 * Styles inherit each others. By default a style is all set to invalid values meaning "unset".
 * This means that the value is to be taken from the parent. The getters are able to resolve
 * this process by themselves and therefore must be used instead of a direct access to fields.
 * </p>
 */
public class Style
{
// Constants
	
	/**
	 * Text styles.
	 */
	public static enum TextStyle { NORMAL, BOLD, ITALIC, BOLD_ITALIC };
	
	/**
	 * Text alignments.
	 */
	public static enum TextAlignment { LEFT, RIGHT, CENTER, ASIDE, ALONG };

	/**
	 * Text modes.
	 */
	public static enum TextMode { NORMAL, TRUNCATED, HIDDEN };
	
	/**
	 * Arrow shapes.
	 */
	public static enum ArrowShape { NONE, SIMPLE, DIAMANT, CIRCLE, IMAGE };

	/**
	 * Node shapes.
	 */
	public static enum NodeShape { SQUARE, CIRCLE, TRIANGLE, CROSS, IMAGE, TEXT_BOX, TEXT_ELLIPSE };
	
	/**
	 * Edge shapes. 
	 */
	public static enum EdgeShape { LINE, ANGLE, CUBIC_CURVE, POINTS };
	
	/**
	 * Edge styles.
	 */
	public static enum EdgeStyle { PLAIN, DOTS, DASHES };
	
	/**
	 * Sprites shapes.
	 */
	public static enum SpriteShape { CIRCLE, IMAGE, IMAGES, ARROW, FLOW, PIE_CHART, TEXT_BOX, TEXT_ELLIPSE };
	
	/**
	 * Sprite orientation. 
	 */
	public static enum SpriteOrientation { NONE, TO, FROM, ORIGIN };
	
	/**
	 * Is the image drawn at its position and size only, or tiled arround ?. 
	 */
	public static enum ImageMode { SIMPLE, TILED };
	
	/**
	 * The shadow style.
	 */
	public static enum ShadowStyle { NONE, SIMPLE };
	
	/**
	 * Unit systems.
	 * GU means Graph Units, and PX means Pixels. Percents are percents of the graph view size. 
	 */
	public static enum Units { GU, PX, PERCENTS };
	
// Attributes

	/**
	 * A part of the cascade.
	 */
	protected Rule parent = null;

	/**
	 * The other part of the cascade.
	 */
	protected ArrayList<Rule> classParents = null;
	
	/**
	 * The values of each style property.
	 */
	protected HashMap<String,Object> values = new HashMap<String,Object>();
		
	/**
	 * The set of special styles that must override this style when some event occurs.
	 */
	protected HashMap<String,Rule> alternates = null;
	
	/**
	 * Is there a style that replaces this one actually ?. This is a stack since events
	 * can override one another temporarily.
	 */
	protected ArrayList<String> alternateStack = null;
	
// Constructors
	
	/**
	 * New style with all settings to a special value meaning "unset". In this
	 * modeField, all the settings are inherited from the parent (when set).
	 */
	public Style()
	{
	}
	
	/**
	 * New style with all settings to a special value meaning "unset". In this
	 * modeField, all the settings are inherited from the parent.
	 * @param parent The parent style.
	 */
	public Style( Rule parent )
	{
		this.parent = parent;
	}
	
// Access

	/**
	 * The parent style.
	 * @return a style from which some settings are inherited.
	 */
	public Rule getParent()
	{
		return parent;
	}

	/**
	 * Get the value of a given property.
	 * 
	 * This code is the same for all "getX" methods so we explain it once here.
	 * This is the implementation of style inheritance and aggregation.
	 *
	 * 1. First the style looks at an eventual "current event", that is a style
	 * that actually replaces the current style. If this style has a value for
	 * the required property, it is returned. As most of the time this replacing style
	 * inherits this style, there must exist a value.
	 *
	 * 2. Else The style returns its value if it has one.
	 *
	 * 3. If not, it must look at
	 * the classes if aggregates. If it aggregates classes, it is a meta style
	 * and therefore it has a parent that represents its real style as well as
	 * classParents that represents its aggregated classes. Therefore we first
	 * look at the parent style (and only one step in the parent hierarchy and only
	 * if it is a "id" style) and then to all the classes it aggregates in order.
	 *
	 * 4. If it does not aggregates classes (or if no parent classes has a value for
	 * the given property), this is a style that inherit its parent
	 * and we look at the entire parent style hierarchy, recursively until root.
	 * 
	 * @param property The style property the value is searched for.
	 */
	protected Object getValue( String property )
	{
		Object value = values.get( property );
		
		if( alternateStack != null && alternateStack.size() > 0 )
		{
			Object o = null;
			int    i = alternateStack.size() - 1;

			do
			{
				o = getValueForEvent( property, alternateStack.get( i ) );
				i--;
			}
			while( o == null && i >= 0 );
			
			if( o != null )
				return o;
		}
		
		if( value == null )
		{
			if( classParents != null )
			{
				if( parent != null && parent.selector.getId() != null )
				{
					value = parent.style.values.get( property );
					
					if( value != null )
						return value;
				}
				
				for( Rule r: classParents )
				{
					value = r.style.values.get( property );
					
					if( value != null )
						return value;
				}
			}
			
			if( parent != null )
				return parent.style.getValue( property );
		}
		
		return value;
	}
	
	protected Object getValueForEvent( String property, String event )
	{
		if( alternates != null )
		{
			Rule rule = alternates.get( event );
			
			if( rule != null )
			{
				Object o = rule.getStyle().values.get( property );
				
				if( o != null )
					return o;
			}
		}
		else if( parent != null )
		{
			return parent.style.getValueForEvent( property, event );
		}
		
		return null;
	}
	
	/**
	 * True if the given field exists in this style only (not the parents).
	 * @param field The field to test.
	 * @return True if this style has a value for the given field.
	 */
	public boolean hasValue( String field )
	{
		return( values.get( field ) != null );
	}
	
	/**
	 * The colour of foreground elements.
     * @return A colour.
     */
    public Color getColor()
    {
    	ArrayList<Color> colors = getPalette();
    	
    	if( colors != null && colors.size() > 0 )
    		return colors.get( 0 );
    	
    	return null;
    }
    
    /**
     * The background colour.
     * @return A colour.
     */
    public Color getBackgroundColor()
    {
    	ArrayList<Color> colors = getBackgroundPalette();
    	
    	if( colors != null && colors.size() > 0 )
    		return colors.get( 0 );
    	
    	return null;
    }
    
    /**
     * The palette of colours for gradients and multi-colour elements.
     * The first element of the palette is the foreground colour.
     * @see #getColor()
     * @return An array of colours.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Color> getPalette()
    {
    	return (ArrayList<Color>) getValue( "colors" );
    }
    
    /**
     * The palette of background colours for gradients and multi-colour elements.
     * The first element of the background palette is the default background colour.
     * @return An array of colours.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Color> getBackgroundPalette()
    {
    	return (ArrayList<Color>) getValue( "bgColors" );
    }

	/**
	 * The width or diameter or overall size of the element.
     * @return A width in rendering units.
     */
    public Value getWidth()
    {
    	return (Value) getValue( "width" );
    }

    /**
     * The height of length of the element.
     * @return A height in rendering units.
     */
    public Value getHeight()
    {
    	return (Value) getValue( "height" );
    }

    /**
     * The rendering layer this element pertains. 0 is the default layer.
     * @return A layer.
     */
    public int getZIndex()
    {
    	return (Integer) getValue( "z_index" );
    }

	/**
	 * The node shape.
     * @return A node shape.
     */
    public NodeShape getNodeShape()
    {
    	return (NodeShape) getValue( "nodeShape" );
    }

	/**
	 * The edge shape.
     * @return A edge shape.
     */
    public EdgeShape getEdgeShape()
    {
    	return (EdgeShape) getValue( "edgeShape" );
    }
    
    /**
     * The edge style.
     * @return A edge style.
     */
    public EdgeStyle getEdgeStyle()
    {
    	return (EdgeStyle) getValue( "edgeStyle" );
    }
    
    /**
     * The edge point list.
     * @return An array of points.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Point3> getEdgePoints()
    {
    	return (ArrayList<Point3>) getValue( "edgePoints" );
    }

	/**
	 * The image URL.
     * @return An URL.
     */
    public String getImageUrl()
    {
    	return (String) getValue( "imageUrl" );
    }
    
    /**
     * The image modeField.
     * @return An image modeField.
     */
    public ImageMode getImageMode()
    {
    	return (ImageMode) getValue( "imageMode" );
    }
    
    /**
     * The image offset in X.
     * @return An offset in X.
     */
    public Value getImageOffsetX()
    {
    	return (Value) getValue( "imageOffsetX" );
    }
    
    /**
     * The image offset in Y.
     * @return An offset in Y.
     */
    public Value getImageOffsetY()
    {
    	return (Value) getValue( "imageOffsetY" );
    }
    
    /**
     * The sprite shape.
     * @return a sprite shape.
     */
    public SpriteShape getSpriteShape()
    {
    	return (SpriteShape) getValue( "spriteShape" );
    }
    
    /**
     * The sprite orientation.
     * @return A sprite orientation.
     */
    public SpriteOrientation getSpriteOrientation()
    {
    	return (SpriteOrientation) getValue( "spriteOrientation" );
    }

	/**
	 * The arrow shape.
     * @return An arrow shape.
     */
    public ArrowShape getArrowShape()
    {
    	return (ArrowShape) getValue( "arrowShape" );
    }

	/**
	 * The arrow image URL.
     * @return An URL. 
     */
    public String getArrowImageUrl()
    {
    	return (String) getValue( "arrowImageUrl" );
    }
    
    /**
     * The arrow length.
     * @return A length.
     */
    public Value getArrowLength()
    {
    	return (Value) getValue( "arrowLength" );
    }
    
    /**
     * The arrow width.
     * @return A width.
     */
    public Value getArrowWidth()
    {
    	return (Value) getValue( "arrowWidth" );
    }
    
	/**
	 * The width.
     * @return The border width.
     */
    public Value getBorderWidth()
    {
    	return (Value) getValue( "borderWidth" );
    }
    
	/**
	 * The border colour.
     * @return A colour.
     */
    public Color getBorderColor()
    {
    	return (Color) getValue( "borderColor" );
    }
    
    /**
     * The padding.
     * @return The padding length.
     */
    public Value getPadding()
    {
    	return (Value) getValue( "padding" );
    }

	/**
	 * The text foreground colour.
     * @return A colour.
     */
    public Color getTextColor()
    {
    	return (Color) getValue( "textColor" );
    }

	/**
	 * The text size in points.
     * @return A text size.
     */
    public float getTextSize()
    {
    	return (Float) getValue( "textSize" );
    }

	/**
	 * The name of the text font.
     * @return A font name.
     */
    public String getTextFont()
    {
    	return (String) getValue( "textFont" );
    }

	/**
	 * The offset along X from the attach point for the text base
	 * point.
     * @return An offset along X.
     */
    public Value getTextOffsetX()
    {
    	return (Value) getValue( "textOffsetX" );
    }

	/**
	 * The offset along Y from the attach point for the text base
	 * point.
     * @return An offset along Y.
     */
    public Value getTextOffsetY()
    {
    	return (Value) getValue( "textOffsetY" );
    }
    
	/**
	 * The text style.
     * @return A text style.
     */
    public TextStyle getTextStyle()
    {
    	return (TextStyle) getValue( "textStyle" );
    }

	/**
	 * The text alingment.
     * @return A Text alignment
     */
    public TextAlignment getTextAlignment()
    {
    	return (TextAlignment) getValue( "textAlignment" );
    }

    /**
     * The text mode.
     * @return A text mode.
     */
    public TextMode getTextMode()
    {
    	return (TextMode) getValue( "textMode" );
    }
    
// Shadows

    /**
     * The shadow style.
     * @return a shadow style value.
     */
    public ShadowStyle getShadowStyle()
    {
    	return (ShadowStyle) getValue( "shadowStyle" );
    }
    
    /**
     * The shadow width.
     * @return The shadow width.
     */
    public Value getShadowWidth()
    {
    	return (Value) getValue( "shadowWidth" );
    }
    
    /**
     * The shadow offset along X.
     * @return The shadow x offset.
     */
    public Value getShadowOffsetX()
    {
    	return (Value) getValue( "shadowOffsetX" );
    }
    
    /**
     * The shadow offset along Y.
     * @return The shadow Y offset.
     */
    public Value getShadowOffsetY()
    {
    	return (Value) getValue( "shadowOffsetY" );
    }
    
    /**
     * The shadow set of colours.  
     * @return A set of colours.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Color> getShadowPalette()
    {
    	return (ArrayList<Color>) getValue( "shadowPalette" );
    }
    
    /**
     * The main shadow colour.
     * @return The main shadow colour.
     */
    public Color getShadowColor()
    {
    	ArrayList<Color> colors = getShadowPalette();
    	
    	if( colors != null && colors.size() > 0 )
    		return colors.get( 0 );
    	
    	return null;
    }

// Commands

    /**
     * Set the default values for each setting.
     */
    public void setDefaults()
    {
    	ArrayList<Color> colors   = new ArrayList<Color>();
    	ArrayList<Color> bgColors = new ArrayList<Color>();
    	ArrayList<Color> shadows  = new ArrayList<Color>();
    	
    	colors.add( Color.BLACK );
    	bgColors.add( Color.WHITE );
    	shadows.add( new Color( 0.7f, 0.7f, 0.7f ) );
    	
    	values.put( "colors"            , colors );
    	values.put( "bgColors"          , bgColors );
    	values.put( "width"             , new Value( 12, Units.PX ) );
    	values.put( "height"            , new Value( 12, Units.PX ) );
    	values.put( "z_index"           , new Integer( 0 ) );

    	values.put( "nodeShape"         , NodeShape.CIRCLE );
    	values.put( "edgeShape"         , EdgeShape.LINE );
    	values.put( "edgePoints"        , null );
    	values.put( "edgeStyle"         , EdgeStyle.PLAIN );
    	values.put( "spriteShape"       , SpriteShape.CIRCLE );
    	values.put( "spriteOrientation" , SpriteOrientation.NONE );
    	
    	values.put( "borderWidth"       , new Value( 0, Units.PX ) );
    	values.put( "borderColor"       , Color.BLACK );
    	values.put( "padding"           , new Value( 0, Units.PX ) );
    	 
    	values.put( "imageUrl"          , null );
    	values.put( "imageOffsetX"      , new Value( 2, Units.PX ) );
    	values.put( "imageOffsetY"      , new Value( 2, Units.PX ) );
    	values.put( "imageMode"         , ImageMode.SIMPLE );
    	
    	values.put( "arrowShape"        , ArrowShape.SIMPLE );
    	values.put( "arrowLength"       , new Value( 16, Units.PX ) );
    	values.put( "arrowWidth"        , new Value( 4, Units.PX ) );
    	values.put( "arrowImageUrl"     , null );
    	
    	values.put( "textColor"         , Color.GRAY );
    	values.put( "textSize"          , new Float( 11 ) );
    	values.put( "textFont"          , "sans-serif" );
    	values.put( "textOffsetX"       , new Value( 0, Units.PX ) );
    	values.put( "textOffsetY"       , new Value( 0, Units.PX ) );
    	values.put( "textStyle"         , TextStyle.NORMAL );
    	values.put( "textAlignment"     , TextAlignment.CENTER );
    	values.put( "textMode"          , TextMode.TRUNCATED );

    	values.put( "shadowStyle"       , ShadowStyle.NONE );
    	values.put( "shadowWidth"       , new Value( 0, Units.PX ) );
    	values.put( "shadowOffsetX"     , new Value( 0, Units.PX ) );
    	values.put( "shadowOffsetY"     , new Value( 0, Units.PX ) );
    	values.put( "shadowPalette"     , shadows );
    }
    
    /**
     * Copy all the settings of the other style that are set, excepted the parent. Only the
     * settings that have a value (different from "unset") are copied. The parent field is never
     * copied.
     * @param other Another style.
     */
    @SuppressWarnings("unchecked")
    public void augment( Style other )
    {
    	if( other != this )
    	{
    		if( other.hasValue( "colors"        ) ) setValue( "colors",        new ArrayList<Color>( (ArrayList<Color>) other.getValue( "colors" ) ) );
    	    if( other.hasValue( "bgColors"      ) ) setValue( "bgColors",      new ArrayList<Color>( (ArrayList<Color>) other.getValue( "bgColors" ) ) );
    	    if( other.hasValue( "shadowPalette" ) ) setValue( "shadowPalette", new ArrayList<Color>( (ArrayList<Color>) other.getValue( "shadowPalette" ) ) );

    		augmentField( "width",   other );
    		augmentField( "height",  other );
    		augmentField( "z_index", other );
    		
    	    augmentField( "nodeShape", other );
    	    augmentField( "edgeShape", other );
    	    augmentField( "edgeStyle", other );
    	    
    		if( other.hasValue( "edgePoints" ) ) setValue( "edgePoints", new ArrayList<Point3>( (ArrayList<Point3>) other.getValue( "edgePoints" ) ) );
    		
    		augmentField( "spriteShape",       other );
    		augmentField( "spriteOrientation", other );
    		
    		augmentField( "borderWidth", other );
    		augmentField( "borderColor", other );
    		augmentField( "padding",     other );
    		
    		augmentField( "arrowShape",    other );
    		augmentField( "arrowLength",   other );
    		augmentField( "arrowWidth",    other );
    		augmentField( "arrowImageUrl", other );
    		
    		augmentField( "imageUrl",     other );
    		augmentField( "imageMode",    other );
    		augmentField( "imageOffsetX", other );
    		augmentField( "imageOffsetY", other );
    		
    		augmentField( "textColor",     other );
    		augmentField( "textSize",      other );
    		augmentField( "textFont",      other );
    		augmentField( "textOffsetX",   other );
    		augmentField( "textOffsetY",   other );
    		augmentField( "textStyle",     other );
    		augmentField( "textAlignment", other );
    		augmentField( "textMode",      other );
    		
    		augmentField( "shadowStyle", other );
    		augmentField( "shadowWidth", other );
    		augmentField( "shadowOffsetX", other );
    		augmentField( "shadowOffsetY", other );
    	}
    }
    
    protected void augmentField( String field, Style other )
    {
		Object value = other.values.get( field );

    	if( value != null )
    	{
    		if( value instanceof Value )
    		{
    			setValue( field, new Value( (Value)value ) );
    		}
    		else
    		{
    			setValue( field, value );
    		}
    	}
    }
    
    /**
     * Set or change the parent of the style.
     * @param parent The new parent.
     */
    public void reparent( Rule parent )
    {
    	this.parent = parent;
    }
    
    /**
     * add a parent (class) style.
     * @param parent The new class parent.
     */
    public void addParentClass( Rule parent )
    {
    	if( classParents == null )
    		classParents = new ArrayList<Rule>();
    	
    	classParents.add( 0, parent );
    }
    
    /**
     * Add an alternative style for specific events.
     * @param event The event that triggers the alternate style.
     * @param alternateStyle The alternative style.
     */
    public void addAlternateStyle( String event, Rule alternateStyle )
    {
    	if( alternates == null )
    		alternates = new HashMap<String,Rule>();
    	
    	alternates.put( event, alternateStyle );
    }
    
    /**
     * Set the current event that specify the alternative style.
     * @param event The event name.
     */
    public void stackEvent( String event )
    {
    	if( alternateStack == null )
    		alternateStack = new ArrayList<String>();

		alternateStack.add( event );
    }

    /**
     * Remote the alternate style event.
     * @param event The event name.
     */
    public void unstackEvent( String event )
    {
    	if( alternateStack != null )
    	{
    		int index = alternateStack.indexOf( event );
    	    
    		if( index >= 0 )
    			alternateStack.remove( index );
    	}
    }

// Commands -- Setters
    
    protected void setValue( String field, Object value )
    {
    	values.put( field, value );
    }
    
	/**
	 * Set of change the foreground colour.
     * @param color An AWT colour.
     */
    @SuppressWarnings("unchecked")
    public void setColor( Color color )
    {
    	ArrayList<Color> colors = (ArrayList<Color>) values.get( "colors" );
    	
    	if( colors == null )
    	{
    		colors = new ArrayList<Color>();
    		colors.add( color );
    		setValue( "colors", colors );
    	}
    	else
    	{
    		colors.set( 0, color );
    	}
    }
    
    /**
     * Set or change the background colour.
     * @param color An AWT colour.
     */
    @SuppressWarnings("unchecked")
    public void setBackgroundColor( Color color )
    {
    	ArrayList<Color> colors = (ArrayList<Color>) values.get( "bgColors" );
    	
    	if( colors == null )
    	{
    		colors = new ArrayList<Color>();
    		colors.add( color );
    		setValue( "bgColors", colors );
    	}
    	else
    	{
    		colors.set( 0, color );
    	}
    }
    
    /**
     * Add a colour in the colour palette. If no foreground colour was set with
     * {@link #setColor(Color)}, the first colour added with this method will be the foreground
     * colour. It is will be changed each time {@link #setColor(Color)} is called. 
     * @param color The colour to add.
     */
    @SuppressWarnings("unchecked")
    public void addColor( Color color )
    {
    	ArrayList<Color> colors = (ArrayList<Color>) values.get( "colors" );
    	
    	if( colors == null )
    	{
    		colors = new ArrayList<Color>();
    		setValue( "colors", colors );
    	}
    	
    	colors.add( color );
    }
    
    /**
     * Add a colour in the background colour palette. If no background colour was set with
     * {@link #setBackgroundColor(Color)}, the first colour added with this method will be the
     * background colour. It is will be changed each time {@link #setBackgroundColor(Color)} is
     * called. 
     * @param color The colour to add.
     */
    @SuppressWarnings("unchecked")
    public void addBackgroundColor( Color color )
    {
    	ArrayList<Color> colors = (ArrayList<Color>) values.get( "bgColors" );
    	
    	if( colors == null )
    	{
    		colors = new ArrayList<Color>();
    		setValue( "bgColors", colors );
    	}
    	
    	colors.add( color );
    }
    
    /**
     * Remove the colour palette.
     */
    public void unsetColor()
    {
    	values.remove( "colors" );
    }
    
    public void unsetBgColor()
    {
    	values.remove( "bgColors" );
    }
    
	/**
	 * Set of change the element width (diameter or overall size).
     * @param width The new width.
     * @param units The units.
     */
    public void setWidth( float width, Units units )
    {
    	setValue( "width", new Value( width, units ) );
    }

    /**
     * Remove the width setting.
     */
    public void unsetWidth()
    {
    	values.remove( "width" );
    }
    
    /**
     * Set or change the element height (or length).
     * @param height The new height.
     * @param units The units.
     */
    public void setHeight( float height, Units units )
    {
    	setValue( "height", new Value( height, units ) );
    }
    
    /**
     * Set or change the z-index.
     * @param z_index The new z-index.
     */
    public void setZIndex( int z_index )
    {
    	setValue( "z_index", z_index );
    }

	/**
	 * Set of change the node shape.
     * @param nodeShape A node shape.
     */
    public void setNodeShape( NodeShape nodeShape )
    {
    	setValue( "nodeShape", nodeShape );
    }

	/**
	 * Set of change the edge shape.
     * @param edgeShape A edge shape.
     */
    public void setEdgeShape( EdgeShape edgeShape )
    {
    	setValue( "edgeShape", edgeShape );
    }
    
    /**
     * Set or change the edge style.
     * @param edgeStyle A edge style.
     */
    public void setEdgeStyle( EdgeStyle edgeStyle )
    {
    	setValue( "edgeStyle", edgeStyle );
    }
    
    /**
     * Remove the edge style setting.
     */
    public void unsetEdgeStyle()
    {
    	values.remove( "edgeStyle" );
    }
    
    /**
     * Erase the edge point list.
     */
    public void removeEdgePoints()
    {
    	values.remove( "edgePoints" );
    }
    
    /**
     * Add a new edge point at (x,y,z).
     * @param x The abscissa.
     * @param y The ordinate.
     * @param z The depth.
     */
    @SuppressWarnings("unchecked")
    public void addEdgePoint( float x, float y, float z )
    {
    	ArrayList<Point3> edgePoints = (ArrayList<Point3>) values.get( "edgePoints" );
    	
    	if( edgePoints == null )
    	{
    		edgePoints = new ArrayList<Point3>();
    		setValue( "edgePoints", edgePoints );
    	}
    	
    	edgePoints.add( new Point3( x, y, z ) );
    }
    
    /**
     * Set or change the sprite shape.
     * @param spriteShape A sprite shape.
     */
    public void setSpriteShape( SpriteShape spriteShape )
    {
    	setValue( "spriteShape", spriteShape );
    }
    
    /**
     * Set or change the sprite orientation.
     * @param spriteOrientation A sprite orientation.
     */
    public void setSpriteOrientation( SpriteOrientation spriteOrientation )
    {
    	setValue( "spriteOrientation", spriteOrientation );
    }

	/**
	 * Set of change the border width.
     * @param borderWidth A width.
     * @param units Units.
     */
    public void setBorderWidth( float borderWidth, Units units )
    {
    	setValue( "borderWidth", new Value( borderWidth, units ) );
    }

	/**
	 * Set or change the border colour.
     * @param borderColor An AWT colour.
     */
    public void setBorderColor( Color borderColor )
    {
    	setValue( "borderColor", borderColor );
    }

	/**
	 * Set or change the text foreground colour.
     * @param textColor An AWT colour.
     */
    public void setTextColor( Color textColor )
    {
    	setValue( "textColor", textColor );
    }
    
    /**
     * Set or change the padding.
     * @param padding The padding length.
     * @param units Units.
     */
    public void setPadding( float padding, Units units )
    {
    	setValue( "padding", new Value( padding, units ) );
    }

	/**
	 * Set or change the text size in points. 
     * @param textSize A point size.
     */
    public void setTextSize( float textSize )
    {
    	setValue( "textSize", new Float( textSize ) );
    }

	/**
	 * Set or change the text font name.
     * @param textFont A font name.
     */
    public void setTextFont( String textFont )
    {
    	setValue( "textFont", textFont );
    }

	/**
	 * Set or change the text offset along X and Y from the attach point.
	 * @param x A length.
	 * @param y A length.
     * @param units Units.
     */
    public void setTextOffset( float x, float y, Units units )
    {
    	setValue( "textOffsetX", new Value( x, units ) );
    	setValue( "textOffsetY", new Value( y, units ) );
    }

	/**
	 * Set or change the text style.
     * @param textStyle A text style.
     */
    public void setTextStyle( TextStyle textStyle )
    {
    	setValue( "textStyle", textStyle );
    }
    
    /**
     * Set or change the text mode.
     * @param textMode A text mode.
     */
    public void setTextMode( TextMode textMode )
    {
    	setValue( "textMode", textMode );
    }

	/**
	 * Set or change the image URL.
     * @param nodeImageUrl An URL.
     */
    public void setImageUrl( String nodeImageUrl )
    {
    	setValue( "imageUrl", nodeImageUrl );
    }

	/**
	 * Set or change the arrow image URL.
     * @param arrowImageUrl An URL.
     */
    public void setArrowImageUrl( String arrowImageUrl )
    {
    	setValue( "arrowImageUrl", arrowImageUrl );
    }
    
    /**
     * Set or change the image offset in X and Y.
     * @param x The new offset in X.
     * @param y The new offset in Y.
     * @param units Units.
     */
    public void setImageOffset( float x, float y, Units units )
    {
    	setValue( "imageOffsetX", new Value( x, units ) );
    	setValue( "imageOffsetY", new Value( y, units ) );
    }
    
    /**
     * Set or change the image modeField.
     * @param mode The new image modeField.
     */
    public void setImageMode( ImageMode mode )
    {
    	setValue( "imageMode", mode );
    }
    
    /**
     * Set or change the arrow length.
     * @param arrowLength The new length.
     * @param units Units.
     */
    public void setArrowLength( float arrowLength, Units units )
    {
    	setValue( "arrowLength", new Value( arrowLength, units ) );
    }
    
    /**
     * Set or change the arrow width.
     * @param arrowWidth The new width.
     * @param units Units.
     */
    public void setArrowWidth( float arrowWidth, Units units )
    {
    	setValue( "arrowWidth", new Value( arrowWidth, units ) );
    }

	/**
	 * Set or change the text alignment.
     * @param textAlignment A text alignment.
     */
    public void setTextAlignment( TextAlignment textAlignment )
    {
    	setValue( "textAlignment", textAlignment );
    }

	/**
	 * Set or change the arrow shape.
     * @param arrowShape an arrow shape.
     */
    public void setArrowShape( ArrowShape arrowShape )
    {
    	setValue( "arrowShape", arrowShape );
    }

// Shadows
    
    /**
     * Set or change the shadow style.
     * @param shadowStyle a shadow style.
     */
    public void setShadowStyle( ShadowStyle shadowStyle )
    {
    	setValue( "shadowStyle", shadowStyle );
    }
    
    /**
     * Set the shadow width.
     * @param value The width.
     * @param units The units.
     */
    public void setShadowWidth( float value, Units units )
    {
    	setValue( "shadowWidth", new Value( value, units ) );
    }
    
	/**
	 * Set of change the shadow foreground colour.
     * @param color An AWT colour.
     */
    @SuppressWarnings("unchecked")
    public void setShadowColor( Color color )
    {
    	ArrayList<Color> colors = (ArrayList<Color>) values.get( "shadowPalette" );
    	
    	if( colors == null )
    	{
    		colors = new ArrayList<Color>();
    		colors.add( color );
    		setValue( "shadowPalette", colors );
    	}
    	else
    	{
    		colors.set( 0, color );
    	}
    }
    
    /**
     * Add a colour in the shadow colour palette. If no foreground colour was set with
     * {@link #setShadowColor(Color)}, the first colour added with this method will be the foreground
     * shadow colour. It is will be changed each time {@link #setShadowColor(Color)} is called. 
     * @param color The colour to add.
     */
    @SuppressWarnings("unchecked")
    public void addShadowColor( Color color )
    {
    	ArrayList<Color> colors = (ArrayList<Color>) values.get( "shadowPalette" );
    	
    	if( colors == null )
    	{
    		colors = new ArrayList<Color>();
    		setValue( "shadowPalette", colors );
    	}
    	
    	colors.add( color );
    }
    
    /**
     * Remove the shadow colour palette.
     */
    public void unsetShadowColor()
    {
    	values.remove( "shadowPalette" );
    }
    
    /**
     * Set or change the shadow offset in X and Y.
     * @param x The new offset in X.
     * @param y The new offset in Y.
     * @param units Units.
     */
    public void setShadowOffset( float x, float y, Units units )
    {
    	setValue( "shadowOffsetX", new Value( x, units ) );
    	setValue( "shadowOffsetY", new Value( y, units ) );    	
    }

// Utility
    
	@Override
    public String toString()
    {
		return toString( -1 );
    }
	
	public String toString( int level )
	{
    	StringBuilder builder = new StringBuilder();
    	String        prefix  = "  ";

    	if( level > 0 )
    	{
    		for( int i=0; i<level; i++ )
    			prefix += "    "; 
    	}
    	
    	Iterator<String> i = values.keySet().iterator();
    	
    	builder.append( String.format( "%s%s%n", prefix, super.toString() ) );
    	builder.append( String.format( "%s%sparent         : %s%n", prefix, prefix, parent != null ? "yes" : "no" ) );
    	builder.append( String.format( "%s%sparent-classes : %d%n", prefix, prefix, classParents != null ? classParents.size() : 0 ) );
    	builder.append( String.format( "%s%salternates     : %d%n", prefix, prefix, alternates   != null ? alternates.size() : 0 ) );
    	
    	while( i.hasNext() )
    	{
    		String key = i.next();
    		Object o   = values.get( key );
    		
    		if( o instanceof ArrayList<?> )
    		{
    			ArrayList<?> array = (ArrayList<?>) o;
    			
    			if( array.size() > 0 )
    			{
    				builder.append( String.format( "%s%s%s%s: ", prefix, prefix, prefix, key ) );

    				for( Object p: array )
    					builder.append( String.format( "%s ", p.toString() ) );
    				
    				builder.append( String.format( "%n" ) );
    			}
    			else
    			{
            		builder.append( String.format( "%s%s%s%s: <empty>%n", prefix, prefix, prefix, key ) );    			    				
    			}
    		}
    		else
    		{
        		builder.append( String.format( "%s%s%s%s: %s%n", prefix, prefix, prefix, key, o != null ? o.toString() : "<null>" ) );    			
    		}
    	}
		
		if( level >= 0 )
		{
			if( parent != null )
			{
				String rec = parent.style.toString( level + 1 );
				
				builder.append( rec );
			}
			
			if( classParents != null )
			{
				for( Rule r: classParents )
				{
					String rec = r.toString();
					
					builder.append( rec );
				}
			}
		}

		String res = builder.toString();
		
		if( res.length() == 0 )
			return String.format( "%s%s<empty>%n", prefix, prefix );
		
		return res;		
	}
}