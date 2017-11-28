package org.graphstream.ui.scalaViewer.renderer.shape.swing.baseShapes;

import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.scalaViewer.Backend;
import org.graphstream.ui.scalaViewer.ScalaDefaultCamera;
import org.graphstream.ui.scalaViewer.renderer.ConnectorSkeleton;
import org.graphstream.ui.scalaViewer.renderer.Skeleton;
import org.graphstream.ui.scalaViewer.renderer.shape.Connector;
import org.graphstream.ui.scalaViewer.renderer.shape.Decorable;
import org.graphstream.ui.scalaViewer.renderer.shape.Shape;

public abstract class ConnectorShape extends Connector implements Shape {
	
	public Decorable decorable ;
	
	public ConnectorShape() {
		this.decorable = new Decorable();
	}
	
	public void configureForGroup(Backend bck, Style style, ScalaDefaultCamera camera) {
		decorable.configureDecorableForGroup(style, camera);
		configureConnectorForGroup(style, camera);
 	}
 
	public void configureForElement(Backend bck, GraphicElement element, Skeleton skel, ScalaDefaultCamera camera) {
		decorable.configureDecorableForElement(bck, camera, element, skel);
		configureConnectorForElement(camera, (GraphicEdge)element, (ConnectorSkeleton)skel /* TODO check this ! */);
	}
}