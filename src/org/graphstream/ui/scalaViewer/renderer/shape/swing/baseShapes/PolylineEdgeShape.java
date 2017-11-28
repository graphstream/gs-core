package org.graphstream.ui.scalaViewer.renderer.shape.swing.baseShapes;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.scalaViewer.Backend;
import org.graphstream.ui.scalaViewer.ScalaDefaultCamera;
import org.graphstream.ui.scalaViewer.renderer.Skeleton;
import org.graphstream.ui.scalaViewer.renderer.shape.swing.ShowCubics;

public class PolylineEdgeShape extends LineConnectorShape {
	public ShowCubics showCubics ;
	protected Path2D.Double theShape = new java.awt.geom.Path2D.Double();

	public PolylineEdgeShape() {
		this.showCubics = new ShowCubics();
	}

	@Override
	public void make(Backend backend, ScalaDefaultCamera camera) {
		int n = skel.size();
		
		theShape.reset();
		theShape.moveTo(skel.apply(0).x, skel.apply(0).y);
		
		for(int i = 0 ; i < n ; i++) {
			theShape.lineTo(skel.apply(i).x, skel.apply(i).y);
		}		
	}

	@Override
	public void makeShadow(Backend backend, ScalaDefaultCamera camera) {}

	@Override
	public void render(Backend bck, ScalaDefaultCamera camera, GraphicElement element, Skeleton skeleton) {
		Graphics2D g = bck.graphics2D();
		make(bck, camera);
		strokableLine.stroke(g, theShape);
		fillableLine.fill(g, theSize, theShape);
		decorable.decorConnector(bck, camera, skel.iconAndText, element, theShape);
	}

	@Override
	public void renderShadow(Backend bck, ScalaDefaultCamera camera, GraphicElement element, Skeleton skeleton) {
		makeShadow(bck, camera);
 		shadowableLine.cast(bck.graphics2D(), theShape);			
	}

}