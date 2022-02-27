// (c) https://github.com/MontiCore/monticore
package montithings.generator.config;

/**
 * Defines how the architecture is splitted in different binaries
 * OFF = No splitting, create a single binary containing everything
 * LOCAL = Deploy on a single machine
 * DISTRIBUTED = Deploy on multiple machines
 */
public enum SplittingMode {
  OFF("OFF"),
  LOCAL("LOCAL"),
  DISTRIBUTED("DISTRIBUTED");

  final String name;

  SplittingMode(String name) {
    this.name = name;
  }

  /**
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return this.name;
  }

  public static SplittingMode fromString(String splittingMode) {
    switch (splittingMode) {
      case "OFF":
        return SplittingMode.OFF;
      case "LOCAL":
        return SplittingMode.LOCAL;
      case "DISTRIBUTED":
        return SplittingMode.DISTRIBUTED;
      default:
        throw new IllegalArgumentException(
          "0xMT301 Splitting mode " + splittingMode + " is unknown");
    }
  }
}