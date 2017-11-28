package org.graphstream.ui.swing.renderer.shape.swing.baseShapes;

import java.awt.geom.Rectangle2D;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.Skeleton;

public abstract class RectangularAreaShape extends AreaShape {
	private java.awt.geom.RectangularShape theShape = new Rectangle2D.Double();
	
	@Override
	public void make(Backend backend, SwingDefaultCamera camera) {
		double w = area.theSize.x;
		double h = area.theSize.y;
		
		theShape().setFrame(area.theCenter.x-w/2, area.theCenter.y-h/2, w, h);	
	}

	@Override
	public void makeShadow(Backend backend, SwingDefaultCamera camera) {
		double x = area.theCenter.x + shadowable.theShadowOff.x;
		double y = area.theCenter.y + shadowable.theShadowOff.y;
		double w = area.theSize.x + shadowable.theShadowWidth.x * 2;
		double h = area.theSize.y + shadowable.theShadowWidth.y * 2;
		
		theShape().setFrame(x-w/2, y-h/2, w, h);
	}

	@Override
	public void render(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skel) {
		make(bck, camera);
 		fillable.fill(bck.graphics2D(), theShape(), camera);
 		strokable.stroke(bck.graphics2D(), theShape());
 		decorArea(bck, camera, skel.iconAndText, element, theShape());
	}

	@Override
	public void renderShadow(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skeleton) {
		makeShadow(bck, camera);
 		shadowable.cast(bck.graphics2D(), theShape());
	}
	
	public java.awt.geom.RectangularShape theShape() {
		return theShape;
	}
}