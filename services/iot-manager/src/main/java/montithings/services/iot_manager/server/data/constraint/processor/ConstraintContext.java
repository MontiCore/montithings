// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data.constraint.processor;

import java.util.Collection;

import montithings.services.iot_manager.server.data.DeployClient;
import montithings.services.iot_manager.server.data.DeploymentConfiguration;

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
