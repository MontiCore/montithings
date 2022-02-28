// (c) https://github.com/MontiCore/monticore
package montithings.generator.config;

public enum PortNameTrafo {
  OFF("OFF"),
  ON("ON");

  final String value;

  PortNameTrafo(String value) {
    this.value = value;
  }

  /**
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return this.value;
  }

  public static PortNameTrafo fromString(String portNameTrafo) {
    switch (portNameTrafo) {
      case "OFF":
        return PortNameTrafo.OFF;
      case "ON":
        return PortNameTrafo.ON;
      default:
        throw new IllegalArgumentException(
          "0xMT304 portNameTrafo option " + portNameTrafo + " is unknown");
    }
  }
}
