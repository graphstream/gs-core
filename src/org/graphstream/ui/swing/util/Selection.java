package org.graphstream.ui.swing.util;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.swing.renderer.SelectionRenderer;

public class Selection {
	
	private boolean active = false;
	private Point3 lo = new Point3() ;
	private Point3 hi = new Point3() ;
	private SelectionRenderer renderer = null ;
	
	public void begins(double x, double y) {
		lo.x = x ;
		lo.y = y ;
		hi.x = x ;
		hi.y = y ;
	}
	
	public void grows(double x, double y) {
		hi.x = x ;
		hi.y = y ;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public double x1() {
		return lo.x ;
	}
	
	public double x2() {
		return hi.x ;
	}
	
	public double y1() {
		return lo.y ;
	}
	
	public double y2() {
		return hi.y ;
	}
	
	public double z1() {
		return lo.z ;
	}
	
	public double z2() {
		return hi.z ;
	}

	public SelectionRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(SelectionRenderer renderer) {
		this.renderer = renderer;
	}
}
