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
    SaidaParser out;

    public LASemanticAnalyzer(SaidaParser out) {
        this.out = out;
    }

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

    /*ERROS ENCONTRADOS 
    * 01. OK tipo não declarado e identificador nao declarado
    * 02. OK identificador não declarado
    * 03. identificador ja declarado e identificador ja declarado   
    * 04. atribuicao nao compativel, fim da compilacao
    * 05. OK identificador ja declarado, fim da compilacao
    * 06. atribuicao nao compativel, identificador nao declarado, fim da compilacao
    * 07. atribuicao nao compativel, fim da compilacao
    * 08. tipo nao declarado, atribuicao nao compativel e fim da compilacao
    * 09. atribuicao nao compativel, identificador nao declarado, fim da compilacao
    * 10. atribuicao nao compativel e fim da compilacao
    * 11. atribuicao nao compativel e fim da compilacao
    * 12. identificador ja declarado, identificador nao declarado e fim da compilacao 
    * 13. incompatibilidade de parametros na chamada e fim da compilacao
    * 14. atribuicao nao compativel, identificador nao declarado e dim da compilacao 
    * 15. comando retorne nao permitido nesse escopo e fim da compilacao
    * 16. comando retorne nao permitido nesse escopo e fim da compilacao
    * 17. identificador ja declarado e fim da compilacao
    * 18. identificador ja declarado e fim da compilacao
     */
 /*@Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        if (ctx.variavel() != null) {
            String tipo = pegarTipo(ctx.variavel().tipo());
            ctx.variavel().identificador().forEach((id) -> {
                pilhaDeTabelas.topo().adicionarSimbolo(id.IDENT(0).getText(), tipo);
            });
        }

        return super.visitDeclaracao_local(ctx); //To change body of generated methods, choose Tools | Templates.
    }*/
    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        int tam = ctx.variavel().identificador().size();
        //System.out.println("tam: " + tam);
        String tipo = ctx.variavel().tipo().getText();
        for (int i = 0; i < tam; i++) {
            String identificador = ctx.variavel().identificador().get(i).IDENT(0).getText();
            System.out.println(identificador + ": " + tipo);
            if (pilhaDeTabelas.existeSimbolo(identificador)) {
                out.println("Linha " + ctx.variavel().getStart().getLine() + ": identificador " + identificador + " ja declarado anteriormente");

            } else {
                //pilhaDeTabelas.topo().adicionarSimbolo(identificador, tipo);
                //System.out.println("Ola, entrei aqui");
                if (ctx.variavel().tipo().registro() != null) {
                    //System.out.println("Ola, rsrs");
                    //tipo = ctx.variavel().tipo().registro().getText();
                    pilhaDeTabelas.topo().adicionarSimbolo(identificador, tipo);

                } else if (ctx.variavel().tipo().tipo_estendido() != null) {
                    //tipo = ctx.variavel().tipo().tipo_estendido().tipo_basico_ident().tipo_basico().getText();
                    //System.out.println("tipo: " + tipo);
                    if (tipo.equalsIgnoreCase("literal") || tipo.equalsIgnoreCase("inteiro") || tipo.equalsIgnoreCase("real") || tipo.equalsIgnoreCase("logico")) {
                        //tipo = ctx.variavel().tipo().tipo_estendido().tipo_basico_ident().tipo_basico().getText();
                        //System.out.println("ADICIONANDO NA TABELA...");
                        pilhaDeTabelas.topo().adicionarSimbolo(identificador, tipo);
                        //System.out.println("Pilha de tabelas: " + pilhaDeTabelas.getTodasTabelas());
                        //System.out.println("Passei por aqui " + i + " vezes");

                    } else {
                        //System.out.println("tipo " + tipo + " nao declarado");
                        //GETLINE TA PEGANDO A LINHA ERRADA
                        out.println("Linha " + ctx.variavel().getStart().getLine() + ": tipo " + tipo + " nao declarado");

                    }
                }
            }
        }

        return null;
    }

    //Se eh atribuido um valor a uma variavel nao declarada
    @Override
    public Void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        String nomeVar = ctx.identificador().IDENT(0).getText();
        if (!pilhaDeTabelas.existeSimbolo(nomeVar)){
            out.println("Linha " + ctx.start.getLine() + ": identificador " + nomeVar + " nao declarado");
            return null;
        }
        else{
            String tipo = pilhaDeTabelas.topo().getTipo(nomeVar);
            System.out.println("CONSEGUI PEGAR O TIPO E EH: " + tipo);
            
            System.out.println("Expressao: " + ctx.expressao().getText());
            
            
            }
        
        
        /*if (!pilhaDeTabelas.existeSimbolo(nomeVar)) {
            out.println("Identificador nao declarado: " + nomeVar);
        }*/
        return null;
    }
    
    //se ocorre uma tentativa de ler uma variavel nao declarada
    @Override
    public Void visitCmdLeia(LAParser.CmdLeiaContext ctx) {
        int tam = ctx.identificador().size();
        String nomeVar = ctx.identificador().get(--tam).getText();
        if (!pilhaDeTabelas.existeSimbolo(nomeVar)) {
            out.println("Linha " + ctx.start.getLine() + ": identificador " + nomeVar + " nao declarado");
        }

        return null;
    }

    /*@Override
    public Void visitCmdEscreva(LAParser.CmdEscrevaContext ctx) {
        int tam = ctx.expressao().size();
        System.out.println("o tam da expressao eh: " + tam);
        String nomeVar = ctx.expressao().get(--tam).getText();
        System.out.println(nomeVar);
        
        pilhaDeTabelas.topo().
        
        nomeVar.contains(pilhaDeTabelas.existeSimbolo(nomeVar));
        
        if (!pilhaDeTabelas.existeSimbolo(nomeVar)) {
            int linha = ctx.start.getLine() + 1;
            
            out.println("Linha " + linha + ": identificador " + nomeVar + " nao declarado");
            
        }

        return null;
    }*/

    /*@Override
    public Void visitIdentificador(LAParser.IdentificadorContext ctx) {
        // identificador : IDENT ('.'  IDENT)* dimensao; 
        String nome = ctx.IDENT().get(0).getText();
        for (int i = 1; i < ctx.IDENT().size(); i++) {
            nome += "." + ctx.IDENT().get(i).getText();
        }
        if (!pilhaDeTabelas.topo().existeSimbolo(nome)) {
            out.println("Linha " + ctx.start.getLine() + ": identificador " + nome + " nao declarado");
        }
        return null;
    }
    //metodos auxiliares
    private String pegarTipo(LAParser.TipoContext tipo) {
        if (tipo.registro() != null) {
            return tipo.registro().getText();
        } else if (tipo.tipo_estendido() != null) {
            return tipo.tipo_estendido().tipo_basico_ident().tipo_basico().getText();
        }
        return "";
    }*/
}
