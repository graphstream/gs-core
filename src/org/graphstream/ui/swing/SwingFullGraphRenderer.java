/*
 * Copyright 2006 - 2016
 *     Stefan Balev     <stefan.balev@graphstream-project.org>
 *     Julien Baudry    <julien.baudry@graphstream-project.org>
 *     Antoine Dutot    <antoine.dutot@graphstream-project.org>
 *     Yoann Pigné      <yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin   <guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.ui.swing;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.graphstream.graph.Element;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicEdge;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicElement.SwingElementRenderer;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.StyleGroupListener;
import org.graphstream.ui.graphicGraph.StyleGroupSet;
import org.graphstream.ui.graphicGraph.stylesheet.Selector;
import org.graphstream.ui.swing.renderer.GraphBackgroundRenderer;
import org.graphstream.ui.swing.renderer.JComponentRenderer;
import org.graphstream.ui.swing.renderer.SelectionRenderer;
import org.graphstream.ui.swing.renderer.StyleRenderer;
import org.graphstream.ui.swing.util.FPSLogger;
import org.graphstream.ui.swing.util.Selection;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.util.GraphMetrics;
import org.graphstream.ui.swingViewer.util.Graphics2DOutput;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.GraphRenderer;
import org.graphstream.ui.view.LayerRenderer;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.util.InteractiveElement;

/**	
 * 2D renderer.
 * 
 * The role of this class is to equip each style group with a specific renderer and
 * to call these renderer to redraw the graph when needed. The renderers then equip
 * each node, edge and sprite with a skeleton that gives is main geometry, then
 * selects a shape according to the group style. The shape will be "applied" to
 * each element to draw in the group. The shape will be moved and scaled according
 * to the skeleton.
 * 
 * A render pass begins by using the camera instance to set up the projection (allows
 * to pass from graph units to pixels, make a rotation a zoom or a translation) and
 * render each style group once for the shadows, and once for the real rendering
 * in Z order.
 * 
 * This class also handles a "selection" object that represents the current selection
 * and renders it.
 * 
 * The renderer uses a backend so that it can adapt to multiple rendering
 * targets (here Swing and OpenGL). As the shapes are finally responsible
 * for drawing the graph, the backend is also responsible for the shape
 * creation.
 */
public class SwingFullGraphRenderer implements GraphRenderer<Container, Graphics2D>, StyleGroupListener {
	public final static String DEFAULT_RENDERER = "j2d_def_rndr";
	
	protected SwingDefaultCamera camera = null;
	
	protected GraphicGraph graph = null;
	
	protected Selection selection = new Selection();
	
	protected LayerRenderer<Graphics2D> backRenderer = null;

	protected LayerRenderer<Graphics2D> foreRenderer = null;
	
	protected Backend backend = null ;
	
	protected FPSLogger fpsLogger = null ;
	
// Construction

	@Override
	public void open(GraphicGraph graph, Container drawingSurface) {
		if( this.graph == null ) {
			this.graph   = graph;
			this.backend = new BackendJ2D();		// choose it according to some setting
		  	this.camera  = new SwingDefaultCamera(graph);
		  	graph.getStyleGroups().addListener(this);
		  	backend.open(drawingSurface);
	  	}
		else {
	  		throw new RuntimeException("renderer already open, use close() first");
	  	}
	}
	
	@Override
	public void close() {
		if(graph != null) {
			if(fpsLogger != null) {
				fpsLogger.close();
				fpsLogger = null;
			}
   
			removeRenderers();
			backend.close();
			graph.getStyleGroups().removeListener(this);
			graph   = null;
			backend = null;
			camera  = null;
		}
	}
	
// Access
	
	@Override
	public Camera getCamera() {
		return camera;
	}
		  
	public Container renderingSurface() {
		return backend.drawingSurface();
	}
	
	/** Get (and assign if needed) a style renderer to the graphic graph. The renderer will be reused then. */
    protected GraphBackgroundRenderer getStyleRenderer(GraphicGraph graph) {
  		if(graph.getStyle().getRenderer("dr") == null)
  			graph.getStyle().addRenderer("dr", new GraphBackgroundRenderer(graph, graph.getStyle()));
  		
  		return (GraphBackgroundRenderer)graph.getStyle().getRenderer("dr");
    }

// Access -- Renderer bindings
    
