// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution;

import montithings.services.iot_manager.server.data.Distribution;
import montithings.services.iot_manager.server.distribution.suggestion.Suggestion;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface IDistributionCalculator {
  
  public CompletableFuture<Distribution> computeDistribution(DistributionCalcRequest request);
  public CompletableFuture<Map<Distribution, List<Suggestion>>> computeDistributionSuggestion(DistributionSuggestionRequest request);
  
}
