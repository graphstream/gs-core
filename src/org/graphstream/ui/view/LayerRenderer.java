/*
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

/**
 * @since 2010-02-18
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.ui.view;

import org.graphstream.ui.graphicGraph.GraphicGraph;

/**
 * A specific rendering class that can be plugged in any view and is called to
 * draw under or above the graph.
 * 
 * @see org.graphstream.ui.view.View#setForeLayoutRenderer(LayerRenderer)
 * @see org.graphstream.ui.view.View#setBackLayerRenderer(LayerRenderer)
 */
public interface LayerRenderer<G> {
	/**
	 * Render something under or above the graph.
	 * 
	 * @param graphics
	 *            The graphics module used to draw (Graphics2D for example, when
	 *            using Swing).
	 * @param graph
	 *            The graphic representation of the graph.
	 * @param px2Gu
	 *            The ratio to pass from pixels to graph units.
	 * @param widthPx
	 *            The width in pixels of the view port.
	 * @param heightPx
	 *            The height in pixels of the view port.
	 * @param minXGu
	 *            The minimum visible point abscissa of the graph in graph units.
	 * @param minYGu
	 *            The minimum visible point ordinate of the graph in graph units.
	 * @param maxXGu
	 *            The maximum visible point abscissa of the graph in graph units.
	 * @param maxYGu
	 *            The maximum visible point ordinate of the graph in graph units.
	 */
	void render(G graphics, GraphicGraph graph, double px2Gu, int widthPx, int heightPx, double minXGu, double minYGu,
			double maxXGu, double maxYGu);
}
