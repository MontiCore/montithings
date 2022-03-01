// (c) https://github.com/MontiCore/monticore
package montithings.generator.config;

public enum LogTracing {
  OFF("OFF"),
  ON("ON");

  final String value;

  LogTracing(String value) {
    this.value = value;
  }

  /**
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return this.value;
  }

  public static LogTracing fromString(String logTracing) {
    switch (logTracing) {
      case "OFF":
        return LogTracing.OFF;
      case "ON":
        return LogTracing.ON;
      default:
        throw new IllegalArgumentException(
          "0xMT306 Log tracing mode " + logTracing + " is unknown");
    }
  }
}
