// (c) https://github.com/MontiCore/monticore
package ps.deployment.server.distribution.listener;

import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.Distribution;

public class VoidDeployStatusListener implements IDeployStatusListener {
  
  @Override
  public void onClientOnline(DeployClient client) {
    
  }
  
  @Override
  public void onClientOffline(DeployClient client) {
    
  }

  @Override
  public void onDeploymentUpdated(Distribution dist) {
    
  }
  
}
