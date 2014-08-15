package org.graphstream.ui.view.util;

import org.graphstream.graph.Node;

/**
 * a mouse event listener
 * <p/>
 * User: bowen
 * Date: 8/14/14
 */
public interface MouseListener
{
    public enum MouseButton { LEFT, RIGHT, MIDDLE; }
    void nodeClicked(Node node, MouseButton button);
    void nodeSelected(Node node);
}
