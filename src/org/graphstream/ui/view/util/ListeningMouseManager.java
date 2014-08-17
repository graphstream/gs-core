package org.graphstream.ui.view.util;

import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.view.View;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * a mouse manager that notifies listeners mouse user-interface events
 * <p/>
 * User: bowen
 * Date: 8/14/14
 */
public class ListeningMouseManager implements MouseManager
{
    /**
     * the view this manager is tied to
     */
    private View view;

    /**
     * the graph context
     */
    private GraphicGraph graph;

    /**
     * mouse listener set
     */
    private final Set<MouseListener> listeners = new CopyOnWriteArraySet<>();

    /**
     * the set of selected elements
     */
    private final Map<String, GraphicElement> selectedElements = new TreeMap<>();

    /**
     * the currently selected/active elements
     */
    private GraphicElement activeElement;

    /**
     * the mouse [x,y] value used for selection start position
     */
    protected Point2D selectionStart = null;


    public boolean addListener(final MouseListener l)
    {
        if (null == l)
        {
            return false;
        }
        return this.listeners.add(l);
    }


    public boolean removeListener(final MouseListener l)
    {
        if (null == l)
        {
            return false;
        }
        return this.listeners.remove(l);
    }


    @Override
    public void init(final GraphicGraph graph, final View view)
    {
        this.view = view;
        this.graph = graph;
        view.addMouseListener(this);
        view.addMouseMotionListener(this);
    }


    @Override
    public void release()
    {
        view.removeMouseListener(this);
        view.removeMouseMotionListener(this);
    }


    @Override
    public void mouseClicked(final MouseEvent event)
    {
        if (event.isPopupTrigger())
        {
            this.firePopup();
        }
    }


    @Override
    public void mousePressed(final MouseEvent event)
    {
        final MouseListener.Button button = MouseListener.Button.fromSwing(event);
        final boolean multiSelect = event.isShiftDown();
        final boolean toggleSelect = event.isControlDown() || event.isMetaDown();
        this.handleMousePressed(button, multiSelect, toggleSelect, event.getX(), event.getY());
    }


    @Override
    public void mouseReleased(final MouseEvent event)
    {
        this.handleMouseReleased(event.getX(), event.getY());
    }


    @Override
    public void mouseDragged(final MouseEvent event)
    {
        final MouseListener.Button button = MouseListener.Button.fromSwing(event);
        this.handleMouseDragged(button, event.getX(), event.getY());
    }


    @Override
    public void mouseEntered(final MouseEvent event)
    {

    }


    @Override
    public void mouseExited(final MouseEvent event)
    {

    }


    @Override
    public void mouseMoved(final MouseEvent e)
    {

    }


    private void handleMousePressed(final MouseListener.Button button, final boolean multiSelect, final boolean toggleSelect, final double x, final double y)
    {
        this.clear();
        this.activeElement = this.view.findNodeOrSpriteAt(x, y);

        if (this.activeElement != null)
        {
            // user clicked on specific element - determine if this is part of extended selection or click event
            view.freezeElement(this.activeElement, true);
            if (MouseListener.Button.LEFT.equals(button))
            {
                if (multiSelect)
                {
                    this.selectElement(this.activeElement, true);
                }
                else if (toggleSelect)
                {
                    final String id = this.findNode(this.activeElement);
                    if (id != null)
                    {
                        final boolean selected = this.selectedElements.containsKey(id);
                        this.selectElement(this.activeElement, !selected);
                    }
                }
                else
                {
                    this.unselectAll();
                    this.selectElement(this.activeElement, true);
                    this.clickElement(this.activeElement, true);
                }
            }
        }
        else
        {
            // user clicked on empty space - start selection
            this.view.requestFocus();
            if (MouseListener.Button.LEFT.equals(button))
            {
                this.selectionStart = new Point2D.Double(x, y);
                if (!multiSelect && !toggleSelect)
                {
                    this.unselectAll();
                }
                this.view.beginSelectionAt(this.selectionStart.getX(), this.selectionStart.getY());
            }
        }
    }


