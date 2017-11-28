package org.graphstream.ui.swing.util;

import org.graphstream.ui.geom.Point3;

public class EdgePoints {
	protected Point3[] points ;
	
	
	public EdgePoints(int n) {
		this.points = new Point3[n];
		
		for ( int i = 0 ; i < size() ; i++ )
			points[i] = new Point3();
	}


	public int size() {
		return points.length;
	}
	
	public void copy(Point3[] newPoints) {
		points = new Point3[newPoints.length];
		for ( int i = 0 ; i < size() ; i++ ) {
			points[i] = new Point3(newPoints[i]);
		}
	}
	
	public void set(int i, double x, double y, double z) {
		points[i].set(x, y, z);
	}
	
	public Point3 get(int i) {
		return points[i];
	}
	
	public Point3 apply(int i) {
		return points[i];
	}
	
	public void update(int i, Point3 coos) {
		points[i] = new Point3(coos.x, coos.y, coos.z);
	}
	
	@Override
	public String toString() {
		String s = "pts(";
		for (int i = 0 ; i < size() ; i++)
			s += points[i].toString()+", " ;
		s += ") : "+size();
		
		return s ;
	}
}
