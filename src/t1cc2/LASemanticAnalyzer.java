/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package t1cc2;

import java.util.List;

public class LASemanticAnalyzer extends LABaseVisitor<Void> {

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
    * 03. OK identificador ja declarado e identificador ja declarado   
    * 04. OK atribuicao nao compativel, fim da compilacao
    * 05. OK identificador ja declarado, fim da compilacao
    * 06. OK atribuicao nao compativel, identificador nao declarado, fim da compilacao
    * 07. OK atribuicao nao compativel, fim da compilacao
    * 08. OK tipo nao declarado, atribuicao nao compativel e fim da compilacao
    * 09. atribuicao nao compativel, identificador nao declarado, fim da compilacao
    * 10. atribuicao nao compativel e fim da compilacao
    * 11. atribuicao nao compativel e fim da compilacao
    * 12. identificador ja declarado, identificador nao declarado e fim da compilacao 
    * 13. incompatibilidade de parametros na chamada e fim da compilacao
    * 14. atribuicao nao compativel, identificador nao declarado e dim da compilacao 
    * 15. comando retorne nao permitido nesse escopo e fim da compilacao
    * 16. comando retorne nao permitido nesse escopo e fim da compilacao
    * 17. identificador ja declarado e fim da compilacao
    * 18. OK identificador ja declarado e fim da compilacao
     */
    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        //  declaracao_local : 'declare' variavel
        if (ctx.variavel() != null) {
            int tam = ctx.variavel().identificador().size();
            System.out.println("EH VARIAVEL");
            String tipo = ctx.variavel().tipo().getText();
            for (int i = 0; i < tam; i++) {
                String identificador = ctx.variavel().identificador().get(i).IDENT(0).getText();
                //System.out.println(identificador + ": " + tipo);
                if (pilhaDeTabelas.existeSimbolo(identificador)) {
                    out.println("Linha " + ctx.variavel().identificador(i).getStart().getLine() + ": identificador " + identificador + " ja declarado anteriormente");

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
        }

        if (ctx.valor_constante() != null) { //  declaracao_local : 'constante' IDENT ':' tipo_basico '=' valor_constante
            if (!pilhaDeTabelas.existeSimbolo(ctx.IDENT().getText())) { //se a constante nao existir na tabela
                pilhaDeTabelas.topo().adicionarSimbolo(ctx.IDENT().getText(), ctx.tipo_basico().getText()); //adiciona a constante na tabela
            }
        }

        if (ctx.tipo() != null) {
            System.out.println("tem registro no codigo " + ctx.IDENT().getText());
            if (!pilhaDeTabelas.topo().existeSimbolo(ctx.IDENT().getText())) {
                pilhaDeTabelas.topo().adicionarSimbolo(ctx.IDENT().getText(), ctx.tipo().getText());
                //System.out.println("Pilha de tabelas: " + pilhaDeTabelas.getTodasTabelas());
                System.out.println(ctx.IDENT().getText());
                System.out.println(ctx.tipo().getText());
            }

        }
        return null;
    }

    //Se eh atribuido um valor a uma variavel nao declarada
    @Override
    public Void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        String textoidentificador = ctx.identificador().IDENT(0).getText();
        if (!pilhaDeTabelas.existeSimbolo(textoidentificador)) {
            out.println("Linha " + ctx.start.getLine() + ": identificador " + textoidentificador + " nao declarado");
            return null;
        } else {

            //pegando o tipo do identificador
            String tipoidentificador = pilhaDeTabelas.topo().getTipo(textoidentificador);
            //System.out.println("o tipo do identificador eh: " + tipoidentificador);

            //pegando o tamanho do termo pra saber em quantas vezes ele esta dividido
            int termo = 1; //caso a expressao nao seja uma expressao aritmetica
            String textoexpressao = ctx.expressao().getText(); //pegando texto da expressao completa
            if (textoexpressao.contains("+")) {
                termo = ctx.expressao().termo_logico().get(0).fator_logico().get(0).parcela_logica().exp_relacional().exp_aritmetica().get(0).termo().size();
            }

            //System.out.println("O tamanho do termo eh: " + termo);
            for (int i = 0; i < termo; i++) {
                String textotermo;
                String tipotermo;
                int linha; //para a funcao getLine()
                //pegando cada parte do termo da expressao
                if (termo != 1) { //se for uma expressao com mais de um termo
                    textotermo = ctx.expressao().termo_logico().get(0).fator_logico().get(0).parcela_logica().exp_relacional().exp_aritmetica().get(0).termo().get(i).getText();
                    linha = ctx.expressao().termo_logico().get(0).fator_logico().get(0).parcela_logica().exp_relacional().exp_aritmetica().get(0).termo().get(i).getStart().getLine();
                } else { //expressao com um unico termo
                    textotermo = ctx.expressao().getText();
                    linha = ctx.expressao().getStart().getLine();
                }
                //identificando o tipo de cada termo
                tipotermo = pilhaDeTabelas.topo().getTipo(textotermo);
                if (tipotermo != tipoidentificador) {
                    //se o tipo do identificador for literal
                    if (tipoidentificador.equals("literal")) {
                        //se o texto nao estiver entre " "
                        if (!textotermo.contains("\"")) {
                            out.println("Linha " + linha + ": atribuicao nao compativel para " + textoidentificador);
                        }
                    } else if (tipoidentificador.equals("logico")) {
                        if (textotermo.contains("\"")) {
                            out.println("Linha " + linha + ": atribuicao nao compativel para " + textoidentificador);
                        }
                    }
                }
            }
        }
        return null;
    }

    //se ocorre uma tentativa de ler uma variavel nao declarada
    @Override
    public Void visitCmdLeia(LAParser.CmdLeiaContext ctx
    ) {
        int tam = ctx.identificador().size();
        String nomeVar = ctx.identificador().get(--tam).getText();
        if (!pilhaDeTabelas.existeSimbolo(nomeVar)) {
            out.println("Linha " + ctx.start.getLine() + ": identificador " + nomeVar + " nao declarado");
        }

        return null;
    }

    @Override
    public Void visitCmdEscreva(LAParser.CmdEscrevaContext ctx
    ) {
        int tam = ctx.expressao().size(); //tamanho da expressao
        //System.out.println("o tam do termo logico eh: " + tam);
        String nomeVar = ctx.expressao().get(--tam).termo_logico(0).getText(); //nome do ultimo item da lista de expressoes
        //System.out.println(nomeVar);

        //quantidade de termos da expressao
        int termo = ctx.expressao().get(tam).termo_logico().get(0).fator_logico().get(0).parcela_logica().exp_relacional().exp_aritmetica().get(0).termo().size();
        //System.out.println("O tamanho do termo eh: " + termo);

        //o laço percorre cada termo da expressao
        for (int i = 0; i < termo; i++) {
            //pega o texto contido no termo
            String textotermo = ctx.expressao().get(tam).termo_logico().get(0).fator_logico().get(0).parcela_logica().exp_relacional().exp_aritmetica().get(0).termo().get(i).getText();
            //System.out.println("texto do termo: " + textotermo);

            //se o termo nao estiver contido em aspas
            if (!textotermo.contains("\"")) {
                //se o termo nao estiver na tabela de simbolos
                if (!pilhaDeTabelas.existeSimbolo(textotermo)) {
                    out.println("Linha " + ctx.expressao().get(1).termo_logico().get(0).fator_logico().get(0).parcela_logica().exp_relacional().exp_aritmetica().get(0).termo().get(i).getStart().getLine() + ": identificador " + textotermo + " nao declarado");
                }
            }

        }
        return null;
    }
}
