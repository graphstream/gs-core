package org.graphstream.ui.swing.renderer.shape.swing.shapePart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.shape.swing.ShapePaint;
import org.graphstream.ui.swing.renderer.shape.swing.ShapeStroke;

public class FillableLine {
	ShapeStroke fillStroke = null ;
	double theFillPercent = 0.0 ;
	Color theFillColor = null ;
	boolean plainFast = false ;
  
	public void fill(Graphics2D g, double width, double dynColor, java.awt.Shape shape) {
		if(fillStroke != null) {
		    if(plainFast) {
				g.setColor(theFillColor);
		        g.draw(shape);
		    }
		    else {
				Stroke stroke = fillStroke.stroke(width);
   
				g.setColor(theFillColor);
				g.setStroke(stroke);
				g.draw(shape);
			}
		}
	}
 
	public void fill(Graphics2D g, double width, java.awt.Shape shape) { fill(g, width, theFillPercent, shape); }
 
	public void configureFillableLineForGroup(Backend bck, Style style, SwingDefaultCamera camera, double theSize) {
		fillStroke = ShapeStroke.strokeForConnectorFill( style );
  	  	plainFast = (style.getSizeMode() == StyleConstants.SizeMode.NORMAL); 
		theFillColor = style.getFillColor(0);
		bck.graphics2D().setColor(theFillColor);
		if(fillStroke != null)
			bck.graphics2D().setStroke(fillStroke.stroke(theSize));
	}

	public void configureFillableLineForElement( Style style, SwingDefaultCamera camera, GraphicElement element ) {
		theFillPercent = 0 ;
  	  	if( style.getFillMode() == StyleConstants.FillMode.DYN_PLAIN && element != null ) {
  	  		
	  	  	if ( element.getAttribute( "ui.color" ) instanceof Number ) {
  	  			theFillPercent = (float)((Number)element.getAttribute( "ui.color" ));
  	  			theFillColor = ShapePaint.interpolateColor( style.getFillColors(), theFillPercent ) ;
  	  		}
  	  		else if ( element.getAttribute( "ui.color" ) instanceof Color ) {
  	  			theFillColor = ((Color)element.getAttribute( "ui.color" )); 
  	  			theFillPercent = 0;	
  	  		}
  	  		else {
  	  			theFillPercent = 0f;
  	  			theFillColor = style.getFillColor(0);
  	  		}
	  	  	
  	  		plainFast = false;
  	  	}
	}
}