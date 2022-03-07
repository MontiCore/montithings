// (c) https://github.com/MontiCore/monticore
package montithings.generator.config;

public enum TargetPlatform {
  GENERIC("GENERIC"),
  DSA_VCG("DSA_VCG"), // based on dev-docker.sh and docker.dsa-ac.de:20001/dev-l06
  ARDUINO("ARDUINO"),
  DSA_LAB("DSA_LAB"), // connected cars lab, based on docker.dsa-ac.de:20001/dev-l06-customer
  RASPBERRY("RASPBERRY"); // Raspberry Pi + Grove Base HAT

  final String name;

  TargetPlatform(String name) {
    this.name = name;
  }

  /**
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return this.name;
  }

  public static TargetPlatform fromString(String platform) {
    switch (platform) {
      case "GENERIC":
        return TargetPlatform.GENERIC;
      case "DSA_VCG":
      case "l06":
      case "DSA":
      case "VCG":
        return TargetPlatform.DSA_VCG;
      case "DSA_LAB":
      case "LAB":
        return TargetPlatform.DSA_LAB;
      case "ARDUINO":
      case "ESP32":
        return TargetPlatform.ARDUINO;
      case "RASPBERRY":
      case "RASPBERRYPI":
      case "RASPI":
        return TargetPlatform.RASPBERRY;
      default:
        throw new IllegalArgumentException(
          "0xMT300 Platform " + platform + " is unknown");
    }
  }
}