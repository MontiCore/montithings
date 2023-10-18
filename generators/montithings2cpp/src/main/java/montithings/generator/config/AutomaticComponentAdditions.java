// (c) https://github.com/MontiCore/monticore
package montithings.generator.config;

public enum AutomaticComponentAdditions {
  OFF("OFF"),
  ON("ON");

  final String value;

  AutomaticComponentAdditions(String value) {
    this.value = value;
  }

  /**
   * @see Enum#toString()
   */
  @Override
  public String toString() {
    return this.value;
  }

  public static AutomaticComponentAdditions fromString(String automaticComponentAddition) {
    switch (automaticComponentAddition) {
      case "OFF":
        return AutomaticComponentAdditions.OFF;
      case "ON":
        return AutomaticComponentAdditions.ON;
      default:
        throw new IllegalArgumentException(
          "0xMT304 automaticComponentAddition option " + automaticComponentAddition + " is unknown");
    }
  }
}