    /** Get (and assign if needed) a style renderer to a style group. The renderer will be reused then. */
    protected StyleRenderer getStyleRenderer(StyleGroup style) {
  		if( style.getRenderer("dr") == null)
  			style.addRenderer("dr", StyleRenderer.apply(style, this));
    
  		return (StyleRenderer)style.getRenderer("dr");
    }
    
    /** Get (and assign if needed) the style renderer associated with the style group of the element. */
    protected StyleRenderer getStyleRenderer(GraphicElement element) {
  		return getStyleRenderer(element.getStyle());
    }
    
    /** Remove all the registered renderers from the graphic graph. */
    protected void removeRenderers() {
        graph.getStyle().removeRenderer("dr");
        graph.nodes().forEach(node -> ((GraphicNode)node).getStyle().removeRenderer("dr")); 
        graph.edges().forEach(edge -> ((GraphicEdge)edge).getStyle().removeRenderer("dr"));
        graph.sprites().forEach(sprite -> sprite.getStyle().removeRenderer("dr"));
    }
    
// Command

    public void beginSelectionAt(double x, double y) {
  		selection.setActive(true);
  		selection.begins(x, y);
  		Logger.getLogger(this.getClass().getSimpleName()).fine("Selection begins at "+x+" "+y);
  	}

    public void selectionGrowsAt(double x, double y) {
  		selection.grows(x, y);
  	}

    public void endSelectionAt(double x, double y) {
  		selection.grows(x, y);
  		selection.setActive(false);
  	}

    public void moveElementAtPx(GraphicElement element, double x, double y) {
  		Point3 p = camera.transformPxToGu(x, y);
  		element.move(p.x, p.y, element.getZ());
  	}
 
// Commands -- Rendering
    
    @Override
    public void render(Graphics2D g, int x, int y, int width, int height) {
    	if(graph != null) {
  	        startFrame();
  	        
  		    // Verify this view is not closed, the Swing repaint mechanism may trigger 1 or 2
  		    // calls to this after being closed.
  		    if(backend == null)
  		        backend = new BackendJ2D(); // TODO choose it according to some setting ...
  		    
  		    backend.prepareNewFrame(g);
  		    camera.setBackend(backend);
  		        
  			StyleGroupSet sgs = graph.getStyleGroups();
  			
  			setupGraphics();
  			graph.computeBounds();
  			camera.setBounds(graph);
  			camera.setViewport(x, y, width, height);
  			getStyleRenderer(graph).render(backend, camera, width, height);
  			renderBackLayer();

  			camera.pushView(graph);
  			sgs.shadows().forEach( s -> getStyleRenderer(s).renderShadow(backend, camera));
  			
  			
  			sgs.getZIndex().forEach( groups -> {
  				groups.forEach( group -> {
  					if(group.getType() != Selector.Type.GRAPH) {
  						getStyleRenderer(group).render(backend, camera);
		  	  		}
  				});
  			});
 
  			camera.popView();
  			renderForeLayer();
  
  			if( selection.getRenderer() == null ) 
  				selection.setRenderer(new SelectionRenderer( selection, graph ));
  			selection.getRenderer().render(backend, camera, width, height );
  			
  	    	endFrame();
  	    }
    }
    
    protected void startFrame() {
  	    if((fpsLogger == null) && graph.hasLabel("ui.log")) {
  	    	fpsLogger = new FPSLogger(graph.getLabel("ui.log").toString());
  	    }
  	    
  	    if(! (fpsLogger == null))
  	    	fpsLogger.beginFrame();
  	}
  	
  	protected void endFrame() {
  	    if(! (fpsLogger == null))
  	        fpsLogger.endFrame();
  	}
  	
  	protected void renderBackLayer() {
  		if(backRenderer != null) 
  			renderLayer(backRenderer);
  	}
	
	protected void renderForeLayer() {
		if(foreRenderer != null)
			renderLayer(foreRenderer);
	}
	
