/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package t1cc2;

/**
 *
 * @author daniellucredio
 */
public class LASemanticAnalyzer extends LABaseVisitor<Void> {
    // Não esqueça de colocar os RAs do seu grupo na variável a seguir    
    public static String grupo = "<726588, 726523>"; 

    PilhaDeTabelas pilhaDeTabelas = new PilhaDeTabelas();

    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("global"));
        
        // A chamada a seguir invoca o comportamento padrão,
        // que é o de visitar todos os filhos
        super.visitPrograma(ctx);
        // Também poderia substituir por uma chamada específica a
        // outro visitante, como a seguir:
        //visitTrecho(ctx.trecho());
        // Neste caso, é preciso especificar o contexto específico
        // do visitante (trecho)


        // Cuidado para lembrar de inserir corretamente as chamadas
        // dos visitantes a seguir, pois no padrão Visitor do ANTLR,
        // a visitação deve ser explicitamente controlada pelo programador.

        pilhaDeTabelas.desempilhar();
        
        // Deve haver "return null" no final de cada método, devido
        // à verificação de tipos genéricos do Java. Como não estamos
        // utilizando tipo de retorno, não é necessário.
        return null;
    }
    
    //Visita a atribuição passando primeiro pela expressão e depois pela variável onde o resultado será salvo
    
    @Override
    public Void visitDeclaracoes(LAParser.DeclaracoesContext ctx){
        visitChildren(ctx);
        return null;
    }
  @Override
    public Void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx){
        visitIdentificador(ctx.identificador());
        visitExpressao(ctx.expressao());
        return null;
    }
    /*
    //Visita a exrpessão, usada geralmente em uma atribuição ou um context de 'for'
   @Override
    public Void visitListaexp(LAParser.ListaexpContext ctx){
        visitChildren(ctx);
        return null;
    }
    
    //Adiciona ao escopo as variaǘeis que ainda não foram adicionadas
    @Override
    public Void visitListavar(LAParser.ListavarContext ctx){
        for (String var : ctx.nomes){
            if(!pilhaDeTabelas.existeSimbolo(var)){
                pilhaDeTabelas.topo().adicionarSimbolo(var, "variavel");
            }
        }
        return null;
   
    }
    
    // Visita uma Função empilhando uma nova tabela de simbolos para ser seu escopo
    @Override
    public Void visitComandoFunction(LAParser.ComandoFunctionContext ctx){
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos (ctx.nomedafuncao().nome));
        visitNomedafuncao(ctx.nomedafuncao());
         if(ctx.nomedafuncao().metodo){
            pilhaDeTabelas.topo().adicionarSimbolo("self", "parametro");
        }
        visitCorpodafuncao(ctx.corpodafuncao());
        
        return null;
    }
    
    // Empilha os nomes no escopo em questão
      @Override
    public Void visitListaParListaDeNomes(LAParser.ListaParListaDeNomesContext ctx){
        pilhaDeTabelas.topo().adicionarSimbolos(ctx.listadenomes().nomes, "parametro");
        return null;
    } 
    
    //Visita os filhos da função e depois desempilha o escopo da função
    @Override
    public Void visitCorpodafuncao(LAParser.CorpodafuncaoContext ctx){
        visitChildren(ctx);
        pilhaDeTabelas.desempilhar();
        return null;
    }
    
    /*Visita o comando for. Primeiro empilha uma tabela de simbolos para ser o escopo desse for, depois adiciona a esse escopo o contador do for.
    Visita os filhos  desse context e ao final, desempilha o escopo*/
    /*@Override
    public Void visitComandoFor1(LAParser.ComandoFor1Context ctx){
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos ("for"));
        pilhaDeTabelas.topo().adicionarSimbolo(ctx.NOME().getText(), "variavel");
        visitChildren(ctx);
        pilhaDeTabelas.desempilhar();
        return null;
    }
    /*Mesmo comportamento da função acima, mas agora com o adicional de que variáveis declaradas e atribuidas na expressão do for agora são adicionadas ao escopo
    do for em questão*/
    /*public Void visitComandoFor2(LAParser.ComandoFor2Context ctx){
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("for"));
        pilhaDeTabelas.topo().adicionarSimbolos(ctx.listadenomes().nomes, "variavel");
       visitListadenomes( ctx.listadenomes());
       visitListaexp(ctx.listaexp()); 
       pilhaDeTabelas.desempilhar();
        return null;
    }
    
    //Visita a atribuição de variavei sobreposta com a atribuição 'local' e a adiciona no escopo em questão
    public Void visitComandoLocalAtribuicao(LAParser.ComandoLocalAtribuicaoContext ctx){
        visitListaexp(ctx.listaexp());
        pilhaDeTabelas.topo().adicionarSimbolos(ctx.listadenomes().nomes, "variavel");
        visitListadenomes(ctx.listadenomes());
        return null;
    }*/
 
 
}
