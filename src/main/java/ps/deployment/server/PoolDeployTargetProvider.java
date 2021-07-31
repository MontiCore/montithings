package ps.deployment.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.DeploymentInfo;
import ps.deployment.server.data.Distribution;
import ps.deployment.server.data.NetworkInfo;
import ps.deployment.server.distribution.listener.IDeployStatusListener;
import ps.deployment.server.distribution.listener.VoidDeployStatusListener;
import ps.deployment.server.exception.DeploymentException;

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
  
  private IDeployTargetProvider getTargetForClient(String clientID) {
    // Loop through each provider & search for client with this ID.
    for(IDeployTargetProvider provider : providers) {
      for(DeployClient client : provider.getClients()) {
        if(clientID.equals(client.getClientID())) {
          return provider;
        }
      }
    }
    return null;
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
  
}
