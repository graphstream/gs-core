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

package org.miv.graphstream.graph;

/**
 * A void implementation of <code>org.miv.graphstream.graph.GraphListener</code>.
 * 
 * Inherit the class and override some of its methods so as to handle some of the services of the
 * <code>GraphListener</code> interface.
 * 
 * @since 2008/08/01
 * @see org.miv.graphstream.graph.GraphListener
 * 
 */
public class GraphListenerHelper implements GraphListener
{

	public void afterEdgeAdd(Graph graph, Edge edge)
	{
	}

	public void afterNodeAdd(Graph graph, Node node)
	{
	}

	public void attributeChanged(Element element, String attribute, Object oldValue, Object newValue)
	{
	}

	public void beforeEdgeRemove(Graph graph, Edge edge)
	{
	}

	public void beforeGraphClear(Graph graph)
	{
	}

	public void beforeNodeRemove(Graph graph, Node node)
	{
	}

	public void stepBegins(Graph graph, double time)
	{
	}

}
