package me.karun.country.proxy;

import com.codahale.metrics.MetricRegistry;
import com.github.rawls238.scientist4j.Experiment;

class ToggleAwareExperiment<T> extends Experiment<T> {
  ToggleAwareExperiment(final String name, final MetricRegistry registry) {
    super(name, registry);
  }

  @Override
  protected boolean runIf() {
    return Math.random() > 0.5;
  }
}
