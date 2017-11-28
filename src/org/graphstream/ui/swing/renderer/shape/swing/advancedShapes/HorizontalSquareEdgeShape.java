package org.graphstream.ui.swing.renderer.shape.swing.advancedShapes;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.Skeleton;
import org.graphstream.ui.swing.renderer.shape.swing.baseShapes.LineConnectorShape;

public class HorizontalSquareEdgeShape extends LineConnectorShape {
	Path2D.Double theShape = new Path2D.Double();

	@Override
	public void make(Backend backend, SwingDefaultCamera camera) {
		make(camera, 0, 0, 0, 0);
	}
	
	private void make(SwingDefaultCamera camera, double sox, double soy, double swx, double swy) {
		if (skel.multi() > 1 || skel.isLoop()) // is a loop or a multi edge
			makeMultiOrLoop(camera, sox, soy, swx, swy);
        else
        	makeSingle(camera, sox, soy, swx, swy); // is a single edge.
	}

	private void makeMultiOrLoop(SwingDefaultCamera camera, double sox, double soy, double swx, double swy) {
		if (skel.isLoop())
            makeLoop(camera, sox, soy, swx, swy);
		else
			makeMulti(camera, sox, soy, swx, swy);
	}

	private void makeLoop(SwingDefaultCamera camera, double sox, double soy, double swx, double swy) {
		double fromx = skel.apply(0).x + sox;
		double fromy = skel.apply(0).y + soy;
		double tox = skel.apply(3).x + sox;
		double toy = skel.apply(3).y + soy;
		double c1x = skel.apply(1).x + sox;
		double c1y = skel.apply(1).y + soy;
		double c2x = skel.apply(2).x + sox;
		double c2y = skel.apply(2).y + soy;
	
	    theShape.reset();
	    theShape.moveTo(fromx, fromy);
	    theShape.curveTo(c1x, c1y, c2x, c2y, tox, toy);
	}

	private void makeMulti(SwingDefaultCamera camera, double sox, double soy, double swx, double swy) {
		double fromx = skel.apply(0).x + sox;
		double fromy = skel.apply(0).y + soy;
		double tox = skel.apply(3).x + sox;
		double toy = skel.apply(3).y + soy;
		double c1x = skel.apply(1).x + sox;
		double c1y = skel.apply(1).y + soy;
		double c2x = skel.apply(2).x + sox;
		double c2y = skel.apply(2).y + soy;
		
		theShape.reset();
		theShape.moveTo(fromx, fromy);
		theShape.curveTo(c1x, c1y, c2x, c2y, tox, toy);		
	}

	private void makeSingle(SwingDefaultCamera camera, double sox, double soy, double swx, double swy) {
		Point3 from = new Point3(skel.from().x + sox, skel.from().y + soy, 0);
		Point3 to = new Point3(skel.to().x + sox, skel.to().y + soy, 0);
        double size = (theSourceSize.x + theTargetSize.x);
        Point3 inter1 = null;
        Point3 inter2 = null;
        Point3 inter3 = null;
        Point3 inter4 = null;

        if (to.x > from.x) {
        	double len = to.x - from.x;

            if (len < size) {
                inter1 = new Point3(from.x + theSourceSize.x, from.y, 0);
                inter2 = new Point3(to.x - theTargetSize.x, to.y, 0);

                inter3 = new Point3(inter1.x, inter1.y + (to.y - from.y) / 2, 0);
                inter4 = new Point3(inter2.x, inter3.y, 0);

                if (sox == 0 && soy == 0)
                {
                	Point3[] pts = {from, inter1, inter3, inter4, inter2, to};
                	skel.setPoly(pts);
                }

            }
            else {
                double middle = (to.x - from.x) / 2;
                inter1 = new Point3(from.x + middle, from.y, 0);
                inter2 = new Point3(to.x - middle, to.y, 0);

                if (sox == 0 && soy == 0)
                {
                	Point3[] pts = {from, inter1, inter2, to};
                	skel.setPoly(pts);
                }
            }
        } 
        else {
            double len = from.x - to.x;

            if (len < size) {
                inter1 = new Point3(from.x - theSourceSize.x, from.y, 0);
                inter2 = new Point3(to.x + theTargetSize.x, to.y, 0);

                inter3 = new Point3(inter1.x, inter1.y + (to.y - from.y) / 2, 0);
                inter4 = new Point3(inter2.x, inter3.y, 0);

                if (sox == 0 && soy == 0)
                {
                	Point3[] pts = {from, inter1, inter3, inter4, inter2, to};
                	skel.setPoly(pts);
                }

            }
            else {
                double middle = (to.x - from.x) / 2;
                inter1 = new Point3(from.x + middle, from.y, 0);
                inter2 = new Point3(to.x - middle, to.y, 0);

                if (sox == 0 && soy == 0)
                {
                	Point3[] pts = {from, inter1, inter2, to};
                	skel.setPoly(pts);
                }
            }
        }

        theShape.reset();
        theShape.moveTo(from.x, from.y);
        theShape.lineTo(inter1.x, inter1.y);
        if ((inter3 != null) && (inter4 != null)) {
            theShape.lineTo(inter3.x, inter3.y);
            theShape.lineTo(inter4.x, inter4.y);
        }
        theShape.lineTo(inter2.x, inter2.y);
        theShape.lineTo(to.x, to.y);
	}

	@Override
	public void makeShadow(Backend backend, SwingDefaultCamera camera) {
		if (skel.isCurve())
			makeMultiOrLoop(camera, shadowableLine.theShadowOff.x, shadowableLine.theShadowOff.y, shadowableLine.theShadowWidth, shadowableLine.theShadowWidth);
        else 
        	makeSingle(camera, shadowableLine.theShadowOff.x, shadowableLine.theShadowOff.y, shadowableLine.theShadowWidth, shadowableLine.theShadowWidth);		
	}

	@Override
	public void render(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skeleton) {
		Graphics2D g = bck.graphics2D();
		make(bck, camera);
		strokableLine.stroke(g, theShape);
		fillableLine.fill(g, theSize, theShape);
		decorable.decorConnector(bck, camera, skel.iconAndText, element, theShape);
	}

	@Override
	public void renderShadow(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skeleton) {
		makeShadow(bck, camera);
		shadowableLine.cast(bck.graphics2D(), theShape);
	}
}