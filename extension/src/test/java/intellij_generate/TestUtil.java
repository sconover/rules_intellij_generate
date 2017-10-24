package intellij_generate;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static intellij_generate.Util.checkState;
import static java.lang.String.format;

public class TestUtil {
  static List<String> xpathList(String xmlDocument, String xpath) {
    NodeList nodeList = xpathNodeListFromDoc(xpath, parseXml(xmlDocument));
    List<String> results = new ArrayList<>();
    for (int i = 0; i < nodeList.getLength(); i++) {
      results.add(nodeList.item(i).getTextContent());
    }
    return results;
  }

  static String xpath(String xmlDocument, String xpath) {
    NodeList nodeList = xpathNodeListFromDoc(xpath, parseXml(xmlDocument));
    checkState(nodeList.getLength() == 1,
      format("expected number of results for xpath text result query to be exactly 1, " +
        "but was %d, results: %s", nodeList.getLength(), nodeList));
    return nodeList.item(0).getTextContent();
  }

  private static Document parseXml(String xmlString) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setIgnoringElementContentWhitespace(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(new ErrorHandler() {
        @Override
        public void warning(SAXParseException exception) throws SAXException {
          // silence SAX parse warnings
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
          // silence SAX parse errors
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
          throw exception;
        }
      });
      return builder.parse(new ByteArrayInputStream(xmlString.getBytes()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static NodeList xpathNodeListFromDoc(String xpathExpression, Document doc) {
    try {
      XPathFactory xPathfactory = XPathFactory.newInstance();
      XPath xpath = xPathfactory.newXPath();
      XPathExpression expr = xpath.compile(xpathExpression);
      return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
