package montithings.services.iot_manager.server.distribution;

import montithings.services.iot_manager.server.data.DeployClient;
import montithings.services.iot_manager.server.data.Distribution;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class DistributionCalcRequest {
  
  private final Collection<DeployClient> deployTargets;
  private final List<String> components;
  private @Nullable Distribution referenceDistribution = null;
  
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
  
  /**
   * @see {@link DistributionCalcRequest::setReferenceDistribution}
   * */
  public @Nullable Distribution getReferenceDistribution() {
    return referenceDistribution;
  }
  
  /**
   * Setter for the reference distribution used for the calculation.
   * If an existing distribution is specified, the system will try to make as few changes as possible.
   * */
  public void setReferenceDistribution(@Nullable Distribution referenceDistribution) {
    this.referenceDistribution = referenceDistribution;
  }
  
}
