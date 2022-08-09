// (c) https://github.com/MontiCore/monticore
package montithings.generator.config;

/**
 * Defines the format used for serialization of messages
 * JSON = Use JSON
 * PROTOBUF = Use Google Protocol Buffers
 */
public enum SerializationMode {
  JSON("JSON"), PROTOBUF("PROTOBUF");

  final String name;

  SerializationMode(String name) {
    this.name = name;
  }

  public static SerializationMode fromString(String serializationMode) {
    switch (serializationMode) {
      case "JSON":
        return SerializationMode.JSON;
      case "PROTOBUF":
        return SerializationMode.PROTOBUF;
      default:
        throw new IllegalArgumentException("0xMT307 Serialization mode " + serializationMode + " is unknown");
    }
  }

  /**
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return this.name;
  }
}
