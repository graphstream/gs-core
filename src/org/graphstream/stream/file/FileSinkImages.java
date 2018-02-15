/*
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

/**
 * @since 2010-07-23
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author kitskub <kitskub@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */
package org.graphstream.stream.file;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.graphstream.graph.Graph;
import org.graphstream.stream.GraphReplay;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.Sink;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.LayoutRunner;
import org.graphstream.ui.layout.Layouts;
import org.graphstream.ui.view.GraphRenderer;

/**
 * Output graph in image files.
 * 
 * <p>
 * Given a prefix "dir/prefix_" and an output policy, this sink will output
 * graph in an image file which name is prefix + a growing counter.
 * </p>
 * <p>
 * Then images can be processed to produce a movie. For example, with mencoder,
 * the following produce high quality movie :
 * </p>
 * 
 * <pre>
 * 
 * #!/bin/bash
 * 
 * EXT=png
 * CODEC=msmpeg4v2
 * BITRATE=6000
 * OPT="vcodec=mpeg4:vqscale=2:vhq:v4mv:trell:autoaspect"
 * FPS=15
 * PREFIX=$1
 * OUTPUT=$2
 * 
 * mencoder "mf://$PREFIX*.$EXT" -mf fps=$FPS:type=$EXT -ovc lavc -lavcopts $OPTS -o $OUTPUT -nosound -vf scale
 * 
 * </pre>
 */
public class FileSinkImages implements FileSink {
	/**
	 * Output resolutions.
	 */
	public static interface Resolution {
		int getWidth();

		int getHeight();
	}

	/**
	 * Common resolutions.
	 */
	public static enum Resolutions implements Resolution {
		QVGA(320, 240), CGA(320, 200), VGA(640, 480), NTSC(720, 480), PAL(768, 576), WVGA_5by3(800, 480), SVGA(800,
				600), WVGA_16by9(854, 480), WSVGA(1024, 600), XGA(1024, 768), HD720(1280, 720), WXGA_5by3(1280,
						768), WXGA_8by5(1280, 800), SXGA(1280, 1024), FWXGA(1366, 768), SXGAp(1400, 1050), WSXGAp(1680,
								1050), UXGA(1600, 1200), HD1080(1920, 1080), WUXGA(1920, 1200), TwoK(2048,
										1080), QXGA(2048, 1536), WQXGA(2560, 1600), QSXGA(2560, 2048), UHD_4K(3840,
												2160), UHD_8K_16by9(7680,
														4320), UHD_8K_17by8(8192, 4320), UHD_8K_1by1(8192, 8192)

		;

		final int width, height;

		Resolutions(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		@Override
		public String toString() {
			return String.format("%s (%dx%d)", name(), width, height);
		}
	}

	/**
	 * User-defined resolution.
	 */
	public static class CustomResolution implements Resolution {
		final int width, height;

		public CustomResolution(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		@Override
		public String toString() {
			return String.format("%dx%d", width, height);
		}
	}

	/**
	 * Output image type.
	 */
	public static enum OutputType {
		PNG(BufferedImage.TYPE_INT_ARGB, "png"), JPG(BufferedImage.TYPE_INT_RGB,
				"jpg"), png(BufferedImage.TYPE_INT_ARGB, "png"), jpg(BufferedImage.TYPE_INT_RGB, "jpg")

		;

		final int imageType;
		final String ext;

		OutputType(int imageType, String ext) {
			this.imageType = imageType;
			this.ext = ext;
		}
	}

	/**
	 * Output policy. Specify when an image is written. This is an important choice.
	 * Best choice is to divide the graph in steps and choose the *ByStepOutput*.
	 * Remember that if your graph has x nodes and *ByEventOutput* or
	 * *ByNodeEventOutput* is chosen, this will produce x images just for nodes
	 * creation.
	 */
	public static enum OutputPolicy {
		BY_EVENT, BY_ELEMENT_EVENT, BY_ATTRIBUTE_EVENT, BY_NODE_EVENT, BY_EDGE_EVENT, BY_GRAPH_EVENT, BY_STEP, BY_NODE_ADDED_REMOVED, BY_EDGE_ADDED_REMOVED, BY_NODE_ATTRIBUTE, BY_EDGE_ATTRIBUTE, BY_GRAPH_ATTRIBUTE, BY_LAYOUT_STEP, BY_NODE_MOVED, ON_RUNNER, NONE
	}

