package ps.deployment.server.distribution.listener;

import ps.deployment.server.data.DeployClient;

public class VoidDeployStatusListener implements IDeployStatusListener {
  
  @Override
  public void onClientOnline(DeployClient client) {
    // TODO REMOVE
    System.out.println("[DEBUG] onClientOnline: "+client.getClientID());
  }
  
  @Override
  public void onClientOffline(DeployClient client) {
    // TODO REMOVE
    System.out.println("[DEBUG] onClientOffline: "+client.getClientID());
  }
  
}
