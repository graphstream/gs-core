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
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.graph;

/**
 * An interface aimed at dynamically creating graph objects based on a class name.
 *
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
				completeGraphClass = "org.graphstream.graph.implementations."+graphClass ;
			}
			else {
				completeGraphClass = graphClass ;
			}
			//Graph res = (Graph) Class.forName( completeGraphClass ).newInstance();
			//res.setId( id );
			Class<?> clazz = Class.forName( completeGraphClass );
			Graph res = (Graph) clazz.getConstructor(String.class).newInstance(id);
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
		catch( NoSuchMethodException e )
		{
			System.out.println( "GraphFactory newInstance NoSuchMethodException : "
			        + e.getMessage() );
		}
		catch( java.lang.reflect.InvocationTargetException e )
		{
			System.out.println( "GraphFactory newInstance InvocationTargetException : "
			        + e.getMessage() );
		}

		return null;
	}
}