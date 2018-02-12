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
