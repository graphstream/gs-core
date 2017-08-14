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
package org.graphstream.ui.swingViewer.basicRenderer;

import org.graphstream.graph.Element;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.StyleGroupSet;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.FillMode;
import org.graphstream.ui.graphicGraph.stylesheet.Value;
import org.graphstream.ui.swingViewer.SwingGraphRendererBase;
import org.graphstream.ui.swingViewer.util.DefaultCamera;
import org.graphstream.ui.swingViewer.util.GraphMetrics;
import org.graphstream.ui.swingViewer.util.Graphics2DOutput;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.LayerRenderer;
import org.graphstream.ui.view.util.InteractiveElement;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A very simple view of the graph that respect only a subset of CSS.
 * 
 * <p>
 * This is a minimal implementation of a renderer that only supports a subset of
 * the CSS :
 * <ul>
 * <li>Colours</li>
 * <li>Widths</li>
 * <li>Borders</li>
 * </ul>
 * </p>
 * 
 * TODO - Les sprites. - Les bordures.
 */
public class SwingBasicGraphRenderer extends SwingGraphRendererBase {

	private static final Logger logger = Logger.getLogger(SwingBasicGraphRenderer.class.getName());

	// Attribute

	/**
	 * Set the view on the view port defined by the metrics.
	 */
	protected DefaultCamera camera = null;

	protected NodeRenderer nodeRenderer = new NodeRenderer();

	protected EdgeRenderer edgeRenderer = new EdgeRenderer();

	protected SpriteRenderer spriteRenderer = new SpriteRenderer();

	protected LayerRenderer<Graphics2D> backRenderer = null;

	protected LayerRenderer<Graphics2D> foreRenderer = null;

	protected PrintStream fpsLog = null;

	protected long T1 = 0;

	protected long steps = 0;

	protected double sumFps = 0;

	// Construction

