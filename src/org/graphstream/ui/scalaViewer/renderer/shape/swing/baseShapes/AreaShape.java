package org.graphstream.ui.scalaViewer.renderer.shape.swing.baseShapes;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.scalaViewer.Backend;
import org.graphstream.ui.scalaViewer.ScalaDefaultCamera;
import org.graphstream.ui.scalaViewer.renderer.AreaSkeleton;
import org.graphstream.ui.scalaViewer.renderer.Skeleton;
import org.graphstream.ui.scalaViewer.renderer.shape.Decorable;
import org.graphstream.ui.scalaViewer.renderer.shape.Shape;
import org.graphstream.ui.scalaViewer.renderer.shape.swing.Area;
import org.graphstream.ui.scalaViewer.renderer.shape.swing.shapePart.Fillable;
import org.graphstream.ui.scalaViewer.renderer.shape.swing.shapePart.Shadowable;
import org.graphstream.ui.scalaViewer.renderer.shape.swing.shapePart.Strokable;

public abstract class AreaShape extends Decorable implements Shape {
	
	public Fillable fillable ;
	public Strokable strokable ;
	public Shadowable shadowable ;
	public Area area ;
	
	public AreaShape() {
		this.fillable = new Fillable();
		this.strokable = new Strokable();
		this.shadowable = new Shadowable();
		this.area = new Area();
	}
	
	
	public void configureForGroup(Backend bck, Style style, ScalaDefaultCamera camera) {
 	  	fillable.configureFillableForGroup(bck, style, camera);
 	  	strokable.configureStrokableForGroup(style, camera);
 	  	shadowable.configureShadowableForGroup(style, camera);
 	  	configureDecorableForGroup(style, camera);
 	  	area.configureAreaForGroup(style, camera);
 	}
 
	public void configureForElement(Backend bck, GraphicElement element, Skeleton skel, ScalaDefaultCamera camera) {
		fillable.configureFillableForElement(element.getStyle(), camera, element);
		configureDecorableForElement(bck, camera, element, skel);
		area.configureAreaForElement(bck, camera, (AreaSkeleton)skel, element, theDecor);
	}
}