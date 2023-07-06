package testes;

import org.graphwalker.java.annotation.Model;
import org.graphwalker.java.annotation.Vertex;
import org.graphwalker.java.annotation.Edge;

@Model(file = "./ProjetoPathModel.json")
public interface ProjetoPathModel {

    @Vertex()
    void v_ExcecaoIllegalArgument();

    @Edge()
    void e_AdicionaNoEAresta();

    @Edge()
    void e_Reinicia();

    @Vertex()
    void v_Inicio();

    @Edge()
    void e_RedefineRaiz();

    @Vertex()
    void v_CaminhoComArestas();

    @Edge()
    void e_AdicionaAresta();

    @Edge()
    void e_AdicionaArestaAntesDaRaiz();

    @Edge()
    void e_DefineRaiz();

    @Edge()
    void e_CriaCaminho();

    @Vertex()
    void v_CaminhoComRaiz();

    @Edge()
    void e_AdicionaNoNaoPresenteEAresta();

    @Vertex()
    void v_CaminhoVazio();

    @Edge()
    void e_RemoveNo();

    @Edge()
    void e_RemoveAresta();

    @Edge()
    void e_AdicionaNoFonteDiferenteUltimoNoDestino();

    @Edge()
    void e_AdicionaNoNuloEAresta();
}
