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

import org.graphstream.ui.view.GraphRendererBase;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region ;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;


public abstract class FxGraphRendererBase extends GraphRendererBase<Region, GraphicsContext>
		implements FxGraphRenderer {

	// Utilities

	public View createDefaultView(Viewer viewer, String viewId) {
		return new FxDefaultView(viewer, viewId, this);
	}

	protected void displayNothingToDo(GraphicsContext g, int w, int h) {
		String msg1 = "Graph width/height/depth is zero !!";
		String msg2 = "Place components using the 'xyz' attribute.";

		g.setStroke(Color.RED);
		g.setFill(Color.RED);
		g.strokeLine(0, 0, w, h);
		g.strokeLine(0, h, w, 0);
		
		final Text text1 = new Text(msg1);
		final Text text2 = new Text(msg2);
		text1.applyCss();
		text2.applyCss();
		
		double msg1length = text1.getLayoutBounds().getWidth();
		double msg2length = text2.getLayoutBounds().getWidth();
		
		double x = w / 2;
		double y = h / 2;

		g.setStroke(Color.RED);
		g.setFill(Color.RED);
		g.fillText(msg1, (float) (x - msg1length / 2), (float) (y - 20));
		g.fillText(msg2, (float) (x - msg2length / 2), (float) (y + 20));
	}

}