	/** Render a back or from layer. */ 
	protected void renderLayer(LayerRenderer<Graphics2D> renderer) {
		GraphMetrics metrics = camera.getMetrics();
		
		renderer.render(backend.graphics2D(), graph, metrics.ratioPx2Gu,
			(int)metrics.viewport[2],
			(int)metrics.viewport[3],
			metrics.loVisible.x,
			metrics.loVisible.y,
			metrics.hiVisible.x,
			metrics.hiVisible.y);
	}
    
	/** Setup the graphic pipeline before drawing. */
	protected void setupGraphics() {
       backend.setAntialias(graph.hasAttribute("ui.antialias"));
       backend.setQuality(graph.hasAttribute("ui.quality"));
	}
	
	public void screenshot(String filename, int width, int height) {
		if(filename.toLowerCase().endsWith("png")) {
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			render(img.createGraphics(), 0, 0, width, height);
			File file = new File(filename);
			try {
				ImageIO.write(img, "png", file);
			} catch (IOException e) { e.printStackTrace(); }
	   	} 
		else if(filename.toLowerCase().endsWith("bmp")) {
			// Who, in the world, is still using BMP ???
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			render(img.createGraphics(), 0, 0, width, height);
			File file = new File(filename);
			try {
				ImageIO.write(img, "bmp", file);
			} catch (IOException e) { e.printStackTrace(); }
		}
		else if(filename.toLowerCase().endsWith("jpg") || filename.toLowerCase().endsWith("jpeg")) {
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			render(img.createGraphics(), 0, 0, width, height);
			File file = new File(filename);
			try {
				ImageIO.write(img, "jpg", file);
			} catch (IOException e) {e.printStackTrace();}
		} 
		else if(filename.toLowerCase().endsWith("svg")) {
		    try {
				String plugin = "org.graphstream.ui.batik.BatikGraphics2D";
				Class c = Class.forName(plugin);
				Object o = (Object)c.newInstance();
				if(o instanceof Graphics2DOutput) {
					Graphics2DOutput out = (Graphics2DOutput)o;
					Graphics2D g2 = out.getGraphics();
					render(g2, 0, 0, width, height);
					out.outputTo(filename);
				} 
				else {
					Logger.getLogger(this.getClass().getSimpleName()).warning("Plugin "+plugin+" is not an instance of Graphics2DOutput ("+o.getClass().getName()+").");
				}
			} catch (Exception e){ e.printStackTrace();}
		} 
		else {
		    Logger.getLogger(this.getClass().getSimpleName()).warning("Unknown screenshot filename extension "+filename+", saving to jpeg.");
		    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			render(img.createGraphics(), 0, 0, width, height);
			File file = new File(filename+".jpg");
			try {
				ImageIO.write(img, "jpg", file);
			} catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	public void setBackLayerRenderer(LayerRenderer<Graphics2D> renderer) { backRenderer = renderer; }

	public void setForeLayoutRenderer(LayerRenderer<Graphics2D> renderer) { foreRenderer = renderer; }
	
	// Commands -- Style group listener
    public void elementStyleChanged(Element element, StyleGroup oldStyle, StyleGroup style) {
    	// XXX The element renderer should be the listener, not this. ... XXX

    	if(oldStyle != null) {
    		SwingElementRenderer renderer = oldStyle.getRenderer(SwingFullGraphRenderer.DEFAULT_RENDERER);

	    	if((renderer != null ) && renderer instanceof JComponentRenderer)
	    		((JComponentRenderer)renderer).unequipElement((GraphicElement)element);
    	}
    }
    
    @Override
    public View createDefaultView(Viewer viewer, String id) {
    	return new DefaultView(viewer, id, this);
    }

	@Override
	public GraphicElement findGraphicElementAt(EnumSet<InteractiveElement> types, double x, double y) {
		return camera.findGraphicElementAt(graph, types, x, y);
	}

	@Override
	public Collection<GraphicElement> allGraphicElementsIn(EnumSet<InteractiveElement> types, double x1, double y1,
			double x2, double y2) {
		return camera.allGraphicElementsIn(graph,types,x1, y1, x2, y2);
	}
}