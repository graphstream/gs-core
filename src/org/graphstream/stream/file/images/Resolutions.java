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
package org.graphstream.stream.file.images;

/**
 * Common resolutions.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Graphics_display_resolution">Graphics display resolution page on Wikipedia</a>
 */
public enum Resolutions implements Resolution {
	QVGA(320, 240), CGA(320, 200), VGA(640, 480), NTSC(720, 480), PAL(768, 576), WVGA_5by3(800, 480), SVGA(800,
			600), WVGA_16by9(854, 480), WSVGA(1024, 600), XGA(1024, 768), HD720(1280, 720), WXGA_5by3(1280,
			768), WXGA_8by5(1280, 800), SXGA(1280, 1024), FWXGA(1366, 768), SXGAp(1400, 1050), WSXGAp(1680, 1050), UXGA(
			1600, 1200), HD1080(1920, 1080), WUXGA(1920, 1200), TwoK(2048, 1080), QXGA(2048, 1536), WQXGA(2560,
			1600), QSXGA(2560, 2048), UHD_4K(3840, 2160), UHD_8K_16by9(7680, 4320), UHD_8K_17by8(8192,
			4320), UHD_8K_1by1(8192, 8192);

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

	@Override public String toString() {
		return String.format("%s (%dx%d)", name(), width, height);
	}
}