	/**
	 * Layout policy. Specify how layout is computed. It can be computed in its own
	 * thread with a LayoutRunner but if image rendering takes too much time, node
	 * positions will be very different between two images. To have a better result,
	 * we can choose to compute layout when a new image is rendered. This will
	 * smooth the move of nodes in the movie.
	 */
	public static enum LayoutPolicy {
		NO_LAYOUT, COMPUTED_IN_LAYOUT_RUNNER, COMPUTED_ONCE_AT_NEW_IMAGE, COMPUTED_FULLY_AT_NEW_IMAGE
	}

	/**
	 * Defines post rendering action on images.
	 */
	public static interface PostRenderer {
		void render(Graphics2D g);
	}

	/**
	 * Post rendering action allowing to add a logo-picture on images.
	 */
	protected static class AddLogoRenderer implements PostRenderer {
		/**
		 * The logo.
		 */
		BufferedImage logo;
		/**
		 * Logo position on images.
		 */
		int x, y;

		public AddLogoRenderer(String logoFile, int x, int y) throws IOException {
			File f = new File(logoFile);

			if (f.exists())
				this.logo = ImageIO.read(f);
			else
				this.logo = ImageIO.read(ClassLoader.getSystemResource(logoFile));

			this.x = x;
			this.y = y;
		}

		public void render(Graphics2D g) {
			g.drawImage(logo, x, y, null);
		}
	}

	/**
	 * Experimental. Allows to choose which renderer will be used.
	 */
	public static enum RendererType {
		BASIC("org.graphstream.ui.swingViewer.basicRenderer.SwingBasicGraphRenderer"), SCALA(
				"org.graphstream.ui.j2dviewer.J2DGraphRenderer")

		;

		final String classname;

		RendererType(String cn) {
			this.classname = cn;
		}
	}

	public static enum Quality {
		LOW, MEDIUM, HIGH
	}

	private static final Logger LOGGER = Logger.getLogger(FileSinkImages.class.getName());

	protected Resolution resolution;
	protected OutputType outputType;
	protected GraphRenderer renderer;
	protected String filePrefix;
	protected BufferedImage image;
	protected Graphics2D g2d;
	protected final GraphicGraph gg;
	protected Sink sink;
	protected int counter;
	protected OutputPolicy outputPolicy;
	protected LinkedList<PostRenderer> postRenderers;
	protected LayoutPolicy layoutPolicy;
	protected LayoutRunner optLayout;
	protected ProxyPipe layoutPipeIn;
	protected Layout layout;
	protected float layoutStabilizationLimit = 0.9f;
	protected int layoutStepAfterStabilization = 10;
	protected int layoutStepPerFrame = 4;
	protected int layoutStepWithoutFrame = 0;
	protected long outputRunnerDelay = 10;
	protected boolean outputRunnerAlive = false;
	protected OutputRunner outputRunner;
	protected ThreadProxyPipe outputRunnerProxy;
	protected boolean clearImageBeforeOutput = false;
	protected boolean hasBegan = false;
	protected boolean autofit = true;
	protected String styleSheet = null;

	public FileSinkImages() {
		this(OutputType.PNG, Resolutions.HD720);
	}

	public FileSinkImages(OutputType type, Resolution resolution) {
		this("frame_", type, resolution, OutputPolicy.NONE);
	}

	public FileSinkImages(String prefix, OutputType type, Resolution resolution, OutputPolicy outputPolicy) {
		this.resolution = resolution;
		this.outputType = type;
		this.filePrefix = prefix;
		this.counter = 0;
		this.gg = new GraphicGraph(prefix);
		this.postRenderers = new LinkedList<PostRenderer>();
		this.layoutPolicy = LayoutPolicy.NO_LAYOUT;
		this.layout = null;
		this.optLayout = null;
		this.layoutPipeIn = null;
		this.sink = gg;

		setOutputPolicy(outputPolicy);
		setRenderer(RendererType.BASIC);

		initImage();
	}

