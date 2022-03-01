// (c) https://github.com/MontiCore/monticore
package montithings.generator.config;

public enum RecordingMode {
  OFF("OFF"),
  ON("ON");

  final String mode;

  RecordingMode(String mode) {
    this.mode = mode;
  }

  /**
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return this.mode;
  }

  public static RecordingMode fromString(String recordingMode) {
    switch (recordingMode) {
      case "OFF":
        return RecordingMode.OFF;
      case "ON":
        return RecordingMode.ON;
      default:
        throw new IllegalArgumentException(
          "0xMT305 Recording mode " + recordingMode + " is unknown");
    }
  }
}
