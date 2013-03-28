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

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;

import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.StyleGroupListener;

public abstract class GraphRendererBase implements GraphRenderer,
		StyleGroupListener {
	// Attribute

	/**
	 * The graph to draw.
	 */
	protected GraphicGraph graph;

	/**
	 * Current selection or null.
	 */
	protected Selection selection = null;

	/**
	 * The surface we are rendering on (used only
	 */
	protected Container renderingSurface;

	// Initialisation

	public void open(GraphicGraph graph, Container renderingSurface) {
		if (this.graph != null)
			throw new RuntimeException(
					"renderer already open, cannot open twice");

		this.graph = graph;
		this.renderingSurface = renderingSurface;

		this.graph.getStyleGroups().addListener(this);
	}

	public void close() {
		if (graph != null) {
			graph.getStyleGroups().removeListener(this);
			graph = null;
		}
	}

	// Access

	public Container getRenderingSurface() {
		return renderingSurface;
	}

	// Selection

	public void beginSelectionAt(double x1, double y1) {
		if (selection == null)
			selection = new Selection();

		selection.x1 = x1;
		selection.y1 = y1;
		selection.x2 = x1;
		selection.y2 = y1;
	}

	public void selectionGrowsAt(double x, double y) {
		selection.x2 = x;
		selection.y2 = y;
	}

	public void endSelectionAt(double x2, double y2) {
		selection = null;
	}

	// Utilities

	protected void displayNothingToDo(Graphics2D g, int w, int h) {
		String msg1 = "Graph width/height/depth is zero !!";
		String msg2 = "Place components using the 'xyz' attribute.";

		g.setColor(Color.RED);
		g.drawLine(0, 0, w, h);
		g.drawLine(0, h, w, 0);

		double msg1length = g.getFontMetrics().stringWidth(msg1);
		double msg2length = g.getFontMetrics().stringWidth(msg2);

		double x = w / 2;
		double y = h / 2;

		g.setColor(Color.BLACK);
		g.drawString(msg1, (float)(x - msg1length / 2), (float)(y - 20));
		g.drawString(msg2, (float)(x - msg2length / 2), (float)(y + 20));
	}
}