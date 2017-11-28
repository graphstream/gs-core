package org.graphstream.ui.scalaViewer.renderer;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.geom.Vector2;
import org.graphstream.ui.geom.Vector3;
import org.graphstream.ui.scalaViewer.util.AttributeUtils;
import org.graphstream.ui.scalaViewer.util.EdgePoints;
import org.graphstream.ui.view.util.CubicCurve;

/** Skeleton for edges.
 * Data stored on the edge to retrieve the edge basic geometry and various shared data between
 * parts of the renderer.
 * 
 * XXX TODO
 * This part needs much work. The skeleton geometry of an edge can be various things:
 *  - An automatically computed shape (for multi-graphs and loop edges).
 *  - An user specified shape:
 *     - A polyline (points are in absolute coordinates).
 *     - A polycurve (in absolute coordinates).
 *     - A vector representation (points are relative to an origin and the whole may be rotated).  
 */
public class ConnectorSkeleton extends Skeleton implements AttributeUtils {
	
	private EdgePoints points;
	private double[] lengths;
	private double lengthsSum;
	private EdgeShapeKind kind;
	private boolean isACurve;
	private int aMulti;
	private boolean isALoop;

	public ConnectorSkeleton() {
		this.points = new EdgePoints(2);
		this.lengths = null; 
		this.lengthsSum = -1.0 ;
		this.kind = EdgeShapeKind.LINE ;
		this.isACurve = false ;
		this.aMulti = 1 ;
		this.isALoop = false ;
	}
	
	@Override
	public String toString() {
		return "CtorSkel("+kindString()+", {"+points.toString()+"})";
	}

	public String kindString() {
		if ( kind == EdgeShapeKind.POLYLINE )
			return "polyline";
		else if ( kind == EdgeShapeKind.CURVE )
			return "curve";
		else
			return "line";
	}
	
	/** If true the edge shape is a polyline made of size points. */
	public boolean isPoly() {
		return (kind == EdgeShapeKind.POLYLINE);
	}
	
	/** If true the edge shape is a loop defined by four points. */
	public boolean isCurve() {
		return (kind == EdgeShapeKind.CURVE);
	}
	
	/** If larger than one there are several edges between the two nodes of this edge. */
	public int multi() {
		return aMulti;
	}
	
	/** This is only set when the edge is a curve, if true the starting and
	 * ending nodes of the edge are the same node. */
	public boolean isLoop() {
		return isALoop;
	}
	
	public void setPoly(Object aSetOfPoints) {
		if (!kind.equals(EdgeShapeKind.POLYLINE)) {
			kind = EdgeShapeKind.POLYLINE ;
			Point3[] thePoints = getPoints(aSetOfPoints);
			points = new EdgePoints(thePoints.length);
			points.copy(thePoints);
			lengths = null ;
		}
	}
	
	public void setPoly(Point3[] aSetOfPoints) {
		if (points == null || points.size() != aSetOfPoints.length) {
			points = new EdgePoints(aSetOfPoints.length);
		}
		
		kind = EdgeShapeKind.POLYLINE ;
		
		for ( int i = 0 ; i < aSetOfPoints.length ; i++) {
			points.set(i, aSetOfPoints[i].x, aSetOfPoints[i].y, aSetOfPoints[i].z);
		}
	}
	
	public void setCurve(double x0, double y0, double z0,
			double x1, double y1, double z1,
			double x2, double y2, double z2,
			double x3, double y3, double z3) {
		kind = EdgeShapeKind.CURVE ;
		if(points.size() != 4)
			points = new EdgePoints(4);
		points.update(0, new Point3(x0, y0, z0));
		points.update(1, new Point3(x1, y1, z1));
		points.update(2, new Point3(x2, y2, z2));
		points.update(3, new Point3(x3, y3, z3));
	}
	
