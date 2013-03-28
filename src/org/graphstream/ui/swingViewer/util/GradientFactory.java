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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;

import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.FillMode;

public class GradientFactory {
	/**
	 * Generate a gradient in the given pixel area following the given style.
	 * This produces a gradient only if the style fill-mode is compatible.
	 * 
	 * @param x0
	 *            The left corner of the area.
	 * @param y0
	 *            The bottom corner of the area.
	 * @param width
	 *            The area width.
	 * @param height
	 *            The area height.
	 * @param style
	 *            The style.
	 * @return A gradient paint or null if the style does not specify a
	 *         gradient.
	 */
	public static Paint gradientInArea(int x0, int y0, int width, int height,
			Style style) {
		switch (style.getFillMode()) {
		case GRADIENT_VERTICAL:
			return linearGradientFromStyle(x0, y0, x0, y0 + height, style);
		case GRADIENT_HORIZONTAL:
			return linearGradientFromStyle(x0, y0, x0 + width, y0, style);
		case GRADIENT_DIAGONAL1:
			return linearGradientFromStyle(x0, y0, x0 + width, y0 + height,
					style);
		case GRADIENT_DIAGONAL2:
			return linearGradientFromStyle(x0 + width, y0, x0, y0 + height,
					style);
		case GRADIENT_RADIAL:
			return radialGradientFromStyle(x0 + (width / 2), y0 + (height / 2),
					width > height ? width / 2 : height / 2, style);
		default:
			return null;
		}
	}

	/**
	 * Generate a linear gradient between two given points corresponding to the
	 * given style.
	 * 
	 * @param x0
	 *            The start point abscissa.
	 * @param y0
	 *            The start point ordinate.
	 * @param x1
	 *            The end point abscissa.
	 * @param y1
	 *            The end point ordinate.
	 * @param style
	 *            The style.
	 * @return A paint for the gradient or null if the style specifies no
	 *         gradient (the fill mode is not a linear gradient or there is only
	 *         one fill colour).
	 */
	public static Paint linearGradientFromStyle(float x0, float y0, float x1,
			float y1, Style style) {
		Paint paint = null;

		if (style.getFillColorCount() > 1) {
			switch (style.getFillMode()) {
			case GRADIENT_DIAGONAL1:
			case GRADIENT_DIAGONAL2:
			case GRADIENT_HORIZONTAL:
			case GRADIENT_VERTICAL:
				if (version16)
					paint = new LinearGradientPaint(x0, y0, x1, y1,
							createFractions(style), createColors(style));
				else
					paint = new GradientPaint(x0, y0, style.getFillColor(0),
							x1, y1, style.getFillColor(1));
				break;
			default:
				break;
			}
		}

		return paint;
	}

	public static Paint radialGradientFromStyle(float cx, float cy,
			float radius, Style style) {
		return radialGradientFromStyle(cx, cy, radius, cx, cy, style);
	}

	/**
	 * Generate a radial gradient between whose center is at (cx,cy) with the
	 * given radius. The focus (fx,fy) is the start position of the gradient in
	 * the circle.
	 * 
	 * @param cx
	 *            The center point abscissa.
	 * @param cy
	 *            The center point ordinate.
	 * @param fx
	 *            The start point abscissa.
	 * @param fy
	 *            The start point ordinate.
	 * @param radius
	 *            The gradient radius.
	 * @param style
	 *            The style.
	 * @return A paint for the gradient or null if the style specifies no
	 *         gradient (the fill mode is not a radial gradient or there is only
	 *         one fill colour).
	 */
	public static Paint radialGradientFromStyle(float cx, float cy,
			float radius, float fx, float fy, Style style) {
		Paint paint = null;

		if (version16) {
			if (style.getFillColorCount() > 1
					&& style.getFillMode() == FillMode.GRADIENT_RADIAL) {
				float fractions[] = createFractions(style);
				Color colors[] = createColors(style);
				paint = new RadialGradientPaint(cx, cy, radius, fx, fy,
						fractions, colors,
						MultipleGradientPaint.CycleMethod.REFLECT);
			}
		}

		return paint;
	}

	protected static float[] createFractions(Style style) {
		int n = style.getFillColorCount();

		if (n < predefFractions.length)
			return predefFractions[n];

		float fractions[] = new float[n];
		float div = 1f / (n - 1);

		for (int i = 1; i < (n - 1); i++)
			fractions[i] = div * i;

		fractions[0] = 0f;
		fractions[n - 1] = 1f;

		return fractions;
	}

	protected static Color[] createColors(Style style) {
		int n = style.getFillColorCount();
		Color colors[] = new Color[n];

		for (int i = 0; i < n; i++)
			colors[i] = style.getFillColor(i);

		return colors;
	}

	public static boolean version16 = false;
	public static float[][] predefFractions = new float[11][];
	public static float[] predefFractions2 = { 0f, 1f };
	public static float[] predefFractions3 = { 0f, 0.5f, 1f };
	public static float[] predefFractions4 = { 0f, 0.33f, 0.66f, 1f };
	public static float[] predefFractions5 = { 0f, 0.25f, 0.5f, 0.75f, 1f };
	public static float[] predefFractions6 = { 0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f };
	public static float[] predefFractions7 = { 0f, 0.1666f, 0.3333f, 0.4999f,
			0.6666f, 0.8333f, 1f };
	public static float[] predefFractions8 = { 0f, 0.1428f, 0.2856f, 0.4284f,
			0.5712f, 0.7140f, 0.8568f, 1f };
	public static float[] predefFractions9 = { 0f, 0.125f, 0.25f, 0.375f, 0.5f,
			0.625f, .75f, 0.875f, 1f };
	public static float[] predefFractions10 = { 0f, 0.1111f, 0.2222f, 0.3333f,
			0.4444f, 0.5555f, 0.6666f, 0.7777f, 0.8888f, 1f };

	static {
		String version = System.getProperty("java.version");

		if (version.startsWith("1.") && version.length() >= 3) {
			String v = version.substring(2, 3);
			int n = Integer.parseInt(v);

			if (n >= 6)
				version16 = true;
		}

		predefFractions[0] = null;
		predefFractions[1] = null;
		predefFractions[2] = predefFractions2;
		predefFractions[3] = predefFractions3;
		predefFractions[4] = predefFractions4;
		predefFractions[5] = predefFractions5;
		predefFractions[6] = predefFractions6;
		predefFractions[7] = predefFractions7;
		predefFractions[8] = predefFractions8;
		predefFractions[9] = predefFractions9;
		predefFractions[10] = predefFractions10;
	}
}