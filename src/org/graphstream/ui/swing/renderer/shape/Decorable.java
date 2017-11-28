package org.graphstream.ui.swing.renderer.shape;

import java.awt.geom.Rectangle2D;

import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.Skeleton;
import org.graphstream.ui.swing.renderer.shape.swing.IconAndText;
import org.graphstream.ui.swing.renderer.shape.swing.ShapeDecor;

/** Trait for shapes that can be decorated by an icon and/or a text. */
public class Decorable extends HasSkel {
	/** The string of text of the contents. */
	public String text = null;
 
	/** The text and icon. */
	public ShapeDecor theDecor = null ;
  
 	/** Paint the decorations (text and icon). */
 	public void decorArea(Backend backend, SwingDefaultCamera camera, IconAndText iconAndText, GraphicElement element, java.awt.Shape shape ) {
 	  	boolean visible = true ;
 	  	if( element != null ) visible = camera.isTextVisible( element );
 	  	if( theDecor != null && visible ) {
 	  		Rectangle2D bounds = shape.getBounds2D();
 	  		theDecor.renderInside(backend, camera, iconAndText, bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY() );
 	  	}
 	}
	
	public void decorConnector(Backend backend, SwingDefaultCamera camera, IconAndText iconAndText, GraphicElement element, java.awt.Shape shape ) {
		boolean visible = true ;
 	  	if( element != null ) visible = camera.isTextVisible( element );
 	  	if( theDecor != null && visible ) {
 	  		if ( element instanceof GraphicEdge ) {
 	  			GraphicEdge edge = (GraphicEdge)element;
 	  			if((skel != null) && (skel.isCurve())) {
 	  				theDecor.renderAlong(backend, camera, iconAndText, skel);
 	  			} 
 	  			else {
 	  				theDecor.renderAlong(backend, camera, iconAndText, edge.from.x, edge.from.y, edge.to.x, edge.to.y);
 	  			}
 	  		}
 	  		else {
 	  			Rectangle2D bounds = shape.getBounds2D();
 	  			theDecor.renderAlong(backend, camera, iconAndText, bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY() );
 	  		}
 	  	}
	}
	
	/** Configure all the static parts needed to decor the shape. */
  	public void configureDecorableForGroup( Style style, SwingDefaultCamera camera) {
		/*if( theDecor == null )*/ theDecor = ShapeDecor.apply( style );
  	}
  	/** Setup the parts of the decor specific to each element. */
  	public void configureDecorableForElement(Backend backend, SwingDefaultCamera camera, GraphicElement element, Skeleton skel) {
  		text = element.label;
  		if( skel != null ) {
  			StyleGroup style = element.getStyle();
  			skel.iconAndText = IconAndText.apply( style, camera, element );
  			if( style.getIcon() != null && style.getIcon().equals( "dynamic" ) && element.hasAttribute( "ui.icon" ) ) {
  				String url = element.getLabel("ui.icon").toString();
  				skel.iconAndText.setIcon(backend, url);
  			}
  			skel.iconAndText.setText(backend, element.label);
  		}
  	}
}