    private void handleMouseReleased(final double x, final double y)
    {
        if (this.activeElement != null)
        {
            // handle element selection or click
            this.view.freezeElement(this.activeElement, false);
            this.clickElement(this.activeElement, false);
            this.activeElement = null;
        }

        if (this.selectionStart != null)
        {
            // get selection bounds
            double x1 = this.selectionStart.getX();
            double y1 = this.selectionStart.getY();
            double x2 = x;
            double y2 = y;
            if (x1 > x2)
            {
                // swap
                final double t = x1;
                x1 = x2;
                x2 = t;
            }
            if (y1 > y2)
            {
                // swap
                final double t = y1;
                y1 = y2;
                y2 = t;
            }

            // select elements in area
            for (final GraphicElement element : view.allNodesOrSpritesIn(x1, y1, x2, y2))
            {
                this.selectElement(element, true);
            }
            this.view.endSelectionAt(x2, y2);

            // reset context
            this.clear();
        }
    }


    private void handleMouseDragged(final MouseListener.Button button, final double x, final double y)
    {
        if (MouseListener.Button.LEFT.equals(button) && this.activeElement != null)
        {
            this.view.moveElementAtPx(this.activeElement, x, y);
        }
        if (this.selectionStart != null)
        {
            this.view.selectionGrowsAt(x, y);
        }
    }


    private void clear()
    {
        this.activeElement = null;
        this.selectionStart = null;
    }


    private void clickElement(final GraphicElement element, final boolean clicked)
    {
        if (null == element)
        {
            return;
        }

        if (clicked)
        {
            element.addAttribute("ui.clicked");
        }
        else
        {
            element.removeAttribute("ui.clicked");
        }
    }


    private void selectElement(final GraphicElement element, final boolean selected)
    {
        if (null == element)
        {
            return;
        }

        final String id = this.findNode(element);
        if (null == id)
        {
            return;
        }

        if (selected && !element.hasAttribute("ui.selected"))
        {
            element.addAttribute("ui.selected");
            this.selectedElements.put(id, element);
            this.fireSelected(id, element);
        }
        else if (!selected && element.hasAttribute("ui.selected"))
        {
            element.removeAttribute("ui.selected");
            this.selectedElements.remove(id);
            this.fireUnselected(id, element);
        }
    }


    private void unselectAll()
    {
        for (final Map.Entry<String, GraphicElement> entry : this.selectedElements.entrySet())
        {
            final String id = entry.getKey();
            final GraphicElement element = entry.getValue();
            element.removeAttribute("ui.selected");
            this.fireUnselected(id, element);
        }
        this.selectedElements.clear();
    }


    private String findNode(final GraphicElement element)
    {
        // attempt to get node id from graph element
        if (element instanceof GraphicNode)
        {
            // element id is node id
            return element.getId();
        }
        else if (element instanceof GraphicEdge)
        {
            // edge selection not supported
            return null;
        }
        else if (element instanceof GraphicSprite)
        {
            // get node id from sprite
            final GraphicSprite sprite = (GraphicSprite) element;
            final GraphicNode node = sprite.getNodeAttachment();
            if (null == node)
            {
                return null;
            }
            return node.getId();
        }
        return null;
    }


    private void firePopup()
    {
        for (final MouseListener l : this.listeners)
        {
            l.nodePopup();
        }
    }


    private void fireSelected(final String id, final GraphicElement element)
    {
        if (null == element)
        {
            return;
        }
        if (null == id)
        {
            return;
        }

        for (final MouseListener l : this.listeners)
        {
            l.nodeSelected(id, element);
        }
    }


    private void fireUnselected(final String id, final GraphicElement element)
    {
        if (null == element)
        {
            return;
        }
        if (null == id)
        {
            return;
        }

        for (final MouseListener l : this.listeners)
        {
            l.nodeUnselected(id, element);
        }
    }
}
