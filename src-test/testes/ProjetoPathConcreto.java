package testes;

import org.graphstream.graph.Path;

import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;

import org.graphstream.graph.Edge;

import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.java.annotation.GraphWalker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@GraphWalker(value = "random(edge_coverage(100))", start = "v_Inicio")
public class ProjetoPathConcreto extends ExecutionContext implements ProjetoPathModel {

	Path path = null;
	static Graph graph = new SingleGraph("graph");
	boolean illegalArgument = false;

	private void imprimeGrafo() {
		// Verificar lista de nós
		System.out.printf("Contagem de nós: %d\n", path.getNodeCount());		
		System.out.printf("Lista de nós: %s\n", path.getNodePath().toString());
		
		// Verificar lista de arestas
		int quantidadeArestas = path.getEdgeCount();
		System.out.printf("Contagem de arestas: %d\n", quantidadeArestas);
		System.out.printf("Lista de arestas: %s\n", path.getEdgePath().toString());
		
		// Verificando últimos valores da pilha
		if(quantidadeArestas != 0) {
			Edge lastEdge = path.peekEdge();
			System.out.printf("Última aresta: %s\n", lastEdge.getId());
			
			Node lastNode = path.peekNode();
			System.out.printf("Último nó: %s\n\n", lastNode.getId());
		}
		
		// Transformar em string
		System.out.printf("toString(): %s\n\n", path.toString());
	}
	
	@Override
	public void v_Inicio(){
		System.out.println("Running: v_Inicio\n");
		Assertions.assertNull(path);
	};

	@Override
	public void e_CriaCaminho(){
		System.out.println("Running: e_CriaCaminho\n");
		path = new Path();
	};

	@Override
	public void v_CaminhoVazio(){
		System.out.println("Running: v_CaminhoVazio\n");
		Assertions.assertNotNull(path);				// Caminho instanciado
		Assertions.assertNull(path.getRoot());		// Raiz vazia
		Assertions.assertTrue(path.empty());		// Caminho vazio
		imprimeGrafo();
	};
	
	static String geraIdDeTamanho5() {
		int n = 5;
        // length is bounded by 256 Character
        byte[] array = new byte[256];
        new Random().nextBytes(array);
  
        String randomString = new String(array, Charset.forName("UTF-8"));
   
        // Create a StringBuffer to store the result
        StringBuffer r = new StringBuffer();
  
        // Append first 20 alphanumeric characters
        // from the generated random String into the result
        for (int k = 0; k < randomString.length(); k++) {
  
            char ch = randomString.charAt(k);
  
            if (((ch >= 'a' && ch <= 'z')
                 || (ch >= 'A' && ch <= 'Z')
                 || (ch >= '0' && ch <= '9'))
                && (n > 0)) {
  
                r.append(ch);
                n--;
            }
        }
  
        // return the resultant string
        return r.toString();
    }
	
	private Node geraNoCompativel() {
		// Gerando id aleatório
        String id = geraIdDeTamanho5();
		
        // Significa que já existem arestas no grafo
		if(path.size() != 0) {
			Node ultimo_no = path.peekNode();
			String id_ultimo_no = ultimo_no.getId();
			while(id == id_ultimo_no) {	// O novo nó de destino não pode ser igual ao de origem
		        id = geraIdDeTamanho5();
			}
		} 
		
		Node no = graph.addNode(id.toUpperCase());
		return no;
	};

	@Override
	public void e_DefineRaiz(){
		System.out.println("Running: e_DefineRaiz\n");
		path.setRoot(geraNoCompativel());
	};

	@Override
	public void e_RedefineRaiz() {
		System.out.println("Running: e_RedefineRaiz\n");
		path.setRoot(geraNoCompativel());
	}

	@Override
	public void v_CaminhoComRaiz(){
		System.out.println("Running: v_CaminhoComRaiz\n");
		Assertions.assertNotNull(path);				// Caminho instanciado
		Assertions.assertNotNull(path.getRoot());	// Raiz não vazia
		Assertions.assertFalse(path.empty());		// Caminho não vazio
		imprimeGrafo();
	};
	
	private Edge geraArestaCompativel() {
		Node no_origem = null;
		
		if(!path.empty()) {
			no_origem = path.peekNode();
		} else {
			// Criando novo nó
			no_origem = geraNoCompativel();
		}
		
		// Criando novo nó
		Node no_destino = geraNoCompativel();
		
		// Criando edge
		Edge edge = graph.addEdge(no_origem.getId().concat(no_destino.getId()), no_origem, no_destino);
		
		return edge;
	};

