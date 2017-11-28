package org.graphstream.ui.swing.renderer.shape.swing.basicShapes;

import java.awt.geom.RoundRectangle2D;

import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.shape.swing.baseShapes.RectangularAreaShape;

public class RoundedSquareShape extends RectangularAreaShape {
	RoundRectangle2D.Double theShape = new RoundRectangle2D.Double();
	
	@Override
	public void make(Backend backend, SwingDefaultCamera camera) {
		double w = area.theSize.x ;
		double h = area.theSize.x ;
		double r = h/8 ;
		if( h/8 > w/8 )
			r = w/8 ;
		((RoundRectangle2D) theShape()).setRoundRect( area.theCenter.x-w/2, area.theCenter.y-h/2, w, h, r, r ) ;
	}
	
	@Override
	public void makeShadow(Backend backend, SwingDefaultCamera camera) {
		double x = area.theCenter.x + shadowable.theShadowOff.x;
		double y = area.theCenter.y + shadowable.theShadowOff.y;
		double w = area.theSize.x + shadowable.theShadowWidth.x * 2;
		double h = area.theSize.y + shadowable.theShadowWidth.y * 2;
		double r = h/8 ;
		if( h/8 > w/8 ) 
			r = w/8;
				
		((RoundRectangle2D) theShape()).setRoundRect( x-w/2, y-h/2, w, h, r, r );
	}
	
	@Override
	public java.awt.geom.RectangularShape theShape() {
		return theShape;
	}
}