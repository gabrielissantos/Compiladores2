package t1cc2;

import java.util.LinkedList;
import java.util.List;

public class PilhaDeTabelas {

    private LinkedList<TabelaDeSimbolos> pilha;
    private List<EntradaTabelaDeSimbolos> simbolos;

    public PilhaDeTabelas() {
        pilha = new LinkedList<TabelaDeSimbolos>();
    }

    public void empilhar(TabelaDeSimbolos ts) {
        pilha.push(ts);
    }

    public TabelaDeSimbolos topo() {
        return pilha.peek();
    }

    public boolean existeSimbolo(String nome) {
        for (TabelaDeSimbolos ts : pilha) {
            if (ts.existeSimbolo(nome)) {
                return true;
            }
        }
        return false;
    }

    public void desempilhar() {
        TabelaDeSimbolos ret = pilha.pop();
        Saida.println(ret.toString());
    }

    public List getTodasTabelas() {
        return pilha;
    }

    //new
    public void adicionaSimboloTopo(EntradaTabelaDeSimbolos entrada) {
        this.topo().adicionarSimbolo(entrada.getNome(), entrada.getTipo());
    }

    public void adicionaSimboloTopo(String nome, String tipo) {
        this.topo().adicionarSimbolo(nome, tipo);
    }

    public String getSimboloTipo(String nome) {
        for (TabelaDeSimbolos tabela : pilha) {
            if (tabela.existeSimbolo(nome)) {
                return tabela.getTipo(nome);
            }
        }
        return "falso";
    }
;
;
}
