package org.graphstream.ui.scalaViewer.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.StyleGroup;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.scalaViewer.Backend;
import org.graphstream.ui.scalaViewer.ScalaDefaultCamera;
import org.graphstream.ui.scalaViewer.util.ImageCache;
import org.graphstream.ui.swingViewer.util.GradientFactory;
import org.graphstream.ui.swingViewer.util.GraphMetrics;

/**
 * Renderer for the graph background.
 * 
 * This class is not a StyleRenderer because the graph is not a GraphicElement.
 * 
 * TODO XXX make this class an abstract one, and create several distinct back-ends.
 */
public class GraphBackgroundRenderer implements GraphicElement.SwingElementRenderer
{
	private GraphicGraph graph ;
	private StyleGroup style ;
	
	public GraphBackgroundRenderer(GraphicGraph graph, StyleGroup style) {
		this.graph = graph ;
		this.style = style ;
	}
	
	/**
     * Render a background indicating there is nothing to draw. 
	 */
	public void displayNothingToDo(Backend bck, int w, int h) {
		String msg1 = "Graph width/height/depth is zero !!";
		String msg2 = "Place components using the 'xyz' attribute." ;
		Graphics2D g = bck.graphics2D() ;
		
		g.setColor( Color.WHITE );
		g.fillRect( 0, 0, w, h );
		g.setColor( Color.RED );
		g.drawLine( 0, 0, w, h );
		g.drawLine( 0, h, w, 0 );
				
		int msg1length = g.getFontMetrics().stringWidth( msg1 );
		int msg2length = g.getFontMetrics().stringWidth( msg2 );
		int x = w / 2;
		int y = h / 2;
		
		g.setColor( Color.BLACK );
		g.drawString( msg1, x - msg1length/2, y-20 );
		g.drawString( msg2, x - msg2length/2, y+20 );
	}
	
	
	public void render(Backend bck,ScalaDefaultCamera camera, int w, int h) {
		if ( (camera.graphViewport() == null) && camera.getMetrics().diagonal == 0
				&& (graph.getNodeCount() == 0 && graph.getSpriteCount() == 0)) {
			displayNothingToDo(bck, w, h);
		}
		else {
			renderGraphBackground(bck, camera);
			strokeGraph(bck, camera);
		}
	}

	private void renderGraphBackground(Backend bck, ScalaDefaultCamera camera) {
		Graphics2D g = bck.graphics2D() ;
		switch (graph.getStyle().getFillMode()) {
		case NONE:
			break;
		case IMAGE_TILED: fillImageTiled(g, camera);
			break;
		case IMAGE_SCALED: fillImageScaled(g, camera, 0);
			break;
		case IMAGE_SCALED_RATIO_MAX: fillImageScaled(g, camera, 1);
			break;
		case IMAGE_SCALED_RATIO_MIN: fillImageScaled(g, camera, 2);
			break;
		case GRADIENT_DIAGONAL1: fillGradient(g, camera);
			break;
		case GRADIENT_DIAGONAL2: fillGradient(g, camera);
			break;
		case GRADIENT_HORIZONTAL: fillGradient(g, camera);
			break;
		case GRADIENT_VERTICAL: fillGradient(g, camera);
			break;
		case GRADIENT_RADIAL: fillGradient(g, camera);
			break;
		case DYN_PLAIN: fillBackground(g, camera);
			break;
		default: fillBackground(g, camera);
			break;
		}
	}

	private void fillBackground(Graphics2D g, ScalaDefaultCamera camera) {
		GraphMetrics metrics = camera.getMetrics();
		
		g.setColor(style.getFillColor(0));
		g.fillRect(0, 0, (int)metrics.viewport[2], (int)metrics.viewport[3]);
	}
	
	private void fillCanvasBackground(Graphics2D g, ScalaDefaultCamera camera) {
		GraphMetrics metrics = camera.getMetrics();

		g.setColor( style.getCanvasColor( 0 ) );
		g.fillRect( 0, 0, (int) metrics.viewport[2], (int) metrics.viewport[3]);
	}
	

