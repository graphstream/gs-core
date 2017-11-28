package org.graphstream.ui.scalaViewer.renderer.shape;

import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.scalaViewer.ScalaDefaultCamera;
import org.graphstream.ui.scalaViewer.renderer.shape.swing.Area;

/** Some areas are attached to a connector (sprites). */
public class AreaOnConnector extends Area {
	/** The connector we are attached to. */
	protected Connector theConnector = null;
	
	/** The edge represented by the connector.. */
	protected GraphicEdge theEdge = null;
			
	/** XXX must call this method explicitly in the renderer !!! bad !!! XXX */
	public void theConnectorYoureAttachedTo(Connector connector) { theConnector = connector; }
	
	protected void configureAreaOnConnectorForGroup(Style style, ScalaDefaultCamera camera) {
		sizeForEdgeArrow(style, camera);
	}
	
	protected void configureAreaOnConnectorForElement(GraphicEdge edge, Style style, ScalaDefaultCamera camera) {
		connector(edge);
		theCenter.set(edge.to.getX(), edge.to.getY());
	}
	
	private void connector(GraphicEdge edge) { theEdge = edge ; }
 
	private void sizeForEdgeArrow(Style style, ScalaDefaultCamera camera) {
		double w = camera.getMetrics().lengthToGu(style.getArrowSize(), 0);
		double h = w ;
		if(style.getArrowSize().size() > 1)
			h = camera.getMetrics().lengthToGu(style.getArrowSize(), 1) ;
  
		theSize.set(w, h);
	}
	
}