# Dynamic EBNF-like Parser Library

This library allows for the dynamic creation of parsers based on a grammar description language similar to EBNF
(Extended Backus-Naur Form), which we'll refer to as "EBNF-like." The library reads an EBNF-like configuration file,
builds a parser, and then uses that parser to process input files, resulting in a parse tree that can be traversed.

## Features

- Dynamic Parser Creation: Create parsers on the fly using EBNF-like configuration files.
- Flexible Grammar: Define complex parsing rules with support for sequences, choices, repetitions, and more.
- Parse Tree Generation: After parsing, a parse tree is generated, which can be traversed for further processing.

## Installation

To use this library in your project, include the following dependency in your pom.xml if using Maven:

```xml

<dependency>
    <groupId>guru.bug.tools</groupId>
    <artifactId>parsgn</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Alternatively, you can clone the repository and build the project locally:

```shell
git clone https://github.com/yourusername/parsgn.git
cd parsgn
mvn clean install
```

## Usage

### EBNF-like Configuration File

The EBNF-like configuration file used by the library is located at
`src/main/resources/guru/bug/tools/parsgn/ebnf/config.rules`.

This file contains the grammar definition used by the library to dynamically create parsers.

### Basic Example

Below is an example of how to use the library to create a parser from an EBNF-like configuration file and parse an input
string.

Here is an example of a simple EBNF-like grammar that can be used to parse basic arithmetic expressions:

```
expression: operand operator operand;

operand: number;

operator: addition | subtraction | multiplication | division;

number: #DIGIT+;

addition: "+";

subtraction: "-";

multiplication: "*";

division: "/";
```

Given the above EBNF-like grammar, consider the following input to be parsed:

```
12345+678
```

Below is the Java code that demonstrates how to create a parser, parse this input, and then traverse the resulting parse
tree:

```java
package guru.bug.tools.parsgn.demo;

import guru.bug.tools.parsgn.Parser;
import guru.bug.tools.parsgn.ebnf.DefaultParserBuilder;
import guru.bug.tools.parsgn.utils.*;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import static java.util.Objects.requireNonNull;

public class ParserExample {

    public static Parser createParser(String fileName) throws Exception {
        DefaultParserBuilder builder = new DefaultParserBuilder();
        try (
                InputStream ebnfInput = requireNonNull(ParserExample.class.getResourceAsStream(fileName));
                BufferedInputStream buf = new BufferedInputStream(ebnfInput);
                InputStreamReader reader = new InputStreamReader(buf)
        ) {
            return builder.createParser(reader);
        }
    }

    public static void main(String[] args) throws Exception {
        Parser parser = createParser("calculator.rules");
        String strExpression = "12345+678";
        try (var reader = new StringReader(strExpression)) {
            ParseTreeResultBuilder resultBuilder = new ParseTreeResultBuilder();
            parser.parse(reader, resultBuilder);
            ParseNode root = resultBuilder.getRoot();
            System.out.println("Parse tree:");
            System.out.println(root);
            System.out.println("Walking:");
            ParseTreeUtils.walk(root, false, new ParseNodeVisitor() {

                @Override
                public ParseNodeVisitResult startNode(ParseNode node) {
                    System.out.println("START: " + node.getName() + " = " + node.getValue());
                    return ParseNodeVisitResult.CONTINUE;
                }

                @Override
                public void endNode(ParseNode node) {
                    System.out.println("END  : " + node.getName());
                }

            });
        }
    }
}
```

### Parse Tree Output

After parsing the input "12345+678" using the example grammar, the resulting parse tree would look like this:

```
expression
   operand
      number=12345
   operator
      addition
   operand
      number=678
```

### Visitor Output

When traversing the parse tree with the ParseNodeVisitor, the output would look like this:

```
START: expression = null
START: operand = null
START: number = 12345
END  : number
END  : operand
START: operator = null
START: addition = null
END  : addition
END  : operator
START: operand = null
START: number = 678
END  : number
END  : operand
END  : expression
```

## Contributing

Contributions are welcome! If you have suggestions for improvements or new features, feel free to open an issue or
submit a pull request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

    JavaCC - Java Compiler Compiler, an inspiration for parsing-related projects.
    ANTLR - Another parser generator framework that influenced the development of this library.

