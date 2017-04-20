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
package org.graphstream.ui.view.util;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.view.View;

import java.awt.event.MouseEvent;
import java.util.EnumSet;

public class DefaultMouseManager implements MouseManager
{
	// Attribute

	/**
	 * The view this manager operates upon.
	 */
	protected View view;

	/**
	 * The graph to modify according to the view actions.
	 */
	protected GraphicGraph graph;

	final private EnumSet<InteractiveElement> types;

	// Construction

	public DefaultMouseManager() {
		this(EnumSet.of(InteractiveElement.NODE,InteractiveElement.SPRITE));
	}

	public DefaultMouseManager(EnumSet<InteractiveElement> types) {
		this.types = types;
	}

	public void init(GraphicGraph graph, View view) {
		this.view = view;
		this.graph = graph;
		view.addMouseListener(this);
		view.addMouseMotionListener(this);
	}

	@Override
	public EnumSet<InteractiveElement> getManagedTypes() {
		return types;
	}

	public void release() {
		view.removeMouseListener(this);
		view.removeMouseMotionListener(this);
	}

	// Command

	protected void mouseButtonPress(MouseEvent event) {
		view.requestFocus();

		// Unselect all.

		if (!event.isShiftDown()) {
		    if (types.contains(InteractiveElement.NODE)) {
				for (Node node : graph) {
					if (node.hasAttribute("ui.selected"))
						node.removeAttribute("ui.selected");
				}
			}

			if (types.contains(InteractiveElement.SPRITE)) {
				for (GraphicSprite sprite : graph.spriteSet()) {
					if (sprite.hasAttribute("ui.selected"))
						sprite.removeAttribute("ui.selected");
				}
			}

			if (types.contains(InteractiveElement.EDGE)) {
				for (Edge edge : graph.getEdgeSet()) {
					if (edge.hasAttribute("ui.selected"))
						edge.removeAttribute("ui.selected");
				}
			}
		}
	}

	protected void mouseButtonRelease(MouseEvent event,
			Iterable<GraphicElement> elementsInArea) {
		for (GraphicElement element : elementsInArea) {
			if (!element.hasAttribute("ui.selected"))
				element.addAttribute("ui.selected");
		}
	}

	protected void mouseButtonPressOnElement(GraphicElement element,
			MouseEvent event) {
		view.freezeElement(element, true);
		if (event.getButton() == 3) {
			element.addAttribute("ui.selected");
		} else {
			element.addAttribute("ui.clicked");
		}
	}

	protected void elementMoving(GraphicElement element, MouseEvent event) {
		view.moveElementAtPx(element, event.getX(), event.getY());
	}

	protected void mouseButtonReleaseOffElement(GraphicElement element,
			MouseEvent event) {
		view.freezeElement(element, false);
		if (event.getButton() != 3) {
			element.removeAttribute("ui.clicked");
		} else {
		}
	}

	// Mouse Listener

	protected GraphicElement curElement;

	protected float x1, y1;

	public void mouseClicked(MouseEvent event) {
		// NOP
	}

	public void mousePressed(MouseEvent event) {
		curElement = view.findGraphicElementAt(types,event.getX(), event.getY());

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

			mouseButtonRelease(event, view.allGraphicElementsIn(types,x1, y1, x2, y2));
			view.endSelectionAt(x2, y2);
		}
	}

	public void mouseEntered(MouseEvent event) {
		// NOP
	}

	public void mouseExited(MouseEvent event) {
		// NOP
	}

	public void mouseMoved(MouseEvent event) {
		// NOP
	}
}