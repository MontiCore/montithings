// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.util;

import com.google.common.collect.Lists;

import java.util.List;

public class MontiThingsUtil {
  
  public static String getRunArgumentsAsString(String instanceName, String mqttHost, int mqttPort) {
    return String.join(" ", getRunArguments(instanceName, mqttHost, mqttPort));
  }
  
  public static List<String> getRunArguments(String instanceName, String mqttHost, int mqttPort) {
    return Lists.newArrayList("--name",instanceName,"--brokerHostname", mqttHost, "--brokerPort", String.valueOf(mqttPort));
  }
  
}
