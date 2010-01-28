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

/**
 * Definition of style sheets and styles.
 * 
 * <p>
 * The {@link org.graphstream.ui.graphicGraph.stylesheet.parser} sub-package provide a JavaCC
 * parser for the style sheets defined in this package.
 * </p>
 * 
 * <p>
 * This package contains several classes that concentrate on the definition of a style sheet,
 * its contents, and the way they allow fast drawing of a very large number of graph elements.
 * </p>
 * 
 * <p>
 * The {@link org.graphstream.ui.graphicGraph.stylesheet.Style} class contains the definition
 * of a single style. A style is a set of pairs (property,value) that can inherit another style or
 * link to a set of alternative styles (also known as meta-classes, or events).
 * </p>
 * 
 * <p>
 * Inside the style the values can be colours, strings and numeric values. Numeric values can use
 * units of measure. This is why most numeric values in the style sheet are stored as instances of
 * {@link org.graphstream.ui.graphicGraph.stylesheet.Value}. This class stores a value an the
 * units of measure it is expressed in.
 * </p>
 * 
 * <p>
 * In the style sheet, the styles are not referred directly, they are contained in
 * {@link org.graphstream.ui.graphicGraph.stylesheet.Rule}s. A rule is a pair (selector,style).
 * The {@link org.graphstream.ui.graphicGraph.stylesheet.Selector} class defines the set
 * of elements the style applies to, it can be loosely compared to a regular expression.
 * </p>
 * 
 * <p>
 * The {@link org.graphstream.ui.graphicGraph.stylesheet.StyleSheetListener} allows to listen
 * at events occurring in the style sheet.
 * </p>
 */
package org.graphstream.ui.graphicGraph.stylesheet;