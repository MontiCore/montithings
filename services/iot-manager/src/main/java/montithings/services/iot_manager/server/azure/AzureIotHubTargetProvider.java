/* (c) https://github.com/MontiCore/monticore */

package montithings.services.iot_manager.server.azure;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.azure.sdk.iot.service.IotHubConnectionString;
import com.microsoft.azure.sdk.iot.service.IotHubConnectionStringBuilder;
import com.microsoft.azure.sdk.iot.service.auth.IotHubServiceSasToken;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.devicetwin.Query;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubExceptionManager;
import com.microsoft.azure.sdk.iot.service.transport.http.HttpMethod;
import com.microsoft.azure.sdk.iot.service.transport.http.HttpRequest;
import com.microsoft.azure.sdk.iot.service.transport.http.HttpResponse;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import montithings.services.iot_manager.server.IDeployTargetProvider;
import montithings.services.iot_manager.server.data.*;
import montithings.services.iot_manager.server.distribution.listener.IDeployStatusListener;
import montithings.services.iot_manager.server.distribution.listener.VoidDeployStatusListener;
import montithings.services.iot_manager.server.exception.DeploymentException;
import org.apache.commons.io.output.StringBuilderWriter;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AzureIotHubTargetProvider implements IDeployTargetProvider {

  // connection String of the iot-hub, as found under 'shared-access-policies' in the Azure Portal
  String iotHubConnectionString;

  DeviceTwin iotHub;

  boolean initialized = false;

  private IDeployStatusListener listener = new VoidDeployStatusListener();

  private final long providerID;

  private List<DeployClient> clients = new ArrayList<>();

  private boolean running = true;

  public AzureIotHubTargetProvider(long providerID, String iotHubConnectionString) {
    this.providerID = providerID;
    this.iotHubConnectionString = iotHubConnectionString;
  }

  /**
   * Applies a deployment to a set of devices of the iot-hub
   *
   * @param dist     defines, which devices receive which deployment(s)
   * @param deplInfo defines the deployments
   * @param netInfo  provides connectivity information for the deployments
   */
  @Override
  public void deploy(Distribution dist, DeploymentInfo deplInfo, NetworkInfo netInfo) {
    if (!initialized) {
      initialize();
    }
    Map<String, String[]> distributionMap = dist.getDistributionMap();
    for (String deviceID : distributionMap.keySet()) {
      GeneratorSetup setup = new GeneratorSetup();
      setup.setTracing(false);
      GeneratorEngine engine = new GeneratorEngine(setup);
      StringBuilderWriter deployment = new StringBuilderWriter();
      engine.generateNoA("templates/azureDeployment.ftl", deployment,
        distributionMap.get(deviceID), deplInfo, netInfo);
      try {
        String deviceName = AzureIotUtils.getDeviceName(deviceID, iotHubConnectionString);
        applyConfigurationContentOnDevice(deviceName, deployment.toString(),
          iotHubConnectionString);
      }
      catch (IotHubException | IOException e) {
        System.out.println("ERROR! Deployment failed on device " + deviceID);
        e.printStackTrace();
      }
    }
  }

  /**
   * gets all the clients / devices connected to the iot-hub
   *
   * @return a Collection of all devices connected to the iot-hub, parsed as DeployClients
   */
  @Override
  public Collection<DeployClient> getClients() {
    return clients;
  }

  @Override
  public void initialize() {
    iotHub = new DeviceTwin(iotHubConnectionString);
    initialized = true;

    // start polling for periodic updates
    Thread updaterThread = new Thread(this::startPolling);
    updaterThread.setDaemon(true);
    updaterThread.start();
  }

  @Override
  public void close() throws DeploymentException {
    this.running = false;
  }

  @Override
  public void setStatusListener(IDeployStatusListener listener) {
    this.listener = listener;
  }

  public void refreshClients() {
    System.out.println("Refresh clients...");
    List<DeployClient> previousClients = new ArrayList<>(clients);
    List<DeployClient> currentClients = requestClientsFromAzure();
    Set<DeployClient> allClients = new HashSet<>(previousClients);
    allClients.addAll(currentClients);

    for (DeployClient client : allClients) {
      if (previousClients.contains(client) && !currentClients.contains(client)) {
        client.setOnline(false);
        listener.onClientOffline(client);
        System.out.println("Client '" + client.getClientID() + "' marked as offline");
      }
      if (!previousClients.contains(client) && currentClients.contains(client)) {
        if (client.isOnline()) {
          listener.onClientOnline(client);
        }
        else {
          listener.onClientOffline(client);
        }
        System.out.println("Client '" + client.getClientID() + "' marked as " +
          (client.isOnline() ? "online" : "offline"));
      }
    }
    clients = currentClients;
  }

  protected List<DeployClient> requestClientsFromAzure() {
    List<DeployClient> result = new ArrayList<>();
    try {
      Query hubQuery = iotHub.queryTwin("SELECT * FROM devices");

      while (hubQuery.hasNext()) {
        JsonObject deviceTwinJSON = JsonParser.parseString(hubQuery.next().toString())
          .getAsJsonObject();
        result.add(getDeployClientfromObject(deviceTwinJSON));
      }
    }
    catch (IotHubException | IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * parses a deviceTwin received from azure-iot-hub to an instance of DeployClient
   *
   * @param deviceTwinJSON the JSON of a deviceTwin received from an azure-iot-hub query
   * @return an instance of DeployClient containing all important information of deviceTwin
   */
  public DeployClient getDeployClientfromObject(JsonObject deviceTwinJSON) {

    String deviceName = deviceTwinJSON.get("deviceId").getAsString();
    String deviceID = AzureIotUtils.getDeviceIdentifier(deviceName, iotHubConnectionString);

    boolean online = deviceTwinJSON.get("connectionState").getAsString().equals("Connected");
    long targetProviderID = providerID;
    String[] hardware = getHardwareFromDeviceTwin(deviceTwinJSON);
    LocationSpecifier location = getLocationFromDeviceTwin(deviceTwinJSON);

    return DeployClient.create(deviceID, online, location, targetProviderID, hardware);
  }

  /**
   * gets the hardware components of a deviceTwinJSON
   *
   * @param deviceTwinJSON the JSON of a deviceTwinJSON received from an azure-iot-hub query
   * @return all hardware components listed under "tags" in deviceTwinJSON
   */
  public String[] getHardwareFromDeviceTwin(JsonObject deviceTwinJSON) {
    try {
      JsonElement hardwareJson = deviceTwinJSON.getAsJsonObject("tags").get("hardware");
      Gson gson = new Gson();
      return gson.fromJson(hardwareJson, String[].class);
    }
    catch (NullPointerException e) {
      return new String[] {};
    }
  }

  /**
   * gets the Location data of a deviceTwinJSON
   *
   * @param deviceTwinJSON the JSON of a deviceTwinJSON received from an azure-iot-hub query
   * @return the location-data listed under "tags in deviceTwinJSON parsed to an instace of LocationSpecifier
   */
  public LocationSpecifier getLocationFromDeviceTwin(JsonObject deviceTwinJSON) {
    try {
      JsonObject locationJson = deviceTwinJSON.getAsJsonObject("tags").getAsJsonObject("location");
      return new LocationSpecifier(
        locationJson.get("building").getAsString(),
        locationJson.get("floor").getAsString(),
        locationJson.get("room").getAsString());
    }
    catch (NullPointerException e) {
      return new LocationSpecifier("unspecified", "unspecified", "unspecified");
    }
  }

  /**
   * copied and modified from com.microsoft.azure.sdk.iot.service.RegistryManager
   */
  public static void applyConfigurationContentOnDevice(String deviceId, String content,
    String iotHubConnectionString)
    throws IOException, IotHubException {
    Preconditions.checkNotNull(content, "Content must not be null");
    IotHubConnectionString connectionString = IotHubConnectionStringBuilder.createIotHubConnectionString(
      iotHubConnectionString);
    URL url = IotHubConnectionString.getUrlApplyConfigurationContent(connectionString.getHostName(),
      deviceId);
    HttpRequest request = createRequest(connectionString, url, HttpMethod.POST, content.getBytes());
    HttpResponse response = request.send();
    IotHubExceptionManager.httpResponseVerification(response);
  }

  /**
   * copied and modified from com.microsoft.azure.sdk.iot.service.RegistryManager
   */
  public static HttpRequest createRequest(IotHubConnectionString connectionString, URL url,
    HttpMethod method, byte[] payload)
    throws IOException {
    HttpRequest request = new HttpRequest(url, method, payload, null);
    String sasToken = new IotHubServiceSasToken(connectionString).toString();
    request.setHeaderField("authorization", sasToken);
    request.setHeaderField("Request-Id", "1001");
    request.setHeaderField("Accept", "application/json");
    request.setHeaderField("Content-Type", "application/json");
    request.setHeaderField("charset", "utf-8");
    return request;
  }

  private void startPolling() {
    while (this.running) {
      try {
        refreshClients();
        Thread.sleep(30_000);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}