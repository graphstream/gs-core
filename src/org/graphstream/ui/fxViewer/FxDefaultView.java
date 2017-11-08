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
package org.graphstream.ui.fxViewer;

import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.EnumSet;

import org.graphstream.ui.fxViewer.util.DefaultApplication;
import org.graphstream.ui.fxViewer.util.FxMouseManager;
import org.graphstream.ui.fxViewer.util.FxShortcutManager;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.GraphRenderer;
import org.graphstream.ui.view.LayerRenderer;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.util.InteractiveElement;
import org.graphstream.ui.view.util.MouseManager;
import org.graphstream.ui.view.util.ShortcutManager;

import javafx.scene.canvas.GraphicsContext;

/**
 * Base for constructing views.
 * 
 * <p>
 * This base view is an abstract class that provides mechanism that are
 * necessary in any view :
 * <ul>
 * <li>the painting and repainting mechanism.</li>
 * <li>the optional frame handling.</li>
 * <li>the frame closing protocol.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * This view also handle a current selection of nodes and sprites.
 * </p>
 * 
 * <h3>The painting mechanism</h3>
 * 
 * <p>
 * The main method to implement is {@link #render(Graphics2D)}. This method is
 * called each time the graph needs to be rendered anew in the canvas.
 * </p>
 * 
 * <p>
 * The {@link #render(Graphics2D)} is called only when a repainting is really
 * needed.
 * </p>
 * 
 * <p>
 * All the painting, by default, is deferred to a {@link GraphRenderer}
 * instance. This mechanism allows developers that do not want to mess with the
 * viewer/view mechanisms to render a graph in any Swing surface.
 * </p>
 * 
 * <h3>The optional frame handling</h3>
 * 
 * <p>
 * This abstract view is able to create a frame that is added around this panel
 * (each view is a JPanel instance). The frame can be removed at any time.
 * </p>
 * 
 * <h3>The frame closing protocol</h3>
 * 
 * <p>
 * This abstract view handles the closing protocol. This means that it will
 * close the view if needed, or only hide it to allow reopening it later.
 * Furthermore it adds the "ui.viewClosed" attribute to the graph when the view
 * is closed or hidden, and removes it when the view is shown. The value of this
 * graph attribute is the identifier of the view.
 * </p>
 */
public class FxDefaultView extends FxViewPanel implements WindowListener, ComponentListener {
	private static final long serialVersionUID = -4489484861592064398L;

	/**
	 * Parent viewer.
	 */
	protected Viewer viewer;

	/**
	 * The graph to render, shortcut to the viewers reference.
	 */
	protected GraphicGraph graph;


	/**
	 * Manager for events with the keyboard.
	 */
	protected ShortcutManager shortcuts;

	/**
	 * Manager for events with the mouse.
	 */
	protected MouseManager mouseClicks;

	/**
	 * The graph renderer.
	 */
	protected GraphRenderer renderer;

	// Construction

	public FxDefaultView(Viewer viewer, String identifier, GraphRenderer renderer) {
		super(identifier);
		
		
		this.viewer = viewer;
		this.graph = viewer.getGraphicGraph();
		this.renderer = renderer;
		
		setupGraphics();
		

		setMouseManager(null);
		setShortcutManager(null);
		renderer.open(graph, this);
		
	}
	
	protected void setupGraphics() {
	}

	// Access
	
	// Command

	public Camera getCamera() {
		return renderer.getCamera();
	}
	
	//public static int i = 0 ;
	@Override
	protected void layoutChildren() {
		// TODO Auto-generated method stub
		
		//System.out.println(i++);
		if ( DefaultApplication.isInstance )
		{
			DefaultApplication.checkTitle();
			DefaultApplication.setAliasing(graph.hasAttribute("ui.antialias"));
		}
		render(getGraphics());
	}

	public void display(GraphicGraph graph, boolean graphChanged) {
		layoutChildren();
	}

	
	public void close(GraphicGraph graph) {
		renderer.close();
		graph.setAttribute("ui.viewClosed", getIdView());

		//removeKeyListener(shortcuts);
		shortcuts.release();
		mouseClicks.release();

		openInAFrame(false);
	}

