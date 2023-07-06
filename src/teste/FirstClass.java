package teste;

import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import org.graphstream.graph.Path;

public class FirstClass {
	
	private static String styleSheet =
            "node {"+
            "   size-mode: dyn-size;"+
            "   shape: circle;"+
            "   size: 20px;"+
            "   fill-mode: plain;"+
            "   fill-color: #CCC;"+
            "   stroke-mode: plain;"+
            "   stroke-color: black;"+
            "   stroke-width: 1px;"+
            "}";

	public static void main(String[] args) {
		Graph graph = new SingleGraph("graph");
		
		// Visualização do grafo
//		System.setProperty("org.graphstream.ui", "swing"); 
//		graph.display();
//		graph.setAttribute("ui.stylesheet", styleSheet);
		
		Node A = graph.addNode("A");
		Node B = graph.addNode("B");
//		Node C = graph.addNode("C");
//		Node D = graph.addNode("D");
//		Node E = graph.addNode("E");
//		Node F = graph.addNode("F");
//
		Edge AB = graph.addEdge("AB", "A", "B");
//		Edge BC = graph.addEdge("BC", "B", "C");
//		Edge CA = graph.addEdge("CA", "C", "A");
//		Edge CE = graph.addEdge("CE", "C", "E");		
//		Edge AD = graph.addEdge("AD", "A", "D");
//		Edge DE = graph.addEdge("DE", "D", "E");
//		Edge DF = graph.addEdge("DF", "D", "F");
//		Edge EF = graph.addEdge("EF", "E", "F");
//		
//		graph.setStrict(false);
//        graph.setAutoCreate(true);
//
//		System.out.printf("Arestas: %s\n\n", graph.edges().toArray()[0]);
//        
//        for (Node node : graph) {
//            node.setAttribute("ui.label", node.getId());
//        }
		
		//////// Path
		Path path = new Path();
		
//		path.add(AB);
		
//		Node root = path.getRoot();		
//		System.out.printf("Raiz: %s\n\n", root.toString());
		
//		int tamanho = path.size();
//		System.out.printf("Tamanho: %d\n\n", tamanho);
		
		
//		// Adicionando raiz
//		path.setRoot(A);
//		System.out.printf("Vazio? %s\n\n", path.empty());
//		
//		path.setRoot(B);
//		System.out.printf("Raiz: %s\n\n", path.getRoot());
		
//		
//		// Adicionando aresta 
//		path.add(AB);
//		path.add(BC);
//		path.add(CA);
//		path.add(AD);
//		path.add(DE);
//		path.add(EF);
		
		// No origem igual a no destino
		Edge AA = graph.addEdge("AA", "A", "A");
		path.add(AA);
//		
//		// Transformar em string
		System.out.printf("toString(): %s\n\n", path.toString());
//		
//		// Apagar path
//		path.clear();
//		System.out.printf("Vazio? %s\n\n", path.empty());
//		System.out.printf("toString(): %s\n\n", path.toString());
//		
//		
//		// Verificar lista de nós
//		System.out.printf("Contagem de nós: %d\n", path.getNodeCount());		
//		System.out.printf("Lista de nós: %s\n\n", path.getNodePath().toString());
//		
//		// Verificar lista de arestas
//		System.out.printf("Contagem de arestas: %d\n", path.getEdgeCount());
//		System.out.printf("Lista de arestas: %s\n\n", path.getEdgePath().toString());
//		
//		// Verificando últimos valores da pilha
//		Edge lastEdge = path.peekEdge();
//		System.out.printf("Última aresta: %s\n\n", lastEdge.getId());
//		
//		Node lastNode = path.peekNode();
//		System.out.printf("Último nó: %s\n\n", lastNode.getId());	
//		
//		// Remover
//		Path pathCopy1 = path.getACopy();
//		Edge removedEdge = pathCopy1.popEdge();
//		System.out.printf("Aresta removida: %s\n\n", removedEdge.getId());	
//		System.out.printf("Caminho removendo última aresta: %s\n\n", pathCopy1.toString());	
//		
//		Path pathCopy2 = path.getACopy();
//		Node removedNode = pathCopy2.popNode();
//		System.out.printf("Nó removido: %s\n\n", removedNode.getId());
//		System.out.printf("Caminho removendo último nó: %s\n\n", pathCopy2.toString());
	
	}

}
