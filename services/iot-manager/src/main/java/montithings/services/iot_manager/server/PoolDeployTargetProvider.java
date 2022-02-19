// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server;

import montithings.services.iot_manager.server.data.DeployClient;
import montithings.services.iot_manager.server.data.DeploymentInfo;
import montithings.services.iot_manager.server.data.Distribution;
import montithings.services.iot_manager.server.data.NetworkInfo;
import montithings.services.iot_manager.server.distribution.listener.IDeployStatusListener;
import montithings.services.iot_manager.server.distribution.listener.VoidDeployStatusListener;
import montithings.services.iot_manager.server.exception.DeploymentException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PoolDeployTargetProvider implements IDeployTargetProvider {

  private final Collection<IDeployTargetProvider> providers = new ArrayList<>();
  private IDeployStatusListener listener = new VoidDeployStatusListener();
  
  public void addProvider(IDeployTargetProvider provider) {
    this.providers.add(provider);
    provider.setStatusListener(this.listener);
    // notify the listener about the added clients
    for(DeployClient client : provider.getClients()) {
      listener.onClientOnline(client);
    }
  }
  
  public void removeProvider(IDeployTargetProvider provider) {
    this.providers.remove(provider);
    provider.setStatusListener(new VoidDeployStatusListener());
    // notify the listener about the removed clients
    for(DeployClient client : provider.getClients()) {
      listener.onClientOffline(client);
    }
  }
  
  @Override
  public void deploy(Distribution distribution, DeploymentInfo deploymentInfo, NetworkInfo net) throws DeploymentException {
    for(IDeployTargetProvider target : this.providers) {
      target.deploy(distribution, deploymentInfo, net);
    }
  }

  @Override
  public Collection<DeployClient> getClients() {
    List<DeployClient> clients = new ArrayList<>();
    for(IDeployTargetProvider provider : providers) {
      clients.addAll(provider.getClients());
    }
    return Collections.unmodifiableCollection(clients);
  }

  @Override
  public void setStatusListener(IDeployStatusListener listener) {
    this.listener = listener;
    for(IDeployTargetProvider provider : providers) {
      provider.setStatusListener(listener);
    }
  }

  @Override
  public void initialize() throws DeploymentException {
    for(IDeployTargetProvider prov : providers) {
      try {
        prov.initialize();
      } catch(DeploymentException e) {
        // If one provider fails to initialize, this should not hinder the other providers from working.
        System.err.println("Failed to initialize provider: "+e.getMessage());
      }
    }
  }

  @Override
  public void close() throws DeploymentException {
    for(IDeployTargetProvider prov : providers) {
      try {
        prov.close();
      } catch(DeploymentException e) {
        e.printStackTrace();
      }
    }
  }
  
}
