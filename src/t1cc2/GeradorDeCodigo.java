package t1cc2;

import t1cc2.LAParser.IdentificadorContext;

/**
 *
 * @author rodrigo
 */
public class GeradorDeCodigo extends LABaseVisitor<String> {

    //declarações iniciais
    //constantes
    private static final String TAB = "\t"; // String para tab-space
    private static final String BLANK = " "; // String para espaço em branco
    private static final String END_CMD_LINE = ";\n"; // String para pular linhas

    //variaveis
    private SaidaParser out;
    private boolean estaVisitandoRegistro; // Variável que indica se visitRegistro foi chamada na execução
    private StringBuilder CodigoC; // Estrutura para armazenar o código em C a ser escrito
    private String bufferTipoExp; // Armazena tipo que a expressao retorna
    private PilhaDeTabelas pilhaDeTabelas; // Pilha de tabelas de escopo
    private TabelaDeSimbolos declAtributosStruct; // Tabela temporária para variáveis de uma struct

    /**
     * GeradorDeCodigo (construtor) Recebe um objeto SaidaParser para escrita do
     * código C
     *
     */
    public GeradorDeCodigo(SaidaParser out) {
        this.out = out;
        bufferTipoExp = "inteiro";
        CodigoC = new StringBuilder();
        estaVisitandoRegistro = false;
        declAtributosStruct = new TabelaDeSimbolos("struct");
    }// fim construtor

    // Método para transformar os tipos de variável em tipos em C
    /**
     * tipoParaC Dado um tipo em ALG, retornar tipo em C
     *
     */
    private String tipoParaC(String tipo) {
        String ponteiro = "";
        if (tipo != null) {
            if (tipo.startsWith("^")) {
                tipo = tipo.replace("^", "");
                ponteiro = "*";
            }
            if (tipo.startsWith("struct")) {
                return tipo;
            }
            String novoTipo;
            switch (tipo) {
                case "literal":
                    novoTipo = "char*";
                    break;
                case "inteiro":
                case "logico":
                    novoTipo = "int";
                    break;
                case "real":
                    novoTipo = "float";
                    break;
                default:
                    novoTipo = tipo;
            }
            return novoTipo + ponteiro;
        } else {
            return null;
        }
    } // fim tipoParaC

    /**
     * tipoParaFlagC Dado um tipo, transformar em flag de tipo para cadeia de
     * caracteres
     *
     */
    private String tipoParaFlagC(String tipo) {
        String flag;
        switch (tipo) {
            case "literal":
            case "char*":
                flag = "%s";
                break;
            case "inteiro":
            case "int":
            case "logico":
                flag = "%d";
                break;
            case "real":
            case "float":
                flag = "%f";
                break;
            default:
                flag = "";
        }
        return flag;
    } // fim tipoParaFlag

    /* -----------------------------------------------------------------------
    * Sobrecarga de operadores do visitor
    * Alguns métodos retornam String, por escolha de implementação.
    * -------------------------------------------------------------------------*/
    @Override
    public String visitPrograma(LAParser.ProgramaContext ctx) {
        pilhaDeTabelas = new PilhaDeTabelas(); // inicia a pilha de tabelas de símbolos
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("global")); // empilha-se a tabela de escopo global
        // Escreve no código em C o início dos programas
        CodigoC.append(
                // início dos programas em C segundo os casos de teste
                "#include<stdio.h>\n"
                + "#include<stdlib.h>\n\n"
        );
        // visitChildren(ctx): chamada de visits de regras derivadas.
        visitDeclaracoes(ctx.declaracoes());
        visitCorpo(ctx.corpo());

        // Escreve o código no SaidaParser
        out.println(CodigoC.toString());

