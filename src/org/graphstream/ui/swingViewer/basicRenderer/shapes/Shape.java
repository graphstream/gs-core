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

package org.graphstream.ui.swingViewer.basicRenderer.shapes;

import java.awt.Graphics2D;

import org.graphstream.ui.swingViewer.util.GraphMetrics;

public abstract class Shape {
	/**
	 * Same as calling {@link #renderStroke(Graphics2D,GraphMetrics)} and
	 * {@link #renderFill(Graphics2D,GraphMetrics)} at once.
	 * 
	 * @param g
	 *            The Swing graphics.
	 */
	public void render(Graphics2D g, GraphMetrics metrics) {
		renderStroke(g, metrics);
		renderFill(g, metrics);
	}

	/**
	 * Render the stroke of the shape.
	 * 
	 * @param g
	 *            The Swing graphics.
	 */
	public abstract void renderStroke(Graphics2D g, GraphMetrics metrics);

	/**
	 * Render the filled part of the shape.
	 * 
	 * @param g
	 *            The Swing graphics.
	 */
	public abstract void renderFill(Graphics2D g, GraphMetrics metrics);
}
