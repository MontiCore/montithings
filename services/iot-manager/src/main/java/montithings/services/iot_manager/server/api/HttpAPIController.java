// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import montithings.services.iot_manager.server.DeployTargetProviderParser;
import montithings.services.iot_manager.server.DeploymentManager;
import montithings.services.iot_manager.server.IDeployTargetProvider;
import montithings.services.iot_manager.server.TfResourceManager;
import montithings.services.iot_manager.server.azurecloud.AzureCredentials;
import montithings.services.iot_manager.server.data.DeploymentConfiguration;
import montithings.services.iot_manager.server.exception.DeploymentException;
import spark.Request;
import spark.Response;
import spark.Spark;

public class HttpAPIController {

  private final static String RESPONSE_JSON_SUCCESS = "{\"success\":true}";
  private final static String RESPONSE_JSON_FAILED = "{\"success\":false}";

  private final DeploymentManager manager;

  private final IMqttSettingsListener mqttSettingsListener;

  public HttpAPIController(DeploymentManager manager, IMqttSettingsListener mqttSettingsListener) {
    this.manager = manager;
    this.mqttSettingsListener = mqttSettingsListener;
  }

  public boolean start() {
    try {
      Spark.port(4210);
      Spark.put("/suggestions", this::handlePathSuggestions);
      Spark.put("/validate", this::handlePathValidate);
      Spark.put("/deploy", this::handleDeployRequest);
      Spark.put("/stopDeployment", this::handleStopDeployment);
      Spark.put("/setDockerRegistry", this::handleSetDockerRegistry);
      Spark.put("/setMqttBroker", this::handleSetMqttBroker);
      Spark.put("/setTerraformInfo", this::handleSetTerraformInfo);
      Spark.put("/providers", this::handlePutProviders);

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private Object handlePathSuggestions(Request request, Response response) {
    response.type("application/json");

    try {
      String strJson = request.body();
      int suggestionIndex = 0;
      String suggestionIndexStr = request.queryParams("index");
      if (suggestionIndexStr != null) {
        suggestionIndex = Integer.parseInt(suggestionIndexStr);
      }

      DeploymentConfiguration config = DeploymentConfiguration.fromJson(strJson);
      DeploymentConfiguration newConfig = manager.computeSuggestion(config, suggestionIndex);

      return newConfig.getConstraintsAsJson();
    } catch (JsonParseException | DeploymentException | NumberFormatException e) {
      /// e.printStackTrace();
      return RESPONSE_JSON_FAILED;
    }
  }

  private Object handlePathValidate(Request request, Response response) {
    response.type("application/json");

    boolean success;
    try {
      String strJson = request.body();
      DeploymentConfiguration config = DeploymentConfiguration.fromJson(strJson);
      success = manager.validate(config);
    } catch (Throwable e) {
      e.printStackTrace();
      success = false;
    }

    response.status(success ? 200 : 409);
    return "";
  }

  private Object handleDeployRequest(Request request, Response response) {
    String body = request.body();
    if (body != null) {
      try {
        DeploymentConfiguration config = DeploymentConfiguration.fromJson(body);
        if (manager.validate(config)) {
          manager.setDeploymentInfo(config.getDeploymentInfo());
          manager.setDeploymentConfig(config);
          manager.updateDeployment();
          return RESPONSE_JSON_SUCCESS;
        }
      } catch (JsonParseException | DeploymentException e) {
        e.printStackTrace();
      }
    }
    // This is only executed when the above does not succeed in any way.
    response.status(409);
    return RESPONSE_JSON_FAILED;
  }

  private Object handleStopDeployment(Request req, Response resp) {
    manager.terminate();
    return "";
  }

  private Object handlePutProviders(Request req, Response resp) {
    try {
      String bodyStr = req.body();
      JsonElement json = JsonParser.parseString(bodyStr);

      IDeployTargetProvider provider = DeployTargetProviderParser.parse(json);
      manager.setTargetProvider(provider);

      return RESPONSE_JSON_SUCCESS;
    } catch (Exception e) {
      e.printStackTrace();
      resp.status(400);
      return RESPONSE_JSON_FAILED;
    }
  }

  private Object handleSetMqttBroker(Request req, Response resp) {
    try {
      String bodyStr = req.body();
      JsonElement json = JsonParser.parseString(bodyStr);
      manager.getNetworkInfo().setMqttHost(json.getAsJsonObject().get("hostname").getAsString());
      System.out.println("Set MQTT Hostname: " + manager.getNetworkInfo().getMqttHost());
      manager.getNetworkInfo().setMqttPort(Integer.parseInt(json.getAsJsonObject().get("port").getAsString()));
      System.out.println("Set MQTT Port: " + manager.getNetworkInfo().getMqttPort());
      manager.getNetworkInfo().setMqttUsername(json.getAsJsonObject().get("username").getAsString());
      System.out.println("Set MQTT username: " + manager.getNetworkInfo().getMqttUsername());
      manager.getNetworkInfo().setMqttPassword(json.getAsJsonObject().get("password").getAsString());
      System.out.println("Set MQTT password: " + manager.getNetworkInfo().getMqttPassword());
      if (json.getAsJsonObject().has("secure")) {
        manager.getNetworkInfo().setMqttSecure(json.getAsJsonObject().get("secure").getAsBoolean());
        System.out.println("Set MQTT secure: " + manager.getNetworkInfo().getMqttSecure());
      }

      mqttSettingsListener.onMqttSettingsChanged(manager.getNetworkInfo());
    } catch (Exception e) {
      e.printStackTrace();
      resp.status(400);
      return RESPONSE_JSON_FAILED;
    }
    return RESPONSE_JSON_SUCCESS;
  }

  private Object handleSetDockerRegistry(Request req, Response resp) {
    try {
      String bodyStr = req.body();
      JsonElement json = JsonParser.parseString(bodyStr);
      manager.getNetworkInfo().setDockerRepositoryPrefix(json.getAsJsonObject().get("hostname").getAsString());
      System.out.println("Set docker registry: " + manager.getNetworkInfo().getDockerRepositoryPrefix());
      manager.getNetworkInfo().setDockerRepositoryUsername(json.getAsJsonObject().get("username").getAsString());
      System.out.println("Set docker registry username: " + manager.getNetworkInfo().getDockerRepositoryUsername());
      manager.getNetworkInfo().setDockerRepositoryPassword(json.getAsJsonObject().get("password").getAsString());
      System.out.println("Set docker registry password: " + manager.getNetworkInfo().getDockerRepositoryPassword());
    } catch (Exception e) {
      e.printStackTrace();
      resp.status(400);
      return RESPONSE_JSON_FAILED;
    }
    return RESPONSE_JSON_SUCCESS;
  }

  private Object handleSetTerraformInfo(Request req, Response resp) {
    try {
      String bodyStr = req.body();
      JsonElement json = JsonParser.parseString(bodyStr);
      String terraformDeployerUrl = json.getAsJsonObject().get("terraformDeployerUrl").getAsString();
      String storageAccountName = json.getAsJsonObject().get("storageAccountName").getAsString();
      String clientId = json.getAsJsonObject().get("clientId").getAsString();
      String clientSecret = json.getAsJsonObject().get("clientSecret").getAsString();
      String subscriptionId = json.getAsJsonObject().get("subscriptionId").getAsString();
      String tenantId = json.getAsJsonObject().get("tenantId").getAsString();
      AzureCredentials credentials = new AzureCredentials(clientId, clientSecret, subscriptionId, tenantId);
      TfResourceManager tfResourceManager = TfResourceManager.getInstance();
      tfResourceManager.setTerraformDeployerUrl(terraformDeployerUrl);
      tfResourceManager.setStorageAccountName(storageAccountName);
      tfResourceManager.setCredentials(credentials);
      manager.setTfResourceManager(tfResourceManager);
    } catch (Exception e) {
      e.printStackTrace();
      resp.status(400);
      return RESPONSE_JSON_FAILED;
    }
    return RESPONSE_JSON_SUCCESS;
  }

}
