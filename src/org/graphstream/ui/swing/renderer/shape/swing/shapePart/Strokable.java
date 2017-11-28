package org.graphstream.ui.swing.renderer.shape.swing.shapePart;

import java.awt.Color;
import java.awt.Graphics2D;

import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.shape.swing.ShapeStroke;

public class Strokable {
    /** The stroke color. */
	public Color strokeColor = null ;

	/** The stroke. */
	public ShapeStroke theStroke = null ;
 	
	/** The stroke width. */
	public double theStrokeWidth = 0.0 ;

 	/** Paint the stroke of the shape. */
	public void stroke( Graphics2D g, java.awt.Shape shape ) {
		if(theStroke != null) {
			g.setStroke( theStroke.stroke( theStrokeWidth ) );
			g.setColor( strokeColor );
			g.draw( shape );
		}	  
	}
	
 	/** Configure all the static parts needed to stroke the shape. */
 	public void configureStrokableForGroup( Style style, SwingDefaultCamera camera ) {
		theStrokeWidth = camera.getMetrics().lengthToGu( style.getStrokeWidth() );
		/*if( strokeColor == null )*/ strokeColor = ShapeStroke.strokeColor( style );
		/*if( theStroke   == null )*/ theStroke   = ShapeStroke.strokeForArea( style );
 	}
}