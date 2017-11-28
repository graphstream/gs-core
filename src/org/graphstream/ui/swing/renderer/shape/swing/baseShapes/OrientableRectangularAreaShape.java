package org.graphstream.ui.swing.renderer.shape.swing.baseShapes;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.geom.Vector2;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.Skeleton;
import org.graphstream.ui.swing.renderer.shape.Orientable;

public class OrientableRectangularAreaShape extends RectangularAreaShape {
	Orientable orientable ;
	
	Point3 p = null;
	double angle = 0.0;
	double w = 0.0;
	double h = 0.0;
	boolean oriented = false;
	
	public OrientableRectangularAreaShape() {
		orientable = new Orientable();
	}
	
	public void configureForGroup(Backend bck, Style style, SwingDefaultCamera camera) {
		super.configureForGroup(bck, style, camera);
		orientable.configureOrientableForGroup(style, camera);
		oriented = (style.getSpriteOrientation() != StyleConstants.SpriteOrientation.NONE);
	}
	
	public void configureForElement(Backend bck, GraphicElement element, Skeleton skel, SwingDefaultCamera camera) {
		super.configureForElement(bck, element, skel, camera);
		orientable.configureOrientableForElement(camera, (GraphicSprite) element /* Check This XXX TODO !*/);
	}
	
	public void make(Backend backend, SwingDefaultCamera camera) {make(backend, false, camera);}
	
 	public void makeShadow(Backend backend, SwingDefaultCamera camera) {make(backend, true, camera);}

	private void make(Backend bck, boolean forShadow, SwingDefaultCamera camera) {
		if (oriented) {
			Vector2 theDirection = new Vector2(
					orientable.target.x - area.theCenter.x,
					orientable.target.y - area.theCenter.y );
			
			theDirection.normalize();
		
			double x = area.theCenter.x;
			double y = area.theCenter.y;
		
			if( forShadow ) {
				x += shadowable.theShadowOff.x;
				y += shadowable.theShadowOff.y;
			}
		
			p = camera.transformGuToPx(x, y, 0); // Pass to pixels, the image will be drawn in pixels.
			angle = Math.acos(theDirection.dotProduct( 1, 0 ));
		
			if( theDirection.y() > 0 )			// The angle is always computed for acute angles
				angle = ( Math.PI - angle );
	
			w = camera.getMetrics().lengthToPx(area.theSize.x, Units.GU);
			h = camera.getMetrics().lengthToPx(area.theSize.y, Units.GU);
			theShape().setFrame(0, 0, w, h);
		} else {
			if (forShadow)
				super.makeShadow(bck, camera);
			else
				super.make(bck, camera);
		}
	}
 	
	public void render(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skel) {
		make(bck, false, camera);
		
		Graphics2D g = bck.graphics2D();
 		
 		if (oriented) {
	 		AffineTransform Tx = g.getTransform();
	 		AffineTransform Tr = new java.awt.geom.AffineTransform();
	 		Tr.translate( p.x, p.y );								// 3. Position the image at its position in the graph.
	 		Tr.rotate( angle );										// 2. Rotate the image from its center.
	 		Tr.translate( -w/2, -h/2 );								// 1. Position in center of the image.
	 		g.setTransform( Tr );									// An identity matrix.
	 		strokable.stroke(g, theShape());
	 		fillable.fill(g, theShape(), camera);
	 		g.setTransform( Tx );									// Restore the original transform
	 		theShape().setFrame(area.theCenter.x-w/2, area.theCenter.y-h/2, w, h);
	 		decorArea(bck, camera, skel.iconAndText, element, theShape());
 		}
 		else {
 			super.render(bck, camera, element, skel);
 		}
	}
	
	
 	public void renderShadow(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skel) {
 		make(bck, true, camera);
 		
 		Graphics2D g = bck.graphics2D();
 		
 		if (oriented) {
	 		AffineTransform Tx = g.getTransform();
	 		AffineTransform Tr = new java.awt.geom.AffineTransform();
	 		Tr.translate( p.x, p.y );								// 3. Position the image at its position in the graph.
	 		Tr.rotate( angle );										// 2. Rotate the image from its center.
	 		Tr.translate( -w/2, -h/2 );								// 1. Position in center of the image.
	 		g.setTransform( Tr );									// An identity matrix.
 			shadowable.cast(g, theShape());
	 		g.setTransform( Tx );									// Restore the original transform
 		} else {
 			super.renderShadow(bck, camera, element, skel);
 		}
 	}
}
