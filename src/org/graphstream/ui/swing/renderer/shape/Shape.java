package org.graphstream.ui.swing.renderer.shape;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.swing.Backend;
import org.graphstream.ui.swing.SwingDefaultCamera;
import org.graphstream.ui.swing.renderer.Skeleton;
import org.graphstream.ui.view.Camera;

/** Base for all shapes. */
public interface Shape {
	
	/** Configure as much as possible the graphics before painting several version of this shape
     * at different positions.
     * @param backend The rendering back-end.
     * @param style The style for the group.
     * @param camera the view parameters. */
	void configureForGroup(Backend backend, Style style, SwingDefaultCamera camera) ;
	
	/** Configure all the dynamic and per element settings.
	  * Some configurations can only be done before painting the element, since they change for
	  * each element.
	  * @param backend The rendering back-end.
	  * @param element The specific element to render.
	  * @param skeleton The element geometry and information.
	  * @param camera the view parameters. */
	void configureForElement(Backend backend, GraphicElement element, Skeleton skeleton, SwingDefaultCamera camera);
	
	/** Must create the shape from informations given earlier, that is, resize it if needed and
     * position it, and do all the things that are specific to each element, and cannot be done
     * for the group of elements.
     * This method is made to be called inside the render() method, hence it is protected.
     * @param backend The rendering back-end.
     * @param camera the view parameters. */
	void make(Backend backend, SwingDefaultCamera camera);
	
	/** Same as {@link #make(Camera)} for the shadow shape. The shadow shape may be moved and
	  * resized compared to the original shape. This method is made to be called inside the
	  * renderShadow() method, hence it is protected. */
 	void makeShadow(Backend backend, SwingDefaultCamera camera);
 
 	/** Render the shape for the given element.
     * @param backend The rendering back-end.
     * @param camera The view parameters.
     * @param element The element to render.
     * @param skeleton The element geometry and information. */
 	void render(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skeleton);
  
  	/** Render the shape shadow for the given element. The shadow is rendered in a different pass
     * than usual rendering, therefore it is a separate method. */
 	void renderShadow(Backend bck, SwingDefaultCamera camera, GraphicElement element, Skeleton skeleton);
}
