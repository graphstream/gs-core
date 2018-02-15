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
 * @since 2011-07-23
 * 
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Stefan Balev <stefan.balev@graphstream-project.org>
 * @author Alex Bowen <bowen.a@gmail.com>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 * @author Yoann Pigné <yoann.pigne@graphstream-project.org>
 */
package org.graphstream.stream.file.pajek.test;

import java.io.IOException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSourcePajek;
import org.graphstream.ui.view.Viewer;
import org.junit.Ignore;

@Ignore
public class TestPajekParser {
	static class TestEntry {
		String ressourceName;
		boolean autoLayout;
		boolean addLabels;
		boolean veryLarge;

		TestEntry(String name, boolean layout, boolean addLabels, boolean veryLarge) {
			this.ressourceName = name;
			this.autoLayout = layout;
			this.addLabels = addLabels;
			this.veryLarge = veryLarge;
		}
	}

	static TestEntry[] entries = { new TestEntry("1.NET", false, false, false),
			new TestEntry("1CRN.NET", false, false, false), new TestEntry("AHO1.NET", false, false, false),
			new TestEntry("AHO2.NET", true, false, false), new TestEntry("AHO3.NET", true, false, false),
			new TestEntry("AHO4.NET", true, false, false), new TestEntry("B.NET", false, false, false),
			new TestEntry("C.NET", false, false, false), new TestEntry("CC.NET", false, false, false),
			new TestEntry("CENPROD.NET", false, false, false), new TestEntry("CIRC.NET", false, false, false),
			new TestEntry("CITE.NET", true, false, false), new TestEntry("CP.NET", false, false, false),
			new TestEntry("CPM1.NET", false, false, false), new TestEntry("CPM2.NET", false, false, false),
			new TestEntry("CPM3.NET", true, false, false), new TestEntry("CVRML.NET", false, false, false),
			new TestEntry("CX.NET", false, false, false), new TestEntry("D.NET", false, false, false),
			new TestEntry("DNA.NET", false, false, false), new TestEntry("DREV1.NET", false, false, false),
			new TestEntry("DREVO.NET", false, false, false), new TestEntry("ETHANOL.NET", false, false, false),
			new TestEntry("FILE1.NET", true, false, false), new TestEntry("FLOW.NET", false, false, false),
			new TestEntry("FLOW3.NET", false, false, false), new TestEntry("FLOW4.NET", false, false, false),
			new TestEntry("FRAG1.NET", false, false, false), new TestEntry("FRAG1Y.NET", false, false, false),
			new TestEntry("FRAG2.NET", false, false, false), new TestEntry("FRAG3.NET", false, false, false),
			new TestEntry("FRAG4.NET", false, false, false), new TestEntry("FRAG5.NET", false, false, false),
			new TestEntry("GCD.NET", false, false, false), new TestEntry("GR3_44.NET", false, false, false),
			new TestEntry("GR3_53.NET", false, false, false), new TestEntry("GR3_60.NET", false, false, false),
			new TestEntry("GR3_81.NET", false, false, false), new TestEntry("GR344.NET", false, false, false),
			new TestEntry("H20.NET", false, false, false), new TestEntry("HEXANE.NET", false, false, false),
			new TestEntry("KOCKA.NET", true, false, false), new TestEntry("KOCKA1.NET", false, false, false),
			new TestEntry("KVADRAT.NET", false, false, false), new TestEntry("LINKS.NET", false, false, false),
			new TestEntry("LOND1.NET", false, false, false), new TestEntry("LONDON.NET", false, false, false),
			new TestEntry("MCCABE1.NET", false, false, false), new TestEntry("MCCABE1A.NET", false, false, false),
			new TestEntry("MCCABE2.NET", false, false, false), new TestEntry("MCCABE2A.NET", false, false, false),
			new TestEntry("MREZA.NET", false, false, false), new TestEntry("MREZA1.NET", false, false, false),
			new TestEntry("MREZA2.NET", false, false, false), new TestEntry("MREZA3.NET", false, false, false),
			new TestEntry("MREZAS1.NET", true, false, false), new TestEntry("MREZASHR.NET", false, false, false),
			new TestEntry("NEG.NET", false, false, false), new TestEntry("NEIG4.NET", false, false, false),
			new TestEntry("NOOY.NET", false, false, false), new TestEntry("OLDFILE.NET", false, false, false),
			new TestEntry("PATH4.NET", false, false, false), new TestEntry("PETER.NET", false, false, false),
			new TestEntry("PETRI1.NET", false, false, false), new TestEntry("PETRI2.NET", false, false, false),
			new TestEntry("PETRI3.NET", false, false, false), new TestEntry("PETRI4.NET", false, false, false),
			new TestEntry("PETRI5.NET", false, false, false), new TestEntry("PETRI5X.NET", false, false, false),
			new TestEntry("PETRI5Y.NET", false, false, false), new TestEntry("PETRI51.NET", false, false, false),
			new TestEntry("PETRI51X.NET", false, false, false), new TestEntry("PETRI51Y.NET", false, false, false),
			new TestEntry("PETRI52.NET", false, false, false), new TestEntry("PETRI52X.NET", false, false, false),
			new TestEntry("PETRI52Y.NET", false, false, false), new TestEntry("PETRI53.NET", false, false, false),
			new TestEntry("PITT.NET", false, false, false), new TestEntry("PRIME.NET", false, false, false),
			new TestEntry("PRIME1.NET", false, false, false), new TestEntry("PROT.NET", false, false, false),
			new TestEntry("PROTI.NET", true, false, false), new TestEntry("SAMPLE6.NET", false, false, false),
			new TestEntry("SAMPLE9.NET", false, false, false), new TestEntry("SAMPLE10.NET", false, false, false),
			new TestEntry("SHORT.NET", false, false, false), new TestEntry("SHR.NET", false, false, false),
			new TestEntry("SHRINK.NET", false, false, false), new TestEntry("SHRINK4.NET", false, false, false),
			new TestEntry("SHRINK2.NET", false, false, false), new TestEntry("SLOVEN.NET", false, false, false),
			new TestEntry("STROPIC.NET", false, false, false), new TestEntry("T.NET", false, false, false),
			new TestEntry("TEST1.NET", false, false, false), new TestEntry("TINA.NET", false, false, false),
			new TestEntry("TINAMATR.NET", false, false, false), new TestEntry("TRANS.NET", true, false, false),
			new TestEntry("WIRTH.NET", false, false, false), new TestEntry("WRITE.NET", false, false, false) };

