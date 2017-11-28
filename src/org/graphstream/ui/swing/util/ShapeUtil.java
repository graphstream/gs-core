package org.graphstream.ui.swing.util;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.ShapeKind;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.StrokeMode;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.AreaSkeleton;
import org.graphstream.ui.swing.renderer.ConnectorSkeleton;
import org.graphstream.ui.swing.renderer.Skeleton;

public class ShapeUtil {
	
	/** Try to evaluate the "radius" of the edge target node shape along the edge. In other words
	  * this method computes the intersection point between the edge and the node shape contour.
	  * The returned length is the length of a line going from the center of the shape toward
	  * the point of intersection between the target node shape contour and the edge.
	  * @param edge The edge (it contains its target node).
	  * @param camera the camera.
	  * @return The radius. */
	public static double evalTargetRadius2D(GraphicEdge edge, SwingDefaultCamera camera) {
		ConnectorSkeleton eskel = (ConnectorSkeleton)edge.getAttribute(Skeleton.attributeName);
		if(eskel != null) {
			return evalTargetRadius2D(
	    		edge.to.getStyle(),
	    		(AreaSkeleton)edge.to.getAttribute(Skeleton.attributeName),
	    		new Point3(eskel.from().x, eskel.from().y, eskel.from().z),
	    		new Point3(eskel.to().x, eskel.to().y, eskel.to().z),
	   	 		camera);
		} 
		else {
			throw new RuntimeException("no skeleton on edge ??");
		}
	}
	
	
	/** Try to evaluate the "radius" of the given node considering an edge between points `from` and `to`.
	  * In other words, this method computes the intersection point between the edge and the node
	  * shape contour. The returned length is the length of a line going from the center of the shape
	  * toward the point of intersection between the target node shape contour and the edge.
	  * @param from The origin point of the edge.
	  * @param to The target point of the edge.
	  * @param node The target node shape.
	  * @param the camera.
	  * @return The radius. */
	public static double evalTargetRadius2D(Point3 from, Point3 to, GraphicNode node, SwingDefaultCamera camera) {
		return evalTargetRadius2D(node.getStyle(), (AreaSkeleton)node.getAttribute(Skeleton.attributeName),
	            from, null, null, to, camera);
	}
	
	
	/** Try to evaluate the "radius" of the given area skeleton considering an edge between points `p0` and
	  * point `p3` (the edge is considered a straight line). In other words, this method computes the intersection
	  * point between the edge and the area geometry. The returned length of the line going from the center of
	  * the skeleton geometry toward the point of intersection between the skeleton geometry and the edge.
	  * @param style The style of the area skeleton.
	  * @param skeleton The skeleton.
	  * @param p0 The origin point of the edge.
	  * @param p3 the target point of the edge.
	  * @param camera the camera.
	  * @return the radius. */
  	public static double evalTargetRadius2D(Style style, AreaSkeleton skeleton, Point3 p0, Point3 p3, SwingDefaultCamera camera) {
  		return evalTargetRadius2D(style, skeleton, p0, null, null, p3, camera);
  	}
  	
  	/** Try to evaluate the "radius" of the given area skeleton considering a cubic curve edge between points `p0` and
	  * point `p3` and curving to control points `p1`  and `p2`. In other words, this method computes the intersection
	  * point between the edge and the area geometry. The returned length of the line going from the center of
	  * the skeleton geometry toward the point of intersection between the skeleton geometry and the edge.
	  * @param edge The edge.
	  * @param p0 The origin point of the edge.
	  * @param p3 the target point of the edge.
	  * @param camera the camera.
	  * @return the radius. */
 	public static double evalTargetRadius2D(GraphicEdge edge, Point3 p0, Point3 p1, Point3 p2, Point3 p3, SwingDefaultCamera camera) {
 		return evalTargetRadius2D(edge.to.getStyle(),
 	 			(AreaSkeleton)edge.to.getAttribute(Skeleton.attributeName),
 	 			p0, p1, p2, p3, camera);
 	}
 	