        // Desempilha a tabela de símbolos de escopo global
        pilhaDeTabelas.desempilhar();
        return CodigoC.toString();
    } // fim visitPrograma

    @Override
    public String visitDeclaracoes(LAParser.DeclaracoesContext ctx) {
        if (!ctx.decl_local_global().isEmpty()) {
            for (LAParser.Decl_local_globalContext dlg : ctx.decl_local_global()) {
                visitDecl_local_global(dlg);
            }
        }

        return null;
    } // fim visitDeclaracoes

    @Override
    public String visitCorpo(LAParser.CorpoContext ctx) {
        // Cria estrutura para armazenar múltiplas Declarações locais
        StringBuilder bufferDL = new StringBuilder();

        // Adiciona na estrutura as declarações locais
        if (ctx.cmd() != null) {
            for (LAParser.Declaracao_localContext dl : ctx.declaracao_local()) {
                bufferDL.append(TAB).append(visitDeclaracao_local(dl));
            }
        }

        // Cria estrutura para armazenar múltiplos comandos
        StringBuilder bufferCmd = new StringBuilder();

        // Adiciona na estrutura os comandos
        if (ctx.cmd() != null) {
            for (LAParser.CmdContext c : ctx.cmd()) {
                bufferCmd.append(TAB).append(visitCmd(c));
            }
        }

        // Escreve as declarações locais no código em C
        CodigoC.append("int main () {\n").append(bufferDL.toString())
                .append(bufferCmd.toString()).append(TAB).append("return 0").append(END_CMD_LINE)
                .append("}\n");

        // Esse método não precisa retornar nada, pois é apenas chamado. Trata-se de um procedimento.
        return null;
    }// fim visitCorpo

    @Override
    public String visitVariavel(LAParser.VariavelContext ctx) {
        String tipo; // Armazenar o tipo da variável
        // Cria estrutura para armazenar o conteúdo da declaração de variável em C
        StringBuilder buffer = new StringBuilder();

        // Se o tipo não for registro
        if (!ctx.getText().contains("registro")) {
            // Converte o tipo em LA para C
            tipo = tipoParaC(visitTipo(ctx.tipo()));
            if (!ctx.identificador1.isEmpty()) {
                // Salva a string do identificador
                String id = visitIdentificador(ctx.identificador1);
                if (!estaVisitandoRegistro) // Se essa visit não vem de uma chamada recursiva de visitRegistro
                {
                    pilhaDeTabelas.adicionaSimboloTopo(id, tipo); // Adicione o símbolo na tabela
                } else // se vem
                {
                    declAtributosStruct.adicionarSimbolo(id, tipo); // Adicione na tabela temporária
                }
                if (tipo.equals("char*")) // Se o tipo encontrado for string em C (vetor de/ponteiro para char)
                // Atribua como dizem os casos de teste, com tamanho máximo de 80
                {
                    buffer.append(tipo.replace("*", "")).append(BLANK).append(id).append("[80]");
                } else // Se não
                // Adicione normalmente
                {
                    buffer.append(tipo).append(BLANK).append(id);
                }

                if (tipo.equals(tipoParaC(tipo))) { // Caso se trate de um typedef

                    /* Como mostram os casos de teste, isso só ocorre quando há um tipo personalizado de registro
                    * (struct). Portanto, atribua os <id> "." <campos> na tabela de símbolos do escopo
                    * */
                    for (EntradaTabelaDeSimbolos ets : declAtributosStruct.getSimbolos()) {
                        pilhaDeTabelas.adicionaSimboloTopo(id + "." + ets.getNome(), ets.getTipo());
                    }
                }
            }
            if (!ctx.outrosIdentificadores.isEmpty()) // Se há mais de um identificador
            {
                for (LAParser.IdentificadorContext idc : ctx.outrosIdentificadores) { // Para cada contexto de identificador
                    // Guarde seu identificador
                    String id = visitIdentificador(idc);
                    if (!estaVisitandoRegistro) // Se essa visit não vem de uma chamada recursiva de visitRegistro
                    {
                        pilhaDeTabelas.adicionaSimboloTopo(id, tipo); // Adicione o símbolo na tabela
                    } else // se vem
                    {
                        declAtributosStruct.adicionarSimbolo(id, tipo);// Adicione na tabela temporária
                    }
                    // Guarde no buffer separando por vírgula
                    buffer.append(",").append(BLANK).append(id);
                }
            }

        } else { // Se o tipo é registro
            buffer.append(visitTipo(ctx.tipo())); // Escreva-o no buffer
            tipo = "struct"; // Para a tabela de símbolos, é necessário que o tipo seja guardado como struct
            //if (!ctx.identificador1.isEmpty()) { //
            if (!ctx.identificador1.isEmpty()) { //
                // guarda o identificador da struct
                String id = visitIdentificador(ctx.identificador1);
                // Adiciona na tabela o nome da struct
                pilhaDeTabelas.adicionaSimboloTopo(id, tipo);
                // Acumula no buffer o identificador
                buffer.append(id);

                // Para cada estrutura dentro do registro
                for (EntradaTabelaDeSimbolos ets : declAtributosStruct.getSimbolos()) // empilhe na tabela de símbolos <id> "." <estrutura> e seu tipo
                {
                    pilhaDeTabelas.adicionaSimboloTopo(id + "." + ets.getNome(), ets.getTipo());
                }
            }
        }
        // retorne o buffer inteiro
        return buffer.toString();
    }// fim visitVariável

    /**
     * visitValor_constante Tratamento de valores constantes
     *
     * @author João Gabriel Melo Barbirato
     *
     */
    @Override
    public String visitValor_constante(LAParser.Valor_constanteContext ctx) {
        String buffer;
        String cte = ctx.getStart().getText();
        switch (cte) {
            // Troca veradeiro por true, falso por false
            case "verdadeiro":
                buffer = "true";
                break;
            case "falso":
                buffer = "false";
                break;
            // Caso haja outra alternativa, retorne o próprio getText()
            default:
                buffer = cte;
                break;
        }
        return buffer;
    } // fim visitValor_constante

    /**
     * visitDeclaracao_local
     *
     * Visita declarações locais - dentro da função principal do programa
     *
     *
     */
    @Override
    public String visitDeclaracao_local(LAParser.Declaracao_localContext ctx) {
        // Guarda o começo da declaração, onde é possível distinguí-la
        String dlid = ctx.getStart().getText();
        String id, tipo;    // Armazenam ID e Tipo da variável para adição na tabela de símbolos
        String buffer;     // Armazenamento auxiliar de texto
        switch (dlid) {
            // Declaração de variáveis
            case "declare":
                // Guarde o texto da variável
                buffer = visitVariavel(ctx.variavel());

                /*Dados os casos de testes, toda vez que há o tipo "char" trata-se de um vetor de char.
                * Portanto, adicione a dimensão "[80]"
                * */
                if (buffer.startsWith("char")) {
                    buffer += "[80]";
                }

                buffer += END_CMD_LINE;
                break;

            // Declaração de constantes - Trata-se de uma macro #define
            case "constante":
                // guarda o tipo da constante apenas para tabela de símbolos
                tipo = tipoParaC(ctx.tipo_basico().getText());
                id = ctx.IDENT().getText();   // guarda o identificador da constante
                String valorConstante = visitValor_constante(ctx.valor_constante()); // Guarda o valor da constante

                // Adiciona na tabela de símbolos
                pilhaDeTabelas.adicionaSimboloTopo(id, tipo);

                /*Para #define, é necessário que seja inscrita logo após as primeiras linhas do programa.
                * Portanto, escreva no código C e acabe a execução desta chamada.
                * */
                CodigoC.append("#define ").append(id).append(BLANK).append(valorConstante).append("\n\n");
                return null;

            // Declarar tipo - typedef
            case "tipo":
                /*Pelos casos de teste, este caso só será chamado com structs*/
                tipo = tipoParaC(visitTipo(ctx.tipo())); // armazena o tipo para o tipo personalizado
                id = ctx.IDENT().getText();

                // Adiciona-se o id e o tipo struct na tabela de símbolos
                pilhaDeTabelas.adicionaSimboloTopo(id, "struct");

                // Para cada estrutura dentro da struct
                if (!declAtributosStruct.getSimbolos().isEmpty()) {
                    for (EntradaTabelaDeSimbolos ets : declAtributosStruct.getSimbolos()) // Adicione à tabela de símbolos
                    {
                        pilhaDeTabelas.adicionaSimboloTopo(id + "." + ets.getNome(), ets.getTipo());
                    }
                }

                // Adicione ao buffer o texto a ser escrito
                buffer = "typedef " + BLANK + tipo + BLANK + id + END_CMD_LINE;
                break;
            default:
                return null;
        }
        return buffer;
    }// fim visitDeclaracao_local

    /**
     * visitParametros Visita um conjunto de parâmetros de uma função em C. É
     * possível mais de um conjunto de parâmetros devido ao agrupamento do tipo,
     * em .ALG
     *
     */
    @Override
    public String visitParametros(LAParser.ParametrosContext ctx) {
        // Constroi a estrutura que armazena os parâmetros
        StringBuilder buffer = new StringBuilder();
        // Primeiro conjunto de parâmetros
        if (ctx.parametro1 != null) {
            buffer.append(visitParametro(ctx.parametro1));
        }
        if (ctx.outrosParametros != null) // Para cada outros conjuntos de parâmetros
        {
            for (LAParser.ParametroContext p : ctx.outrosParametros) // Separe-os com vírgula
            {
                buffer.append(",").append(visitParametro(p));
            }
        }

        return buffer.toString();
    } // fim visitParametros

    /**
     * visitParametro Visita cada parâmetro do conjunto de parâmetros.
     *
     */
    @Override
    public String visitParametro(LAParser.ParametroContext ctx) {
        /*Armazena tipo,
        * Converte tipo de .ALGL para tipo em C,
        * Converte ponteiro para os padrões da linguagem C
        * */
        String tipo = tipoParaC(ctx.tipo_estendido().getText().replace("^", "*"));
        // cria buffer de armazenamento de texto a ser escrito
        StringBuilder buffer = new StringBuilder();
        if (!ctx.identificador1.isEmpty()) {
            // Guarda o ID
            String id = visitIdentificador(ctx.identificador1);
            // adiciona na tabela de símbolos
            pilhaDeTabelas.adicionaSimboloTopo(id, tipo);
            // Separa o parâmetro para escrita
            buffer.append(tipo).append(BLANK).append(id);
        }

        // Para cada outros identificadores
        if (!ctx.outrosIdentificadores.isEmpty()) {
            for (LAParser.IdentificadorContext i : ctx.outrosIdentificadores) {
                // Guarda o id
                String id = visitIdentificador(i);
                // adiciona na tabela de símbolos
                pilhaDeTabelas.adicionaSimboloTopo(id, tipo);
                // Separa o parâmetro para escrita
                buffer.append("," + BLANK).append(tipo).append(BLANK).append(id);
            }
        }

        // retorna para escrita
        return buffer.toString();
    }// fim visitParametro

    /**
     * visitDeclaracao_global_funcao Visita declaração global de uma função em C
     * (Derivado de declaração global)
     *
     */
    @Override
    public String visitDeclaracao_global_funcao(LAParser.Declaracao_global_funcaoContext ctx) {
        // Guarda nome da função e tipo de retorno
        String nome = ctx.IDENT().toString();
        String tipo = ctx.tipo_estendido().getText().replace("^", "*");
        // Guarda na tabela de símbolos
        pilhaDeTabelas.adicionaSimboloTopo(nome, tipo);
        // Cria uma nova tabela de símbolos para função
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("funcao"));

        // Prepara estrutura que guarda declarações locais
        StringBuilder bufferDL = new StringBuilder();
        if (!ctx.listaComandos.isEmpty()) // Para cada declaração local
        {
            for (LAParser.Declaracao_localContext dl : ctx.listaDL) // Adicione na estrutura separada por espaço
            {
                bufferDL.append(TAB).append(visitDeclaracao_local(dl));
            }
        }

        // Prepara estrutura que guarda comandos
        StringBuilder bufferCmd = new StringBuilder();
        if (!ctx.listaComandos.isEmpty()) // Para cada comando
        {
            for (LAParser.CmdContext c : ctx.listaComandos) // Adicione na estrutura separada por espaço
            {
                bufferCmd.append(TAB).append(visitCmd(c));
            }
        }

        // Escreva o código C
        CodigoC.append(tipoParaC(tipo)).append(BLANK).append(nome).append("(")
                .append(visitParametros(ctx.parametros())).append(")").append("{\n ")
                .append(bufferDL.toString()).append(BLANK).append(bufferCmd.toString())
                .append("}\n\n");
        // Desempilhe a tabela referente ao escopo da função
        pilhaDeTabelas.desempilhar();
        return null;
    }// fim visitDeclaracao_global_funcao

    /**
     * visitDeclaracao_global_procedimento Visita declaração global de um
     * procedimento em C
     *
     */
    @Override
    public String visitDeclaracao_global_procedimento(LAParser.Declaracao_global_procedimentoContext ctx) {
        // Guarda nome da função e tipo de retorno
        String nome = ctx.IDENT().toString();
        String tipo = "void";
        // Guarda na tabela de símbolos
        pilhaDeTabelas.adicionaSimboloTopo(nome, tipo);
        // Cria uma nova tabela de símbolos para o procedimento
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("procedimento"));

        // Prepara estrutura que guarda declarações locais
        StringBuilder bufferDL = new StringBuilder();
        if (!ctx.listaDL.isEmpty()) // Para cada declaração local
        {
            for (LAParser.Declaracao_localContext dl : ctx.listaDL) // Adicione para ser escrito.
            {
                bufferDL.append(TAB).append(visitDeclaracao_local(dl));
            }
        }

        /*Por conta dos parâmetros e declarações adicionados à tabela
        * que podem ser utilizados nos comandos,
        * escreva no código C com chamando o visitParametros
        * */
        CodigoC.append(tipoParaC(tipo)).append(BLANK).append(nome).append("(")
                .append(visitParametros(ctx.parametros()))
                .append(")").append("{\n ").append(bufferDL.toString());

        // Prepara estrutura que guarda comandos
        StringBuilder bufferCmd = new StringBuilder();
        if (!ctx.listaComandos.isEmpty()) // Para cada comando
        {
            for (LAParser.CmdContext c : ctx.listaComandos) // Adicione para ser escrito
            {
                bufferCmd.append(TAB).append(visitCmd(c));
            }
        }

        // Escreva no código C
        CodigoC.append(bufferCmd.toString()).append("}\n");
        // Desempilhe a tabela do procedimento.
        pilhaDeTabelas.desempilhar();
        return null;
    } // fim visitDeclaracao_global_procedimento

    /**
     * visitCmdLeia Visita o comando de leitura de entrada do usuário A função
     * se preocupa em separar as flags de reconhecimento do tipo da escrita e
     * sua respectiva variável.
     *
     */
    @Override
    public String visitCmdLeia(LAParser.CmdLeiaContext ctx) {
        String bufferRetorno;
        // Estrutura de armazenamento de identificadores
        StringBuilder bufferIdent = new StringBuilder("\", &");
        String formato = "";
        String argumentos = "";
        /*Estrutura de armazenamento de flags de tipos,
        * para indicar que tipo de variável a leitura será convertida
        **/
        StringBuilder bufferTipo = new StringBuilder();
        String tipo = null;
        for (IdentificadorContext id : ctx.identificador()) {
            String tipoDeDado = pilhaDeTabelas.getSimboloTipo(id.getText());
            String simbolo = "";
            formato += formatoLA2C(tipoDeDado);
            if (!tipoDeDado.equals("literal")) {
                simbolo += "&";
            }
            simbolo += id.getText();
            argumentos += simbolo + ",";
        }
        if (ctx.id1 != null) {
            String nomeEntrada = visitIdentificador(ctx.id1);
            EntradaTabelaDeSimbolos ets = pilhaDeTabelas.escopoAtual().getEntrada(nomeEntrada);
            bufferIdent.append(nomeEntrada);
            if (ets != null) {
                tipo = tipoParaFlagC(tipoParaC(ets.getTipo()));
                bufferTipo.append(tipo);
            }
        }
        if (ctx.outrosIds != null) {
            for (LAParser.IdentificadorContext ic : ctx.outrosIds) {
                String nomeEntrada = ic.IDENT().toString();
                EntradaTabelaDeSimbolos ets = pilhaDeTabelas.escopoAtual().getEntrada(nomeEntrada);
                if (ets != null) {
                    bufferIdent.append(", &").append(nomeEntrada);
                    tipo = tipoParaFlagC(tipoParaC(ets.getTipo()));
                    bufferTipo.append(BLANK).append(tipo);
                }
            }
        }

        // Se o tipo da variável for do tipo string v(vetor de caracteres)
        if (tipo != null && tipo.equals("%s")) // Trata-se do comando gets()   em C
        {
            bufferRetorno = "gets(" + bufferIdent.toString().replace("\",", "").replace("&", "").replace(" ", "") + ")";
        } else // Trata-se do comando scanf()  em C
        {
            bufferRetorno = "scanf(\"" + bufferTipo.toString() + bufferIdent.toString() + ")";
        }

        return bufferRetorno + END_CMD_LINE;
    }

    /**
     * visitCmdEscreva A função se preocupa em separar o que flags na strig e as
     * respecitvas expressoes
     *
     */
    @Override
    public String visitCmdEscreva(LAParser.CmdEscrevaContext ctx) {
        // Se há cadeia de caracteres para printar:
        StringBuilder bufferRetorno = new StringBuilder();
        for (LAParser.ExpressaoContext expc : ctx.expressao()) {
            // Estrutura que armazena flags e strings
            StringBuilder bufferCadeia = new StringBuilder("\"");
            // Estrutura que armazena conjunto de variáveis
            StringBuilder bufferVar = new StringBuilder();
            String textoExp = visitExpressao(expc);
            boolean ehCadeia = textoExp.startsWith("\"");
            if (ehCadeia) {
                bufferCadeia.append(textoExp.replace("\"", ""));
            } else {
                bufferVar.append(", ").append(textoExp);
            }
            if (!pilhaDeTabelas.topo().existeSimbolo(textoExp)) {
                switch (bufferTipoExp) {
                    case "inteiro":
                    case "int":
                        bufferCadeia.append("%d");
                        break;
                    case "real":
                    case "float":
                        bufferCadeia.append("%f");
                        break;
                    case "literal":
                    case "char*":
                        bufferCadeia.append("%s");
                        break;
                    default:
                        break;
                }
            } else {
                bufferCadeia.append(tipoParaFlagC(pilhaDeTabelas.topo().getEntrada(textoExp).getTipo()));
            }
            // Junta elementos da cadeia com elementos variáveis
            bufferRetorno.append("printf(").append(bufferCadeia.toString()).append("\"")
                    .append(bufferVar.toString()).append(")").append(END_CMD_LINE);
        }

        return bufferRetorno.toString();
    } // fim visitCmdEscreva

    /**
     * visitCmdSe Visita comando Se (if)
     *
     */
    @Override
    public String visitCmdSe(LAParser.CmdSeContext ctx) {
        // Começo da função if
        String bufferRetorno = "if ( ";
        StringBuilder bufferCmdEntao = new StringBuilder();
        StringBuilder bufferCmdSenao = new StringBuilder();

        bufferRetorno += visitExpressao(ctx.expressao()) + " ) {\n";

        // Lista de comandos para "se"
        if (!ctx.cmdEntao.isEmpty()) {
            for (LAParser.CmdContext c : ctx.cmdEntao) {
                bufferCmdEntao.append(BLANK).append(visitCmd(c));
            }
        }
        bufferRetorno += bufferCmdEntao;

        // Lista de comandos para "então" (else) caso existam
        if (!ctx.cmdSenao.isEmpty()) {
            bufferRetorno += "\n} else {\n";
            for (LAParser.CmdContext c : ctx.cmdSenao) {
                bufferCmdSenao.append(BLANK).append(visitCmd(c));
            }
            bufferRetorno += bufferCmdSenao;
        }

        bufferRetorno += "}\n"; // fim_se
        return bufferRetorno;
    }// fim visitCmdSe

    /**
     * visitCmdCaso Visita comando Caso (switch)
     *
     */
    @Override
    public String visitCmdCaso(LAParser.CmdCasoContext ctx) {
        // Inicia a função, com a expressão aritmetica do argumento
        String bufferRetorno = "switch(" + visitExp_aritimetica(ctx.exp_aritimetica()) + ") {\n";
        StringBuilder bufferCmd = new StringBuilder();
        // Adiciona estrutura de seleção
        bufferRetorno += visitSelecao(ctx.selecao());
        if (!ctx.cmd().isEmpty()) {
            // Adiciona o caso default
            bufferRetorno += "\ndefault:\n";
            for (LAParser.CmdContext c : ctx.cmd()) {
                bufferCmd.append(BLANK).append(visitCmd(c));
            }
            bufferRetorno += bufferCmd;
        }
        bufferRetorno += "}\n";
        return bufferRetorno;
    }// fim visitCmdCaso

    /**
     * visitSelecao Conjunto de "itens de seleção"
     *
     */
