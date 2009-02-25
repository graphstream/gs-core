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
package org.miv.graphstream.graph.implementations;

import org.miv.graphstream.graph.Edge;
import org.miv.graphstream.graph.EdgeFactory;
import org.miv.graphstream.graph.Node;

/**
* A class aimed at dynamically creating edge objects based on a class name. 
* All created object must extend the {@link DefaultEdge} class.
* 
* @author Antoine Dutot
* @author Yoann Pign�
* @since September 2007
*/
public class DefaultEdgeFactory
	implements EdgeFactory
{
	public DefaultEdgeFactory()
	{
	}

	public Edge newInstance( String id, Node src, Node dst )
	{
		return new SingleEdge(id,src,dst);
	}
}