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
 * @since 2014-08-08
 * 
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Beau Tremblay <trembb01@nightcrawler.pfizer.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 */
package org.graphstream.ui.view;

import java.util.Collection;
import java.util.EnumSet;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.util.InteractiveElement;
import org.graphstream.ui.view.util.MouseManager;
import org.graphstream.ui.view.util.ShortcutManager;

/**
 * A view on a graphic graph.
 */
public interface View {
	/**
	 * Get the unique view id.
	 *
	 * @return a view id
	 */
	String getIdView();

	/**
	 * Get a camera object to provide control commands on the view.
	 *
	 * @return a Camera instance
	 */
	Camera getCamera();

	/**
	 * Search for the first GraphicElement among the specified types (precedence:
	 * node, edge, sprite) that contains the point at coordinates (x, y).
	 *
	 * @param types
	 *            The types to check
	 * @param x
	 *            The point abscissa.
	 * @param y
	 *            The point ordinate.
	 * @return The first GraphicElement among the specified types at the given
	 *         coordinates or null if nothing found.
	 */
	GraphicElement findGraphicElementAt(EnumSet<InteractiveElement> types, double x, double y);

	/**
	 * Search for all the graphic elements contained inside the rectangle
	 * (x1,y1)-(x2,y2).
	 *
	 * @param types
	 *            The set of types to check
	 * @param x1
	 *            The rectangle lowest point abscissa.
	 * @param y1
	 *            The rectangle lowest point ordinate.
	 * @param x2
	 *            The rectangle highest point abscissa.
	 * @param y2
	 *            The rectangle highest point ordinate.
	 * @return The set of sprites, nodes, and edges in the given rectangle.
	 */
	Collection<GraphicElement> allGraphicElementsIn(EnumSet<InteractiveElement> types, double x1, double y1, double x2,
			double y2);

	/**
	 * Redisplay or update the view contents. Called by the Viewer.
	 *
	 * @param graph
	 *            The graphic graph to represent.
	 * @param graphChanged
	 *            True if the graph changed since the last call to this method.
	 */
	void display(GraphicGraph graph, boolean graphChanged);

	/**
	 * Open this view in a frame. The argument allows to put the view in a new frame
	 * or to remove it from the frame (if it already exists). Called by the Viewer.
	 *
	 * @param on
	 *            Add the view in its own frame or remove it if it already was in
	 *            its own frame.
	 */
	void openInAFrame(boolean on);

	/**
	 * Close definitively this view. Called by the Viewer.
	 *
	 * @param graph
	 *            The graphic graph.
	 */
	void close(GraphicGraph graph);

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
	 * Freeze an element so that the optional layout cannot move it.
	 *
	 * @param element
	 *            The element.
	 * @param frozen
	 *            If true the element cannot be moved automatically.
	 */
	void freezeElement(GraphicElement element, boolean frozen);

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

	/**
	 * Change the manager for mouse events on this view. If the value for the new
	 * manager is null, a default manager is installed. The
	 * {@link org.graphstream.ui.view.util.MouseManager#init(org.graphstream.ui.graphicGraph.GraphicGraph, View)}
	 * method must not yet have been called.
	 *
	 * @param manager
	 *            The new manager, or null to set the default manager.
	 * @see org.graphstream.ui.view.util.MouseManager
	 */
	void setMouseManager(MouseManager manager);

	/**
	 * Change the manager for key and shortcuts events on this view. If the value
	 * for the new manager is null, a default manager is installed. The
	 * {@link org.graphstream.ui.view.util.ShortcutManager#init(org.graphstream.ui.graphicGraph.GraphicGraph, View)}
	 * method must not yet have been called.
	 *
	 * @param manager
	 *            The new manager, or null to set the default manager
	 * @see org.graphstream.ui.view.util.ShortcutManager
	 */
	void setShortcutManager(ShortcutManager manager);
	
	/**
	 * This is a shortcut to a call setShortcutManager with a MouseOverMouseManager instance and with
	 * (InteractiveElement.EDGE, InteractiveElement.NODE, InteractiveElement.SPRITE).
	 */
	void enableMouseOptions();
	
	/**
	 * Request ui focus.
	 * 
	 * @return optional object used to confirm the request
	 */
	Object requireFocus();

	/**
	 * Generic method for add a new Listener.
	 *
	 * @param T
	 *            Describe the listener
	 * @param U
	 *            Listener
	 */
	<T, U> void addListener(T descriptor, U listener);

	/**
	 * Generic method for remove a Listener.
	 *
	 * @param T
	 *            Describe the listener
	 * @param U
	 *            Listener
	 */
	<T, U> void removeListener(T descriptor, U listener);
}
