package montithings.services.iot_manager.server.azurecloud;

import java.util.ArrayList;
import java.util.List;
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
  private final long providerID;
  private final String terraformDeployerUrl;
  private final AzureCredentials credentials;
  // TODO: Read token as env var
  private final String token = "03c11e6e-41fc-4862-a37a-6dbc46a834b9";
  private final Duration timeout = Duration.ofMinutes(15);
  private List<DeployClient> clients = new ArrayList<>();
  // Base64 encoded string of base.tf
  private final String baseTf = "dGVycmFmb3JtIHsNCiAgcmVxdWlyZWRfcHJvdmlkZXJzIHsNCiAgICBhenVyZXJtID0gew0KICAgICAgc291cmNlID0gImhhc2hpY29ycC9henVyZXJtIg0KICAgIH0NCiAgICBhemFwaSA9IHsNCiAgICAgIHNvdXJjZSA9ICJBenVyZS9hemFwaSINCiAgICB9DQogIH0NCn0NCg0KcHJvdmlkZXIgImF6dXJlcm0iIHsNCiAgZmVhdHVyZXMge30NCn0NCg0KcHJvdmlkZXIgImF6YXBpIiB7fQ0KDQp2YXJpYWJsZSAibG9jYXRpb24iIHsNCiAgdHlwZSAgICAgICAgPSBzdHJpbmcNCiAgZGVmYXVsdCAgICAgPSAiZ2VybWFueXdlc3RjZW50cmFsIg0KICBkZXNjcmlwdGlvbiA9ICJEZXNpcmVkIEF6dXJlIFJlZ2lvbiINCn0NCg0KdmFyaWFibGUgInJlZ2lzdHJ5UHdkIiB7DQogIHR5cGUgICAgICAgID0gc3RyaW5nDQogIGRlZmF1bHQgICAgID0gIjJtZi9QaldXQlZkazYwSWxldEhMOVhXaVl0UFRxMUJxIg0KICBkZXNjcmlwdGlvbiA9ICJQYXNzd29yZCB0byBjb25uZWN0IHdpdGggQXp1cmUgcmVnaXN0cnkgYW5kIHB1bGwgZGF0YSBmcm9tIg0KfQ0KDQpyZXNvdXJjZSAiYXp1cmVybV9yZXNvdXJjZV9ncm91cCIgInJnIiB7DQogIG5hbWUgICAgID0gInJnLXRlcnJhZm9ybSINCiAgbG9jYXRpb24gPSB2YXIubG9jYXRpb24NCn0NCg0KcmVzb3VyY2UgImF6dXJlcm1fbG9nX2FuYWx5dGljc193b3Jrc3BhY2UiICJsYXciIHsNCiAgbmFtZSAgICAgICAgICAgICAgICA9ICJsYXctdGVycmFmb3JtIg0KICByZXNvdXJjZV9ncm91cF9uYW1lID0gYXp1cmVybV9yZXNvdXJjZV9ncm91cC5yZy5uYW1lDQogIGxvY2F0aW9uICAgICAgICAgICAgPSBhenVyZXJtX3Jlc291cmNlX2dyb3VwLnJnLmxvY2F0aW9uDQogIHNrdSAgICAgICAgICAgICAgICAgPSAiUGVyR0IyMDE4Ig0KICByZXRlbnRpb25faW5fZGF5cyAgID0gOTANCn0NCg0KcmVzb3VyY2UgImF6YXBpX3Jlc291cmNlIiAibWVudiIgew0KICB0eXBlICAgICAgPSAiTWljcm9zb2Z0LkFwcC9tYW5hZ2VkRW52aXJvbm1lbnRzQDIwMjItMDMtMDEiDQogIHBhcmVudF9pZCA9IGF6dXJlcm1fcmVzb3VyY2VfZ3JvdXAucmcuaWQNCiAgbG9jYXRpb24gID0gYXp1cmVybV9yZXNvdXJjZV9ncm91cC5yZy5sb2NhdGlvbg0KICBuYW1lICAgICAgPSAibWVudi10ZXJyYWZvcm0iDQoNCiAgYm9keSA9IGpzb25lbmNvZGUoew0KICAgIHByb3BlcnRpZXMgPSB7DQogICAgICBhcHBMb2dzQ29uZmlndXJhdGlvbiA9IHsNCiAgICAgICAgZGVzdGluYXRpb24gPSAibG9nLWFuYWx5dGljcyINCiAgICAgICAgbG9nQW5hbHl0aWNzQ29uZmlndXJhdGlvbiA9IHsNCiAgICAgICAgICBjdXN0b21lcklkID0gYXp1cmVybV9sb2dfYW5hbHl0aWNzX3dvcmtzcGFjZS5sYXcud29ya3NwYWNlX2lkDQogICAgICAgICAgc2hhcmVkS2V5ICA9IGF6dXJlcm1fbG9nX2FuYWx5dGljc193b3Jrc3BhY2UubGF3LnByaW1hcnlfc2hhcmVkX2tleQ0KICAgICAgICB9DQogICAgICB9DQogICAgICB6b25lUmVkdW5kYW50ID0gZmFsc2UNCiAgICB9DQogIH0pDQp9DQo=";

  public AzureCloudTargetProvider(long providerID, String terraformDeployerUrl, AzureCredentials credentials) {
    this.providerID = providerID;
    this.terraformDeployerUrl = terraformDeployerUrl;
    this.credentials = credentials;
  }

  @Override
  public void deploy(Distribution distribution, DeploymentInfo deploymentInfo, NetworkInfo net)
      throws DeploymentException {
    ApplyTerraformDTO body = new ApplyTerraformDTO(credentials, deploymentInfo.getTerraformInfos());
    this.applyTerraform(body);
  }

  private void applyTerraform(ApplyTerraformDTO body) throws DeploymentException {
    HttpURLConnection connection = null;

    try {
      // 1. Open connection
      URL url = new URL(this.terraformDeployerUrl + "/apply");
      connection = (HttpURLConnection) url.openConnection();

      // 2. Prepare request
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
      DataOutputStream wr = new DataOutputStream(
          connection.getOutputStream());
      wr.write(postData);
      wr.close();

      // 4. Parse response
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
    this.applyBaseTf();
  }

  private void setupAzureClient() {
    LocationSpecifier location = new LocationSpecifier("unspecified", "unspecified", "unspecified");
    DeployClient azureCloud = DeployClient.create("ec259dea-3a13-4815-bea7-68d2faac631f", true, location, providerID,
        "");
    this.clients.add(azureCloud);
  }

  private void applyBaseTf() throws DeploymentException {
    TerraformInfo[] files = new TerraformInfo[1];
    files[0] = new TerraformInfo("base.tf", baseTf);
    ApplyTerraformDTO dto = new ApplyTerraformDTO(credentials, files);
    this.applyTerraform(dto);
  }

  @Override
  public void close() throws DeploymentException {
    System.out.println("Close AzureCloudTargetProvider");
  }
}
