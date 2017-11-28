package org.graphstream.ui.swing.renderer.shape.swing.shapePart;

import java.awt.Color;
import java.awt.Graphics2D;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.shape.swing.ShapePaint;
import org.graphstream.ui.swing.renderer.shape.swing.ShapePaint.ShapeAreaPaint;
import org.graphstream.ui.swing.renderer.shape.swing.ShapePaint.ShapeColorPaint;
import org.graphstream.ui.swing.renderer.shape.swing.ShapePaint.ShapePlainColorPaint;

public class Fillable {
	
	/** The fill paint. */
	ShapePaint fillPaint = null;
 
	/** Value in [0..1] for dyn-colors. */
	double theFillPercent = 0.0;
	
	Color theFillColor = null;
	
	boolean plainFast = false;
	
	/** Fill the shape.
	 * @param g The Java2D graphics.
	 * @param dynColor The value between 0 and 1 allowing to know the dynamic plain color, if any.
	 * @param shape The awt shape to fill. */
	public void fill(Graphics2D g, double dynColor, Color optColor, java.awt.Shape shape, SwingDefaultCamera camera) {
		if(plainFast) {
			g.setColor(theFillColor);
			g.fill(shape);
	    } 
		else {
			if ( fillPaint instanceof ShapeAreaPaint ) {
				g.setPaint(((ShapeAreaPaint)fillPaint).paint(shape, camera.getMetrics().ratioPx2Gu));   
				g.fill(shape);
			}
			else if (fillPaint instanceof ShapeColorPaint ) {
				g.setPaint(((ShapeColorPaint)fillPaint).paint(dynColor, optColor));
				g.fill(shape);
			}
	    }
	}
	
	/** Fill the shape.
	 * @param g The Java2D graphics.
	 * @param shape The awt shape to fill. */
 	public void fill(Graphics2D g, java.awt.Shape shape, SwingDefaultCamera camera) { fill( g, theFillPercent, theFillColor, shape, camera ); }

    /** Configure all static parts needed to fill the shape. */
 	public void configureFillableForGroup(Backend bck, Style style, SwingDefaultCamera camera ) {
 		fillPaint = ShapePaint.apply(style);
 
 		if(fillPaint instanceof ShapePlainColorPaint) {
 			ShapePlainColorPaint paint = (ShapePlainColorPaint)fillPaint;
 		    plainFast = true;
 		    theFillColor = paint.color;
 		    bck.graphics2D().setColor(theFillColor);
 		    // We prepare to accelerate the filling process if we know the color is not dynamic
 		    // and is plain: no need to change the paint at each new position for the shape.
 		} 
 		else {
 		    plainFast = false;
 		}
 	}
 	
    /** Configure the dynamic parts needed to fill the shape. */
  	public void configureFillableForElement( Style style, SwingDefaultCamera camera, GraphicElement element ) {
  	  	if( style.getFillMode() == StyleConstants.FillMode.DYN_PLAIN && element != null ) {
  	  		if ( element.getAttribute( "ui.color" ) instanceof Number ) {
  	  			theFillPercent = (float)((Number)element.getAttribute( "ui.color" ));
  	  			theFillColor = null;
  	  		}
  	  		else if ( element.getAttribute( "ui.color" ) instanceof Color ) {
  	  			theFillColor = ((Color)element.getAttribute( "ui.color" )); 
  	  			theFillPercent = 0;
  	  		}
  	  		else {
  	  			theFillPercent = 0; 
  	  			theFillColor = null;
  	  		}
  	  	}
  	  	else {
  	  		theFillPercent = 0;
  	  	}
  	}
}