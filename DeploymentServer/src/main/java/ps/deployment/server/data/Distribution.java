package ps.deployment.server.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Distribution {
  
  private final Map<String, String[]> distributionMap = new HashMap<>();
  
  /**
   * @param distribution A map that assigns several executables (values) to each client (keys).
   * */
  public Distribution(Map<String, String[]> distribution) {
    this.distributionMap.putAll(distribution);
  }
  
  public Map<String, String[]> getDistributionMap() {
    return this.distributionMap;
  }
  
  public static Distribution from(Map<String, List<String>> distribution) {
    HashMap<String, String[]> dmap = new HashMap<>();
    for (Entry<String, List<String>> e : distribution.entrySet()) {
      dmap.put(e.getKey(), e.getValue().toArray(new String[e.getValue().size()]));
    }
    return new Distribution(dmap);
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Distribution[{");
    for (Entry<String, String[]> e : distributionMap.entrySet()) {
      sb.append(e.getKey()).append("=").append(Arrays.toString(e.getValue())).append(", ");
    }
    if (distributionMap.size() > 0) {
      // remove last comma and space
      sb.setLength(sb.length() - 2);
    }
    return sb.append("}]").toString();
  }
  
}