	private void fillImageTiled(Graphics2D g, ScalaDefaultCamera camera) {
		GraphMetrics metrics = camera.getMetrics();
		double px2gu = metrics.ratioPx2Gu;
		BufferedImage img = null ;
		
		img = ImageCache.loadImage(style.getFillImage());
		if ( img == null ) {
			img = ImageCache.dummyImage();
		}
		
		double gw    = ( metrics.graphWidthGU()  * px2gu ) ;// + ( padx * 2 )	// consider the padding ???
		double gh    = ( metrics.graphHeightGU() * px2gu ) ;// + ( pady * 2 )	// probably not.
		double x     = ( metrics.viewport[2] / 2 ) - ( gw / 2 ) ;
		double y     = metrics.viewport[3] - ( metrics.viewport[3] / 2 ) - ( gh / 2 ) ;
		TexturePaint paint = new TexturePaint( img, new Rectangle2D.Double( x, y, img.getWidth(), img.getHeight()) ) ;
		Rectangle2D rect  = new Rectangle2D.Double( 0, 0, metrics.viewport[2], metrics.viewport[3] );
		
		g.setPaint( paint );
		g.fill( rect );
		g.setPaint( null );
	}
	

	private void fillImageScaled(Graphics2D g, ScalaDefaultCamera camera, int mode) {
		GraphMetrics metrics = camera.getMetrics();
		double px2gu = metrics.ratioPx2Gu;
		BufferedImage img = null ;
				
		img = ImageCache.loadImage(style.getFillImage());
		if ( img == null ) {
			img = ImageCache.dummyImage();
		}
				
		fillCanvasBackground( g, camera );
		double gw    = ( metrics.graphWidthGU()  * px2gu ) ;
		double gh    = ( metrics.graphHeightGU() * px2gu ) ;
		double x     = ( metrics.viewport[2] / 2 ) - ( gw / 2 ) ;
		double y     = metrics.viewport[3] - ( metrics.viewport[3] / 2 ) - ( gh / 2 ) ;
		
		if (mode == 0) { // Ratio
			g.drawImage(img, (int)x, (int)y, (int)(x+gw), (int)(y+gh), 0, 0, img.getWidth(), img.getHeight(), null);
		}
		else if (mode == 1) { // Ratio-max
			double ratioi = (double)img.getWidth() / (double)img.getHeight();
			double ratiog = gw / gh;
			
			if(ratioi > ratiog) {
				double newgw = gh * ratioi;
				double newx  = x - ((newgw-gw)/2);
				g.drawImage( img, (int)newx, (int)y, (int)(newx+newgw), (int)(y+gh), 0, 0, img.getWidth(), img.getHeight(), null );
			}
			else {
				double newgh = gw / ratioi;
				double newy  = y - ((newgh-gh)/2);
				g.drawImage(img, (int)x, (int)newy, (int)(x+gw), (int)(newy+newgh),	0, 0, img.getWidth(), img.getHeight(), null);
			}
		}
		else if (mode == 2) { // Ratio-min
			double ratioi = (double) img.getWidth() / (double) img.getHeight();
			double ratiog = gw / gh;
					
			if( ratiog > ratioi ) {
				double newgw = gh * ratioi;
				double newx  = x + ((gw-newgw)/2);
				g.drawImage( img, (int)newx, (int)y, (int)(newx+newgw), (int)(y+gh), 0, 0, img.getWidth(), img.getHeight(), null );
			}
			else {
				double newgh = gw / ratioi;
				double newy  = y + ((gh-newgh)/2);
				g.drawImage(img, (int)x, (int)newy, (int)(x+gw), (int)(newy+newgh),	0, 0, img.getWidth(), img.getHeight(), null);
			}
		}
		else {
			throw new RuntimeException("Error graphBackground");
		}
	}

	private void strokeGraph(Backend bck, ScalaDefaultCamera camera) {
		GraphMetrics metrics = camera.getMetrics();
		Graphics2D g = bck.graphics2D() ;
		
		if( style.getStrokeMode() != StyleConstants.StrokeMode.NONE && style.getStrokeWidth().value > 0 ) {
			g.setColor( style.getStrokeColor( 0 ) );
			g.setStroke( new BasicStroke( (float)metrics.lengthToGu( style.getStrokeWidth() ) ) );
			int padx = (int)metrics.lengthToPx( style.getPadding(), 0 ) ;
			int pady = padx ;
			if( style.getPadding().size() > 1 ) 
				pady = (int)metrics.lengthToPx( style.getPadding(), 1 );
				
			g.drawRect( padx, pady, (int)metrics.viewport[2] - padx*2, (int)metrics.viewport[3] - pady*2 );
		}
	}
	
	protected void fillGradient(Graphics2D g, ScalaDefaultCamera camera) {
		GraphMetrics metrics = camera.getMetrics();

		if( style.getFillColors().size() < 2 ) {
			fillBackground( g, camera );
		}
		else {
			int w = (int)metrics.viewport[2] ; 
			int h = (int)metrics.viewport[3] ;
			
			g.setPaint( GradientFactory.gradientInArea( 0, 0, w, h, style ) );
			g.fillRect( 0, 0, w, h );
			g.setPaint( null );
		}
	}
	
}

