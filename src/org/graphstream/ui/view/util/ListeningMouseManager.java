package org.graphstream.ui.view.util;

import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.View;

import java.awt.event.MouseEvent;
import java.util.Set;
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
     * The view this manager operates upon.
     */
    private View view;

    /**
     * The graph to modify according to the view actions.
     */
    private GraphicGraph graph;

    /**
     * mouse listener set
     */
    private final Set<MouseListener> listeners = new CopyOnWriteArraySet<>();


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
    public void mouseClicked(MouseEvent e)
    {

    }


    @Override
    public void mousePressed(MouseEvent e)
    {

    }


    @Override
    public void mouseReleased(MouseEvent e)
    {

    }


    @Override
    public void mouseEntered(MouseEvent e)
    {

    }


    @Override
    public void mouseExited(MouseEvent e)
    {

    }


    @Override
    public void mouseDragged(MouseEvent e)
    {

    }


    @Override
    public void mouseMoved(MouseEvent e)
    {

    }
}
