// Generated from LA.g4 by ANTLR 4.7.2
package t1cc2;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LAParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LAVisitor<T> extends ParseTreeVisitor<T> {
	
	T visitPrograma(LAParser.ProgramaContext ctx);
	
	T visitDeclaracoes(LAParser.DeclaracoesContext ctx);
	
	T visitDecl_local_global(LAParser.Decl_local_globalContext ctx);
	
	T visitDeclaracao_local(LAParser.Declaracao_localContext ctx);
	
	T visitVariavel(LAParser.VariavelContext ctx);
	
	T visitIdentificador(LAParser.IdentificadorContext ctx);
	
	T visitDimensao(LAParser.DimensaoContext ctx);
	
	T visitTipo(LAParser.TipoContext ctx);
	
	T visitTipo_basico(LAParser.Tipo_basicoContext ctx);
	
	T visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx);
	
	T visitTipo_estendido(LAParser.Tipo_estendidoContext ctx);
	
	T visitValor_constante(LAParser.Valor_constanteContext ctx);
	
	T visitRegistro(LAParser.RegistroContext ctx);
	
	T visitDeclaracao_global(LAParser.Declaracao_globalContext ctx);
	
	T visitParametro(LAParser.ParametroContext ctx);
	
	T visitParametros(LAParser.ParametrosContext ctx);
	
	T visitCorpo(LAParser.CorpoContext ctx);
	
	T visitCmd(LAParser.CmdContext ctx);
	
	T visitCmdLeia(LAParser.CmdLeiaContext ctx);
	
	T visitCmdEscreva(LAParser.CmdEscrevaContext ctx);
	
	T visitCmdSe(LAParser.CmdSeContext ctx);
	
	T visitCmdCaso(LAParser.CmdCasoContext ctx);
	
	T visitCmdPara(LAParser.CmdParaContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expPrefixo2ChamadaDeFuncao}
	 * labeled alternative in {@link LAParser#expprefixo2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCmdEnquanto(LAParser.CmdEnquantoContext ctx);
	/**
	 * Visit a parse tree produced by the {@code expPrefixo2Exp}
	 * labeled alternative in {@link LAParser#expprefixo2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCmdFaca(LAParser.CmdFacaContext ctx);
	/**
	 * Visit a parse tree produced by {@link LAParser#chamadadefuncao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx);
	/**
	 * Visit a parse tree produced by {@link LAParser#args}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCmdChamada(LAParser.CmdChamadaContext ctx);
	/**
	 * Visit a parse tree produced by {@link LAParser#funcao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCmdRetorne(LAParser.CmdRetorneContext ctx);
	/**
	 * Visit a parse tree produced by {@link LAParser#corpodafuncao}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelecao(LAParser.SelecaoContext ctx);
	/**
	 * Visit a parse tree produced by the {@code listaParListaDeNomes}
	 * labeled alternative in {@link LAParser#listapar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitItem_selecao(LAParser.Item_selecaoContext ctx);
	/**
	 * Visit a parse tree produced by the {@code listaParVarargs}
	 * labeled alternative in {@link LAParser#listapar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantes(LAParser.ConstantesContext ctx);
	/**
	 * Visit a parse tree produced by {@link LAParser#construtortabela}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumero_intervalo(LAParser.Numero_intervaloContext ctx);
	/**
	 * Visit a parse tree produced by {@link LAParser#listadecampos}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp_unario(LAParser.Op_unarioContext ctx);
	/**
	 * Visit a parse tree produced by {@link LAParser#campo}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx);
	/**
	 * Visit a parse tree produced by {@link LAParser#separadordecampos}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermo(LAParser.TermoContext ctx);
	/**
	 * Visit a parse tree produced by {@link LAParser#opbin}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFator(LAParser.FatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link LAParser#opunaria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp1(LAParser.Op1Context ctx);
        /**
	 * Visit a parse tree produced by {@link LAParser#opunaria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp2(LAParser.Op2Context ctx);/**
	 * Visit a parse tree produced by {@link LAParser#opunaria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp3(LAParser.Op3Context ctx);/**
	 * Visit a parse tree produced by {@link LAParser#opunaria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParcela(LAParser.ParcelaContext ctx);/**
	 * Visit a parse tree produced by {@link LAParser#opunaria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParcela_unario(LAParser.Parcela_unarioContext ctx);/**
	 * Visit a parse tree produced by {@link LAParser#opunaria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx);/**
	 * Visit a parse tree produced by {@link LAParser#opunaria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp_relacional(LAParser.Exp_relacionalContext ctx);/**
	 * Visit a parse tree produced by {@link LAParser#opunaria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp_relacional(LAParser.Op_relacionalContext ctx);/**
	 * Visit a parse tree produced by {@link LAParser#opunaria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressao(LAParser.ExpressaoContext ctx);/**
	 * Visit a parse tree produced by {@link LAParser#opunaria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermo_logico(LAParser.Termo_logicoContext ctx);/**
	 * Visit a parse tree produced by {@link LAParser#opunaria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFator_logico(LAParser.Fator_logicoContext ctx);/**
	 * Visit a parse tree produced by {@link LAParser#opunaria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParcela_logica(LAParser.Parcela_logicaContext ctx);/**
	 * Visit a parse tree produced by {@link LAParser#opunaria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp_logico_1(LAParser.Op_logico_1Context ctx);/**
	 * Visit a parse tree produced by {@link LAParser#opunaria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp_logico_2(LAParser.Op_logico_2Context ctx);
}