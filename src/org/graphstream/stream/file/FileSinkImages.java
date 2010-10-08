/*
 * This file is part of GraphStream.
 * 
 * GraphStream is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GraphStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GraphStream.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2006 - 2010
 * 	Julien Baudry
 * 	Antoine Dutot
 * 	Yoann Pigné
 * 	Guilhelm Savin
 */
package org.graphstream.stream.file;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import org.graphstream.stream.ProxyPipe;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.LayoutRunner;
import org.graphstream.ui.layout.Layouts;
import org.graphstream.ui.swingViewer.GraphRenderer;

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
public class FileSinkImages extends FileSinkBase {
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
		QVGA(320, 240), CGA(320, 200), VGA(640, 480), NTSC(720, 480), PAL(768,
				576), WVGA_5by3(800, 480), SVGA(800, 600), WVGA_16by9(854, 480), WSVGA(
				1024, 600), XGA(1024, 768), HD720(1280, 720), WXGA_5by3(1280,
				768), WXGA_8by5(1280, 800), SXGA(1280, 1024), SXGAp(1400, 1050), WSXGAp(
				1680, 1050), UXGA(1600, 1200), HD1080(1920, 1080), WUXGA(1920,
				1200), TwoK(2048, 1080), QXGA(2048, 1536), WQXGA(2560, 1600), QSXGA(
				2560, 2048)

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

		public String toString() {
			return String.format("%dx%d (%s)", width, height, name());
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

		public String toString() {
			return String.format("%dx%d", width, height);
		}
	}

	/**
	 * Output image type.
	 */
	public static enum OutputType {
		PNG(BufferedImage.TYPE_INT_ARGB), JPG(BufferedImage.TYPE_INT_RGB), png(
				BufferedImage.TYPE_INT_ARGB), jpg(BufferedImage.TYPE_INT_RGB)

		;

		final int imageType;

		OutputType(int imageType) {
			this.imageType = imageType;
		}
	}

	/**
	 * Output policy. Specify when an image is written. This is an important
	 * choice. Best choice is to divide the graph in steps and choose the
	 * *ByStepOutput*. Remember that if your graph has x nodes and
	 * *ByEventOutput* or *ByNodeEventOutput* is chosen, this will produce x
	 * images just for nodes creation.
	 */
	public static enum OutputPolicy {
		ByEventOutput, ByElementEventOutput, ByAttributeEventOutput, ByNodeEventOutput, ByEdgeEventOutput, ByGraphEventOutput, ByStepOutput, ByNodeAddedRemovedOutput, ByEdgeAddedRemovedOutput, ByNodeAttributeOutput, ByEdgeAttributeOutput, ByGraphAttributeOutput
	}

	/**
	 * Layout policy. Specify how layout is computed. It can be computed in its
	 * own thread with a LayoutRunner but if image rendering takes too much
	 * time, node positions will be very different between two images. To have a
	 * better result, we can choose to compute layout when a new image is
	 * rendered. This will smooth the move of nodes in the movie.
	 */
	public static enum LayoutPolicy {
		NoLayout, ComputedInLayoutRunner, ComputedAtNewImage
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

		public AddLogoRenderer(String logoFile, int x, int y)
				throws IOException {
			File f = new File(logoFile);

			if (f.exists())
				this.logo = ImageIO.read(f);
			else
				this.logo = ImageIO.read(ClassLoader
						.getSystemResource(logoFile));

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
		Basic(
				"org.graphstream.ui.swingViewer.basicRenderer.SwingBasicGraphRenderer"), Scala(
				"org.graphstream.ui.j2dviewer.J2DGraphRenderer")

		;

		final String classname;

		RendererType(String cn) {
			this.classname = cn;
		}
	}

	protected Resolution resolution;
	protected OutputType outputType;
	protected GraphRenderer renderer;
	protected String filePrefix;
	protected BufferedImage image;
	protected Graphics2D g2d;
	protected GraphicGraph gg;
	protected int counter;
	protected OutputPolicy outputPolicy;
	protected LinkedList<PostRenderer> postRenderers;
	protected LayoutPolicy layoutPolicy;
	protected LayoutRunner optLayout;
	protected ProxyPipe layoutPipeIn;
	protected Layout layout;

	public FileSinkImages(String prefix, OutputType type,
			Resolution resolution, OutputPolicy outputPolicy) {
		this.resolution = resolution;
		this.outputType = type;
		this.filePrefix = prefix;
		this.counter = 0;
		this.gg = new GraphicGraph(prefix);
		this.outputPolicy = outputPolicy;
		this.postRenderers = new LinkedList<PostRenderer>();
		this.layoutPolicy = LayoutPolicy.NoLayout;
		this.layout = null;
		this.optLayout = null;
		this.layoutPipeIn = null;

		setRenderer(RendererType.Basic);

		initImage();

		this.renderer.open(gg, null);
	}

