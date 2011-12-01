/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
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
package org.graphstream.ui.layout;

import org.graphstream.graph.Graph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.Source;
import org.graphstream.stream.thread.ThreadProxyPipe;

/**
 * Allows to run a layout in a distinct thread.
 */
public class LayoutRunner extends Thread {
	/**
	 * The layout algorithm.
	 */
	protected Layout layout = null;

	/**
	 * The proxy on the source of graph events.
	 */
	protected ThreadProxyPipe pumpPipe = null;

	/**
	 * The meaning of life.
	 */
	protected boolean loop = true;

	/**
	 * New layout runner that listen at the given source and compute a layout on
	 * its graph structure in a distinct thread.
	 * 
	 * @param source
	 *            The source of graph events.
	 * @param layout
	 *            The layout algorithm to use.
	 */
	public LayoutRunner(Source source, Layout layout) {
		this(source, layout, true);
	}

	/**
	 * New layout runner that listen at the given source and compute a layout on
	 * its graph structure in a distinct thread.
	 * 
	 * @param source
	 *            The source of graph events.
	 * @param layout
	 *            The layout algorithm to use.
	 * @param start
	 *            Start the layout thread immediately ? Else the start() method
	 *            must be called later.
	 */
	public LayoutRunner(Source source, Layout layout, boolean start) {
		this.layout = layout;
		this.pumpPipe = new ThreadProxyPipe(source);
		this.pumpPipe.addSink(layout);

		if (start)
			start();
	}

	public LayoutRunner(Graph graph, Layout layout, boolean start,
			boolean replay) {
		this.layout = layout;
		this.pumpPipe = new ThreadProxyPipe(graph, true);
		this.pumpPipe.addSink(layout);

		if (start)
			start();
	}

	/**
	 * Pipe out whose input is connected to the layout algorithm. You can safely
	 * connect as a sink to it to receive events of the layout from a distinct
	 * thread.
	 */
	public ProxyPipe newLayoutPipe() {
		return new ThreadProxyPipe(layout);
	}
	
	@Override
	public void run() {
		String layoutName = layout.getLayoutAlgorithmName();

		while (loop) {
			double limit = layout.getStabilizationLimit();
			
			pumpPipe.pump();
			if(limit > 0) {
				if(layout.getStabilization()>limit) {
					nap(80);
//System.err.printf("Stable layout (%f  >  %f)%n", layout.getStabilization(), limit);
				} else {
//System.err.printf("** layout (%f  <  %f)%n", layout.getStabilization(), limit);
					layout.compute();
					nap(10);
				}
			} else {
				layout.compute();
				nap(10);
			}
		}

		System.out.printf("Layout '%s' process stopped.%n", layoutName);
		System.out.flush();
	}

	public void release() {
		pumpPipe.unregisterFromSource();
		pumpPipe.removeSink(layout);
		pumpPipe = null;
		layout = null;
		loop = false;
	}

	protected void nap(long ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
		}
	}
}
