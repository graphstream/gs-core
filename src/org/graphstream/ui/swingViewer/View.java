/*
 * Copyright 2006 - 2013
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
package org.graphstream.ui.swingViewer;

import java.util.ArrayList;

import javax.swing.JPanel;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.util.Camera;
import org.graphstream.ui.swingViewer.util.MouseManager;
import org.graphstream.ui.swingViewer.util.ShortcutManager;

/**
 * A view on a graphic graph.
 * 
 * <p>
 * Basically a view is a Swing panel where a {@link GraphRenderer} renders the
 * graphic graph. If you are in the Swing thread, you can change the view on the
 * graphic graph using methods to translate, zoom and rotate the view.
 * </p>
 */
public abstract class View extends JPanel {
	private static final long serialVersionUID = 4372240131578395549L;
	
	// Attribute

	/**
	 * The view identifier.
	 */
	private String id;

	// Construction

	/**
	 * New view.
	 * 
	 * @param identifier
	 *            The view unique identifier.
	 */
	public View(String identifier) {
		id = identifier;
	}

	// Access

	public String getId() {
		return id;
	}

	/**
	 * Get a camera object to provide control commands on the view.
	 * 
	 * @return a Camera instance
	 */
	public abstract Camera getCamera();

	/**
	 * Search for the first node or sprite (in that order) that contains the
	 * point at coordinates (x, y).
	 * 
	 * @param x
	 *            The point abscissa.
	 * @param y
	 *            The point ordinate.
	 * @return The first node or sprite at the given coordinates or null if
	 *         nothing found.
	 */
	public abstract GraphicElement findNodeOrSpriteAt(double x, double y);

	/**
	 * Search for all the nodes and sprites contained inside the rectangle
	 * (x1,y1)-(x2,y2).
	 * 
	 * @param x1
	 *            The rectangle lowest point abscissa.
	 * @param y1
	 *            The rectangle lowest point ordinate.
	 * @param x2
	 *            The rectangle highest point abscissa.
	 * @param y2
	 *            The rectangle highest point ordinate.
	 * @return The set of sprites and nodes in the given rectangle.
	 */
	public abstract ArrayList<GraphicElement> allNodesOrSpritesIn(double x1,
			double y1, double x2, double y2);

	// Command

	/**
	 * Redisplay or update the view contents. Called by the Viewer.
	 * 
	 * @param graph
	 *            The graphic graph to represent.
	 * @param graphChanged
	 *            True if the graph changed since the last call to this method.
	 */
	public abstract void display(GraphicGraph graph, boolean graphChanged);

	/**
	 * Close definitively this view. Called by the Viewer.
	 * 
	 * @param graph
	 *            The graphic graph.
	 */
	public abstract void close(GraphicGraph graph);

	/**
	 * Open this view JPanel in a frame. The argument allows to put the panel in
	 * a new frame or to remove it from the frame (if it already exists). Called
	 * by the Viewer.
	 * 
	 * @param on
	 *            Add the panel in its own frame or remove it if it already was
	 *            in its own frame.
	 */
	public abstract void openInAFrame(boolean on);

	/**
	 * Set the size of the view frame, if any. If this view has been open in a frame, this changes
	 * the size of the frame containing it. 
	 * 
	 * @param width The new width.
	 * @param height The new height.
	 */
	public abstract void resizeFrame(int width, int height);

	/**
	 * Called by the mouse manager to specify where a node and sprite selection
	 * started.
	 * 
	 * @param x1
	 *            The selection start abscissa.
	 * @param y1
	 *            The selection start ordinate.
	 */
	public abstract void beginSelectionAt(double x1, double y1);

	/**
	 * The selection already started grows toward position (x, y).
	 * 
	 * @param x
	 *            The new end selection abscissa.
	 * @param y
	 *            The new end selection ordinate.
	 */
	public abstract void selectionGrowsAt(double x, double y);

	/**
	 * Called by the mouse manager to specify where a node and spite selection
	 * stopped.
	 * 
	 * @param x2
	 *            The selection stop abscissa.
	 * @param y2
	 *            The selection stop ordinate.
	 */
	public abstract void endSelectionAt(double x2, double y2);

	/**
	 * Freeze an element so that the optional layout cannot move it.
	 * 
	 * @param element
	 * 			The element.
	 * @param frozen
	 * 			If true the element cannot be moved automatically.
	 */
	public abstract void freezeElement(GraphicElement element, boolean frozen);
	
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
	public abstract void moveElementAtPx(GraphicElement element, double x,
			double y);

	/**
	 * Set a layer renderer that will be called each time the graph needs to be
	 * redrawn before the graph is rendered. Pass "null" to remove the layer
	 * renderer.
	 * 
	 * @param renderer
	 *            The renderer (or null to remove it).
	 */
	public abstract void setBackLayerRenderer(LayerRenderer renderer);

	/**
	 * Set a layer renderer that will be called each time the graph needs to be
	 * redrawn after the graph is rendered. Pass "null" to remove the layer
	 * renderer.
	 * 
	 * @param renderer
	 *            The renderer (or null to remove it).
	 */
	public abstract void setForeLayoutRenderer(LayerRenderer renderer);
	
	/**
	 * Change the manager for mouse events on this view. If the value for the new manager is
	 * null, a default manager is installed. The {@link MouseManager#init(GraphicGraph, View)}
	 * method must not yet have been called.
	 * @param manager The new manager, or null to set the default manager.
	 * @see MouseManager
	 */
	public abstract void setMouseManager(MouseManager manager);
	
	/**
	 * Change the manager for key and shortcuts events on this view. If the value for the new
	 * manager is null, a default manager is installed. The {@link ShortcutManager#init(GraphicGraph, View)}
	 * method must not yet have been called.
	 * @param manager The new manager, or null to set the default manager
	 * @see ShortcutManager
	 */
	public abstract void setShortcutManager(ShortcutManager manager);
}