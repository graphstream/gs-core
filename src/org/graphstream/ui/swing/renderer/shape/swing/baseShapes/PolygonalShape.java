package org.graphstream.ui.swing.renderer.shape.swing.baseShapes;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.Skeleton;

public abstract class PolygonalShape extends AreaShape {
	private Path2D.Double theShape = new java.awt.geom.Path2D.Double();
 
 	public void renderShadow(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skel) {
 		makeShadow(bck, camera);
 		shadowable.cast(bck.graphics2D(), theShape());
 	}
  
 	public void render(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skel) {
 		Graphics2D g = bck.graphics2D();
 		make(bck, camera);
 		fillable.fill(g, theShape(), camera);
 		strokable.stroke(g, theShape());
 		decorArea(bck, camera, skel.iconAndText, element, theShape());
 	}
 	
 	public Path2D.Double theShape() {
 		return theShape ;
 	}
}