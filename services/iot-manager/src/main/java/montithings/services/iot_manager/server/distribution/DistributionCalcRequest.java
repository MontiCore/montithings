package montithings.services.iot_manager.server.distribution;

import java.util.Collection;
import java.util.List;

import montithings.services.iot_manager.server.data.DeployClient;

public class DistributionCalcRequest {
  
  private final Collection<DeployClient> deployTargets;
  private final List<String> components;
  
  public DistributionCalcRequest(Collection<DeployClient> deployTargets, List<String> components) {
    this.deployTargets = deployTargets;
    this.components = components;
  }
  
  public Collection<DeployClient> getDeployTargets() {
    return deployTargets;
  }
  
  public List<String> getComponents() {
    return components;
  }
  
}