	/**
	 * Enable high-quality rendering and anti-aliasing.
	 */
	public void setHighQuality() {
		gg.addAttribute("ui.quality");
		gg.addAttribute("ui.antialias");
	}

	/**
	 * Defines style of the graph as a css stylesheet.
	 * 
	 * @param styleSheet
	 *            the style sheet
	 */
	public void setStyleSheet(String styleSheet) {
		gg.addAttribute("ui.stylesheet", styleSheet);
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
		if (resolution == null || resolution.getWidth() != width
				|| resolution.getHeight() != height) {
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
			this.renderer = obj;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			System.err
					.printf("not a renderer \"%s\"%n", rendererType.classname);
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
			case ComputedInLayoutRunner:
				optLayout.release();
				optLayout = null;
				layoutPipeIn.removeAttributeSink(gg);
				layoutPipeIn = null;
				layout = null;
				break;
			case ComputedAtNewImage:
				gg.removeSink(layout);
				layout.removeAttributeSink(gg);
				layout = null;
				break;
			}

			switch (policy) {
			case ComputedInLayoutRunner:
				layout = Layouts.newLayoutAlgorithm();
				optLayout = new LayoutRunner(gg, layout);
				layoutPipeIn = optLayout.newLayoutPipe();
				layoutPipeIn.addAttributeSink(gg);
				break;
			case ComputedAtNewImage:
				layout = Layouts.newLayoutAlgorithm();
				gg.addSink(layout);
				layout.addAttributeSink(gg);
				break;
			}

			layoutPolicy = policy;
		}
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

	protected void initImage() {
		image = new BufferedImage(resolution.getWidth(),
				resolution.getHeight(), outputType.imageType);

		g2d = image.createGraphics();
	}

	/**
	 * Produce a new image.
	 */
	protected synchronized void outputNewImage() {
		switch (layoutPolicy) {
		case ComputedAtNewImage:
			if (layout != null)
				layout.compute();
			break;
		}

		if (resolution.getWidth() != image.getWidth()
				|| resolution.getHeight() != image.getHeight())
			initImage();

		if (gg.getNodeCount() > 0) {
			gg.computeBounds();

			Point3 lo = gg.getMinPos();
			Point3 hi = gg.getMaxPos();

			renderer.setBounds(lo.x, lo.y, lo.z, hi.x, hi.y, hi.z);
			renderer.render(g2d, resolution.getWidth(), resolution.getHeight());
		}

		for (PostRenderer action : postRenderers)
			action.render(g2d);

		image.flush();

		try {
			File out = new File(String.format("%s%06d.png", filePrefix,
					counter++));

			if (!out.getParentFile().exists())
				out.getParentFile().mkdirs();

			ImageIO.write(image, outputType.name(), out);

			printProgress();
		} catch (IOException e) {
			// ?
		}
	}

	protected void printProgress() {
		System.out.printf("\033[s\033[K%d images written\033[u", counter);
	}

	/**
	 * @see org.graphstream.stream.FileSink
	 */
	protected void outputEndOfFile() throws IOException {
	}