	/**
	 * Enable high-quality rendering and anti-aliasing.
	 */
	public void setQuality(Quality q) {
		switch (q) {
		case LOW:
			if (gg.hasAttribute("ui.quality"))
				gg.removeAttribute("ui.quality");
			if (gg.hasAttribute("ui.antialias"))
				gg.removeAttribute("ui.antialias");

			break;
		case MEDIUM:
			if (!gg.hasAttribute("ui.quality"))
				gg.setAttribute("ui.quality");
			if (gg.hasAttribute("ui.antialias"))
				gg.removeAttribute("ui.antialias");

			break;
		case HIGH:
			if (!gg.hasAttribute("ui.quality"))
				gg.setAttribute("ui.quality");
			if (!gg.hasAttribute("ui.antialias"))
				gg.setAttribute("ui.antialias");

			break;
		}
	}

	/**
	 * Defines style of the graph as a css stylesheet.
	 * 
	 * @param styleSheet
	 *            the style sheet
	 */
	public void setStyleSheet(String styleSheet) {
		this.styleSheet = styleSheet;
		gg.setAttribute("ui.stylesheet", styleSheet);
	}

	/**
	 * Set resolution of images.
	 * 
	 * @param r
	 *            resolution
	 */
	public void setResolution(Resolution r) {
		if (r != resolution) {
			resolution = r;
			initImage();
		}
	}

	/**
	 * Set a custom resolution.
	 * 
	 * @param width
	 * @param height
	 */
	public void setResolution(int width, int height) {
		if (resolution == null || resolution.getWidth() != width || resolution.getHeight() != height) {
			resolution = new CustomResolution(width, height);
			initImage();
		}
	}

