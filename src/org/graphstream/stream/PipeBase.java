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
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.stream;

/**
 * A base pipe that merely let all events pass.
 *
 * <p>
 * This pipe does nothing and let all events pass. It can be used as a base
 * to implement more specific filters by refining some of its methods.
 * </p>
 * 
 * <p>
 * Another use of this pipe is to duplicate a stream of events from one
 * input toward several outputs.
 * </p>
 */
public class PipeBase extends SourceBase implements Pipe
{
	public void edgeAttributeAdded( String graphId, long timeId, String edgeId, String attribute, Object value )
    {
		sendEdgeAttributeAdded( graphId, timeId, edgeId, attribute, value );
    }

	public void edgeAttributeChanged( String graphId, long timeId, String edgeId, String attribute,
            Object oldValue, Object newValue )
    {
		sendEdgeAttributeChanged( graphId, timeId, edgeId, attribute, oldValue, newValue );
    }

	public void edgeAttributeRemoved( String graphId, long timeId, String edgeId, String attribute )
    {
		sendEdgeAttributeRemoved( graphId, timeId, edgeId, attribute );
    }

	public void graphAttributeAdded( String graphId, long timeId, String attribute, Object value )
    {
		sendGraphAttributeAdded( graphId, timeId, attribute, value );
    }

	public void graphAttributeChanged( String graphId, long timeId, String attribute, Object oldValue,
            Object newValue )
    {
		sendGraphAttributeChanged( graphId, timeId, attribute, oldValue, newValue );
    }

	public void graphAttributeRemoved( String graphId, long timeId, String attribute )
    {
		sendGraphAttributeRemoved( graphId, timeId, attribute );
    }

	public void nodeAttributeAdded( String graphId, long timeId, String nodeId, String attribute, Object value )
    {
		sendNodeAdded( graphId, timeId, nodeId );
    }

	public void nodeAttributeChanged( String graphId, long timeId, String nodeId, String attribute,
            Object oldValue, Object newValue )
    {
		sendNodeAttributeChanged( graphId, timeId, nodeId, attribute, oldValue, newValue );
    }

	public void nodeAttributeRemoved( String graphId, long timeId, String nodeId, String attribute )
    {
		sendNodeAttributeRemoved( graphId, timeId, nodeId, attribute );
    }

	public void edgeAdded( String graphId, long timeId, String edgeId, String fromNodeId, String toNodeId,
            boolean directed )
    {
		sendEdgeAdded( graphId, timeId, edgeId, fromNodeId, toNodeId, directed );
    }

	public void edgeRemoved( String graphId, long timeId, String edgeId )
    {
		sendEdgeRemoved( graphId, timeId, edgeId );
    }

	public void graphCleared( String graphId, long timeId )
    {
		sendGraphCleared( graphId, timeId );
    }

	public void nodeAdded( String graphId, long timeId, String nodeId )
    {
		sendNodeAdded( graphId, timeId, nodeId );
    }

	public void nodeRemoved( String graphId, long timeId, String nodeId )
    {
		sendNodeRemoved( graphId, timeId, nodeId );
    }

	public void stepBegins( String graphId, long timeId, double step )
    {
		sendStepBegins( graphId, timeId, step );
    }
}