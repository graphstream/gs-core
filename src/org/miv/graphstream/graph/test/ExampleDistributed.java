/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.miv.graphstream.graph.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.miv.graphstream.algorithm.Algorithms;
import org.miv.graphstream.distributed.algorithm.DistributedAlgorithms;
import org.miv.graphstream.distributed.req.GraphAsyncReq;
import org.miv.graphstream.distributed.req.GraphGenericReq;
//import org.miv.graphstream.distributed.req.GraphReqContainer;
import org.miv.graphstream.distributed.rmi.GraphRegistry;
import org.miv.graphstream.distributed.rmi.GraphRmi;
import org.miv.graphstream.distributed.rmi.GraphRmiServer;
import org.miv.graphstream.graph.Graph;
import org.miv.graphstream.graph.implementations.DefaultGraph;
import org.miv.graphstream.io.GraphParseException;
import org.miv.graphstream.io.GraphReader;
import org.miv.graphstream.io.GraphReaderFactory;
import org.miv.graphstream.io.GraphReaderListenerExtended;
import org.miv.graphstream.io.GraphReaderListenerHelper;


/**
 *
 * @author baudryj
 *
 */

public class ExampleDistributed {

	static String fileSrc ;

	public static void main(String args[]) throws RemoteException, Exception {
		System.out.println("Debut de la demo") ;
		long t1 = System.currentTimeMillis() ;
		//int i = 0 ;

		//demo222();
			testNonDistributed();
		//testLecture("z_grid-d3-g1.dgs", new DefaultGraph());

		long t2 = System.currentTimeMillis() ;
		System.out.println("delta t = " + (int)((t2-t1)/1000));
		System.out.println("Fin de la demo") ;
	}

	public static void testNonDistributed() {
		try {
			Graph g = new DefaultGraph() ;
			//GraphReader reader = GraphReaderFactory.readerFor("z_grid_d-100_c-100.dgs") ;
			//GraphReader reader = GraphReaderFactory.readerFor("./dgs/z_grid_d-200_c-120.dgs") ;
			GraphReader reader = GraphReaderFactory.readerFor("./dgs/z_grid_d-500_c-30.dgs");
			GraphReaderListenerExtended alistener = new GraphReaderListenerHelper(g);
			reader.addGraphReaderListener(alistener);
			Algorithms algo = new Algorithms();
			algo.setGraph(g);
			//reader.begin("z_grid_d-100_c-100.dgs") ;
			//reader.begin("./dgs/z_grid_d-200_c-120.dgs") ;
			reader.begin("./dgs/z_grid_d-500_c-30.dgs") ;
			while(reader.nextStep()) {
			}
			reader.end();
			System.out.println("nb edge/node = " + g.getEdgeCount() +"-"+ g.getNodeCount() + "-" + algo.getDensity());
		}
		catch(IOException e) {
		}
		catch(GraphParseException e) {
		}
	}


