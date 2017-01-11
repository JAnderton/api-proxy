package me.karun.country.proxy;

import com.codahale.metrics.*;
import com.github.rawls238.scientist4j.Experiment;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;

class MetricsEngine {
  private final MetricRegistry registry = new MetricRegistry();

  Experiment<String> createExperiment(final String name) {
    JmxReporter.forRegistry(registry)
      .build()
      .start();
    Slf4jReporter.forRegistry(registry)
      .outputTo(LoggerFactory.getLogger(MetricsEngine.class))
      .convertRatesTo(TimeUnit.SECONDS)
      .convertDurationsTo(TimeUnit.MILLISECONDS)
      .build()
      .start(5, TimeUnit.SECONDS);

    return new ToggleAwareExperiment<>(name, registry);
  }

  String formattedText() {
    final String metricsData = registry.getMetrics().entrySet().stream()
      .map(e -> e.getKey() + " => " + metricToString(e.getValue()))
      .collect(Collectors.joining(lineSeparator()));
    return "Metrics:" + lineSeparator()
      + metricsData + lineSeparator();
  }

  private String metricToString(final Metric value) {
    if (value instanceof Timer) {
      return "{count=" + ((Timer) value).getCount() + ", mean=" + ((Timer) value).getMeanRate() + "}";
    } else if (value instanceof Counter) {
      return "{count=" + ((Counter) value).getCount() + "}";
    }
    return value.toString();
  }
}
