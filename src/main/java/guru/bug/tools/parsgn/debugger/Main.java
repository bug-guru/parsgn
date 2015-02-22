/*
 * Copyright (c) 2015 Dimitrijs Fedotovs http://www.bug.guru
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package guru.bug.tools.parsgn.debugger;

import guru.bug.tools.parsgn.Parser;
import guru.bug.tools.parsgn.ebnf.DefaultParserBuilder;
import guru.bug.tools.parsgn.ebnf.EBNFParser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String rulesFileName = getParameters().getNamed().get("rules");
        String sourceFileName = getParameters().getNamed().get("source");
        ProcessDebugger debugger = setupEnvironment(rulesFileName, sourceFileName);
        MainView root = new MainView(debugger);
        primaryStage.setTitle("ParsGN Debugger");
        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
        primaryStage.setMinWidth(root.getMinWidth());
        primaryStage.setMinHeight(root.getMinHeight());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //    private void setupRulesStructure(String rulesFileName) throws IOException {
//        String rulesContent = readContent(rulesFileName);
//        StringReader rulesReader = new StringReader(rulesContent);
//        EBNFParser ebnfParser = new EBNFParser();
//        ParseTreeResultBuilder resultBuilder = new ParseTreeResultBuilder();
//        ebnfParser.parse(rulesReader, resultBuilder);
//        RulesViewModel model = new RulesViewModel(resultBuilder.getRoot(), rulesContent);
//    }
//
    private ProcessDebugger setupEnvironment(String rulesFileName, String sourceFileName) throws IOException {
        Parser parser;
        if (rulesFileName == null) {
            parser = new EBNFParser();
        } else {
            DefaultParserBuilder builder = new DefaultParserBuilder();
            parser = builder.createParser(new FileReader(new File(rulesFileName)));
        }
        ProcessDebugger result = new ProcessDebugger(parser);
        result.debug(sourceFileName);
        return result;
    }


}
