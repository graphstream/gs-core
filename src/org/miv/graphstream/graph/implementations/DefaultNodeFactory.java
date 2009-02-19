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

import org.miv.graphstream.graph.Node;
import org.miv.graphstream.graph.NodeFactory;

/**
* A class aimed at dynamically creating node objects based on a class name. 
* All created object must extend the {@link DefaultNode} class.
* 
* @author Antoine Dutot
* @author Yoann Pigné
* @since September 2007
*/
public class DefaultNodeFactory implements NodeFactory
{
	protected Class<?> nodeClass;

	protected Class<?> baseNodeclass;

	DefaultNodeFactory()
	{
		this( org.miv.util.Environment.getGlobalEnvironment() );
	}

	DefaultNodeFactory( org.miv.util.Environment environment )
	{
		String cnfs = environment.getParameter( "nodeClass" ); 
		
		if( cnfs == null || cnfs.length() == 0 )
			cnfs = "org.miv.graphstream.graph.implementations.SingleNode";
		
		init( cnfs );
	}
	
	DefaultNodeFactory( String cnfs )
	{
		init( cnfs );
	}
	
	protected void init( String cnfs )
	{
		try
		{
			baseNodeclass = Class.forName( cnfs );
		}
		catch( ClassNotFoundException e )
		{
			e.printStackTrace();
		}

		setNodeClass( cnfs );
	}

	/**
	 * Modifies the name of the class to be used to create new nodes.
	 * @param cnfs full qualified name of the class.
	 */
	@SuppressWarnings("unchecked")
	public void setNodeClass( String cnfs )
	{
		try
		{
			if( cnfs != null  &&  !cnfs.equals("") )
			{
				Class cl = Class.forName( cnfs );

				if( baseNodeclass.isAssignableFrom( cl ) )
				{
					nodeClass = cl;
				}
				else
				{
					System.err.printf("Not able to use \"%s\" to create nodes. " +
							"You must use a class that extends \"DefaultNode\" (environment parameter: \"nodeClass\")%n", cnfs );
				}
			}
			if( nodeClass == null )
				nodeClass = baseNodeclass;

		}
		catch( ClassNotFoundException e )
		{
			System.err.printf("Unable to use \"%s\" to create nodes. Not Found in the classpath. %n", cnfs );
			if( nodeClass == null )
				nodeClass = baseNodeclass;
		}
	}

	public Node newInstance()
	{
		Node n = null;

		try
		{
			n = (Node) nodeClass.newInstance();
		}
		catch( InstantiationException e )
		{
			System.err.printf("Unable to instantiate class\"%s\". It probably contains no void constructor? %n%n", nodeClass.getName() );
			System.exit( -1 );
		}
		catch( IllegalAccessException e )
		{
			System.err.printf("Unable to instantiate class\"%s\". Is it publically accessible? %n%n", nodeClass.getName() );
		}

		return n;
	}
}