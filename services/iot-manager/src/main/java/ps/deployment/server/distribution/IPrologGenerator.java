// (c) https://github.com/MontiCore/monticore
package ps.deployment.server.distribution;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import ps.deployment.server.data.DeployClient;

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
