package org.graphstream.ui.swing.renderer;

import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.SwingFullGraphRenderer;
import org.graphstream.ui.swing.renderer.shape.Connector;
import org.graphstream.ui.swing.renderer.shape.Shape;
import org.graphstream.ui.swing.renderer.shape.swing.baseShapes.AreaOnConnectorShape;

public class EdgeRenderer extends StyleRenderer {	
	private Shape shape = null;
	AreaOnConnectorShape arrow = null;
			
	public EdgeRenderer(StyleGroup styleGroup) {
		super(styleGroup);
	}
	
	public EdgeRenderer(StyleGroup styleGroup, SwingFullGraphRenderer mainRenderer) {
		super(styleGroup);
	}

	@Override
	public void setupRenderingPass(Backend bck, SwingDefaultCamera camera, boolean forShadow) {
		shape = bck.chooseEdgeShape(shape, group);
		arrow = (AreaOnConnectorShape)bck.chooseEdgeArrowShape(arrow, group);
	}

	@Override
	public void pushStyle(Backend bck, SwingDefaultCamera camera, boolean forShadow) {
		shape.configureForGroup(bck, group, camera);
		
		if(arrow != null) {
			arrow.configureForGroup(bck, group, camera);
		}
	}

	@Override
	public void pushDynStyle(Backend bck, SwingDefaultCamera camera, GraphicElement element) {}

	@Override
	public void renderElement(Backend bck, SwingDefaultCamera camera, GraphicElement element) {
		GraphicEdge edge = (GraphicEdge)element;
		ConnectorSkeleton skel = getOrSetConnectorSkeleton(element);
		
		shape.configureForElement(bck, element, skel, camera);
		shape.render(bck, camera, element, skel);
		  
		if(edge.isDirected() && (arrow != null)) {
			arrow.theConnectorYoureAttachedTo((Connector)shape /* !!!! Test this TODO ensure this !!! */);
			arrow.configureForElement(bck, element, skel, camera);
		  	arrow.render(bck, camera, element, skel);
		}
	}

	@Override
	public void renderShadow(Backend bck, SwingDefaultCamera camera, GraphicElement element) {
		GraphicEdge edge = (GraphicEdge)element;
		ConnectorSkeleton skel = getOrSetConnectorSkeleton(element);
				
		shape.configureForElement(bck, element, skel, camera);
		shape.renderShadow(bck, camera, element, skel);
  
		if(edge.isDirected() && (arrow != null)) {
			arrow.theConnectorYoureAttachedTo((Connector)shape /* !!!! Test this TODO ensure this !!! */);
			arrow.configureForElement(bck, element, skel, camera);
			arrow.renderShadow(bck, camera, element, skel);
		}
	}
	
	/** Retrieve the shared edge informations stored on the given edge element.
	  * If such information is not yet present, add it to the element. 
	  * @param element The element to look for.
	  * @return The edge information.
	  * @throws RuntimeException if the element is not an edge. */
	protected ConnectorSkeleton getOrSetConnectorSkeleton(GraphicElement element) {
		if(element instanceof GraphicEdge) {
			ConnectorSkeleton info = (ConnectorSkeleton)element.getAttribute(Skeleton.attributeName) ;
			
			if(info == null) {
				info = new ConnectorSkeleton();
				element.setAttribute(Skeleton.attributeName, info);
			}
			
			return info;
		}
		else {
			throw new RuntimeException("Trying to get EdgeInfo on non-edge...");
		}
	}
	
	@Override
	public void elementInvisible(Backend bck, SwingDefaultCamera camera, GraphicElement element) {}
	
	@Override
	public void endRenderingPass(Backend bck, SwingDefaultCamera camera, boolean forShadow) {}
}
