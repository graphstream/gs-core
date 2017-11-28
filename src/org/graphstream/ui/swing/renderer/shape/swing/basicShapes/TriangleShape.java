package org.graphstream.ui.swing.renderer.shape.swing.basicShapes;

import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.shape.swing.baseShapes.PolygonalShape;

public class TriangleShape extends PolygonalShape {

	@Override
	public void make(Backend backend, SwingDefaultCamera camera) {
		double x  = area.theCenter.x;
		double y  = area.theCenter.y;
		double w2 = area.theSize.x / 2;
		double h2 = area.theSize.y / 2;
		
		theShape().reset();
		theShape().moveTo( x,      y + h2 );
		theShape().lineTo( x + w2, y - h2 );
		theShape().lineTo( x - w2, y - h2 );
		theShape().closePath();
	}

	@Override
	public void makeShadow(Backend backend, SwingDefaultCamera camera) {
		double x  = area.theCenter.x + shadowable.theShadowOff.x;
		double y  = area.theCenter.y + shadowable.theShadowOff.y;
		double w2 = ( area.theSize.x + shadowable.theShadowWidth.x ) / 2;
		double h2 = ( area.theSize.y + shadowable.theShadowWidth.y ) / 2;
		
		theShape().reset();
		theShape().moveTo( x,      y + h2 );
		theShape().lineTo( x + w2, y - h2 );
		theShape().lineTo( x - w2, y - h2 );
		theShape().closePath();
	}
}