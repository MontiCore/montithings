// (c) https://github.com/MontiCore/monticore
package montithings.generator.config;

public enum MessageBroker {
  OFF("OFF"),
  MQTT("MQTT"),
  DDS("DDS");

  final String name;

  MessageBroker(String name) {
    this.name = name;
  }

  /**
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return this.name;
  }

  public static MessageBroker fromString(String messageBroker) {
    switch (messageBroker) {
      case "OFF":
        return MessageBroker.OFF;
      case "MQTT":
        return MessageBroker.MQTT;
      case "DDS":
        return MessageBroker.DDS;
      default:
        throw new IllegalArgumentException(
          "0xMT302 Message broker " + messageBroker + " is unknown");
    }
  }
}
