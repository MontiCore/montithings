package montithings.services.iot_manager.server.azurecloud;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.StringBuilderWriter;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;

import java.util.Collection;

import montithings.services.iot_manager.server.IDeployTargetProvider;
import montithings.services.iot_manager.server.TfResourceManager;
import montithings.services.iot_manager.server.data.DeployClient;
import montithings.services.iot_manager.server.data.DeploymentInfo;
import montithings.services.iot_manager.server.data.Distribution;
import montithings.services.iot_manager.server.data.LocationSpecifier;
import montithings.services.iot_manager.server.data.NetworkInfo;
import montithings.services.iot_manager.server.data.TerraformInfo;
import montithings.services.iot_manager.server.distribution.listener.IDeployStatusListener;
import montithings.services.iot_manager.server.exception.DeploymentException;

/**
 * Runs components on Azure Container Instances
 */
public class AzureCloudTargetProvider implements IDeployTargetProvider {
  // Cloud acts as a single device with id "azurecloud"
  private final String deviceId = "azurecloud";
  // Path to container instances terraform freemarker template
  private final String containerInstancesTf = "templates/azureCloudContainerInstancesTf.ftl";
  // Provider id of this target provider
  private final long providerID;
  // List of clients managed by this target provider
  // For cloud we have a single client
  private List<DeployClient> clients = new ArrayList<>();
  // Resource manager class that is used to provision container instances
  private TfResourceManager tfResourceManager;
  // Reference to applied container instance terraform info used to destroy
  // resource on close
  private TerraformInfo appliedContainerInstanceTf;

  public AzureCloudTargetProvider(long providerID) {
    this.providerID = providerID;
  }

  public void setTfResourceManager(TfResourceManager tfResourceManager) {
    this.tfResourceManager = tfResourceManager;
  }

  @Override
  public void deploy(Distribution distribution, DeploymentInfo deploymentInfo, NetworkInfo net)
      throws DeploymentException {
    if (tfResourceManager == null) {
      throw new DeploymentException("TfResourceManager not set");
    }

    Map<String, String[]> distributionMap = distribution.getDistributionMap();

    // Get terraform for container instance executable
    this.appliedContainerInstanceTf = getContainerInstanceTf(distributionMap.get(deviceId), deploymentInfo, net,
        tfResourceManager.getEnvvars());

    // Deploy all terraform files
    tfResourceManager.apply(distribution, this.appliedContainerInstanceTf);
  }

  /**
   * Generates container instance executable terraform resource
   * 
   * @param modules
   * @param deplInfo
   * @param netInfo
   * @param envvars
   * @return
   */
  private TerraformInfo getContainerInstanceTf(String[] modules, DeploymentInfo deplInfo, NetworkInfo netInfo,
      Map<String, String> envvars) {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setTracing(false);
    GeneratorEngine engine = new GeneratorEngine(setup);
    StringBuilderWriter containerinstanceTf = new StringBuilderWriter();
    engine.generateNoA(this.containerInstancesTf, containerinstanceTf, modules, deplInfo, netInfo, envvars);
    String filecontent = Base64.getEncoder().encodeToString(containerinstanceTf.toString().getBytes());
    return new TerraformInfo(deviceId, filecontent);
  }

  @Override
  public Collection<DeployClient> getClients() {
    return clients;
  }

  @Override
  public void setStatusListener(IDeployStatusListener listener) {
    System.out.println("Set status listener AzureCloudTargetProvider");
  }

  @Override
  public void initialize() throws DeploymentException {
    this.setupAzureClient();
  }

  private void setupAzureClient() {
    LocationSpecifier location = new LocationSpecifier("unspecified", "unspecified", "unspecified");
    DeployClient azureCloud = DeployClient.create(
        deviceId, true, location, providerID,
        "");
    this.clients.add(azureCloud);
  }

  @Override
  public void close() throws DeploymentException {
    System.out.println("Close AzureCloudTargetProvider");
    if (this.appliedContainerInstanceTf != null && this.tfResourceManager != null) {
      System.out.println("Destroy Azure Container Instances");
      tfResourceManager.destroy(this.appliedContainerInstanceTf);
    }
  }
}
