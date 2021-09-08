// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import montithings.services.iot_manager.server.data.DeployClient;
import montithings.services.iot_manager.server.data.Distribution;
import montithings.services.iot_manager.server.distribution.suggestion.Suggestion;

public interface IDistributionCalculator {
  
  public CompletableFuture<Distribution> computeDistribution(Collection<DeployClient> targets, List<String> components);
  public CompletableFuture<Map<Distribution, List<Suggestion>>> computeDistributionSuggestion(DistributionSuggestionRequest request);
  
}
