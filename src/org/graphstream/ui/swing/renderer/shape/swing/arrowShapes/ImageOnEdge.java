package org.graphstream.ui.swing.renderer.shape.swing.arrowShapes;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.geom.Vector2;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.Skeleton;
import org.graphstream.ui.swing.renderer.shape.swing.baseShapes.AreaOnConnectorShape;
import org.graphstream.ui.swing.util.ImageCache;
import org.graphstream.ui.swing.util.ShapeUtil;
import org.graphstream.ui.swing.util.AttributeUtils.Tuple;
import org.graphstream.ui.view.util.CubicCurve;

public class ImageOnEdge extends AreaOnConnectorShape {
	BufferedImage image = null;
	Point3 p = null ;
	double angle = 0.0;
	
	@Override
	public void configureForGroup(Backend bck, Style style, SwingDefaultCamera camera) {
		super.configureForGroup(bck, style, camera);
	}
	
	@Override
	public void configureForElement(Backend bck, GraphicElement element, Skeleton skel, SwingDefaultCamera camera) {
		super.configureForElement(bck, element, skel, camera);
		
		String url = element.getStyle().getArrowImage();
				
		if( url.equals( "dynamic" ) ) {
			if( element.hasLabel( "ui.arrow-image" ) )
				url = element.getLabel( "ui.arrow-image" ).toString();
			else
				url = null;
		}
				
		if( url != null ) {
			image = ImageCache.loadImage(url);
			if (image == null) {
				image = ImageCache.dummyImage();
			}
		}
	}
	
	@Override
	public void make(Backend backend, SwingDefaultCamera camera) {
		make( false, camera );
	}
	@Override
	public void makeShadow(Backend backend, SwingDefaultCamera camera) {
		make( true, camera );
	}
	
	private void make(boolean forShadow, SwingDefaultCamera camera) {
		if( theConnector.skel.isCurve() )
			makeOnCurve( forShadow, camera );
		else
			makeOnLine( forShadow, camera );	
	}
	
	private void makeOnCurve(boolean forShadow, SwingDefaultCamera camera) {
		Tuple<Point2, Double> tuple = CubicCurve.approxIntersectionPointOnCurve( theEdge, theConnector, camera );
		Point2 p1 = tuple.x ;
		double t = tuple.y ;
		
		Style style  = theEdge.getStyle();
		Point3 p2  = CubicCurve.eval( theConnector.fromPos(), theConnector.byPos1(), theConnector.byPos2(), theConnector.toPos(), t-0.1f );
		Vector2 dir = new Vector2( p1.x - p2.x, p1.y - p2.y );
		
		dir.normalize();
		
		double iw = camera.getMetrics().lengthToGu( image.getWidth(), Units.PX ) / 2;
		double x  = p1.x - ( dir.x() * iw );
		double y  = p1.y - ( dir.y() * iw );
		
		if( forShadow ) {
			x += shadowable.theShadowOff.x;
			y += shadowable.theShadowOff.y;
		}
		
		p = camera.transformGuToPx( x, y, 0 );
		angle = Math.acos( dir.dotProduct( 1, 0 ) );
		
		if( dir.y() > 0 )
			angle = ( Math.PI - angle );
	}
	
	private void makeOnLine(boolean forShadow, SwingDefaultCamera camera) {
		double off = ShapeUtil.evalTargetRadius2D( theEdge, camera );
		
		Vector2 theDirection = new Vector2(
				theConnector.toPos().x - theConnector.fromPos().x,
				theConnector.toPos().y - theConnector.fromPos().y );
					
		theDirection.normalize();
				
		double iw = camera.getMetrics().lengthToGu( image.getWidth(), Units.PX ) / 2;
		double x  = theCenter.x - ( theDirection.x() * ( off + iw ) );
		double y  = theCenter.y - ( theDirection.y() * ( off + iw ) );
				
		if( forShadow ) {
			x += shadowable.theShadowOff.x;
			y += shadowable.theShadowOff.y;
		}	
				
		p = camera.transformGuToPx( x, y, 0 );	// Pass to pixels, the image will be drawn in pixels.
		angle = Math.acos( theDirection.dotProduct( 1, 0 ) );
				
		if( theDirection.y() > 0 )			// The angle is always computed for acute angles
			angle = ( Math.PI - angle );
	}

	@Override
	public void render(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skeleton) {
		Graphics2D g = bck.graphics2D();

 		make( false, camera );
 		// stroke( g, theShape )
 		// fill( g, theShape, camera )
 		
 		if( image != null ) {
 			AffineTransform Tx = g.getTransform();
 			AffineTransform Tr = new AffineTransform();
 			
 			g.setTransform( Tr );										// An identity matrix.
 			Tr.translate( p.x, p.y );									// 3. Position the image at its position in the graph.
 			Tr.rotate( angle );											// 2. Rotate the image from its center.
 			Tr.translate( -image.getWidth()/2, -image.getHeight()/2 );	// 1. Position in center of the image.
 			g.drawImage( image, Tr, null );								// Paint the image.
 			g.setTransform( Tx );										// Restore the original transform
 		}
	}

	@Override
	public void renderShadow(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skeleton) {
		//make( true, camera );
		//shadowable.cast( g, theShape );
	}	
}