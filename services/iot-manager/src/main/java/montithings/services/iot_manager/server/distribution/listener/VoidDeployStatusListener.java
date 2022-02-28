// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution.listener;

import montithings.services.iot_manager.server.data.DeployClient;
import montithings.services.iot_manager.server.data.Distribution;

public class VoidDeployStatusListener implements IDeployStatusListener {
  
  @Override
  public void onClientOnline(DeployClient client) {
    // intentionally left empty
  }
  
  @Override
  public void onClientOffline(DeployClient client) {
    // intentionally left empty
  }

  @Override
  public void onDeploymentUpdated(Distribution dist) {
    // intentionally left empty
  }
  
}
