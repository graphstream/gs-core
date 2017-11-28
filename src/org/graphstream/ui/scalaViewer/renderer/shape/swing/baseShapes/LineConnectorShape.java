package org.graphstream.ui.scalaViewer.renderer.shape.swing.baseShapes;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.scalaViewer.Backend;
import org.graphstream.ui.scalaViewer.ScalaDefaultCamera;
import org.graphstream.ui.scalaViewer.renderer.Skeleton;
import org.graphstream.ui.scalaViewer.renderer.shape.swing.shapePart.FillableLine;
import org.graphstream.ui.scalaViewer.renderer.shape.swing.shapePart.ShadowableLine;
import org.graphstream.ui.scalaViewer.renderer.shape.swing.shapePart.StrokableLine;

public abstract class LineConnectorShape extends ConnectorShape {
	
	public FillableLine fillableLine ;
	public StrokableLine strokableLine;
	public ShadowableLine shadowableLine;
	
	public LineConnectorShape() {
		this.fillableLine = new FillableLine() ;
		this.strokableLine = new StrokableLine() ;
		this.shadowableLine = new ShadowableLine() ;
	}
	
	
	public void configureForGroup(Backend bck, Style style, ScalaDefaultCamera camera) {
		super.configureForGroup(bck, style, camera);
		fillableLine.configureFillableLineForGroup(bck, style, camera, theSize);
		strokableLine.configureStrokableLineForGroup(style, camera);
		shadowableLine.configureShadowableLineForGroup(style, camera);
 	}
 
	public void configureForElement(Backend bck, GraphicElement element, Skeleton skel, ScalaDefaultCamera camera) {
		fillableLine.configureFillableLineForElement(element.getStyle(), camera, element);
		super.configureForElement(bck, element, skel, camera);
	}
}