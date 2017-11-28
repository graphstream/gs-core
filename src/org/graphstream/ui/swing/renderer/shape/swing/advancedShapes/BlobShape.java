package org.graphstream.ui.swing.renderer.shape.swing.advancedShapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import org.graphstream.ui.geom.Vector2;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.ConnectorSkeleton;
import org.graphstream.ui.swing.renderer.Skeleton;
import org.graphstream.ui.swing.renderer.shape.swing.ShowCubics;
import org.graphstream.ui.swing.renderer.shape.swing.baseShapes.AreaConnectorShape;

public class BlobShape extends AreaConnectorShape {
	ShowCubics showCubics ;
	Path2D.Double theShape = new Path2D.Double();

	public BlobShape() {
		showCubics = new ShowCubics() ;
	}

	@Override
	public void make(Backend backend, SwingDefaultCamera camera) {
		make(camera, 0, 0, 0, 0);
	}

	private void make(SwingDefaultCamera camera, double sox, double soy, double swx, double swy) {
		if (skel.isCurve())
            makeOnCurve(camera, sox, soy, swx, swy);
        else if (skel.isPoly())
            makeOnPolyline(camera, sox, soy, swx, swy);
        else
        	makeOnLine(camera, sox, soy, swx, swy);
	}
	
	private void makeOnCurve(SwingDefaultCamera camera, double sox, double soy, double swx, double swy) {
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
		double srcsz = Math.min(theSourceSize.x, theSourceSize.y);
		//		double trgsz = min( theTargetSizeX, theTargetSizeY )
		
		Vector2 dirFrom = new Vector2(c1x - fromx, c1y - fromy);
		Vector2 dirTo = new Vector2(tox - c2x, toy - c2y);
		Vector2 mainDir = new Vector2(c2x - c1x, c2y - c1y);
		
		Vector2 perpFrom = new Vector2(dirFrom.y(), -dirFrom.x()); 
		perpFrom.normalize();
		Vector2 mid1 = new Vector2(dirFrom); mid1.sub(mainDir); 
		mid1.normalize();
		Vector2 mid2 = new Vector2(mainDir); mid2.sub(dirTo); 
		mid2.normalize();
		
		perpFrom.scalarMult((srcsz + swx) * 0.3f);
		
		if (isDirected) {
		    mid1.scalarMult((theSize + swx) * 4f);
		    mid2.scalarMult((theSize + swx) * 2f);
		}
		else {
		    mid1.scalarMult((theSize + swx) * 4f);
		    mid2.scalarMult((theSize + swx) * 4f);
		}
		
		theShape.reset();
		theShape.moveTo(fromx + perpFrom.x(), fromy + perpFrom.y());
		if (isDirected) {
		    theShape.curveTo(c1x + mid1.x(), c1y + mid1.y(), c2x + mid2.x(), c2y + mid2.y(), tox, toy);
		    theShape.curveTo(c2x - mid2.x(), c2y - mid2.y(), c1x - mid1.x(), c1y - mid1.y(), fromx - perpFrom.x(), fromy - perpFrom.y());
		} 
		else {
		    Vector2 perpTo = new Vector2(dirTo.y(), -dirTo.x()); 
		    perpTo.normalize();
		    perpTo.scalarMult((srcsz + swx) * 0.3f);
		    theShape.curveTo(c1x + mid1.x(), c1y + mid1.y(), c2x + mid2.x(), c2y + mid2.y(), tox + perpTo.x(), toy + perpTo.y());
		    theShape.lineTo(tox - perpTo.x(), toy - perpTo.y());
		    theShape.curveTo(c2x - mid2.x(), c2y - mid2.y(), c1x - mid1.x(), c1y - mid1.y(), fromx - perpFrom.x(), fromy - perpFrom.y());
		}
		theShape.closePath();
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
		double srcsz = Math.min(theSourceSize.x, theSourceSize.y);
		double trgsz = Math.min(theTargetSize.x, theTargetSize.y);
		
		Vector2 maindir = new Vector2(c2x - c1x, c2y - c1y);
		Vector2 perp1 = new Vector2(maindir.y(), -maindir.x());
		perp1.normalize(); // 1/2 perp vector to the from point.
		Vector2 perp2 = new Vector2(perp1.x(), perp1.y()); // 1/2 perp vector to the to point.
		Vector2 perpm = new Vector2(perp1.x(), perp1.y()); // 1/2 perp vector to the middle point on the edge.
		
		double t = 5f;
		
		perp1.scalarMult((srcsz + swx) / 2f);
		perpm.scalarMult((theSize + swx) / 2f);
		
		//   ctrl1           ctrl2
		//     x---t-------t---x
		//    /                 \
		//   /                   \
		        //  X                     X
		// from                  to
		
		if (isDirected)
			perp2.scalarMult((theSize + swx) / 2f);
		else 
			perp2.scalarMult((trgsz + swx) / 2f);
		
		theShape.reset();
		theShape.moveTo(fromx + perp1.x(), fromy + perp1.y());
		
		theShape.quadTo(c1x + perpm.x(), c1y + perpm.y(),
				c1x + maindir.x() / t + perpm.x(), c1y + maindir.y() / t + perpm.y());
		theShape.lineTo(c2x - maindir.x() / t + perpm.x(), c2y - maindir.y() / t + perpm.y());
		theShape.quadTo(c2x + perpm.x(), c2y + perpm.y(), tox + perp2.x(), toy + perp2.y());	

		theShape.lineTo(tox - perp2.x(), toy - perp2.y());

		theShape.quadTo(c2x - perpm.x(), c2y - perpm.y(),
				c2x - maindir.x() / t - perpm.x(), c2y - maindir.y() / t - perpm.y());
		theShape.lineTo(c1x + maindir.x() / t - perpm.x(), c1y + maindir.y() / t - perpm.y());
		theShape.quadTo(c1x - perpm.x(), c1y - perpm.y(), fromx - perp1.x(), fromy - perp1.y());

		theShape.closePath();
	}

