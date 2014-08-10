package org.graphstream.ui.view;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.util.MouseManager;
import org.graphstream.ui.view.util.ShortcutManager;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collection;

/**
 * A view on a graphic graph.
 */
public interface View
{
    /**
     * Get the unique view id.
     *
     * @return a view id
     */
    String getId();

    /**
     * Get a camera object to provide control commands on the view.
     *
     * @return a Camera instance
     */
    Camera getCamera();

    /**
     * Search for the first node or sprite (in that order) that contains the
     * point at coordinates (x, y).
     *
     * @param x The point abscissa.
     * @param y The point ordinate.
     * @return The first node or sprite at the given coordinates or null if
     * nothing found.
     */
    GraphicElement findNodeOrSpriteAt(double x, double y);

    /**
     * Search for all the nodes and sprites contained inside the rectangle
     * (x1,y1)-(x2,y2).
     *
     * @param x1 The rectangle lowest point abscissa.
     * @param y1 The rectangle lowest point ordinate.
     * @param x2 The rectangle highest point abscissa.
     * @param y2 The rectangle highest point ordinate.
     * @return The set of sprites and nodes in the given rectangle.
     */
    Collection<GraphicElement> allNodesOrSpritesIn(double x1, double y1, double x2, double y2);

    /**
     * Redisplay or update the view contents. Called by the Viewer.
     *
     * @param graph        The graphic graph to represent.
     * @param graphChanged True if the graph changed since the last call to this method.
     */
    void display(GraphicGraph graph, boolean graphChanged);

    /**
     * Close definitively this view. Called by the Viewer.
     *
     * @param graph The graphic graph.
     */
    void close(GraphicGraph graph);


    /**
     * Called by the mouse manager to specify where a node and sprite selection
     * started.
     *
     * @param x1 The selection start abscissa.
     * @param y1 The selection start ordinate.
     */
    void beginSelectionAt(double x1, double y1);

    /**
     * The selection already started grows toward position (x, y).
     *
     * @param x The new end selection abscissa.
     * @param y The new end selection ordinate.
     */
    void selectionGrowsAt(double x, double y);

    /**
     * Called by the mouse manager to specify where a node and spite selection
     * stopped.
     *
     * @param x2 The selection stop abscissa.
     * @param y2 The selection stop ordinate.
     */
    void endSelectionAt(double x2, double y2);

    /**
     * Freeze an element so that the optional layout cannot move it.
     *
     * @param element The element.
     * @param frozen  If true the element cannot be moved automatically.
     */
    void freezeElement(GraphicElement element, boolean frozen);

    /**
     * Force an element to move at the given location in pixels.
     *
     * @param element The element.
     * @param x       The requested position abscissa in pixels.
     * @param y       The requested position ordinate in pixels.
     */
    void moveElementAtPx(GraphicElement element, double x, double y);

    /**
     * Change the manager for mouse events on this view. If the value for the new manager is
     * null, a default manager is installed. The {@link org.graphstream.ui.view.util.MouseManager#init(org.graphstream.ui.graphicGraph.GraphicGraph, View)}
     * method must not yet have been called.
     *
     * @param manager The new manager, or null to set the default manager.
     * @see org.graphstream.ui.view.util.MouseManager
     */
    void setMouseManager(MouseManager manager);

    /**
     * Change the manager for key and shortcuts events on this view. If the value for the new
     * manager is null, a default manager is installed. The {@link org.graphstream.ui.view.util.ShortcutManager#init(org.graphstream.ui.graphicGraph.GraphicGraph, View)}
     * method must not yet have been called.
     *
     * @param manager The new manager, or null to set the default manager
     * @see org.graphstream.ui.view.util.ShortcutManager
     */
    void setShortcutManager(ShortcutManager manager);

    /**
     * Request ui focus.
     */
    void requestFocus();

    /**
     * Add key ui listener.
     *
     * @param l the listener
     */
    void addKeyListener(KeyListener l);

    /**
     * Remove key ui listener.
     *
     * @param l the listener
     */
    void removeKeyListener(KeyListener l);

    /**
     * Add mouse ui listener.
     *
     * @param l the listener
     */
    void addMouseListener(MouseListener l);

    /**
     * Remove mouse ui listener.
     *
     * @param l the listener
     */
    void removeMouseListener(MouseListener l);

    /**
     * Add mouse motion ui listener.
     *
     * @param l the listener
     */
    void addMouseMotionListener(MouseMotionListener l);

    /**
     * Remove mouse motion ui listener.
     *
     * @param l the listener
     */
    void removeMouseMotionListener(MouseMotionListener l);
}
