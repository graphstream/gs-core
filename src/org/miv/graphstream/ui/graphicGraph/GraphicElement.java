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

package org.miv.graphstream.ui.graphicGraph;

import org.miv.graphstream.graph.implementations.AbstractElement;
import org.miv.graphstream.ui.graphicGraph.stylesheet.Rule;
import org.miv.graphstream.ui.graphicGraph.stylesheet.Selector;
import org.miv.graphstream.ui.graphicGraph.stylesheet.Style;

/**
 * Super class of all graphic node, edge, and sprite elements.
 * 
 * <p>
 * This class defines the common features of all graphic elements in the graphic graph. This
 * includes the common behaviour (the style handling, has well as the classes, the Z-index or
 * the common attributes like "label" for example).
 * </p>
 */
public abstract class GraphicElement extends AbstractElement
{
// Attributes
	
	/**
	 * Graph containing this element.
	 */
	protected GraphicGraph mygraph;
	
	/**
	 * The label of the node or null if not specified.
	 */
	public String label;

	/**
	 * The node style.
	 */
	public Rule rule;
	
	/**
	 * Quick access to the style z-index.
	 */
	public int zIndex = Integer.MIN_VALUE;
	
// Constructors
	
	public GraphicElement( String id, GraphicGraph graph )
    {
	    super( id );
	    this.mygraph = graph;
		checkStyle();
    }
	
// Access

	/**
	 * Type of selector for the graphic element (Node, Edge, Sprite ?).
	 */
	protected abstract Selector.Type getSelectorType();
	
	/**
	 * This graphic element style rule.
	 * @return The style rule matching this node.
	 */
	public Rule getStyleRule()
	{
		return rule;
	}
	
	/**
	 * This graphic element style.
	 * @return A style taken from the style sheet.
	 */
	public Style getStyle()
	{
		return rule.getStyle();
	}

	/**
	 * The graphic element label or null if not set.
	 * @return A string or null.
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Position in X of the element. For edges this is the X of the "from" node.
	 */
	public abstract float getX();


	/**
	 * Position in Y of the element. For edges this is the Y of the "from" node.
	 */
	public abstract float getY();
	

	/**
	 * Position in Z of the element. For edges this is the Z of the "from" node.
	 */
	public abstract float getZ();

	/**
	 * Does the graphical representation on screen (2D thus) contains the point (x,y).
	 * @param x The X coordinate in graph units.
	 * @param y The Y coordinate in graph units.
	 * @return True if the point is in the graphical representation of the element.
	 */
	public abstract boolean contains( float x, float y );
	
// Commands

	/**
	 * Set the bounds of the graphical representation of this element on screen. This method allows
	 * renderers to specify the space occupied by an element on screen. This allows to pick an
	 * element with the mouse according to its real size on screen. This is set in graph units.
	 * @param x The lowest X bound.
	 * @param y The lowest Y bound.
	 * @param w The node extent along X.
	 * @param h The node extent along Y.
	 */
	public abstract void setBounds( float x, float y, float w, float h );

	/**
	 * Check if the style sheet contains a style for this graphic element. This must be called
	 * on each graphic element when the style sheet changes, and at start. This is also responsible
	 * of the registration of the graphic element in the graph z-index rendering list and shadow
	 * casting list.
	 */
	public void checkStyle()
	{
		rule = mygraph.styleSheet.getRuleFor( this );
		
		checkZIndex();
		checkStyleEvents();
		mygraph.graphChanged = true;
	}
	
	protected void checkZIndex()
	{
		if( rule.getStyle().getShadowStyle() != Style.ShadowStyle.NONE )
		     mygraph.registerShadow( this );
		else mygraph.unregisterShadow( this );
		
		if( zIndex == Integer.MIN_VALUE )
		{
			zIndex = rule.getStyle().getZIndex();
			mygraph.registerAtIndex( zIndex, this );
		}
		else
		{
			if( zIndex != rule.getStyle().getZIndex() )
			{
				mygraph.unregisterAtIndex( zIndex, this );
			
				zIndex = rule.getStyle().getZIndex();
		
				mygraph.registerAtIndex( zIndex, this );
			}
		}
	}
	