 	/** Try to evaluate the "radius" of the given area skeleton considering a cubic curve edge between points `p0` and
	  * point `p3` and curving to control points `p1`  and `p2`. In other words, this method computes the intersection
	  * point between the edge and the area geometry. The returned length of the line going from the center of
	  * the skeleton geometry toward the point of intersection between the skeleton geometry and the edge.
	  * @param style The style of the area skeleton.
	  * @param skeleton The skeleton.
	  * @param p0 The origin point of the edge.
	  * @param p3 the target point of the edge.
	  * @param camera the camera.
	  * @return the radius. */
 	public static double evalTargetRadius2D(Style style, AreaSkeleton skeleton, Point3 p0, Point3 p1, Point3 p2, Point3 p3, SwingDefaultCamera camera) { 
 	  	double w = 0.0;
 	  	double h = 0.0;
 	  	double s = 0f ;
 	  	if(style.getStrokeMode() != StrokeMode.NONE) 
 	  		s = camera.getMetrics().lengthToGu(style.getStrokeWidth());

 	  	if(skeleton != null) {
 	  		w = skeleton.theSize.x;
 	  		h = skeleton.theSize.y;
 	  	}
 	  	else {
 	  		w = camera.getMetrics().lengthToGu(style.getSize(), 0);
 	  		h = w ;
 	  		if(style.getSize().size() > 1)
 	  			h = camera.getMetrics().lengthToGu(style.getSize(), 1);
 	  		
 	  	}
 	  	
 	  	switch (style.getShape()) {
			case CIRCLE:		return evalEllipseRadius2D(p0, p1, p2, p3, w, h, s);
			case DIAMOND:		return evalEllipseRadius2D(p0, p1, p2, p3, w, h, s);
			case CROSS: 		return evalEllipseRadius2D(p0, p1, p2, p3, w, h, s);
			case TRIANGLE: 		return evalEllipseRadius2D(p0, p1, p2, p3, w, h, s);
			case TEXT_CIRCLE:	return evalEllipseRadius2D(p0, p1, p2, p3, w, h, s);
			case TEXT_DIAMOND:	return evalEllipseRadius2D(p0, p1, p2, p3, w, h, s);
			case PIE_CHART:		return evalEllipseRadius2D(p0, p1, p2, p3, w, h, s);
			case BOX:			return evalBoxRadius2D(p0, p1, p2, p3, w/2+s, h/2+s);
			case ROUNDED_BOX:	return evalBoxRadius2D(p0, p1, p2, p3, w/2+s, h/2+s);
			case TEXT_BOX:		return evalBoxRadius2D(p0, p1, p2, p3, w/2+s, h/2+s);
			case JCOMPONENT:	return evalBoxRadius2D(p0, p1, p2, p3, w/2+s, h/2+s);
			default: 			return evalBoxRadius2D(p0, p1, p2, p3, w/2+s, h/2+s) ;
		}
	}
 	
 	/** Compute the length of a (eventually cubic curve) vector along the edge from the ellipse center toward the intersection
  	  * point with the ellipse that match the ellipse radius. If `p1` and `p2` are null, the edge is considered
  	  * a straight line, else a cubic curve.
  	  * @param p0 the origin point of the edge
  	  * @param p1 the first cubic-curve control point or null for straight edge.
  	  * @param p2 the second cubic-curve control point or null for straight edge.
  	  * @param p3 the target point of the edge.
  	  * @param w the width of the ellipse.
  	  * @param h the height of the ellipse.
  	  * @param s the width of the stroke of the ellipse shape.
  	  */
 	public static double evalEllipseRadius2D(Point3 p0, Point3 p1, Point3 p2, Point3 p3, double w, double h, double s) {
 	  	if(w == h)
 	  		return (w / 2 + s) ;	// Welcome simplification for circles ...
 	  	else
 	  		return evalEllipseRadius2D(p0, p1, p2, p3, w/2 + s, h/2 + s);
 	}
 	
