package org.graphstream.ui.swing.renderer.shape.swing.basicShapes;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.Skeleton;
import org.graphstream.ui.swing.renderer.shape.swing.baseShapes.RectangularAreaShape;

public class FreePlaneNodeShape extends RectangularAreaShape {
	Rectangle2D.Double theShape = new Rectangle2D.Double();
	Line2D.Double theLineShape = new Line2D.Double();
	
	@Override
	public void make(Backend backend, SwingDefaultCamera camera) {
		double w = area.theSize.x ;
		double h = area.theSize.y ;
		double x = area.theCenter.x ;
		double y = area.theCenter.y ;
		
		((Rectangle2D) theShape()).setRect( x-w/2, y-h/2, w, h );
		
		w -= strokable.theStrokeWidth;
		
		theLineShape.setLine( x-w/2, y-h/2, x+w/2, y-h/2 );
	}
	
	@Override
	public void makeShadow(Backend backend, SwingDefaultCamera camera) {
		double x = area.theCenter.x + shadowable.theShadowOff.x;
		double y = area.theCenter.y + shadowable.theShadowOff.y;
		double w = area.theSize.x + shadowable.theShadowWidth.x * 2;
		double h = area.theSize.y + shadowable.theShadowWidth.y * 2;
		
		((Rectangle2D) theShape()).setRect( x-w/2, y-h/2, w, h );
		theLineShape.setLine( x-w/2, y-h/2, x+w/2, y-h/2 );	
	}
	
	@Override
	public void render(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skel) {
		Graphics2D g = bck.graphics2D();
		make(bck, camera);
		fillable.fill(g, theShape(), camera);
		strokable.stroke(g, theLineShape);
		decorArea(bck, camera, skel.iconAndText, element, theShape());
	}
	
	@Override
	public java.awt.geom.RectangularShape theShape() {
		return theShape;
	} 
	
}