	protected void checkStyleEvents()
	{
		if( hasAttribute( "ui.clicked" ) )
		{
			attributeChanged( "ui.clicked", null, true );
		}
		if( hasAttribute( "ui.selected" ) )
		{
			attributeChanged( "ui.selected", null, true );
		}
		if( hasAttribute( "ui.state" ) )
		{
			attributeChanged( "ui.state", null, getAttribute( "ui.state" ) );
		}
		if( hasAttribute( "ui.color" ) )
		{
			attributeChanged( "ui.color", null, getAttribute( "ui.color" ) );
		}
		if( hasAttribute( "ui.width" ) )
		{
			attributeChanged( "ui.width", null, getAttribute( "ui.width" ) );
		}
	}
	
	/**
	 * The graphic element was removed from the graphic graph, clean up.
	 */
	protected void removed()
	{
		if( zIndex != Integer.MIN_VALUE )
		{
			mygraph.unregisterAtIndex( zIndex, this );
		}

		mygraph.unregisterShadow( this );
	}
	
	/**
	 * Try to force move the element. For edge, this may move the two attached nodes.
	 * @param x The new X.
	 * @param y The new Y.
	 * @param z the new Z.
	 */
	public abstract void move( float x, float y, float z );

	/**
	 * Handle the "class", "color", "width", "label" and "style" attributes.
	 */
	@Override
    protected void attributeChanged( String attribute, Object oldValue, Object newValue )
    {
		if( attribute.equals( "ui.class" ) || attribute.equals( "class" ) )
		{
			// The attribute is removed automatically but after this attributeChanged method is
			// called. Therefore we "remove" it by ourselves here so that checkStyle works. This
			// is a quite dirty hack !

			if( newValue == null )
			     attributes.remove( attribute );
			else attributes.put( attribute, newValue );
			
			checkStyle();
			mygraph.graphChanged = true;
		}
		else if( attribute.equals( "ui.color" ) || attribute.equals( "color" ) )
		{
			// The rule is a meta rule ! So we can modify it !
			
			if( newValue == null )
			     rule.getStyle().unsetColor();
			else rule.getStyle().setColor( GraphicGraph.convertColor( newValue ) );

			mygraph.graphChanged = true;
		}
		else if( attribute.equals( "ui.width" ) || attribute.equals( "width" ) )
		{
			if( newValue == null )
			     rule.getStyle().unsetWidth();
			else rule.getStyle().setWidth( GraphicGraph.convertWidth( newValue ), Style.Units.PX );

			mygraph.graphChanged = true;
		}
		else if( attribute.equals( "label" ) )
		{
			label = GraphicGraph.convertLabel( newValue );
			mygraph.graphChanged = true;
		}
		else if( attribute.equals( "ui.style" ) || attribute.equals( "style" ) )
		{
			// Cascade the new style in the style sheet.
			
			if( newValue instanceof String )
			{
				try
				{
					mygraph.styleSheet.parseStyleFromString( new Selector( getSelectorType(), getId(), null ), (String)newValue );
				}
				catch( java.io.IOException e )
				{
					System.err.printf( "Error while parsing style for %S '%s' :", getSelectorType(), getId() );
					System.err.printf( "    %s%n", e.getMessage() );
					// TODO handle this differently ?!!.
				}

				mygraph.graphChanged = true;
				checkStyle();
			}
		}
		else if( attribute.equals( "ui.hide" ) )
		{
			mygraph.graphChanged = true;
		}
		else if( attribute.equals( "ui.clicked" ) )
		{
			if( newValue == null )
			     getStyle().unstackEvent( "clicked" );
			else getStyle().stackEvent( "clicked" );
			mygraph.graphChanged = true;
		}
		else if( attribute.equals( "ui.selected" ) )
		{
			if( newValue == null )
			     getStyle().unstackEvent( "selected");
			else getStyle().stackEvent( "selected" );			
			mygraph.graphChanged = true;
		}
		else if( attribute.equals( "ui.state" ) )
		{
			if( newValue == null )
			{
				getStyle().unstackEvent( state );
				state = null;
			}
			else if( newValue instanceof String )
			{
				state = (String) newValue;
				getStyle().stackEvent( state );
			}
		}
    }
	
	protected String state = null;
}