	private void makeOnPolyline(SwingDefaultCamera camera, double sox, double soy, double swx, double swy) {
		makeOnLine(camera, sox, soy, swx, swy);
	}

	private void makeOnLine(SwingDefaultCamera camera, double sox, double soy, double swx, double swy) {
		double fromx = skel.from().x + sox;
		double fromy = skel.from().y + soy;
		double tox = skel.to().x + sox;
		double toy = skel.to().y + soy;
		Vector2 dir = new Vector2(tox - fromx, toy - fromy);
		Vector2 perp1 = new Vector2(dir.y(), -dir.x());
		perp1.normalize(); // 1/2 perp vector to the from point.
		Vector2 perp2 = new Vector2(perp1.x(), perp1.y()); // 1/2 perp vector to the to point.
		Vector2 perpm = new Vector2(perp1.x(), perp1.y()); // 1/2 perp vector to the middle point on the edge.
		double srcsz = Math.min(theSourceSize.x, theSourceSize.y);
		double trgsz = Math.min(theTargetSize.x, theTargetSize.y);

		perp1.scalarMult((srcsz + swx) / 2f);
		perpm.scalarMult((theSize + swx) / 2f);

		if (isDirected)
			perp2.scalarMult((theSize + swx) / 2f);
		else 
			perp2.scalarMult((trgsz + swx) / 2f);

		double t1 = 5f;
		double t2 = 2.3f;
		double m = 1f;
		theShape.reset();
		theShape.moveTo(fromx + perp1.x(), fromy + perp1.y());
		theShape.quadTo(fromx + dir.x() / t1 + perpm.x() * m, fromy + dir.y() / t1 + perpm.y() * m,
				fromx + dir.x() / t2 + perpm.x(), fromy + dir.y() / t2 + perpm.y());
		theShape.lineTo(tox - dir.x() / t2 + perpm.x(), toy - dir.y() / t2 + perpm.y());
		theShape.quadTo(tox - dir.x() / t1 + perpm.x() * m, toy - dir.y() / t1 + perpm.y() * m,
				tox + perp2.x(), toy + perp2.y());
		theShape.lineTo(tox - perp2.x(), toy - perp2.y());
		theShape.quadTo(tox - dir.x() / t1 - perpm.x() * m, toy - dir.y() / t1 - perpm.y() * m,
				tox - dir.x() / t2 - perpm.x(), toy - dir.y() / t2 - perpm.y());
		theShape.lineTo(fromx + dir.x() / t2 - perpm.x(), fromy + dir.y() / t2 - perpm.y());
		theShape.quadTo(fromx + dir.x() / t1 - perpm.x() * m, fromy + dir.y() / t1 - perpm.y() * m,
				fromx - perp1.x(), fromy - perp1.y());
		theShape.closePath();
	}

	@Override
	public void makeShadow(Backend backend, SwingDefaultCamera camera) {
        make(camera, (int)shadowable.theShadowOff.x, (int)shadowable.theShadowOff.y, (int)shadowable.theShadowWidth.x, (int)shadowable.theShadowWidth.y);
	}

	@Override
	public void render(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skeleton) {
		Graphics2D g = bck.graphics2D();
		make(bck, camera);
		strokable.stroke(g, theShape);
		fillable.fill(g, theShape, camera);
		decorable.decorConnector(bck, camera, skel.iconAndText, element, theShape);	

		if (showCubics.showControlPolygon) {
			Color c = g.getColor();
			Stroke s = g.getStroke();
			g.setStroke(new java.awt.BasicStroke((float)camera.getMetrics().px1));
			g.setColor(Color.red);
			g.draw(theShape);
			g.setStroke(s);
			g.setColor(c);
			showCubics.showCtrlPoints(g, camera, (ConnectorSkeleton)skel);
		}
	}

	@Override
	public void renderShadow(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skeleton) {
		makeShadow(bck, camera);
		shadowable.cast(bck.graphics2D(), theShape);
	}
}