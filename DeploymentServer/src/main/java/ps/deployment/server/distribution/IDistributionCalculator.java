package ps.deployment.server.distribution;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.Distribution;

public interface IDistributionCalculator {
  
  public CompletableFuture<Distribution> computeDistribution(Collection<DeployClient> targets, List<String> components);
  
}
