// (c) https://github.com/MontiCore/monticore
package montithings.generator.config;

public enum ReplayMode {
  OFF("OFF"),
  ON("ON");

  final String name;

  ReplayMode(String name) {
    this.name = name;
  }

  /**
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return this.name;
  }

  public static ReplayMode fromString(String replayMode) {
    switch (replayMode) {
      case "OFF":
        return ReplayMode.OFF;
      case "ON":
        return ReplayMode.ON;
      default:
        throw new IllegalArgumentException(
          "0xMT303 Replay mode " + replayMode + " is unknown");
    }
  }
}