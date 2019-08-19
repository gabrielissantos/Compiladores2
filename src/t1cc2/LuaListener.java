// Generated from c:\Users\Gabrieli Santos\Documents\NetBeansProjects\T1CC2\src\t1cc2\Lua.g4 by ANTLR 4.7.2
package t1cc2;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LuaParser}.
 */
public interface LuaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LuaParser#programa}.
	 * @param ctx the parse tree
	 */
	void enterPrograma(LuaParser.ProgramaContext ctx);
	/**
	 * Exit a parse tree produced by {@link LuaParser#programa}.
	 * @param ctx the parse tree
	 */
	void exitPrograma(LuaParser.ProgramaContext ctx);
}