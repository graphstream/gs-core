package org.graphstream.ui.scalaViewer.renderer;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.scalaViewer.Backend;
import org.graphstream.ui.scalaViewer.ScalaDefaultCamera;
import org.graphstream.ui.scalaViewer.ScalaGraphRenderer;
import org.graphstream.ui.scalaViewer.renderer.shape.Shape;

public class NodeRenderer extends StyleRenderer {
	
	private Shape shape = null;

	public NodeRenderer(StyleGroup style) {
		super(style);
	}
	
	public static StyleRenderer apply(StyleGroup style, ScalaGraphRenderer renderer) {
		if (style.getShape() == org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape.JCOMPONENT)
			return new JComponentRenderer(style, renderer);
		else
			return new NodeRenderer(style);
	}

	@Override
	public void setupRenderingPass(Backend bck, ScalaDefaultCamera camera, boolean forShadow) {
		shape = bck.chooseNodeShape(shape, group);	
	}

	@Override
	public void pushStyle(Backend bck, ScalaDefaultCamera camera, boolean forShadow) {
		shape.configureForGroup(bck, group, camera);		
	}

	@Override
	public void pushDynStyle(Backend bck, ScalaDefaultCamera camera, GraphicElement element) {}

	@Override
	public void renderElement(Backend bck, ScalaDefaultCamera camera, GraphicElement element) {
		AreaSkeleton skel = getOrSetAreaSkeleton(element);
		shape.configureForElement(bck, element, skel, camera);
		shape.render(bck, camera, element, skel);
	}

	@Override
	public void renderShadow(Backend bck, ScalaDefaultCamera camera, GraphicElement element) {
		AreaSkeleton skel = getOrSetAreaSkeleton(element);
		shape.configureForElement(bck, element, skel, camera);
		shape.renderShadow(bck, camera, element, skel);
	}
	
	@Override
	public void elementInvisible(Backend bck, ScalaDefaultCamera camera, GraphicElement element) {}

	/** Retrieve the area shared informations stored on the given node element.
	  * If such information is not yet present, add it to the element. 
	  * @param element The element to look for.
	  * @return The node information.
	  * @throws RuntimeException if the element is not a node. */
	private AreaSkeleton getOrSetAreaSkeleton(GraphicElement element) {
		if(element instanceof GraphicNode) {
			AreaSkeleton skel =(AreaSkeleton) element.getAttribute(Skeleton.attributeName);
			
			if(skel == null) {
				skel = new AreaSkeleton();
				element.setAttribute(Skeleton.attributeName, skel);
			}
			
			return skel;
		} 
		else {
			throw new RuntimeException("Trying to get AreaSkeleton on non-area (node or sprite) ...");
		}
	}

	@Override
	public void endRenderingPass(Backend bck, ScalaDefaultCamera camera, boolean forShadow) {
		// TODO Auto-generated method stub
		
	}
	
}
