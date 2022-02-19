// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution;

import montithings.services.iot_manager.server.data.DeployClient;

import java.util.Collection;
import java.util.List;

public class DistributionSuggestionRequest {
  
  private final Collection<DeployClient> targets;
  private final List<String> components;
  private final int offset;
  private final int maxCount;
  
  /**
   * @param targets The targets available.
   * @param components The components to deploy.
   * @param offset The first {@code offset} suggestions will be skipped.
   * @param maxCount How many suggestions to calculate (if possible).
   */
  public DistributionSuggestionRequest(Collection<DeployClient> targets, List<String> components, int offset, int maxCount) {
    super();
    this.targets = targets;
    this.components = components;
    this.offset = offset;
    this.maxCount = maxCount;
  }
  
  public Collection<DeployClient> getTargets() {
    return targets;
  }
  
  public List<String> getComponents() {
    return components;
  }
  
  public int getOffset() {
    return offset;
  }
  
  public int getMaxCount() {
    return maxCount;
  }
  
}
