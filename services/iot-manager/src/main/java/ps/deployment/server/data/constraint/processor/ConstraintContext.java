// (c) https://github.com/MontiCore/monticore
package ps.deployment.server.data.constraint.processor;

import java.util.Collection;

import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.DeploymentConfiguration;

public class ConstraintContext {
  
  private final DeploymentConfiguration config;
  private final Collection<DeployClient> clients;
  
  public ConstraintContext(DeploymentConfiguration config, Collection<DeployClient> collection) {
    super();
    this.config = config;
    this.clients = collection;
  }
  
  public DeploymentConfiguration getConfig() {
    return config;
  }
  
  public Collection<DeployClient> getClients() {
    return clients;
  }
  
}
