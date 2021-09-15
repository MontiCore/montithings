// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server;

import java.util.Collection;
import java.util.Collections;

import montithings.services.iot_manager.server.data.DeployClient;
import montithings.services.iot_manager.server.data.DeploymentInfo;
import montithings.services.iot_manager.server.data.Distribution;
import montithings.services.iot_manager.server.data.NetworkInfo;
import montithings.services.iot_manager.server.distribution.listener.IDeployStatusListener;
import montithings.services.iot_manager.server.exception.DeploymentException;

public class NullDeployTargetProvider implements IDeployTargetProvider {
  
  @Override
  public void deploy(Distribution distribution, DeploymentInfo deploymentInfo, NetworkInfo net) throws DeploymentException {
    
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Collection<DeployClient> getClients() {
    return Collections.EMPTY_LIST;
  }
  
  @Override
  public void setStatusListener(IDeployStatusListener listener) {
    
  }
  
  @Override
  public void initialize() throws DeploymentException {
    
  }
  
  @Override
  public void close() throws DeploymentException {
    
  }
  
}
