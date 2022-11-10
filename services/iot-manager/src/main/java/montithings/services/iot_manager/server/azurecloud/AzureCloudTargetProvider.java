package montithings.services.iot_manager.server.azurecloud;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.time.Duration;
import java.util.Collection;

import montithings.services.iot_manager.server.IDeployTargetProvider;
import montithings.services.iot_manager.server.data.DeployClient;
import montithings.services.iot_manager.server.data.DeploymentInfo;
import montithings.services.iot_manager.server.data.Distribution;
import montithings.services.iot_manager.server.data.LocationSpecifier;
import montithings.services.iot_manager.server.data.NetworkInfo;
import montithings.services.iot_manager.server.distribution.listener.IDeployStatusListener;
import montithings.services.iot_manager.server.dto.ApplyTerraformDTO;
import montithings.services.iot_manager.server.exception.DeploymentException;

public class AzureCloudTargetProvider implements IDeployTargetProvider {
  private final long providerID;
  private final String terraformDeployerUrl;
  private final AzureCredentials credentials;
  // TODO: Eventually read token as env var
  private final String token = "03c11e6e-41fc-4862-a37a-6dbc46a834b9";
  private final Duration timeout = Duration.ofMinutes(15);
  private final HttpClient httpClient = HttpClient.newBuilder()
      .version(Version.HTTP_2)
      .followRedirects(Redirect.NORMAL)
      .build();
  private List<DeployClient> clients = new ArrayList<>();

  public AzureCloudTargetProvider(long providerID, String terraformDeployerUrl, AzureCredentials credentials) {
    this.providerID = providerID;
    this.terraformDeployerUrl = terraformDeployerUrl;
    this.credentials = credentials;
  }

  @Override
  public void deploy(Distribution distribution, DeploymentInfo deploymentInfo, NetworkInfo net)
      throws DeploymentException {
    try {
      ApplyTerraformDTO body = new ApplyTerraformDTO(credentials, deploymentInfo.getTerraformInfos());
      HttpResponse<String> response = this.applyTerraform(body);

      if (response.statusCode() != 201) {
        String errorMessage = "Terraform apply failed with status code " + response.statusCode()
            + " and error message: " + response.body();
        throw new DeploymentException(errorMessage);
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  private HttpResponse<String> applyTerraform(ApplyTerraformDTO body) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest
        .newBuilder()
        .uri(URI.create(this.terraformDeployerUrl + "/apply"))
        .POST(HttpRequest.BodyPublishers.ofString(body.toJson()))
        .header("Accept", "application/json")
        .header("X-Token", token)
        .timeout(timeout)
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    return response;
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
    LocationSpecifier location = new LocationSpecifier("unspecified", "unspecified", "unspecified");
    DeployClient azureCloud = DeployClient.create("ec259dea-3a13-4815-bea7-68d2faac631f", true, location, providerID,
        "");
    this.clients.add(azureCloud);
  }

  @Override
  public void close() throws DeploymentException {
    System.out.println("Close AzureCloudTargetProvider");
  }
}
