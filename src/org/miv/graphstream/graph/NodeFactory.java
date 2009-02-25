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
package org.miv.graphstream.graph;

/**
 * An interface aimed at dynamicaly creating node objects based on a class name.
 * 
 * @author Antoine Dutot
 * @author Yoann Pign�
 * @since september 2007
 */
public interface NodeFactory
{
	/**
	 * Create a new instance of node.
	 * @return The newly created edge.
	 */
	public Node newInstance( String id, Graph graph );

	/**
	 * Modifies the name of the class to be used to create new nodes.
	 * @param nodeClass The full qualified name of the class.
	 */
	//public void setNodeClass(String nodeClass);
}
