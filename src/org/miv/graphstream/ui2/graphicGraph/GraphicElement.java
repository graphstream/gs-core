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

package org.miv.graphstream.ui2.graphicGraph;

import org.miv.graphstream.graph.implementations.AbstractElement;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.Selector;
import org.miv.graphstream.ui2.graphicGraph.stylesheet.StyleConstants;

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
// Attribute
	
	/**
	 * Graph containing this element.
	 */
	protected GraphicGraph mygraph;
	
	/**
	 * The label or null if not specified.
	 */
	public String label;

	/**
	 * The node style.
	 */
	public StyleGroup style;

	/**
	 * The element state.
	 */
	//public String state = null;
	
// Construction
	
	/**
	 * New element.
	 */
	public GraphicElement( String id, GraphicGraph graph )
    {
	    super( id );
	    this.mygraph = graph;
    }
	
// Access

	/**
	 * Type of selector for the graphic element (Node, Edge, Sprite ?).
	 */
	protected abstract Selector.Type getSelectorType();
	
	/**
	 * Style group. An style group may reference several elements.
	 * @return The style group corresponding to this element.
	 */
	public StyleGroup getStyle()
	{
		return style;
	}
	
	/**
	 * Label or null if not set.
	 * @return A string or null.
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Abscissa of the element. For edges this is the X of the "from" node.
	 */
	public abstract float getX();


	/**
	 * Ordinate of the element. For edges this is the Y of the "from" node.
	 */
	public abstract float getY();
	

	/**
	 * Depth of the element. For edges this is the Z of the "from" node.
	 */
	public abstract float getZ();

	/**
	 * Does the graphical representation on screen (2D thus) contains the point (x,y).
	 * @param x The X coordinate in graph units.
	 * @param y The Y coordinate in graph units.
	 * @param z The Z coordinate in graph units.
	 * @return True if the point is in the graphical representation of the element.
	 */
	public abstract boolean contains( float x, float y, float z );
	
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
	 * The graphic element was removed from the graphic graph, clean up.
	 */
	protected abstract void removed();
	
	/**
	 * Try to force move the element. For edge, this may move the two attached nodes.
	 * @param x The new X.
	 * @param y The new Y.
	 * @param z the new Z.
	 */
	public abstract void move( float x, float y, float z );

	/**
	 * Handle the "ui.class", "label", "ui.style", etc. attributes.
	 */
	@Override
    protected void attributeChanged( String attribute, Object oldValue, Object newValue )
    {
		if( attribute.equals( "ui.class" ) )
		{
			mygraph.styleGroups.removeElement( this );
			mygraph.styleGroups.addElement( this );
			mygraph.graphChanged = true;
		}
		else if( attribute.equals( "label" ) || attribute.equals( "ui.label" ) )
		{
			label = StyleConstants.convertLabel( newValue );
			mygraph.graphChanged = true;
		}
		else if( attribute.equals( "ui.style" ) )
		{
			// Cascade the new style in the style sheet.
			
			if( newValue instanceof String )
			{
				try
				{
					mygraph.styleSheet.parseStyleFromString(
							new Selector( getSelectorType(), getId(), null ),
							(String)newValue );
				}
				catch( java.io.IOException e )
				{
					System.err.printf( "Error while parsing style for %S '%s' :", getSelectorType(), getId() );
					System.err.printf( "    %s%n", e.getMessage() );
					System.err.printf( "    The style was ignored" );
				}

				mygraph.graphChanged = true;
			}
		}
		else if( attribute.equals( "ui.hide" ) )
		{
			mygraph.graphChanged = true;
		}
		else if( attribute.equals( "ui.clicked" ) )
		{
			mygraph.graphChanged = true;
		}
		else if( attribute.equals( "ui.selected" ) )
		{
			mygraph.graphChanged = true;
		}
/*		else if( attribute.equals( "ui.state" ) )
		{
			if( newValue == null )
			{
				state = null;
			}
			else if( newValue instanceof String )
			{
				state = (String) newValue;
			}
		}
*/   }
}