//    @Override
//    public String visitSelecao ( LAParser.SelecaoContext ctx ) {
//        StringBuilder buffer = new StringBuilder (  );
//        if ( ctx.item_selecao () != null )
//            for (LAParser.Item_selecaoContext isc : ctx.item_selecao ())
//                buffer.append ( visitItem_selecao ( isc ) );
//        return buffer.toString ();
//    } // fim visitSelecao
    /**
     * visitItem_selecao Conjunto de comandos + break
     *
     */
//    @Override
//    public String visitItem_selecao ( LAParser.Item_selecaoContext ctx ) {
//        String buffer = visitConstantes( ctx.constantes () );
//        StringBuilder bufferCmd = new StringBuilder (  );
//        if ( !ctx.cmd().isEmpty () )
//            for (LAParser.CmdContext c : ctx.cmd ( ))
//                bufferCmd.append ( BLANK ).append ( visitCmd ( c ) );
//        bufferCmd.append( "break" ).append( END_CMD_LINE );
//        return buffer + bufferCmd.toString ();
//    } // fim visitItem_selecao
    /**
     * visitConstantes Argumento de cada "case" do switch
     *
     */
    @Override
    public String visitConstantes(LAParser.ConstantesContext ctx) {
        String buffer = visitNumero_intervalo(ctx.numero_intervalo1);
        StringBuilder bufferNumInter = new StringBuilder();
        if (!ctx.outrosNumero_intervalo.isEmpty()) {
            for (LAParser.Numero_intervaloContext nic : ctx.outrosNumero_intervalo) {
                bufferNumInter.append("\n").append(visitNumero_intervalo(nic)).append(":\n");
            }
        }
        buffer += bufferNumInter.toString();
        return buffer;
    } // fim visitConstantes

    /**
     * visitNumero_intervalo Cada case do switch
     *
     */
    @Override
    public String visitNumero_intervalo(LAParser.Numero_intervaloContext ctx) {
        StringBuilder buffer = new StringBuilder();
        if (ctx.op_unario1 != null) {
            buffer.append("case ").append(ctx.op_unario1.getText()).append(ctx.ni1.getText());
        } else {
            buffer.append("case ").append(ctx.ni1.getText());
        }
        buffer.append(":\n");
        if (ctx.ni2 != null) {
            StringBuilder bufferOpUnario2 = new StringBuilder();
            if (!ctx.op_unario2.isEmpty()) {
                for (LAParser.Op_unarioContext ouc : ctx.op_unario2) {
                    bufferOpUnario2.append(ouc.getText());
                }
            }

            int ni1 = Integer.parseInt(ctx.ni1.getText());
            int ni2 = Integer.parseInt(ctx.ni2.getText());
            for (int i = ni1 + 1; i <= ni2; i += 1) {
                if (i == ni1 + 1) {
                    buffer.append("case ").append(bufferOpUnario2.toString()).append(String.valueOf(i));
                } else {
                    buffer.append(":\ncase ").append(bufferOpUnario2.toString()).append(String.valueOf(i));
                }
            }
            buffer.append(":\n");
        }

        return buffer.toString();
    } // fim visitNumero_intervalo

    /**
     * visitCmdEnquanto Visita a estrutura de repetição Para (for)
     *
     */
    @Override
    public String visitCmdPara(LAParser.CmdParaContext ctx) {
        String id = ctx.IDENT().toString();
        // Estrutura do for
        String buffer = "for(" + id + "=" + visitExp_aritimetica(ctx.exp_aritmetica1) + ";"
                + id + "<=" + visitExp_aritimetica(ctx.exp_aritmetica2) + ";" + id + "++){\n";
        StringBuilder bufferCmd = new StringBuilder();
        if (ctx.cmd() != null) {
            for (LAParser.CmdContext c : ctx.cmd()) // Cada comando do for
            {
                bufferCmd.append(BLANK).append(visitCmd(c));
            }
        }

        buffer += bufferCmd;
        buffer += "\n}\n";
        return buffer;
    } // fim visitCmdPara

    /**
     * visitCmdEnquanto Visita a estrutura de repetição Enquanto (while)
     *
     */
    @Override
    public String visitCmdEnquanto(LAParser.CmdEnquantoContext ctx) {
        String buffer = "while(" + visitExpressao(ctx.expressao()) + "){\n";
        StringBuilder bufferCmd = new StringBuilder();
        if (ctx.cmd() != null) {
            for (LAParser.CmdContext c : ctx.cmd()) {
                bufferCmd.append(BLANK).append(visitCmd(c));
            }
        }

        buffer += bufferCmd;
        buffer += "\n}\n";
        return buffer;
    } // fim visitCmdEnquanto

    /**
     * visitCmdFaca Visita a estrutura de repetição Faça (do-while)
     *
     */
    @Override
    public String visitCmdFaca(LAParser.CmdFacaContext ctx) {
        String buffer = "do {";
        StringBuilder bufferCmd = new StringBuilder();
        if (ctx.cmd() != null) {
            for (LAParser.CmdContext c : ctx.cmd()) {
                bufferCmd.append(BLANK).append(visitCmd(c));
            }
        }

        buffer += bufferCmd;
        buffer += "\n} while(" + visitExpressao(ctx.expressao()) + ")" + END_CMD_LINE;
        return buffer;
    }

    @Override
    public String visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        String buffer = "";
        String id = visitIdentificador(ctx.identificador());
        String expr = visitExpressao(ctx.expressao());
        if (pilhaDeEscopos.escopoAtual().existeSimbolo(id)
                && pilhaDeEscopos.escopoAtual().getEntrada(id).getTipo().contains("*")
                && !expr.contains("&")) {
            buffer += "*";
        }

        buffer += id + " = " + expr + END_CMD_LINE;
        if (pilhaDeEscopos.escopoAtual().existeSimbolo(id)
                && pilhaDeEscopos.escopoAtual().getEntrada(id).getTipo().equals("char*")) {
            buffer = "strcpy(" + buffer.replace(" = ", ", ")
                    .replace(END_CMD_LINE, ")" + END_CMD_LINE).replace("*", "");
        }
        return buffer;
    }

    /**
     * visitTipo, visitTipo_basico, visitTipo_basico_ident, visitTipo_estendido
     * Tratamento de visit para visitTipo e derivados
     *
     */
    @Override
    public String visitTipo(LAParser.TipoContext ctx) {
        return super.visitTipo(ctx);
    }

    @Override
    public String visitTipo_basico(LAParser.Tipo_basicoContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx) {
        return super.visitTipo_basico_ident(ctx);
    }

    @Override
    public String visitTipo_estendido(LAParser.Tipo_estendidoContext ctx) {
        return ctx.getText();
    }

    /**
     * visitRegistro Visita estrutura de registro
     *
     */
    @Override
    public String visitRegistro(LAParser.RegistroContext ctx) {
        estaVisitandoRegistro = true;
        // Escreva, para o retorno da função, o começo da struct
        StringBuilder bufferRetorno = new StringBuilder("struct {\n");
        StringBuilder bufferVar = new StringBuilder();
        if (!ctx.variavel().isEmpty()) // Para cada variável da struct
        {
            for (LAParser.VariavelContext vc : ctx.variavel()) // Adicione na estrutura
            {
                bufferVar.append(visitVariavel(vc)).append(END_CMD_LINE);
            }
        }

        // feche o registro
        bufferRetorno.append(bufferVar).append("} ");
        estaVisitandoRegistro = false;
        return bufferRetorno.toString();
    } // fim visitRegistro

    /**
     * visitCmdChamada Visita chamada de função/procedimento anterirmente
     * declarada
     *
     */
    @Override
    public String visitCmdChamada(LAParser.CmdChamadaContext ctx) {
        // Armazena identificador, abre-chaves e expressão como argumento
        String buffer = ctx.IDENT().toString() + "(" + visitExpressao(ctx.exp1);
        StringBuilder bufferExp = new StringBuilder();
        if (ctx.outrasExp != null) // Para cada outra expressão
        {
            for (LAParser.ExpressaoContext ec : ctx.outrasExp) // Armazene separado por vírgula
            {
                bufferExp.append(", ").append(visitExpressao(ec));
            }
        }

        // termine a chamada
        buffer += bufferExp.toString() + ")" + END_CMD_LINE;
        return buffer;
    } // visitCmdChamada

    /**
     * visitCmdRetorne Visita comando de retorno para função.
     *
     */
    @Override
    public String visitCmdRetorne(LAParser.CmdRetorneContext ctx) {
        return "return "
                + // Palavra reservada
                visitExpressao(ctx.expressao())
                + // Expressão a ser retornada
                END_CMD_LINE;
    } // fim visitCmdRetorne

    /**
     * visitExpressao Visita expressão Primeiro visit das estruturas de
     * expressão
     *
     */
    @Override
    public String visitExpressao(LAParser.ExpressaoContext ctx) {
        // Inicia armazenando o primeiro termo
        String buffer = visitTermo_logico(ctx.termo_l1);
        // Prepara a estrutura para disjunção de termos
        StringBuilder bufferTermos = new StringBuilder();
        if (ctx.outrosTermos != null) // Para cada termo
        {
            for (LAParser.Termo_logicoContext tlc : ctx.outrosTermos) {
                bufferTermos.append(" || ");        // Operador logico "ou"
                // Acrescente-o na estrutura
                bufferTermos.append(visitTermo_logico(tlc));
            }
        }
        buffer += bufferTermos.toString();
        return buffer;
    }

    /**
     * visitTermo_logico Visita termo lógico
     *
     */
    @Override
    public String visitTermo_logico(LAParser.Termo_logicoContext ctx) {
        String buffer = ctx.fator_l1.getText();

        // Substitui escrita em ALG para C
        buffer = buffer.replace("=", "==");     // Operador de igualdade
        buffer = buffer.replace("<>", "!=");    // Operador de diferença
        buffer = buffer.replace(">==", ">=");   // Operador maior igual
        buffer = buffer.replace("<==", "<=");   // Operador menor igual
        buffer = buffer.replace("nao", "!");  // Operador de negacao
        buffer = buffer.replace("verdadeiro", "true");  // valor logico verdadeiro
        buffer = buffer.replace("falso", "false");      // valor logico falso
        super.visitFator_logico(ctx.fator_l1);

        StringBuilder bufferTermos = new StringBuilder();
        if (!ctx.outrosFatores.isEmpty()) {
            for (LAParser.Fator_logicoContext flc : ctx.outrosFatores) {
                bufferTermos.append(" && ");        // Operador logico "e"
                String texto = flc.getText();
                texto = texto.replace("=", "==");     // Operador de igualdade
                texto = texto.replace("<>", "!=");    // Operador de diferença
                texto = texto.replace(">==", ">=");   // Operador maior igual
                texto = texto.replace("<==", "<=");   // Operador menor igual
                texto = texto.replace("nao", "!");  // Operador de negacao
                texto = texto.replace("verdadeiro", "true");  // valor logico verdadeiro
                texto = texto.replace("falso", "false");      // valor logico falso
                bufferTermos.append(texto);
                super.visitFator_logico(flc);
            }
        }

        // Retorna para escrita em C
        buffer += bufferTermos.toString();
        return buffer;
    } // fim visitTermo_logico

    /**
     * visitExp_aritmetica Visita expressão aritmética Apenas retorna o contexto
     * como texto
     *
     */
    @Override
    public String visitExp_aritimetica(LAParser.Exp_aritimeticaContext ctx) {
        super.visitExp_aritimetica(ctx);
        return ctx.getText();
    } // fim visitExp_aritmetica

    /*
        A partir deste ponto, os métodos de visit servem para identificar o tipo do valor que as expressões retornam.
     */
    /**
     * visitParcela_unario_inteiro Parcela cujo valor é um número real.
     *
     */
    @Override
    public String visitParcela_unario_inteiro(LAParser.Parcela_unario_inteiroContext ctx) {
        bufferTipoExp = "inteiro";
        return null;
    } // visitParcela_unario_inteiro

    /**
     * visitParcela_unario_real Parcela cujo valor é um número real.
     *
     */
    @Override
    public String visitParcela_unario_real(LAParser.Parcela_unario_realContext ctx) {
        bufferTipoExp = "real;";
        return null;
    } // fim visitParcela_unario_real

    /**
     * visitParcela_nao_unario_cadeia Parcela representada por cadeia de
     * caracteres.
     *
     */
    @Override
    public String visitParcela_nao_unario_cadeia(LAParser.Parcela_nao_unario_cadeiaContext ctx) {
        bufferTipoExp = "cadeia";
        return null;
    } // fim visitParcela_nao_unario_cadeia

    /**
     * visitParcela_unario_id Parcela representada por apenas um identificador.
     *
     */
    @Override
    public String visitParcela_unario_id(LAParser.Parcela_unario_idContext ctx) {
        String simbolo = visitIdentificador(ctx.identificador());
        if (pilhaDeEscopos.escopoAtual().getEntrada(simbolo) != null) {
            String tipo = pilhaDeEscopos.escopoAtual().getEntrada(simbolo).getTipo();
            if (!tipo.equals("void")) {
                bufferTipoExp = tipo;
            }
        }
        return null;
    }

}
