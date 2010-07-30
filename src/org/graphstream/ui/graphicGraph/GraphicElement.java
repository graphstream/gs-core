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

package org.graphstream.ui.graphicGraph;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphstream.graph.implementations.AbstractElement;
import org.graphstream.ui.graphicGraph.stylesheet.Selector;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;

/**
 * Super class of all graphic node, edge, and sprite elements.
 * 
 * <p>
 * Each graphic element references a style, a graphic graph and has a label.
 * </p>
 * 
 * <p>
 * The element also defines the basic behaviour to reload the style when needed, defines abstract
 * methods to set and get the position and bounds in spaces of the element, and to do appropriate
 * actions when specific predefined attributes change (most of them starting with the prefix
 * "ui."). 
 * </p>
 * 
 * <p>
 * The graphic element has the ability to store attributes like any other graph element, however
 * the attributes stored by the graphic element are restricted. There is a filter on the attribute
 * adding methods that let pass only :
 * <ul>
 * 		<li>All attributes starting with "ui.".</li>
 * 		<li>The "x", "y", "z", "xy" and "xyz" attributes.</li>
 * 		<li>The "stylesheet" attribute.</li>
 * 		<li>The "label" attribute.</li>
 * </ul>
 * All other attributes are filtered and not stored. The result is that if the graphic graph is
 * used as an input (a source of graph events) some attributes will not pass through the filter.
 * </p>
 */
public abstract class GraphicElement extends AbstractElement
{
// Nested interfaces
	
	public interface SwingElementRenderer
	{
	}
	
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
	 * Associated GUI component.
	 */
	public Object component;
	
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
	
	public GraphicGraph myGraph()
	{
		return mygraph;
	}
	
	@Override
	protected String myGraphId()	// XXX
	{
		return mygraph.getId();
	}
	
	@Override
	protected long newEvent()		// XXX
	{
		return mygraph.newEvent();
	}

	/**
	 * Type of selector for the graphic element (Node, Edge, Sprite ?).
	 */
	public abstract Selector.Type getSelectorType();
	
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
	 * Abscissa of the element, always in GU (graph units). For edges this is the X of the "from" node.
	 */
	public abstract float getX();


	/**
	 * Ordinate of the element, always in GU (graph units). For edges this is the Y of the "from" node.
	 */
	public abstract float getY();
	

	/**
	 * Depth of the element, always in GU (graph units). For edges this is the Z of the "from" node.
	 */
	public abstract float getZ();

	/**
	 * The associated GUI component.
	 * @return An object.
	 */
	public Object getComponent()
	{
		return component;
	}
	
// Commands

	/**
	 * The graphic element was removed from the graphic graph, clean up.
	 */
	protected abstract void removed();
	
	/**
	 * Try to force the element to move at the give location in graph units (GU). For edges, this
	 * may move the two attached nodes.
	 * @param x The new X.
	 * @param y The new Y.
	 * @param z the new Z.
	 */
	public abstract void move( float x, float y, float z );

	/**
	 * Set the GUI component of this element.
	 * @param component The component.
	 */
	public void setComponent( Object component )
	{
		this.component = component;
	}
	
	/**
	 * Handle the "ui.class", "label", "ui.style", etc. attributes.
	 */
	@Override
    protected void attributeChanged( String sourceId, long timeId, String attribute, AttributeChangeEvent event, Object oldValue, Object newValue )
    {
		if( event == AttributeChangeEvent.ADD || event == AttributeChangeEvent.CHANGE )
		{
			if( attribute.equals( "ui.class" ) )
			{
				mygraph.styleGroups.checkElementStyleGroup( this );
//				mygraph.styleGroups.removeElement( tis );
//				mygraph.styleGroups.addElement( this );
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
				else
				{
					System.err.printf( "ERROR !!%n" );
				}
			}
			else if( attribute.equals( "ui.hide" ) )
			{
				mygraph.graphChanged = true;
			}
			else if( attribute.equals( "ui.clicked" ) )
			{
				style.pushEventFor( this, "clicked" );
				mygraph.graphChanged = true;
			}
			else if( attribute.equals( "ui.selected" ) )
			{
				style.pushEventFor( this, "selected" );
				mygraph.graphChanged = true;
			}
			else if( attribute.equals( "ui.color" ) )
			{
				style.pushElementAsDynamic( this );
				mygraph.graphChanged = true;
			}
			else if( attribute.equals( "ui.size" ) )
			{
				style.pushElementAsDynamic( this );
				mygraph.graphChanged = true;
			}
			else if( attribute.equals( "ui.icon" ) )
			{
				mygraph.graphChanged = true;
			}
//			else if( attribute.equals( "ui.state" ) )
//			{
//				if( newValue == null )
//					state = null;
//				else if( newValue instanceof String )
//					state = (String) newValue;
//			}
		}
		else	// REMOVE
		{
			if( attribute.equals( "ui.class" ) )
			{
				mygraph.styleGroups.checkElementStyleGroup( this );
				mygraph.graphChanged = true;
			}
			else if( attribute.equals( "label" ) || attribute.equals( "ui.label" ) )
			{
				label = "";
				mygraph.graphChanged = true;
			}
			else if( attribute.equals( "ui.hide" ) )
			{
				mygraph.graphChanged = true;
			}
			else if( attribute.equals( "ui.clicked" ) )
			{
				style.popEventFor( this, "clicked" );
				mygraph.graphChanged = true;
			}
			else if( attribute.equals( "ui.selected" ) )
			{
				style.popEventFor( this, "selected" );
				mygraph.graphChanged = true;
			}
			else if( attribute.equals( "ui.color" ) )
			{
				style.popElementAsDynamic( this );
				mygraph.graphChanged =true;
			}
			else if( attribute.equals( "ui.size" ) )
			{
				style.popElementAsDynamic( this );
				mygraph.graphChanged = true;
			}
		}
    }
	
// Overriding of standard attribute changing to filter them.
	
	protected static Pattern acceptedAttribute;
	
	static {
		acceptedAttribute = Pattern.compile( "(ui\\..*)|x|y|z|xy|xyz|label|stylesheet" );
	}
	
	@Override
	public void addAttribute( String attribute, Object ... values )
	{
		Matcher matcher = acceptedAttribute.matcher( attribute );
		
		if( matcher.matches() )
			super.addAttribute( attribute, values );
	}
	
// Make change _ methods visible

	@Override
	protected void addAttribute_( String sourceId, long timeId, String attribute, Object ... values )
	{
		super.addAttribute_( sourceId, timeId, attribute, values );
	}
	
	@Override
	protected void changeAttribute_( String sourceId, long timeId, String attribute, Object ... values )
	{
		super.changeAttribute_( sourceId, timeId, attribute, values );
	}
	
	@Override
	protected void setAttribute_( String sourceId, long timeId, String attribute, Object ...values )
	{
		super.setAttribute_( sourceId, timeId, attribute, values );
	}
	
	@Override
	protected void addAttributes_( String sourceId, long timeId, Map<String,Object> attributes )
	{
		super.addAttributes_( sourceId, timeId, attributes );
	}
	
	@Override
	protected void removeAttribute_( String sourceId, long timeId, String attribute )
	{
		super.removeAttribute_( sourceId, timeId, attribute );
	}
}