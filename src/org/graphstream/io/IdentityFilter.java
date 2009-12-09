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
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.io;

import org.graphstream.graph.GraphEvent;
import org.graphstream.graph.GraphEvent.AttributeEvent;
import org.graphstream.graph.GraphEvent.EdgeEvent;
import org.graphstream.graph.GraphEvent.GraphStepEvent;
import org.graphstream.graph.GraphEvent.NodeEvent;

/**
 * A base filter that do not filter anything.
 *
 * <p>
 * This filter does nothing and let all events pass. It can be used as a base
 * to implement more specific filters by refining some of its methods.
 * </p>
 * 
 * <p>
 * Another use of this filter is to duplicate a stream of events from one
 * input toward several outputs.
 * </p>
 */
public class IdentityFilter 
	extends SourceBase
	implements Pipe
{

	public void edgeAttributeAdded(AttributeEvent e)
	{
		appendEvent(e);
	}

	public void edgeAttributeChanged(AttributeEvent e)
	{
		appendEvent(e);
	}

	public void edgeAttributeRemoved(AttributeEvent e)
	{
		appendEvent(e);
	}

	public void graphAttributeAdded(AttributeEvent e)
	{
		appendEvent(e);
	}

	public void graphAttributeChanged(AttributeEvent e)
	{
		appendEvent(e);
	}

	public void graphAttributeRemoved(AttributeEvent e)
	{
		appendEvent(e);
	}

	public void nodeAttributeAdded(AttributeEvent e)
	{
		appendEvent(e);
	}

	public void nodeAttributeChanged(AttributeEvent e)
	{
		appendEvent(e);
	}

	public void nodeAttributeRemoved(AttributeEvent e)
	{
		appendEvent(e);
	}

	public void edgeAdded(EdgeEvent e)
	{
		appendEvent(e);
	}

	public void edgeRemoved(EdgeEvent e)
	{
		appendEvent(e);
	}

	public void graphCleared(GraphEvent e)
	{
		appendEvent(e);
	}

	public void nodeAdded(NodeEvent e)
	{
		appendEvent(e);
	}

	public void nodeRemoved(NodeEvent e)
	{
		appendEvent(e);
	}

	public void stepBegins(GraphStepEvent e)
	{
		appendEvent(e);
	}

}