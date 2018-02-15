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
 * @since 2009-07-05
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Stefan Balev <stefan.balev@graphstream-project.org>
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.graphicGraph;

import org.graphstream.graph.implementations.AbstractElement;
import org.graphstream.ui.graphicGraph.stylesheet.Selector;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Super class of all graphic node, edge, and sprite elements.
 * 
 * <p>
 * Each graphic element references a style, a graphic graph and has a label.
 * </p>
 * 
 * <p>
 * The element also defines the basic behaviour to reload the style when needed,
 * defines abstract methods to set and get the position and bounds in spaces of
 * the element, and to do appropriate actions when specific predefined
 * attributes change (most of them starting with the prefix "ui.").
 * </p>
 * 
 * <p>
 * The graphic element has the ability to store attributes like any other graph
 * element, however the attributes stored by the graphic element are restricted.
 * There is a filter on the attribute adding methods that let pass only :
 * <ul>
 * <li>All attributes starting with "ui.".</li>
 * <li>The "x", "y", "z", "xy" and "xyz" attributes.</li>
 * <li>The "stylesheet" attribute.</li>
 * <li>The "label" attribute.</li>
 * </ul>
 * All other attributes are filtered and not stored. The result is that if the
 * graphic graph is used as an input (a source of graph events) some attributes
 * will not pass through the filter.
 * </p>
 */
public abstract class GraphicElement extends AbstractElement {

	/**
	 * class level logger
	 */
	private static final Logger logger = Logger.getLogger(GraphicElement.class.getSimpleName());

	/**
	 * Interface for renderers registered in each style group.
	 */
	public interface SwingElementRenderer {
	}

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

	/**
	 * Do not show.
	 */
	public boolean hidden = false;

	/**
	 * New element.
	 */
	public GraphicElement(String id, GraphicGraph graph) {
		super(id);
		this.mygraph = graph;
	}

	public GraphicGraph myGraph() {
		return mygraph;
	}

	/**
	 * Type of selector for the graphic element (Node, Edge, Sprite ?).
	 */
	public abstract Selector.Type getSelectorType();

	/**
	 * Style group. An style group may reference several elements.
	 * 
	 * @return The style group corresponding to this element.
	 */
	public StyleGroup getStyle() {
		return style;
	}

	/**
	 * Label or null if not set.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Abscissa of the element, always in GU (graph units). For edges this is the X
	 * of the "from" node.
	 */
	public abstract double getX();

	/**
	 * Ordinate of the element, always in GU (graph units). For edges this is the Y
	 * of the "from" node.
	 */
	public abstract double getY();

	/**
	 * Depth of the element, always in GU (graph units). For edges this is the Z of
	 * the "from" node.
	 */
	public abstract double getZ();

	/**
	 * The associated GUI component.
	 * 
	 * @return An object.
	 */
	public Object getComponent() {
		return component;
	}

	// Commands

	/**
	 * The graphic element was removed from the graphic graph, clean up.
	 */
	protected abstract void removed();

	/**
	 * Try to force the element to move at the give location in graph units (GU).
	 * For edges, this may move the two attached nodes.
	 * 
	 * @param x
	 *            The new X.
	 * @param y
	 *            The new Y.
	 * @param z
	 *            the new Z.
	 */
	public abstract void move(double x, double y, double z);

	/**
	 * Set the GUI component of this element.
	 * 
	 * @param component
	 *            The component.
	 */
	public void setComponent(Object component) {
		this.component = component;
	}

	/**
	 * Handle the "ui.class", "label", "ui.style", etc. attributes.
	 */
	@Override
	protected void attributeChanged(AttributeChangeEvent event, String attribute, Object oldValue, Object newValue) {
		if (event == AttributeChangeEvent.ADD || event == AttributeChangeEvent.CHANGE) {
			if (attribute.charAt(0) == 'u' && attribute.charAt(1) == 'i') {
				if (attribute.equals("ui.class")) {
					mygraph.styleGroups.checkElementStyleGroup(this);
					// mygraph.styleGroups.removeElement( tis );
					// mygraph.styleGroups.addElement( this );
					mygraph.graphChanged = true;
				} else if (attribute.equals("ui.label")) {
					label = StyleConstants.convertLabel(newValue);
					mygraph.graphChanged = true;
				} else if (attribute.equals("ui.style")) {
					// Cascade the new style in the style sheet.

					if (newValue instanceof String) {
						try {
							mygraph.styleSheet.parseStyleFromString(new Selector(getSelectorType(), getId(), null),
									(String) newValue);
						} catch (Exception e) {
							logger.log(Level.WARNING, String.format("Error while parsing style for %S '%s' :",
									getSelectorType(), getId()), e);
						}
						mygraph.graphChanged = true;
					} else {
						logger.warning("Unknown value for style [" + newValue + "].");
					}
				} else if (attribute.equals("ui.hide")) {
					hidden = true;
					mygraph.graphChanged = true;
				} else if (attribute.equals("ui.clicked")) {
					style.pushEventFor(this, "clicked");
					mygraph.graphChanged = true;
				} else if (attribute.equals("ui.selected")) {
					style.pushEventFor(this, "selected");
					mygraph.graphChanged = true;
				} else if (attribute.equals("ui.color")) {
					style.pushElementAsDynamic(this);
					mygraph.graphChanged = true;
				} else if (attribute.equals("ui.size")) {
					style.pushElementAsDynamic(this);
					mygraph.graphChanged = true;
				} else if (attribute.equals("ui.icon")) {
					mygraph.graphChanged = true;
				}
				// else if( attribute.equals( "ui.state" ) )
				// {
				// if( newValue == null )
				// state = null;
				// else if( newValue instanceof String )
				// state = (String) newValue;
				// }
			} else if (attribute.equals("label")) {
				label = StyleConstants.convertLabel(newValue);
				mygraph.graphChanged = true;
			}
		} else // REMOVE
		{
			if (attribute.charAt(0) == 'u' && attribute.charAt(1) == 'i') {
				if (attribute.equals("ui.class")) {
					Object o = attributes.remove("ui.class"); // Not yet removed
																// at
																// this point !
					mygraph.styleGroups.checkElementStyleGroup(this);
					attributes.put("ui.class", o);
					mygraph.graphChanged = true;
				} else if (attribute.equals("ui.label")) {
					label = "";
					mygraph.graphChanged = true;
				} else if (attribute.equals("ui.hide")) {
					hidden = false;
					mygraph.graphChanged = true;
				} else if (attribute.equals("ui.clicked")) {
					style.popEventFor(this, "clicked");
					mygraph.graphChanged = true;
				} else if (attribute.equals("ui.selected")) {
					style.popEventFor(this, "selected");
					mygraph.graphChanged = true;
				} else if (attribute.equals("ui.color")) {
					style.popElementAsDynamic(this);
					mygraph.graphChanged = true;
				} else if (attribute.equals("ui.size")) {
					style.popElementAsDynamic(this);
					mygraph.graphChanged = true;
				}
			} else if (attribute.equals("label")) {
				label = "";
				mygraph.graphChanged = true;
			}
		}
	}

	// Overriding of standard attribute changing to filter them.

	protected static Pattern acceptedAttribute;

	static {
		acceptedAttribute = Pattern.compile("(ui[.].*)|(layout[.].*)|x|y|z|xy|xyz|label|stylesheet");
	}

	@Override
	public void setAttribute(String attribute, Object... values) {
		Matcher matcher = acceptedAttribute.matcher(attribute);

		if (matcher.matches())
			super.setAttribute(attribute, values);
	}
}