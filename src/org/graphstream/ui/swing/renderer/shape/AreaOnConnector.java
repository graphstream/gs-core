package org.graphstream.ui.swing.renderer.shape;

import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.shape.swing.Area;

/** Some areas are attached to a connector (sprites). */
public class AreaOnConnector extends Area {
	/** The connector we are attached to. */
	protected Connector theConnector = null;
	
	/** The edge represented by the connector.. */
	protected GraphicEdge theEdge = null;
			
	/** XXX must call this method explicitly in the renderer !!! bad !!! XXX */
	public void theConnectorYoureAttachedTo(Connector connector) { theConnector = connector; }
	
	protected void configureAreaOnConnectorForGroup(Style style, SwingDefaultCamera camera) {
		sizeForEdgeArrow(style, camera);
	}
	
	protected void configureAreaOnConnectorForElement(GraphicEdge edge, Style style, SwingDefaultCamera camera) {
		connector(edge);
		theCenter.set(edge.to.getX(), edge.to.getY());
	}
	
	private void connector(GraphicEdge edge) { theEdge = edge ; }
 
	private void sizeForEdgeArrow(Style style, SwingDefaultCamera camera) {
		double w = camera.getMetrics().lengthToGu(style.getArrowSize(), 0);
		double h = w ;
		if(style.getArrowSize().size() > 1)
			h = camera.getMetrics().lengthToGu(style.getArrowSize(), 1) ;
  
		theSize.set(w, h);
	}
	
}