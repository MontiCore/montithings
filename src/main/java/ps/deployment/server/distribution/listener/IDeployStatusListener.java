package ps.deployment.server.distribution.listener;

import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.Distribution;

public interface IDeployStatusListener {
  
  public void onClientOnline(DeployClient client);
  public void onClientOffline(DeployClient client);
  
  public void onDeploymentUpdated(Distribution distribution);
  
}
