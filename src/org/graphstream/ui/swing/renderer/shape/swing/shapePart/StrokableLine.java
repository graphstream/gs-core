package org.graphstream.ui.swing.renderer.shape.swing.shapePart;

import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.shape.swing.ShapeStroke;

public class StrokableLine extends Strokable {
 	public void configureStrokableForGroup( Style style, SwingDefaultCamera camera ) {
		theStrokeWidth = camera.getMetrics().lengthToGu( style.getStrokeWidth() ) + camera.getMetrics().lengthToGu( style.getSize(), 0 );
		strokeColor = ShapeStroke.strokeColor( style );
		theStroke = ShapeStroke.strokeForArea( style );
 	}
 	
 	public void configureStrokableLineForGroup( Style style, SwingDefaultCamera camera ) { 
 		configureStrokableForGroup( style, camera ) ;
 	}
}