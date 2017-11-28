package org.graphstream.ui.swing.renderer.shape.swing.spriteShapes;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.geom.Vector2;
import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.SpriteOrientation;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.ConnectorSkeleton;
import org.graphstream.ui.swing.renderer.Skeleton;
import org.graphstream.ui.swing.renderer.Skeleton.Triplet;
import org.graphstream.ui.swing.renderer.shape.Decorable;
import org.graphstream.ui.swing.renderer.shape.Shape;
import org.graphstream.ui.swing.renderer.shape.swing.shapePart.FillableLine;
import org.graphstream.ui.swing.renderer.shape.swing.shapePart.ShadowableLine;
import org.graphstream.ui.swing.renderer.shape.swing.shapePart.StrokableLine;
import org.graphstream.ui.view.util.CubicCurve;

public class SpriteFlowShape implements Shape {
	FillableLine fillableLine ;
	StrokableLine strokableLine ;
	ShadowableLine shadowableLine ;
	Decorable decorable ;
	
	double theSize = 0.0;
	double along = 0.0;
	double offset = 0.0;
	ConnectorSkeleton connectorSkel = null ;
	GeneralPath theShape = new GeneralPath() ;
	boolean reverse = false ;
	
	public SpriteFlowShape() {
		this.fillableLine = new FillableLine() ;
		this.strokableLine = new StrokableLine() ;
		this.shadowableLine = new ShadowableLine() ;
		this.decorable = new Decorable() ;
	}
	
	@Override
	public void configureForGroup(Backend bck, Style style, SwingDefaultCamera camera) {
		theSize = camera.getMetrics().lengthToGu(style.getSize(), 0);
		reverse = (style.getSpriteOrientation() == SpriteOrientation.FROM);
		
		fillableLine.configureFillableLineForGroup(bck, style, camera, theSize);
		strokableLine.configureStrokableForGroup(style, camera);
		shadowableLine.configureShadowableLineForGroup(style, camera);
		decorable.configureDecorableForGroup(style, camera);
	}

	@Override
	public void configureForElement(Backend bck, GraphicElement element, Skeleton skel,	SwingDefaultCamera camera) {
		GraphicSprite sprite = (GraphicSprite)element;
			
		if( sprite.isAttachedToEdge() ) {
			GraphicEdge edge = sprite.getEdgeAttachment();
			
			fillableLine.configureFillableLineForElement( element.getStyle(), camera, element );
			decorable.configureDecorableForElement( bck, camera, element, skel );
		
			if( element.hasAttribute( "ui.size" ) )
				theSize = camera.getMetrics().lengthToGu( StyleConstants.convertValue( element.getAttribute( "ui.size" ) ) );
			
			along    = element.getX();
			offset   = camera.getMetrics().lengthToGu( element.getY(), sprite.getUnits() );
			connectorSkel = (ConnectorSkeleton)edge.getAttribute( Skeleton.attributeName );
		} 
		else {
			connectorSkel = null;
		}
	}

	@Override
	public void make(Backend backend, SwingDefaultCamera camera) {
		make(backend.graphics2D(), camera, 0, 0 );
	}

	@Override
	public void makeShadow(Backend backend, SwingDefaultCamera camera) {
		make(backend.graphics2D(), camera, shadowableLine.theShadowOff.x, shadowableLine.theShadowOff.y );
	}

