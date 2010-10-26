/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Copyright 2006 - 2009
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */

package org.graphstream.ui.swingViewer;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.util.MouseManager;
import org.graphstream.ui.swingViewer.util.ShortcutManager;

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
 * This mechanism pushes a repaint query each time the viewer asks us to
 * repaint. Two flags are provided to know what to repaint :
 * {@link #graphChanged} allows to know when the graph needs to be rendered anew
 * because its structure changed and {@link #canvasChanged} allows to know one
 * must repaint because the rendering canvas was resized, shown, etc.
 * </p>
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
public class DefaultView extends View implements ComponentListener,
		WindowListener {
	// Attribute

	/**
	 * Parent viewer.
	 */
	protected Viewer viewer;

	/**
	 * The graph to render, shortcut to the viewers reference.
	 */
	protected GraphicGraph graph;

	/**
	 * The (optional) frame.
	 */
	protected JFrame frame;

	/**
	 * The graph changed since the last repaint.
	 */
	protected boolean graphChanged;

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

	/**
	 * Set to true each time the drawing canvas changed.
	 */
	protected boolean canvasChanged = true;

	// Construction

	public DefaultView(Viewer viewer, String identifier, GraphRenderer renderer) {
		super(identifier);

		this.viewer = viewer;
		this.graph = viewer.getGraphicGraph();
		this.renderer = renderer;
		shortcuts = new ShortcutManager(this);
		mouseClicks = new MouseManager(this.graph, this);

		addComponentListener(this);
		addKeyListener(shortcuts);
		addMouseListener(mouseClicks);
		addMouseMotionListener(mouseClicks);
		renderer.open(graph, this);
	}

	// Access

	// Command

	@Override
	public void display(GraphicGraph graph, boolean graphChanged) {
		this.graphChanged = graphChanged;

		repaint();
	}

	@Override
	public void paint(Graphics g) {
		if (graphChanged || canvasChanged) {
			checkTitle();

			Graphics2D g2 = (Graphics2D) g;

			// super.paint( g );
			render(g2);
			paintChildren(g2);

			graphChanged = canvasChanged = false;
		}
	}

	protected void checkTitle() {
		if (frame != null) {
			String titleAttr = String.format("ui.%s.title", getId());
			String title = (String) graph.getLabel(titleAttr);

			if (title == null)
				title = (String) graph.getLabel("ui.default.title");

			if (title == null)
				title = (String) graph.getLabel("ui.title");
			
			if (title != null)
				frame.setTitle(title);
		}
	}

	@Override
	public void close(GraphicGraph graph) {
		renderer.close();
		graph.addAttribute("ui.viewClosed", getId());
		removeComponentListener(this);
		removeKeyListener(shortcuts);
		removeMouseListener(mouseClicks);
		removeMouseMotionListener(mouseClicks);
		openInAFrame(false);
	}

	@Override
	public void openInAFrame(boolean on) {
		if (on) {
			if (frame == null) {
				frame = new JFrame("GraphStream");
				frame.setLayout(new BorderLayout());
				frame.add(this, BorderLayout.CENTER);
				frame.setSize(800, 600);
				frame.setVisible(true);
				frame.addWindowListener(this);
				frame.addKeyListener(shortcuts);
			} else {
				frame.setVisible(true);
			}
		} else {
			if (frame != null) {
				frame.removeWindowListener(this);
				frame.removeKeyListener(shortcuts);
				frame.remove(this);
				frame.setVisible(false);
				frame.dispose();
			}
		}
	}

	public void render(Graphics2D g) {
		setBackground(graph.getStyle().getFillColor(0));
		renderer.render(g, getWidth(), getHeight());

		String screenshot = (String) graph.getLabel("ui.screenshot");

		if (screenshot != null) {
			graph.removeAttribute("ui.screenshot");
			renderer.screenshot(screenshot, getWidth(), getHeight());
		}
	}

	// Selection

	@Override
	public void beginSelectionAt(float x1, float y1) {
		renderer.beginSelectionAt(x1, y1);
		canvasChanged = true;
	}

	@Override
	public void selectionGrowsAt(float x, float y) {
		renderer.selectionGrowsAt(x, y);
		canvasChanged = true;
	}

	@Override
	public void endSelectionAt(float x2, float y2) {
		renderer.endSelectionAt(x2, y2);
		canvasChanged = true;
	}

	// Component listener

	public void componentShown(ComponentEvent e) {
		canvasChanged = true;
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
		canvasChanged = true;
	}

	// Window Listener

	public void windowActivated(WindowEvent e) {
		canvasChanged = true;
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		graph.addAttribute("ui.viewClosed", getId());

		switch (viewer.getCloseFramePolicy()) {
		case CLOSE_VIEWER:
			viewer.removeView(getId());
			break;
		case HIDE_ONLY:
			if (frame != null)
				frame.setVisible(false);
			break;
		case EXIT:
			System.exit(0);
		default:
			throw new RuntimeException(
					String.format(
							"The %s view is not up to date, do not know %s CloseFramePolicy.",
							getClass().getName(), viewer.getCloseFramePolicy()));
		}
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
		canvasChanged = true;
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
		graph.removeAttribute("ui.viewClosed");
		canvasChanged = true;
	}

	// Methods deferred to the renderer

	@Override
	public ArrayList<GraphicElement> allNodesOrSpritesIn(float x1, float y1,
			float x2, float y2) {
		return renderer.allNodesOrSpritesIn(x1, y1, x2, y2);
	}

	@Override
	public GraphicElement findNodeOrSpriteAt(float x, float y) {
		return renderer.findNodeOrSpriteAt(x, y);
	}

	@Override
	public float getGraphDimension() {
		return renderer.getGraphDimension();
	}

	@Override
	public Point3 getViewCenter() {
		return renderer.getViewCenter();
	}

	@Override
	public float getViewPercent() {
		return renderer.getViewPercent();
	}

	@Override
	public float getViewRotation() {
		return renderer.getViewRotation();
	}

	@Override
	public void moveElementAtPx(GraphicElement element, float x, float y) {
		renderer.moveElementAtPx(element, x, y);
	}

	@Override
	public void resetView() {
		renderer.resetView();
		canvasChanged = true;
	}

	@Override
	public void setBounds(float minx, float miny, float minz, float maxx,
			float maxy, float maxz) {
		renderer.setBounds(minx, miny, minz, maxx, maxy, maxz);
	}

	@Override
	public void setViewCenter(float x, float y, float z) {
		renderer.setViewCenter(x, y, z);
		canvasChanged = true;
	}

	@Override
	public void setGraphViewport(float minx, float miny, float maxx, float maxy) {
		renderer.setGraphViewport(minx, miny, maxx, maxy);
		canvasChanged = true;
	}

	@Override
	public void removeGraphViewport() {
		renderer.removeGraphViewport();
		canvasChanged = true;
	}

	@Override
	public void setViewPercent(float percent) {
		renderer.setViewPercent(percent);
		canvasChanged = true;
	}

	@Override
	public void setViewRotation(float theta) {
		renderer.setViewRotation(theta);
		canvasChanged = true;
	}

	@Override
	public void setBackLayerRenderer(LayerRenderer renderer) {
		this.renderer.setBackLayerRenderer(renderer);
		canvasChanged = true;
	}

	@Override
	public void setForeLayoutRenderer(LayerRenderer renderer) {
		this.renderer.setForeLayoutRenderer(renderer);
		canvasChanged = true;
	}
}