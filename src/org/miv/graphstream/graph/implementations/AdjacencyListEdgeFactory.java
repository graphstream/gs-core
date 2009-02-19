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

/**
* A class aimed at dynamicaly creating edge objects based on a class name. 
* All created object must extend the {@link AdjacencyListEdge} class.
* 
* @author Antoine Dutot
* @author Yoann Pigné
* @since september 2007
*/
public class AdjacencyListEdgeFactory implements EdgeFactory
{
	Class<?> edgeClass;

	Class<?> baseEdgeclass;

	AdjacencyListEdgeFactory()
	{
		this( org.miv.util.Environment.getGlobalEnvironment().getParameter( "edgeClass" ) );
	}

	AdjacencyListEdgeFactory( org.miv.util.Environment environment )
	{
		this( environment.getParameter( "edgeClass" ) );
	}

	AdjacencyListEdgeFactory( String cnfs )
	{
		try
		{
			baseEdgeclass = Class.forName( "org.miv.graphstream.graph.implementations.AdjacencyListEdge" );
		}
		catch( ClassNotFoundException e )
		{
			e.printStackTrace();
		}

		setEdgeClass( cnfs );
	}

	/**
	 * Modifies the name of the class to be used to create new edges.
	 * @param cnfs full qualified name of the class.
	 */
	@SuppressWarnings("unchecked")
	public void setEdgeClass( String cnfs )
	{
		try
		{
			if( cnfs != null && !cnfs.equals( "" ) )
			{
				Class cl = Class.forName( cnfs );
				if( baseEdgeclass.isAssignableFrom( cl ) )
				{
					edgeClass = cl;
				}
				else
				{
					System.err.printf( "Not able to use \"%s\" to create edges. "
							+ "You must use a class that extends \"AdjacencyListEdge\" (paramter: \"edgeClass\")%n", cnfs );
				}
			}
			if( edgeClass == null )
				edgeClass = baseEdgeclass;

		}
		catch( ClassNotFoundException e )
		{
			System.err.printf( "Unable to use \"%s\" to create edges. Not Found in the classpath. %n", cnfs );
			if( edgeClass == null )
				edgeClass = baseEdgeclass;
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.miv.graphstream.graph.EdgeFactory#newInstance()
	 */
	public Edge newInstance()
	{
		Edge n = null;
		try
		{
			n = (Edge) edgeClass.newInstance();
		}
		catch( InstantiationException e )
		{
			System.err.printf( "Unable to instantiate class\"%s\". It probably contains no void constructor? %n%n", edgeClass.getName() );
			System.exit( -1 );
		}
		catch( IllegalAccessException e )
		{
			System.err.printf( "Unable to instantiate class\"%s\". Is it publically accessible? %n%n", edgeClass.getName() );
		}
		return n;
	}

}