	public static void main(String args[]) throws IOException {
		System.setProperty("org.graphstream.ui", "org.graphstream.ui.swingViewer.util.SwingDisplay");
		TestPajekParser test = new TestPajekParser();

		for (int i = 0; i < entries.length; i++)
			test.test(entries[i]);
	}

	public void test(TestEntry entry) throws IOException {
		System.out.printf("> \"%s\"\n", entry.ressourceName);

		Graph graph = new MultiGraph("foo");
		FileSourcePajek in = new FileSourcePajek();

		graph.setAttribute("ui.quality");
		if (!entry.veryLarge)
			graph.setAttribute("ui.antialias");
		if (entry.addLabels)
			graph.setAttribute("ui.stylesheet", String.format(
					"node { text-alignment: center; size: %dpx; fill-color: grey; %s } edge { fill-color: #333; }",
					entry.veryLarge ? 6 : 16, entry.veryLarge ? "" : "stroke-mode: plain; stroke-color: #333;"));
		else
			graph.setAttribute("ui.stylesheet", String.format(
					"node { text-alignment: at-right; size: %dpx; fill-color: grey; %s text-background-mode: plain; text-offset: 2px, 0px; text-padding: 2px; text-background-color: #FFFFFFAA; } edge { fill-color: #333; }",
					entry.veryLarge ? 6 : 16, entry.veryLarge ? "" : "stroke-mode: plain; stroke-color: #333;"));

		Viewer v = graph.display(entry.autoLayout);
		v.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);

		in.addSink(graph);
		in.readAll(TestPajekParser.class.getResourceAsStream(entry.ressourceName));

		if (entry.addLabels) {
			for (Node node : graph) {
				node.setAttribute("ui.label", node.getId());
			}
		}

		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}

		v.close();
	}
}