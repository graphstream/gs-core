package org.graphstream.ui.swing.renderer.shape.swing.shapePart;

import java.awt.Color;
import java.awt.Graphics2D;

import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.shape.swing.ShapeStroke;

public class ShadowableLine {
	/** The shadow paint. */
	public ShapeStroke shadowStroke = null;

	/** Additional width of a shadow (added to the shape size). */
	public double theShadowWidth = 0.0;
 
	/** Offset of the shadow according to the shape center. */
	public Point2 theShadowOff = new Point2();

	public Color theShadowColor = null ;
 
	/** Sety the shadow width added to the shape width. */
	public void shadowWidth( double width ) { theShadowWidth = width; }
 
 	/** Set the shadow offset according to the shape. */ 
	public void shadowOffset( double xoff, double yoff ) { theShadowOff.set( xoff, yoff ); }
	
 	/**
     * Render the shadow.
     * @param g The Java2D graphics.
     */
   	public void cast( Graphics2D g, java.awt.Shape shape ) {
   	  	g.setColor( theShadowColor );
   	  	g.setStroke( shadowStroke.stroke( theShadowWidth ) );
   	  	g.draw( shape );
   	}
 
    /** Configure all the static parts needed to cast the shadow of the shape. */
 	public void configureShadowableLineForGroup( Style style, SwingDefaultCamera camera) {
 		theShadowWidth = camera.getMetrics().lengthToGu( style.getSize(), 0 ) +
 			camera.getMetrics().lengthToGu( style.getShadowWidth() ) +
 			camera.getMetrics().lengthToGu( style.getStrokeWidth() ) ;
 		theShadowOff.x = camera.getMetrics().lengthToGu( style.getShadowOffset(), 0 );
 		theShadowOff.y = theShadowOff.x ;
 		if( style.getShadowOffset().size() > 1 ) 
 			camera.getMetrics().lengthToGu( style.getShadowOffset(), 1 ) ;
  	  	theShadowColor = style.getShadowColor( 0 );
 		shadowStroke   = ShapeStroke.strokeForConnectorFill( style );
 	}	
}