/*
 * Copyright 2006 - 2011 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
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
	public void beginSelectionAt(double x1, double y1) {
		renderer.beginSelectionAt(x1, y1);
		canvasChanged = true;
	}

	@Override
	public void selectionGrowsAt(double x, double y) {
		renderer.selectionGrowsAt(x, y);
		canvasChanged = true;
	}

	@Override
	public void endSelectionAt(double x2, double y2) {
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
	public ArrayList<GraphicElement> allNodesOrSpritesIn(double x1, double y1,
			double x2, double y2) {
		return renderer.allNodesOrSpritesIn(x1, y1, x2, y2);
	}

	@Override
	public GraphicElement findNodeOrSpriteAt(double x, double y) {
		return renderer.findNodeOrSpriteAt(x, y);
	}

	@Override
	public double getGraphDimension() {
		return renderer.getGraphDimension();
	}

	@Override
	public Point3 getViewCenter() {
		return renderer.getViewCenter();
	}

	@Override
	public double getViewPercent() {
		return renderer.getViewPercent();
	}

	@Override
	public double getViewRotation() {
		return renderer.getViewRotation();
	}

	@Override
	public void moveElementAtPx(GraphicElement element, double x, double y) {
		renderer.moveElementAtPx(element, x, y);
	}

	@Override
	public void resetView() {
		renderer.resetView();
		canvasChanged = true;
	}

	@Override
	public void setBounds(double minx, double miny, double minz, double maxx,
			double maxy, double maxz) {
		renderer.setBounds(minx, miny, minz, maxx, maxy, maxz);
	}

	@Override
	public void setViewCenter(double x, double y, double z) {
		renderer.setViewCenter(x, y, z);
		canvasChanged = true;
	}

	@Override
	public void setGraphViewport(double minx, double miny, double maxx, double maxy) {
		renderer.setGraphViewport(minx, miny, maxx, maxy);
		canvasChanged = true;
	}

	@Override
	public void removeGraphViewport() {
		renderer.removeGraphViewport();
		canvasChanged = true;
	}

	@Override
	public void setViewPercent(double percent) {
		renderer.setViewPercent(percent);
		canvasChanged = true;
	}

	@Override
	public void setViewRotation(double theta) {
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