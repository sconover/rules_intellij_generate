package intellij_generate.scenarios;

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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

/**
 * code copied from the extensions side of the project.
 * preference in this case is fewer deps beats a little bit of copied code.
 */
public class TestUtil {
  private static final String PART_OF_WORKING_DIR_PATH = "execroot/rules_intellij_generate_scenarios/";

  public static List<String> removeWorkingDirectory(List<String> strings) {
    return strings.stream().map(str -> removeWorkingDirectory(str)).collect(toList());
  }

  public static String removeWorkingDirectory(String str) {
    // kinda hackey but works
    if (str.contains(PART_OF_WORKING_DIR_PATH)) {
      return str.split(PART_OF_WORKING_DIR_PATH)[1];
    } else {
      return str;
    }
  }

  public static String loadBazelGeneratedFile(String pathRelativeToBazelBin) {
    return readFile(format("../%s", pathRelativeToBazelBin));
  }

  public static String readFile(String pathStr) {
    try {
      if (pathStr == null) {
        throw new IllegalStateException("expected file path to be non-null");
      }
      if (!Paths.get(pathStr).toFile().exists()) {
        throw new IllegalStateException(format("expected file to exist: %s", pathStr));
      }
      return new String(Files.readAllBytes(Paths.get(pathStr)));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static List<String> xpathList(String xmlDocument, String xpath) {
    NodeList nodeList = xpathNodeListFromDoc(xpath, parseXml(xmlDocument));
    List<String> results = new ArrayList<>();
    for (int i = 0; i < nodeList.getLength(); i++) {
      results.add(nodeList.item(i).getTextContent());
    }
    return results;
  }

  public static String xpath(String xmlDocument, String xpath) {
    NodeList nodeList = xpathNodeListFromDoc(xpath, parseXml(xmlDocument));

    if (nodeList.getLength() != 1) {
      throw new RuntimeException(format("expected number of results for xpath text result query to be exactly 1, " +
        "but was %d, results: %s", nodeList.getLength(), nodeList));
    }
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