	/**
	 * @see org.graphstream.stream.FileSink
	 */
	protected void outputHeader() throws IOException {
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void edgeAttributeAdded(String sourceId, long timeId, String edgeId,
			String attribute, Object value) {
		gg.edgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);

		switch (outputPolicy) {
		case ByEventOutput:
		case ByEdgeEventOutput:
		case ByEdgeAttributeOutput:
		case ByAttributeEventOutput:
			outputNewImage();
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void edgeAttributeChanged(String sourceId, long timeId,
			String edgeId, String attribute, Object oldValue, Object newValue) {
		gg.edgeAttributeChanged(sourceId, timeId, edgeId, attribute, oldValue,
				newValue);

		switch (outputPolicy) {
		case ByEventOutput:
		case ByEdgeEventOutput:
		case ByEdgeAttributeOutput:
		case ByAttributeEventOutput:
			outputNewImage();
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void edgeAttributeRemoved(String sourceId, long timeId,
			String edgeId, String attribute) {
		gg.edgeAttributeRemoved(sourceId, timeId, edgeId, attribute);

		switch (outputPolicy) {
		case ByEventOutput:
		case ByEdgeEventOutput:
		case ByEdgeAttributeOutput:
		case ByAttributeEventOutput:
			outputNewImage();
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void graphAttributeAdded(String sourceId, long timeId,
			String attribute, Object value) {
		gg.graphAttributeAdded(sourceId, timeId, attribute, value);

		switch (outputPolicy) {
		case ByEventOutput:
		case ByGraphEventOutput:
		case ByGraphAttributeOutput:
		case ByAttributeEventOutput:
			outputNewImage();
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void graphAttributeChanged(String sourceId, long timeId,
			String attribute, Object oldValue, Object newValue) {
		gg.graphAttributeChanged(sourceId, timeId, attribute, oldValue,
				newValue);

		switch (outputPolicy) {
		case ByEventOutput:
		case ByGraphEventOutput:
		case ByGraphAttributeOutput:
		case ByAttributeEventOutput:
			outputNewImage();
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void graphAttributeRemoved(String sourceId, long timeId,
			String attribute) {
		gg.graphAttributeRemoved(sourceId, timeId, attribute);

		switch (outputPolicy) {
		case ByEventOutput:
		case ByGraphEventOutput:
		case ByGraphAttributeOutput:
		case ByAttributeEventOutput:
			outputNewImage();
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void nodeAttributeAdded(String sourceId, long timeId, String nodeId,
			String attribute, Object value) {
		gg.nodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);

		switch (outputPolicy) {
		case ByEventOutput:
		case ByNodeEventOutput:
		case ByNodeAttributeOutput:
		case ByAttributeEventOutput:
			outputNewImage();
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void nodeAttributeChanged(String sourceId, long timeId,
			String nodeId, String attribute, Object oldValue, Object newValue) {
		gg.nodeAttributeChanged(sourceId, timeId, nodeId, attribute, oldValue,
				newValue);

		switch (outputPolicy) {
		case ByEventOutput:
		case ByNodeEventOutput:
		case ByNodeAttributeOutput:
		case ByAttributeEventOutput:
			outputNewImage();
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void nodeAttributeRemoved(String sourceId, long timeId,
			String nodeId, String attribute) {
		gg.nodeAttributeRemoved(sourceId, timeId, nodeId, attribute);

		switch (outputPolicy) {
		case ByEventOutput:
		case ByNodeEventOutput:
		case ByNodeAttributeOutput:
		case ByAttributeEventOutput:
			outputNewImage();
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void edgeAdded(String sourceId, long timeId, String edgeId,
			String fromNodeId, String toNodeId, boolean directed) {
		gg.edgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId, directed);

		switch (outputPolicy) {
		case ByEventOutput:
		case ByEdgeEventOutput:
		case ByEdgeAddedRemovedOutput:
		case ByElementEventOutput:
			outputNewImage();
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void edgeRemoved(String sourceId, long timeId, String edgeId) {
		gg.edgeRemoved(sourceId, timeId, edgeId);

		switch (outputPolicy) {
		case ByEventOutput:
		case ByEdgeEventOutput:
		case ByEdgeAddedRemovedOutput:
		case ByElementEventOutput:
			outputNewImage();
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void graphCleared(String sourceId, long timeId) {
		gg.graphCleared(sourceId, timeId);

		switch (outputPolicy) {
		case ByEventOutput:
		case ByGraphEventOutput:
		case ByNodeAddedRemovedOutput:
		case ByEdgeAddedRemovedOutput:
		case ByElementEventOutput:
			outputNewImage();
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void nodeAdded(String sourceId, long timeId, String nodeId) {
		gg.nodeAdded(sourceId, timeId, nodeId);

		switch (outputPolicy) {
		case ByEventOutput:
		case ByNodeEventOutput:
		case ByNodeAddedRemovedOutput:
		case ByElementEventOutput:
			outputNewImage();
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void nodeRemoved(String sourceId, long timeId, String nodeId) {
		gg.nodeRemoved(sourceId, timeId, nodeId);

		switch (outputPolicy) {
		case ByEventOutput:
		case ByNodeEventOutput:
		case ByNodeAddedRemovedOutput:
		case ByElementEventOutput:
			outputNewImage();
			break;
		}
	}

	/**
	 * @see org.graphstream.stream.Sink
	 */
	public void stepBegins(String sourceId, long timeId, double step) {
		gg.stepBegins(sourceId, timeId, step);

		switch (outputPolicy) {
		case ByEventOutput:
		case ByStepOutput:
			outputNewImage();
			break;
		}
	}

	public static void main(String... args) throws IOException {
		if (args == null || args.length < 5) {
			System.out
					.printf("usage: java %s fichier.dgs outputPrefix imageType res policy%n",
							FileSinkImages.class.getName());
			System.exit(0);
		}

		FileSourceDGS dgs = new FileSourceDGS();
		FileSinkImages fsi = new FileSinkImages(args[1],
				OutputType.valueOf(args[2]), Resolutions.valueOf(args[3]),
				OutputPolicy.valueOf(args[4]));

		dgs.addSink(fsi);

		if (args.length > 5)
			fsi.addLogo(args[5], 0, 0);

		fsi.setHighQuality();
		fsi.setStyleSheet("graph { padding: 50px; fill-color: black; }"
				+ "node { stroke-mode: plain; stroke-color: #3d5689,#639330,#8d4180,#97872f,#9c4432; stroke-width: 2px; fill-mode: dyn-plain; fill-color: #5782db,#90dd3e,#e069cb,#e0ce69,#e07c69; }"
				+ "edge { fill-color: white; }");

		boolean next = true;

		dgs.begin(args[0]);

		while (next)
			next = dgs.nextStep();

		dgs.end();
	}
}
