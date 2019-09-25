package t1cc2;

public class EntradaTabelaDeSimbolos {
    private String nome, tipo;
    private int tipoEntrada; // 1=var, 2=registro, 
    private TabelaDeSimbolos subTabela;
    
    public EntradaTabelaDeSimbolos(String nome, String tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }
    
    public String getNome() {
        return nome;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    @Override
    public String toString() {
        return nome+"("+tipo+")";
    }
}
