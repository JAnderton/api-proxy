package me.karun.country.proxy;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class SoapRequestTest {

  private String soapRequest;

  @Before
  public void setUp() throws Exception {
    soapRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
      "                  xmlns:gs=\"http://karun.me/country\">\n" +
      "  <soapenv:Header/>\n" +
      "  <soapenv:Body>\n" +
      "    <gs:getCountryRequest>\n" +
      "      <gs:name>Spain</gs:name>\n" +
      "    </gs:getCountryRequest>\n" +
      "  </soapenv:Body>\n" +
      "</soapenv:Envelope>\n";
  }

  @Test
  public void shouldUseActionHeaderWhenAvailable() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
    final String methodName = new SoapRequest(soapRequest)
      .addActionHeader("apples")
      .addSoapActionHeader("mangoes")
      .fetchSoapMethod();

    assertThat(methodName).isEqualTo("apples");
  }

  @Test
  public void shouldUseSoapHeaderWhenAvailable() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
    final String methodName = new SoapRequest(soapRequest)
      .addSoapActionHeader("oranges")
      .fetchSoapMethod();

    assertThat(methodName).isEqualTo("oranges");
  }

  @Test
  public void shouldReturnMethodNameWhenNameSpaceIsPresent() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
    final String methodName = new SoapRequest(soapRequest).fetchSoapMethod();

    assertThat(methodName).isEqualTo("getCountryRequest");
  }

  @Test
  public void shouldReturnMethodNameWhenNameSpaceIsNotPresent() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
    final String soapRequest = this.soapRequest.replace("gs:getCountryRequest", "pineapples");
    final String methodName = new SoapRequest(soapRequest).fetchSoapMethod();

    assertThat(methodName).isEqualTo("pineapples");
  }

  @Test
  public void shouldReturnDefaultWhenMethodCouldNotBeFound() throws XPathExpressionException, IOException, SAXException, ParserConfigurationException {
    final String soapRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
      "                  xmlns:gs=\"http://karun.me/country\">\n" +
      "  <soapenv:Header/>\n" +
      "  <soapenv:Body>\n" +
      "  </soapenv:Body>\n" +
      "</soapenv:Envelope>\n";
    final String methodName = new SoapRequest(soapRequest).fetchSoapMethod();

    assertThat(methodName).isEqualTo("unknown-method");
  }
}