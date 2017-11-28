package org.graphstream.ui.swing.renderer.shape.swing.spriteShapes;

import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import org.graphstream.ui.swing.renderer.shape.swing.baseShapes.OrientableRectangularAreaShape;

public class OrientableSquareShape extends OrientableRectangularAreaShape {
	private RectangularShape theShape = new Rectangle2D.Double();
	
	public java.awt.geom.RectangularShape theShape() {
		return theShape;
	}
}