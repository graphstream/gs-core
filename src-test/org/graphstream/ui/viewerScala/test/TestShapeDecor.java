package org.graphstream.ui.viewerScala.test;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.scalaViewer.ScalaGraphRenderer;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.swingViewer.SwingViewer;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class TestShapeDecor implements ViewerListener {
	public static void main(String[] args) {
		(new TestShapeDecor()).run();
	}
	public static final String URL_IMAGE = "file:///home/hicham/Bureau/b.png";
	
	boolean loop = true ;
	private void run() {
		MultiGraph graph = new MultiGraph("Shape Decor");
		
		Viewer viewer = new SwingViewer( graph, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD );
		ViewerPipe pipeIn = viewer.newViewerPipe();
		DefaultView view   = (DefaultView)viewer.addView( "view1", new ScalaGraphRenderer() );
		
		view.resizeFrame(500, 430);

		pipeIn.addAttributeSink( graph );
		pipeIn.addViewerListener( this );
		pipeIn.pump();

		graph.setAttribute( "ui.stylesheet", styleSheet );
		graph.setAttribute( "ui.antialias" );
		graph.setAttribute( "ui.quality" );
		
		Node A = graph.addNode( "A" );
		Node B = graph.addNode( "B" );
		Node C = graph.addNode( "C" );
		Node D = graph.addNode( "D" );
		Node E = graph.addNode( "E" );
		Node F = graph.addNode( "F" );
		Node G = graph.addNode( "G" );
		
		Node U = graph.addNode( "U" );
		Node V = graph.addNode( "V" );
		Node W = graph.addNode( "W" );
		Node X = graph.addNode( "X" );
		Node Y = graph.addNode( "Y" );
		Node Z = graph.addNode( "Z" );
		Node T = graph.addNode( "T" );
		
		graph.addNode("a");
		graph.addNode("b");
		graph.addNode("c");
		graph.addNode("d");
		graph.addNode("e");
		graph.addNode("f");
		graph.addNode("g");

		graph.addNode("u");
		graph.addNode("v");
		graph.addNode("w");
		graph.addNode("x");
		graph.addNode("y");
		graph.addNode("z");
		graph.addNode("t");

		graph.addNode("i");
		graph.addNode("j");
		
		Edge au = graph.addEdge( "au", "a", "u" );
		Edge bv = graph.addEdge( "bv", "b", "v" );
		Edge cw = graph.addEdge( "cw", "c", "w" );
		Edge dx = graph.addEdge( "dx", "d", "x" );
		Edge ey = graph.addEdge( "ey", "e", "y" );
		Edge fz = graph.addEdge( "fz", "f", "z" );
		Edge gt = graph.addEdge( "gt", "g", "t" );
		Edge ij = graph.addEdge( "ij", "i", "j" );

		Edge AU = graph.addEdge( "AU", "A", "U" );
		Edge BV = graph.addEdge( "BV", "B", "V" );
		Edge CW = graph.addEdge( "CW", "C", "W" );
		Edge DX = graph.addEdge( "DX", "D", "X" );
		Edge EY = graph.addEdge( "EY", "E", "Y" );
		Edge FZ = graph.addEdge( "FZ", "F", "Z" );
		Edge GT = graph.addEdge( "GT", "G", "T" );
		
		A.setAttribute("xyz", new double[] { 0, 6, 0 });
		B.setAttribute("xyz", new double[] { 0, 5, 0 });
		C.setAttribute("xyz", new double[] { 0, 4, 0 });
		D.setAttribute("xyz", new double[] { 0, 3, 0 });
		E.setAttribute("xyz", new double[] { 0, 2, 0 });
		F.setAttribute("xyz", new double[] { 0, 1, 0 });
		G.setAttribute("xyz", new double[] { 0, 0, 0 });

		U.setAttribute("xyz", new double[] { 3, 5, 0 });
		V.setAttribute("xyz", new double[] { 3, 4, 0 });
		W.setAttribute("xyz", new double[] { 3, 3, 0 });
		X.setAttribute("xyz", new double[] { 3, 2, 0 });
		Y.setAttribute("xyz", new double[] { 3, 1, 0 });
		Z.setAttribute("xyz", new double[] { 3, 0, 0 });
		T.setAttribute("xyz", new double[] { 3,-1, 0 });
		
		graph.getNode("a").setAttribute("xyz", new double[] { 6, 5, 0 });
		graph.getNode("b").setAttribute("xyz", new double[] { 6, 4, 0 });
		graph.getNode("c").setAttribute("xyz", new double[] { 6, 3, 0 });
		graph.getNode("d").setAttribute("xyz", new double[] { 6, 2, 0 });
		graph.getNode("e").setAttribute("xyz", new double[] { 6, 1, 0 });
		graph.getNode("f").setAttribute("xyz", new double[] { 6, 0, 0 });
		graph.getNode("g").setAttribute("xyz", new double[] { 6,-1, 0 });

		graph.getNode("u").setAttribute("xyz", new double[] { 9, 6, 0 });
		graph.getNode("v").setAttribute("xyz", new double[] { 9, 5, 0 });
		graph.getNode("w").setAttribute("xyz", new double[] { 9, 4, 0 });
		graph.getNode("x").setAttribute("xyz", new double[] { 9, 3, 0 });
		graph.getNode("y").setAttribute("xyz", new double[] { 9, 2, 0 });
		graph.getNode("z").setAttribute("xyz", new double[] { 9, 1, 0 });
		graph.getNode("t").setAttribute("xyz", new double[] { 9, 0, 0 });

		graph.getNode("i").setAttribute("xyz", new double[] { 3, 7, 0 });
		graph.getNode("j").setAttribute("xyz", new double[] { 6, 8, 0 });

		A.setAttribute("label", "Center");
		B.setAttribute("label", "AtLeft");
		C.setAttribute("label", "AtRight");
		D.setAttribute("label", "Left");
		E.setAttribute("label", "Right");
		F.setAttribute("label", "Under");
		G.setAttribute("label", "Above");

		U.setAttribute("label", "Center");
		V.setAttribute("label", "AtLeft");
		W.setAttribute("label", "AtRight");
		X.setAttribute("label", "Left");
		Y.setAttribute("label", "Right");
		Z.setAttribute("label", "Under");
		T.setAttribute("label", "Above");

		au.setAttribute("label", "Center");
		bv.setAttribute("label", "AtLeft");
		cw.setAttribute("label", "AtRight");
		dx.setAttribute("label", "Left");
		ey.setAttribute("label", "Right");
		fz.setAttribute("label", "Under");
		gt.setAttribute("label", "Above");
		ij.setAttribute("label", "Along");

		graph.setAttribute("ui.screenshot", "text_align.png");

		while( loop ) {
			pipeIn.pump();
			sleep( 40 );
		}
		System.out.println( "bye bye" );
		System.exit(0);
	}
	
	protected void sleep( long ms ) {
		try {
			Thread.sleep( ms );
		} catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	// Viewer Listener Interface
	
		public void viewClosed( String id ) { loop = false ;}
	
		public void buttonPushed( String id ) {
			if( id == "quit" )
	 			loop = false;
	 		else if( id == "A" )
	 			System.out.println( "Button A pushed" );
		}
	
	 	public void buttonReleased( String id ) {}
	 
	 // Data
	private String styleSheet = 
		"graph {"+
		"	canvas-color: white;"+
		"	fill-mode: plain;"+
		"	fill-color: white;"+
		"	padding: 30px;"+
		"}"+
		"node {"+
		"	shape: circle;"+
		"	size: 10px;"+
		"	fill-mode: plain;"+
		"	fill-color: #0004;"+
		"}"+
		"node:clicked {"+
		"	fill-color: #F004;"+
		"}"+
		"node:selected {"+
		"	fill-color: #00F4;"+
		"}"+
		"node#A {"+
		"	text-alignment: center;"+
		"	text-color: #F00;"+
		"}"+
		"node#B {"+
		"	text-alignment: at-left;"+
		"	text-color: #0F0;"+
		"}"+
		"node#C {"+
		"	text-alignment: at-right;"+
		"	text-color: #00F;"+
		"}"+
		"node#D {"+
		"	text-alignment: left;"+
		"	text-color: #FA0;"+
		"}"+
		"node#E {"+
		"	text-alignment: right;"+
		"	text-color: #0FF;"+
		"}"+
		"node#F {"+
		"	text-alignment: under;"+
		"	text-color: #F0F;"+
		"}"+
		"node#G {"+
		"	text-alignment: above;"+
		"	text-color: #999;"+
		"}"+
		"node#U {"+
		"	text-alignment: center;"+
		"	text-color: #F00;"+
		"	icon-mode: at-left;"+
		"	icon: url('"+URL_IMAGE+"');"+
		"}"+
		"node#V {"+
		"	text-alignment: at-left;"+
		"	text-color: #0F0;"+
		"	icon-mode: at-left;"+
		"	icon: url('"+URL_IMAGE+"');"+
		"}"+
		"node#W {"+
		"	text-alignment: at-right;"+
		"	text-color: #00F;"+
		"	icon-mode: at-left;"+
		"	icon: url('"+URL_IMAGE+"');"+
		"}"+
		"node#X {"+
		"	text-alignment: left;"+
		"	text-color: #FA0;"+
		"	icon-mode: at-left;"+
		"	icon: url('"+URL_IMAGE+"');"+
		"}"+
		"node#Y {"+
		"	text-alignment: right;"+
		"	text-color: #0FF;"+
		"	icon-mode: at-left;"+
		"	icon: url('"+URL_IMAGE+"');"+
		"}"+
		"node#Z {"+
		"	text-alignment: under;"+
		"	text-color: #F0F;"+
		"	icon-mode: at-left;"+
		"	icon: url('"+URL_IMAGE+"');"+
		"}"+
		"node#K {"+
		"	text-alignment: above;"+
		"	text-color: #999;"+
		"	icon-mode: at-left;"+
		"	icon: url('"+URL_IMAGE+"');"+
		"}"+
		"edge {"+
		"	fill-color: #0004;"+
		"}"+
		"edge#au { text-alignment: center; }"+
		"edge#bv { text-alignment: at-left; }"+
		"edge#cw { text-alignment: at-right; }"+
		"edge#dx { text-alignment: left; }"+
		"edge#ey { text-alignment: right; }"+
		"edge#fz { text-alignment: under; }"+
		"edge#gt { text-alignment: above; }"+
		"edge#ij { text-alignment: along; }";
	public void mouseOver(String id){}

	public void mouseLeft(String id){}
}
