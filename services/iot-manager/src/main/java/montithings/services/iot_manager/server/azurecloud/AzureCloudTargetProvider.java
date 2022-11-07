package montithings.services.iot_manager.server.azurecloud;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.jose4j.json.internal.json_simple.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collection;

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
      String filename = "test.tf";
      String filecontent = this.fileToBase64Str(filename);
      TerraformFileInfo file = new TerraformFileInfo(filename, filecontent);
      TerraformBody body = new TerraformBody(file);
      byte[] postData = body.toJson().getBytes();
      String response = this.httpPost("http://localhost:5000/apply", postData);
      System.out.println(response);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String fileToBase64Str(String fileName) throws IOException {
    File file = new File(fileName);
    byte[] fileContent = Files.readAllBytes(file.toPath());
    return Base64.getEncoder().encodeToString(fileContent);
  }

  private String httpPost(String targetURL, byte[] postData) {
    HttpURLConnection connection = null;

    try {
      // Create connection
      URL url = new URL(targetURL);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type",
          "application/json; charset=UTF-8");
      connection.setRequestProperty("Content-Language", "en-US");

      connection.setFixedLengthStreamingMode(postData.length);
      connection.setUseCaches(false);
      connection.setDoOutput(true);

      // Send request
      DataOutputStream wr = new DataOutputStream(
          connection.getOutputStream());
      wr.write(postData);
      wr.close();

      // Get Response
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      StringBuffer response = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null) {
        response.append(line);
        response.append('\r');
      }
      rd.close();
      return response.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
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
