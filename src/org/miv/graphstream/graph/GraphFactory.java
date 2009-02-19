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
 * An interface aimed at dynamically creating graph objects based on a class name.
 *
 * @author Antoine Dutot
 * @author Yoann Pigné
 * @since september 2007
 */
public class GraphFactory
{
	/**
	 * Create a new instance of graph.
	 */
	public GraphFactory()
	{
	}

	/**
	 * Instantiate a new graph from the given class name.
	 * @return A graph instance or null if the graph class was not found.
	 */
	public Graph newInstance( String id, String graphClass )
	{
		try
		{
			String completeGraphClass ;
			if(graphClass.split("[.]").length < 2) {
				completeGraphClass = "org.miv.graphstream.graph.implementations."+graphClass ;
			}
			else {
				completeGraphClass = graphClass ;
			}
			Graph res = (Graph) Class.forName( completeGraphClass ).newInstance();
			res.setId( id );
			return res;
		}
		catch( InstantiationException e )
		{
			System.out.println( "GraphFactory newInstance InstantiationException : "
			        + e.getMessage() );
		}
		catch( ClassNotFoundException e )
		{
			System.out.println( "GraphFactory newInstance ClassNotFoundException : "
			        + e.getMessage() );
		}
		catch( IllegalAccessException e )
		{
			System.out.println( "GraphFactory newInstance IllegalAccessException : "
			        + e.getMessage() );
		}

		return null;
	}
}