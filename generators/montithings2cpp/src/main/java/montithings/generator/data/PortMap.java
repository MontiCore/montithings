// (c) https://github.com/MontiCore/monticore
package montithings.generator.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Assigns network ports to component instances
 */
public class PortMap {
  protected static final int PORT_BASE = 30000;
  protected static int maximumPort = PORT_BASE;

  Map<String, String> managementPorts = new HashMap<>();
  Map<String, String> communicationPorts = new HashMap<>();

  protected void ensureExists(String componentInstanceName) {
    if (!managementPorts.containsKey(componentInstanceName)) {
      managementPorts.put(componentInstanceName, String.valueOf(maximumPort++));
    }
    if (!communicationPorts.containsKey(componentInstanceName)) {
      communicationPorts.put(componentInstanceName, String.valueOf(maximumPort++));
    }
  }

  public String getManagementPort(String componentInstanceName) {
    ensureExists(componentInstanceName);
    return managementPorts.get(componentInstanceName);
  }

  public String getCommunicationPort(String componentInstanceName) {
    ensureExists(componentInstanceName);
    return communicationPorts.get(componentInstanceName);
  }
}
