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

package org.graphstream.ui.swingViewer.basicRenderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.graphstream.graph.Element;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.StyleGroup.ElementEvents;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.swingViewer.util.Camera;
import org.graphstream.ui.swingViewer.util.FontCache;

public abstract class ElementRenderer {
	// Attribute

	/**
	 * Allow to know if an event began or ended.
	 */
	protected boolean hadEvents = false;

	protected Font textFont;

	protected Color textColor;

	protected int textSize;

	// Constructor

	/**
	 * New swing element renderer for the given style group.
	 */
	public ElementRenderer() {
	}

	// Command

	/**
	 * Render all the (visible) elements of the group.
	 */
	public void render(StyleGroup group, Graphics2D g, Camera camera) {
		setupRenderingPass(group, g, camera);
		pushStyle(group, g, camera);

		for (Element e : group.bulkElements()) {
			GraphicElement ge = (GraphicElement) e;

			if (camera.isVisible(ge))
				renderElement(group, g, camera, ge);
			else
				elementInvisible(group, g, camera, ge);
		}

		if (group.hasDynamicElements()) {
			for (Element e : group.dynamicElements()) {
				GraphicElement ge = (GraphicElement) e;

				if (camera.isVisible(ge)) {
					if (!group.elementHasEvents(ge)) {
						pushDynStyle(group, g, camera, ge);
						renderElement(group, g, camera, ge);
					}
				} else {
					elementInvisible(group, g, camera, ge);
				}
			}
		}

		if (group.hasEventElements()) {
			for (ElementEvents event : group.elementsEvents()) {
				GraphicElement ge = (GraphicElement) event.getElement();

				if (camera.isVisible(ge)) {
					event.activate();
					pushStyle(group, g, camera);
					renderElement(group, g, camera, ge);
					event.deactivate();
				} else {
					elementInvisible(group, g, camera, ge);
				}
			}

			hadEvents = true;
		} else {
			hadEvents = false;
		}
	}

	/**
	 * Called before the whole rendering pass for all elements.
	 * 
	 * @param g
	 *            The Swing graphics.
	 * @param camera
	 *            The camera.
	 */
	protected abstract void setupRenderingPass(StyleGroup group, Graphics2D g,
			Camera camera);

	/**
	 * Called before the rendering of bulk and event elements.
	 * 
	 * @param g
	 *            The Swing graphics.
	 * @param camera
	 *            The camera.
	 */
	protected abstract void pushStyle(StyleGroup group, Graphics2D g,
			Camera camera);

	/**
	 * Called before the rendering of elements on dynamic styles. This must only
	 * change the style properties that can change dynamically.
	 * 
	 * @param g
	 *            The Swing graphics.
	 * @param camera
	 *            The camera.
	 * @param element
	 *            The graphic element concerned by the dynamic style change.
	 */
	protected abstract void pushDynStyle(StyleGroup group, Graphics2D g,
			Camera camera, GraphicElement element);

	/**
	 * Render a single element knowing the style is already prepared. Elements
	 * that are not visible are not drawn.
	 * 
	 * @param g
	 *            The Swing graphics.
	 * @param camera
	 *            The camera.
	 * @param element
	 *            The element to render.
	 */
	protected abstract void renderElement(StyleGroup group, Graphics2D g,
			Camera camera, GraphicElement element);

	/**
	 * Called during rendering in place of
	 * {@link #renderElement(StyleGroup, Graphics2D, Camera, GraphicElement)} to
	 * signal that the given element is not inside the view. The renderElement()
	 * method will be called as soon as the element becomes visible anew.
	 * 
	 * @param g
	 *            The Swing graphics.
	 * @param camera
	 *            The camera.
	 * @param element
	 *            The element to render.
	 */
	protected abstract void elementInvisible(StyleGroup group, Graphics2D g,
			Camera camera, GraphicElement element);

	// Utility

	protected void configureText(StyleGroup group, Camera camera) {
		String fontName = group.getTextFont();
		StyleConstants.TextStyle textStyle = group.getTextStyle();

		textSize = (int) group.getTextSize().value;
		textColor = group.getTextColor(0);
		textFont = FontCache.defaultFontCache().getFont(fontName, textStyle,
				textSize);
	}

	protected void renderText(StyleGroup group, Graphics2D g, Camera camera,
			GraphicElement element) {
		if (group.getTextMode() != StyleConstants.TextMode.HIDDEN) {
			String label = element.getLabel();

			if (label != null) {
				Point2D.Float p = null;
				GraphicSprite s = null;

				if (element instanceof GraphicSprite)
					s = (GraphicSprite) element;

				if (s != null && s.getUnits() == Units.PX) {
					float w = camera.getMetrics()
							.lengthToPx(group.getSize(), 0);
					p = new Point2D.Float();
					p.x = element.getX() + (w / 2);
					p.y = element.getY();
				} else if (s != null && s.getUnits() == Units.PERCENTS) {
					float w = camera.getMetrics()
							.lengthToPx(group.getSize(), 0);
					p = new Point2D.Float();
					p.x = camera.getMetrics().viewport.data[1] * element.getX()
							+ (w / 2);
					p.y = camera.getMetrics().viewport.data[2] * element.getY();
				} else {
					float w = camera.getMetrics()
							.lengthToGu(group.getSize(), 0);
					p = camera.transform(element.getX() + (w / 2),
							element.getY());
				}

				AffineTransform Tx = g.getTransform();
				Color c = g.getColor();

				g.setColor(textColor);
				g.setFont(textFont);
				g.setTransform(new AffineTransform());
				g.drawString(label, p.x, p.y + textSize / 3); // approximation
																// to gain time.
				g.setTransform(Tx);
				g.setColor(c);
			}
		}
	}

	protected Color interpolateColor(StyleGroup group, GraphicElement element) {
		Color color = group.getFillColor(0);

		int n = group.getFillColorCount();

		if (n > 1) {
			if (element.hasNumber("ui.color") && n > 1) {
				float value = (float) element.getNumber("ui.color");

				if (value < 0)
					value = 0;
				else if (value > 1)
					value = 1;

				if (value == 1) {
					color = group.getFillColor(n - 1); // Simplification,
														// faster.
				} else if (value != 0) // If value == 0, color is already set
										// above.
				{
					float div = 1f / (n - 1);
					int col = (int) (value / div);

					div = (value - (div * col)) / div;
					// div = value / div - col;

					Color color0 = group.getFillColor(col);
					Color color1 = group.getFillColor(col + 1);
					float red = ((color0.getRed() * (1 - div)) + (color1
							.getRed() * div)) / 255f;
					float green = ((color0.getGreen() * (1 - div)) + (color1
							.getGreen() * div)) / 255f;
					float blue = ((color0.getBlue() * (1 - div)) + (color1
							.getBlue() * div)) / 255f;
					float alpha = ((color0.getAlpha() * (1 - div)) + (color1
							.getAlpha() * div)) / 255f;

					color = new Color(red, green, blue, alpha);
				}
			}
		}

		return color;
	}
}