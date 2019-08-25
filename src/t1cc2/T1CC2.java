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
     */
    //string com o caminho do projeto de casos de teste
    public static void main(String[] args) throws IOException, RecognitionException {
        /*
        CharStream input = CharStreams.fromFileName(args[0]); //entrada
        File saida = new File(args[1]);
        PrintWriter pw = new PrintWriter(new FileOutputStream(saida));
        LALexer lexer = new LALexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokenStream);
        parser.programa();
        pw.print("Conteudo");
        pw.flush();
        pw.close(); */

        SaidaParser out = new SaidaParser();
        CharStream input = CharStreams.fromFileName(args[0]);
        LALexer lexer = new LALexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokens);
        parser.addErrorListener(new ErrorListener(out));
        parser.programa();
        if (!out.isModificado()) {
            out.println("Fim da analise. Sem erros sintaticos.");
            try (PrintWriter pw = new PrintWriter(new FileOutputStream(args[1]))) {
                pw.print(out);
                pw.println("Fim da compilacao");
            }

        }

    }
}