	@Override
	public void e_AdicionaAresta(){
		try {
			System.out.println("Running: e_AdicionaAresta\n");
			Edge edge = geraArestaCompativel();
			path.add(edge);		// Pode acontecer no caso em que a raiz ainda não foi definida, gerando erro
		} catch (java.lang.IllegalArgumentException e) {
			illegalArgument = true;
		}
	};
	
	@Override
	public void e_AdicionaNoEAresta() {
		System.out.println("Running: e_AdicionaNoEAresta\n");
		Edge edge = geraArestaCompativel();
		path.add(edge.getNode0(), edge);	// Segunda versão do método add, o qual adiciona aresta e define raiz
	}

	@Override
	public void v_CaminhoComArestas(){
		System.out.println("Running: v_CaminhoComArestas\n");
		Assertions.assertNotNull(path);				// Caminho instanciado
		Assertions.assertNotNull(path.getRoot());	// Raiz não vazia
		Assertions.assertNotEquals(0, path.size(), "Tamanho do caminho está zerado.\n");
		imprimeGrafo();
	};

	@Override
	public void e_RemoveAresta(){
		System.out.println("Running: e_RemoveAresta\n");
		Edge edge_removida = path.popEdge();
		System.out.printf("A aresta removida foi: %s\n", edge_removida.toString());
	};

	@Override
	public void e_RemoveNo(){
		System.out.println("Running: e_RemoveNo\n");
		Node node_removido = path.popNode();
		System.out.printf("O nó removido foi: %s\n", node_removido.toString());
	};

	@Override
	public void e_AdicionaArestaAntesDaRaiz(){
		try {
			System.out.println("Running: e_AdicionaArestaAntesDaRaiz\n");
			Edge edge = geraArestaCompativel();
			path.add(edge);
		} catch (java.lang.IllegalArgumentException e) {
			illegalArgument = true;
		}
	};
	
	private Edge geraArestaIncompativel() {
		String id = geraIdDeTamanho5();
		Node no = graph.addNode(id);
		Edge edge = graph.addEdge(id.concat(id), no, no);
		return edge;
	}

	@Override
	public void e_AdicionaNoFonteDiferenteUltimoNoDestino(){
		try {
			System.out.println("Running: e_AdicionaNoFonteDiferenteUltimoNoDestino\n");
			Edge edge = geraArestaIncompativel();
			path.add(edge);
		} catch (java.lang.IllegalArgumentException e) {
			illegalArgument = true;
		}
	};

	@Override
	public void e_AdicionaNoNuloEAresta() {
		try {
			System.out.println("Running: e_AdicionaNoNuloEAresta\n");
			Edge edge = geraArestaCompativel();
			path.add(null, edge);	// Gera exceção
		} catch (java.lang.IllegalArgumentException e) {
			illegalArgument = true;
		}
	}

	@Override
	public void e_AdicionaNoNaoPresenteEAresta() {
		try {
			System.out.println("Running: e_AdicionaNoNaoPresenteEAresta\n");
			Edge edge = geraArestaCompativel();
			String id = edge.getNode0().getId();
			while(id == edge.getNode0().getId() || id == edge.getNode1().getId()) {
				id = geraIdDeTamanho5();
			}
			Node no = graph.addNode(id);
			path.add(no, edge);	// Gera exceção
		} catch (java.lang.IllegalArgumentException e) {
			illegalArgument = true;
		}		
	}

	@Override
	public void v_ExcecaoIllegalArgument(){
		System.out.println("Running: v_ExcecaoIllegalArgument\n");
		// Caso a raiz seja nula, é o caso que veio de "e_AdicionaArestaAntesDaRaiz". Então, testar no if, diz que está correto por si só.
		// Caso a raiz não seja nula, é o caso que deveria vir de "e_AdicionaNoFonteDiferenteUltimoNoDestino", então precisa verificar se a exceção foi lanaçada também.
		if(path.getRoot() != null) {
			Assertions.assertTrue(illegalArgument);
		}
	};
	
	@Override
	public void e_Reinicia(){
		System.out.println("Running: e_Reinicia\n");
		path.clear();
		illegalArgument = false;
		System.out.printf("Path: %s\n\n", path.toString());
		path = null;
	};
	
