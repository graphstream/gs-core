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
 */

/**
 * Classes of the graph representation used by the graph viewer.
 * 
 * <p>
 * The graphic graph is a {@link org.miv.graphstream.graph.Graph} implementation
 * that also defines position attributes for its nodes and edges ({@link org.miv.graphstream.ui.graphicGraph.GraphicNode}
 * and {@link org.miv.graphstream.ui.graphicGraph.GraphicEdge}). It also
 * defines a style sheet and associates with each node and edge a style element
 * according to the style sheet (or a "style" attribute).
 * </p>
 * 
 * <p>
 * In addition, a sprite manager ({@link org.miv.graphstream.ui.graphicGraph.SpriteManager})
 * and {@link org.miv.graphstream.ui.Sprite} implementation
 * ({@link org.miv.graphstream.ui.graphicGraph.GraphicSprite}) is provided. Sprites are
 * visual or textual elements that can be attached to the graph display to visualise
 * data on the graph.
 * </p>
 * 
 * @author Antoine Dutot
 * @author Yoann Pigné
 */
package org.miv.graphstream.ui.graphicGraph;