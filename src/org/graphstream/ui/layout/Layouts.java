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
package org.graphstream.ui.layout;

import java.security.AccessControlException;

/**
 * A factory in charge or creating various layout implementations.
 * 
 * This class is mainly used to create the default layout for the graph viewer.
 * You can also use layouts directly on your graphs, but in this case you do not
 * need this factory.
 * 
 * This class looks at the "gs.ui.layout" system property to create a layout
 * class. You can change this property using
 * <code>System.setProperty("gs.ui.layout", you_layout_class_name)</code>.
 */
public class Layouts {
	/**
	 * Creates a layout according to the "org.graphstream.ui.layout" system property.
	 * 
	 * @return The new layout or the default GraphStream "Spring-Box" layout if
	 *         the "gs.ui.layout" system property is either not set or contains
	 *         a class that cannot be found.
	 */
	public static Layout newLayoutAlgorithm() {
		String layoutClassName;

		try {
			layoutClassName = System.getProperty("gs.ui.layout");

			if (layoutClassName != null) {
				System.err.printf("\"gs.ui.layout\" is deprecated,");
				System.err.printf("use \"org.graphstream.ui.layout\""
						+ " instead\n");
			} else {
				layoutClassName = System
						.getProperty("org.graphstream.ui.layout");
			}
		} catch (AccessControlException e) {
			layoutClassName = null;
		}

		if (layoutClassName != null) {
			try {
				Class<?> c = Class.forName(layoutClassName);
				Object object = c.newInstance();

				if (object instanceof Layout) {
					return (Layout) object;
				} else {
					System.err.printf("class '%s' is not a 'GraphRenderer'%n",
							object);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.err
						.printf("Cannot create layout, 'GraphRenderer' class not found : "
								+ e.getMessage());
			} catch (InstantiationException e) {
				e.printStackTrace();
				System.err.printf("Cannot create layout, class '"
						+ layoutClassName + "' error : " + e.getMessage());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				System.err.printf("Cannot create layout, class '"
						+ layoutClassName + "' illegal access : "
						+ e.getMessage());
			}
		}

		return new org.graphstream.ui.layout.springbox.implementations.SpringBox(
				false);
	}
}