	private static void graphConfig() {
		graph.setStrict(false);
        graph.setAutoCreate(true);
	}
	
	
	@Test
	public void TestPath() {
		graphConfig();
		
		// Sequencia de chamadas
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_DefineRaiz();
		v_CaminhoComRaiz();
		e_AdicionaAresta();
		v_CaminhoComArestas();
		e_RedefineRaiz();
		v_CaminhoComArestas();
		e_AdicionaAresta();
		v_CaminhoComArestas();
		e_AdicionaNoFonteDiferenteUltimoNoDestino();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_AdicionaNoNaoPresenteEAresta();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_DefineRaiz();
		v_CaminhoComRaiz();
		e_RedefineRaiz();
		v_CaminhoComRaiz();
		e_RedefineRaiz();
		v_CaminhoComRaiz();
		e_AdicionaNoNaoPresenteEAresta();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_AdicionaAresta();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_AdicionaNoNuloEAresta();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_AdicionaNoNuloEAresta();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_AdicionaArestaAntesDaRaiz();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_AdicionaAresta();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_DefineRaiz();
		v_CaminhoComRaiz();
		e_AdicionaNoEAresta();
		v_CaminhoComArestas();
		e_RedefineRaiz();
		v_CaminhoComArestas();
		e_RemoveNo();
		v_CaminhoComRaiz();
		e_AdicionaNoEAresta();
		v_CaminhoComArestas();
		e_RedefineRaiz();
		v_CaminhoComArestas();
		e_RemoveNo();
		v_CaminhoComRaiz();
		e_AdicionaAresta();
		v_CaminhoComArestas();
		e_AdicionaNoEAresta();
		v_CaminhoComArestas();
		e_AdicionaNoFonteDiferenteUltimoNoDestino();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_AdicionaAresta();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_AdicionaAresta();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_DefineRaiz();
		v_CaminhoComRaiz();
		e_AdicionaAresta();
		v_CaminhoComArestas();
		e_RemoveAresta();
		v_CaminhoComRaiz();
		e_AdicionaNoNuloEAresta();
		v_CaminhoComArestas();
		e_AdicionaNoFonteDiferenteUltimoNoDestino();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_DefineRaiz();
		v_CaminhoComRaiz();
		e_AdicionaNoNuloEAresta();
		v_CaminhoComArestas();
		e_AdicionaAresta();
		v_CaminhoComArestas();
		e_RemoveNo();
		v_CaminhoComArestas();
		e_RedefineRaiz();
		v_CaminhoComArestas();
		e_AdicionaNoNuloEAresta();
		v_CaminhoComArestas();
		e_RedefineRaiz();
		v_CaminhoComArestas();
		e_RedefineRaiz();
		v_CaminhoComArestas();
		e_AdicionaNoNuloEAresta();
		v_CaminhoComArestas();
		e_AdicionaNoNaoPresenteEAresta();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_DefineRaiz();
		v_CaminhoComRaiz();
		e_AdicionaNoNuloEAresta();
		v_CaminhoComArestas();
		e_AdicionaNoNuloEAresta();
		v_CaminhoComArestas();
		e_AdicionaAresta();
		v_CaminhoComArestas();
		e_AdicionaNoEAresta();
		v_CaminhoComArestas();
		e_AdicionaNoEAresta();
		v_CaminhoComArestas();
		e_RemoveAresta();
		v_CaminhoComArestas();
		e_AdicionaNoNaoPresenteEAresta();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_AdicionaArestaAntesDaRaiz();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_AdicionaNoEAresta();
		v_CaminhoComArestas();
		e_RemoveNo();
		v_CaminhoComRaiz();
		e_AdicionaAresta();
		v_CaminhoComArestas();
		e_RedefineRaiz();
		v_CaminhoComArestas();
		e_RedefineRaiz();
		v_CaminhoComArestas();
		e_AdicionaNoEAresta();
		v_CaminhoComArestas();
		e_RemoveAresta();
		v_CaminhoComArestas();
		e_AdicionaAresta();
		v_CaminhoComArestas();
		e_RemoveNo();
		v_CaminhoComArestas();
		e_AdicionaAresta();
		v_CaminhoComArestas();
		e_AdicionaAresta();
		v_CaminhoComArestas();
		e_RedefineRaiz();
		v_CaminhoComArestas();
		e_AdicionaNoFonteDiferenteUltimoNoDestino();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_DefineRaiz();
		v_CaminhoComRaiz();
		e_AdicionaNoNaoPresenteEAresta();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_AdicionaArestaAntesDaRaiz();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_AdicionaNoNuloEAresta();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_DefineRaiz();
		v_CaminhoComRaiz();
		e_AdicionaNoNuloEAresta();
		v_CaminhoComArestas();
		e_AdicionaNoNaoPresenteEAresta();
		v_ExcecaoIllegalArgument();
		e_Reinicia();
		v_Inicio();
		e_CriaCaminho();
		v_CaminhoVazio();
		e_DefineRaiz();
		v_CaminhoComRaiz();
		e_AdicionaNoFonteDiferenteUltimoNoDestino();
		v_ExcecaoIllegalArgument();

	}


}
