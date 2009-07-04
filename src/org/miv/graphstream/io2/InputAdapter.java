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
package org.miv.graphstream.io2;

import org.miv.graphstream.graph.GraphAttributesListener;
import org.miv.graphstream.graph.GraphElementsListener;
import org.miv.graphstream.graph.GraphListener;

/**
 * Adapter for the input interface.
 * 
 * <p>All methods are empty.</p>
 */
public class InputAdapter
	implements Input
{
	public void addGraphAttributesListener(GraphAttributesListener listener) {}
	public void addGraphElementsListener(GraphElementsListener listener) {}
	public void addGraphListener(GraphListener listener) {}
	public void removeGraphAttributesListener(GraphAttributesListener listener) {}
	public void removeGraphElementsListener(GraphElementsListener listener) {}
	public void removeGraphListener(GraphListener listener) {}
}