	public void setLine(double x0, double y0, double z0, double x1, double y1, double z1){
		kind = EdgeShapeKind.LINE ;
		if(points.size() != 2)
			points = new EdgePoints(2);
		points.update(0, new Point3(x0, y0, z0));
		points.update(1, new Point3(x1, y1, z1));
	}
	
	public void setMulti(int aMulti) {
		this.aMulti = aMulti;
	}
	
	public boolean isMulti() {
		return (multi() > 1);
	}
	
	public void setLoop(double x0, double y0, double z0,
			double x1, double y1, double z1,
			double x2, double y2, double z2) {
		kind = EdgeShapeKind.CURVE ;
		if(points.size() != 4)
			points = new EdgePoints(4);
		isALoop = true;
		points.update(0, new Point3(x0, y0, z0));
		points.update(1, new Point3(x1, y1, z1));
		points.update(2, new Point3(x2, y2, z2));
		points.update(3, new Point3(x0, y0, z0));
	}
	
	/** The number of points in the edge shape. */
	public int size() {
		return points.size();
	}
	
	/** The i-th point of the edge shape. */
	public Point3 apply(int i) {
		return points.get(i);
	}
	
	/** Change the i-th point in the set of points making up the shape of this edge. */
	public void update(int i, Point3 p) {
		points.update(i, p);
	}
	
	/** The last point of the edge shape. */
	public Point3 to() {
		return points.get(points.size()-1);
	}
	
	/** The first point of the edge shape. */
	public Point3 from() {
		return points.get(0);
	}
	
	/**
	 * Total length of the polyline defined by the points.
	 */
	public double length() {
		if(lengths == null)
			segmentsLengths();
		
		return lengthsSum;
	}

	/** Compute the length of each segment between the points making up this edge. This is mostly
	  * only useful for polylines. The results of this method is cached. It is only recomputed when
	  * a points changes in the shape. There are size-1 segments if the are size points. The segment
	  * 0 is between points 0 and 1. */
	public double[] segmentsLengths() {
		if( lengths == null ) {
			if(isPoly()) {
				int n = points.size() ;
				lengthsSum = 0;
				if(n > 0) {
					lengths = new double[points.size() - 1];
					Point3 prev = points.get(0);
					Point3 next = null ;
					
					for (int i = 1 ; i < n ; i++) {
						next = points.get(i);
						lengths[i-1] = next.distance(prev);
						lengthsSum += lengths[i-1];
						prev = next ;
					}
				}
				else {
					lengths = new double[0];
				}
			}
			else if (isCurve()) {
				throw new RuntimeException("segmentsLengths for curve ....");
			}
			else {
				lengths = new double[1];
				lengths[0] = points.get(0).distance(points.get(3));
				lengthsSum = lengths[0];
			}
		}
		
		return lengths;
	}
	
	/** Length of the i-th segment. There are size-1 segments if there are size points. The segment
	 * 0 is between points 0 and 1. */
	public double segmentLength(int i) {
		return segmentsLengths()[i];
	}
	
	/** Compute a point at the given percent on the shape and return it.
	 * The percent must be a number between 0 and 1. */
	public Point3 pointOnShape(double percent) {
		return pointOnShape(percent, new Point3());
	}
	
	/** Compute a point at a given percent on the shape and store it in the target,
	 * also returning it. The percent must be a number between 0 and 1. */
	public Point3 pointOnShape(double percent, Point3 target) {
		double at = percent ;
		if(percent > 1)
			at = 1 ;
		if(at < 0)
			at = 0 ;
		
		if( isCurve() ) {
			CubicCurve.eval(points.get(0), points.get(1), points.get(2), points.get(3), at, target);
		}
		else if( isPoly() ) {
			Triplet<Integer, Double, Double> triplet = wichSegment(at);
			int i = triplet.i ;
			double sum = triplet.sum ;
			double ps = triplet.ps ;
			
			Vector3 dir = new Vector3(points.get(i+1).x-points.get(i).x, points.get(i+1).y-points.get(i).y, 0);
			dir.scalarMult(ps);
			target.set(points.get(i).x + dir.data[0], points.get(i).y + dir.data[1], points.get(i).z);
		}
		else {
			Vector3 dir = new Vector3(to().x-from().x, to().y-from().y, 0);
			dir.scalarMult(at);
			target.set(from().x+dir.data[0], from().y+dir.data[1]);
		}
		
		return target ;
	}
	
