package ps.deployment.server.distribution;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.Distribution;
import ps.deployment.server.distribution.suggestion.Suggestion;

public interface IDistributionCalculator {
  
  public CompletableFuture<Distribution> computeDistribution(Collection<DeployClient> targets, List<String> components);
  public CompletableFuture<Map<Distribution, List<Suggestion>>> computeDistributionSuggestion(DistributionSuggestionRequest request);
  
}
