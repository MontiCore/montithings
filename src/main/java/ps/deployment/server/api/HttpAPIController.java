package ps.deployment.server.api;

import com.google.gson.JsonParseException;

import ps.deployment.server.DeploymentManager;
import ps.deployment.server.data.DeploymentConfiguration;
import ps.deployment.server.exception.DeploymentException;
import spark.Request;
import spark.Response;
import spark.Spark;

public class HttpAPIController {
  
  private final static String RESPONSE_JSON_SUCCESS = "{\"success\":true}";
  private final static String RESPONSE_JSON_FAILED = "{\"success\":false}";
  
  private final DeploymentManager manager;
  
  public HttpAPIController(DeploymentManager manager) {
    this.manager = manager;
  }
  
  public boolean start() {
    try {
      Spark.port(4210);
      Spark.put("/suggestions", this::handlePathSuggestions);
      Spark.put("/validate", this::handlePathValidate);
      Spark.put("/deploy", this::handleDeployRequest);
      
      return true;
    } catch(Exception e) {
      return false;
    }
  }
  
  private Object handlePathSuggestions(Request request, Response response) {
    response.type("application/json");
    
    try {
      String strJson = request.body();
      int suggestionIndex = 0;
      String suggestionIndexStr = request.queryParams("index");
      if(suggestionIndexStr != null) {
        suggestionIndex = Integer.parseInt(suggestionIndexStr);
      }
      
      DeploymentConfiguration config = DeploymentConfiguration.fromJson(strJson);
      DeploymentConfiguration newConfig = manager.computeSuggestion(config, suggestionIndex);
      
      return newConfig.getConstraintsAsJson();
    } catch(JsonParseException | DeploymentException | NumberFormatException e) {
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
    } catch(DeploymentException e) {
      e.printStackTrace();
      success = false;
    } catch(Throwable t) {
      t.printStackTrace();
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
      }
      catch (JsonParseException | DeploymentException e) {
        e.printStackTrace();
      }
    }
    // This is only executed when the above does not succeed in any way.
    return RESPONSE_JSON_FAILED;
  }
  
}
