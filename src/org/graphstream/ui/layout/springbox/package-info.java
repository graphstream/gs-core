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
 * Spring-based algorithm to layout a graph (deprecated, use the elastic box).
 * 
 * <p>
 * The spring layout follow these two simple rules:
 * <ul>
 * 		<li>Each node repulse all other nodes;</li>
 * 		<li>Nodes that are linked by an edge, attract themselves.</li>
 * </ul>
 * The general effect of these rules is unfold the graph in often pleasing and
 * easily visualizable ways. It has interesting properties:
 * <ul>
 * 		<li>If the graph is symetric, this symetry is shown;</li>
 * 		<li>The layout works as well in 2D and 3D.</li>
 * </ul>
 * </p>
 */
package org.graphstream.ui.layout.springbox;