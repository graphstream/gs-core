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
package org.graphstream.ui.swingViewer.util;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;

/**
 * Generator for strokes based on the given style.
 */
public class StrokeFactory {
	/**
	 * Generate a stroke of the appropriate width and style according to the
	 * given style and metrics.
	 * 
	 * @param style
	 *            The style to use.
	 * @param metrics
	 *            The metrics to use.
	 * @return The stroke or null if the style specifies a "none" stroke mode.
	 */
	public static Stroke generateStroke(Style style, GraphMetrics metrics) {
		if (style.getStrokeWidth().value == 0)
			return null;

		switch (style.getStrokeMode()) {
		case PLAIN:
			return generatePlainStroke(style, metrics);
		case DOTS:
			return generateDotsStroke(style, metrics);
		case DASHES:
			return generateDashesStroke(style, metrics);
		default:
		case NONE:
			return null;
		}
	}

	protected static Stroke generatePlainStroke(Style style,
			GraphMetrics metrics) {
		float width = (float)metrics.lengthToGu(style.getStrokeWidth());

		/*
		 * if( width == 1f ) return plainLine1px; // XXX Not a good optimisation
		 * else if( width == 2f ) return plainLine2px; // We draw the whole
		 * graph in GU else if( width == 3f ) return plainLine3px; // In graph
		 * units the width is never exactly 1,2, 5 ... else if( width == 5f )
		 * return plainLine5px; else if( width == 10f ) return plainLine10px;
		 * else
		 */{
			return new BasicStroke(width);
		}
	}

	protected static Stroke generateDotsStroke(Style style, GraphMetrics metrics) {
		float width = (float)metrics.lengthToGu(style.getStrokeWidth());
		/*
		 * if( width == 1f ) return dotsLine1px; else if( width == 2f ) return
		 * dotsLine2px; else if( width == 3f ) return dotsLine3px; else if(
		 * width == 5f ) return dotsLine5px; else if( width == 10f ) return
		 * dotsLine10px; else
		 */{
			dots[0] = (float)metrics.lengthToGu(1f, Units.PX);
			dots[1] = dots[0];
			return new BasicStroke(width, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 1f, dots, 0);
		}
	}

	protected static Stroke generateDashesStroke(Style style,
			GraphMetrics metrics) {
		float width = (float)metrics.lengthToGu(style.getStrokeWidth());
		/*
		 * if( width == 1f ) return dashesLine1px; else if( width == 2f ) return
		 * dashesLine2px; else if( width == 3f ) return dashesLine3px; else if(
		 * width == 5f ) return dashesLine5px; else if( width == 10f ) return
		 * dashesLine10px; else
		 */{
			dashes[0] = (float)metrics.lengthToGu(3f, Units.PX);
			dashes[1] = dashes[0];
			return new BasicStroke(width, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER, 1f, dashes, 0);
		}
	}

	protected static float[] dots = { 1f, 1f };
	protected static float[] dashes = { 3f, 3f };
	/*
	 * protected static Stroke plainLine1px = new BasicStroke( 1 ); protected
	 * static Stroke dotsLine1px = new BasicStroke( 1, BasicStroke.CAP_BUTT,
	 * BasicStroke.JOIN_BEVEL, 1f, dots, 0 ); protected static Stroke
	 * dashesLine1px = new BasicStroke( 1, BasicStroke.CAP_BUTT,
	 * BasicStroke.JOIN_BEVEL, 1f, dots, 0 ); protected static Stroke
	 * plainLine2px = new BasicStroke( 2 ); protected static Stroke dotsLine2px
	 * = new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f,
	 * dots, 0 ); protected static Stroke dashesLine2px = new BasicStroke( 2,
	 * BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, dots, 0 ); protected
	 * static Stroke plainLine3px = new BasicStroke( 2 ); protected static
	 * Stroke dotsLine3px = new BasicStroke( 2, BasicStroke.CAP_BUTT,
	 * BasicStroke.JOIN_BEVEL, 1f, dots, 0 ); protected static Stroke
	 * dashesLine3px = new BasicStroke( 2, BasicStroke.CAP_BUTT,
	 * BasicStroke.JOIN_BEVEL, 1f, dots, 0 ); protected static Stroke
	 * plainLine5px = new BasicStroke( 2 ); protected static Stroke dotsLine5px
	 * = new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f,
	 * dots, 0 ); protected static Stroke dashesLine5px = new BasicStroke( 2,
	 * BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, dots, 0 ); protected
	 * static Stroke plainLine10px = new BasicStroke( 2 ); protected static
	 * Stroke dotsLine10px = new BasicStroke( 2, BasicStroke.CAP_BUTT,
	 * BasicStroke.JOIN_BEVEL, 1f, dots, 0 ); protected static Stroke
	 * dashesLine10px = new BasicStroke( 2, BasicStroke.CAP_BUTT,
	 * BasicStroke.JOIN_BEVEL, 1f, dots, 0 );
	 */
}