	public SwingBasicGraphRenderer() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.ui.view.GraphRendererBase#open(org.graphstream.ui.
	 * graphicGraph.GraphicGraph, java.lang.Object)
	 */
	@Override
	public void open(GraphicGraph graph, Container renderingSurface) {
		super.open(graph, renderingSurface);
		camera = new DefaultCamera(graph);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.ui.view.GraphRendererBase#close()
	 */
	@Override
	public void close() {
		if (fpsLog != null) {
			fpsLog.flush();
			fpsLog.close();
			fpsLog = null;
		}

		camera = null;
		super.close();
	}

	// Access

	public Camera getCamera() {
		return camera;
	}

	@Override
	public Collection<GraphicElement> allGraphicElementsIn(EnumSet<InteractiveElement> types, double x1, double y1, double x2, double y2) {
		return camera.allGraphicElementsIn(graph,types,x1, y1, x2, y2);
	}

	@Override
	public GraphicElement findGraphicElementAt(EnumSet<InteractiveElement> types, double x, double y) {
		return camera.findGraphicElementAt(graph, types, x, y);
	}

	// Command

	public void render(Graphics2D g, int x, int y, int width, int height) {
		// If not closed, one or two renders can occur after closed.
		// Camera == null means closed. In case render occurs after closing
		// (called from the gfx thread).
		if (graph != null && g != null && camera != null) {
			beginFrame();

			if (camera.getGraphViewport() == null && camera.getMetrics().diagonal == 0
					&& (graph.getNodeCount() == 0 && graph.getSpriteCount() == 0)) {
				displayNothingToDo(g, width, height);
			} else {
				camera.setPadding(graph);
				camera.setViewport(x, y, width, height);
				renderGraph(g);
				renderSelection(g);
			}

			endFrame();
		}
	}

	protected void beginFrame() {
		if (graph.hasLabel("ui.log") && fpsLog == null) {
			try {
				fpsLog = new PrintStream(graph.getLabel("ui.log").toString());
			} catch (IOException e) {
				fpsLog = null;
				e.printStackTrace();
			}
		}

		if (fpsLog != null) {
			T1 = System.currentTimeMillis();
		}
	}

	protected void endFrame() {
		if (fpsLog != null) {
			steps += 1;
			long T2 = System.currentTimeMillis();
			long time = T2 - T1;
			double fps = 1000.0 / time;
			sumFps += fps;
			fpsLog.printf("%.3f   %d   %.3f%n", fps, time, (sumFps / steps));
		}
	}

	public void moveElementAtPx(GraphicElement element, double x, double y) {
		Point3 p = camera.transformPxToGu(camera.getMetrics().viewport[0] + x, camera.getMetrics().viewport[1] + y);
		element.move(p.x, p.y, element.getZ());
	}

	// Rendering

	protected void renderGraph(Graphics2D g) {
		StyleGroup style = graph.getStyle();

		setupGraphics(g);
		renderGraphBackground(g);
		renderBackLayer(g);
		camera.pushView(graph, g);
		renderGraphElements(g);

		if (style.getStrokeMode() != StyleConstants.StrokeMode.NONE && style.getStrokeWidth().value != 0) {
			GraphMetrics metrics = camera.getMetrics();
			Rectangle2D rect = new Rectangle2D.Double();
			double px1 = metrics.px1;
			Value stroke = style.getShadowWidth();

			rect.setFrame(metrics.lo.x, metrics.lo.y + px1, metrics.size.data[0] - px1, metrics.size.data[1] - px1);
			g.setStroke(new BasicStroke((float) metrics.lengthToGu(stroke)));
			g.setColor(graph.getStyle().getStrokeColor(0));
			g.draw(rect);
		}

		camera.popView(g);
		renderForeLayer(g);
	}

	protected void setupGraphics(Graphics2D g) {
		if (graph.hasAttribute("ui.antialias")) {
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		if (graph.hasAttribute("ui.quality")) {
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		} else {
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
					RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		}
	}

	/**
	 * Render the background of the graph.
	 *
	 * @param g
	 *            The Swing graphics.
	 */
	protected void renderGraphBackground(Graphics2D g) {
		StyleGroup group = graph.getStyle();

		if (group.getFillMode() != FillMode.NONE) {
			g.setColor(group.getFillColor(0));
			g.fillRect(0, 0, (int) camera.getMetrics().viewport[2], (int) camera.getMetrics().viewport[3]);
		}
	}

	/**
	 * Render the element of the graph.
	 *
	 * @param g
	 *            The Swing graphics.
	 */
	protected void renderGraphElements(Graphics2D g) {
		try {
			StyleGroupSet sgs = graph.getStyleGroups();

			if (sgs != null) {
				for (HashSet<StyleGroup> groups : sgs.zIndex()) {
					for (StyleGroup group : groups) {
						renderGroup(g, group);
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Unexpected error during graph render.", e);
		}
	}

	/**
	 * Render a style group.
	 *
	 * @param g
	 *            The Swing graphics.
	 * @param group
	 *            The group to render.
	 */
	protected void renderGroup(Graphics2D g, StyleGroup group) {
		switch (group.getType()) {
		case NODE:
			nodeRenderer.render(group, g, camera);
			break;
		case EDGE:
			edgeRenderer.render(group, g, camera);
			break;
		case SPRITE:
			spriteRenderer.render(group, g, camera);
			break;
		default:
			// Do nothing
			break;
		}
	}

	protected void setupSpriteStyle(Graphics2D g, StyleGroup group) {
		g.setColor(group.getFillColor(0));
	}

	protected void renderSelection(Graphics2D g) {
		if (selection != null && selection.x1 != selection.x2 && selection.y1 != selection.y2) {
			double x1 = selection.x1;
			double y1 = selection.y1;
			double x2 = selection.x2;
			double y2 = selection.y2;
			double t;

			double w = camera.getMetrics().getSize().data[0];
			double h = camera.getMetrics().getSize().data[1];

			if (x1 > x2) {
				t = x1;
				x1 = x2;
				x2 = t;
			}
			if (y1 > y2) {
				t = y1;
				y1 = y2;
				y2 = t;
			}

			Stroke s = g.getStroke();
			g.setStroke(new BasicStroke(1));

			g.setColor(new Color(50, 50, 200, 128));
			g.fillRect((int) x1, (int) y1, (int) (x2 - x1), (int) (y2 - y1));
			g.setColor(new Color(0, 0, 0, 128));
			g.drawLine(0, (int) y1, (int) w, (int) y1);
			g.drawLine(0, (int) y2, (int) w, (int) y2);
			g.drawLine((int) x1, 0, (int) x1, (int) h);
			g.drawLine((int) x2, 0, (int) x2, (int) h);
			g.setColor(new Color(50, 50, 200, 64));
			g.drawRect((int) x1, (int) y1, (int) (x2 - x1), (int) (y2 - y1));
			g.setStroke(s);
		}
	}

	protected void renderBackLayer(Graphics2D g) {
		if (backRenderer != null)
			renderLayer(g, backRenderer);
	}

	protected void renderForeLayer(Graphics2D g) {
		if (foreRenderer != null)
			renderLayer(g, foreRenderer);
	}

	protected void renderLayer(Graphics2D g, LayerRenderer<Graphics2D> renderer) {
		GraphMetrics metrics = camera.getMetrics();

		renderer.render(g, graph, metrics.ratioPx2Gu, (int) metrics.viewport[2], (int) metrics.viewport[3],
				metrics.loVisible.x, metrics.loVisible.y, metrics.hiVisible.x, metrics.hiVisible.y);
	}

	// Utility | Debug

	/**
	 * Show the centre, the low and high points of the graph, and the visible
	 * area (that should always map to the window borders).
	 */
	protected void debugVisibleArea(Graphics2D g) {
		Rectangle2D rect = new Rectangle2D.Double();
		GraphMetrics metrics = camera.getMetrics();

		double x = metrics.loVisible.x;
		double y = metrics.loVisible.y;
		double w = Math.abs(metrics.hiVisible.x - x);
		double h = Math.abs(metrics.hiVisible.y - y);

		rect.setFrame(x, y, w, h);
		g.setStroke(new BasicStroke((float) (metrics.px1 * 4)));
		g.setColor(Color.RED);
		g.draw(rect);

		g.setColor(Color.BLUE);
		Ellipse2D ellipse = new Ellipse2D.Double();
		double px1 = metrics.px1;
		ellipse.setFrame(camera.getViewCenter().x - 3 * px1, camera.getViewCenter().y - 3 * px1, px1 * 6, px1 * 6);
		g.fill(ellipse);
		ellipse.setFrame(metrics.lo.x - 3 * px1, metrics.lo.y - 3 * px1, px1 * 6, px1 * 6);
		g.fill(ellipse);
		ellipse.setFrame(metrics.hi.x - 3 * px1, metrics.hi.y - 3 * px1, px1 * 6, px1 * 6);
		g.fill(ellipse);
	}

	public void screenshot(String filename, int width, int height) {
		if (graph != null) {
			if (filename.endsWith("png") || filename.endsWith("PNG")) {
				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				renderGraph(img.createGraphics());

				File file = new File(filename);
				try {
					ImageIO.write(img, "png", file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (filename.endsWith("bmp") || filename.endsWith("BMP")) {
				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				renderGraph(img.createGraphics());

				File file = new File(filename);
				try {
					ImageIO.write(img, "bmp", file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (filename.endsWith("jpg") || filename.endsWith("JPG") || filename.endsWith("jpeg")
					|| filename.endsWith("JPEG")) {
				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				renderGraph(img.createGraphics());

				File file = new File(filename);
				try {
					ImageIO.write(img, "jpg", file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (filename.toLowerCase().endsWith("svg")) {
				try {
					String plugin = "org.graphstream.ui.batik.BatikGraphics2D";
					Class<?> c = Class.forName(plugin);
					Object o = c.newInstance();
					if (o instanceof Graphics2DOutput) {
						Graphics2DOutput out = (Graphics2DOutput) o;
						Graphics2D g2 = out.getGraphics();
						render(g2, (int) camera.getMetrics().viewport[0], (int) camera.getMetrics().viewport[1],
								(int) camera.getMetrics().viewport[2], (int) camera.getMetrics().viewport[3]);
						out.outputTo(filename);
					} else {
						logger.warning(String.format("Plugin %s is not an instance of Graphics2DOutput (%s).", plugin,
								o.getClass().getName()));
					}
				} catch (Exception e) {
					logger.log(Level.WARNING, "Unexpected error during screen shot.", e);
				}
			}
		}
	}

	public void setBackLayerRenderer(LayerRenderer<Graphics2D> renderer) {
		backRenderer = renderer;
	}

	public void setForeLayoutRenderer(LayerRenderer<Graphics2D> renderer) {
		foreRenderer = renderer;
	}

	// Style Group Listener

	public void elementStyleChanged(Element element, StyleGroup oldStyle, StyleGroup style) {
	}
}