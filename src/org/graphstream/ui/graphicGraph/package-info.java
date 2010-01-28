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

/**
 * Classes of the graph representation used by the graph viewer.
 * 
 * <p>
 * The graphic graph is a {@link org.graphstream.graph.Graph} implementation
 * named {@link org.graphstream.ui.graphicGraph.GraphicGraph} that also defines
 * attributes for visual representation.
 * </p>
 * 
 * <p>
 * The graphic graph contains a style sheet whose styling principle is copied from
 * the way CSS is used for HTML pages or SVG drawings. This style sheet defines styles (set of
 * style properties and values for these properties) and rules to define to which
 * graph elements the style applies. A rule may allow to apply a style to several
 * graph elements at once, to all nodes, to elements having a given class, or identifier,
 * etc. See the {@link org.graphstream.ui.graphicGraph.stylesheet}
 * package for more informations.
 * </p>
 * 
 * <p>
 * The graphic graph uses a set of {@link org.graphstream.ui.graphicGraph.StyleGroup}s
 * to aggregate the style rules and the elements they apply to. A style group both
 * contains a set of graph elements (nodes, edges, sprites, graphs) and the rules
 * that define the styles for these elements.
 * </p>
 * 
 * <p>
 * Inside the graphic graph, the style groups are organised inside a
 * {@link org.graphstream.ui.graphicGraph.StyleGroupSet} that listens at the style
 * sheet and at the graph and creates the style groups for elements, call the graphic
 * graph each time the style changes, etc.
 * </p>
 * 
 * <p>
 * The graphic graph is constructed like any other instance of {@link org.graphstream.graph.Graph}
 * but provides less methods (some methods are not implemented). This is indeed
 * because they are not really useful in the rendering context. For example it is
 * not possible to use the read() and write() utility methods. In the same way,
 * naturally, the display() method is not implemented since this graph purpose is
 * to be used inside the implementation of a display for the graph.
 * </p>
 * 
 * <p>
 * The style group set also defines utilities to help in drawing the graphic graph.
 * It provides a Z index object and iterator that allows to browse the style groups
 * following the "z-index" style property. This allows to draw the elements in order
 * according to the depth, and therefore to avoid undesired overlapping of elements.
 * </p>
 * 
 * <p>
 * In the same idea, a shadow set of element is provided, as well as an iterator on
 * this set to browse all the elements that cast a shadow. Most of the time the
 * shadows will be drawn first.
 * </p>
 * 
 * <p>
 * Inside the graphic graph, the graph elements are special implementations for
 * nodes and edges : {@link org.graphstream.ui.graphicGraph.GraphicNode} and
 * {@link org.graphstream.ui.graphicGraph.GraphicEdge}. In addition the notion
 * of sprite is added with the {@link org.graphstream.ui.graphicGraph.GraphicSprite}. The
 * sprites are not really graph elements, but are treated the same in the graphic
 * graph. They can have attributes as well.
 * </p>
 * 
 * <p>
 * All this graph elements inherit the {@link org.graphstream.ui.graphicGraph.GraphicElement}
 * class that in turn implements {@link org.graphstream.graph.Element}. The graphic element
 * is a base class that defines the basic properties for the graphical representations of
 * graph elements. All elements will have a position, bounds, a label and a style group.
 * </p>
 * 
 * <p>
 * This package and the main class {@link org.graphstream.ui.graphicGraph.GraphicGraph}
 * provide a large level of automatism. You should not have to worry about the internals of
 * the graphic graph as it is maintained automatically. Use the "stylesheet" or "ui.stylesheet"
 * attributes to provide the style sheet (or accumulate them as the cascade is implemented).
 * The style mechanism is otherwise completely automatic.
 * </p>
 * 
 * <p>
 * The only way the graphic graph differentiate in usage of a graph is in its additional
 * handling of sprites, and the methods is provides to access the style group set and
 * the z index and shadow map.
 * </p>
 */
package org.graphstream.ui.graphicGraph;