	public void openInAFrame(boolean on) {
	}

	public void render(GraphicsContext g) {
		renderer.render(g, (int)getLayoutX(), (int)getLayoutY(), (int)getWidth(), (int)getHeight());

		String screenshot = (String) graph.getLabel("ui.screenshot");

		if (screenshot != null) {
			graph.removeAttribute("ui.screenshot");
			renderer.screenshot(screenshot, (int)getScene().getWidth(), (int)getScene().getHeight());
		}
	}

	// Selection

	public void beginSelectionAt(double x1, double y1) {
		renderer.beginSelectionAt(x1, y1);
		layoutChildren();
	}

	public void selectionGrowsAt(double x, double y) {
		renderer.selectionGrowsAt(x, y);
		layoutChildren();
	}

	public void endSelectionAt(double x2, double y2) {
		renderer.endSelectionAt(x2, y2);
		layoutChildren();
	}

	// Window Listener

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		graph.setAttribute("ui.viewClosed", getId());

		switch (viewer.getCloseFramePolicy()) {
		case CLOSE_VIEWER:
			viewer.removeView(getId());
			break;
		case HIDE_ONLY:
			
			break;
		case EXIT:
			System.exit(0);
		default:
			throw new RuntimeException(String.format("The %s view is not up to date, do not know %s CloseFramePolicy.",
					getClass().getName(), viewer.getCloseFramePolicy()));
		}
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
		graph.removeAttribute("ui.viewClosed");
	}

	public void componentHidden(ComponentEvent e) {
		layoutChildren();
	}

	public void componentMoved(ComponentEvent e) {
		layoutChildren();
	}

	public void componentResized(ComponentEvent e) {
		layoutChildren();
	}

	public void componentShown(ComponentEvent e) {
		layoutChildren();
	}

	// Methods deferred to the renderer

	@Override
	public Collection<GraphicElement> allGraphicElementsIn(EnumSet<InteractiveElement> types, double x1, double y1, double x2, double y2) {
		return renderer.allGraphicElementsIn(types, x1, y1, x2, y2);
	}

	@Override
	public GraphicElement findGraphicElementAt(EnumSet<InteractiveElement> types, double x, double y) {
		return renderer.findGraphicElementAt(types,x, y);
	}


	public void moveElementAtPx(GraphicElement element, double x, double y) {
		// The feedback on the node positions is often off since not needed
		// and generating lots of events. We activate it here since the
		// movement of the node is decided by the viewer. This is one of the
		// only moment when the viewer really moves a node.
		boolean on = graph.feedbackXYZ();
		graph.feedbackXYZ(true);
		renderer.moveElementAtPx(element, x, y);
		graph.feedbackXYZ(on);
	}

	public void freezeElement(GraphicElement element, boolean frozen) {
		if (frozen) {
			element.setAttribute("layout.frozen");
		} else {
			element.removeAttribute("layout.frozen");
		}
	}

	public void setBackLayerRenderer(LayerRenderer<GraphicsContext> renderer) {
		this.renderer.setBackLayerRenderer(renderer);
		layoutChildren();
	}

	public void setForeLayoutRenderer(LayerRenderer<GraphicsContext> renderer) {
		this.renderer.setForeLayoutRenderer(renderer);
		layoutChildren();
	}

	public void setMouseManager(MouseManager manager) {
		if (mouseClicks != null)
			mouseClicks.release();

		if (manager == null)
			manager = new FxMouseManager();
		
		manager.init(graph, this);
		
		mouseClicks = manager;
	}

	public void setShortcutManager(ShortcutManager manager) {
		if (shortcuts != null)
			shortcuts.release();

		if (manager == null)
			manager = new FxShortcutManager();

		manager.init(graph, this);

		shortcuts = manager;
	}

	@Override
	public void addKeyListener(KeyListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeKeyListener(KeyListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMouseListener(MouseListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeMouseListener(MouseListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMouseMotionListener(MouseMotionListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeMouseMotionListener(MouseMotionListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Viewer getViewer() {
		// TODO Auto-generated method stub
		return this.viewer;
	}

	

}