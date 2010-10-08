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

package org.graphstream.ui.swingViewer;

import java.awt.Graphics2D;

import org.graphstream.ui.graphicGraph.GraphicGraph;

/**
 * A specific rendering class that can be plugged in any view and is called to
 * draw under or above the graph.
 * 
 * @see View#setForeLayoutRenderer(LayerRenderer)
 * @see View#setBackLayerRenderer(LayerRenderer)
 */
public interface LayerRenderer {
	/**
	 * Render something under or above the graph.
	 * 
	 * @param graphics
	 *            The Swing graphics.
	 * @param graph
	 *            The graphic representation of the graph.
	 * @param px2Gu
	 *            The ratio to pass from pixels to graph units.
	 * @param widthPx
	 *            The width in pixels of the view port.
	 * @param heightPx
	 *            The height in pixels of the view port.
	 * @param minXGu
	 *            The minimum visible point abscissa of the graph in graph
	 *            units.
	 * @param minYGu
	 *            The minimum visible point ordinate of the graph in graph
	 *            units.
	 * @param maxXGu
	 *            The maximum visible point abscissa of the graph in graph
	 *            units.
	 * @param maxYGu
	 *            The maximum visible point ordinate of the graph in graph
	 *            units.
	 */
	void render(Graphics2D graphics, GraphicGraph graph, float px2Gu,
			int widthPx, int heightPx, float minXGu, float minYGu,
			float maxXGu, float maxYGu);
}
