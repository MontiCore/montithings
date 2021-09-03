// (c) https://github.com/MontiCore/monticore
package ps.deployment.server.util;

import java.util.HashMap;
import java.util.List;

public class InstanceNameResolver {
  
  // assign each instance name in lower case the correctly cased name
  private final HashMap<String, String> nameMap = new HashMap<>();
  
  public InstanceNameResolver(List<String> instanceNames) {
    // cache lower case names for easy access
    for(String name : instanceNames) {
      nameMap.put(name.toLowerCase(), name);
    }
  }
  
  /**
   * Corrects the case of an instance name.
   * E.g.: hierarchy.test.value -> hierarchy.Test.value
   * */
  public String resolveOnlyCase(String wrongCasedInstanceName) {
    return nameMap.getOrDefault(wrongCasedInstanceName.toLowerCase(), wrongCasedInstanceName);
  }
  
  /**
   * Resolves a prolog name to the model name.
   * E.g. HierarchyIotestTempsensor -> hierarchy.IOTest.tempSensor
   */
  public String resolveFromPrologName(String prologInstanceName) {
    String lowerCaseName = transformInstanceName(prologInstanceName).toLowerCase();
    return resolveOnlyCase(lowerCaseName);
  }
  
  /**
   * Transforms an instance name from Prolog format (e.g. HierarchyExampleSink)
   * to its model name (e.g. hierarchy.example.sink)
   */
  private static String transformInstanceName(String prologName) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < prologName.length(); i++) {
      char c = prologName.charAt(i);
      if (Character.isUpperCase(c)) {
        // if this character is in upper case, also insert a dot
        if (sb.length() > 0) {
          // the first character should not be preceded by a dot
          sb.append('.');
        }
        
        sb.append(Character.toLowerCase(c));
      }
      else {
        // if this character is in lower case, just append it
        sb.append(c);
      }
    }
    return sb.toString();
  }
  
}
