// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server;

import montithings.services.iot_manager.server.data.DeployClient;
import montithings.services.iot_manager.server.data.DeploymentInfo;
import montithings.services.iot_manager.server.data.Distribution;
import montithings.services.iot_manager.server.data.NetworkInfo;
import montithings.services.iot_manager.server.distribution.listener.IDeployStatusListener;
import montithings.services.iot_manager.server.exception.DeploymentException;

import java.util.Collection;

/**
 * An instance of {@link IDeployTargetProvider} is responsible for providing and
 * managing access to deployment clients.
 */
public interface IDeployTargetProvider {
  
  public void deploy(Distribution distribution, DeploymentInfo deploymentInfo, NetworkInfo net) throws DeploymentException;
  
  public Collection<DeployClient> getClients();
  
  public void setStatusListener(IDeployStatusListener listener);
  
  public void initialize() throws DeploymentException;
  
  public void close() throws DeploymentException;
  
}
