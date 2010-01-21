/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

/**
 * Classes of the graph representation used by the graph viewer.
 * 
 * <p>
 * The graphic graph is a {@link org.graphstream.graph.Graph} implementation
 * that also defines position attributes for its nodes and edges ({@link org.graphstream.ui.graphicGraph.GraphicNode}
 * and {@link org.graphstream.ui.graphicGraph.GraphicEdge}). It also
 * defines a style sheet and associates with each node and edge a style element
 * according to the style sheet (or a "style" attribute).
 * </p>
 * 
 * <p>
 * In addition, a sprite manager ({@link org.graphstream.ui.graphicGraph.SpriteManager})
 * and {@link org.graphstream.ui.Sprite} implementation
 * ({@link org.graphstream.ui.graphicGraph.GraphicSprite}) is provided. Sprites are
 * visual or textual elements that can be attached to the graph display to visualise
 * data on the graph.
 * </p>
 */
package org.graphstream.ui2.old.graphicGraph;