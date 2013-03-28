/*
 * Copyright 2006 - 2013
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.graph;

/**
 * An interface aimed at dynamically creating graph objects based on a class
 * name.
 * 
 * @since september 2007
 */
public class GraphFactory {
	/**
	 * Create a new instance of graph.
	 */
	public GraphFactory() {
	}

	/**
	 * Instantiate a new graph from the given class name.
	 * 
	 * @return A graph instance or null if the graph class was not found.
	 */
	public Graph newInstance(String id, String graphClass) {
		try {
			String completeGraphClass;
			if (graphClass.split("[.]").length < 2) {
				completeGraphClass = "org.graphstream.graph.implementations."
						+ graphClass;
			} else {
				completeGraphClass = graphClass;
			}
			// Graph res = (Graph) Class.forName( completeGraphClass
			// ).newInstance();
			// res.setId( id );
			Class<?> clazz = Class.forName(completeGraphClass);
			Graph res = (Graph) clazz.getConstructor(String.class).newInstance(
					id);
			return res;
		} catch (InstantiationException e) {
			System.out
					.println("GraphFactory newInstance InstantiationException : "
							+ e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out
					.println("GraphFactory newInstance ClassNotFoundException : "
							+ e.getMessage());
		} catch (IllegalAccessException e) {
			System.out
					.println("GraphFactory newInstance IllegalAccessException : "
							+ e.getMessage());
		} catch (NoSuchMethodException e) {
			System.out
					.println("GraphFactory newInstance NoSuchMethodException : "
							+ e.getMessage());
		} catch (java.lang.reflect.InvocationTargetException e) {
			System.out
					.println("GraphFactory newInstance InvocationTargetException : "
							+ e.getMessage());
		}

		return null;
	}
}