	/**
	 * Set the renderer type. This is experimental.
	 * 
	 * @param rendererType
	 */
	@SuppressWarnings("unchecked")
	public void setRenderer(RendererType rendererType) {
		try {
			Class<? extends GraphRenderer> clazz = (Class<? extends GraphRenderer>) Class
					.forName(rendererType.classname);

			GraphRenderer obj = clazz.newInstance();

			if (this.renderer != null)
				this.renderer.close();

			this.renderer = obj;
			this.renderer.open(gg, null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			LOGGER.warning(String.format("not a renderer \"%s\"%n", rendererType.classname));
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the output policy.
	 * 
	 * @param policy
	 *            policy defining when images are produced
	 */
	public void setOutputPolicy(OutputPolicy policy) {
		this.outputPolicy = policy;
	}

	/**
	 * Set the layout policy.
	 * 
	 * @param policy
	 *            policy defining how the layout is computed
	 */
	public synchronized void setLayoutPolicy(LayoutPolicy policy) {
		if (policy != layoutPolicy) {
			switch (layoutPolicy) {
			case COMPUTED_IN_LAYOUT_RUNNER:
				// layout.removeListener(this);
				optLayout.release();
				optLayout = null;
				layoutPipeIn.removeAttributeSink(gg);
				layoutPipeIn = null;
				layout = null;
				break;
			case COMPUTED_ONCE_AT_NEW_IMAGE:
				// layout.removeListener(this);
				gg.removeSink(layout);
				layout.removeAttributeSink(gg);
				layout = null;
				break;
			default:
				break;
			}

			switch (policy) {
			case COMPUTED_IN_LAYOUT_RUNNER:
				layout = Layouts.newLayoutAlgorithm();
				optLayout = new InnerLayoutRunner();
				break;
			case COMPUTED_FULLY_AT_NEW_IMAGE:
			case COMPUTED_ONCE_AT_NEW_IMAGE:
				layout = Layouts.newLayoutAlgorithm();
				gg.addSink(layout);
				layout.addAttributeSink(gg);
				break;
			default:
				break;
			}

			// layout.addListener(this);
			layoutPolicy = policy;
		}
	}

	/**
	 * Set the amount of step before output a new image. This is used only in
	 * ByLayoutStepOutput output policy.
	 * 
	 * @param spf
	 *            step per frame
	 */
	public void setLayoutStepPerFrame(int spf) {
		this.layoutStepPerFrame = spf;
	}

	/**
	 * Set the amount of steps after the stabilization of the algorithm.
	 * 
	 * @param sas
	 *            step after stabilization.
	 */
	public void setLayoutStepAfterStabilization(int sas) {
		this.layoutStepAfterStabilization = sas;
	}

	/**
	 * Set the stabilization limit of the layout used to compute coordinates of
	 * nodes. See
	 * {@link org.graphstream.ui.layout.Layout#setStabilizationLimit(double)} for
	 * more informations about this limit.
	 * 
	 * @param limit
	 */
	public void setLayoutStabilizationLimit(double limit) {
		if (layout == null)
			throw new NullPointerException("did you enable layout ?");

		layout.setStabilizationLimit(limit);
	}

	/**
	 * Add a logo on images.
	 * 
	 * @param logoFile
	 *            path to the logo picture-file
	 * @param x
	 *            x position of the logo (top-left corner is (0;0))
	 * @param y
	 *            y position of the logo
	 */
	public void addLogo(String logoFile, int x, int y) {
		PostRenderer pr;

		try {
			pr = new AddLogoRenderer(logoFile, x, y);
			postRenderers.add(pr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void setOutputRunnerEnabled(boolean on) {
		if (!on && outputRunnerAlive) {
			outputRunnerAlive = false;

			try {
				if (outputRunner != null)
					outputRunner.join();
			} catch (InterruptedException e) {
				// ... ?
			}

			outputRunner = null;
			sink = gg;

			if (outputRunnerProxy != null)
				outputRunnerProxy.pump();
		}

		outputRunnerAlive = on;

		if (outputRunnerAlive) {
			if (outputRunnerProxy == null) {
				outputRunnerProxy = new ThreadProxyPipe();
				outputRunnerProxy.init(gg);
			}

			sink = outputRunnerProxy;
			outputRunner = new OutputRunner();
			outputRunner.start();
		}
	}

	public void setOutputRunnerDelay(long delay) {
		outputRunnerDelay = delay;
	}

	public void stabilizeLayout(double limit) {
		if (layout != null) {
			while (layout.getStabilization() < limit)
				layout.compute();
		}
	}

	public Point3 getViewCenter() {
		return renderer.getCamera().getViewCenter();
	}

	public void setViewCenter(double x, double y) {
		renderer.getCamera().setViewCenter(x, y, 0);
	}

	public double getViewPercent() {
		return renderer.getCamera().getViewPercent();
	}

	public void setViewPercent(double zoom) {
		renderer.getCamera().setViewPercent(zoom);
	}

	public void setGraphViewport(double minx, double miny, double maxx, double maxy) {
		renderer.getCamera().setGraphViewport(minx, miny, maxx, maxy);
	}

	public void setClearImageBeforeOutputEnabled(boolean on) {
		clearImageBeforeOutput = on;
	}

	public void setAutofit(boolean on) {
		autofit = on;
	}

	protected void initImage() {
		image = new BufferedImage(resolution.getWidth(), resolution.getHeight(), outputType.imageType);

		g2d = image.createGraphics();
	}

	protected void clearGG() {
		gg.clear();

		if (styleSheet != null)
			gg.setAttribute("ui.stylesheet", styleSheet);

		if (layout != null)
			layout.clear();
	}

	/**
	 * Produce a new image.
	 */
	public void outputNewImage() {
		outputNewImage(String.format("%s%06d.%s", filePrefix, counter++, outputType.ext));
	}

	public synchronized void outputNewImage(String filename) {
		switch (layoutPolicy) {
		case COMPUTED_IN_LAYOUT_RUNNER:
			layoutPipeIn.pump();
			break;
		case COMPUTED_ONCE_AT_NEW_IMAGE:
			if (layout != null)
				layout.compute();
			break;
		case COMPUTED_FULLY_AT_NEW_IMAGE:
			stabilizeLayout(layout.getStabilizationLimit());
			break;
		default:
			break;
		}

		if (resolution.getWidth() != image.getWidth() || resolution.getHeight() != image.getHeight())
			initImage();

		if (clearImageBeforeOutput) {
			for (int x = 0; x < resolution.getWidth(); x++)
				for (int y = 0; y < resolution.getHeight(); y++)
					image.setRGB(x, y, 0x00000000);
		}

		if (gg.getNodeCount() > 0) {
			if (autofit) {
				gg.computeBounds();

				Point3 lo = gg.getMinPos();
				Point3 hi = gg.getMaxPos();

				renderer.getCamera().setBounds(lo.x, lo.y, lo.z, hi.x, hi.y, hi.z);
			}

			renderer.render(g2d, 0, 0, resolution.getWidth(), resolution.getHeight());
		}

		for (PostRenderer action : postRenderers)
			action.render(g2d);

		image.flush();

		try {
			File out = new File(filename);

			if (out.getParent() != null && !out.getParentFile().exists())
				out.getParentFile().mkdirs();

			ImageIO.write(image, outputType.name(), out);

			printProgress();
		} catch (IOException e) {
			// ?
		}
	}

	protected void printProgress() {
		LOGGER.info(String.format("\033[s\033[K%d images written\033[u", counter));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSink#begin(java.io.OutputStream)
	 */
	public void begin(OutputStream stream) throws IOException {
		throw new IOException("not implemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSink#begin(java.io.Writer)
	 */
	public void begin(Writer writer) throws IOException {
		throw new IOException("not implemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSink#begin(java.lang.String)
	 */
	public void begin(String prefix) throws IOException {
		this.filePrefix = prefix;
		this.hasBegan = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSink#flush()
	 */
	public void flush() throws IOException {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.stream.file.FileSink#end()
	 */
	public void end() throws IOException {
		flush();
		this.hasBegan = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.FileSink#writeAll(org.graphstream.graph.Graph ,
	 * java.io.OutputStream)
	 */
	public void writeAll(Graph g, OutputStream stream) throws IOException {
		throw new IOException("not implemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.FileSink#writeAll(org.graphstream.graph.Graph ,
	 * java.io.Writer)
	 */
	public void writeAll(Graph g, Writer writer) throws IOException {
		throw new IOException("not implemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.stream.file.FileSink#writeAll(org.graphstream.graph.Graph ,
	 * java.lang.String)
	 */
	public synchronized void writeAll(Graph g, String filename) throws IOException {
		clearGG();

		GraphReplay replay = new GraphReplay(String.format("file_sink_image-write_all-replay-%x", System.nanoTime()));

		replay.addSink(gg);
		replay.replay(g);
		replay.removeSink(gg);

		outputNewImage(filename);

		clearGG();
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId, String attribute, Object value) {
		sink.edgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);

		switch (outputPolicy) {
		case BY_EVENT:
		case BY_EDGE_EVENT:
		case BY_EDGE_ATTRIBUTE:
		case BY_ATTRIBUTE_EVENT:
			if (hasBegan)
				outputNewImage();
			break;
		default:
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void edgeAttributeChanged(String sourceId, long timeId, String edgeId, String attribute, Object oldValue,
			Object newValue) {
		sink.edgeAttributeChanged(sourceId, timeId, edgeId, attribute, oldValue, newValue);

		switch (outputPolicy) {
		case BY_EVENT:
		case BY_EDGE_EVENT:
		case BY_EDGE_ATTRIBUTE:
		case BY_ATTRIBUTE_EVENT:
			if (hasBegan)
				outputNewImage();
			break;
		default:
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
		sink.edgeAttributeRemoved(sourceId, timeId, edgeId, attribute);

		switch (outputPolicy) {
		case BY_EVENT:
		case BY_EDGE_EVENT:
		case BY_EDGE_ATTRIBUTE:
		case BY_ATTRIBUTE_EVENT:
			if (hasBegan)
				outputNewImage();
			break;
		default:
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
		sink.graphAttributeAdded(sourceId, timeId, attribute, value);

		switch (outputPolicy) {
		case BY_EVENT:
		case BY_GRAPH_EVENT:
		case BY_GRAPH_ATTRIBUTE:
		case BY_ATTRIBUTE_EVENT:
			if (hasBegan)
				outputNewImage();
			break;
		default:
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void graphAttributeChanged(String sourceId, long timeId, String attribute, Object oldValue,
			Object newValue) {
		sink.graphAttributeChanged(sourceId, timeId, attribute, oldValue, newValue);

		switch (outputPolicy) {
		case BY_EVENT:
		case BY_GRAPH_EVENT:
		case BY_GRAPH_ATTRIBUTE:
		case BY_ATTRIBUTE_EVENT:
			if (hasBegan)
				outputNewImage();
			break;
		default:
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
		sink.graphAttributeRemoved(sourceId, timeId, attribute);

		switch (outputPolicy) {
		case BY_EVENT:
		case BY_GRAPH_EVENT:
		case BY_GRAPH_ATTRIBUTE:
		case BY_ATTRIBUTE_EVENT:
			if (hasBegan)
				outputNewImage();
			break;
		default:
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
		sink.nodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);

		switch (outputPolicy) {
		case BY_EVENT:
		case BY_NODE_EVENT:
		case BY_NODE_ATTRIBUTE:
		case BY_ATTRIBUTE_EVENT:
			if (hasBegan)
				outputNewImage();
			break;
		default:
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void nodeAttributeChanged(String sourceId, long timeId, String nodeId, String attribute, Object oldValue,
			Object newValue) {
		sink.nodeAttributeChanged(sourceId, timeId, nodeId, attribute, oldValue, newValue);

		switch (outputPolicy) {
		case BY_EVENT:
		case BY_NODE_EVENT:
		case BY_NODE_ATTRIBUTE:
		case BY_ATTRIBUTE_EVENT:
			if (hasBegan)
				outputNewImage();
			break;
		default:
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
		sink.nodeAttributeRemoved(sourceId, timeId, nodeId, attribute);

		switch (outputPolicy) {
		case BY_EVENT:
		case BY_NODE_EVENT:
		case BY_NODE_ATTRIBUTE:
		case BY_ATTRIBUTE_EVENT:
			if (hasBegan)
				outputNewImage();
			break;
		default:
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId, String fromNodeId, String toNodeId,
			boolean directed) {
		sink.edgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId, directed);

		switch (outputPolicy) {
		case BY_EVENT:
		case BY_EDGE_EVENT:
		case BY_EDGE_ADDED_REMOVED:
		case BY_ELEMENT_EVENT:
			if (hasBegan)
				outputNewImage();
			break;
		default:
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		sink.edgeRemoved(sourceId, timeId, edgeId);

		switch (outputPolicy) {
		case BY_EVENT:
		case BY_EDGE_EVENT:
		case BY_EDGE_ADDED_REMOVED:
		case BY_ELEMENT_EVENT:
			if (hasBegan)
				outputNewImage();
			break;
		default:
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void graphCleared(String sourceId, long timeId) {
		sink.graphCleared(sourceId, timeId);

		switch (outputPolicy) {
		case BY_EVENT:
		case BY_GRAPH_EVENT:
		case BY_NODE_ADDED_REMOVED:
		case BY_EDGE_ADDED_REMOVED:
		case BY_ELEMENT_EVENT:
			if (hasBegan)
				outputNewImage();
			break;
		default:
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		sink.nodeAdded(sourceId, timeId, nodeId);

		switch (outputPolicy) {
		case BY_EVENT:
		case BY_NODE_EVENT:
		case BY_NODE_ADDED_REMOVED:
		case BY_ELEMENT_EVENT:
			if (hasBegan)
				outputNewImage();
			break;
		default:
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		sink.nodeRemoved(sourceId, timeId, nodeId);

		switch (outputPolicy) {
		case BY_EVENT:
		case BY_NODE_EVENT:
		case BY_NODE_ADDED_REMOVED:
		case BY_ELEMENT_EVENT:
			if (hasBegan)
				outputNewImage();
			break;
		default:
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void stepBegins(String sourceId, long timeId, double step) {
		sink.stepBegins(sourceId, timeId, step);

		switch (outputPolicy) {
		case BY_EVENT:
		case BY_STEP:
			if (hasBegan)
				outputNewImage();
			break;
		default:
			break;
		}
	}

	// public void nodeMoved(String id, double x, double y, double z) {
	// switch (outputPolicy) {
	// case BY_NODE_MOVED:
	// if (hasBegan)
	// outputNewImage();
	// break;
	// }
	// }

	// public void nodeInfos(String id, double dx, double dy, double dz) {
	// }

	// public void edgeChanged(String id, double[] points) {
	// }

	// public void nodesMoved(Map<String, double[]> nodes) {
	// switch (outputPolicy) {
	// case BY_NODE_MOVED:
	// if (hasBegan)
	// outputNewImage();
	// break;
	// }
	// }

	// public void edgesChanged(Map<String, double[]> edges) {
	// }

	// public void stepCompletion(double percent) {
	// switch (outputPolicy) {
	// case BY_LAYOUT_STEP:
	// layoutStepWithoutFrame++;
	//
	// if (layoutStepWithoutFrame >= layoutStepPerFrame) {
	// if (hasBegan)
	// outputNewImage();
	// layoutStepWithoutFrame = 0;
	// }
	//
	// break;
	// }
	// }

	public static enum Option {
		IMAGE_PREFIX("image-prefix", 'p', "prefix of outputted images", true, true, "image_"), IMAGE_TYPE("image-type",
				't', "image type. one of " + Arrays.toString(OutputType.values()), true, true,
				"PNG"), IMAGE_RESOLUTION("image-resolution", 'r',
						"defines images resolution. \"width x height\" or one of "
								+ Arrays.toString(Resolutions.values()),
						true, true, "HD720"), OUTPUT_POLICY("output-policy", 'e',
								"defines when images are outputted. one of " + Arrays.toString(OutputPolicy.values()),
								true, true, "ByStepOutput"), LOGO("logo", 'l', "add a logo to images", true, true,
										null), STYLESHEET("stylesheet", 's',
												"defines stylesheet of graph. can be a file or a string.", true, true,
												null), QUALITY("quality", 'q',
														"defines quality of rendering. one of "
																+ Arrays.toString(Quality.values()),
														true, true, "HIGH");
		String fullopts;
		char shortopts;
		String description;
		boolean optional;
		boolean valuable;
		String defaultValue;

		Option(String fullopts, char shortopts, String description, boolean optional, boolean valuable,
				String defaultValue) {
			this.fullopts = fullopts;
			this.shortopts = shortopts;
			this.description = description;
			this.optional = optional;
			this.valuable = valuable;
			this.defaultValue = defaultValue;
		}
	}

	protected class InnerLayoutRunner extends LayoutRunner {

		public InnerLayoutRunner() {
			super(FileSinkImages.this.gg, FileSinkImages.this.layout, true, true);

			FileSinkImages.this.layoutPipeIn = newLayoutPipe();
			FileSinkImages.this.layoutPipeIn.addAttributeSink(FileSinkImages.this.gg);
		}

		public void run() {

			int stepAfterStabilization = 0;

			do {
				pumpPipe.pump();
				layout.compute();

				if (layout.getStabilization() > layout.getStabilizationLimit())
					stepAfterStabilization++;
				else
					stepAfterStabilization = 0;

				nap(80);

				if (stepAfterStabilization > layoutStepAfterStabilization)
					loop = false;
			} while (loop);
		}
	}

	protected class OutputRunner extends Thread {
		public OutputRunner() {
			setDaemon(true);
		}

		public void run() {
			while (outputRunnerAlive && outputPolicy == OutputPolicy.ON_RUNNER) {
				outputRunnerProxy.pump();
				if (hasBegan)
					outputNewImage();

				try {
					Thread.sleep(outputRunnerDelay);
				} catch (InterruptedException e) {
					outputRunnerAlive = false;
				}
			}
		}
	}

	public static void usage() {
		LOGGER.info(String.format("usage: java %s [options] fichier.dgs%n", FileSinkImages.class.getName()));
		LOGGER.info(String.format("where options in:%n"));
		for (Option option : Option.values()) {
			LOGGER.info(String.format("%n --%s%s , -%s %s%n%s%n", option.fullopts, option.valuable ? "=..." : "",
					option.shortopts, option.valuable ? "..." : "", option.description));
		}
	}

	public static void main(String... args) throws IOException {

		HashMap<Option, String> options = new HashMap<Option, String>();
		LinkedList<String> others = new LinkedList<String>();

		for (Option option : Option.values())
			if (option.defaultValue != null)
				options.put(option, option.defaultValue);

		if (args != null && args.length > 0) {
			Pattern valueGetter = Pattern.compile("^--\\w[\\w-]*\\w?(?:=(?:\"([^\"]*)\"|([^\\s]*)))$");

			for (int i = 0; i < args.length; i++) {

				if (args[i].matches("^--\\w[\\w-]*\\w?(=(\"[^\"]*\"|[^\\s]*))?$")) {
					boolean found = false;
					for (Option option : Option.values()) {
						if (args[i].startsWith("--" + option.fullopts + "=")) {
							Matcher m = valueGetter.matcher(args[i]);

							if (m.matches()) {
								options.put(option, m.group(1) == null ? m.group(2) : m.group(1));
							}

							found = true;
							break;
						}
					}

					if (!found) {
						LOGGER.severe(
								String.format("unknown option: %s%n", args[i].substring(0, args[i].indexOf('='))));
						System.exit(1);
					}
				} else if (args[i].matches("^-\\w$")) {
					boolean found = false;

					for (Option option : Option.values()) {
						if (args[i].equals("-" + option.shortopts)) {
							options.put(option, args[++i]);
							break;
						}
					}

					if (!found) {
						LOGGER.severe(String.format("unknown option: %s%n", args[i]));
						System.exit(1);
					}
				} else {
					others.addLast(args[i]);
				}
			}
		} else {
			usage();
			System.exit(0);
		}

		LinkedList<String> errors = new LinkedList<String>();

		if (others.size() == 0) {
			errors.add("dgs file name missing.");
		}

		String imagePrefix;
		OutputType outputType = null;
		OutputPolicy outputPolicy = null;
		Resolution resolution = null;
		Quality quality = null;
		String logo;
		String stylesheet;

		imagePrefix = options.get(Option.IMAGE_PREFIX);

		try {
			outputType = OutputType.valueOf(options.get(Option.IMAGE_TYPE));
		} catch (IllegalArgumentException e) {
			errors.add("bad image type: " + options.get(Option.IMAGE_TYPE));
		}

		try {
			outputPolicy = OutputPolicy.valueOf(options.get(Option.OUTPUT_POLICY));
		} catch (IllegalArgumentException e) {
			errors.add("bad output policy: " + options.get(Option.OUTPUT_POLICY));
		}

		try {
			quality = Quality.valueOf(options.get(Option.QUALITY));
		} catch (IllegalArgumentException e) {
			errors.add("bad quality: " + options.get(Option.QUALITY));
		}

		logo = options.get(Option.LOGO);
		stylesheet = options.get(Option.STYLESHEET);

		try {
			resolution = Resolutions.valueOf(options.get(Option.IMAGE_RESOLUTION));
		} catch (IllegalArgumentException e) {
			Pattern p = Pattern.compile("^\\s*(\\d+)\\s*x\\s*(\\d+)\\s*$");
			Matcher m = p.matcher(options.get(Option.IMAGE_RESOLUTION));

			if (m.matches()) {
				resolution = new CustomResolution(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
			} else {
				errors.add("bad resolution: " + options.get(Option.IMAGE_RESOLUTION));
			}
		}

		if (stylesheet != null && stylesheet.length() < 1024) {
			File test = new File(stylesheet);

			if (test.exists()) {
				FileReader in = new FileReader(test);
				char[] buffer = new char[128];
				String content = "";

				while (in.ready()) {
					int c = in.read(buffer, 0, 128);
					content += new String(buffer, 0, c);
				}

				stylesheet = content;
				in.close();
			}
		}

		{
			File test = new File(others.peek());
			if (!test.exists())
				errors.add(String.format("file \"%s\" does not exist", others.peek()));
		}

		if (errors.size() > 0) {
			LOGGER.info(String.format("error:%n"));

			for (String error : errors)
				LOGGER.info(String.format("- %s%n", error));

			System.exit(1);
		}

		FileSourceDGS dgs = new FileSourceDGS();
		FileSinkImages fsi = new FileSinkImages(imagePrefix, outputType, resolution, outputPolicy);

		dgs.addSink(fsi);

		if (logo != null)
			fsi.addLogo(logo, 0, 0);

		fsi.setQuality(quality);
		if (stylesheet != null)
			fsi.setStyleSheet(stylesheet);

		boolean next = true;

		dgs.begin(others.get(0));

		while (next)
			next = dgs.nextStep();

		dgs.end();
	}
}
