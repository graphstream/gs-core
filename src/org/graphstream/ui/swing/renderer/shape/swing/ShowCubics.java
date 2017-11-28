package org.graphstream.ui.swing.renderer.shape.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.swing.renderer.ConnectorSkeleton;
import org.graphstream.ui.view.Camera;

/** Utility trait to display cubics Bézier curves control polygons. */
public class ShowCubics {
	public boolean showControlPolygon = false ;
	
	/** Show the control polygons. */
    public void showCtrlPoints(Graphics2D g, Camera camera, ConnectorSkeleton skel) {
        if (showControlPolygon && skel.isCurve()) {
            Point3 from = skel.from();
            Point3 ctrl1 = skel.apply(1);
            Point3 ctrl2 = skel.apply(2);
            Point3 to = skel.to();
            Ellipse2D.Double odouble = new Ellipse2D.Double();
            Color color = g.getColor();
            Stroke stroke = g.getStroke();
            double px6 = camera.getMetrics().px1 * 6;
            double px3 = camera.getMetrics().px1 * 3;

            g.setColor(Color.RED);
            odouble.setFrame(from.x - px3, from.y - px3, px6, px6);
            g.fill(odouble);

            if (ctrl1 != null) {
                odouble.setFrame(ctrl1.x - px3, ctrl1.y - px3, px6, px6);
                g.fill(odouble);
                odouble.setFrame(ctrl2.x - px3, ctrl2.y - px3, px6, px6);
                g.fill(odouble);
                Line2D.Double line = new Line2D.Double();
                line.setLine(ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y);
                g.setStroke(new java.awt.BasicStroke((float)camera.getMetrics().px1));
                g.draw(line);
                line.setLine(from.x, from.y, ctrl1.x, ctrl1.y);
                g.draw(line);
                line.setLine(ctrl2.x, ctrl2.y, to.x, to.y);
                g.draw(line);
            }

            odouble.setFrame(to.x - px3, to.y - px3, px6, px6);
            g.fill(odouble);
            g.setColor(color);;
            g.setStroke(stroke);
        }
    }
}