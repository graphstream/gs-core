package org.graphstream.ui.viewerScala.test;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.scalaViewer.ScalaGraphRenderer;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.SwingViewer;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class TestSprites implements ViewerListener{
	public static void main(String[] args) {
		(new TestSprites()).run();
	}
	
	public static final String URL_IMAGE = "file:///home/hicham/Bureau/b.png";
	
	boolean loop = true ;
	private void run() {
		MultiGraph	graph  = new MultiGraph( "TestSprites" );
		
		Viewer viewer = new SwingViewer( graph, SwingViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD );
		ViewerPipe pipeIn = viewer.newViewerPipe();
		viewer.addView( "view1", new ScalaGraphRenderer() );

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
		
		C.setAttribute("ui.points",
		        new Point3(-0.05f, -0.05f, 0f),
		        new Point3( 0f,    -0.02f, 0f),
		        new Point3( 0.05f, -0.05f, 0f),
		        new Point3( 0f,     0.05f, 0f));
		
		graph.addEdge( "AB1", "A", "B", true );
		graph.addEdge( "AB2", "B", "A", true );
		graph.addEdge( "BC", "B", "C" );
		graph.addEdge( "CD", "C", "D", true );
		graph.addEdge( "DA", "D", "A" );
		graph.addEdge( "DE", "D", "E", true );
		graph.addEdge( "EB", "E", "B", true );
		graph.addEdge( "BB", "B", "B", true );

		graph.getEdge("CD").setAttribute("ui.points",
		        new Point3(1, 0, 0),
		        new Point3(0.6f, 0.1f, 0f),
		        new Point3(0.3f,-0.1f, 0f),
		        new Point3(0, 0, 0));
		
		
		A.setAttribute("xyz",new double[] { 0, 1, 0 });
		B.setAttribute("xyz",new double[] { 1.5, 1, 0 });
		C.setAttribute("xyz",new double[] { 1, 0, 0 });
		D.setAttribute("xyz",new double[] { 0, 0, 0 });
		E.setAttribute("xyz",new double[] { 0.4, 0.6, 0 });

		A.setAttribute("label", "A");
		B.setAttribute("label", "B");
		C.setAttribute("label", "C");
		D.setAttribute("label", "D");
		E.setAttribute("label", "E");

		SpriteManager sman = new SpriteManager( graph );
		
		MovingEdgeSprite s1 = sman.addSprite("S1", MovingEdgeSprite.class);
		MovingEdgeSprite s2 = sman.addSprite("S2", MovingEdgeSprite.class);
		MovingEdgeSprite s3 = sman.addSprite("S3", MovingEdgeSprite.class);
		MovingEdgeSprite s4 = sman.addSprite("S4", MovingEdgeSprite.class);
		DataSprite s5 = sman.addSprite("S5", DataSprite.class);
		MovingEdgeSprite s6 = sman.addSprite("S6", MovingEdgeSprite.class);
		MovingEdgeSprite s7 = sman.addSprite("S7", MovingEdgeSprite.class);
		MovingEdgeSprite s8 = sman.addSprite("S8", MovingEdgeSprite.class);
		
		s1.attachToEdge("AB1");
		s2.attachToEdge("CD");
		s3.attachToEdge("DA");
		s4.attachToEdge("EB");
		s5.attachToNode("A");
		s6.attachToNode("D");
		s7.attachToEdge("AB2");
		s8.attachToEdge("EB");

		s2.setOffsetPx(20);
		s3.setOffsetPx(15);
		s4.setOffsetPx(4);
		s5.setPosition(Units.PX, 0, 30, 0);
		s5.setData(new float[]{0.3f, 0.5f, 0.2f});
		//s6.setOffsetPx(20);
		s8.setPosition(0.5f, 0.5f, 0f);

		s1.setAttribute("ui.label", "FooBar1");
		s2.setAttribute("ui.label", "FooBar2");
		s4.setAttribute("ui.label", "FooBar4");
		s7.setAttribute("ui.label", "FooBar7");

		s8.setAttribute("ui.points",
		        new Point3(-0.02f, -0.02f, 0f),
		        new Point3( 0f,    -0.01f, 0f),
		        new Point3( 0.02f, -0.02f, 0f),
		        new Point3( 0f,     0.02f, 0f));

		E.setAttribute("ui.pie-values", 0.2f, 0.3f, 0.4f, 0.1f);
		
		while( loop ) {
			pipeIn.pump();
			s1.move();
			s2.move();
			s3.move();
			s4.move();
			s6.move();
			s7.move();
			s8.move();
			sleep( 4 );
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

 	private String styleSheet = "graph {"+
					"	canvas-color: white;"+
					"	fill-mode: gradient-radial;"+
					"	fill-color: white, #EEEEEE;"+
					"	padding: 60px;"+
					"}"+
					"node {"+
					"	shape: circle;"+
					"	size: 14px;"+
					"	fill-mode: plain;"+
					"	fill-color: white;"+
					"	stroke-mode: plain;"+
					"	stroke-color: grey;"+
					"	stroke-width: 1px;"+
					"}"+
					"node:clicked {"+
					"	stroke-mode: plain;"+
					"	stroke-color: red;"+
					"}"+
					"node:selected {"+
					"	stroke-mode: plain;"+
					"	stroke-color: blue;"+
					"}"+
					"node#C {"+
					"	shape: polygon;"+
					"}"+
					"node#E {"+
					"	shape: pie-chart;"+
					"	fill-color: red, green, blue, yellow, magenta;"+
					"	size: 30px;"+
					"	stroke-mode: plain;"+
					"	stroke-width: 1px;"+
					"	stroke-color: black;"+
					"}"+
					"edge {"+
					"	shape: line;"+
					"	size: 1px;"+
					"	fill-color: grey;"+
					"	fill-mode: plain;"+
					"	arrow-shape: arrow;"+
					"	arrow-size: 10px, 3px;"+
					"}"+
					"edge#BC {"+
					"	shape: cubic-curve;"+
					"}"+
					"edge#EB {"+
					"	shape: cubic-curve;"+
					"}"+
					"edge#CD {"+
					"	shape: polyline;"+
					"}"+
					"sprite {"+
					"	shape: circle;"+
					"	fill-color: #944;"+
					"	z-index: -1;"+
					"}"+
					"sprite#S2 {"+
					"	shape: arrow;"+
					"	sprite-orientation: projection;"+
					"	size: 20px, 10px;"+
					"	fill-color: #449;"+
					"}"+
					"sprite#S3 {"+
					"	shape: arrow;"+
					"	sprite-orientation: to;"+
					"	size: 10px, 5px;"+
					"	fill-color: #494;"+
					"}"+
					"sprite#S4 {"+
					"	shape: flow;"+
					"	size: 8px;"+
					"	fill-color: #99A9;"+
					"	sprite-orientation: to;"+
					"}"+
					"sprite#S5 {"+
					"	shape: pie-chart;"+
					"	size: 40px;"+
					"	fill-color: cyan, magenta, yellow, red, green, blue;"+
					"	stroke-mode: plain;"+
					"	stroke-width: 1px;"+
					"	stroke-color: black;"+
					"}"+
					"sprite#S6 {"+
					"	shape: circle;"+
					"	size: 30px;"+
					"	fill-color: #55C;"+
					"}"+
					"sprite#S7 {"+
					"	shape: box;"+
					"	size: 100px, 58px;"+
					"	sprite-orientation: projection;"+
					"	fill-mode: image-scaled;"+
					"	fill-image: url('"+URL_IMAGE+"');"+
					"	fill-color: red;"+
					"	stroke-mode: none;"+
					"}"+
					"sprite#S8 {"+
					"	shape: polygon;"+
					"	fill-color: yellow;"+
					"	stroke-mode: plain;"+
					"	stroke-width: 1px;"+
					"	stroke-color: red;"+
					"	shadow-mode: plain;"+
					"	shadow-color: #707070;"+
					"	shadow-offset: 3px, -3px;"+
					"}";

	public void mouseOver(String id){}

	public void mouseLeft(String id){}

}



class MovingNodeSprite extends Sprite {
	double SPEED = 1f;
	double speed = SPEED;
	double off = 0f;
	Units units = Units.PX;

	public void setOffsetPx( float offset ) { off = offset; units = Units.PX ;}

	public void move() {
		double p = getZ() + speed;

		if( p < 0 || p > 360 ) { p = 0f; }

		Node node = (Node)getAttachment();

		if( node != null ) {
			setPosition( units, off, 0, p );
		}
	}
}

