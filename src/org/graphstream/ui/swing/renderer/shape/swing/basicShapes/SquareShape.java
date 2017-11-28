package org.graphstream.ui.swing.renderer.shape.swing.basicShapes;

import java.awt.geom.Rectangle2D;

import org.graphstream.ui.swing.renderer.shape.swing.baseShapes.RectangularAreaShape;

public class SquareShape extends RectangularAreaShape {
	private Rectangle2D.Double theShape = new Rectangle2D.Double();
	
	public java.awt.geom.RectangularShape theShape() {
		return theShape;
	}
}