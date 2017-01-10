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
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.lineSeparator;
import static me.karun.country.proxy.ExperimentType.CANDIDATE;
import static me.karun.country.proxy.ExperimentType.CONTROL;
import static org.apache.commons.io.FileUtils.readFileToString;

@Controller
@RequestMapping("/proxy")
public class ProxyEndpoint {

  private final MetricRegistry registry = new MetricRegistry();

  @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_XML_VALUE)
  @ResponseBody
  public String proxyRequest() throws Exception {
    return new Experiment<String>("getCountry", registry)
      .run(this::controlFunction, this::candidateFunction);
  }

  @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value = "/metrics")
  @ResponseBody
  public String experimentResult() throws Exception {
    final String metrics = registry.getMetrics().entrySet().stream()
      .map(e -> e.getKey() + " => " + metricToString(e.getValue()))
      .collect(Collectors.joining(lineSeparator()));

    final String meters = registry.getMeters().entrySet().stream()
      .map(e -> e.getKey() + " => " + metricToString(e.getValue()))
      .collect(Collectors.joining(lineSeparator()));


    return String.format("Metrics:%s%s%sMeters:%s%s", lineSeparator(), metrics, lineSeparator(), lineSeparator(), meters);
  }

  private String metricToString(final Metric value) {
    if (value instanceof Timer) {
      return "count=" + ((Timer) value).getCount() + ",mean=" + ((Timer) value).getMeanRate();
    } else if (value instanceof Counter) {
      return "count=" + ((Counter) value).getCount();
    }
    return value.toString();
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
    System.out.println(">> Start calling " + type);

    final String response = SoapClient.testClient(String.format("http://localhost:%d/ws", type.getPort()))
      .withHeader(HTTP.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
      .post(inputFromFile())
      .process();

    System.out.println(">> End calling " + type + ". Execution time = " + (currentTimeMillis() - start));
    return response;
  }

  private String inputFromFile() throws IOException {
    final String absoluteFilePath = this.getClass()
      .getResource("/request.xml")
      .getFile();
    return readFileToString(new File(absoluteFilePath));
  }
}

