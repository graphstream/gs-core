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
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.ui.layout;

public class Layouts {
	public static Layout newLayoutAlgorithm() {
		String layoutClassName = System.getProperty("gs.ui.layout");

		if (layoutClassName == null)
			return new org.graphstream.ui.layout.springbox.SpringBox(false);

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
			System.err.printf("Cannot create layout, class '" + layoutClassName
					+ "' error : " + e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.err.printf("Cannot create layout, class '" + layoutClassName
					+ "' illegal access : " + e.getMessage());
		}

		return new org.graphstream.ui.layout.springbox.SpringBox(false);
	}

}
