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
public class IdentityFilter extends SourceBase implements Pipe
{
	public void edgeAttributeAdded( String graphId, String edgeId, String attribute, Object value )
    {
		sendEdgeAttributeAdded( graphId, edgeId, attribute, value );
    }

	public void edgeAttributeChanged( String graphId, String edgeId, String attribute,
            Object oldValue, Object newValue )
    {
		sendEdgeAttributeChanged( graphId, edgeId, attribute, oldValue, newValue );
    }

	public void edgeAttributeRemoved( String graphId, String edgeId, String attribute )
    {
		sendEdgeAttributeRemoved( graphId, edgeId, attribute );
    }

	public void graphAttributeAdded( String graphId, String attribute, Object value )
    {
		sendGraphAttributeAdded( graphId, attribute, value );
    }

	public void graphAttributeChanged( String graphId, String attribute, Object oldValue,
            Object newValue )
    {
		sendGraphAttributeChanged( graphId, attribute, oldValue, newValue );
    }

	public void graphAttributeRemoved( String graphId, String attribute )
    {
		sendGraphAttributeRemoved( graphId, attribute );
    }

	public void nodeAttributeAdded( String graphId, String nodeId, String attribute, Object value )
    {
		sendNodeAdded( graphId, nodeId );
    }

	public void nodeAttributeChanged( String graphId, String nodeId, String attribute,
            Object oldValue, Object newValue )
    {
		sendNodeAttributeChanged( graphId, nodeId, attribute, oldValue, newValue );
    }

	public void nodeAttributeRemoved( String graphId, String nodeId, String attribute )
    {
		sendNodeAttributeRemoved( graphId, nodeId, attribute );
    }

	public void edgeAdded( String graphId, String edgeId, String fromNodeId, String toNodeId,
            boolean directed )
    {
		sendEdgeAdded( graphId, edgeId, fromNodeId, toNodeId, directed );
    }

	public void edgeRemoved( String graphId, String edgeId )
    {
		sendEdgeRemoved( graphId, edgeId );
    }

	public void graphCleared( String graphId )
    {
		sendGraphCleared( graphId );
    }

	public void nodeAdded( String graphId, String nodeId )
    {
		sendNodeAdded( graphId, nodeId );
    }

	public void nodeRemoved( String graphId, String nodeId )
    {
		sendNodeRemoved( graphId, nodeId );
    }

	public void stepBegins( String graphId, double time )
    {
		sendStepBegins( graphId, time );
    }
}