	public static void demo222() {
		// Creation graphDistribue
		GraphRegistry r = new GraphRegistry() ;

			System.out.println("Ajout des serveurs");
			r.addClient("rmi:127.0.0.1/DefaultGraph:g1");
			r.addClient("rmi:127.0.0.1/DefaultGraph:g2");
			r.addClient("rmi:127.0.0.1/DefaultGraph:g3");

			r.broadcastClient("g1");
			r.broadcastClient("g2");
			r.broadcastClient("g3");

			System.out.println("Broadcast entre serveurs");

			// Chargement de dgs distribués
			GraphAsyncReq async = new GraphAsyncReq(r) ;

			List<GraphGenericReq> l = new ArrayList<GraphGenericReq>();
			//l.add(new GraphGenericReq("g1", r.getClient("g1").exec(), "loadData", new String[] {"simu03-d3-g1-amazon_0201485419_87500.dgs"}));
			//l.add(new GraphGenericReq("g2", r.getClient("g2").exec(), "loadData", new String[] {"simu03-d3-g2-amazon_0201485419_87500.dgs"}));
			//l.add(new GraphGenericReq("g3", r.getClient("g3").exec(), "loadData", new String[] {"simu03-d3-g3-amazon_0201485419_87500.dgs"}));

			//l.add(new GraphGenericReq("g1", r.getClient("g1").exec(), "loadData", new String[] {"z_grid-d3-g1.dgs"}));
			//l.add(new GraphGenericReq("g2", r.getClient("g2").exec(), "loadData", new String[] {"z_grid-d3-g2.dgs"}));
			//l.add(new GraphGenericReq("g3", r.getClient("g3").exec(), "loadData", new String[] {"z_grid-d3-g3.dgs"}));

			l.add(new GraphGenericReq("g1", r.getClient("g1").exec(), "loadData", new String[] {"z_grid_d-500_c-30-d3-g1.dgs"}));
			l.add(new GraphGenericReq("g2", r.getClient("g2").exec(), "loadData", new String[] {"z_grid_d-500_c-30-d3-g2.dgs"}));
			l.add(new GraphGenericReq("g3", r.getClient("g3").exec(), "loadData", new String[] {"z_grid_d-500_c-30-d3-g3.dgs"}));

			//try {
				//r.getClient("g1").exec().loadData("z_grid-d3-g1.dgs");
				//GraphReqContainer g = new GraphReqContainer();
				//g.setAllParams("g1", "DefaultGraph", "addNode", new String[] {"z_grid-d3-g1.dgs"});
				//r.getClient("g1").exec().executeReq(g);
				//r.getClient("g1").exec().
				//r.getClient("g2").exec().loadData("z_grid-d3-g2.dgs");
				//r.getClient("g3").exec().loadData("z_grid-d3-g3.dgs");

				/*r.getClient("g1").exec().loadData("z_grid_d-500_c-30-d4-g1.dgs");
				r.getClient("g2").exec().loadData("z_grid_d-500_c-30-d4-g2.dgs");
				r.getClient("g3").exec().loadData("z_grid_d-500_c-30-d4-g3.dgs");
				r.getClient("g4").exec().loadData("z_grid_d-500_c-30-d4-g4.dgs");*/

				//r.getClient("g3").exec().removeEdge("30-20");
				/*r.getClient("g1").exec().addNode("g1/1");
				r.getClient("g1").exec().addNode("g1/2");
				r.getClient("g2").exec().addNode("g2/3");
				r.getClient("g3").exec().addNode("g3/4");
				r.getClient("g1").exec().addEdge("23", "g1/2", "g2/3");
				r.getClient("g1").exec().addEdge("12", "g1/1", "g1/2");
				r.getClient("g1").exec().removeNode("g1/2");*/
			/*}
			catch(RemoteException e) {
				System.out.println("RemoteException : " + e.getMessage());
			}*/

			System.out.println("Chargement des graphs");
			async.exec(l);

			// Calcul d'un indicateur
			DistributedAlgorithms algo = new DistributedAlgorithms() ;
			algo.setGraphRegistry(r);
			System.out.println("Calcul du nombre de noeuds");
			System.out.println("algo.getNodeCount() : " + algo.getNodeCount());
			//algo.getEdgeCount();
			System.out.println("algo.getEdgeCount() : " + algo.getEdgeCount());

			/*try {
				System.out.println("r3 = " + r.getClient("g3").exec().getEdgeCount());
			}
			catch(RemoteException e) {

			}*/

		}


	public static void demo333() {
		try {
			GraphRmiServer g = new GraphRmiServer();
			g.createGraph("omario", "DefaultGraph");
			g.register("rmi:127.0.0.1/DefaultGraph:g1");
			g.register("rmi:127.0.0.1/DefaultGraph:g2");
			g.addNode("g1/001");
			g.addNode("g2/001");
			g.addNode("omario/001");

			GraphRmi g1 = g.getServerInst("g1");
			g1.createGraph("g1", "DefaultGraph");
			g1.addNode("g1/001");

			DistributedAlgorithms algo = new DistributedAlgorithms() ;

			algo.getNodeCount();

			System.out.println("g1.getNodeCount() : " + algo.getNodeCount());


		}
		catch(RemoteException e) {
			System.out.println("" + e.getMessage());
		}
	}

	public static void testReflex() throws Exception {
		DefaultGraph g = new DefaultGraph() ;
		String m = "addNode" ;
		String[] a = {"001"};
		lancerMethode(g,a,m);

		System.out.println("value : " + ((Integer)lancerMethode(g,null,"getNodeCount")).intValue());

	}

	static public Object lancerMethode(Object obj, Object[] args, String nomMethode) {
	try {
	   Class<?>[] paramTypes = null;
	   if(args != null)
	   {
	      paramTypes = new Class[args.length];
	      for(int i=0;i<args.length;++i)
	      {
	         paramTypes[i] = args[i].getClass();
	      }
	   }
	   Method m = obj.getClass().getMethod(nomMethode,paramTypes);
	   return m.invoke(obj,args);
		}
		catch(NoSuchMethodException e) {
			System.out.println("NoSuchMethodException " + e.getMessage());
			return null ;
		}
		catch(IllegalAccessException e) {
			System.out.println("IllegalAccessException " + e.getMessage());
			return null ;
		}
		catch(InvocationTargetException e) {
			System.out.println("InvocationTargetException " + e.getMessage());
			return null ;
		}
	}

	public static void testLecture(String fileName, Graph g) throws Exception {
		try {
			GraphReader graphReader = GraphReaderFactory.readerFor(fileName) ;
			GraphReaderListenerExtended alistener = new GraphReaderListenerHelper(g) ;
			graphReader.addGraphReaderListener(alistener) ;
			graphReader.begin(fileName) ;
			while(graphReader.nextEvents()) {
			}
			graphReader.end();
			System.out.println("lecture ok");
			System.out.println("nb node : " + g.getNodeCount());
		}
		catch(IOException e) {
			System.out.println("IOException getDgsReader : " + e.getMessage());
		}
		catch(GraphParseException e) {
			System.out.println("GraphParseException in testLecture : " + e.getMessage());
		}
	}


}
