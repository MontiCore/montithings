package montithings.services.iot_manager.server.azurecloud;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import montithings.services.iot_manager.server.IDeployTargetProvider;
import montithings.services.iot_manager.server.data.DeployClient;
import montithings.services.iot_manager.server.data.DeploymentInfo;
import montithings.services.iot_manager.server.data.Distribution;
import montithings.services.iot_manager.server.data.LocationSpecifier;
import montithings.services.iot_manager.server.data.NetworkInfo;
import montithings.services.iot_manager.server.distribution.listener.IDeployStatusListener;
import montithings.services.iot_manager.server.distribution.listener.VoidDeployStatusListener;
import montithings.services.iot_manager.server.exception.DeploymentException;

public class AzureCloudTargetProvider implements IDeployTargetProvider {
  private final long providerID;

  private IDeployStatusListener listener = new VoidDeployStatusListener();

  private List<DeployClient> clients = new ArrayList<>();

  public AzureCloudTargetProvider(long providerID) {
    this.providerID = providerID;
  }

  @Override
  public void deploy(Distribution distribution, DeploymentInfo deploymentInfo, NetworkInfo net)
      throws DeploymentException {
    try {
      ProcessBuilder builder = new ProcessBuilder();
      Process process;
      process = builder.start();
      builder.command("terraform apply -auto-approve");
      StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
      Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
      int exitCode = process.waitFor();
      assert exitCode == 0;
      future.get(10, TimeUnit.SECONDS);
    } catch (TimeoutException | ExecutionException | InterruptedException | IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Collection<DeployClient> getClients() {
    return clients;
  }

  @Override
  public void setStatusListener(IDeployStatusListener listener) {
    this.listener = listener;
  }

  @Override
  public void initialize() throws DeploymentException {
    LocationSpecifier location = new LocationSpecifier("unspecified", "unspecified", "unspecified");
    DeployClient azureCloud = DeployClient.create("ec259dea-3a13-4815-bea7-68d2faac631f", true, location, providerID,
        "");
    this.clients.add(azureCloud);
  }

  @Override
  public void close() throws DeploymentException {
    System.out.println("Close AzureContainerAppsTargetProvider");
  }
}
