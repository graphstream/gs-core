/*
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

/**
 * @since 2009-02-19
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Julien Baudry <julien.baudry@gmail.com>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.graph;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An interface aimed at dynamically creating graph objects based on a class
 * name.
 * 
 * @since september 2007
 */
public class GraphFactory {

	private static final Logger logger = Logger.getLogger(GraphFactory.class.getSimpleName());

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
				completeGraphClass = "org.graphstream.graph.implementations." + graphClass;
			} else {
				completeGraphClass = graphClass;
			}
			// Graph res = (Graph) Class.forName( completeGraphClass
			// ).newInstance();
			// res.setId( id );
			Class<?> clazz = Class.forName(completeGraphClass);
			Graph res = (Graph) clazz.getConstructor(String.class).newInstance(id);
			return res;
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "Error executing GraphFactory#newInstance.", e);
		}
		return null;
	}
}