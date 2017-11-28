package org.graphstream.ui.swing.renderer.shape;

import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicEdge.EdgeGroup;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.AreaSkeleton;
import org.graphstream.ui.swing.renderer.ConnectorSkeleton;
import org.graphstream.ui.swing.renderer.Skeleton;

public class Connector extends HasSkel {
	// Attribute

	/** The edge, we will also need it often. */
	public GraphicEdge theEdge = null;

	/** Width of the connector. */
	public double theSize = 0;
		
	/** Overall size of the area at the end of the connector. */
	public Point2 theTargetSize = new Point2(0, 0);

	/** Overall sizes of the area at the end of the connector. */
	public Point2 theSourceSize = new Point2(0, 0);

	/** Is the connector directed ? */
	public boolean isDirected = false;
		
	// Command
		
	/** Origin point of the connector. */
	public Point3 fromPos() {
		return skel.from();
	}
	
	/** First control point. Works only for curves. */
	public Point3 byPos1() {
		if(skel.isCurve())
			return skel.apply(1) ;
		else return null;
	}
	
	/** Second control point. Works only for curves. */
	public Point3 byPos2() {
		if(skel.isCurve())
			return skel.apply(2) ;
		else return null;
	}
	
	/** Destination of the connector. */
	public Point3 toPos() {
		return skel.to();
	}

	public void configureConnectorForGroup(Style style, SwingDefaultCamera camera) {
		sizeForGroup(style, camera);
	}

	public void configureConnectorForElement(SwingDefaultCamera camera, GraphicEdge element, ConnectorSkeleton skel) {
		this.skel = skel;
		this.theEdge = element;
			    
		sizeForElement(element.getStyle(), camera, element);
		endPoints(element.from, element.to, element.isDirected(), camera);
				
		if(element.getGroup() != null) {
			skel.setMulti(element.getGroup().getCount());
		}
				
		// XXX TODO there are a lot of cases where we do not need this information.
		// It would be good to compute it lazily, only when needed;
		// Furthermore, it would be good to be able to update it, only when really
		// Changed.
		// There is lots of work to be done here, in order to extend the way we get
		// the points of the skeleton. Probably a PointVector class that can tell
		// when some of its parts changed.
		if(element.hasAttribute("ui.points")) {
			skel.setPoly(element.getAttribute("ui.points"));
		} else {
			positionForLinesAndCurves( skel, element.from.getStyle(), element.from, 
					element.to, element.multi, element.getGroup() );
		}
	}

	/** Set the size of the connector using the predefined style. */
	private void sizeForGroup(Style style, SwingDefaultCamera camera) {
		theSize = camera.getMetrics().lengthToGu( style.getSize(), 0 ) ;
	}
	
	/** Set the size of the connector for this particular `element`. */
	private void sizeForElement(StyleGroup style, SwingDefaultCamera camera, GraphicEdge element) {
		if(style.getSizeMode() == StyleConstants.SizeMode.DYN_SIZE && element.hasAttribute( "ui.size")) {
			theSize = camera.getMetrics().lengthToGu(StyleConstants.convertValue(element.getAttribute("ui.size")));
		}			
	}
	
	/** Define the two end points sizes using the fit size stored in the nodes. */
	public void endPoints(GraphicNode from, GraphicNode to, boolean directed, SwingDefaultCamera camera) {
		AreaSkeleton fromInfo = (AreaSkeleton)from.getAttribute( Skeleton.attributeName );
		AreaSkeleton toInfo = (AreaSkeleton)to.getAttribute( Skeleton.attributeName );
		
		if(fromInfo != null && toInfo != null) {
			isDirected     = directed;
			theSourceSize.copy(fromInfo.theSize);
			theTargetSize.copy(toInfo.theSize);
		} else {
			endPoints(from.getStyle(), to.getStyle(), directed, camera);
		}
	}
	
	/** Define the two end points sizes (does not use the style nor the fit size). */
	public void endPoints(double sourceWidth, double targetWidth, boolean directed) {
	    theSourceSize.set(sourceWidth, sourceWidth);
	    theTargetSize.set(targetWidth, targetWidth);
		isDirected = directed;
	}
	
	/** Define the two end points sizes (does not use the style nor the fit size). */
	public void endPoints(double sourceWidth, double sourceHeight, double targetWidth, double targetHeight, boolean directed) {
	    theSourceSize.set(sourceWidth, sourceHeight);
	    theTargetSize.set(targetWidth, targetHeight);
		isDirected = directed;
	}
	
