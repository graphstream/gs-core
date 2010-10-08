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
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.ui.swingViewer.util;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.event.MouseInputListener;

import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.swingViewer.View;

/**
 * A global behaviour for all mouse events on graphic elements.
 */
public class MouseManager implements MouseInputListener {
	// Attribute

	/**
	 * The view this manager operates upon.
	 */
	protected View view;

	/**
	 * The graph to modify according to the view actions.
	 */
	protected GraphicGraph graph;

	// Construction

	/**
	 * New mouse manager on the given view.
	 * 
	 * @param graph
	 *            The graph to control.
	 * @param view
	 *            The view to control.
	 */
	public MouseManager(GraphicGraph graph, View view) {
		this.view = view;
		this.graph = graph;
	}

	// Command

	protected void mouseButtonPress(MouseEvent event) {
		view.requestFocus();

		// Unselect all.

		if (!event.isShiftDown()) {
			for (Node node : graph) {
				if (node.hasAttribute("ui.selected"))
					node.removeAttribute("ui.selected");
			}

			for (GraphicSprite sprite : graph.spriteSet()) {
				if (sprite.hasAttribute("ui.selected"))
					sprite.removeAttribute("ui.selected");
			}
		}
	}

	protected void mouseButtonRelease(MouseEvent event,
			ArrayList<GraphicElement> elementsInArea) {
		for (GraphicElement element : elementsInArea) {
			if (!element.hasAttribute("ui.selected"))
				element.addAttribute("ui.selected");
		}
	}

	protected void mouseButtonPressOnElement(GraphicElement element,
			MouseEvent event) {
		if (event.getButton() == 3)
			element.addAttribute("ui.selected");
		else
			element.addAttribute("ui.clicked");
	}

	protected void elementMoving(GraphicElement element, MouseEvent event) {
		view.moveElementAtPx(element, event.getX(), event.getY());
	}

	protected void mouseButtonReleaseOffElement(GraphicElement element,
			MouseEvent event) {
		if (event.getButton() != 3)
			element.removeAttribute("ui.clicked");
	}

	// Mouse Listener

	protected GraphicElement curElement;

	protected float x1, y1;

	public void mouseClicked(MouseEvent event) {
		// NOP
	}

	public void mousePressed(MouseEvent event) {
		curElement = view.findNodeOrSpriteAt(event.getX(), event.getY());

		if (curElement != null) {
			mouseButtonPressOnElement(curElement, event);
		} else {
			x1 = event.getX();
			y1 = event.getY();
			mouseButtonPress(event);
			view.beginSelectionAt(x1, y1);
		}
	}

	public void mouseDragged(MouseEvent event) {
		if (curElement != null) {
			elementMoving(curElement, event);
		} else {
			view.selectionGrowsAt(event.getX(), event.getY());
		}
	}

	public void mouseReleased(MouseEvent event) {
		if (curElement != null) {
			mouseButtonReleaseOffElement(curElement, event);
			curElement = null;
		} else {
			float x2 = event.getX();
			float y2 = event.getY();
			float t;

			if (x1 > x2) {
				t = x1;
				x1 = x2;
				x2 = t;
			}
			if (y1 > y2) {
				t = y1;
				y1 = y2;
				y2 = t;
			}

			mouseButtonRelease(event, view.allNodesOrSpritesIn(x1, y1, x2, y2));
			view.endSelectionAt(x2, y2);
		}
	}

	public void mouseEntered(MouseEvent event) {
		// NOP
	}

	public void mouseExited(MouseEvent event) {
		// NOP
	}

	public void mouseMoved(MouseEvent e) {
	}
}