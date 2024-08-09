# Dynamic EBNF-like Parser Library

This library allows for the dynamic creation of parsers based on a grammar description language similar to EBNF
(Extended Backus-Naur Form), which we'll refer to as "EBNF-like." The library reads an EBNF-like configuration file,
builds a parser, and then uses that parser to process input files, resulting in a parse tree that can be traversed.

## Features

- Dynamic Parser Creation: Create parsers on the fly using EBNF-like configuration files.
- Flexible Grammar: Define complex parsing rules with support for sequences, choices, repetitions, and more.
- Use of Variables and Arithmetic Expressions: Incorporate variables and arithmetic expressions directly in your grammar
  definitions, allowing for context-sensitive parsing rules.
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

## Advanced Grammar Example

ParsGN also supports the use of variables and arithmetic expressions within grammar definitions. This allows for the
creation of context-sensitive rules, such as those that depend on indentation levels or other dynamically calculated
values.

Here is an example demonstrating the use of variables in the grammar:

```
Root:
    [EOL* Line(0)]* EOL*;

Line(level):
    Indent(level) [!EOL #VALID]* [EOL* Line(level+1)]*;

.Indent(level):
    " "{level * 4};

.EOL:
    " "* ["\r\n" | "\r" | "\n"];
```

In this example:

- The variable level is used as a parameter to control the indentation size dynamically.
- The Indent rule multiplies the level by 4 to determine the number of spaces required for indentation.

This feature adds significant flexibility to your grammars, allowing you to handle complex input formats that depend on
context. The use of variables makes the Line rule recursive, allowing lines with greater indentation to be treated as
children of previous lines with lesser indentation.

### Example Input and Parse Tree

Given the following input content:

```
line 1 level 0
line 2 level 0
line 3 level 0
    line 3.1 level 1
    line 3.2 level 1
        line 3.2.1 level 2
        line 3.2.2 level 2
        line 3.2.3 level 2
    line 3.3 level 1
        line 3.3.1 level 2
line 4 level 0
line 5 level 0
    line 5.1
```

The resulting parse tree would be:

```
Root
   Line=line 1 level 0
   Line=line 2 level 0
   Line=line 3 level 0
      Line=line 3.1 level 1
      Line=line 3.2 level 1
         Line=line 3.2.1 level 2
         Line=line 3.2.2 level 2
         Line=line 3.2.3 level 2
      Line=line 3.3 level 1
         Line=line 3.3.1 level 2
   Line=line 4 level 0
   Line=line 5 level 0
      Line=line 5.1
```

As you can see, thanks to the use of variables, the rule has become recursive. Lines with greater indentation are
treated as children of previous lines with lesser indentation. This capability demonstrates how ParsGN can be used to
create sophisticated parsers for structured text formats, making it an excellent choice for scenarios requiring
context-sensitive parsing.

## Comparison with ANTLR

While ANTLR is a powerful tool for generating parsers and compilers for complex languages, ParsGN offers several
advantages that make it preferable in certain scenarios:

1. **Dynamic Parser Creation**:

- **ParsGN** allows for the creation of parsers on-the-fly based on EBNF-like descriptions without the need for code
  generation. This makes it ideal for scenarios where you need to quickly develop and test parsers without the overhead
  of integrating generated code into your project.
- **ANTLR** requires code generation and compilation, which can be more complex and time-consuming, especially for rapid
  prototyping or when the grammar is frequently changing.

2. **Simplicity and Ease of Use**:

- **ParsGN** uses a straightforward, EBNF-like syntax that is easy to learn and use, making it accessible even for those
  who may not have deep experience with parser generators.
- **ANTLR**, while more powerful, has a steeper learning curve and requires a good understanding of its syntax and
  concepts like predicates, actions, and the distinction between lexer and parser rules.

3. **Context-Sensitive Parsing**:

- **ParsGN** excels in scenarios where context-sensitive rules are needed, thanks to its support for variables and
  arithmetic expressions within grammars. This allows for flexible and powerful grammar definitions, such as handling
  indentation or other context-dependent features.
- **ANTLR** can handle context-sensitive parsing but typically requires more complex configurations, such as semantic
  predicates or actions embedded in the grammar.

4. **Rapid Development and Prototyping**:

- **ParsGN** is particularly well-suited for rapid development and prototyping of custom data formats or domain-specific
  languages. Its ability to quickly define and deploy parsers directly from grammar descriptions without intermediate
  steps streamlines the development process.
- **ANTLR**, while powerful, might introduce additional steps in the development process due to its code generation
  phase.

In summary, while ANTLR remains a top choice for developing complex parsers and compilers, ParsGN provides a simpler,
more dynamic approach that is often more suitable for rapid development, context-sensitive parsing, and situations where
ease of use and flexibility are paramount.

## Contributing

Contributions are welcome! If you have suggestions for improvements or new features, feel free to open an issue or
submit a pull request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

    JavaCC - Java Compiler Compiler, an inspiration for parsing-related projects.
    ANTLR - Another parser generator framework that influenced the development of this library.

