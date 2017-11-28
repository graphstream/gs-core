package org.graphstream.ui.scalaViewer.renderer.shape.swing.baseShapes;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.scalaViewer.Backend;
import org.graphstream.ui.scalaViewer.ScalaDefaultCamera;
import org.graphstream.ui.scalaViewer.renderer.Skeleton;
import org.graphstream.ui.scalaViewer.renderer.shape.swing.shapePart.Fillable;
import org.graphstream.ui.scalaViewer.renderer.shape.swing.shapePart.Shadowable;
import org.graphstream.ui.scalaViewer.renderer.shape.swing.shapePart.Strokable;

public abstract class AreaConnectorShape extends ConnectorShape {
	
	public Fillable fillable ;
	public Strokable strokable ;
	public Shadowable shadowable ;
	
	public AreaConnectorShape() {
		this.fillable = new Fillable();
		this.strokable = new Strokable();
		this.shadowable = new Shadowable();
	}
	
	public void configureForGroup(Backend bck, Style style, ScalaDefaultCamera camera) {
		fillable.configureFillableForGroup(bck, style, camera);
		strokable.configureStrokableForGroup(style, camera);
		shadowable.configureShadowableForGroup(style, camera);
		super.configureForGroup(bck, style, camera);
 	}
 
	public void configureForElement(Backend bck, GraphicElement element, Skeleton skel, ScalaDefaultCamera camera) {
		fillable.configureFillableForElement(element.getStyle(), camera, element);
		super.configureForElement(bck, element, skel, camera);
	}
}
