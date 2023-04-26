package montithings.services.iot_manager.server.localProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import montithings.services.iot_manager.server.IDeployTargetProvider;
import montithings.services.iot_manager.server.data.DeployClient;
import montithings.services.iot_manager.server.data.DeploymentInfo;
import montithings.services.iot_manager.server.data.Distribution;
import montithings.services.iot_manager.server.data.LocationSpecifier;
import montithings.services.iot_manager.server.data.NetworkInfo;
import montithings.services.iot_manager.server.distribution.listener.IDeployStatusListener;

/**
 * Use this target provider for quick local tests
 */
public class LocalTargetProvider implements IDeployTargetProvider {
  // Mock device
  private final String deviceId = "uselessdevice";
  // Provider id of this target provider
  private final long providerID;
  // List of clients managed by this target provider
  private List<DeployClient> clients = new ArrayList<>();

  public LocalTargetProvider(long providerID) {
    this.providerID = providerID;
  }

  @Override
  public void deploy(Distribution distribution, DeploymentInfo deploymentInfo, NetworkInfo net) {
    System.out.println("LocalTargetProvider: Simulate deploy()");
  }

  @Override
  public Collection<DeployClient> getClients() {
    return clients;
  }

  @Override
  public void setStatusListener(IDeployStatusListener listener) {
    System.out.println("LocalTargetProvider: Simulate setStatusListener()");
  }

  @Override
  public void initialize() {
    LocationSpecifier location = new LocationSpecifier("unspecified", "unspecified", "unspecified");
    DeployClient uselessClient = DeployClient.create(
        deviceId, true, location, providerID,
        "");
    this.clients.add(uselessClient);
  }

  @Override
  public void close() {
    System.out.println("LocalTargetProvider: Simulate close()");
  }
}
