package org.graphstream.ui.swing.renderer.shape.swing.basicShapes;

import java.awt.geom.Ellipse2D;

import org.graphstream.ui.swing.renderer.shape.swing.baseShapes.RectangularAreaShape;

public class CircleShape extends RectangularAreaShape {
	private java.awt.geom.RectangularShape theShape = new Ellipse2D.Double();
	
	public java.awt.geom.RectangularShape theShape() {
		return theShape;
	}
}