package montithings.services.iot_manager.server.azure;

public class AzureIotUtils {

  public static String getDeviceIdentifier(String deviceName, String iotHubConnectionString) {
    String hostName = iotHubConnectionString.substring(
      iotHubConnectionString.indexOf("HostName=") + 9, iotHubConnectionString.indexOf(";")); // "HostName=".length() = 9
    return hostName + ":" + deviceName;
  }

  public static String getDeviceName(String deviceId, String iotHubConnectionString) {
    String hostName = iotHubConnectionString.substring(
      iotHubConnectionString.indexOf("HostName=") + 9, iotHubConnectionString.indexOf(";")); // "HostName=".length() = 9
    return deviceId.substring((hostName + ":").length());
  }
}
