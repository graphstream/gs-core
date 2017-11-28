package org.graphstream.ui.swing.renderer.shape.swing.baseShapes;

import java.awt.Graphics2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.Skeleton;


public class LineShape extends LineConnectorShape {
	protected Line2D.Double theShapeL = new java.awt.geom.Line2D.Double();
	protected CubicCurve2D.Double theShapeC = new java.awt.geom.CubicCurve2D.Double();
	protected java.awt.Shape theShape = null;
			
	@Override
	public void make(Backend backend, SwingDefaultCamera camera) {
		Point3 from = skel.from();
		Point3 to = skel.to();
		if( skel.isCurve() ) {
			Point3 ctrl1 = skel.apply(1);
			Point3 ctrl2 = skel.apply(2);
			theShapeC.setCurve( from.x, from.y, ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, to.x, to.y );
			theShape = theShapeC;
		} else {
			theShapeL.setLine( from.x, from.y, to.x, to.y );
			theShape = theShapeL;
		} 
	}

	@Override
	public void makeShadow(Backend backend, SwingDefaultCamera camera) {
		double x0 = skel.from().x + shadowableLine.theShadowOff.x;
		double y0 = skel.from().y + shadowableLine.theShadowOff.y;
		double x1 = skel.to().x + shadowableLine.theShadowOff.x;
		double y1 = skel.to().y + shadowableLine.theShadowOff.y;
		
		if( skel.isCurve() ) {
			double ctrlx0 = skel.apply(1).x + shadowableLine.theShadowOff.x;
			double ctrly0 = skel.apply(1).y + shadowableLine.theShadowOff.y;
			double ctrlx1 = skel.apply(2).x + shadowableLine.theShadowOff.x;
			double ctrly1 = skel.apply(2).y + shadowableLine.theShadowOff.y;
			
			theShapeC.setCurve( x0, y0, ctrlx0, ctrly0, ctrlx1, ctrly1, x1, y1 );
			theShape = theShapeC;
		} else {
			theShapeL.setLine( x0, y0, x1, y1 );
			theShape = theShapeL;
		}
	}

	@Override
	public void render(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skeleton) {
		Graphics2D g = bck.graphics2D();
		make(bck, camera);
		strokableLine.stroke(g, theShape );
		fillableLine.fill(g, theSize, theShape);
		decorable.decorConnector(bck, camera, skel.iconAndText, element, theShape);
	}

	@Override
	public void renderShadow(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skeleton) {
		makeShadow(bck, camera);
 		shadowableLine.cast(bck.graphics2D(), theShape);
	}
}