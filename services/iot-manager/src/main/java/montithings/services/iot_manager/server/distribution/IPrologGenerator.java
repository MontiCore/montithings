// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution;

import montithings.services.iot_manager.server.data.DeployClient;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface IPrologGenerator {
  
  /**
   * Generates the facts.pl as Prolog source.
   * @return Future for Prolog source code as {@link String}
   * */
  public CompletableFuture<String> generateFacts(Collection<DeployClient> clients);
  
  /**
   * Generates the query.pl as Prolog source.
   * @return Future for Prolog source code as {@link String}
   * */
  public CompletableFuture<String> generateQuery(String jsonConfig);
  
}
