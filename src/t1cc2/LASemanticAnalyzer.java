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
    * 09. OK atribuicao nao compativel, identificador nao declarado, fim da compilacao
    * 10. OK atribuicao nao compativel e fim da compilacao
    * 11. OK atribuicao nao compativel e fim da compilacao
    * 12. identificador ja declarado, identificador nao declarado e fim da compilacao 
    * 13. incompatibilidade de parametros na chamada e fim da compilacao
    * 14. atribuicao nao compativel, identificador nao declarado e dim da compilacao 
    * 15. comando retorne nao permitido nesse escopo e fim da compilacao
    * 16. comando retorne nao permitido nesse escopo e fim da compilacao
    * 17. identificador ja declarado e fim da compilacao //declaracao de registro e procedimento
    * 18. OK identificador ja declarado e fim da compilacao
     */
    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        //  declaracao_local : 'declare' variavel
        if (ctx.variavel() != null) {
            int tam = ctx.variavel().identificador().size();
            String tipo = ctx.variavel().tipo().getText();
            for (int i = 0; i < tam; i++) {
                String identificador = ctx.variavel().identificador().get(i).IDENT(0).getText();
                if (pilhaDeTabelas.existeSimbolo(identificador)) { //se o identificador já está na pilha de tabelas
                    out.println("Linha " + ctx.variavel().identificador(i).getStart().getLine() + ": identificador " + identificador + " ja declarado anteriormente");
                } else {
                    if (ctx.variavel().tipo().registro() != null) { //se o tipo for um registro
                        int tamIdentRegistro = ctx.variavel().tipo().registro().variavel().get(0).identificador().size();
                        for (int j = 0; j < tamIdentRegistro; j++) {
                            String nomeregistro = identificador + "." + ctx.variavel().tipo().registro().variavel().get(0).identificador().get(j).getText();
                            pilhaDeTabelas.topo().adicionarSimbolo(nomeregistro, ctx.variavel().tipo().registro().variavel().get(0).tipo().getText());
                        }
                    } else if (ctx.variavel().tipo().tipo_estendido() != null) { //se o tipo for um tipo estendido
                        if (tipo.equalsIgnoreCase("literal") || tipo.equalsIgnoreCase("inteiro") || tipo.equalsIgnoreCase("real") || tipo.equalsIgnoreCase("logico")) {
                            pilhaDeTabelas.topo().adicionarSimbolo(identificador, tipo);
                        } else if (tipo.equalsIgnoreCase("^literal") || tipo.equalsIgnoreCase("^inteiro") || tipo.equalsIgnoreCase("^real") || tipo.equalsIgnoreCase("^logico")) {
                            pilhaDeTabelas.topo().adicionarSimbolo(identificador, tipo);
                        } else {
                            out.println("Linha " + ctx.variavel().getStart().getLine() + ": tipo " + tipo + " nao declarado");
                        }
                    }
                }
            }
        }
        //  declaracao_local : 'constante' IDENT ':' tipo_basico '=' valor_constante
        if (ctx.valor_constante() != null) {
            if (!pilhaDeTabelas.existeSimbolo(ctx.IDENT().getText())) { //se a constante nao existir na tabela
                pilhaDeTabelas.topo().adicionarSimbolo(ctx.IDENT().getText(), ctx.tipo_basico().getText()); //adiciona a constante na tabela
            }
        }
        // declaracao_local : 'tipo' IDENT ':' tipo
        /*if (ctx.tipo() != null) {
            if (!pilhaDeTabelas.topo().existeSimbolo(ctx.IDENT().getText())) {
                pilhaDeTabelas.topo().adicionarSimbolo(ctx.IDENT().getText(), ctx.tipo().getText());
                System.out.println(ctx.IDENT().getText());
                System.out.println(ctx.tipo().getText());
            }
        }*/
        return null;
    }

    @Override
    public Void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        String textoidentificador = ctx.identificador().getText();

        //se o identificador não está na tabela de simbolos
        if (!pilhaDeTabelas.existeSimbolo(textoidentificador)) {
            out.println("Linha " + ctx.start.getLine() + ": identificador " + textoidentificador + " nao declarado");
            return null;
        } else {

            //pegando o tipo do identificador
            String tipoidentificador = pilhaDeTabelas.topo().getTipo(textoidentificador);

            //pegando o tamanho do termo pra saber em quantas vezes ele esta dividido
            int termo = 1; //caso a expressao nao seja uma expressao aritmetica
            String textoexpressao = ctx.expressao().getText(); //pegando texto da expressao completa
            if (textoexpressao.contains("+")) {
                termo = ctx.expressao().termo_logico().get(0).fator_logico().get(0).parcela_logica().exp_relacional().exp_aritmetica().get(0).termo().size();
            }

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
                    } else if (tipoidentificador.equals("real")) {
                        if (textotermo.contains("\"")) {
                            out.println("Linha " + linha + ": atribuicao nao compativel para " + textoidentificador);
                        }
                    } else if (tipoidentificador.equals("^inteiro")) { //ponteiro do tipo inteiro
                        if (textotermo.contains("\"")) {
                            out.println("Linha " + linha + ": atribuicao nao compativel para ^" + textoidentificador);
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
        for (int i = 0; i < tam; i++) {
            String nomeVar = ctx.identificador().get(i).getText();
            if (!pilhaDeTabelas.existeSimbolo(nomeVar)) {
                out.println("Linha " + ctx.start.getLine() + ": identificador " + nomeVar + " nao declarado");
            }
        }
        return null;
    }

    @Override
    public Void visitCmdEscreva(LAParser.CmdEscrevaContext ctx
    ) {
        int tam = ctx.expressao().size() - 1; //tamanho da expressao

        //quantidade de termos da expressao
        int termo = ctx.expressao().get(tam).termo_logico().get(0).fator_logico().get(0).parcela_logica().exp_relacional().exp_aritmetica().get(0).termo().size();
        int fator = ctx.expressao().get(tam).termo_logico().get(0).fator_logico().get(0).parcela_logica().exp_relacional().exp_aritmetica().get(0).termo().get(0).fator().size();

        //o laço percorre cada termo da expressao
        if (fator > 1) {
            for (int i = 0; i < fator; i++) {
                //pega o texto contido no fator
                String textofator = ctx.expressao().get(tam).termo_logico().get(0).fator_logico().get(0).parcela_logica().exp_relacional().exp_aritmetica().get(0).termo().get(0).fator().get(i).getText();

                //se o fator nao estiver contido em aspas
                if (!textofator.contains("\"")) {
                    //se o fator nao estiver na tabela de simbolos
                    if (!pilhaDeTabelas.existeSimbolo(textofator)) {
                        //se o fator nao for um numero
                        if (!textofator.startsWith("0") & !textofator.startsWith("1") & !textofator.startsWith("2") & !textofator.startsWith("3") & !textofator.startsWith("4") & !textofator.startsWith("5") & !textofator.startsWith("6") & !textofator.startsWith("7") & !textofator.startsWith("8") & !textofator.startsWith("9")) {
                            out.println("Linha " + ctx.expressao().get(1).termo_logico().get(0).fator_logico().get(0).parcela_logica().exp_relacional().exp_aritmetica().get(0).termo().get(0).fator().get(i).getStart().getLine() + ": identificador " + textofator + " nao declarado");
                        }

                    }
                }
            }
        } else {
            for (int i = 0; i < termo; i++) {
                //pega o texto contido no termo
                String textotermo = ctx.expressao().get(tam).termo_logico().get(0).fator_logico().get(0).parcela_logica().exp_relacional().exp_aritmetica().get(0).termo().get(i).getText();

                //se o termo nao estiver contido em aspas
                if (!textotermo.contains("\"")) {
                    //se o termo nao estiver na tabela de simbolos
                    if (!pilhaDeTabelas.existeSimbolo(textotermo)) {
                        out.println("Linha " + ctx.expressao().get(1).termo_logico().get(0).fator_logico().get(0).parcela_logica().exp_relacional().exp_aritmetica().get(0).termo().get(i).getStart().getLine() + ": identificador " + textotermo + " nao declarado");
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Void visitCmdEnquanto(LAParser.CmdEnquantoContext ctx) {
        int tamfator_logico = ctx.expressao().termo_logico().get(0).fator_logico().size();
        int tamexp_aritmetica = ctx.expressao().termo_logico().get(0).fator_logico().get(0).parcela_logica().exp_relacional().exp_aritmetica().size();
        for (int i = 0; i < tamfator_logico; i++) {
            for (int j = 0; j < tamexp_aritmetica; j++) {
                String texto = ctx.expressao().termo_logico().get(0).fator_logico().get(i).parcela_logica().exp_relacional().exp_aritmetica().get(j).getText();
                if (!texto.contains("\"")) {
                    if (!pilhaDeTabelas.existeSimbolo(texto)) {
                        if (!texto.startsWith("0") & !texto.startsWith("1") & !texto.startsWith("2") & !texto.startsWith("3") & !texto.startsWith("4") & !texto.startsWith("5") & !texto.startsWith("6") & !texto.startsWith("7") & !texto.startsWith("8") & !texto.startsWith("9")) {
                            out.println("Linha " + ctx.expressao().termo_logico().get(0).fator_logico().get(i).parcela_logica().exp_relacional().exp_aritmetica().get(j).getStart().getLine() + ": identificador " + texto + " nao declarado");
                        }
                    }
                }

            }

        }

        return null;
    }

    /*@Override
    public Void visitExpressao(LAParser.ExpressaoContext ctx){
        System.out.println("oi " + ctx.termo_logico().get(0).getText());
        return null;
    }*/
}