	private void make(Graphics2D graphics2d, SwingDefaultCamera camera, double shx, double shy) {
		// EdgeInfo contains a way to compute points perpendicular to the shape, however here
		// we only need to compute the perpendicular vector once, hence this code.
		if(connectorSkel != null) {
	        if(connectorSkel.isCurve()) {
	        	Point3 P0 = connectorSkel.apply(0);
	        	if(reverse) 
	        		P0 = connectorSkel.apply(3) ;
	        	Point3 P1 = connectorSkel.apply(1) ;
	        	if(reverse)
	        		P1 = connectorSkel.apply(2) ;
	        	Point3 P2 = connectorSkel.apply(2) ;
	        	if(reverse) 
	        		P2 = connectorSkel.apply(1) ;
	        	Point3 P3 = connectorSkel.apply(3) ;
	        	if(reverse)
	        		P3 = connectorSkel.apply(0) ;
				double inc = 0.01;
				double t   = 0.0;
				Vector2 dir = new Vector2(P3.x-P0.x, P3.y-P0.y);
				Vector2 per = new Vector2(dir.y() + shx, -dir.x() + shy);
				
				per.normalize();
				per.scalarMult(offset);
				theShape.reset();
				theShape.moveTo(P0.x + per.x(), P0.y + per.y());
				
				while(t <= along) {
					theShape.lineTo(
						CubicCurve.eval(P0.x + per.x(), P1.x + per.x(), P2.x + per.x(), P3.x + per.x(), t),
						CubicCurve.eval(P0.y + per.y(), P1.y + per.y(), P2.y + per.y(), P3.y + per.y(), t)
					);
					
					t += inc;
				}
	        } 
	        else if(connectorSkel.isPoly()) {
	            Point3 P0 = connectorSkel.from() ;
	            if(reverse)
	            	P0 = connectorSkel.to() ;
	            Point3 P1 = connectorSkel.to();
	            if(reverse)
	            	P1 = connectorSkel.from() ;
	            double a = along ;
	            if(reverse)
	            	a = 1-along;
	           Triplet<Integer, Double, Double> triplet = connectorSkel.wichSegment(a);
	           int i = triplet.i ;
	           double sum = triplet.sum;
	           double ps = triplet.ps ;
	           
	           Vector2 dir = new Vector2(P1.x-P0.x, P1.y-P0.y);
	           Vector2 per = new Vector2(dir.y() + shx, -dir.x() + shy);
	            
				per.normalize();
				per.scalarMult(offset);
				
				theShape.reset();

				if(reverse) {
				    int n = connectorSkel.size();
	                sum = connectorSkel.length() - sum;
	                ps  = 1-ps;
	                theShape.moveTo(P1.x+per.x(), P1.y+per.y());
	                for(int j = n-2 ; j < i ; j--) {
	                	theShape.lineTo(connectorSkel.apply(j).x + per.x(), connectorSkel.apply(j).y + per.y());
	                }
	                Point3 PX = connectorSkel.pointOnShape(along);
	                theShape.lineTo(PX.x+per.x(), PX.y+per.y());
	            } 
				else {
	                theShape.moveTo(P0.x+per.x(), P0.y+per.y());
	                for(int j = 1 ; j <= i ; j++) {
	                	theShape.lineTo(connectorSkel.apply(j).x + per.x(), connectorSkel.apply(j).y + per.y());
	                }
	                Point3 PX = connectorSkel.pointOnShape(along);
	                theShape.lineTo(PX.x+per.x(), PX.y+per.y());
	            }
	        } 
	        else {
	            Point3 P0 = connectorSkel.from();
	            if(reverse) 
	            	P0 = connectorSkel.to() ;
	            Point3 P1 = connectorSkel.to() ;
	            if(reverse) 
	            	P1 = connectorSkel.from() ;
	            Vector2 dir = new Vector2(P1.x-P0.x, P1.y-P0.y);
	            Vector2 per = new Vector2(dir.y() + shx, -dir.x() + shy);

				per.normalize();
				per.scalarMult(offset);
				dir.scalarMult(along);

				theShape.reset();
				theShape.moveTo(P0.x + per.x(), P0.y + per.y());
				theShape.lineTo(P0.x + dir.x() + per.x(), P0.y + dir.y() + per.y());
	        }
	    }
	    
//		if( connectorSkel != null ) {
//			var P0  = if( reverse ) connectorSkel.to else connectorSkel.from
//			var P3  = if( reverse ) connectorSkel.from else connectorSkel.to
//			val dir = Vector2( P3.x-P0.x, P3.y-P0.y )
//			val per = Vector2( dir.y + shx, -dir.x + shy )
//			
//			per.normalize
//			per.scalarMult( offset )
//			theShape.reset
//			theShape.moveTo( P0.x + per.x, P0.y + per.y  )
//			
//			if( connectorSkel.isCurve ) {
//				val P1  = if( reverse ) connectorSkel(2) else connectorSkel(1)
//				val P2  = if( reverse ) connectorSkel(1) else connectorSkel(2)
//				val inc = 0.01f
//				var t   = 0f
//				
//				while( t <= along ) {
//					theShape.lineTo(
//						CubicCurve.eval( P0.x + per.x, P1.x + per.x, P2.x + per.x, P3.x + per.x, t ),
//						CubicCurve.eval( P0.y + per.y, P1.y + per.y, P2.y + per.y, P3.y + per.y, t )
//					)
//					
//					t += inc
//				}
//			} else {
//				val dir = Vector2( P3.x-P0.x, P3.y-P0.y )
//				dir.scalarMult( along )
//				theShape.lineTo( P0.x + dir.x + per.x, P0.y + dir.y + per.y )
//			}
//		}
	}

	@Override
	public void render(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skel) {
		if( connectorSkel != null ) {
 		    Graphics2D g = bck.graphics2D();
 			make(bck, camera);
 			strokableLine.stroke( g, theShape );
 			fillableLine.fill( g, theSize, theShape );
 			decorable.decorConnector( bck, camera, skel.iconAndText, element, theShape );
 		}
	}

	@Override
	public void renderShadow(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skeleton) {
		if( connectorSkel != null ) {
			makeShadow(bck, camera);
			shadowableLine.cast(bck.graphics2D(), theShape );
		}
	}
	
}
