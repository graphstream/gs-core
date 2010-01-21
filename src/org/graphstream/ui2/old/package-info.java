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

/**
 * Graph rendering and visualisation classes.
 * 
 * <p>
 * The UI package is composed of three main parts :
 * </p>
 * 
 * <ul>
 * 		<li>The graph viewer classes that define the interface of a graph viewer
 *			UI component, as well as a mean to control it and listen at its events.
 * 		<li>The graphic graph classes in the {@link org.graphstream.ui.graphicGraph}
 * 			package which define a {@link org.graphstream.graph.Graph} implementation
 * 			that listen at a graph, copy its structure and
 * 			defines a style sheet, attaches styles to graphic node and graphic edges,
 * 			define the notion of "sprite" and allows to position all these elements.</li>
 * 		<li>The graph viewer implementations. At this time there is only one implementation,
 * 			done in Swing inside the {@link org.graphstream.ui2.old.swing} package. Other
 * 			implementations will probably be given in separate projects since they
 * 			often need external libraries that we cannot ship easily with GraphStream
 * 			(for ease of use, portability and size reasons).</li>
 * </ul>
 *
 * <p>
 * The {@link org.graphstream.ui.GraphViewer} class defines the services proposed
 * by a graph viewer. A graph viewer is a graphical component that renders a graph to
 * visualise it. The graph viewer is made to run in a separate thread so that the viewer
 * can draw the graph in parallel with your code (which creates and modifies the graph).
 * Lets call your code running in the main thread the "user thread" and the graph viewer
 * thread the "viewer thread".
 * </p>
 * 
 * <p>
 * A default base (incomplete abstract class) implementation is given by the
 * {@link org.graphstream.ui.GraphViewerBase} class. This implementations creates a
 * graphic graph in the viewer thread that copies your graph and provides graphic attributes
 * like positions and CSS style. Lets call the graph in the user thread the "user graph" and the
 * graph in the "viewer thread" the "graphic graph". The graphic graph is created automatically
 * and listen at the graph so
 * that an exact copy is maintained. This graphic graph is able to read style sheets
 * and automatically equip its (graphic) nodes and (graphic) edges with style elements according to the
 * style sheet or style attributes put on the user graph. The default viewer is also able
 * to launch automatically a layout algorithm in a dedicated "layout thread" and provides
 * all the machinery to communicate with this thread and modify the nodes positions. Finally
 * the graphic graph is equipped with a "sprite" manager that allows to add graphical elements
 * to the thread display.
 * </p>
 * 
 * <p>
 * A {@link org.graphstream.ui.GraphViewerRemote} is provided to manipulate the graph viewer
 * from the user thread.
 * </p>
 * 
 * <p>
 * The {@link org.graphstream.ui.graphicGraph} package provides the implementation for the graphic graph, for the style sheet
 * and style classes as well as for the style sheet parser.
 * </p
 * 
 * <p>
 * The {@link org.graphstream.ui2.old.swing} package provides a complete (and reference) implementation for the graph viewer
 * class. It understands styles provided by the style sheet. It is quite portable since it
 * uses Swing and Java2D for its rendering. 
 * </p>
 */
package org.graphstream.ui2.old;