 	/** Compute the length of a vector along the edge from the ellipse center that match the
	  * ellipse radius.
	  * @param edge The edge representing the vector.
	  * @param w The ellipse first radius (width/2).
	  * @param h The ellipse second radius (height/2).
	  * @return The length of the radius along the edge vector. */
	public static double evalEllipseRadius2D(Point3 p0, Point3 p1, Point3 p2, Point3 p3, double w, double h) {
 	  	if(w == h) {
 	  		return (w / 2);	// Welcome simplification for circles ...
 	  	} 
 	  	else {
			// Vector of the entering edge.
	
			double dx = 0.0;
			double dy = 0.0;
	
			if(p1 != null && p2 != null) {
				dx = p3.x - p2.x; //( p2.x + ((p1.x-p2.x)/4) )	// Use the line going from the last control-point to target
				dy = p3.y - p2.y ;//( p2.y + ((p1.y-p2.y)/4) )	// center as the entering edge.
			} 
			else {
				dx = p3.x - p0.x;
				dy = p3.y - p0.y;
			}
			
			// The entering edge must be deformed by the ellipse ratio to find the correct angle.
	
			dy *= w / h;
	
			// Find the angle of the entering vector with (1,0).
	
			double d  = Math.sqrt(dx*dx + dy*dy);
			double a  = dx / d;
	
			// Compute the coordinates at which the entering vector and the ellipse cross.
	
			a  = Math.acos(a);
			dx = Math.cos(a) * w;
			dy = Math.sin(a) * h;
	
			// The distance from the ellipse center to the crossing point of the ellipse and
			// vector. Yo !
	
			return Math.sqrt(dx*dx + dy*dy);
 	  	}
	}
	
	/** Compute the length of a vector along the edge from the box center that match the box
	  * "radius".
	  * @param edge The edge representing the vector.
	  * @param w The box first radius (width/2).
	  * @param h The box second radius (height/2).
	  * @return The length of the radius along the edge vector. */
	public static double evalBoxRadius2D(Point3 p0, Point3 p1, Point3 p2, Point3 p3, double w, double h) {
		// Pythagora : Angle at which we compute the intersection with the height or the width.
	
		double da = w / (double)Math.sqrt(w*w + h*h);
		
		if(da < 0)
			da = -da;
		
		// Angle of the incident vector.
		double dx = 0.0;
		double dy = 0.0;

		if(p1 != null && p2 != null) {
			dx = p3.x - p2.x; // ( p2.x + ((p1.x-p2.x)/4) )	// Use the line going from the last control-point to target
			dy = p3.y - p2.y; //( p2.y + ((p1.y-p2.y)/4) )	// center as the entering edge.
		} 
		else {
			dx = p3.x - p0.x;
			dy = p3.y - p0.y;
		}
 
		double d = Math.sqrt(dx*dx + dy*dy);
		double a = dx/d;
		
		if(a < 0) 
			a = -a ;
		
		// Choose the side of the rectangle the incident edge vector crosses.
		
		if(da < a) {
			return (w / a);
		}
		else {
			a = dy/d;
			if(a < 0)
				a = -a;
			return (h / a);
		}
	}
	
	/** Compute if point `p` is inside of the shape of `elt` whose overall size is `w` x `h`. */
	public static boolean isPointIn(GraphicElement elt, Point3 p, double w, double h) {
		if ( elt.getStyle().getShape().kind == ShapeKind.RECTANGULAR) {
			return isPointIn2DBox( p, elt.getX(), elt.getY(), w, h ) ;
		}
		else if ( elt.getStyle().getShape().kind == ShapeKind.ELLIPSOID) {
			return isPointIn2DEllipse( p, elt.getX(), elt.getY(), w, h ) ;
		}
		else {
			return false ;
		}
	}
	
	/** Compute if point `p` is inside of a rectangular shape of overall size `w` x `h`. */
	public static boolean isPointIn2DBox(Point3 p, double x, double y, double w, double h) {
		double w2 = w/2;
		double h2 = h/2;
		return ( p.x > (x-w2) && p.x < (x+w2) && p.y > (y-h2) && p.y < (y+h2) );
	}
	
	/** Compute if point `p` is inside of a ellipsoid shape of overall size `w` x `h`. */
	public static boolean isPointIn2DEllipse(Point3 p, double x, double y, double w, double h) {
		double xx = p.x - x;
		double yy = p.y - y;
		double w2 = w/2;
		double h2 = h/2;
		
		return (((xx*xx)/(w2*w2)) + ((yy*yy)/(h2*h2)) < 1);
	}
}
