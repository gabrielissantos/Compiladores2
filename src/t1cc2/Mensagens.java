package t1cc2;

public class Mensagens {
    public static void erroVariavelNaoExiste(int numLinha, int numColuna, String variavel) {
        Saida.println(numLinha+","+(numColuna+1)+":Variavel "+variavel+" nao amarrada");
    }
}
