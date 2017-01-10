package me.karun.country.proxy;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.github.rawls238.scientist4j.Experiment;
import org.apache.http.protocol.HTTP;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.io.FileUtils.readFileToString;

@Controller
@RequestMapping("/proxy")
public class ProxyEndpoint {

  private final MetricRegistry registry = new MetricRegistry();

  @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_XML_VALUE)
  @ResponseBody
  public String proxyRequest() throws Exception {
    return new Experiment<String>("getCountry", registry)
      .runAsync(this::controlFunction, this::candidateFunction);
  }

  @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/metrics")
  @ResponseBody
  public String experimentResult() throws Exception {
    final Map<String, Metric> metrics = registry.getMetrics();

    return metrics.entrySet().stream()
      .map(e -> e.getKey() + " => " + metricToString(e.getValue()))
      .collect(Collectors.joining(System.lineSeparator()));
  }

  private String metricToString(final Metric value) {
    if (value instanceof Timer) {
      return "count=" + ((Timer) value).getCount() + ",rate=" + ((Timer) value).getOneMinuteRate();
    } else if (value instanceof Counter) {
      return "count=" + ((Counter) value).getCount();
    }
    return value.toString();
  }

  private String controlFunction() {
    try {
      return evaluateSoapRequest("control");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String candidateFunction() {
    try {
      return evaluateSoapRequest("candidate");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String evaluateSoapRequest(final String type) throws IOException {
    System.out.println("> Calling " + type);
    return SoapClient.testClient(String.format("http://localhost:8080/ws/%s", type))
      .withHeader(HTTP.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
      .post(inputFromFile(String.format("%s-request.xml", type)))
      .process();
  }

  private String inputFromFile(final String fileName) throws IOException {
    final String absoluteFilePath = this.getClass()
      .getResource("/" + fileName)
      .getFile();
    return readFileToString(new File(absoluteFilePath));
  }
}
