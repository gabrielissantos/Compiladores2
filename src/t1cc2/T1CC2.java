/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package t1cc2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

/**
 *
 * @author Gabrieli Santos
 */
public class T1CC2 {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, RecognitionException {
        SaidaParser out = new SaidaParser();
        LAParser.ProgramaContext arvore = null; //para a analise semantica
        try {
            CharStream input = CharStreams.fromFileName(args[0]); //entrada
            LALexer lexer = new LALexer(input);
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            LAParser parser = new LAParser(tokenStream);
            parser.addErrorListener(new ErrorListener(out));
            arvore = parser.programa();
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            System.out.println("Erro: nenhum arquivo de entrada foi dado ao executar o compilador.");
        }

//        //analise semantica
//        if (!out.isModificado()) {
//            LASemanticAnalyzer semantico = new LASemanticAnalyzer(out);
//            semantico.visitPrograma(arvore);
//        } else {
//            out.println("Fim da compilacao");
//
//        }
        if (!out.isModificado()) {
            GeradorDeCodigo gerador = new GeradorDeCodigo(out);
            gerador.visitPrograma(arvore);
        } else {
            out.println("Fim da compilacao");

        }

        File saida = new File(args[1]);
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(saida))) {
            //out.println("Fim da compilacao");
            pw.print(out.toString());
            pw.flush();
        }

    }
}
