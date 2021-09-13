// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution.listener;

import montithings.services.iot_manager.server.data.DeployClient;
import montithings.services.iot_manager.server.data.Distribution;

public interface IDeployStatusListener {
  
  public void onClientOnline(DeployClient client);
  public void onClientOffline(DeployClient client);
  
  public void onDeploymentUpdated(Distribution distribution);
  
}
