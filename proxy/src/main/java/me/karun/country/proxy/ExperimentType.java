package me.karun.country.proxy;

enum ExperimentType {
  CONTROL(9100), CANDIDATE(9000);

  private final int port;

  ExperimentType(final int port) {
    this.port = port;
  }

  public int getPort() {
    return port;
  }

  public String getName() {
    return name().toLowerCase();
  }

  @Override
  public String toString() {
    return getName();
  }
}
