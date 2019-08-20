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

/**
 *
 * @author Gabrieli Santos
 */
public class T1CC2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        CharStream input = CharStreams.fromFileName(args[0]);
        File saida = new File(args[1]);
        PrintWriter pw = new PrintWriter(new FileOutputStream(saida));
        LALexer lexer = new LALexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokenStream);
        parser.programa();
        pw.print("Conteudo");
        pw.flush();
        pw.close();
    }
    
}
