package me.karun.country.proxy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;

class SoapRequest {
  private final Map<String, String> headers;
  private final String body;
  private final Logger logger = LoggerFactory.getLogger(SoapRequest.class);
  private static final String DEFAULT_SOAP_METHOD_NAME = "unknown-method";
  private final XPathExpression expr;

  SoapRequest(final String body) throws XPathExpressionException {
    this.headers = new HashMap<>();
    this.body = body;
    expr = XPathFactory.newInstance().newXPath().compile("/Envelope/Body/*[1]");
  }

  SoapRequest addActionHeader(final String value) {
    headers.put("action", value);
    return this;
  }

  SoapRequest addSoapActionHeader(final String value) {
    headers.put("SOAPAction", value);
    return this;
  }

  String fetchSoapMethod() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
    final String action = headers.get("action");
    if (StringUtils.isNotEmpty(action)) {
      logger.debug("Found SOAP method in header key \"{}\". Value is \"{}\"", "action", action);
      return action;
    }

    final String soapAction = headers.get("SOAPAction");
    if (StringUtils.isNotEmpty(soapAction)) {
      logger.debug("Found SOAP method in header key \"{}\". Value is \"{}\"", "SOAPAction", soapAction);
      return soapAction;
    }

    return fetchSoapMethodFromBody();
  }

  private String fetchSoapMethodFromBody() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
    final Optional<Node> soapMethodNode = Optional.ofNullable((Node) expr.evaluate(convertStringToDocument(body), XPathConstants.NODE));

    if (!soapMethodNode.isPresent()) {
      logger.debug("Could not find SOAP header by parsing the body. Value is \"{}\"", DEFAULT_SOAP_METHOD_NAME);
      return DEFAULT_SOAP_METHOD_NAME;
    }

    final String nodeName = soapMethodNode.get().getNodeName();
    logger.debug("Parsed node name from the request is {}", nodeName);

    final List<String> nodeNameParts = asList(nodeName.split(":"));
    final String result = nodeNameParts.size() > 0 ? nodeNameParts.get(nodeNameParts.size() - 1) : nodeName;
    logger.debug("Found SOAP method in body. Value is \"{}\"", result);

    return result;
  }

  private static Document convertStringToDocument(String xmlStr) throws IOException, SAXException, ParserConfigurationException {
    final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    return builder.parse(new InputSource(new StringReader(xmlStr)));
  }
}
