package me.karun.country.proxy;

import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;

import static java.lang.System.currentTimeMillis;
import static me.karun.country.proxy.ExperimentType.CANDIDATE;
import static me.karun.country.proxy.ExperimentType.CONTROL;
import static org.apache.commons.io.FileUtils.readFileToString;

@Controller
@RequestMapping("/proxy")
public class ProxyEndpoint {

  private final MetricsEngine metricEngine = new MetricsEngine();
  private final Logger logger = LoggerFactory.getLogger(ProxyEndpoint.class);

  @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_XML_VALUE)
  @ResponseBody
  public String proxyRequest() throws Exception {
    return metricEngine.createExperiment("getCountry")
      .run(this::controlFunction, this::candidateFunction);
  }

  @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/metrics")
  @ResponseBody
  public String experimentResult() throws Exception {
    return metricEngine.formattedText();
  }

  private String controlFunction() {
    try {
      return evaluateSoapRequest(CONTROL);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String candidateFunction() {
    try {
      return evaluateSoapRequest(CANDIDATE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String evaluateSoapRequest(final ExperimentType type) throws IOException {
    final long start = currentTimeMillis();
    logger.debug("Start calling {}", type);

    final String response = SoapClient.testClient(String.format("http://localhost:%d/ws", type.getPort()))
      .withHeader(HTTP.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
      .post(inputFromFile())
      .process();

    logger.debug("End calling {}. Execution time = {}", type, (currentTimeMillis() - start));
    return response;
  }

  private String inputFromFile() throws IOException {
    final String absoluteFilePath = this.getClass()
      .getResource("/request.xml")
      .getFile();
    return readFileToString(new File(absoluteFilePath));
  }
}

