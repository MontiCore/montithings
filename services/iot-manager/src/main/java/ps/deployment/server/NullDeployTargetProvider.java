// (c) https://github.com/MontiCore/monticore
package ps.deployment.server;

import java.util.Collection;
import java.util.Collections;

import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.DeploymentInfo;
import ps.deployment.server.data.Distribution;
import ps.deployment.server.data.NetworkInfo;
import ps.deployment.server.distribution.listener.IDeployStatusListener;
import ps.deployment.server.exception.DeploymentException;

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
