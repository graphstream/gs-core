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

package org.graphstream.stream.file.pajek;

import java.io.IOException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

public class TestPajekParser {
	public static void main(String args[]) throws IOException {
		System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		
		@SuppressWarnings("unused")
		TestPajekParser test = new TestPajekParser();
		
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/1.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/1CRN.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/AHO1.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/AHO2.NET", true, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/AHO3.NET", true, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/AHO4.NET", true, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/B.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/C.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/CC.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/CENPROD.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/CIRC.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/CITE.NET", true, false, false);		// What is the edge format ?
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/CP.NET", false, false, false);		// Identifiers with spaces without quotes.
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/CPM1.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/CPM2.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/CPM3.NET", true, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/CVRML.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/CX.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/D.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/DNA.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/DREV1.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/DREVO.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/ETHANOL.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/FILE1.NET", true, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/FLOW.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/FLOW3.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/FLOW4.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/FRAG1.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/FRAG1Y.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/FRAG2.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/FRAG3.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/FRAG4.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/FRAG5.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/GCD.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/GR3_44.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/GR3_53.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/GR3_60.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/GR3_81.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/GR344.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/H20.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/HEXANE.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/KOCKA.NET", true, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/KOCKA1.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/KVADRAT.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/LINKS.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/LOND1.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/LONDON.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/MCCABE1.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/MCCABE1A.NET", false, false, false);// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/MCCABE2.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/MCCABE2A.NET", false, false, false);// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/MREZA.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/MREZA1.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/MREZA2.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/MREZA3.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/MREZAS1.NET", true, false, false);		// All	is empty !!
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/MREZASHR.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/NEG.NET", false, false, false);			// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/NEIG4.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/NOOY.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/OLDFILE.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PATH4.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PETER.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PETRI1.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PETRI2.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PETRI3.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PETRI4.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PETRI5.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PETRI5X.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PETRI5Y.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PETRI51.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PETRI51X.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PETRI51Y.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PETRI52.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PETRI52X.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PETRI52Y.NET", false, false, false);	// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PETRI53.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PITT.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PRIME.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PRIME1.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PROT.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/PROTI.NET", true, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/SAMPLE6.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/SAMPLE9.NET", false, false, false);		// Unknown format
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/SAMPLE10.NET", false, false, false);	// Unknown format
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/SHORT.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/SHR.NET", false, false, false);			// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/SHRINK.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/SHRINK4.NET", false, false, false);		// Don't know the meaning of "--"
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/SHRINK2.NET", false, false, false);		// "--"
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/SLOVEN.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/STROPIC.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/T.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/TEST1.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/TINA.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/TINAMATR.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/TRANS.NET", true, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/WIRTH.NET", false, false, false);		// All
//		test.test("/home/antoine/Documents/Recherche/Media/Graphs/Pajek/WRITE.NET", false, false, false);		// All

		//test.test("/home/antoine/Bureau/USpowerGrid.net", true, false, true);
		//test.test("/home/antoine/Bureau/California.net", true, false, true);
		//test.test("/home/antoine/Bureau/dolphins.net", true, false, true);
	}
	
	public void test(String file, boolean autoLayout, boolean addLabels, boolean veryLarge) throws IOException {
		Graph graph = new MultiGraph("foo");
		FileSourcePajek in = new FileSourcePajek();
		
		graph.addAttribute("ui.quality");
		if(!veryLarge) graph.addAttribute("ui.antialias");
		if(addLabels)
		     graph.addAttribute("ui.stylesheet", String.format("node { text-alignment: center; size: %dpx; fill-color: grey; %s } edge { fill-color: #333; }", veryLarge? 6 : 16, veryLarge? "":"stroke-mode: plain; stroke-color: #333;"));
		else graph.addAttribute("ui.stylesheet", String.format("node { text-alignment: at-right; size: %dpx; fill-color: grey; %s text-background-mode: plain; text-offset: 2px, 0px; text-padding: 2px; text-background-color: #FFFFFFAA; } edge { fill-color: #333; }", veryLarge ? 6:16, veryLarge? "":"stroke-mode: plain; stroke-color: #333;"));
		graph.display(autoLayout);
		in.addSink(graph);
		in.readAll(file);
		
		if(addLabels) {
			for(Node node: graph) {
				node.addAttribute("ui.label", node.getId());
			}
		}
	}
}