	/** Compute a point at a given percent on the shape and push it from the shape perpendicular
	 * to it at a given distance in GU. The percent must be a number between 0 and 1. The resulting
	 * points is returned. */
	public Point3 pointOnShapeAndPerpendicular(double percent, double perpendicular) {
		return pointOnShapeAndPerpendicular(percent, perpendicular, new Point3());
	}
	
	/** Compute a point at a given percent on the shape and push it from the shape perpendicular
	 * to it at a given distance in GU. The percent must be a number between 0 and 1. The result
	 * is stored in target and also returned. */
	public Point3 pointOnShapeAndPerpendicular(double percent, double perpendicular, Point3 target) {
		double at = percent ;
		if(percent > 1)
			at = 1 ;
		if(at < 0)
			at = 0 ;
		
		
		if ( isCurve() ) {
			Point3 p0 = points.get(0);
			Point3 p1 = points.get(1);
			Point3 p2 = points.get(2);
			Point3 p3 = points.get(3);
			Vector2 perp = CubicCurve.perpendicular(p0, p1, p2, p3, at);
			
			perp.normalize();
			perp.scalarMult(perpendicular);
			
			target.x = CubicCurve.eval(p0.x, p1.x, p2.x, p3.x, at) - perp.data[0];
			target.y = CubicCurve.eval(p0.y, p1.y, p2.y, p3.y, at) - perp.data[1];
			target.z = 0 ;
		}
		else if( isPoly() ) {
			Triplet<Integer, Double, Double> triplet = wichSegment(at);
			int i = triplet.i ;
			double sum = triplet.sum ;
			double ps = triplet.ps ;
			
			Vector3 dir = new Vector3(points.get(i+1).x-points.get(i).x, points.get(i+1).y-points.get(i).y, 0);
			Vector3 perp = new Vector3(dir.data[1], -dir.data[0], 0);
			
			perp.normalize();
			perp.scalarMult(perpendicular);
			dir.scalarMult(ps);
			target.set(points.get(i).x+dir.data[0]+perp.data[0], points.get(i).y+dir.data[1]+perp.data[1], points.get(i).z);
		}
		else {
			Vector3 dir = new Vector3(to().x-from().x, to().y-from().y, 0);
			Vector3 perp = new Vector3(dir.data[1], -dir.data[0], 0);
			
			perp.normalize();
			perp.scalarMult(perpendicular);
			dir.scalarMult(at);
			target.set(from().x+dir.data[0]+perp.data[0], from().y+dir.data[1]+perp.data[1], from().z);
		}
		
		return target ;
	}
	
	/** On which segment of the line shape is the value at. The value at must be between 0 and 1
	 * and expresses a percentage on the shape. There are size-1 segments if size is the number
	 * of points of the shape. The segment 0 is between points 0 and 1. This method both compute
	 * the index of the segment, but also the sum of the previous segments lengths (not including
	 * the i-th segment), as well as the percent on the segment (a number in 0..1). */
	public Triplet<Integer, Double, Double> wichSegment(double at) {
		int n = size()-1 ; // n-1 segments, for n points
		double pos = length()*at ; // Length is the sum of all segments lengths
		double sum = lengths[0];
		int i = 0 ;
		
		while(pos > sum) {
			i++ ;
			sum += lengths[i];
		}
		
		assert(i>=0 && i<n);
		
		sum -= lengths[i];
		
		return new Triplet<Integer, Double, Double>(i, sum, (pos-sum)/lengths[i]);
	}
}