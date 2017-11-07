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
package org.graphstream.ui.fxViewer;

import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
/**
 * A view on a graphic graph.
 * 
 * Basically a view is a Swing panel where a
 * {@link org.graphstream.ui.view.GraphRenderer} renders the graphic graph. If
 * you are in the Swing thread, you can change the view on the graphic graph
 * using methods to translate, zoom and rotate the view.
 */
public abstract class FxViewPanel extends StackPane implements View {

	/**
	 * The view identifier.
	 */
	private final String id;
	
	private Canvas canvas ;
	/**
	 * New view.
	 *
	 * @param identifier
	 *            The view unique identifier.
	 */
	public FxViewPanel(final String identifier) {

		if (null == identifier || identifier.isEmpty()) {
			throw new IllegalArgumentException("View id cannot be null/empty.");
		}
		id = identifier;
	
		canvas = new Canvas(800, 600);
    	
		this.getChildren().add(canvas);
		this.setPrefSize(800, 600);
		
		canvas.widthProperty().bind(
        		this.widthProperty());
		canvas.heightProperty().bind(
        		this.heightProperty());
	}

	public String getIdView() {
		return id;
	}
	
	public GraphicsContext getGraphics() {
		return canvas.getGraphicsContext2D();
	}
	
	public void setAliasing(boolean ali) {
		
	}
	
	public abstract Viewer getViewer();

	@Override
	protected abstract void layoutChildren() ;
}