	public void endPoints(StyleGroup sourceStyle, StyleGroup targetStyle, boolean directed, SwingDefaultCamera camera) {
		double srcx = camera.getMetrics().lengthToGu(sourceStyle.getSize(), 0);
		double srcy = srcx ;
		if(sourceStyle.getSize().size() > 1)
			camera.getMetrics().lengthToGu(sourceStyle.getSize(), 1) ;
		double trgx = camera.getMetrics().lengthToGu(targetStyle.getSize(), 0);
		double trgy = trgx ; 
		if(targetStyle.getSize().size() > 1) 
			camera.getMetrics().lengthToGu(targetStyle.getSize(), 1) ;
				
		theSourceSize.set(srcx, srcy);
		theTargetSize.set(trgx, trgy);
		isDirected = directed;
	}
	
	public void positionForLinesAndCurves(ConnectorSkeleton skel, StyleGroup style, GraphicNode from, GraphicNode to) {
		positionForLinesAndCurves(skel, style, from, to, 0, null);
		
	}
	
	public void positionForLinesAndCurves(ConnectorSkeleton skel, StyleGroup style, GraphicNode from,
			GraphicNode to, int multi, EdgeGroup group) {
		if( group != null ) {
			if( from == to ) {
				positionEdgeLoop(skel, from.getX(), from.getY(), multi);
			}
			else {
				positionMultiEdge(skel, from.getX(), from.getY(), to.getX(), to.getY(), multi, group);
			}
		}
		else {
			if( from == to) {
				positionEdgeLoop(skel, from.getX(), from.getY(), 0);
			}
			else {
				// This does not mean the edge is not a curve, this means
				// that with what we know actually it is not a curve.
				// The style mays indicate a curve.
			    skel.setLine(from.getX(), from.getY(), 0, to.getX(), to.getY(), 0);
			    // XXX we will have to mutate the skel into a curve later.
			}		  
		}
		
	}

	private void positionEdgeLoop(ConnectorSkeleton skel, double x, double y, int multi) {
		double m = 1f + multi * 0.2f;
		double s = ( theTargetSize.x + theTargetSize.y ) / 2;
		double d = s / 2 * m + 4 * s * m;

		skel.setLoop(x, y, 0, x+d, y, 0, x, y+d, 0 );
	}
	

	private void positionMultiEdge(ConnectorSkeleton skel, double x1, double y1, double x2, double y2, int multi,
			EdgeGroup group) {
		double vx  = (  x2 - x1 );
		double vy  = (  y2 - y1 );
		double vx2 = (  vy ) * 0.6;
		double vy2 = ( -vx ) * 0.6;
		double gap = 0.2 ;
		double ox  = 0.0;
		double oy  = 0.0; 
		double f   = ( ( 1 + multi ) / 2 ) * gap ;// (1+multi)/2 must be done on integers.
		  
		vx *= 0.2;
		vy *= 0.2;
		  
		GraphicEdge main = group.getEdge( 0 );
		GraphicEdge edge = group.getEdge( multi );
		 
		if( group.getCount() %2 == 0 ) {
			ox = vx2 * (gap/2);
			oy = vy2 * (gap/2);
			if( ! edge.from.equals(main.from) ) {	// Edges are in the same direction.
				ox = - ox;
				oy = - oy;
			}
		}
		  
		vx2 *= f;
		vy2 *= f;
		
		double xx1 = x1 + vx;
		double yy1 = y1 + vy;
		double xx2 = x2 - vx;
		double yy2 = y2 - vy;
		
		double m = multi ;
		if( edge.from.equals(main.from) ) 
			m += 0;
		else
			m += 1;
				
		if( m % 2 == 0 ) {
			xx1 += ( vx2 + ox );
			yy1 += ( vy2 + oy );
			xx2 += ( vx2 + ox );
			yy2 += ( vy2 + oy );
		} 
		else {
			xx1 -= ( vx2 - ox );
			yy1 -= ( vy2 - oy );
			xx2 -= ( vx2 - ox );
			yy2 -= ( vy2 - oy );		  
		}
				
		skel.setCurve(
				x1, y1, 0,
				xx1, yy1, 0,
				xx2, yy2, 0,
				x2, y2, 0 );
	
	}
}

/** Thing that links to the skeleton of a connector. */
abstract class HasSkel {
	/** We will use it often, better store it. */
	public ConnectorSkeleton skel = null;
}