package t1cc2;

import java.util.BitSet;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;


public class ErrorListener implements ANTLRErrorListener{

    SaidaParser sp;

    public ErrorListener(SaidaParser sp) {
        this.sp = sp;
    }

    public void syntaxError(Recognizer<?, ?> rcgnzr, Object o, int i, int i1, String string, RecognitionException re) {
        //Transforma o objeto em um common token
        CommonToken ct = (CommonToken) o;
        //Caso a saida fora modificada
        if (!this.sp.isModificado()) {
            //Verifica erro de simbolo n�o identificado
            if(ct.getType() == LALexer.ERROCHAR){
                this.sp.println("Linha " + i + ": " + ct.getText()+ " - simbolo nao identificado");

                //Verifica erro de coment�rio n�o fechado
            }else if( ct.getType() == LALexer.COMMENTNFECHADO ) {
                this.sp.println("Linha " + (i + 1) + ": comentario nao fechado");

            }else{
                //Verifica erro de fim de arquivo e tambem de erro sintatico proxima de uma palavra
                if (ct.getText().equals("<EOF>")){
                    this.sp.println("Linha " + i + ": erro sintatico proximo a EOF");

                } else {
                    this.sp.println("Linha " + i + ": erro sintatico proximo a " + ct.getText());
                }
            }
        }
    }

    @Override
    public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {
     //   if (!this.sp.isModificado()) {
     //       this.sp.println("Ambiguidade: linha " + i + ":" + i1);
     //   }

    }

    @Override
    public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitSet, ATNConfigSet atnConfigSet) {

    }

    @Override
    public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atnConfigSet) {

    }



}
