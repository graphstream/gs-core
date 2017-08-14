/*
 * Copyright 2006 - 2016
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
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
package org.graphstream.ui.view;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.util.InteractiveElement;

import java.util.Collection;
import java.util.EnumSet;

/**
 * Interface for classes that draw a GraphicGraph in a swing component.
 * 
 * <p>
 * There are two rendering mechanisms in the Swing ui package : the viewer and
 * the renderers. The viewer is a complete architecture to render a graph in a
 * panel or frame, handling all the details. The renderer architecture is a way
 * to only render the graph in any surface, handled directly by the developer.
 * When using the render you are must handle the graphic graph by yourself, but
 * you have a lot more flexibility.
 * </p>
 * 
 * <p>
 * The viewer mechanisms uses graph renderers.
 * </p>
 */
public interface GraphRenderer<S, G> {
	// Initialisation

	void open(GraphicGraph graph, S drawingSurface);

	void close();

	// Access

	View createDefaultView(Viewer viewer, String id);

	/**
	 * Get a camera object to provide control commands on the view.
	 * 
	 * @return a Camera instance
	 */
	Camera getCamera();

	/**
	 * Search for the first GraphicElement among the specified types (precedence: node, edge, sprite) that contains the
	 * point at coordinates (x, y).
	 *
	 * @param types
	 * 			  The types to check
	 * @param x
	 *            The point abscissa.
	 * @param y
	 *            The point ordinate.
	 * @return The first GraphicElement among the specified types at the given coordinates or null if
	 *         nothing found.
	 */
	GraphicElement findGraphicElementAt(EnumSet<InteractiveElement> types, double x, double y);

	/**
	 * Search for all the graphic elements of the specified types contained inside the rectangle
	 * (x1,y1)-(x2,y2).
	 *
	 * @param types
	 * 			  The types to check
	 * @param x1
	 *            The rectangle lowest point abscissa.
	 * @param y1
	 *            The rectangle lowest point ordinate.
	 * @param x2
	 *            The rectangle highest point abscissa.
	 * @param y2
	 *            The rectangle highest point ordinate.
	 * @return The set of GraphicElements in the given rectangle.
	 */
	Collection<GraphicElement> allGraphicElementsIn(EnumSet<InteractiveElement> types, double x1, double y1, double x2, double y2);

	// Command

	/**
	 * Redisplay or update the graph.
	 */
	void render(G g, int x, int y, int width, int height);

	/**
	 * Called by the mouse manager to specify where a node and sprite selection
	 * started.
	 * 
	 * @param x1
	 *            The selection start abscissa.
	 * @param y1
	 *            The selection start ordinate.
	 */
	void beginSelectionAt(double x1, double y1);

	/**
	 * The selection already started grows toward position (x, y).
	 * 
	 * @param x
	 *            The new end selection abscissa.
	 * @param y
	 *            The new end selection ordinate.
	 */
	void selectionGrowsAt(double x, double y);

	/**
	 * Called by the mouse manager to specify where a node and spite selection
	 * stopped.
	 * 
	 * @param x2
	 *            The selection stop abscissa.
	 * @param y2
	 *            The selection stop ordinate.
	 */
	void endSelectionAt(double x2, double y2);

	/**
	 * Force an element to move at the given location in pixels.
	 * 
	 * @param element
	 *            The element.
	 * @param x
	 *            The requested position abscissa in pixels.
	 * @param y
	 *            The requested position ordinate in pixels.
	 */
	void moveElementAtPx(GraphicElement element, double x, double y);

	void screenshot(String filename, int width, int height);

	/**
	 * Set a layer renderer that will be called each time the graph needs to be
	 * redrawn before the graph is rendered. Pass "null" to remove the layer
	 * renderer.
	 * 
	 * @param renderer
	 *            The renderer (or null to remove it).
	 */
	void setBackLayerRenderer(LayerRenderer<G> renderer);

	/**
	 * Set a layer renderer that will be called each time the graph needs to be
	 * redrawn after the graph is rendered. Pass "null" to remove the layer
	 * renderer.
	 * 
	 * @param renderer
	 *            The renderer (or null to remove it).
	 */
	void setForeLayoutRenderer(LayerRenderer<G> renderer);



}