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
 * Implementation of the {@link org.miv.graphstream.ui.GraphViewer} using Swing and
 * the Java2D API.
 * 
 * <p>
 * This implementation is the reference implementation. It respects the full style sheet
 * specification and provides a sprite renderer.
 * </p>
 * 
 * TODO: this renderer is not really object-oriented. The rendering of edges and
 *  node should be in the edge and node classes. For edge arrow there should exist
 *  an arrow class, etc. Maybe for the next release...
 */
package org.miv.graphstream.ui.swing;