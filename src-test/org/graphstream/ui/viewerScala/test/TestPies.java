package org.graphstream.ui.viewerScala.test;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

public class TestPies {
	public static void main(String[] args) {
		(new TestPies()).run();
	}

	private void run() {
		System.setProperty("org.graphstream.ui", "org.graphstream.ui.scalaViewer.util.ScalaDisplay");
		SingleGraph g = new SingleGraph("test");
		Node A = g.addNode("A");
		Node B = g.addNode("B");
		g.addEdge("AB", "A", "B");

		SpriteManager sm = new SpriteManager(g);
		Sprite pie = sm.addSprite("pie");

		g.setAttribute("ui.antialias");
		pie.setAttribute("ui.style", "shape: pie-chart; fill-color: #F00, #0F0, #00F; size: 30px;");
//		g.addAttribute("ui.stylesheet", "sprite { shape: pie-chart; fill-color: #F00, #0F0, #00F; size: 30px; } node {fill-color: red; }")
		double[] values = new double[3];
		values[0] = 0.3333;
		values[1] = 0.3333;
		values[2] = 0.3333;
		pie.setAttribute("ui.pie-values", values);
		pie.attachToEdge("AB");
		pie.setPosition(0.5);
		
		g.display();

		double[] values2 = new double[3];
		values2[0] = 0.1;
		values2[1] = 0.3;
		values2[2] = 0.6;
		boolean on = true;
		
		while(true) {
		    try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    if(on) {
		        values[0] = 0.1;
		        values[1] = 0.3;
		        values[2] = 0.6;
		        A.setAttribute("ui.pie-values", new double[]{1.0});
		        A.setAttribute("ui.style", "shape:pie-chart; fill-color:red;");
		    } 
		    else {
		        values[0] = 0.3;
		        values[1] = 0.3;
		        values[2] = 0.3;
		        A.setAttribute("ui.pie-values", new double[]{1.0});
		        A.setAttribute("ui.style", "shape:pie-chart; fill-color:blue;");
		    }
		    pie.setAttribute("ui.pie-values", values);
		    
		    //pie.addAttribute("ui.pie-values", if(on) values else values2)
		    on = ! on;
		}
	}
}
