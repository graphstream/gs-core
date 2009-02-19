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
* A class aimed at dynamically creating edge objects based on a class name. 
* All created object must extend the {@link DefaultEdge} class.
* 
* @author Antoine Dutot
* @author Yoann Pigné
* @since September 2007
*/
public class DefaultEdgeFactory implements EdgeFactory
{
	Class<?> edgeClass;

	Class<?> baseEdgeclass;

	public static void main( String[] s )
	{
		DefaultEdgeFactory cnf = new DefaultEdgeFactory( "org.miv.graphstream.graph.SingleEdge" );
		System.out.println(cnf.newInstance().getClass().toString());
	}

	DefaultEdgeFactory()
	{
		this( org.miv.util.Environment.getGlobalEnvironment() );
	}

	DefaultEdgeFactory( org.miv.util.Environment environment )
	{
		String cnfs = environment.getParameter( "edgeClass" );
		
		if( cnfs == null || cnfs.length() == 0 )
			cnfs = "org.miv.graphstream.graph.implementations.SingleEdge";

		init( cnfs );
	}

	DefaultEdgeFactory( String cnfs )
	{
		init( cnfs );
	}
	
	protected void init( String cnfs )
	{
		try
		{
			baseEdgeclass = Class.forName( cnfs );
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
			if( cnfs != null  &&  !cnfs.equals("") )
			{
				Class cl = Class.forName( cnfs );

				if( baseEdgeclass.isAssignableFrom( cl ) )
				{
					edgeClass = cl;
				}
				else
				{
					System.err.printf("Not able to use \"%s\" to create edges. " +
							"You must use a class that extends \"DefaultEdge\" (environment parameter: \"edgeClass\")%n", cnfs );
				}
			}
			if( edgeClass == null )
				edgeClass = baseEdgeclass;

		}
		catch( ClassNotFoundException e )
		{
			System.err.printf("Unable to use \"%s\" to create edges. Not Found in the classpath. %n", cnfs );
			if( edgeClass == null )
				edgeClass = baseEdgeclass;
		}

	}

	public Edge newInstance()
	{
		Edge n = null;

		try
		{
			n = (Edge) edgeClass.newInstance();
		}
		catch( InstantiationException e )
		{
			System.err.printf("Unable to instantiate class\"%s\". It probably contains no void constructor? %n%n", edgeClass.getName() );
			System.exit( -1 );
		}
		catch( IllegalAccessException e )
		{
			System.err.printf("Unable to instantiate class\"%s\". Is it publically accessible? %n%n", edgeClass.getName() );
		}

		return n;
	}
}