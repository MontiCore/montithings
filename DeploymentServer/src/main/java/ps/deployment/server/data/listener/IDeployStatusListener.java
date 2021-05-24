package ps.deployment.server.data.listener;

import ps.deployment.server.data.DeployClient;

public interface IDeployStatusListener {
  
  public void onClientOnline(DeployClient client);
  public void onClientOffline(DeployClient client);
  
}
