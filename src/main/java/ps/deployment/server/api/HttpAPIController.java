package ps.deployment.server.api;

import com.google.gson.JsonParseException;

import ps.deployment.server.DeploymentManager;
import ps.deployment.server.data.DeploymentConfiguration;
import ps.deployment.server.exception.DeploymentException;
import spark.Request;
import spark.Response;
import spark.Spark;

public class HttpAPIController {
  
  private final DeploymentManager manager;
  
  public HttpAPIController(DeploymentManager manager) {
    this.manager = manager;
  }
  
  public boolean start() {
    try {
      Spark.port(4210);
      Spark.put("/suggestions", this::handlePathSuggestions);
      Spark.put("/validate", this::handlePathValidate);
      
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
      e.printStackTrace();
      return "{\"success\":false}";
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
  
}
