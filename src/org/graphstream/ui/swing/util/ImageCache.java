package org.graphstream.ui.swing.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class ImageCache {

	protected static HashMap<String, BufferedImage> imageCache = new HashMap<>();
	
	protected static BufferedImage dummy = null ;
		
	public void init() {
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g2 = img.createGraphics();
		g2.setColor(Color.RED);
		g2.drawRect(0, 0, img.getWidth()-1, img.getHeight()-1);
		g2.drawLine(0, 0, img.getWidth()-1, img.getHeight()-1);
		g2.drawLine(0, img.getHeight()-1, img.getWidth()-1, 0);
		
		dummy = img ;
	}
	
	
	public static BufferedImage loadImage(String fileNameOrUrl) {
		return loadImage(fileNameOrUrl, false);
	}


	public static BufferedImage loadImage(String fileNameOrUrl, boolean forceTryReload) {
		if (imageCache.get(fileNameOrUrl) == null) {
			URL url = ImageCache.class.getClassLoader().getResource(fileNameOrUrl);
			BufferedImage image = null ;
			
			if (url != null) { // The image is in the class path.
				try {
					image = ImageIO.read(url);
					imageCache.put(fileNameOrUrl, image);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					url = new URL(fileNameOrUrl);
					
					image = ImageIO.read(url);
					imageCache.put(fileNameOrUrl, image);
				}
				catch (Exception e) {
					try {
						image = ImageIO.read( new File( fileNameOrUrl ) );	// Try the file.
						imageCache.put( fileNameOrUrl, image );
					}
					catch (Exception ex) {
						image = dummy ;
						imageCache.put( fileNameOrUrl, image );
						Logger.getLogger(ImageCache.class.getSimpleName()).log(Level.WARNING, "Cannot read image '%s'.".format(fileNameOrUrl), e);
					}
				}
			}
			
			return image ;
		}
		else {
			if(imageCache.get(fileNameOrUrl) == dummy && forceTryReload) {
				imageCache.remove(fileNameOrUrl) ;
				return loadImage(fileNameOrUrl);
			}
			else
				return imageCache.get(fileNameOrUrl);
		}
	}
	
	public static BufferedImage dummyImage() {
		return dummy ;
	}
}
