package org.graphstream.ui.scalaViewer.renderer.shape.swing.shapePart;

import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.scalaViewer.ScalaDefaultCamera;
import org.graphstream.ui.scalaViewer.renderer.shape.swing.ShapeStroke;

public class StrokableLine extends Strokable {
 	public void configureStrokableForGroup( Style style, ScalaDefaultCamera camera ) {
		theStrokeWidth = camera.getMetrics().lengthToGu( style.getStrokeWidth() ) + camera.getMetrics().lengthToGu( style.getSize(), 0 );
		strokeColor = ShapeStroke.strokeColor( style );
		theStroke = ShapeStroke.strokeForArea( style );
 	}
 	
 	public void configureStrokableLineForGroup( Style style, ScalaDefaultCamera camera ) { 
 		configureStrokableForGroup( style, camera ) ;
 	}
}