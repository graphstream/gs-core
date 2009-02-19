/*
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.miv.graphstream.graph;

/**
 * A void implementation of <code>org.miv.graphstream.graph.GraphListener</code>.
 * 
 * Inherit the class and override some of its methods so as to handle some of the services of the
 * <code>GraphListener</code> interface.
 * 
 * @author Yoann Pigné
 * @author Antoine Dutot
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
