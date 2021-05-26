package ps.deployment.server.distribution.listener;

import ps.deployment.server.data.DeployClient;

public interface IDeployStatusListener {
  
  public void onClientOnline(DeployClient client);
  public void onClientOffline(DeployClient client);
  
}
