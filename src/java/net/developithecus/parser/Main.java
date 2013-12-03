/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParserException {
        Parser parser = new ConfigRulesBuilder().newParser();
        long t1 = System.nanoTime();
        parser.clear();
        parseFile(args[0], parser);
        parser.close();
        System.out.println(String.format("time spent (avg): %.3f ms", (System.nanoTime() - t1) / 1000000.0));
        System.out.println(parser.getRootGroup().dump());
    }


    private static void parseFile(String fileName, Parser parser) throws ParserException {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(fileName);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr, 4096);
            String line;
            while ((line = br.readLine()) != null) {
                parser.append(line + "\n");
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Reading file error", ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                } else if (isr != null) {
                    isr.close();
                } else if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Closing file error", ex);
            }
        }

    }

}
