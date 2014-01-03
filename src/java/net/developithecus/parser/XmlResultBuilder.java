package net.developithecus.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

/**
 * @author dima
 */
public class XmlResultBuilder extends ResultBuilder<org.w3c.dom.Node> {
    private Document result;

    public XmlResultBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        result = db.newDocument();
    }

    @Override
    protected Node createNode(String name, Position beginPosition, int length, String value, List<Node> children) {
        Element node = result.createElement(name);
        node.setAttribute("row", String.valueOf(beginPosition.getRow()));
        node.setAttribute("col", String.valueOf(beginPosition.getCol()));
        node.setAttribute("len", String.valueOf(length));
        if (value != null) {
            Text txt = result.createTextNode(value);
            node.appendChild(txt);
        }
        if (children != null) {
            for (Node c : children) {
                node.appendChild(c);
            }
        }
        return node;
    }

    @Override
    protected void committedRoot(Node root) {
        result.appendChild(root);
    }

    public Document getResult() {
        return result;
    }
}
