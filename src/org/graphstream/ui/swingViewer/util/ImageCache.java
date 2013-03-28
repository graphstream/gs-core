/*
 * Copyright 2006 - 2013
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
package org.graphstream.ui.swingViewer.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * A simple cache for images to avoid reloading them constantly and to allow
 * sharing.
 * 
 * TODO have a policy to release images if they have not been used for a given
 * time.
 */
public class ImageCache {
	/**
	 * The image cache.
	 */
	protected HashMap<String, Image> imageCache = new HashMap<String, Image>();

	/**
	 * The dummy image used to mark a not found image (and avoid trying to
	 * reload it again and again).
	 */
	protected Image dummy;

	/**
	 * The default singleton image cache instance.
	 */
	protected static ImageCache defaultImageCache;

	/**
	 * New empty image cache.
	 */
	public ImageCache() {
		BufferedImage img = new BufferedImage(16, 16,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();

		g2.setColor(Color.RED);
		g2.drawRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
		g2.drawLine(0, 0, img.getWidth() - 1, img.getHeight() - 1);
		g2.drawLine(0, img.getHeight() - 1, img.getWidth() - 1, 0);

		dummy = img;
	}

	/**
	 * Default singleton image cache instance that can be shared. This method
	 * and singleton must be used only in the Swing thread.
	 * 
	 * @return The default singleton image cache instance.
	 */
	public static ImageCache defaultImageCache() {
		if (defaultImageCache == null)
			defaultImageCache = new ImageCache();

		return defaultImageCache;
	}

	/**
	 * Lookup an image based on its name, if found return it, else try to load
	 * it. If an image is not found once, the cache remembers it and will not
	 * try to reload it again if the same image is requested anew. Therefore
	 * using getImage() is fast and smooth.
	 * 
	 * @param fileNameOrUrl
	 *            A file name or an URL pointing at the image.
	 * @return An image or null if the image cannot be found.
	 */
	public Image getImage(String fileNameOrUrl) {
		return getImage(fileNameOrUrl, false);
	}

	/**
	 * The same as {@link #getImage(String)} but you can force the cache to try
	 * to reload an image that where not found before.
	 * 
	 * @param fileNameOrUrl
	 *            A file name or an URL pointing at the image.
	 * @param forceTryReload
	 *            If true, try to reload an image that where not found before.
	 * @return An image or null if the image cannot be found.
	 */
	public Image getImage(String fileNameOrUrl, boolean forceTryReload) {
		Image ii = imageCache.get(fileNameOrUrl);

		if (ii == dummy && !forceTryReload)
			return null;

		if (ii == null) {
			URL url = ImageCache.class.getClassLoader().getResource(
					fileNameOrUrl);

			if (url != null) {
				try {
					ii = ImageIO.read(url);
					imageCache.put(fileNameOrUrl, ii);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					url = new URL(fileNameOrUrl);

					ii = ImageIO.read(url);
					imageCache.put(fileNameOrUrl, ii);
				} catch (Exception e) {
					try {
						ii = ImageIO.read(new File(fileNameOrUrl));
						imageCache.put(fileNameOrUrl, ii);
					} catch (IOException ee) {
						imageCache.put(fileNameOrUrl, dummy);
						// ee.printStackTrace();
						System.err.printf("Cannot read image '%s'%n",
								fileNameOrUrl);
					}
				}
			}
		}

		return ii;
	}

	/**
	 * A dummy 16x16 image.
	 * 
	 * @return An image.
	 */
	public Image getDummyImage() {
		return dummy;
	}
}