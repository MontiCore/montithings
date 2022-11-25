package montithings.services.iot_manager.server.azurecloud;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.StringBuilderWriter;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.Collection;

import montithings.services.iot_manager.server.IDeployTargetProvider;
import montithings.services.iot_manager.server.data.DeployClient;
import montithings.services.iot_manager.server.data.DeploymentInfo;
import montithings.services.iot_manager.server.data.Distribution;
import montithings.services.iot_manager.server.data.LocationSpecifier;
import montithings.services.iot_manager.server.data.NetworkInfo;
import montithings.services.iot_manager.server.data.TerraformInfo;
import montithings.services.iot_manager.server.distribution.listener.IDeployStatusListener;
import montithings.services.iot_manager.server.dto.ApplyTerraformDTO;
import montithings.services.iot_manager.server.exception.DeploymentException;

public class AzureCloudTargetProvider implements IDeployTargetProvider {
  private final String deviceId = "azurecloud";
  private final String baseTfFtl = "templates/azureCloudBaseTf.ftl";
  private final String containerAppTfFtl = "templates/azureCloudContainerappsTf.ftl";
  private final String storageAccountName = "montithings2"; // Must be unique within Azure
  private final long providerID;
  private final String terraformDeployerUrl;
  private final AzureCredentials credentials;
  private final String token = "03c11e6e-41fc-4862-a37a-6dbc46a834b9";
  private final Duration timeout = Duration.ofMinutes(15);
  private List<DeployClient> clients = new ArrayList<>();

  public AzureCloudTargetProvider(long providerID, String terraformDeployerUrl, AzureCredentials credentials) {
    this.providerID = providerID;
    this.terraformDeployerUrl = terraformDeployerUrl;
    this.credentials = credentials;
  }

  @Override
  public void deploy(Distribution distribution, DeploymentInfo deploymentInfo, NetworkInfo net)
      throws DeploymentException {
    List<TerraformInfo> tfInfos = new ArrayList<TerraformInfo>();

    // 1. Get base tf
    tfInfos.add(new TerraformInfo("base.tf", getBaseTf()));

    // 2. Add component specific resources
    for (TerraformInfo tfInfo : deploymentInfo.getTerraformInfos()) {
      tfInfos.add(tfInfo);
    }

    // 3. Generate tf for containerapp executable
    // Map from device to executables on device. For this targetProvider we have
    // only one device. Thus no for loop required
    Map<String, String[]> distributionMap = distribution.getDistributionMap();
    String filecontent = getContainerappTf(distributionMap.get(deviceId), deploymentInfo, net);
    tfInfos.add(new TerraformInfo(deviceId, filecontent));

    // 4. Deploy all terraform files
    ApplyTerraformDTO body = new ApplyTerraformDTO(credentials, tfInfos, storageAccountName);
    this.applyTerraform(body);
  }

  private String getContainerappTf(String[] modules, DeploymentInfo deplInfo, NetworkInfo netInfo) {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setTracing(false);
    GeneratorEngine engine = new GeneratorEngine(setup);
    StringBuilderWriter containerappTf = new StringBuilderWriter();
    engine.generateNoA(this.containerAppTfFtl, containerappTf, modules, deplInfo, netInfo);
    return Base64.getEncoder().encodeToString(containerappTf.toString().getBytes());
  }

  private String getBaseTf() {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setTracing(false);
    GeneratorEngine engine = new GeneratorEngine(setup);
    StringBuilderWriter baseTf = new StringBuilderWriter();
    engine.generateNoA(this.baseTfFtl, baseTf, storageAccountName);
    return Base64.getEncoder().encodeToString(baseTf.toString().getBytes());
  }

  private void applyTerraform(ApplyTerraformDTO body) throws DeploymentException {
    HttpURLConnection connection = null;

    try {
      // 1. Open connection
      System.out.println("Apply terraform. Url: " + this.terraformDeployerUrl + "/apply");
      URL url = new URL(this.terraformDeployerUrl + "/apply");
      connection = (HttpURLConnection) url.openConnection();

      // 2. Prepare request
      System.out.println("Apply terraform. Prepare request. Body: " + body.toJson());
      byte[] postData = body.toJson().getBytes();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      connection.setRequestProperty("Accept", "application/json");
      connection.setRequestProperty("X-Token", token);
      connection.setFixedLengthStreamingMode(postData.length);
      connection.setReadTimeout((int) timeout.toMillis());
      connection.setConnectTimeout((int) timeout.toMillis());
      connection.setUseCaches(false);
      connection.setDoOutput(true);

      // 3. Send request
      System.out.println("Apply terraform. Send request");
      DataOutputStream wr = new DataOutputStream(
          connection.getOutputStream());
      wr.write(postData);
      wr.close();

      // 4. Parse response
      System.out.println("Apply terraform. Parse response");
      if (connection.getResponseCode() != 201) {
        String errorMessage = "Terraform apply failed with status code " + connection.getResponseCode()
            + " and error message: " + getResponseStr(connection);
        throw new DeploymentException(errorMessage);
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw new DeploymentException(e.getMessage());
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  private String getResponseStr(HttpURLConnection connection) throws IOException {
    InputStream is = connection.getInputStream();
    try (BufferedReader rd = new BufferedReader(new InputStreamReader(is))) {
      StringBuffer response = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null) {
        response.append(line);
        response.append('\r');
      }
      return response.toString();
    }
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
  }
}
