// (c) https://github.com/MontiCore/monticore
package ps.deployment.server.util;

import java.util.List;

import com.google.common.collect.Lists;

public class MontiThingsUtil {
  
  public static String getRunArgumentsAsString(String instanceName, String mqttHost, int mqttPort) {
    return String.join(" ", getRunArguments(instanceName, mqttHost, mqttPort));
  }
  
  public static List<String> getRunArguments(String instanceName, String mqttHost, int mqttPort) {
    return Lists.newArrayList("--name",instanceName,"--brokerHostname", mqttHost, "--brokerPort", String.valueOf(mqttPort));
  }
  
}
